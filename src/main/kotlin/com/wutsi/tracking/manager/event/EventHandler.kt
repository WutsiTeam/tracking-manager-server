package com.wutsi.tracking.manager.event

import com.fasterxml.jackson.databind.ObjectMapper
import com.wutsi.event.EventURN
import com.wutsi.platform.core.logging.KVLogger
import com.wutsi.platform.core.stream.Event
import com.wutsi.tracking.manager.workflow.ProcessTrackWorkflow
import com.wutsi.workflow.WorkflowContext
import org.springframework.context.event.EventListener
import org.springframework.stereotype.Service

@Service
class EventHandler(
    private val objectMapper: ObjectMapper,
    private val workflow: ProcessTrackWorkflow,
    private val logger: KVLogger,
) {
    @EventListener
    fun onEvent(event: Event) {
        if (EventURN.TRACK.urn == event.type) {
            val payload = objectMapper.readValue(event.payload, TrackPayload::class.java)
            onTrackPushed(payload)
        }
    }

    private fun onTrackPushed(payload: TrackPayload) {
        logger.add("payload_correlation_id", payload.data.correlationId)
        logger.add("payload_account_id", payload.data.accountId)
        logger.add("payload_merchant_id", payload.data.merchantId)
        logger.add("payload_product_id", payload.data.productId)
        logger.add("payload_page", payload.data.page)
        logger.add("payload_event", payload.data.event)
        logger.add("payload_value", payload.data.value)
        logger.add("payload_device_id", payload.data.deviceId)

        workflow.execute(payload.data, WorkflowContext())
    }
}
