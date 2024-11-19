package com.a_s.musicplayer.ui.activity

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.Slider
import androidx.compose.material3.SliderState
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.a_s.musicplayer.permission.PermissionProvider
import com.a_s.musicplayer.permission.audioPermissions
import com.a_s.musicplayer.ui.activity.test.Audio
import com.a_s.musicplayer.ui.activity.test.MainActivityEvents
import com.a_s.musicplayer.ui.activity.test.MainActivityViewModel
import com.a_s.musicplayer.ui.theme.MusicPlayerTheme
import com.google.accompanist.permissions.ExperimentalPermissionsApi
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.StateFlow

@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    @OptIn(ExperimentalPermissionsApi::class)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            MusicPlayerTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                ) {
                    PermissionProvider(permission = audioPermissions) {
                        val viewModel by viewModels<MainActivityViewModel>()
                        MainScreen(
                            event = viewModel::events,
                            mainScreenState = viewModel.uiState,
                            position = viewModel.position,
                            onPositionChange = viewModel::onPositionChange
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun MainScreen(
    modifier: Modifier = Modifier,
    event : (event : MainActivityEvents) -> Unit,
    mainScreenState : StateFlow<Audio>,
    position: StateFlow<Float>,
    onPositionChange: (Float) -> Unit,
) {
    val uiState by mainScreenState.collectAsStateWithLifecycle()
    val musicPosition by position.collectAsStateWithLifecycle()

    Box(
        modifier = modifier.fillMaxSize()
    ) {
        Column(
            modifier = modifier.fillMaxSize(),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Text(text = uiState.title)
            Row(
                modifier = modifier
                    .fillMaxWidth()
                    .padding(10.dp),
                horizontalArrangement = Arrangement.SpaceAround,
                verticalAlignment = Alignment.CenterVertically
            ) {
                TextButton(onClick = {
                    event(MainActivityEvents.SeekToPrevious)
                }) {
                    Text(text = "previous")
                }
                TextButton(onClick = {
                    event(MainActivityEvents.Play)
                }) {
                    Text(text = "play")
                }
                TextButton(onClick = {
                    event(MainActivityEvents.Pause)
                }) {
                    Text(text = "pause")
                }
                TextButton(onClick = {
                    event(MainActivityEvents.SeekToNext)
                }) {
                    Text(text = "next")
                }
            }
            if (uiState.title != "null") {
                Slider(
                    modifier = modifier
                        .padding(10.dp)
                        .fillMaxWidth(),
                    value = musicPosition,
                    onValueChange = onPositionChange,
                    valueRange = 0f..uiState.duration.toFloat(),

                )
            }
        }
    }
}