package com.tong.streamdqexp.experiment

import com.amazon.deequ.AnomalyCheckConfig
import com.amazon.deequ.VerificationSuite
import com.amazon.deequ.analyzers.Analyzer
import com.amazon.deequ.analyzers.FrequenciesAndNumRows
import com.amazon.deequ.analyzers.Uniqueness
import com.amazon.deequ.anomalydetection.OnlineNormalStrategy
import com.amazon.deequ.anomalydetection.RelativeRateOfChangeStrategy
import com.amazon.deequ.checks.CheckLevel
import com.amazon.deequ.checks.CheckStatus
import com.amazon.deequ.metrics.Metric
import com.amazon.deequ.repository.memory.InMemoryMetricsRepository
import com.tong.streamdqexp.logger.ExperimentLogger
import com.tong.streamdqexp.model.ExperimentResult
import com.tong.streamdqexp.util.ExperimentUtil
import org.apache.spark.SparkConf
import org.apache.spark.SparkContext
import org.apache.spark.sql.SparkSession
import scala.Option
import scala.Some

/**
 * baseline experiment using aws deequ library
 */
class BaselineExperiment {

    private val util = ExperimentUtil()
    private val RUNTIME_OUTPUT_FILE_PATH =
        "/Users/wutong/Desktop/Thesis/streamdpexp/experimentresult/runtimeBaseline.csv"
    private val LATENCY_OUTPUT_FILE_PATH =
        "/Users/wutong/Desktop/Thesis/streamdpexp/experimentresult/latencyBaseline.csv"

    fun testRunTimeOnDataSet(path: String, columnName: String) {
        val conf = SparkConf().setAppName("Java Spark SQL data sources example")
            .setMaster("local")
            .set("spark.testing.memory", "471859200")
        val sc = SparkContext(conf)
        val spark = SparkSession
            .builder()
            .appName("Java Spark SQL data sources example")
            .config(conf)
            .sparkContext(sc)
            .orCreate
        // initialize sparkMeasure
        val taskMetrics = ch.cern.sparkmeasure.TaskMetrics(spark) // initialize sparkMeasure
        taskMetrics.runAndMeasure {
            val df = spark.read()
                .format("com.databricks.spark.csv")
                .option("header", "true")
                .option("delimiter", ",")
                .option("treatEmptyValuesAsNulls", "true")
                .option("mode", "DROPMALFORMED")
                .option("timestampFormat", "MM-dd-yyyy hh mm ss")
                .load(path)
            val metricsRepository = InMemoryMetricsRepository()
            val verificationResult = VerificationSuite()
                .onData(df)
                .useRepository(metricsRepository)
                .addAnomalyCheck(
                    OnlineNormalStrategy(Some(0.1), Some(0.3), 0.0, true),
                    Uniqueness(
                        util.convertListToSeq(listOf(columnName)),
                        Option.empty()
                    ) as Analyzer<FrequenciesAndNumRows, Metric<Any>>,
                    Some(
                        AnomalyCheckConfig(
                            CheckLevel.Error(), "Anomaly check to succeed",
                            null, Some(0.toLong()), Some(11.toLong())
                        )
                    )
                )
                .run()
            if (verificationResult.status() != CheckStatus.Success()) {
                println("Anomaly detected in the metric!")
                /* Let's have a look at the actual metrics. */
                metricsRepository
                    .load()
                    .forAnalyzers(
                        util.convertListToSeq(
                            listOf(
                                Uniqueness(
                                    util.convertListToSeq(listOf(columnName)),
                                    Option.empty()
                                ) as Analyzer<*, Metric<*>>
                            )
                        )
                    )
                    .getSuccessMetricsAsDataFrame(spark, util.convertListToSeq(listOf()))
                    .show()
            }
        }
        val collectedTaskMetrics = taskMetrics.aggregateTaskMetrics()
        ExperimentLogger().info("Spark Job Duration = ${collectedTaskMetrics.get("taskDuration")}")
        spark.stop()
        //save result
        val result = ExperimentResult(
            experimentName = "baseline runtime",
            timeInMs = collectedTaskMetrics.get("taskDuration").toString().filter { it.isDigit() }
                .toLong(),
            fileName = path.replace("/Users/wutong/Desktop/experiment/dataset/", "")
        )
        util.writeResultToCsvFile(result, RUNTIME_OUTPUT_FILE_PATH)
    }

    fun testLatencyOnDataSet(path: String, columnName: String) {
        val conf = SparkConf().setAppName("Java Spark SQL data sources example")
            .setMaster("local")
            .set("spark.testing.memory", "471859200")
        val sc = SparkContext(conf)
        val spark = SparkSession
            .builder()
            .appName("Java Spark SQL data sources example")
            .config(conf)
            .sparkContext(sc)
            .orCreate
        // initialize sparkMeasure
        val taskMetrics = ch.cern.sparkmeasure.TaskMetrics(spark) // initialize sparkMeasure
        val df = spark.read()
            .format("com.databricks.spark.csv")
            .option("header", "true")
            .option("delimiter", ",")
            .option("treatEmptyValuesAsNulls", "true")
            .option("mode", "DROPMALFORMED")
            .option("timestampFormat", "MM-dd-yyyy hh mm ss")
            .load(path)
        val metricsRepository = InMemoryMetricsRepository()
        taskMetrics.runAndMeasure {
            VerificationSuite()
                .onData(df)
                .useRepository(metricsRepository)
                .addAnomalyCheck(
                    RelativeRateOfChangeStrategy(Some(0.1), Some(0.3), 1),
                    Uniqueness(
                        util.convertListToSeq(listOf(columnName)),
                        Option.empty()
                    ) as Analyzer<FrequenciesAndNumRows, Metric<Any>>,
                    Some(
                        AnomalyCheckConfig(
                            CheckLevel.Error(), "Anomaly check to succeed",
                            null, Some(0.toLong()), Some(11.toLong())
                        )
                    )
                )
                .run()
        }
        val collectedTaskMetrics = taskMetrics.aggregateTaskMetrics()
        ExperimentLogger().info("Spark Job Duration = ${collectedTaskMetrics.get("taskDuration")}")
        spark.stop()
        //save result
        val result = ExperimentResult(
            experimentName = "baseline latency",
            timeInMs = collectedTaskMetrics.get("taskDuration").toString().filter { it.isDigit() }
                .toLong(),
            fileName = path.replace("/Users/wutong/Desktop/experiment/dataset/", "")
        )
        util.writeResultToCsvFile(result, LATENCY_OUTPUT_FILE_PATH)
    }
}
