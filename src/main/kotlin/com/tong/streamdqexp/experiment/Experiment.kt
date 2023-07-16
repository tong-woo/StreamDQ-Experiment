package com.tong.streamdqexp.experiment

import com.stefan_grafberger.streamdq.VerificationSuite
import com.stefan_grafberger.streamdq.anomalydetection.detectors.aggregatedetector.AggregateAnomalyCheck
import com.stefan_grafberger.streamdq.anomalydetection.model.AnomalyCheckResult
import com.stefan_grafberger.streamdq.anomalydetection.strategies.DetectionStrategy
import com.stefan_grafberger.streamdq.checks.aggregate.ApproxUniquenessConstraint
import com.tong.streamdqexp.model.ExperimentResult
import com.tong.streamdqexp.model.RedditPost
import com.tong.streamdqexp.model.WikiClickStream
import com.tong.streamdqexp.processfunction.AnomalyCheckResultProcessFunction
import com.tong.streamdqexp.util.ExperimentUtil
import org.apache.flink.api.common.eventtime.WatermarkStrategy
import org.apache.flink.configuration.Configuration
import org.apache.flink.configuration.RestOptions
import org.apache.flink.streaming.api.datastream.DataStream
import org.apache.flink.streaming.api.environment.StreamExecutionEnvironment
import org.apache.flink.streaming.api.windowing.assigners.GlobalWindows
import org.apache.flink.streaming.api.windowing.assigners.TumblingEventTimeWindows
import org.apache.flink.streaming.api.windowing.assigners.TumblingProcessingTimeWindows
import org.apache.flink.streaming.api.windowing.time.Time
import org.apache.flink.streaming.api.windowing.triggers.CountTrigger

class Experiment {

    private var util = ExperimentUtil()
    private val RUNTIME_OUTPUT_FILE_PATH =
            "/Users/wutong/Desktop/Thesis/streamdpexp/experimentresult/runtime-new.csv"
    private val LATENCY_OUTPUT_FILE_PATH =
            "/Users/wutong/Desktop/Thesis/streamdpexp/experimentresult/latency.csv"
    private val OVERHEAD_OUTPUT_FILE_PATH =
            "/Users/wutong/Desktop/Thesis/streamdpexp/experimentresult/overhead.csv"
    private val WINDOW_CONFIG_OUTPUT_FILE_PATH =
            "/Users/wutong/Desktop/Thesis/streamdpexp/experimentresult/window-config.csv"
    private val CHECKNUMBER_CONFIG_OUTPUT_FILE_PATH =
            "/Users/wutong/Desktop/Thesis/streamdpexp/experimentresult/check-number-config.csv"
    private val PARTITON_CONFIG_OUTPUT_FILE_PATH =
            "/Users/wutong/Desktop/Thesis/streamdpexp/experimentresult/partition-number-config.csv"

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
                .keyBy { post -> post.id }
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
                .keyBy { click -> click.prev }
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
                .keyBy { post -> post.id }
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
                .keyBy { click -> click.prev }
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
                                .withTimestampAssigner { post, _ -> post.createdUtc!!.toLong() }
                )
        //when
        val actualAnomalies = redditPostStream
                .windowAll(TumblingEventTimeWindows.of(Time.milliseconds(windowSize)))
                .aggregate(
                        ApproxUniquenessConstraint("score").getAggregateFunction(
                                redditPostStream.type, env.config
                        )
                ).map { res -> res.aggregate }
        //sink
        actualAnomalies.print()
        val jobExecutionResult = env.execute()
        //save result
        val result = ExperimentResult(
                experimentName = "overhead experiment(only aggregation time)",
                timeInMs = jobExecutionResult.netRuntime,
                fileName = path.replace("/Users/wutong/Desktop/experiment/dataset/", "")
        )
        util.writeResultToCsvFile(result, OVERHEAD_OUTPUT_FILE_PATH)
    }

    fun testOverheadOnClickStream(path: String, windowSize: Long = 1000) {
        //setup env
        val env = util.createStreamExecutionEnvironment()
        val source = util.generateWikiClickFileSourceFromPath(path)
        val wikiClickStream = env
                .fromSource(source, WatermarkStrategy.noWatermarks(), "Wiki Click Stream")
        //aggregate computation
        val actualAnomalies = wikiClickStream
                .windowAll(TumblingProcessingTimeWindows.of(Time.milliseconds(windowSize)))
                .aggregate(
                        ApproxUniquenessConstraint("count").getAggregateFunction(
                                wikiClickStream.type, env.config
                        )
                ).map { res -> res.aggregate }
        //sink
        actualAnomalies.print()
        val jobExecutionResult = env.execute()
        //save result
        val result = ExperimentResult(
                experimentName = "overhead experiment(only aggregation time)",
                timeInMs = jobExecutionResult.netRuntime,
                fileName = path.replace("/Users/wutong/Desktop/experiment/dataset/", "")
        )
        util.writeResultToCsvFile(result, OVERHEAD_OUTPUT_FILE_PATH)
    }

    /**
     * test effect of diff window size config
     * absoluteChange and onApproxUniqueness
     *
     * run on the same dataset
     */
    fun testWindowConfigOnReddit(
            path: String,
            windowSize: Long = 1000
    ) {
        //setup env
        val env = util.createStreamExecutionEnvironment()
        //setup deserialization
        val source = util.generateRedditFileSourceFromPath(path)
        val redditPostStream = env
                .fromSource(source, WatermarkStrategy.noWatermarks(), "Reddit Posts")
                .assignTimestampsAndWatermarks(
                        WatermarkStrategy.forMonotonousTimestamps<RedditPost>()
                                .withTimestampAssigner { post, _ -> post.createdUtc!!.toLong() }
                )
        //detection
        val strategy = DetectionStrategy().absoluteChange(0.0, 0.3, 1)
        val constraint = ApproxUniquenessConstraint("score")
        val actualAnomalies = strategy.detect(redditPostStream
                .windowAll(GlobalWindows.create())
                .trigger(CountTrigger.of(windowSize))
                .aggregate(constraint.getAggregateFunction(redditPostStream.type, redditPostStream.executionConfig)))
        //sink
        actualAnomalies!!.print("AnomalyCheckResult stream output")
        val jobExecutionResult = env.execute()
        //save result
        val result = ExperimentResult(
                experimentName = "windowSize config: $windowSize",
                timeInMs = jobExecutionResult.netRuntime,
                fileName = path.replace("/Users/wutong/Desktop/experiment/dataset/", "")
        )
        util.writeResultToCsvFile(result, WINDOW_CONFIG_OUTPUT_FILE_PATH)
    }

    fun testWindowConfigOnClickStream(path: String, windowSize: Long = 1000) {
        //setup env
        val env = util.createStreamExecutionEnvironment()
        //setup deserialization
        val source = util.generateWikiClickFileSourceFromPath(path)
        val wikiClickStream = env
                .fromSource(source, WatermarkStrategy.noWatermarks(), "Wiki Click Info")
        //detection
        val strategy = DetectionStrategy().absoluteChange(0.0, 0.3, 1)
        val constraint = ApproxUniquenessConstraint("count")
        val actualAnomalies = strategy.detect(wikiClickStream
                .windowAll(GlobalWindows.create())
                .trigger(CountTrigger.of(windowSize))
                .aggregate(constraint.getAggregateFunction(wikiClickStream.type, wikiClickStream.executionConfig)))
        //sink
        actualAnomalies!!.print("AnomalyCheckResult stream output")
        val jobExecutionResult = env.execute()
        //save result
        val result = ExperimentResult(
                experimentName = "windowSize config: $windowSize",
                timeInMs = jobExecutionResult.netRuntime,
                fileName = path.replace("/Users/wutong/Desktop/experiment/dataset/", "")
        )
        util.writeResultToCsvFile(result, WINDOW_CONFIG_OUTPUT_FILE_PATH)
    }

    /**
     * test effect of diff anomaly check number
     * in verification suite
     *
     * run on the same dataset
     */
    fun testCheckNumberConfigOnReddit(
            path: String,
            windowSize: Long = 1000,
            checkNumber: Int = 1,
    ) {
        //setup env
        val env = util.createStreamExecutionEnvironment()
        val anomalyCheck1 = AggregateAnomalyCheck()
                .onApproxUniqueness("score")
                .withWindow(TumblingEventTimeWindows.of(Time.milliseconds(windowSize)))
                .withStrategy(DetectionStrategy().threshold(0.0, 0.3))
                .build()
        val anomalyCheck2 = AggregateAnomalyCheck()
                .onApproxUniqueness("score")
                .withWindow(TumblingEventTimeWindows.of(Time.milliseconds(windowSize)))
                .withStrategy(DetectionStrategy().relativeRateOfChange(0.0, 0.3, 1))
                .build()
        val anomalyCheck3 = AggregateAnomalyCheck()
                .onApproxUniqueness("score")
                .withWindow(TumblingEventTimeWindows.of(Time.milliseconds(windowSize)))
                .withStrategy(DetectionStrategy().onlineNormal(0.0, 0.3, 0.0))
                .build()
        val anomalyCheck4 = AggregateAnomalyCheck()
                .onApproxUniqueness("score")
                .withWindow(TumblingEventTimeWindows.of(Time.milliseconds(windowSize)))
                .withStrategy(DetectionStrategy().absoluteChange(0.0, 0.3, 1))
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
        val verificationResult = when (checkNumber) {
            1 -> VerificationSuite()
                    .onDataStream(redditPostStream, env.config)
                    .addAnomalyChecks(mutableListOf(anomalyCheck1))
                    .build()

            2 -> VerificationSuite()
                    .onDataStream(redditPostStream, env.config)
                    .addAnomalyChecks(mutableListOf(anomalyCheck1, anomalyCheck2))
                    .build()

            3 -> VerificationSuite()
                    .onDataStream(redditPostStream, env.config)
                    .addAnomalyChecks(mutableListOf(anomalyCheck1, anomalyCheck2, anomalyCheck3))
                    .build()

            4 -> VerificationSuite()
                    .onDataStream(redditPostStream, env.config)
                    .addAnomalyChecks(
                            mutableListOf(
                                    anomalyCheck1,
                                    anomalyCheck2,
                                    anomalyCheck3,
                                    anomalyCheck4
                            )
                    )
                    .build()

            else -> null
        }
        val actualAnomaliesList = when (checkNumber) {
            1 -> mutableListOf(verificationResult?.getResultsForCheck(anomalyCheck1))
            2 -> mutableListOf(
                    verificationResult?.getResultsForCheck(anomalyCheck1),
                    verificationResult?.getResultsForCheck(anomalyCheck2)
            )

            3 -> mutableListOf(
                    verificationResult?.getResultsForCheck(anomalyCheck1),
                    verificationResult?.getResultsForCheck(anomalyCheck2),
                    verificationResult?.getResultsForCheck(anomalyCheck3)
            )

            4 -> mutableListOf(
                    verificationResult?.getResultsForCheck(anomalyCheck1),
                    verificationResult?.getResultsForCheck(anomalyCheck2),
                    verificationResult?.getResultsForCheck(anomalyCheck3),
                    verificationResult?.getResultsForCheck(anomalyCheck4)
            )

            else -> mutableListOf<DataStream<AnomalyCheckResult>>()
        }
        //sink
        for (actualAnomalies in actualAnomaliesList) {
            actualAnomalies?.print("AnomalyCheckResult stream output")
        }
        val jobExecutionResult = env.execute()
        //save result
        val result = ExperimentResult(
                experimentName = "check number config: $checkNumber",
                timeInMs = jobExecutionResult.netRuntime,
                fileName = path.replace("/Users/wutong/Desktop/experiment/dataset/", "")
        )
        util.writeResultToCsvFile(result, CHECKNUMBER_CONFIG_OUTPUT_FILE_PATH)
    }

    fun testCheckNumberConfigOnWikiClickStream(
            path: String,
            windowSize: Long = 1000,
            checkNumber: Int = 1,
    ) {
        //setup env
        val env = util.createStreamExecutionEnvironment()
        val anomalyCheck1 = AggregateAnomalyCheck()
                .onApproxUniqueness("count")
                .withWindow(TumblingEventTimeWindows.of(Time.milliseconds(windowSize)))
                .withStrategy(DetectionStrategy().threshold(0.0, 0.3))
                .build()
        val anomalyCheck2 = AggregateAnomalyCheck()
                .onApproxUniqueness("count")
                .withWindow(TumblingEventTimeWindows.of(Time.milliseconds(windowSize)))
                .withStrategy(DetectionStrategy().relativeRateOfChange(0.0, 0.3, 1))
                .build()
        val anomalyCheck3 = AggregateAnomalyCheck()
                .onApproxUniqueness("count")
                .withWindow(TumblingEventTimeWindows.of(Time.milliseconds(windowSize)))
                .withStrategy(DetectionStrategy().onlineNormal(0.0, 0.3, 0.0))
                .build()
        val anomalyCheck4 = AggregateAnomalyCheck()
                .onApproxUniqueness("count")
                .withWindow(TumblingEventTimeWindows.of(Time.milliseconds(windowSize)))
                .withStrategy(DetectionStrategy().absoluteChange(0.0, 0.3, 1))
                .build()
        //setup deserialization
        val source = util.generateWikiClickFileSourceFromPath(path)
        val wikiClickStream = env
                .fromSource(source, WatermarkStrategy.noWatermarks(), "Wiki click stream")
                .assignTimestampsAndWatermarks(
                        WatermarkStrategy.forMonotonousTimestamps<WikiClickStream>()
                                .withTimestampAssigner { _, _ -> System.currentTimeMillis() }
                )
        //detection
        val verificationResult = when (checkNumber) {
            1 -> VerificationSuite()
                    .onDataStream(wikiClickStream, env.config)
                    .addAnomalyChecks(mutableListOf(anomalyCheck1))
                    .build()

            2 -> VerificationSuite()
                    .onDataStream(wikiClickStream, env.config)
                    .addAnomalyChecks(mutableListOf(anomalyCheck1, anomalyCheck2))
                    .build()

            3 -> VerificationSuite()
                    .onDataStream(wikiClickStream, env.config)
                    .addAnomalyChecks(mutableListOf(anomalyCheck1, anomalyCheck2, anomalyCheck3))
                    .build()

            4 -> VerificationSuite()
                    .onDataStream(wikiClickStream, env.config)
                    .addAnomalyChecks(
                            mutableListOf(
                                    anomalyCheck1,
                                    anomalyCheck2,
                                    anomalyCheck3,
                                    anomalyCheck4
                            )
                    )
                    .build()

            else -> null
        }
        val actualAnomaliesList = when (checkNumber) {
            1 -> mutableListOf(verificationResult?.getResultsForCheck(anomalyCheck1))
            2 -> mutableListOf(
                    verificationResult?.getResultsForCheck(anomalyCheck1),
                    verificationResult?.getResultsForCheck(anomalyCheck2)
            )

            3 -> mutableListOf(
                    verificationResult?.getResultsForCheck(anomalyCheck1),
                    verificationResult?.getResultsForCheck(anomalyCheck2),
                    verificationResult?.getResultsForCheck(anomalyCheck3)
            )

            4 -> mutableListOf(
                    verificationResult?.getResultsForCheck(anomalyCheck1),
                    verificationResult?.getResultsForCheck(anomalyCheck2),
                    verificationResult?.getResultsForCheck(anomalyCheck3),
                    verificationResult?.getResultsForCheck(anomalyCheck4)
            )

            else -> mutableListOf<DataStream<AnomalyCheckResult>>()
        }
        //sink
        for (actualAnomalies in actualAnomaliesList) {
            actualAnomalies?.print("AnomalyCheckResult stream output")
        }
        val jobExecutionResult = env.execute()
        //save result
        val result = ExperimentResult(
                experimentName = "check number config: $checkNumber",
                timeInMs = jobExecutionResult.netRuntime,
                fileName = path.replace("/Users/wutong/Desktop/experiment/dataset/", "")
        )
        util.writeResultToCsvFile(result, CHECKNUMBER_CONFIG_OUTPUT_FILE_PATH)
    }

    /**
     * test net run time on partitioned data stream
     */
    fun testPartitionedRunTimeOnReddit(
            path: String,
            windowSize: Long = 1000
    ) {
        //setup env
        val env = util.createStreamExecutionEnvironment()
        //setup deserialization
        val source = util.generateRedditFileSourceFromPath(path)
        val redditPostStream = env
                .fromSource(source, WatermarkStrategy.noWatermarks(), "Reddit Posts")
                .assignTimestampsAndWatermarks(
                        WatermarkStrategy.forMonotonousTimestamps<RedditPost>()
                                .withTimestampAssigner { post, _ -> post.createdUtc!!.toLong() }
                )
                .keyBy { post -> post.partitionId }
        //detection
        val strategy = DetectionStrategy().onlineNormal(0.0, 0.3)
        val constraint = ApproxUniquenessConstraint("score")
        val actualAnomalies = strategy.detect(redditPostStream
                .windowAll(GlobalWindows.create())
                .trigger(CountTrigger.of(windowSize))
                .aggregate(constraint.getAggregateFunction(redditPostStream.type, redditPostStream.executionConfig)))
        //sink
        actualAnomalies.print("AnomalyCheckResult stream output")
        val jobExecutionResult = env.execute()
        //save result
        val partitionNumber = path.replace("/Users/wutong/Desktop/experiment/dataset/reddit_posts/", "")
                .replace("_partitions_20M_Reddit.csv", "")
        val result = ExperimentResult(
                experimentName = "partition number experiment: " + partitionNumber,
                timeInMs = jobExecutionResult.netRuntime,
                fileName = path.replace("/Users/wutong/Desktop/experiment/dataset/", "")
        )
        util.writeResultToCsvFile(result, PARTITON_CONFIG_OUTPUT_FILE_PATH)
    }

    fun testPartitionedRunTimeOnClickStream(path: String, windowSize: Long = 1000) {
        //setup env
        val conf = Configuration()
        conf.set(RestOptions.PORT, 8082)
        val env = StreamExecutionEnvironment.createLocalEnvironmentWithWebUI(conf)
        env.parallelism = 1
        //setup deserialization
        val source = util.generateWikiClickFileSourceFromPath(path)
        val wikiClickStream = env
                .fromSource(source, WatermarkStrategy.noWatermarks(), "Wiki Click Info")
                .keyBy { click -> click.partitionId }
        //detection
        val strategy = DetectionStrategy().onlineNormal(0.0, 0.3)
        val constraint = ApproxUniquenessConstraint("count")
        val actualAnomalies = strategy.detect(wikiClickStream
                .windowAll(GlobalWindows.create())
                .trigger(CountTrigger.of(windowSize))
                .aggregate(constraint.getAggregateFunction(wikiClickStream.type, wikiClickStream.executionConfig)))
        //sink
        actualAnomalies.print("AnomalyCheckResult stream output")
        val jobExecutionResult = env.execute()
        //save result
        val partitionNumber = path.replace("/Users/wutong/Desktop/experiment/dataset/ClickStream/", "")
                .replace("_partitions_2M_clickstream.csv", "")
        val result = ExperimentResult(
                experimentName = "partition number experiment: " + partitionNumber,
                timeInMs = jobExecutionResult.netRuntime,
                fileName = path.replace("/Users/wutong/Desktop/experiment/dataset/", "")
        )
        util.writeResultToCsvFile(result, PARTITON_CONFIG_OUTPUT_FILE_PATH)
    }
}
