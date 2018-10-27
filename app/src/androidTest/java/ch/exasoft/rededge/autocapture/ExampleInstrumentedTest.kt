package ch.exasoft.rededge.autocapture

import android.support.test.InstrumentationRegistry
import android.support.test.runner.AndroidJUnit4
import org.json.JSONObject
import org.junit.Assert.assertEquals
import org.junit.Test
import org.junit.runner.RunWith

/**
 * Instrumented test, which will execute on an Android device.
 *
 * See [testing documentation](http://d.android.com/tools/testing).
 */
@RunWith(AndroidJUnit4::class)
class ExampleInstrumentedTest {
    private val response =
            """
        {
          "id": "bfS90eCacd0bWEE8LNPH",
          "jpeg_cache_path": {
            "1": "/images/tmp0.jpg",
            "2": "/images/tmp1.jpg",
            "3": "/images/tmp2.jpg",
            "4": "/images/tmp3.jpg",
            "5": "/images/tmp4.jpg"
          },
          "status": "complete",
          "time": "2015-10-18T00:16:17.399999Z"
        }
        """


    @Test
    fun useAppContext() {
        // Context of the app under test.
        val appContext = InstrumentationRegistry.getTargetContext()
        assertEquals("ch.exasoft.rededge.autocapture", appContext.packageName)
    }

    @Test
    fun assertThatImagePathIsParsedRight() {
        val jsonObject = JSONObject(response)
        val imagePathGreen = jsonObject.get("jpeg_cache_path") as JSONObject
        assertEquals("/images/tmp1.jpg", imagePathGreen.get("2"))

    }

}
