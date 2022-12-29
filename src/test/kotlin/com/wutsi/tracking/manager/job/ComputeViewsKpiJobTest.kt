package com.wutsi.tracking.manager.job

import com.amazonaws.util.IOUtils
import com.wutsi.tracking.manager.Fixtures
import com.wutsi.tracking.manager.dao.TrackRepository
import com.wutsi.tracking.manager.service.aggregator.views.ProductViewMapper
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.beans.factory.annotation.Value
import org.springframework.boot.test.context.SpringBootTest
import java.io.File
import java.io.FileInputStream
import java.time.LocalDate
import java.time.OffsetDateTime
import java.time.ZoneOffset

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
internal class ComputeViewsKpiJobTest {
    @Value("\${wutsi.platform.storage.local.directory}")
    private lateinit var storageDir: String

    @Autowired
    private lateinit var dao: TrackRepository

    @Autowired
    private lateinit var job: ComputeViewsKpiJob

    @BeforeEach
    fun setUp() {
        File("$storageDir/${TrackRepository.PATH_PREFIX}").deleteRecursively()
        File("$storageDir/kpi").deleteRecursively()
    }

    @Test
    fun run() {
        // GIVEN
        val today = LocalDate.now()
        dao.save(
            listOf(
                Fixtures.createTrackEntity(
                    page = ProductViewMapper.PAGE,
                    event = ProductViewMapper.EVENT,
                    productId = "111",
                    time = today.atStartOfDay().toEpochSecond(ZoneOffset.UTC) * 1000,
                ),
                Fixtures.createTrackEntity(
                    page = ProductViewMapper.PAGE,
                    event = ProductViewMapper.EVENT,
                    productId = "222",
                    time = today.atStartOfDay().toEpochSecond(ZoneOffset.UTC) * 1000,
                ),
                Fixtures.createTrackEntity(page = "error", time = OffsetDateTime.now().toEpochSecond() * 1000),
                Fixtures.createTrackEntity(
                    page = ProductViewMapper.PAGE,
                    event = ProductViewMapper.EVENT,
                    productId = "333",
                    time = today.plusDays(1).atStartOfDay().toEpochSecond(ZoneOffset.UTC) * 1000,
                ),
            ),
        )
        dao.save(
            listOf(
                Fixtures.createTrackEntity(
                    page = ProductViewMapper.PAGE,
                    event = ProductViewMapper.EVENT,
                    productId = "111",
                    time = today.atStartOfDay().toEpochSecond(ZoneOffset.UTC) * 1000,
                ),
                Fixtures.createTrackEntity(
                    page = ProductViewMapper.PAGE,
                    event = ProductViewMapper.EVENT,
                    productId = "111",
                    time = today.minusDays(1).atStartOfDay().toEpochSecond(ZoneOffset.UTC) * 1000,
                ),
            ),
            today.minusDays(1),
        )

        // WHEN
        job.run()

        // THEN
        val file = File("$storageDir/kpi/${today.year}/${today.monthValue}/${today.dayOfMonth}/views.csv")
        assertTrue(file.exists())
        assertEquals(
            """
                product_id,views
                111,2
                222,1
            """.trimIndent(),
            IOUtils.toString(FileInputStream(file)).trimIndent(),
        )
    }
}
