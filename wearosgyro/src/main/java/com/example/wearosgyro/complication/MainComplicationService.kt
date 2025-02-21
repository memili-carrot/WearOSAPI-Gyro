package com.example.wearosgyro.complication

import android.content.Context
import android.hardware.Sensor
import android.hardware.SensorEvent
import android.hardware.SensorEventListener
import android.hardware.SensorManager
import androidx.wear.watchface.complications.data.ComplicationData
import androidx.wear.watchface.complications.data.ComplicationType
import androidx.wear.watchface.complications.data.PlainComplicationText
import androidx.wear.watchface.complications.data.ShortTextComplicationData
import androidx.wear.watchface.complications.datasource.ComplicationRequest
import androidx.wear.watchface.complications.datasource.SuspendingComplicationDataSourceService
import java.util.concurrent.atomic.AtomicReference

/**
 * Complication service that displays real-time gyroscope data.
 */
class MainComplicationService : SuspendingComplicationDataSourceService(), SensorEventListener {

    private lateinit var sensorManager: SensorManager
    private var gyroSensor: Sensor? = null
    private val gyroValue = AtomicReference("0.0")

    override fun onCreate() {
        super.onCreate()
        sensorManager = getSystemService(Context.SENSOR_SERVICE) as SensorManager
        gyroSensor = sensorManager.getDefaultSensor(Sensor.TYPE_GYROSCOPE)
        gyroSensor?.let {
            sensorManager.registerListener(this, it, SensorManager.SENSOR_DELAY_UI)
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        sensorManager.unregisterListener(this)
    }

    override fun getPreviewData(type: ComplicationType): ComplicationData? {
        if (type != ComplicationType.SHORT_TEXT) {
            return null
        }
        return createComplicationData("0.0", "Gyro Data")
    }

    override suspend fun onComplicationRequest(request: ComplicationRequest): ComplicationData {
        val gyroData = getGyroData()
        return createComplicationData(gyroData, "Current Gyro Value")
    }

    private fun createComplicationData(text: String, contentDescription: String) =
        ShortTextComplicationData.Builder(
            text = PlainComplicationText.Builder(text).build(),
            contentDescription = PlainComplicationText.Builder(contentDescription).build()
        ).build()

    private fun getGyroData(): String {
        return gyroValue.get()
    }

    override fun onSensorChanged(event: SensorEvent?) {
        event?.let {
            val value = "%.2f".format(it.values[0]) // 소수점 2자리까지 표시
            gyroValue.set(value)
        }
    }

    override fun onAccuracyChanged(sensor: Sensor?, accuracy: Int) {}
}