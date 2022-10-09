/* While this template provides a good starting point for using Wear Compose, you can always
 * take a look at https://github.com/android/wear-os-samples/tree/main/ComposeStarter and
 * https://github.com/android/wear-os-samples/tree/main/ComposeAdvanced to find the most up to date
 * changes to the libraries and their usages.
 */

package com.example.myapplication.presentation

import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
import android.hardware.Sensor
import android.hardware.SensorEvent
import android.hardware.SensorEventListener
import android.hardware.SensorManager
import android.os.Bundle
import android.os.SystemClock
import android.util.Log
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Face
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.tooling.preview.Devices
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.fragment.app.FragmentActivity
import androidx.wear.ambient.AmbientModeSupport
import androidx.wear.compose.material.*
import com.example.myapplication.presentation.theme.MyApplicationTheme
import java.text.SimpleDateFormat
import androidx.compose.material.icons.rounded.Person
import androidx.compose.ui.text.font.FontStyle

class MainActivity : FragmentActivity(), AmbientModeSupport
.AmbientCallbackProvider {
    private val STEP_SENSOR_LISTENER = 20
    private lateinit var ambientController: AmbientModeSupport.AmbientController


    //TODO ambient mode support

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setupPermissions()
        ambientController = AmbientModeSupport.attach(this)

        setContent {
            WearApp("Android")
        }
    }

    private fun setupPermissions() {
        val permission = ContextCompat.checkSelfPermission(
            this, Manifest.permission.ACTIVITY_RECOGNITION
        )
        if (permission != PackageManager.PERMISSION_GRANTED) {
            Log.i("permisos", "Permiso Denegado")
            makeRequest()
        }
    }

    private fun makeRequest() {
        ActivityCompat.requestPermissions(
            this, arrayOf(
                Manifest.permission.ACTIVITY_RECOGNITION,
                Manifest.permission.BODY_SENSORS
            ),
            STEP_SENSOR_LISTENER
        )
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(
            requestCode,
            permissions, grantResults
        )/////////
        when (requestCode) {
            STEP_SENSOR_LISTENER -> {
                if (grantResults.isEmpty() || grantResults[0] !=
                    PackageManager.PERMISSION_GRANTED
                ) {
                    Log.i("DENIED", "Permisos NO")
                } else {
                    Log.i("GRANTED", "Permisos OK")
                }
            }
        }
    }

    override fun getAmbientCallback(): AmbientModeSupport.AmbientCallback =
        MyAmbientCallback()
}


@Composable
fun StepCounterSensor() {
    val ctx = LocalContext.current
    val sensorManager: SensorManager =
        ctx.getSystemService(Context.SENSOR_SERVICE) as SensorManager
    val StepCountSensor: Sensor = sensorManager.getDefaultSensor(
        Sensor.TYPE_STEP_COUNTER
    )
    var stepCounterStatus = remember {
        mutableStateOf("")
    }

    val stepCounterSensorListener = object : SensorEventListener {
        override fun onSensorChanged(p0: SensorEvent?) {

            p0 ?: return
            p0.values.firstOrNull()?.let {
                Log.d("Steps", "Step Count: $it")
                stepCounterStatus.value = p0.values[0].toString()
            }
            //Verificar tiempo de booteo del dispositivo
            val lastBoot = SystemClock.elapsedRealtime()
            val SensorEventTime = p0.timestamp
            val SensorEventTimeInMillis = SensorEventTime / 1000_000

            val actualSensorTime = lastBoot + SensorEventTimeInMillis
            val displayDateStr = SimpleDateFormat("yyyy-MM-dd HH:mm:ss")
                .format(actualSensorTime)
            Log.i("TIME", "Sensor Event activado a las : $displayDateStr")

        }

        override fun onAccuracyChanged(p0: Sensor?, p1: Int) {
            println("onAccuracyChanged: Sensor : $p0; accuracy: $p1 ")
        }

    }

    sensorManager.registerListener(
        stepCounterSensorListener,
        StepCountSensor,
        SensorManager.SENSOR_DELAY_NORMAL
    )
    //getSensorData().toString()
    Column(
        modifier = Modifier
            .fillMaxSize()
            .fillMaxHeight()
            .fillMaxWidth()
            .padding(5.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Text(text = getSensorData())
        Text(text = stepCounterStatus.value)

    }


}

//////////////////////////

@Composable
fun getSensorData(): String {
val ctx = LocalContext.current
    val sensorManager: SensorManager =
        ctx.getSystemService(Context.SENSOR_SERVICE) as SensorManager
    val HearRateSensor: Sensor = sensorManager.getDefaultSensor(
        Sensor.TYPE_HEART_RATE
    )
    var HrStatus = remember {
        mutableStateOf("")
    }

    val HeartRateSensorListener = object : SensorEventListener {
        override fun onSensorChanged(p0: SensorEvent?) {

            p0 ?: return
            p0.values.firstOrNull()?.let {
                Log.d("Pulso", "Pulso: $it")
                HrStatus.value = p0.values[0].toString()
            }
            //Verificar tiempo de booteo del dispositivo
            val lastBoot = SystemClock.elapsedRealtime()
            val SensorEventTime = p0.timestamp
            val SensorEventTimeInMillis = SensorEventTime / 1000_000

            val actualSensorTime = lastBoot + SensorEventTimeInMillis
            val displayDateStr = SimpleDateFormat("yyyy-MM-dd HH:mm:ss")
                .format(actualSensorTime)
            Log.i("TIME", "Sensor Event activado a las : $displayDateStr")

        }

        override fun onAccuracyChanged(p0: Sensor?, p1: Int) {
            println("onAccuracyChanged: Sensor : $p0; accuracy: $p1 ")
        }

    }


    sensorManager.registerListener(
        HeartRateSensorListener,
        HearRateSensor,
        SensorManager.SENSOR_DELAY_NORMAL
    )

    return HrStatus.value
}


@Composable
fun HeartStatusSensor() {
    val ctx = LocalContext.current
    val sensorManager: SensorManager =
        ctx.getSystemService(Context.SENSOR_SERVICE) as SensorManager
    val HrSensor: Sensor = sensorManager.getDefaultSensor(
        Sensor.TYPE_HEART_RATE
    )
    var SensorStatus = remember {
        mutableStateOf("")
    }

    val HrListener = object : SensorEventListener {
        override fun onSensorChanged(p0: SensorEvent?) {

            p0 ?: return
            p0.values.firstOrNull()?.let {
                Log.d("HEAR BEAT", "Heart Beat: $it")
                SensorStatus.value = p0.values[0].toString()
            }
            //Verificar tiempo de booteo del dispositivo

        }

        override fun onAccuracyChanged(p0: Sensor?, p1: Int) {
            println("onAccuracyChangedHR: Sensor : $p0; accuracy: $p1 ")
        }

    }

    sensorManager.registerListener(
        HrListener,
        HrSensor,
        SensorManager.SENSOR_DELAY_NORMAL
    )

    Column(
        modifier = Modifier
            .fillMaxSize()
            .fillMaxHeight()
            .fillMaxWidth()
            .padding(5.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Text(text = SensorStatus.value)

    }


}

//////////////////////////
private class MyAmbientCallback : AmbientModeSupport.AmbientCallback() {
    override fun onEnterAmbient(ambientDetails: Bundle?) {
        super.onEnterAmbient(ambientDetails)

    }

    override fun onExitAmbient() {
        super.onExitAmbient()
    }

    override fun onUpdateAmbient() {
        super.onUpdateAmbient()
    }
}

@OptIn(ExperimentalWearMaterialApi::class)
@Composable
fun WearApp(greetingName: String) {
    MyApplicationTheme {

        val listState = rememberScalingLazyListState()
        Scaffold(
            timeText = {
                if (!listState.isScrollInProgress) {
                    TimeText()
                }
            },
            vignette = {
                Vignette(vignettePosition = VignettePosition.TopAndBottom)
            },
            positionIndicator =
            {
                PositionIndicator(scalingLazyListState = listState)
            }

        ) {

            ScalingLazyColumn(
                modifier = Modifier.fillMaxSize(),
                autoCentering = true,
                verticalArrangement = Arrangement.Center,
                horizontalAlignment = Alignment.CenterHorizontally,
                state = listState

            ) {
                item {
                    Icon(
                        imageVector = Icons.Rounded.Face, contentDescription = "Hear Rate",
                        Modifier
                            .size(70.dp)
                            .wrapContentSize(
                                align = Alignment.Center
                            ), tint = MaterialTheme.colors.primary
                    )
                }

                item { Text(text = "Heart Rate", color = androidx.compose.ui.graphics.Color.Blue, fontStyle = FontStyle.Italic)  }
                //item { HeartStatusSensor() }
                item { StepCounterSensor() }
                item { Text(text = "Steps", color = androidx.compose.ui.graphics.Color.Red, fontStyle = FontStyle.Italic) }



                item {
                    Icon(
                        imageVector = Icons.Rounded.Person, contentDescription = "Steps",

                        Modifier
                            .size(70.dp)
                            .wrapContentSize(
                                align = Alignment.Center,
                            ), tint = MaterialTheme.colors.secondaryVariant
                    )
                }


            }

        }
    }
}


@Preview(device = Devices.WEAR_OS_SMALL_ROUND, showSystemUi = true)
@Composable
fun DefaultPreview() {
    WearApp("Preview Android")
}