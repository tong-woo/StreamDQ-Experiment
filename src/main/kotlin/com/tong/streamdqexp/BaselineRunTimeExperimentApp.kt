package com.tong.streamdqexp

import com.tong.streamdqexp.experiment.BaselineExperiment
import com.tong.streamdqexp.logger.ExperimentLogger
import kotlinx.cli.ArgParser
import kotlinx.cli.ArgType
import kotlinx.cli.required

class BaselineRunTimeExperimentApp {
    companion object {
        @JvmStatic
        fun main(args: Array<String>) {
            ExperimentLogger().info("Baseline RunTimeExperimentApp started")
            val parser = ArgParser("BaselineRunTimeExperimentApp")
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
            if (path.contains("reddit")) {
                exp.testRunTimeOnDataSet(path, columnName)
            } else {
                exp.testRunTimeOnDataSet(path, columnName)
            }
        }
    }
}