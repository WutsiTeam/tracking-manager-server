package com.wutsi.tracking.manager.config

import com.wutsi.tracking.manager.dao.TrackRepository
import com.wutsi.tracking.manager.service.Pipeline
import com.wutsi.tracking.manager.service.filter.BotFilter
import com.wutsi.tracking.manager.service.filter.CampaignFilter
import com.wutsi.tracking.manager.service.filter.ChannelFilter
import com.wutsi.tracking.manager.service.filter.DeviceTypeFilter
import com.wutsi.tracking.manager.service.filter.PersisterFilter
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration

@Configuration
class PipelineConfiguration(
    private val dao: TrackRepository,
) {
    @Bean
    fun pipeline() = Pipeline(
        arrayListOf(
            BotFilter(),
            DeviceTypeFilter(),
            CampaignFilter(),
            ChannelFilter(),

            // IMPORTANT: Always the last!!!
            persisterFilter(),
        ),
    )

    @Bean(destroyMethod = "destroy")
    fun persisterFilter() = PersisterFilter(dao, 10000)
}
