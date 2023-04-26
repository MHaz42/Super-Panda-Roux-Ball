package imerir.mhaz42.superpandarouxball

import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.hardware.Sensor
import android.hardware.SensorEvent
import android.hardware.SensorEventListener
import android.hardware.SensorManager
import android.location.Address
import android.location.Geocoder
import android.os.Bundle
import android.os.CountDownTimer
import android.os.SystemClock
import android.os.VibrationEffect
import android.os.VibratorManager
import android.util.Log
import android.view.View
import android.view.Window
import android.view.WindowMetrics
import android.widget.Chronometer
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.core.graphics.toColorInt
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import com.google.android.gms.location.Priority
import kotlinx.serialization.Serializable
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import okhttp3.Call
import okhttp3.Callback
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.Response
import java.io.IOException

class MainActivity : AppCompatActivity(), SensorEventListener {
    private lateinit var address: List<Address>
    private lateinit var geocode: Geocoder
    private lateinit var fusedLocationClient: FusedLocationProviderClient
    private lateinit var sm: SensorManager
    private lateinit var currentDisplay: WindowMetrics
    private lateinit var mainThis: MainActivity
    private lateinit var customView: ImageView
    private lateinit var countDownText: TextView
    private lateinit var bitmap: Bitmap
    private lateinit var canvas: Canvas
    private lateinit var chronometer: Chronometer
    private var dw = 0
    private var dh = 0
    private var x: Float = 275F
    private var y: Float = 375F
    private val client = OkHttpClient()

    @Serializable
    private val map: Map = Map()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        map.addMapElement(MapElement(listOf(200, 300, 350, 900), false))
        map.addMapElement(MapElement(listOf(100, 800, 250, 1950), false))
        map.addMapElement(MapElement(listOf(100, 1800, 900, 1950), false))
        map.addMapElement(MapElement(listOf(900, 600, 1000, 1950), false))
        map.addMapElement(MapElement(listOf(750, 500, 1000, 600), false))
        map.addMapElement(MapElement(listOf(750, 300, 850, 600), false))
        map.addMapElement(MapElement(listOf(700, 200, 900, 400), true))

        this.supportRequestWindowFeature(Window.FEATURE_NO_TITLE)

        mainThis = this
        geocode = Geocoder(this)
        sm = getSystemService(SENSOR_SERVICE) as SensorManager
        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this)
        currentDisplay = windowManager.currentWindowMetrics
        dw = currentDisplay.bounds.width()
        dh = currentDisplay.bounds.height()
        setContentView(R.layout.activity_main)
        countDownText = findViewById(R.id.countDownText)
        customView = findViewById(R.id.customView)
        chronometer = findViewById(R.id.chronoRace)

        Log.v("Map", Json.encodeToString(map))

        if (ActivityCompat.checkSelfPermission(
                this, Manifest.permission.ACCESS_COARSE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            if (ActivityCompat.shouldShowRequestPermissionRationale(
                    this@MainActivity, Manifest.permission.ACCESS_COARSE_LOCATION
                )
            ) {
                ActivityCompat.requestPermissions(
                    this@MainActivity, arrayOf(Manifest.permission.ACCESS_COARSE_LOCATION), 1
                )
            } else {
                ActivityCompat.requestPermissions(
                    this@MainActivity, arrayOf(Manifest.permission.ACCESS_COARSE_LOCATION), 1
                )
            }
        } else {
            fusedLocationClient.getCurrentLocation(Priority.PRIORITY_LOW_POWER, null)
                .addOnSuccessListener { location ->
                    if (location != null) {
                        address = geocode.getFromLocation(
                            location.latitude, location.longitude, 1
                        ) as List<Address>
                        Log.v("Location", address[0].getAddressLine(0).toString())
                    }
                }
        }

        val timer = object : CountDownTimer(5000, 1000) {
            override fun onTick(millisUntilFinished: Long) {
                countDownText.text = (millisUntilFinished / 1000).toInt().toString()
                vibrate()
            }

            override fun onFinish() {
                countDownText.visibility = View.INVISIBLE
                sm.registerListener(
                    mainThis, sm.getDefaultSensor(Sensor.TYPE_ACCELEROMETER), 10000
                )
                chronometer.base = SystemClock.elapsedRealtime()
                chronometer.start()
            }
        }

        // Creating a bitmap with fetched dimensions
        bitmap = Bitmap.createBitmap(dw, dh, Bitmap.Config.ARGB_8888)

        // Storing the canvas on the bitmap
        canvas = Canvas(bitmap)

        for (mapElement in map.getMapElements()) {
            if (mapElement.isFinishLine()) canvas.drawRect(
                mapElement.getRect(),
                Paint().apply { color = "#ff3ddc84".toColorInt() })
            else canvas.drawRect(mapElement.getRect(), Paint().apply { color = Color.GRAY })
        }

        canvas.drawCircle(x, y, 10F, Paint().apply {
            color = Color.RED
        })

        // Setting the bitmap on ImageView
        customView.setImageBitmap(bitmap)
        timer.start()
    }


    override fun onSensorChanged(p0: SensorEvent?) {
        if (p0 != null) {
            canvas.drawColor("#ff121212".toColorInt())
            if (x - p0.values[0] * 1 > 0 && x - p0.values[0] * 1 < dw) x -= p0.values[0] * 1
            if (y + p0.values[1] * 1 > 0 && y + p0.values[1] * 1 < dh) y += p0.values[1] * 1
        }

        var status = false

        for (mapElement in map.getMapElements()) {
            if (mapElement.isFinishLine()) {
                canvas.drawRect(
                    mapElement.getRect(),
                    Paint().apply { color = "#ff3ddc84".toColorInt() })
                if (mapElement.contains(x, y)) {
                    chronometer.stop()
                    countDownText.visibility = View.VISIBLE
                    countDownText.text = getString(R.string.success)
                    vibrate()
                    x = 275F
                    y = 375F
                    sm.unregisterListener(this, sm.getDefaultSensor(Sensor.TYPE_ACCELEROMETER))
                    customView.setOnClickListener { recreate() }
                }
            } else canvas.drawRect(mapElement.getRect(), Paint().apply { color = Color.GRAY })
            if (mapElement.contains(x, y)) status = true

        }

        if (!status) {
            chronometer.stop()
            countDownText.visibility = View.VISIBLE
            countDownText.text = getString(R.string.fail)
            vibrate()
            x = 275F
            y = 375F
            sm.unregisterListener(this, sm.getDefaultSensor(Sensor.TYPE_ACCELEROMETER))
            customView.setOnClickListener { recreate() }

        } else {
            canvas.drawCircle(x, y, 10F, Paint().apply {
                color = Color.RED
            })
        }

        // Setting the bitmap on ImageView
        customView.setImageBitmap(bitmap)

    }

    override fun onAccuracyChanged(p0: Sensor?, p1: Int) {
    }

    private fun vibrate() {
        val vib =
            (getSystemService(Context.VIBRATOR_MANAGER_SERVICE) as VibratorManager).defaultVibrator
        vib.vibrate(VibrationEffect.createOneShot(300, VibrationEffect.DEFAULT_AMPLITUDE))
    }

    override fun onRequestPermissionsResult(
        requestCode: Int, permissions: Array<String>, grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        when (requestCode) {
            1 -> {
                if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    if ((ContextCompat.checkSelfPermission(
                            this@MainActivity, Manifest.permission.ACCESS_COARSE_LOCATION
                        ) == PackageManager.PERMISSION_GRANTED)
                    ) {
                        Toast.makeText(this, "Permission Granted", Toast.LENGTH_SHORT).show()
                    }
                } else {
                    Toast.makeText(this, "Permission Denied", Toast.LENGTH_SHORT).show()
                }
            }
        }
    }

    private fun run(url: String) {
        val request = Request.Builder().url(url).build()

        client.newCall(request).enqueue(object : Callback {
            override fun onFailure(call: Call, e: IOException) {
                Log.v("API Call", e.message ?: "")
            }

            override fun onResponse(call: Call, response: Response) {
                Log.v("API Call", response.body()?.string() ?: "")
            }
        })
    }

}
