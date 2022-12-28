package com.wutsi.tracking.manager.service

import com.wutsi.tracking.manager.entity.TrackEntity

interface Filter {
    fun filter(track: TrackEntity): TrackEntity
}
