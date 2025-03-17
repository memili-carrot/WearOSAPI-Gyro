package com.example.wearosgyro.presentation

import android.hardware.Sensor
import android.hardware.SensorEvent
import android.hardware.SensorEventListener
import android.hardware.SensorManager
import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.*
import androidx.wear.compose.material.MaterialTheme
import androidx.wear.compose.material.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import org.json.JSONArray
import org.json.JSONObject
import java.io.File
import java.io.FileWriter
import java.io.IOException

class MainActivity : ComponentActivity(), SensorEventListener {
    private lateinit var sensorManager: SensorManager
    private var gyroSensor: Sensor? = null
    private var gyroX by mutableStateOf(0f)
    private var gyroY by mutableStateOf(0f)
    private var gyroZ by mutableStateOf(0f)
    private val sensorDataList = JSONArray() // JSON 배열 선언
    private val coroutineScope = CoroutineScope(Dispatchers.IO) // 비동기 파일 저장을 위한 코루틴 스코프

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
            try {
                sensorManager.registerListener(this, sensor, SensorManager.SENSOR_DELAY_UI)
            } catch (e: Exception) {
                Log.e("SensorError", "센서 등록 중 오류 발생: ${e.message}")
            }
        } ?: Log.e("SensorError", "자이로스코프 센서가 지원되지 않습니다.")
    }

    override fun onPause() {
        super.onPause()
        sensorManager.unregisterListener(this)
    }

    override fun onSensorChanged(event: SensorEvent?) {
        event?.let {
            try {
                gyroX = it.values[0]
                gyroY = it.values[1]
                gyroZ = it.values[2]

                // JSON 데이터 생성
                val sensorData = JSONObject().apply {
                    put("timestamp", System.currentTimeMillis())
                    put("sensor_name", "Gyroscope Sensor")
                    put("x", gyroX)
                    put("y", gyroY)
                    put("z", gyroZ)
                }
                // JSON 배열에 추가
                sensorDataList.put(sensorData)

                // JSON 파일로 저장 (비동기 처리)
                coroutineScope.launch {
                    saveJsonToFile(sensorDataList)
                }
            } catch (e: Exception) {
                Log.e("SensorError", "센서 데이터 처리 중 오류 발생: ${e.message}")
            }
        }
    }

    override fun onAccuracyChanged(sensor: Sensor?, accuracy: Int) {}

    private fun saveJsonToFile(jsonArray: JSONArray) {
        val fileName = "Gyro_sensor_data.json"
        val file = File(getExternalFilesDir(null), fileName) // 내부 저장소 대신 외부 저장소 사용

        try {
            FileWriter(file).use { writer ->
                writer.write(jsonArray.toString(4))
            }
            Log.d("SensorData", "JSON 파일 저장 완료: ${file.absolutePath}")
        } catch (e: IOException) {
            Log.e("FileError", "JSON 저장 실패: ${e.message}")
        }
    }
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