package com.wutsi.tracking.manager.event

import com.fasterxml.jackson.databind.ObjectMapper
import com.wutsi.event.EventURN
import com.wutsi.event.TrackEventPayload
import com.wutsi.platform.core.logging.KVLogger
import com.wutsi.platform.core.stream.Event
import com.wutsi.tracking.manager.dto.PushTrackRequest
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
            val payload = objectMapper.readValue(event.payload, TrackEventPayload::class.java)
            onTrackPushed(payload)
        }
    }

    private fun onTrackPushed(payload: TrackEventPayload) {
        logger.add("payload_correlation_id", payload.correlationId)
        logger.add("payload_account_id", payload.accountId)
        logger.add("payload_merchant_id", payload.merchantId)
        logger.add("payload_product_id", payload.productId)
        logger.add("payload_device_id", payload.deviceId)
        logger.add("payload_page", payload.page)
        logger.add("payload_event", payload.event)
        logger.add("payload_value", payload.value)
        logger.add("payload_revenue", payload.revenue)
        logger.add("payload_referrer", payload.referrer)

        val request = PushTrackRequest(
            time = payload.time,
            event = payload.event,
            referrer = payload.referrer,
            correlationId = payload.correlationId,
            merchantId = payload.merchantId,
            accountId = payload.accountId,
            deviceId = payload.deviceId,
            productId = payload.productId,
            lat = payload.lat,
            long = payload.long,
            value = payload.value,
            revenue = payload.revenue,
            url = payload.url,
            page = payload.page,
            ua = payload.ua,
            ip = payload.ip,
        )
        workflow.execute(request, WorkflowContext())
    }
}
