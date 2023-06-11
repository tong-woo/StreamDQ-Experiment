package com.tong.streamdqexp.experiment

import com.stefan_grafberger.streamdq.VerificationSuite
import com.stefan_grafberger.streamdq.anomalydetection.detectors.aggregatedetector.AggregateAnomalyCheck
import com.stefan_grafberger.streamdq.anomalydetection.strategies.DetectionStrategy
import com.stefan_grafberger.streamdq.checks.AggregateConstraintResult
import com.stefan_grafberger.streamdq.checks.aggregate.ApproxUniquenessConstraint
import com.tong.streamdqexp.logger.ExperimentLogger
import com.tong.streamdqexp.model.ExperimentResult
import com.tong.streamdqexp.model.RedditPost
import com.tong.streamdqexp.util.ExperimentUtil
import org.apache.flink.api.common.eventtime.WatermarkStrategy
import org.apache.flink.api.common.typeinfo.TypeInformation
import org.apache.flink.streaming.api.windowing.assigners.TumblingEventTimeWindows
import org.apache.flink.streaming.api.windowing.assigners.TumblingProcessingTimeWindows
import org.apache.flink.streaming.api.windowing.time.Time
import java.util.concurrent.TimeUnit

class Experiment {

    private var log = ExperimentLogger()
    private var util = ExperimentUtil()
    private val RUNTIME_OUTPUT_FILE_PATH =
        "/Users/wutong/Desktop/Thesis/streamdpexp/experimentresult/runtime.csv"

    /**
     * test net run time
     */
    fun testRunTimeOnReddit(
        path: String,
        windowSize: Long = 1000
    ) {
        //setup env
        val env = util.createStreamExecutionEnvironment()
        val anomalyCheck = AggregateAnomalyCheck()
            .onApproxUniqueness("score")
            .withWindow(TumblingEventTimeWindows.of(Time.milliseconds(windowSize)))
            .withStrategy(DetectionStrategy().onlineNormal(0.0, 0.3, 0.0))
            .build()
        //setup deserialization
        val source = util.generateRedditFileSourceFromPath(path)
        val redditPostStream = env
            .fromSource(source, WatermarkStrategy.noWatermarks(), "Reddit Posts")
            .assignTimestampsAndWatermarks(
                WatermarkStrategy.forMonotonousTimestamps<RedditPost>()
                    .withTimestampAssigner { post, _ -> post.createdUtc!!.toLong() }
            )
        //detection
        val verificationResult = VerificationSuite()
            .onDataStream(redditPostStream, env.config)
            .addAnomalyChecks(mutableListOf(anomalyCheck))
            .build()
        val actualAnomalies = verificationResult.getResultsForCheck(anomalyCheck)
        //sink
        actualAnomalies!!.print("AnomalyCheckResult stream output")
        val jobExecutionResult = env.execute()
        //save result
        val result = ExperimentResult(
            experimentName = "runtime experiment",
            timeInMs = jobExecutionResult.netRuntime,
            fileName = path.replace("/Users/wutong/Desktop/experiment/dataset/", "")
        )
        util.writeResultToCsvFile(result, RUNTIME_OUTPUT_FILE_PATH)
    }

    fun testRunTimeOnClickStream(path: String, windowSize: Long = 1000) {
        //setup env
        val env = util.createStreamExecutionEnvironment()
        val anomalyCheck = AggregateAnomalyCheck()
            .onApproxUniqueness("count")
            .withWindow(TumblingProcessingTimeWindows.of(Time.milliseconds(windowSize)))
            .withStrategy(DetectionStrategy().onlineNormal(0.0, 0.3, 0.0))
            .build()
        //setup deserialization
        val source = util.generateWikiClickFileSourceFromPath(path)
        val wikiClickStream = env
            .fromSource(source, WatermarkStrategy.noWatermarks(), "Wiki Click Info")
        //detection
        val verificationResult = VerificationSuite()
            .onDataStream(wikiClickStream, env.config)
            .addAnomalyChecks(mutableListOf(anomalyCheck))
            .build()
        val actualAnomalies = verificationResult.getResultsForCheck(anomalyCheck)
        //sink
        actualAnomalies!!.print("AnomalyCheckResult stream output")
        val jobExecutionResult = env.execute()
        //save result
        val result = ExperimentResult(
            experimentName = "runtime experiment",
            timeInMs = jobExecutionResult.netRuntime,
            fileName = path.replace("/Users/wutong/Desktop/experiment/dataset/", "")
        )
        util.writeResultToCsvFile(result, RUNTIME_OUTPUT_FILE_PATH)
    }

    /**
     * test processing time latency
     */
    fun testLatencyOnReddit(path: String, windowSize: Long = 1000) {
        //setup env
        val env = util.createStreamExecutionEnvironment()
        env.config.latencyTrackingInterval = 1000
        val anomalyCheck = AggregateAnomalyCheck()
            .onCompleteness("score")
            .withWindow(TumblingEventTimeWindows.of(Time.milliseconds(windowSize)))
            .withStrategy(DetectionStrategy().onlineNormal(0.0, 0.3, 0.0))
            .build()
        //setup deserialization
        val source = util.generateRedditFileSourceFromPath(path)
        val redditPostStream = env
            .fromSource(source, WatermarkStrategy.noWatermarks(), "Reddit Posts")
            .assignTimestampsAndWatermarks(
                WatermarkStrategy.forMonotonousTimestamps<RedditPost>()
                    .withTimestampAssigner { post, _ -> post.createdUtc!!.toLong() }
            )
        //detection
        val (actualAnomalies, processingTimeLatency) = util.executeAndMeasureTimeMillis {
            val verificationResult = VerificationSuite()
                .onDataStream(redditPostStream, env.config)
                .addAnomalyChecks(mutableListOf(anomalyCheck))
                .build()
            verificationResult.getResultsForCheck(anomalyCheck)
        }
        //
        actualAnomalies!!.print("AnomalyCheckResult stream output")
        env.execute()
        log.info("Processing Time Latency: $processingTimeLatency ms")
    }

    /**
     * compare anomaly detection with only aggregation constraint calculation
     */
    fun testOverheadOnReddit(path: String, windowSize: Long = 1000) {
        //given
        val env = util.createStreamExecutionEnvironment()
        //setup deserialization
        val eventTimeStart = System.nanoTime()
        val source = util.generateRedditFileSourceFromPath(path)
        val redditPostStream = env
            .fromSource(source, WatermarkStrategy.noWatermarks(), "Reddit Posts")
            .assignTimestampsAndWatermarks(
                WatermarkStrategy.forMonotonousTimestamps<RedditPost>()
                    .withTimestampAssigner { post, _ -> post.createdUtc!!.toLong() }
            )
        //when
        val (actualAnomalies, processingTimeLatency) = util.executeAndMeasureTimeMillis {
            redditPostStream
                .windowAll(TumblingEventTimeWindows.of(Time.milliseconds(windowSize)))
                .aggregate(
                    ApproxUniquenessConstraint("score").getAggregateFunction(
                        TypeInformation.of(
                            RedditPost::class.java
                        ), env.config
                    )
                )
                .returns(AggregateConstraintResult::class.java)
        }
        val eventTimeLatency = System.nanoTime() - eventTimeStart
        //then
        //sink
        actualAnomalies.print("AggregateConstraintResult stream output")
        val jobExecutionResult = env.execute()
        log.info("Event Time Latency: " + TimeUnit.NANOSECONDS.toMillis(eventTimeLatency) + " ms")
        log.info("Processing Time Latency: $processingTimeLatency ms")
        log.info("Net Fink Job Execution Run Time: ${jobExecutionResult.netRuntime} ms")
    }
}
