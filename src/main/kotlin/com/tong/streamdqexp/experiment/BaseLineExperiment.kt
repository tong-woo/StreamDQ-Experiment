package com.tong.streamdqexp.experiment

import com.amazon.deequ.VerificationSuite
import com.amazon.deequ.checks.Check
import com.amazon.deequ.checks.CheckLevel
import com.amazon.deequ.checks.CheckStatus
import com.amazon.deequ.constraints.Constraint
import com.amazon.deequ.constraints.ConstraintStatus
import com.amazon.deequ.repository.memory.InMemoryMetricsRepository
import org.apache.spark.sql.SparkSession
import scala.Option
import scala.collection.JavaConverters.asScalaIteratorConverter
import scala.collection.Seq

class BaseLineExperiment {

    companion object {
        @JvmStatic
        fun main(args: Array<String>) {
            val spark = SparkSession
                .builder()
                .appName("Java Spark SQL data sources example")
                .config("spark.master", "local")
                .orCreate

            val path = "/Users/wutong/Desktop/experiment/dataset/reddit_posts/20M_reddit_posts.csv"
            val df = spark.read()
                .format("com.databricks.spark.csv")
                .option("header", "true")
                .option("delimiter", ",")
                .option("treatEmptyValuesAsNulls", "true")
                .option("mode", "DROPMALFORMED")
                .option("timestampFormat", "MM-dd-yyyy hh mm ss")
                .load(path)
            df.show()

//            val myList = listOf("score")
//            val scalaSeq = convertListToSeq(myList)
//            val uniqueness = Uniqueness(scalaSeq, null)
//            val completeness = Completeness("score", null)

            val metricsRepository = InMemoryMetricsRepository()
            val constraintSeq = listOf<Constraint>()

            val verificationResultCompleteness = VerificationSuite()
                .onData(df)
                .useRepository(metricsRepository)
                .addCheck(
                    Check(
                        CheckLevel.Warning(),
                        "random checks",
                        asScalaIteratorConverter(constraintSeq.iterator()).asScala().toSeq()
                    )
                        .isComplete("removed_by", Option.empty())
                )
                .run()

            val analyzerToMetricMap = verificationResultCompleteness.metrics()
            val completenessAnalyzer = analyzerToMetricMap.keySet().iterator().next()
//            val state = completenessAnalyzer.computeStateFrom(df)
//
//            val verificationResult = VerificationSuite()
//                .onData(df)
//                .useRepository(metricsRepository)
//                .addAnomalyCheck(
//                    RelativeRateOfChangeStrategy(Some(1.0), Some(2.0), 1),
//                    completenessAnalyzer,
//                    Some(
//                        AnomalyCheckConfig(
//                            CheckLevel.Error(), "Anomaly check to succeed",
//                        null, Some(0), Some(11))
//                    )
//                )
//                .run()

            if (verificationResultCompleteness.status() == CheckStatus.Success()) {
                println("The data passed the test, everything is fine!")
            } else {
                println("We found errors in the data, the following constraints were not satisfied:\n")
                val resultsForAllChecks =
                    verificationResultCompleteness.checkResults().values().toList()
                val resultsForAllConstraints = resultsForAllChecks.iterator()
                    .map { res -> res.constraintResults() }.toList()
                for (seq in resultsForAllConstraints) {
                    for (res in seq) {
                        if (res.status() != ConstraintStatus.Success()) {
                            println("${res.constraint()} failed: ${res.message().get()}")
                        }
                    }
                }
            }
        }

        fun convertListToSeq(inputList: List<String?>): Seq<String?>? {
            return asScalaIteratorConverter(inputList.iterator()).asScala().toSeq()
        }
    }
}