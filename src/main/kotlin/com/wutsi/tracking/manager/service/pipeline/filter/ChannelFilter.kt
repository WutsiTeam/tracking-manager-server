package com.wutsi.tracking.manager.service.pipeline.filter

import com.wutsi.enums.ChannelType
import com.wutsi.tracking.manager.entity.TrackEntity
import com.wutsi.tracking.manager.service.pipeline.Filter
import com.wutsi.tracking.manager.util.URLUtil
import java.util.Locale

class ChannelFilter : Filter {
    companion object {
        private val SEO_DOMAINS = arrayListOf(
            "google",
            "bing",
            "yahoo",
        )
        private val EMAIL_DOMAINS = arrayListOf(
            "mail.google.com",
            "mail.yahoo.com",
            "outlook.live.com",
        )
        private val SOCIAL_DOMAINS = arrayListOf(
            "facebook.com",
            "t.co",
            "twitter.com",
            "linkedin.com",
            "pinterest.com",
            "instagram.com",
            "snapchat.com",
        )
        private val MESSAGING_DOMAINS = arrayListOf(
            // Whatsapp - https://www.netify.ai/resources/applications/whatsapp
            "wa.me",
            "whatsapp.com",

            // Telegram - see https://www.netify.ai/resources/applications/telegram
            "t.me",
            "telegram.me",
            "telegram.org",
            "telegram.com",

            // Messenger - see https://www.netify.ai/resources/applications/messenger
            "m.me",
            "messenger.com",
            "msngr.com",
        )
    }

    override fun filter(track: TrackEntity): TrackEntity {
        // APP
        if (track.ua?.contains("(dart:io)", true) == true) {
            return track.copy(channel = ChannelType.APP.name)
        }

        // Query string
        if (!track.url.isNullOrEmpty()) {
            val channel = URLUtil.extractParams(track.url)["utm_medium"]
            if (channel != null) {
                return track.copy(channel = channel.uppercase())
            }
        }

        // User Agent
        val ua = track.ua
        if (ua != null) {
            if (
                ua.contains("WhatsApp") ||
                ua.contains("FBAN/Messenger") ||
                ua.contains("FB_IAB/MESSENGER") ||
                ua.contains("TelegramBot (like TwitterBot)")
            ) {
                return track.copy(channel = ChannelType.MESSAGING.name)
            } else if (
                ua.contains("Twitter") ||
                ua.contains("TikTok") ||
                ua.contains("FBAN/FB")
            ) {
                return track.copy(channel = ChannelType.SOCIAL.name)
            }
        }

        // Referer
        val domain = extractDomainName(track.referrer)
        val channel = if (EMAIL_DOMAINS.contains(domain)) {
            ChannelType.EMAIL
        } else if (SEO_DOMAINS.find { domain.contains(it) } != null) {
            ChannelType.SEO
        } else if (SOCIAL_DOMAINS.find { domain.contains(it) } != null) {
            ChannelType.SOCIAL
        } else if (MESSAGING_DOMAINS.contains(domain)) {
            ChannelType.MESSAGING
        } else {
            ChannelType.WEB
        }

        return track.copy(channel = channel.name)
    }

    private fun extractDomainName(url: String?): String {
        if (url == null) {
            return ""
        }

        var domainName = url

        var index = domainName.indexOf("://")

        if (index != -1) {
            // keep everything after the "://"
            domainName = domainName.substring(index + 3)
        }

        index = domainName.indexOf('/')

        if (index != -1) {
            // keep everything before the '/'
            domainName = domainName.substring(0, index)
        }

        // check for and remove a preceding 'www'
        // followed by any sequence of characters (non-greedy)
        // followed by a '.'
        // from the beginning of the string
        domainName = domainName.replaceFirst("^www.*?\\.".toRegex(), "")

        return domainName.lowercase(Locale.getDefault())
    }
}
