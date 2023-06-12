package com.tong.streamdqexp

import com.tong.streamdqexp.experiment.Experiment
import com.tong.streamdqexp.logger.ExperimentLogger
import kotlinx.cli.ArgParser
import kotlinx.cli.ArgType
import kotlinx.cli.default
import kotlinx.cli.required

class LatencyExperimentApp {
    companion object {
        @JvmStatic
        fun main(args: Array<String>) {
            ExperimentLogger().info("LatencyExperimentApp started")
            val parser = ArgParser("LatencyExperimentApp")
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
                exp.testLatencyOnReddit(path, size.toLong())
            } else {
                exp.testLatencyOnClickStream(path, size.toLong())
            }
        }
    }
}