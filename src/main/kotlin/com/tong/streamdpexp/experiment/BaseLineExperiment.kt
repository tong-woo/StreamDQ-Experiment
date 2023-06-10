package com.tong.streamdpexp.experiment

import com.amazon.deequ.repository.memory.InMemoryMetricsRepository
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

            val metricsRepository = InMemoryMetricsRepository()

//            val verificationResult = VerificationSuite()
//                .onData(df)
//                .useRepository(metricsRepository)
//                .addAnomalyCheck(
//                    RelativeRateOfChangeStrategy(1.0, 2.0, 1),
//                    Size(),
//                    null
//                )
//                .run()
        }
    }
}