package com.tong.streamdqexp.experiment

import com.stefan_grafberger.streamdq.VerificationSuite
import com.stefan_grafberger.streamdq.anomalydetection.detectors.aggregatedetector.AggregateAnomalyCheck
import com.stefan_grafberger.streamdq.anomalydetection.strategies.DetectionStrategy
import com.stefan_grafberger.streamdq.checks.aggregate.ApproxUniquenessConstraint
import com.tong.streamdqexp.logger.ExperimentLogger
import com.tong.streamdqexp.model.ExperimentResult
import com.tong.streamdqexp.model.RedditPost
import com.tong.streamdqexp.model.WikiClickStream
import com.tong.streamdqexp.processfunction.AnomalyCheckResultProcessFunction
import com.tong.streamdqexp.util.ExperimentUtil
import org.apache.flink.api.common.eventtime.WatermarkStrategy
import org.apache.flink.api.common.typeinfo.TypeInformation
import org.apache.flink.streaming.api.windowing.assigners.TumblingEventTimeWindows
import org.apache.flink.streaming.api.windowing.assigners.TumblingProcessingTimeWindows
import org.apache.flink.streaming.api.windowing.time.Time

class Experiment {

    private var log = ExperimentLogger()
    private var util = ExperimentUtil()
    private val RUNTIME_OUTPUT_FILE_PATH =
        "/Users/wutong/Desktop/Thesis/streamdpexp/experimentresult/runtime.csv"
    private val LATENCY_OUTPUT_FILE_PATH =
        "/Users/wutong/Desktop/Thesis/streamdpexp/experimentresult/latency.csv"
    private val OVERHEAD_OUTPUT_FILE_PATH =
        "/Users/wutong/Desktop/Thesis/streamdpexp/experimentresult/overhead.csv"

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
        val actualAnomalies = verificationResult
            .getResultsForCheck(anomalyCheck)
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
     * We calculate the event Time Lag
     * which is the current processing time - current watermark
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
            .fromSource(
                source,
                WatermarkStrategy.noWatermarks(),
                "Reddit Posts"
            )
            .assignTimestampsAndWatermarks(
                WatermarkStrategy.forMonotonousTimestamps<RedditPost>()
                    .withTimestampAssigner { _, _ -> System.currentTimeMillis() }
            )
        //detection
        val verificationResult = VerificationSuite()
            .onDataStream(redditPostStream, env.config)
            .addAnomalyChecks(mutableListOf(anomalyCheck))
            .build()
        //process to (result, latency) pair
        val actualAnomalies = verificationResult
            .getResultsForCheck(anomalyCheck)
            ?.process(AnomalyCheckResultProcessFunction(windowSize))
        //sink
        actualAnomalies!!.print("AnomalyCheckResult stream output")
        env.execute()
        val latencies = actualAnomalies
            .map { pair -> pair.f1 }
            .returns(Long::class.java)
            .executeAndCollect().asSequence().toList()
        //save result
        val result = ExperimentResult(
            experimentName = "latency experiment",
            timeInMs = util.percentile(latencies, 95.0),
            fileName = path.replace("/Users/wutong/Desktop/experiment/dataset/", "")
        )
        util.writeResultToCsvFile(result, LATENCY_OUTPUT_FILE_PATH)
    }

    fun testLatencyOnClickStream(path: String, windowSize: Long = 1000) {
        //setup env
        val env = util.createStreamExecutionEnvironment()
        env.config.latencyTrackingInterval = 1000
        val anomalyCheck = AggregateAnomalyCheck()
            .onCompleteness("count")
            .withWindow(TumblingEventTimeWindows.of(Time.milliseconds(windowSize)))
            .withStrategy(DetectionStrategy().onlineNormal(0.0, 0.3, 0.0))
            .build()
        //setup deserialization
        val source = util.generateWikiClickFileSourceFromPath(path)
        val wikiClickStream = env
            .fromSource(
                source,
                WatermarkStrategy.noWatermarks(),
                "wiki click stream"
            )
            .assignTimestampsAndWatermarks(
                WatermarkStrategy.forMonotonousTimestamps<WikiClickStream>()
                    .withTimestampAssigner { _, _ -> System.currentTimeMillis() }
            )
        //detection
        val verificationResult = VerificationSuite()
            .onDataStream(wikiClickStream, env.config)
            .addAnomalyChecks(mutableListOf(anomalyCheck))
            .build()
        val actualAnomalies = verificationResult
            .getResultsForCheck(anomalyCheck)
            ?.process(AnomalyCheckResultProcessFunction(windowSize))
        //sink
        actualAnomalies!!.print("AnomalyCheckResult stream output")
        env.execute()
        val latencies = actualAnomalies
            .map { pair -> pair.f1 }
            .returns(Long::class.java)
            .executeAndCollect().asSequence().toList()
        //save result
        val result = ExperimentResult(
            experimentName = "latency experiment",
            timeInMs = util.percentile(latencies, 95.0),
            fileName = path.replace("/Users/wutong/Desktop/experiment/dataset/", "")
        )
        util.writeResultToCsvFile(result, LATENCY_OUTPUT_FILE_PATH)
    }

    /**
     * To compare anomaly detection
     * measure the run time of only aggregation constraint computation
     */
    fun testOverheadOnReddit(path: String, windowSize: Long = 1000) {
        //setup env
        val env = util.createStreamExecutionEnvironment()
        val source = util.generateRedditFileSourceFromPath(path)
        val redditPostStream = env
            .fromSource(source, WatermarkStrategy.noWatermarks(), "Reddit Posts")
            .assignTimestampsAndWatermarks(
                WatermarkStrategy.forMonotonousTimestamps<RedditPost>()
                    .withTimestampAssigner { _, _ -> System.currentTimeMillis() }
            )
        //when
        val aggregateConstraintResultStream = redditPostStream
            .windowAll(TumblingEventTimeWindows.of(Time.milliseconds(windowSize)))
            .aggregate(
                ApproxUniquenessConstraint("score").getAggregateFunction(
                    TypeInformation.of(
                        RedditPost::class.java
                    ), env.config
                )
            )
        //sink
        aggregateConstraintResultStream.print("aggregate result stream")
        val jobExecutionResult = env.execute()
        //save result
        val result = ExperimentResult(
            experimentName = "overhead experiment(only aggregation time)",
            timeInMs = jobExecutionResult.netRuntime,
            fileName = path.replace("/Users/wutong/Desktop/experiment/dataset/", "")
        )
        util.writeResultToCsvFile(result, OVERHEAD_OUTPUT_FILE_PATH)
        log.info("Net Fink Job Execution Run Time: ${jobExecutionResult.netRuntime} ms")
    }

    fun testOverheadOnClickStream(path: String, windowSize: Long = 1000) {
        //setup env
        val env = util.createStreamExecutionEnvironment()
        val source = util.generateWikiClickFileSourceFromPath(path)
        val wikiClickStream = env
            .fromSource(source, WatermarkStrategy.noWatermarks(), "Wiki Click Stream")
            .assignTimestampsAndWatermarks(
                WatermarkStrategy.forMonotonousTimestamps<WikiClickStream>()
                    .withTimestampAssigner { _, _ -> System.currentTimeMillis() }
            )
        //aggregate computation
        val aggregateConstraintResultStream = wikiClickStream
            .windowAll(TumblingEventTimeWindows.of(Time.milliseconds(windowSize)))
            .aggregate(
                ApproxUniquenessConstraint("count").getAggregateFunction(
                    TypeInformation.of(
                        WikiClickStream::class.java
                    ), env.config
                )
            )
        //sink
        aggregateConstraintResultStream.print("aggregate result stream")
        val jobExecutionResult = env.execute()
        //save result
        val result = ExperimentResult(
            experimentName = "overhead experiment(only aggregation time)",
            timeInMs = jobExecutionResult.netRuntime,
            fileName = path.replace("/Users/wutong/Desktop/experiment/dataset/", "")
        )
        util.writeResultToCsvFile(result, OVERHEAD_OUTPUT_FILE_PATH)
        log.info("Net Fink Job Execution Run Time: ${jobExecutionResult.netRuntime} ms")
    }


}
