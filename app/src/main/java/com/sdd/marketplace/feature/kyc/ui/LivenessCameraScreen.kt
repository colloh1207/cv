package com.sdd.marketplace.feature.kyc.ui

import android.Manifest
import android.content.Context
import android.net.Uri
import androidx.camera.core.*
import androidx.camera.lifecycle.ProcessCameraProvider
import androidx.camera.view.PreviewView
import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalLifecycleOwner
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.core.content.ContextCompat
import com.google.accompanist.permissions.ExperimentalPermissionsApi
import com.google.accompanist.permissions.isGranted
import com.google.accompanist.permissions.rememberPermissionState
import com.google.accompanist.permissions.shouldShowRationale
import com.sdd.marketplace.core.ui.theme.SddPink
import java.io.File

@OptIn(ExperimentalPermissionsApi::class)
@Composable
fun LivenessCameraScreen(
    onComplete: (Uri) -> Unit,
    onCancel: () -> Unit
) {
    val cameraPermission = rememberPermissionState(Manifest.permission.CAMERA)

    when {
        cameraPermission.status.isGranted -> {
            SelfieCameraContent(onComplete = onComplete, onCancel = onCancel)
        }
        cameraPermission.status.shouldShowRationale -> {
            CameraPermissionRationale(
                onRequest = { cameraPermission.launchPermissionRequest() },
                onCancel = onCancel
            )
        }
        else -> {
            LaunchedEffect(Unit) { cameraPermission.launchPermissionRequest() }
            Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                CircularProgressIndicator(color = SddPink)
            }
        }
    }
}

@Composable
private fun SelfieCameraContent(
    onComplete: (Uri) -> Unit,
    onCancel: () -> Unit
) {
    val context = LocalContext.current
    val lifecycleOwner = LocalLifecycleOwner.current

    var imageCapture by remember { mutableStateOf<ImageCapture?>(null) }
    var previewView by remember { mutableStateOf<PreviewView?>(null) }
    var isCapturing by remember { mutableStateOf(false) }
    var errorMsg by remember { mutableStateOf<String?>(null) }

    LaunchedEffect(previewView) {
        val pv = previewView ?: return@LaunchedEffect
        val cameraProviderFuture = ProcessCameraProvider.getInstance(context)
        cameraProviderFuture.addListener({
            val cameraProvider = cameraProviderFuture.get()
            val preview = Preview.Builder().build().also { it.setSurfaceProvider(pv.surfaceProvider) }
            val capture = ImageCapture.Builder()
                .setCaptureMode(ImageCapture.CAPTURE_MODE_MINIMIZE_LATENCY)
                .build()
            imageCapture = capture
            try {
                cameraProvider.unbindAll()
                cameraProvider.bindToLifecycle(
                    lifecycleOwner,
                    CameraSelector.DEFAULT_FRONT_CAMERA,
                    preview, capture
                )
            } catch (e: Exception) {
                errorMsg = "Camera unavailable: ${e.message}"
            }
        }, ContextCompat.getMainExecutor(context))
    }

    Box(Modifier.fillMaxSize().background(Color.Black)) {
        AndroidView(
            factory = { ctx -> PreviewView(ctx).also { previewView = it } },
            modifier = Modifier.fillMaxSize()
        )

        CircularSelfieGuide(modifier = Modifier.fillMaxSize())

        Column(
            Modifier.fillMaxSize().padding(24.dp),
            verticalArrangement = Arrangement.SpaceBetween
        ) {
            Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                IconButton(onClick = onCancel) {
                    Icon(Icons.Filled.Close, "Cancel", tint = Color.White, modifier = Modifier.size(28.dp))
                }
                Surface(
                    color = Color.Black.copy(alpha = 0.5f),
                    shape = RoundedCornerShape(20.dp)
                ) {
                    Text(
                        "Selfie Verification",
                        color = Color.White,
                        fontWeight = FontWeight.SemiBold,
                        fontSize = 14.sp,
                        modifier = Modifier.padding(horizontal = 14.dp, vertical = 8.dp)
                    )
                }
            }

            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                modifier = Modifier.fillMaxWidth()
            ) {
                errorMsg?.let {
                    Surface(color = MaterialTheme.colorScheme.error, shape = RoundedCornerShape(12.dp)) {
                        Text(it, color = Color.White, modifier = Modifier.padding(12.dp), textAlign = TextAlign.Center)
                    }
                    Spacer(Modifier.height(8.dp))
                    TextButton(onClick = {
                        errorMsg = null
                        isCapturing = false
                    }) { Text("Try Again", color = Color.White) }
                } ?: run {
                    Surface(
                        color = Color.Black.copy(alpha = 0.65f),
                        shape = RoundedCornerShape(14.dp)
                    ) {
                        Column(
                            Modifier.padding(horizontal = 20.dp, vertical = 12.dp),
                            horizontalAlignment = Alignment.CenterHorizontally,
                            verticalArrangement = Arrangement.spacedBy(6.dp)
                        ) {
                            Text("📸", fontSize = 28.sp)
                            Text(
                                "Position your face in the circle",
                                color = Color.White,
                                fontWeight = FontWeight.Bold,
                                fontSize = 16.sp,
                                textAlign = TextAlign.Center
                            )
                            Text(
                                "Make sure your face is well-lit and clearly visible",
                                color = Color.White.copy(alpha = 0.75f),
                                fontSize = 12.sp,
                                textAlign = TextAlign.Center
                            )
                        }
                    }
                }

                Spacer(Modifier.height(20.dp))

                Button(
                    onClick = {
                        if (!isCapturing) {
                            isCapturing = true
                            val ic = imageCapture
                            if (ic == null) {
                                errorMsg = "Camera not ready. Please wait and try again."
                                isCapturing = false
                            } else {
                                captureAndSave(context, ic) { uri ->
                                    if (uri != null) {
                                        onComplete(uri)
                                    } else {
                                        errorMsg = "Could not capture photo. Please try again."
                                        isCapturing = false
                                    }
                                }
                            }
                        }
                    },
                    enabled = !isCapturing,
                    colors = ButtonDefaults.buttonColors(containerColor = SddPink),
                    shape = CircleShape,
                    modifier = Modifier.size(72.dp),
                    contentPadding = PaddingValues(0.dp)
                ) {
                    if (isCapturing) {
                        CircularProgressIndicator(
                            color = Color.White,
                            modifier = Modifier.size(28.dp),
                            strokeWidth = 3.dp
                        )
                    } else {
                        Icon(Icons.Filled.CameraAlt, "Take Selfie", tint = Color.White, modifier = Modifier.size(32.dp))
                    }
                }
                Spacer(Modifier.height(8.dp))
                Text(
                    if (isCapturing) "Processing…" else "Tap to take selfie",
                    color = Color.White.copy(alpha = 0.8f),
                    fontSize = 13.sp
                )
                Spacer(Modifier.height(16.dp))
            }
        }
    }
}

@Composable
private fun CircularSelfieGuide(modifier: Modifier) {
    Box(modifier) {
        Box(
            Modifier
                .size(260.dp)
                .align(Alignment.Center)
                .clip(CircleShape)
                .border(3.dp, Color.White.copy(alpha = 0.85f), CircleShape)
        )
    }
}

private fun captureAndSave(
    context: Context,
    imageCapture: ImageCapture,
    onResult: (Uri?) -> Unit
) {
    val file = File(context.cacheDir, "liveness_selfie_${System.currentTimeMillis()}.jpg")
    val outputOptions = ImageCapture.OutputFileOptions.Builder(file).build()
    imageCapture.takePicture(
        outputOptions,
        ContextCompat.getMainExecutor(context),
        object : ImageCapture.OnImageSavedCallback {
            override fun onImageSaved(output: ImageCapture.OutputFileResults) {
                onResult(Uri.fromFile(file))
            }
            override fun onError(exception: ImageCaptureException) {
                onResult(null)
            }
        }
    )
}

@Composable
private fun CameraPermissionRationale(onRequest: () -> Unit, onCancel: () -> Unit) {
    Box(Modifier.fillMaxSize().background(Color.Black), contentAlignment = Alignment.Center) {
        Column(
            Modifier.padding(32.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            Icon(Icons.Filled.CameraAlt, "Camera", tint = Color.White, modifier = Modifier.size(64.dp))
            Text("Camera Permission Required", color = Color.White, fontWeight = FontWeight.Bold, fontSize = 18.sp, textAlign = TextAlign.Center)
            Text("Camera access is needed to take your verification selfie.", color = Color.White.copy(0.7f), textAlign = TextAlign.Center)
            Button(onClick = onRequest, colors = ButtonDefaults.buttonColors(containerColor = SddPink)) { Text("Grant Permission") }
            TextButton(onClick = onCancel) { Text("Cancel", color = Color.White.copy(0.7f)) }
        }
    }
}
