package ch.exasoft.rededge.autocapture

import org.json.JSONObject
import org.junit.Assert.assertEquals
import org.junit.Test

/**
 * Example local unit test, which will execute on the development machine (host).
 *
 * See [testing documentation](http://d.android.com/tools/testing).
 */
class ExampleUnitTest {

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
    fun `assert that image path is parsed right`() {
        val jsonObject = JSONObject(response)
        val imagePathGreen = jsonObject.get("jpeg_cache_path/2") as JSONObject
        assertEquals("/images/tmp1.jpg",imagePathGreen)

    }
}
