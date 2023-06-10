package com.tong.streamdpexp.experiment

import com.tong.streamdpexp.experiment.logger.ExperimentLogger
import kotlinx.cli.ArgParser
import kotlinx.cli.ArgType
import kotlinx.cli.default
import kotlinx.cli.required

class RunTimeExperimentApp {
    companion object {
        @JvmStatic
        fun main(args: Array<String>) {
            ExperimentLogger().info("RunTimeExperimentApp started")
            val parser = ArgParser("RunTimeExperimentApp")
            val path by parser.option(
                ArgType.String,
                shortName = "p",
                description = "path of the csv dataset"
            ).required()
            val size by parser.option(
                ArgType.Int,
                shortName = "s",
                description = "size of the window"
            ).default(1000)
            parser.parse(args)
            val exp = Experiment()
            if (path.contains("reddit")) {
                exp.testRunTimeOnRedditDataSetWithOnlyAggregation(path, size.toLong())
            } else {
                exp.testRunTimePerformanceOnClickStream(path, size.toLong())
            }
        }
    }
}