package com.wutsi.tracking.manager.service.pipeline.filter

import com.wutsi.tracking.manager.entity.TrackEntity
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertNull
import org.junit.jupiter.api.Test

internal class SourceFilterTest {
    private val filter = SourceFilter()

    @Test
    fun `null`() {
        val track = filter.filter(createTrack(null))
        assertNull(track.source)
    }

    @Test
    fun empty() {
        val track = filter.filter(createTrack(""))
        assertNull(track.source)
    }

    @Test
    fun utmSource() {
        val track = filter.filter(createTrack("https://www.f.com?utm_source=foo"))
        assertEquals("foo", track.source)
    }

    private fun createTrack(url: String?) = TrackEntity(
        url = url,
    )
}
