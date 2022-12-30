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
        assertEquals(ChannelType.WEB.name, track.channel)
    }

    @Test
    fun empty() {
        val track = filter.filter(createTrack("", ""))
        assertEquals(ChannelType.WEB.name, track.channel)
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
    fun whatsappReferer() {
        val track = filter.filter(createTrack(referrer = "https://wa.me"))
        assertEquals(ChannelType.MESSAGING.name, track.channel)
    }

    @Test
    fun twitterReferer() {
        assertEquals(ChannelType.SOCIAL.name, filter.filter(createTrack(referrer = "https://www.twitter.com")).channel)
        assertEquals(ChannelType.SOCIAL.name, filter.filter(createTrack(referrer = "https://t.co")).channel)
    }

    @Test
    fun twitterUA() {
        val track =
            filter.filter(createTrack(ua = "Mozilla/5.0 (iPhone; CPU iPhone OS 15_1 like Mac OS X) AppleWebKit/605.1.15 (KHTML, like Gecko) Mobile/14F89 Twitter for iPhone/7.21.1"))
        assertEquals(ChannelType.SOCIAL.name, track.channel)
    }

    @Test
    fun facebookReferer() {
        assertEquals(ChannelType.SOCIAL.name, filter.filter(createTrack(referrer = "https://l.facebook.com")).channel)
        assertEquals(ChannelType.SOCIAL.name, filter.filter(createTrack(referrer = "https://www.facebook.com")).channel)
    }

    @Test
    fun facebookUA() {
        val track =
            filter.filter(createTrack(ua = "Mozilla/5.0 (iPhone; CPU iPhone OS 16_1_1 like Mac OS X) AppleWebKit/605.1.15 (KHTML, like Gecko) Mobile/20B101 [FBAN/FBIOS;FBDV/iPhone12,1;FBMD/iPhone;FBSN/iOS;FBSV/16.1.1;FBSS/2;FBID/phone;FBLC/en_US;FBOP/5]"))
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
