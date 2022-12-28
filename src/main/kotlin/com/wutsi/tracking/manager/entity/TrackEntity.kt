package com.wutsi.tracking.manager.entity

data class TrackEntity(
    val time: Long = 0,
    val correlationId: String? = null,
    val deviceId: String? = null,
    val accountId: String? = null,
    val merchantId: String? = null,
    val productId: String? = null,
    val ua: String? = null,
    val bot: Boolean = false,
    val ip: String? = null,
    val lat: Double? = null,
    val long: Double? = null,
    val referer: String? = null,
    val page: String? = null,
    val event: String? = null,
    val value: Double? = null,
    val url: String? = null,
    val source: String? = null,
    val campaign: String? = null,
    val channel: String? = null,
    val deviceType: String? = null,
)
