package com.wutsi.tracking.manager.event

import com.wutsi.tracking.manager.dto.PushTrackRequest

data class TrackPayload(val data: PushTrackRequest = PushTrackRequest())
