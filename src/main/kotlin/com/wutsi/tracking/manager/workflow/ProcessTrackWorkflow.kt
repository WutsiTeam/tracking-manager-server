package com.wutsi.tracking.manager.workflow

import com.wutsi.tracking.manager.dto.PushTrackRequest
import com.wutsi.tracking.manager.dto.PushTrackResponse
import com.wutsi.tracking.manager.entity.TrackEntity
import com.wutsi.tracking.manager.service.pipeline.Pipeline
import com.wutsi.workflow.WorkflowContext
import com.wutsi.workflow.engine.Workflow
import com.wutsi.workflow.engine.WorkflowEngine
import com.wutsi.workflow.util.WorkflowIdGenerator
import org.springframework.stereotype.Service
import java.util.UUID
import javax.annotation.PostConstruct

@Service
public class ProcessTrackWorkflow(
    private val pipeline: Pipeline,
    private val workflowEngine: WorkflowEngine,
) : Workflow {
    companion object {
        val ID = WorkflowIdGenerator.generate("marketplace", "track")
    }

    @PostConstruct
    fun init() {
        workflowEngine.register(ID, this)
    }

    override fun execute(context: WorkflowContext) {
        val request = context.input as PushTrackRequest
        pipeline.filter(
            track = TrackEntity(
                time = request.time,
                correlationId = request.correlationId,
                deviceId = request.deviceId,
                accountId = request.accountId,
                merchantId = request.merchantId,
                productId = request.productId,
                ua = request.ua,
                ip = request.ip,
                lat = request.lat,
                long = request.long,
                referrer = request.referrer,
                page = request.page,
                event = request.event,
                value = request.value,
                revenue = request.revenue,
                url = request.url,
                businessId = request.businessId,
            ),
        )

        context.output = PushTrackResponse(
            transactionId = UUID.randomUUID().toString(),
        )
    }
}
