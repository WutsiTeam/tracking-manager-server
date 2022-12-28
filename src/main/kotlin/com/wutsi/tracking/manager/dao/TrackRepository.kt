package com.wutsi.tracking.manager.dao

import com.wutsi.platform.core.storage.StorageService
import com.wutsi.tracking.manager.entity.TrackEntity
import org.apache.commons.csv.CSVFormat
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

    private fun storeLocally(items: List<TrackEntity>, out: OutputStream) {
        val writer = BufferedWriter(OutputStreamWriter(out))
        writer.use {
            val printer = CSVPrinter(
                writer,
                CSVFormat.DEFAULT
                    .builder()
                    .setHeader(
                        "time",
                        "correlation_id",
                        "device_id",
                        "account_id",
                        "merchant_id",
                        "product_id",
                        "page",
                        "event",
                        "value",
                        "ip",
                        "long",
                        "lat",
                        "bot",
                        "device_type",
                        "channel",
                        "source",
                        "campaign",
                        "referer",
                        "url",
                        "ua",
                    )
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
                        it.ip,
                        it.long,
                        it.lat,
                        it.bot,
                        it.deviceType,
                        it.channel,
                        it.source,
                        it.campaign,
                        it.url,
                        it.referer,
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
