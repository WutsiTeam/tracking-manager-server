package com.wutsi.tracking.manager.dao

import com.wutsi.enums.ChannelType
import com.wutsi.enums.DeviceType
import com.wutsi.platform.core.storage.StorageService
import com.wutsi.tracking.manager.Fixtures
import com.wutsi.tracking.manager.entity.TrackEntity
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.beans.factory.annotation.Value
import org.springframework.boot.test.context.SpringBootTest
import java.io.ByteArrayOutputStream
import java.io.File
import java.time.LocalDate

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
internal class TrackRepositoryTest {
    @Autowired
    private lateinit var dao: TrackRepository

    @Autowired
    private lateinit var storageService: StorageService

    @Value("\${wutsi.platform.storage.local.directory}")
    private lateinit var storageDirectory: String

    @BeforeEach
    fun setUp() {
        File("$storageDirectory/${TrackRepository.PATH_PREFIX}").deleteRecursively()
    }

    @Test
    fun save() {
        // WHEN
        val url = dao.save(arrayListOf(createTrack()))

        // THEN
        val out = ByteArrayOutputStream()
        storageService.get(url, out)
        assertEquals(
            """
                time,correlation_id,device_id,account_id,merchant_id,product_id,page,event,value,revenue,ip,long,lat,bot,device_type,channel,source,campaign,referrer,url,ua
                3333,123,sample-device,333,555,1234,SR,pageview,yo,1000,1.1.2.3,111.0,222.0,false,DESKTOP,WEB,facebook,12434554,https://www.wutsi.com/read/123/this-is-nice?utm_source=email&utm_campaign=test&utm_medium=email,https://www.google.ca,Mozilla/5.0 (compatible; MSIE 9.0; Windows Phone OS 7.5; Trident/5.0; IEMobile/9.0)
            """.trimIndent(),
            out.toString().trimIndent(),
        )
    }

    @Test
    fun getURLs() {
        // GIVEN
        val date = LocalDate.of(2020, 10, 2)
        dao.save(listOf(Fixtures.createTrackEntity()), date.plusDays(-1))
        dao.save(listOf(Fixtures.createTrackEntity(), Fixtures.createTrackEntity()), date)
        dao.save(listOf(Fixtures.createTrackEntity()), date)
        dao.save(listOf(Fixtures.createTrackEntity()), date.plusDays(1))

        // WHEN
        val urls = dao.getURLs(date)

        // THEN
        assertEquals(2, urls.size)
    }

    private fun createTrack() = TrackEntity(
        time = 3333,
        ua = "Mozilla/5.0 (compatible; MSIE 9.0; Windows Phone OS 7.5; Trident/5.0; IEMobile/9.0)",
        correlationId = "123",
        bot = false,
        event = "pageview",
        productId = "1234",
        page = "SR",
        value = "yo",
        revenue = 1000,
        long = 111.0,
        lat = 222.0,
        ip = "1.1.2.3",
        deviceId = "sample-device",
        accountId = "333",
        merchantId = "555",
        referrer = "https://www.google.ca",
        url = "https://www.wutsi.com/read/123/this-is-nice?utm_source=email&utm_campaign=test&utm_medium=email",
        deviceType = DeviceType.DESKTOP.name,
        source = "facebook",
        channel = ChannelType.WEB.name,
        campaign = "12434554",
    )
}
