package imerir.mhaz42.superpandarouxball

import android.hardware.Sensor
import android.hardware.SensorEvent
import android.hardware.SensorEventListener
import android.hardware.SensorManager
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.TextView

class MainActivity : AppCompatActivity(), SensorEventListener {
    private lateinit var gyroX: TextView
    private lateinit var gyroY: TextView
    private lateinit var gyroZ: TextView
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        gyroX = findViewById(R.id.textGyroX)
        gyroY = findViewById(R.id.textGyroY)
        gyroZ = findViewById(R.id.textGyroZ)
        val sm: SensorManager = getSystemService(SENSOR_SERVICE) as SensorManager
        sm.registerListener(this, sm.getDefaultSensor(Sensor.TYPE_ACCELEROMETER), SensorManager.SENSOR_DELAY_NORMAL)
    }

    override fun onSensorChanged(p0: SensorEvent?) {
        if (p0 != null) {
            gyroX.text = p0.values[0].toString()
            gyroY.text = p0.values[1].toString()
            gyroZ.text = p0.values[2].toString()


        }
    }

    override fun onAccuracyChanged(p0: Sensor?, p1: Int) {
    }
}