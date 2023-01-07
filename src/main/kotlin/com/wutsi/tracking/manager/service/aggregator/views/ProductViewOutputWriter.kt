package com.wutsi.tracking.manager.service.aggregator.views

import com.wutsi.platform.core.storage.StorageService
import com.wutsi.tracking.manager.service.aggregator.KeyPair
import com.wutsi.tracking.manager.service.aggregator.OutputWriter

class ProductViewOutputWriter(path: String, storage: StorageService) :
    OutputWriter<String, Long>(path, storage) {
    override fun headers(): Array<String> =
        arrayOf(
            "product_id",
            "total_views",
        )

    override fun values(pair: KeyPair<String, Long>): Array<Any> =
        arrayOf(
            pair.key,
            pair.value,
        )
}
