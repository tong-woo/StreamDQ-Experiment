package com.tong.streamdqexp.util

import com.tong.streamdqexp.model.ExperimentResult
import com.tong.streamdqexp.model.RedditPost
import com.tong.streamdqexp.model.WikiClickStream
import org.apache.flink.api.common.typeinfo.TypeInformation
import org.apache.flink.configuration.Configuration
import org.apache.flink.configuration.RestOptions
import org.apache.flink.connector.file.src.FileSource
import org.apache.flink.core.fs.Path
import org.apache.flink.formats.csv.CsvReaderFormat
import org.apache.flink.shaded.guava30.com.google.common.base.Function
import org.apache.flink.shaded.jackson2.com.fasterxml.jackson.databind.DeserializationFeature
import org.apache.flink.shaded.jackson2.com.fasterxml.jackson.dataformat.csv.CsvMapper
import org.apache.flink.shaded.jackson2.com.fasterxml.jackson.dataformat.csv.CsvParser
import org.apache.flink.shaded.jackson2.com.fasterxml.jackson.dataformat.csv.CsvSchema
import org.apache.flink.streaming.api.environment.StreamExecutionEnvironment
import scala.collection.JavaConverters
import scala.collection.Seq
import java.io.File
import java.io.FileOutputStream
import java.lang.reflect.ParameterizedType
import java.lang.reflect.Type
import kotlin.math.ceil


class ExperimentUtil {
    fun createStreamExecutionEnvironment(): StreamExecutionEnvironment {
        val conf = Configuration()
        conf.setInteger(RestOptions.PORT, 8081)
        val env: StreamExecutionEnvironment =
            StreamExecutionEnvironment.createLocalEnvironmentWithWebUI(conf)
        env.parallelism = 1
        return env
    }

    fun generateRedditFileSourceFromPath(path: String): FileSource<RedditPost>? {
        val schemaGenerator = Function<CsvMapper, CsvSchema> { mapper ->
            mapper?.schemaFor(RedditPost::class.java)
                ?.withQuoteChar('"')
                ?.withColumnSeparator(',')
                ?.withNullValue("")
                ?.withSkipFirstDataRow(true)
        }
        val mapper = CsvMapper.builder()
            .configure(DeserializationFeature.ACCEPT_EMPTY_STRING_AS_NULL_OBJECT, true)
            .build()
        val csvFormat = CsvReaderFormat
            .forSchema(schemaGenerator.apply(mapper), TypeInformation.of(RedditPost::class.java))
        return FileSource
            .forRecordStreamFormat(csvFormat, Path(path))
            .build()
    }

    fun generateWikiClickFileSourceFromPath(path: String): FileSource<WikiClickStream>? {
        val schemaGenerator = Function<CsvMapper, CsvSchema> { mapper ->
            mapper?.schemaFor(WikiClickStream::class.java)
                ?.withQuoteChar('"')
                ?.withColumnSeparator(',')
                ?.withNullValue("")
                ?.withSkipFirstDataRow(true)
        }
        val mapper = CsvMapper.builder()
            .configure(DeserializationFeature.ACCEPT_EMPTY_STRING_AS_NULL_OBJECT, true)
            .build()
        val csvFormat = CsvReaderFormat
            .forSchema(
                schemaGenerator.apply(mapper),
                TypeInformation.of(WikiClickStream::class.java)
            )
        return FileSource
            .forRecordStreamFormat(csvFormat, Path(path))
            .build()
    }

    inline fun <R> executeAndMeasureTimeMillis(block: () -> R): Pair<R, Long> {
        val start = System.currentTimeMillis()
        val result = block()
        return result to (System.currentTimeMillis() - start)
    }

    fun percentile(latencies: List<Long>, percentile: Double): Long {
        latencies.sorted().filter { element -> element in 0 until 9223370350253956883 }
        val index = ceil(percentile / 100.0 * latencies.size).toInt()
        return latencies[index - 1]
    }

    fun <T> convertListToSeq(inputList: List<T?>): Seq<T?>? {
        return JavaConverters.asScalaIteratorConverter(inputList.iterator()).asScala().toSeq()
    }

    fun getType(raw: Class<*>, vararg args: Type) = object : ParameterizedType {
        override fun getRawType(): Type = raw
        override fun getActualTypeArguments(): Array<out Type> = args
        override fun getOwnerType(): Type? = null
    }

    fun writeResultToCsvFile(result: ExperimentResult, filePath: String) {
        val csvOutputFile = File(filePath)
        if (!csvOutputFile.exists()) {
            csvOutputFile.createNewFile()
        }
        val csvMapper = CsvMapper().apply {
            enable(CsvParser.Feature.TRIM_SPACES)
            enable(CsvParser.Feature.SKIP_EMPTY_LINES)
        }
        val schema = CsvSchema.builder()
            .addColumn("experimentDate")
            .addColumn("experimentName")
            .addColumn("timeInMs")
            .addColumn("fileName")
            .build()
            .withColumnSeparator(',')
        val outputStream = FileOutputStream(csvOutputFile, true)
        csvMapper.writer(schema).writeValues(outputStream).write(result)
    }
}