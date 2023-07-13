package com.tong.streamdqexp

import com.tong.streamdqexp.experiment.Experiment
import com.tong.streamdqexp.logger.ExperimentLogger
import kotlinx.cli.ArgParser
import kotlinx.cli.ArgType
import kotlinx.cli.default
import kotlinx.cli.required

class PartitionNumberConfigExperimentApp {
    companion object {
        @JvmStatic
        fun main(args: Array<String>) {
            ExperimentLogger().info("PartitionNumberConfigExperimentApp started")
            val parser = ArgParser("PartitionNumberConfigExperimentApp")
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
            if (path.contains("Reddit")) {
                repeat(repeat){
                    exp.testPartitionedRunTimeOnReddit(path, size.toLong())
                }
            } else {
                repeat(repeat){
                    exp.testPartitionedRunTimeOnClickStream(path, size.toLong())
                }
            }
        }
    }
}