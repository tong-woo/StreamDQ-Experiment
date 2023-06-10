package com.tong.streamdpexp.experiment

import kotlinx.cli.ArgParser
import kotlinx.cli.ArgType
import kotlinx.cli.default
import kotlinx.cli.required

class ExperimentApp {
    companion object {
        @JvmStatic
        fun main(args: Array<String>) {
            val parser = ArgParser("ExperimentApp")
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
            exp.testAnomalyDetectionRunTimeOnRedditDataSetEndToEnd(path, size.toLong())
        }
    }
}