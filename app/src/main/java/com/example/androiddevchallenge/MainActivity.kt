/*
 * Copyright 2021 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     https://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.example.androiddevchallenge

import android.media.AudioAttributes
import android.media.SoundPool
import android.os.Bundle
import android.os.CountDownTimer
import androidx.activity.compose.setContent
import androidx.appcompat.app.AppCompatActivity
import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.Orientation
import androidx.compose.foundation.gestures.rememberScrollableState
import androidx.compose.foundation.gestures.scrollable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material.Button
import androidx.compose.material.ButtonDefaults
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.androiddevchallenge.ui.theme.MyTheme

private var countDown: CountDown? = null

private var soundPool: SoundPool? = null
private var soundOne: Int = 0
private var streamId: Int = -1

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val audioAttributes = AudioAttributes.Builder()
            .setUsage(AudioAttributes.USAGE_ALARM)
            .setContentType(AudioAttributes.CONTENT_TYPE_MUSIC)
            .build()
        soundPool = SoundPool.Builder()
            .setAudioAttributes(audioAttributes)
            .setMaxStreams(1)
            .build()
        soundOne = soundPool?.load(this, R.raw.alarm, 1) ?: -1

        setContent {
            MyTheme {
                MyApp()
            }
        }
    }
}

@Composable
fun MyApp() {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(235, 94, 11))
    ) {
        val hour: MutableState<Int> = remember { mutableStateOf(0) }
        val minute: MutableState<Int> = remember { mutableStateOf(0) }
        val second: MutableState<Int> = remember { mutableStateOf(0) }
        val start: MutableState<Boolean> = remember { mutableStateOf(false) }
        val countdownText: MutableState<String> = remember { mutableStateOf("${hour.value}:${minute.value}:${second.value}") }
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .weight(1f)
        ) {
            if (start.value) {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .background(Color(235, 94, 11))
                ) {
                    Text(
                        text = countdownText.value,
                        fontSize = 100.sp,
                        color = Color(248, 241, 241),
                        modifier = Modifier.align(Alignment.Center),
                        textAlign = TextAlign.Center,
                    )
                }
            } else {
                Row {
                    SelectNumber(
                        Modifier
                            .fillMaxHeight()
                            .weight(1f)
                            .background(Color(94, 170, 168)),
                        hour,
                        TimeUnit.HOUR
                    )
                    SelectNumber(
                        Modifier
                            .fillMaxHeight()
                            .weight(1f)
                            .background(Color(163, 210, 202)),
                        minute,
                        TimeUnit.MINUTE
                    )
                    SelectNumber(
                        Modifier
                            .fillMaxHeight()
                            .weight(1f)
                            .background(Color(248, 241, 241)),
                        second,
                        TimeUnit.SECOND
                    )
                }
            }
        }
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(100.dp)
        ) {
            Button(
                modifier = Modifier.fillMaxSize(),
                colors = ButtonDefaults.buttonColors(
                    backgroundColor = Color(235, 94, 11)
                ),
                onClick = {
                    start.value = true
                    val millisInFuture = hour.value * 3600000L + minute.value * 60000L + second.value * 1000L
                    startCountDown(
                        millisInFuture,
                        {
                            countdownText.value = it
                        },
                        {
                            countdownText.value = "FINISH!"
                            playAlarm()
                        }
                    )
                }
            ) {
                Text(
                    text = "START",
                    fontSize = 40.sp,
                    color = Color(248, 241, 241)
                )
            }
            if (start.value) {
                Button(
                    modifier = Modifier.fillMaxSize(),
                    onClick = {
                        stopCountDown()
                        stopAlarm()
                        start.value = false
                    },
                    colors = ButtonDefaults.buttonColors(
                        backgroundColor = Color(235, 94, 11)
                    )
                ) {
                    Text(
                        text = "STOP",
                        fontSize = 40.sp,
                        color = Color(248, 241, 241)
                    )
                }
            }
        }
    }
}

@Composable
fun SelectNumber(modifier: Modifier, num: MutableState<Int>, timeUnit: TimeUnit) {
    Box(
        modifier = modifier
            .scrollable(
                orientation = Orientation.Vertical,
                state = rememberScrollableState { delta ->
                    val inputValue = (-delta * 0.05).toInt()
                    var value = 0.coerceAtLeast(inputValue + num.value)
                    value = timeUnit.max.coerceAtMost(value)
                    num.value = value
                    delta
                }
            ),
        contentAlignment = Alignment.Center
    ) {
        Text(
            modifier = Modifier,
            textAlign = TextAlign.Center,
            text = (num.value).toString(),
            fontSize = 50.sp,
            color = Color(33, 65, 81)
        )
        Text(
            modifier = Modifier.padding(top = 80.dp),
            textAlign = TextAlign.Center,
            text = timeUnit.title,
            fontSize = 20.sp,
            color = Color(33, 65, 81)
        )
    }
}

@Preview("Light Theme", widthDp = 360, heightDp = 640)
@Composable
fun LightPreview() {
    MyTheme {
        MyApp()
    }
}

@Preview("Dark Theme", widthDp = 360, heightDp = 640)
@Composable
fun DarkPreview() {
    MyTheme(darkTheme = true) {
        MyApp()
    }
}

private fun startCountDown(millisInFuture: Long, tick: (p0: String) -> Unit, finish: () -> Unit) {
    countDown = CountDown(
        millisInFuture,
        {
            val hms = String.format(
                "%02d:%02d:%02d", java.util.concurrent.TimeUnit.MILLISECONDS.toHours(it),
                java.util.concurrent.TimeUnit.MILLISECONDS.toMinutes(it) % java.util.concurrent.TimeUnit.HOURS.toMinutes(1),
                java.util.concurrent.TimeUnit.MILLISECONDS.toSeconds(it) % java.util.concurrent.TimeUnit.MINUTES.toSeconds(1)
            )
            tick(hms)
        },
        {
            finish()
        }
    )
    countDown?.start()
}

private fun stopCountDown() {
    countDown?.cancel()
}

private fun playAlarm() {
    streamId = soundPool?.play(soundOne, 1.0f, 1.0f, 0, 10, 1.0f) ?: -1
}

private fun stopAlarm() {
    if (0 <= streamId) {
        soundPool?.stop(streamId)
        streamId = -1
    }
}

enum class TimeUnit(val max: Int, val title: String) {
    HOUR(23, "hour"),
    MINUTE(59, "minute"),
    SECOND(59, "second")
}

private class CountDown(millisInFuture: Long, private val tick: (p0: Long) -> Unit, private val finish: () -> Unit) : CountDownTimer(millisInFuture, 1000) {
    override fun onTick(p0: Long) {
        tick(p0)
    }

    override fun onFinish() {
        finish()
    }
}
