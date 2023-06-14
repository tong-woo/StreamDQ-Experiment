package com.tong.streamdqexp

import com.tong.streamdqexp.experiment.BaselineExperiment
import com.tong.streamdqexp.logger.ExperimentLogger
import kotlinx.cli.ArgParser
import kotlinx.cli.ArgType
import kotlinx.cli.default
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
            val repeat by parser.option(
                ArgType.Int,
                shortName = "r",
                description = "repeat times of the experiment"
            ).default(1)
            parser.parse(args)
            val exp = BaselineExperiment()
            repeat(repeat) {
                exp.testRunTimeOnDataSet(path, columnName)
            }
        }
    }
}