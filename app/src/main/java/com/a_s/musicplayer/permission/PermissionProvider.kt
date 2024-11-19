package com.a_s.musicplayer.permission

import android.app.Activity
import android.content.Intent
import android.net.Uri
import android.provider.Settings
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.SideEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import com.a_s.musicplayer.ui.utils.Dimens.MediumPadding1
import com.a_s.musicplayer.ui.utils.Dimens.SmallPadding1
import com.google.accompanist.permissions.ExperimentalPermissionsApi
import com.google.accompanist.permissions.MultiplePermissionsState
import com.google.accompanist.permissions.rememberMultiplePermissionsState

@OptIn(ExperimentalPermissionsApi::class)
@Composable
fun PermissionProvider(
    permission: Permission,
    permissionState : MultiplePermissionsState = rememberMultiplePermissionsState(permissions = permission.permissions),
    message: @Composable (isPermanentlyDenied: Boolean,onClick: () -> Unit) -> Unit = { isPermanentlyDenied,onClick ->
        PermissionProviderDefaultMessage(
            title = stringResource(id = permission.title),
            body = stringResource(id = permission.getDescription(isPermanentlyDenied)),
            isPermissionDeclined = isPermanentlyDenied,
            onClick = onClick
        )
    },
    content: @Composable () -> Unit
) {
    val activity = LocalContext.current as Activity
    permission.permissionGranted = permissionState.allPermissionsGranted

    if(!permissionState.shouldShowRationale) {
        SideEffect {
            permissionState.launchMultiplePermissionRequest()
        }
    }

    when {
        permissionState.allPermissionsGranted -> content()
        permissionState.shouldShowRationale -> {
            message(true) {
                activity.openSetting()
            }
        }
        else -> {
            message(false) {
                permissionState.launchMultiplePermissionRequest()
            }
        }
    }
}

@Composable
private fun PermissionProviderDefaultMessage(
    modifier: Modifier = Modifier,
    title: String,
    body: String,
    isPermissionDeclined: Boolean,
    onClick: () -> Unit,
) {
    Box(
        modifier = modifier
            .wrapContentSize()
            .padding(MediumPadding1),
        contentAlignment = Alignment.Center
    ) {
        Column(
            modifier = modifier.fillMaxWidth(),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = title,
                style = MaterialTheme.typography.titleMedium,
                textAlign = TextAlign.Center
            )
            Spacer(modifier = Modifier.padding(vertical = SmallPadding1))
            Text(
                text = body,
                style = MaterialTheme.typography.bodyMedium,
                textAlign = TextAlign.Center
            )
            Spacer(modifier = Modifier.padding(vertical = SmallPadding1))
            TextButton(
                onClick = onClick
            ) {
                Text(
                    text = if (isPermissionDeclined) "Open setting" else "Allow"
                )
            }
        }
    }
}

private fun Activity.openSetting() {
    startActivity(
        Intent(
            Settings.ACTION_APPLICATION_DETAILS_SETTINGS,
            Uri.fromParts("package", packageName, null)
        )
    )
}
