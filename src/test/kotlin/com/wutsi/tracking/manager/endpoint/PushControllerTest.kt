package com.wutsi.tracking.manager.endpoint

import com.nhaarman.mockitokotlin2.verify
import com.wutsi.tracking.manager.dto.PushTrackRequest
import com.wutsi.tracking.manager.dto.PushTrackResponse
import com.wutsi.tracking.manager.entity.TrackEntity
import com.wutsi.tracking.manager.service.Pipeline
import org.junit.jupiter.api.Test
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.boot.test.mock.mockito.MockBean
import org.springframework.boot.test.web.server.LocalServerPort
import org.springframework.http.HttpStatus
import org.springframework.web.client.RestTemplate
import kotlin.test.assertEquals

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class PushControllerTest {
    @LocalServerPort
    public val port: Int = 0

    private val rest = RestTemplate()

    @MockBean
    private lateinit var pipeline: Pipeline

    private fun url() = "http://localhost:$port/v1/tracks"

    @Test
    fun invoke() {
        // WHEN
        val request = createRequest()
        val response = rest.postForEntity(url(), request, PushTrackResponse::class.java)

        // THEN
        assertEquals(HttpStatus.OK, response.statusCode)

        verify(pipeline).filter(
            TrackEntity(
                time = request.time,
                ua = request.ua,
                correlationId = request.correlationId,
                event = request.event,
                productId = request.productId,
                page = request.page,
                value = request.value,
                long = request.long,
                lat = request.lat,
                ip = request.ip,
                deviceId = request.deviceId,
                accountId = request.accountId,
                merchantId = request.merchantId,
                referer = request.referer,
                url = request.url,
            ),
        )
    }

    private fun createRequest() = PushTrackRequest(
        time = 3333,
        ua = "Mozilla/5.0 (compatible; MSIE 9.0; Windows Phone OS 7.5; Trident/5.0; IEMobile/9.0)",
        correlationId = "123",
        event = "pageview",
        productId = "1234",
        page = "SR",
        value = 100.0,
        long = 111.0,
        lat = 222.0,
        ip = "1.1.2.3",
        deviceId = "sample-device",
        accountId = "333",
        merchantId = "555",
        referer = "https://www.google.ca",
        url = "https://www.wutsi.com/read/123/this-is-nice?utm_source=email&utm_campaign=test&utm_medium=email",
    )
}
