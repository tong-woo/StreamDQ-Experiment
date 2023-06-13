package com.tong.streamdqexp.processfunction

import com.stefan_grafberger.streamdq.anomalydetection.model.AnomalyCheckResult
import org.apache.flink.api.java.tuple.Tuple2
import org.apache.flink.configuration.Configuration
import org.apache.flink.runtime.metrics.DescriptiveStatisticsHistogram
import org.apache.flink.streaming.api.functions.ProcessFunction
import org.apache.flink.util.Collector


class AnomalyCheckResultProcessFunction(private val windowSize: Long) :
    ProcessFunction<AnomalyCheckResult, Tuple2<AnomalyCheckResult, Long>>() {

    private lateinit var eventAndProcessingTimeLag: DescriptiveStatisticsHistogram

    override fun open(parameters: Configuration?) {
        this.eventAndProcessingTimeLag = runtimeContext.metricGroup.histogram(
            "eventAndProcessingTimeLag",
            DescriptiveStatisticsHistogram(windowSize.toInt())
        )
    }

    override fun processElement(
        anomalyCheckResult: AnomalyCheckResult,
        context: Context,
        collector: Collector<Tuple2<AnomalyCheckResult, Long>>
    ) {
        val latency = context.timerService().currentProcessingTime() - context.timerService()
            .currentWatermark()
        eventAndProcessingTimeLag.update(latency)
        collector.collect(Tuple2(anomalyCheckResult, latency))
    }
}