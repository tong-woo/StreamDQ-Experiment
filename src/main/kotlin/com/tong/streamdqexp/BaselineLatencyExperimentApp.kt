package com.tong.streamdqexp

import com.tong.streamdqexp.experiment.BaselineExperiment
import com.tong.streamdqexp.logger.ExperimentLogger
import kotlinx.cli.ArgParser
import kotlinx.cli.ArgType
import kotlinx.cli.required

class BaselineLatencyExperimentApp {
    companion object {
        @JvmStatic
        fun main(args: Array<String>) {
            ExperimentLogger().info("BaselineLatencyExperimentApp started")
            val parser = ArgParser("BaselineLatencyExperimentApp")
            val path by parser.option(
                ArgType.String,
                shortName = "p",
                description = "path of the csv dataset"
            ).required()
            val columnName by parser.option(
                ArgType.String,
                shortName = "c",
                description = "column name of the csv dataset"
            ).required()
            parser.parse(args)
            val exp = BaselineExperiment()
            exp.testLatencyOnDataSet(path, columnName)
        }
    }
}