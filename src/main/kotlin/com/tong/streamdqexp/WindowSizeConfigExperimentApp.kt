package com.tong.streamdqexp

import com.tong.streamdqexp.experiment.Experiment
import com.tong.streamdqexp.logger.ExperimentLogger
import kotlinx.cli.ArgParser
import kotlinx.cli.ArgType
import kotlinx.cli.default
import kotlinx.cli.required

class WindowSizeConfigExperimentApp {
    companion object {
        @JvmStatic
        fun main(args: Array<String>) {
            ExperimentLogger().info("WindowSizeConfigExperimentApp started")
            val parser = ArgParser("WindowSizeConfigExperimentApp")
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
            val repeat by parser.option(
                ArgType.Int,
                shortName = "r",
                description = "repeat times of the experiment"
            ).default(1)
            parser.parse(args)
            val exp = Experiment()
            if (path.contains("reddit")) {
                repeat(repeat) {
                    exp.testWindowConfigOnReddit(path, size.toLong())
                }
            } else {
                repeat(repeat) {
                    exp.testWindowConfigOnClickStream(path, size.toLong())
                }
            }
        }
    }
}