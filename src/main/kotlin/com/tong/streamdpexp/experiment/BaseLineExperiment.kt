package com.tong.streamdpexp.experiment

import org.apache.spark.sql.SparkSession

class BaseLineExperiment {

    companion object {
        @JvmStatic
        fun main(args: Array<String>) {
            val spark = SparkSession
                .builder()
                .appName("Java Spark SQL data sources example")
                .config("spark.master", "local")
                .getOrCreate()

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

//            val metricsRepository = InMemoryMetricsRepository()
//            val uniqueness = Uniqueness(mutableListOf("score"), null)
//
//            val verificationResult = VerificationSuite()
//                .onData(df)
//                .useRepository(metricsRepository)
//                .addAnomalyCheck(
//                    RelativeRateOfChangeStrategy(Some(1.0), Some(2.0), 1),
//                    uniqueness,
//                    Some(
//                        AnomalyCheckConfig(
//                            CheckLevel.Error(), "Anomaly check to succeed",
//                        null, Some(0), Some(11))
//                    )
//                )
//                .run()
        }
    }
}