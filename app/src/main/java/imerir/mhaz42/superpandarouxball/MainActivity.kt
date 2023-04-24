package imerir.mhaz42.superpandarouxball

import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.hardware.Sensor
import android.hardware.SensorEvent
import android.hardware.SensorEventListener
import android.hardware.SensorManager
import android.os.Bundle
import android.view.WindowMetrics
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.core.graphics.toColorInt

class MainActivity : AppCompatActivity(), SensorEventListener {
    private lateinit var currentDisplay: WindowMetrics
    private var dw = 0
    private var dh = 0
    private lateinit var textX: TextView
    private lateinit var textY: TextView
    private lateinit var customView: ImageView
    private lateinit var bitmap: Bitmap
    private lateinit var canvas: Canvas
    private var x: Float = 0F
    private var y: Float = 0F

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        currentDisplay = windowManager.currentWindowMetrics
        dw = currentDisplay.bounds.width()
        dh = currentDisplay.bounds.height()
        setContentView(R.layout.activity_main)
        textX = findViewById(R.id.textX)
        textY = findViewById(R.id.textY)
        customView = findViewById(R.id.customView)

        val sm: SensorManager = getSystemService(SENSOR_SERVICE) as SensorManager
        sm.registerListener(this, sm.getDefaultSensor(Sensor.TYPE_ACCELEROMETER), 10000)

        // Creating a bitmap with fetched dimensions
        bitmap = Bitmap.createBitmap(dw, dh, Bitmap.Config.ARGB_8888)

        // Storing the canvas on the bitmap
        canvas = Canvas(bitmap)

        // Setting the bitmap on ImageView
        customView.setImageBitmap(bitmap)
    }

    override fun onSensorChanged(p0: SensorEvent?) {
        if (p0 != null) {
            canvas.drawColor("#110f12".toColorInt())
            if (x - p0.values[0] * 1 > 0 && x - p0.values[0] * 1 < dw)
                x -= p0.values[0] * 1
            if (y + p0.values[1] * 1 > 0 && y + p0.values[1] * 1 < dh)
                y += p0.values[1] * 1
        }
        textX.text = x.toString()
        textY.text = y.toString()
        canvas.drawCircle(x, y,
            10F, Paint().apply {
                color = Color.RED
            })
        // Setting the bitmap on ImageView
        customView.setImageBitmap(bitmap)

    }

    override fun onAccuracyChanged(p0: Sensor?, p1: Int) {
    }
}