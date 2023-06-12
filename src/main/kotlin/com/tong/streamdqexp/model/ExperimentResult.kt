package com.tong.streamdqexp.model

import org.apache.flink.shaded.jackson2.com.fasterxml.jackson.annotation.JsonPropertyOrder
import scala.Option
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

@JsonPropertyOrder("experimentDate", "experimentName", "timeInMs", "fileName")
data class ExperimentResult(
    val experimentDate: String = LocalDateTime.now()
        .format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm")),
    val experimentName: String,
    val timeInMs: Long,
    val fileName: String
)