package com.wutsi.tracking.manager.service.aggregator.views

import com.wutsi.tracking.manager.service.aggregator.KeyPair
import com.wutsi.tracking.manager.service.aggregator.Reducer

class ProductViewReducer : Reducer<String, Long> {
    override fun reduce(acc: KeyPair<String, Long>, cur: KeyPair<String, Long>): KeyPair<String, Long> =
        KeyPair(acc.key, acc.value + cur.value)
}
