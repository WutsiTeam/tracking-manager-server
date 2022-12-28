package com.wutsi.tracking.manager.event

import com.fasterxml.jackson.databind.ObjectMapper
import com.nhaarman.mockitokotlin2.any
import com.nhaarman.mockitokotlin2.eq
import com.nhaarman.mockitokotlin2.verify
import com.wutsi.event.EventURN
import com.wutsi.event.TrackEventPayload
import com.wutsi.platform.core.stream.Event
import com.wutsi.tracking.manager.dto.PushTrackRequest
import com.wutsi.tracking.manager.workflow.ProcessTrackWorkflow
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.boot.test.mock.mockito.MockBean

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
internal class EventHandlerTest {
    @Autowired
    private lateinit var handler: EventHandler

    @Autowired
    private lateinit var mapper: ObjectMapper

    @MockBean
    private lateinit var workflow: ProcessTrackWorkflow

    @Test
    fun onEvent() {
        val payload = createPayload()
        val event = Event(
            type = EventURN.TRACK.urn,
            payload = mapper.writeValueAsString(payload),
        )
        handler.onEvent(event)

        verify(workflow).execute(
            eq(
                PushTrackRequest(
                    time = payload.time,
                    ua = payload.ua,
                    correlationId = payload.correlationId,
                    event = payload.event,
                    productId = payload.productId,
                    page = payload.page,
                    value = payload.value,
                    long = payload.long,
                    lat = payload.lat,
                    ip = payload.ip,
                    deviceId = payload.deviceId,
                    accountId = payload.accountId,
                    merchantId = payload.merchantId,
                    referer = payload.referer,
                    url = payload.url,
                ),
            ),
            any()
        )
    }

    private fun createPayload() = TrackEventPayload(
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
