package com.wutsi.tracking.manager.delegate

import com.wutsi.tracking.manager.dto.PushTrackRequest
import com.wutsi.tracking.manager.dto.PushTrackResponse
import com.wutsi.tracking.manager.workflow.ProcessTrackWorkflow
import com.wutsi.workflow.WorkflowContext
import org.springframework.stereotype.Service

@Service
public class PushDelegate(private val workflow: ProcessTrackWorkflow) {
    public fun invoke(request: PushTrackRequest): PushTrackResponse {
        val context = WorkflowContext(input = request)
        workflow.execute(context)

        return context.output as PushTrackResponse
    }
}
