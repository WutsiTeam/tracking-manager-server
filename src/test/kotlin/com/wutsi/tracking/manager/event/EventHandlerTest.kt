package com.wutsi.tracking.manager.event

import com.fasterxml.jackson.databind.ObjectMapper
import com.nhaarman.mockitokotlin2.verify
import com.wutsi.event.EventURN
import com.wutsi.platform.core.stream.Event
import com.wutsi.tracking.manager.Fixtures
import com.wutsi.tracking.manager.dto.PushTrackRequest
import com.wutsi.tracking.manager.workflow.ProcessTrackWorkflow
import com.wutsi.workflow.WorkflowContext
import com.wutsi.workflow.engine.WorkflowEngine
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
    private lateinit var workflowEngine: WorkflowEngine

    @Test
    fun onEvent() {
        val payload = Fixtures.createTrackEventPayload()
        val event = Event(
            type = EventURN.TRACK.urn,
            payload = mapper.writeValueAsString(payload),
        )
        handler.onEvent(event)

        verify(workflowEngine).execute(
            ProcessTrackWorkflow.ID,
            WorkflowContext(
                input = PushTrackRequest(
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
                    referrer = payload.referrer,
                    url = payload.url,
                    revenue = payload.revenue,
                ),
            ),
        )
    }
}
