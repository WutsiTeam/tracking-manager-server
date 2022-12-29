package com.wutsi.tracking.manager.dao

import com.wutsi.platform.core.storage.StorageService
import com.wutsi.tracking.manager.entity.TrackEntity
import org.apache.commons.csv.CSVFormat
import org.apache.commons.csv.CSVParser
import org.apache.commons.csv.CSVPrinter
import org.springframework.stereotype.Service
import java.io.BufferedWriter
import java.io.File
import java.io.FileInputStream
import java.io.FileOutputStream
import java.io.InputStream
import java.io.OutputStream
import java.io.OutputStreamWriter
import java.net.URL
import java.text.SimpleDateFormat
import java.time.Clock
import java.util.Date
import java.util.TimeZone
import java.util.UUID

@Service
class TrackRepository(
    private val storage: StorageService,
    private val clock: Clock,
) {
    companion object {
        private val HEADERS = arrayOf(
            "time",
            "correlation_id",
            "device_id",
            "account_id",
            "merchant_id",
            "product_id",
            "page",
            "event",
            "value",
            "revenue",
            "ip",
            "long",
            "lat",
            "bot",
            "device_type",
            "channel",
            "source",
            "campaign",
            "referrer",
            "url",
            "ua",
        )
    }

    fun save(items: List<TrackEntity>): URL {
        val file = File.createTempFile(UUID.randomUUID().toString(), "csv")
        try {
            // Store to file
            val output = FileOutputStream(file)
            output.use {
                storeLocally(items, output)
            }

            // Store to cloud
            val input = FileInputStream(file)
            input.use {
                return storeToCloud(input)
            }
        } finally {
            file.delete()
        }
    }

    fun read(input: InputStream, filter: TrackFilter? = null): List<TrackEntity> {
        val parser = CSVParser.parse(
            input,
            Charsets.UTF_8,
            CSVFormat.Builder.create()
                .setSkipHeaderRecord(true)
                .setDelimiter(",")
                .setHeader(*HEADERS)
                .build(),
        )
        return parser.map {
            TrackEntity(
                time = it.get("time").toLong(),
                correlationId = it.get("correlationId"),
                deviceId = it.get("device_id"),
                accountId = it.get("account_id"),
                merchantId = it.get("merchant_id"),
                productId = it.get("product_id"),
                page = it.get("page"),
                event = it.get("event"),
                value = it.get("value"),
                revenue = it.get("revenue").toLong(),
                ip = it.get("ip"),
                lat = it.get("lat").toDouble(),
                long = it.get("long").toDouble(),
                bot = it.get("bot").toBoolean(),
                deviceType = it.get("device_type"),
                channel = it.get("channel"),
                source = it.get("source"),
                campaign = it.get("campaign"),
                url = it.get("url"),
                referrer = it.get("referrer"),
                ua = it.get("ua"),
            )
        }.filter {
            filter == null || filter.accept(it)
        }
    }

    private fun storeLocally(items: List<TrackEntity>, out: OutputStream) {
        val writer = BufferedWriter(OutputStreamWriter(out))
        writer.use {
            val printer = CSVPrinter(
                writer,
                CSVFormat.DEFAULT
                    .builder()
                    .setHeader(*HEADERS)
                    .build(),
            )
            printer.use {
                items.forEach {
                    printer.printRecord(
                        it.time,
                        it.correlationId,
                        it.deviceId,
                        it.accountId,
                        it.merchantId,
                        it.productId,
                        it.page,
                        it.event,
                        it.value,
                        it.revenue,
                        it.ip,
                        it.long,
                        it.lat,
                        it.bot,
                        it.deviceType,
                        it.channel,
                        it.source,
                        it.campaign,
                        it.url,
                        it.referrer,
                        it.ua,
                    )
                }
                printer.flush()
            }
        }
    }

    private fun storeToCloud(input: InputStream): URL {
        val fmt = SimpleDateFormat("yyyy/MM/dd")
        fmt.timeZone = TimeZone.getTimeZone("UTC")

        val folder = fmt.format(Date(clock.millis()))
        val file = UUID.randomUUID().toString() + ".csv"
        val path = "track/$folder/$file"
        return storage.store(path, input, "text/csv", Int.MAX_VALUE)
    }
}
