package com.wutsi.tracking.manager.service.aggregator

import com.wutsi.tracking.manager.dao.TrackRepository
import java.io.InputStream

class Aggregator<K, V>(
    private val dao: TrackRepository,
    private val inputs: InputStreamIterator,
    private val mapper: Mapper<K, V>,
    private val reducer: Reducer<K, V>,
    private val output: OutputWriter<K, V>,
) {
    fun aggregate() {
        // Map
        val kps = mutableListOf<KeyPair<K, V>>()
        while (inputs.hasNext()) {
            kps.addAll(
                map(inputs.next()).filterNotNull(),
            )
        }

        // Reduce
        val groups = kps.groupBy { it.key }
        val results = groups.map { reduce(it.value) }

        // Output
        output.write(results)
    }

    private fun map(input: InputStream): List<KeyPair<K, V>?> =
        dao.read(input, mapper).map { mapper.map(it) }

    private fun reduce(group: List<KeyPair<K, V>>): KeyPair<K, V> =
        group.reduce { acc, keyPair -> reducer.reduce(acc, keyPair) }
}
