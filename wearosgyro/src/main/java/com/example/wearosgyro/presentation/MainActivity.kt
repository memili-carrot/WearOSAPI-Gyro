package com.example.wearosgyro

import android.hardware.Sensor
import android.hardware.SensorEvent
import android.hardware.SensorEventListener
import android.hardware.SensorManager
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.*
import androidx.wear.compose.material.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.wear.compose.material.MaterialTheme
import androidx.wear.compose.material.Text

class MainActivity : ComponentActivity(), SensorEventListener {
    private lateinit var sensorManager: SensorManager
    private var gyroSensor: Sensor? = null
    private var gyroX by mutableStateOf(0f)
    private var gyroY by mutableStateOf(0f)
    private var gyroZ by mutableStateOf(0f)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        sensorManager = getSystemService(SENSOR_SERVICE) as SensorManager
        gyroSensor = sensorManager.getDefaultSensor(Sensor.TYPE_GYROSCOPE)

        setContent {
            GyroWearOSApp(gyroX, gyroY, gyroZ)
        }
    }

    override fun onResume() {
        super.onResume()
        gyroSensor?.also { sensor ->
            sensorManager.registerListener(this, sensor, SensorManager.SENSOR_DELAY_UI)
        }
    }

    override fun onPause() {
        super.onPause()
        sensorManager.unregisterListener(this)
    }

    override fun onSensorChanged(event: SensorEvent?) {
        event?.let {
            gyroX = it.values[0]
            gyroY = it.values[1]
            gyroZ = it.values[2]
        }
    }

    override fun onAccuracyChanged(sensor: Sensor?, accuracy: Int) {}
}

@Composable
fun GyroWearOSApp(x: Float, y: Float, z: Float) {
    Column(
        modifier = Modifier.fillMaxSize(),
        verticalArrangement = Arrangement.Center
    ) {
        Text("Gyroscope Data", style = MaterialTheme.typography.body1, modifier = Modifier.padding(16.dp))
        Text("X: $x", modifier = Modifier.padding(8.dp))
        Text("Y: $y", modifier = Modifier.padding(8.dp))
        Text("Z: $z", modifier = Modifier.padding(8.dp))
    }
}