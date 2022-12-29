package com.wutsi.tracking.manager.service.aggregator

import com.wutsi.platform.core.storage.StorageService
import java.io.ByteArrayInputStream
import java.io.ByteArrayOutputStream
import java.io.InputStream
import java.net.URL

class StorageInputStreamIterator(
    private val urls: List<URL>,
    private val storage: StorageService,
) : InputStreamIterator {
    private var index = 0

    override fun next(): InputStream {
        ByteArrayOutputStream().use { out ->
            try {
                storage.get(urls[index++], out)
                return ByteArrayInputStream(out.toByteArray())
            } catch (ex: IndexOutOfBoundsException) {
                throw NoSuchElementException()
            }
        }
    }

    override fun hasNext(): Boolean =
        index < urls.size
}
