package com.wutsi.tracking.manager.event

import com.fasterxml.jackson.databind.ObjectMapper
import com.nhaarman.mockitokotlin2.any
import com.nhaarman.mockitokotlin2.eq
import com.nhaarman.mockitokotlin2.verify
import com.wutsi.event.EventURN
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
        val data = PushTrackRequest()
        val event = Event(
            type = EventURN.TRACK.urn,
            payload = mapper.writeValueAsString(data),
        )
        handler.onEvent(event)

        verify(workflow).execute(eq(data), any())
    }
}
