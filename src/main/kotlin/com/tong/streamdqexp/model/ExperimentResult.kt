package com.tong.streamdqexp.model

import org.apache.flink.shaded.jackson2.com.fasterxml.jackson.annotation.JsonPropertyOrder
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

@JsonPropertyOrder("experimentTime", "experimentName", "time", "fileName")
data class ExperimentResult constructor(
    val experimentTime: String = LocalDateTime.now()
        .format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm")),
    val experimentName: String,
    val time: Long,
    val fileName: String
)