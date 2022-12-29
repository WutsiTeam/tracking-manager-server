package com.wutsi.tracking.manager.service.aggregator.views

import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test

internal class ProductViewReducerTest {
    private val reducer = ProductViewReducer()

    @Test
    fun reduce() {
        val acc = ProductView("1", 10)
        val cur = ProductView("1", 1)
        val result = reducer.reduce(acc, cur)

        assertEquals("1", result.key)
        assertEquals(acc.value + cur.value, result.value)
    }
}
