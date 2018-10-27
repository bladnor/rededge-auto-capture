package ch.exasoft.rededge.autocapture

import android.annotation.SuppressLint
import android.graphics.Bitmap
import android.util.Log
import android.widget.ImageView
import ch.exasoft.rededge.autocapture.logging.LogWriter
import com.android.volley.Request
import com.android.volley.Response
import com.android.volley.toolbox.ImageRequest
import com.android.volley.toolbox.JsonObjectRequest
import com.android.volley.toolbox.StringRequest
import kotlinx.android.synthetic.main.activity_main.*
import org.json.JSONObject
import java.time.LocalDateTime


class Requests(private val activity: MainActivity) {

    private val baseUrl = "http://192.168.10.254"

    private val captureUrl = "$baseUrl/capture?preview=true&anti_sat=true"
    private val configUrl = "$baseUrl/config"
    private val previewImageUrl = "$baseUrl/jpeg_url"
    private val versionUrl = "$baseUrl/version"


    private var errorListener: Response.ErrorListener = Response.ErrorListener { error ->
        val message = "data sent not successfully: $error"
        Log.i(logTag, message)
        LogWriter.writeData(message)
    }


    @SuppressLint("SetTextI18n")
    fun rededgeFirmwareVersionRequest() = StringRequest(Request.Method.GET, versionUrl, Response.Listener<String> { response ->
        LogWriter.writeData("response from $versionUrl request: $response")
        activity.text_rededgeVersionInfo.text = response + LocalDateTime.now()
    }, errorListener)


    fun captureRequest() = StringRequest(Request.Method.POST, captureUrl, Response.Listener<String> { response ->
        LogWriter.writeData("response from $captureUrl request: $response")
//            // So far we do not use the response
//            val jsonObject = JSONObject(response)
//            val imagePathGreen = jsonObject.get("jpeg_cache_path") as JSONObject
//            this@MainActivity.requestQueue?.add(downloadImageRequest(imagePathGreen.getString("2")))
    }, errorListener
    )

    fun updateImageWithLatestPreviewRequest() = StringRequest(Request.Method.GET, previewImageUrl, Response.Listener<String> { response ->
        LogWriter.writeData("response from $previewImageUrl request: $response")
        val jsonObject = JSONObject(response)
        val previewImageUrl = jsonObject.get("jpeg_url") as String
        activity.requestQueue?.add(downloadImageRequest(previewImageUrl))
    }, errorListener
    )

    fun streamingRequest(enable: Boolean) = JsonObjectRequest(Request.Method.POST, configUrl, JSONObject().put("streaming_enable", enable), Response.Listener<JSONObject> { response ->
        LogWriter.writeData("response from $configUrl request: $response")
        when (enable) {
            true -> activity.enableImageUpdater()
            else -> activity.disableImageUpdater()
        }
    }, errorListener
    )

    /**
     * Download captured image from rededge
     */
    private fun downloadImageRequest(imagePath: String) = ImageRequest(
            "$baseUrl/$imagePath"
            , Response.Listener<Bitmap> { response ->
        val message = "bitmap received"
        Log.i(logTag, message)
        LogWriter.writeData(message)
        activity.image_rededgePreview.setImageBitmap(response)
    }
            , 0
            , 0, ImageView.ScaleType.CENTER_CROP, Bitmap.Config.RGB_565
            ,
            Response.ErrorListener { error ->
                val message = "data sent not successfully: $error"
                Log.i(logTag, message)
                LogWriter.writeData(message)
            }
    )

}