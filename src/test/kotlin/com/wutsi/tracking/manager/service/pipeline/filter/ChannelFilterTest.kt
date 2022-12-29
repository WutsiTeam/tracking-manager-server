package com.wutsi.tracking.manager.service.pipeline.filter

import com.wutsi.enums.ChannelType
import com.wutsi.tracking.manager.entity.TrackEntity
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test

internal class ChannelFilterTest {
    private val filter = ChannelFilter()

    @Test
    fun `null`() {
        val track = filter.filter(createTrack())
        assertEquals(ChannelType.UNKNOWN.name, track.channel)
    }

    @Test
    fun empty() {
        val track = filter.filter(createTrack("", ""))
        assertEquals(ChannelType.UNKNOWN.name, track.channel)
    }

    @Test
    fun utmMedium() {
        val track = filter.filter(createTrack("https://www.wutsi.com?utm_medium=email", null))
        assertEquals(ChannelType.EMAIL.name, track.channel)
    }

    @Test
    fun app() {
        val track = filter.filter(createTrack(ua = "Dart/2.16 (dart:io)"))
        assertEquals(ChannelType.APP.name, track.channel)
    }

    @Test
    fun refererEmail() {
        val track = filter.filter(createTrack("https://www.wutsi.com", "https://mail.yahoo.com/m/120932"))
        assertEquals(ChannelType.EMAIL.name, track.channel)
    }

    @Test
    fun whatsapp() {
        val track = filter.filter(createTrack(referrer = "https://wa.me"))
        assertEquals(ChannelType.MESSAGING.name, track.channel)
    }

    @Test
    fun social() {
        val track = filter.filter(createTrack(referrer = "https://facebook.com"))
        assertEquals(ChannelType.SOCIAL.name, track.channel)
    }

    @Test
    fun seo() {
        val track = filter.filter(createTrack(referrer = "https://www.google.com"))
        assertEquals(ChannelType.SEO.name, track.channel)
    }

    private fun createTrack(
        url: String? = null,
        referrer: String? = null,
        ua: String? = null,
    ) = TrackEntity(
        url = url,
        referrer = referrer,
        ua = ua,
    )
}
