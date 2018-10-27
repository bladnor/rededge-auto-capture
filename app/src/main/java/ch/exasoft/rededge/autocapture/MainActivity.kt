package ch.exasoft.rededge.autocapture

import android.Manifest
import android.os.Bundle
import android.os.CountDownTimer
import android.os.Environment
import android.os.Handler
import android.support.v7.app.AppCompatActivity
import android.util.Log
import ch.exasoft.rededge.autocapture.logging.FileStorageWriter
import ch.exasoft.rededge.autocapture.logging.LogWriter
import ch.exasoft.rededge.autocapture.logging.LogWriter.writeData
import com.android.volley.RequestQueue
import com.android.volley.toolbox.Volley
import com.intentfilter.androidpermissions.PermissionManager
import kotlinx.android.synthetic.main.activity_main.*
import java.io.File
import java.util.*

const val logTag = "rededge"

class MainActivity : AppCompatActivity() {

    internal var requestQueue: RequestQueue? = null
    private lateinit var handler: Handler
    private lateinit var imageUpdater: Runnable
    private val requests: Requests = Requests(this)


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Check permissions.
        val permissionManager = PermissionManager.getInstance(this)
        val permissions = Arrays.asList(Manifest.permission.INTERNET, Manifest.permission.WRITE_EXTERNAL_STORAGE)
        permissionManager.checkPermissions(permissions, object : PermissionManager.PermissionRequestListener {

            override fun onPermissionGranted() {
                Log.i(logTag, "permission granted for $permissions")
                LogWriter.setLogWriter(FileStorageWriter(getPublicStorageDir()))
            }

            override fun onPermissionDenied() {
                Log.e(logTag, "permission not granted for $permissions")
            }
        })


        setContentView(R.layout.activity_main)
        button_startCapture.setOnClickListener { startCapture() }
        button_checkConnection.setOnClickListener { checkConnection() }
        this.requestQueue = Volley.newRequestQueue(this)

    }


    /**
     * Starts handler to update the latest available preview image from the camera on screen
     */
    internal fun enableImageUpdater() {
        handler = Handler()
        imageUpdater = Runnable {
            writeData("update image preview")
            requestQueue?.add(requests.updateImageWithLatestPreviewRequest())
            handler.postDelayed(imageUpdater, 1000)
        }
        handler.postDelayed(imageUpdater, 1000)

    }

    internal fun disableImageUpdater() {
        handler.removeCallbacks(imageUpdater)
    }


    /**
     * Capture images on every tick and updates number of captures taken on screen. Stops redeges streaming when finished
     */
    private val captureImageTimer = object : CountDownTimer(3000, 1000) {
        var count = 0
        override fun onFinish() {
            count = 0
            writeData("finish capture image")
            requestQueue?.add(requests.streamingRequest(false))
        }

        override fun onTick(millisUntilFinished: Long) {
            ++count
            text_numberOfCaptures.text = count.toString()
            writeData("capture image number $count")
            requestQueue?.add(requests.captureRequest())
        }
    }

    /**
     * Timer which shows countdown on screen and triggers capturing images when finished
     */
    private val waitTimer = object : CountDownTimer(10000, 1000) {
        var count = 0
        override fun onFinish() {
            count = 0
            writeData("starting capture image")
            captureImageTimer.start()
        }

        override fun onTick(millisUntilFinished: Long) {
            ++count
            text_countdown.text = count.toString()
            writeData("tick counter $count")
        }
    }

    /**
     * Starts streaming on the camera
     */
    private fun startCapture() {
        writeData("start capture")
        requestQueue?.add(requests.streamingRequest(true))
        waitTimer.start()
    }

    /**
     * Reads the redege firmware version
     */
    private fun checkConnection() {
        writeData("check connection")
        requestQueue?.add(requests.rededgeFirmwareVersionRequest())
    }

    private fun getPublicStorageDir(): File {
        val file = File(Environment.getExternalStoragePublicDirectory(
                Environment.DIRECTORY_DOCUMENTS), "log-file-rededge")
        file.mkdirs()
        return file
    }
}




