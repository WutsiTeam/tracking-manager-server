package com.wutsi.tracking.manager.dto

public data class PushTrackRequest(
    public val time: Long = 0,
    public val correlationId: String? = null,
    public val deviceId: String? = null,
    public val accountId: String? = null,
    public val merchantId: String? = null,
    public val productId: String? = null,
    public val ua: String? = null,
    public val ip: String? = null,
    public val lat: Double? = null,
    public val long: Double? = null,
    public val referer: String? = null,
    public val page: String? = null,
    public val event: String? = null,
    public val `value`: Double? = null,
    public val url: String? = null,
)
