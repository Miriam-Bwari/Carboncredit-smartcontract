package dev.korryr.shambaguard.sharedComposables

import android.Manifest
import android.annotation.SuppressLint
import android.location.Geocoder
import android.os.Build
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.Undo
import androidx.compose.material.icons.filled.GpsFixed
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.google.accompanist.permissions.ExperimentalPermissionsApi
import com.google.accompanist.permissions.isGranted
import com.google.accompanist.permissions.rememberPermissionState
import com.google.android.gms.location.LocationServices
import com.google.android.gms.location.Priority
import com.google.android.gms.tasks.CancellationTokenSource
import com.google.android.gms.maps.model.CameraPosition
import com.google.android.gms.maps.model.Dash
import com.google.android.gms.maps.model.Gap
import com.google.android.gms.maps.model.LatLng
import com.google.maps.android.compose.GoogleMap
import com.google.maps.android.compose.MapProperties
import com.google.maps.android.compose.MapUiSettings
import com.google.maps.android.compose.Marker
import com.google.maps.android.compose.MarkerState
import com.google.maps.android.compose.Polygon
import com.google.maps.android.compose.Polyline
import com.google.maps.android.compose.rememberCameraPositionState
import dev.korryr.shambaguard.R
import kotlinx.coroutines.suspendCancellableCoroutine
import kotlin.coroutines.resume

// Default zoom — no static center; camera stays at 0,0 until GPS resolves
private const val DEFAULT_ZOOM = 15f

// Brand green used on the polygon overlay
private val PolygonGreen = Color(0xFF2E9647)
private val PolygonFill = Color(0x332E9647)

@OptIn(ExperimentalPermissionsApi::class)
@SuppressLint("MissingPermission")
@Composable
fun ShambaPolygonMap(
    polygonPoints: List<LatLng>,
    currentRegionName: String,
    mapType: Int,
    estimatedAcres: Double?,
    canSave: Boolean,
    saveCtaText: String,
    topBar: @Composable () -> Unit,
    onMapTapped: (LatLng) -> Unit,
    onCameraMoved: (LatLng) -> Unit,
    onUndo: () -> Unit,
    onToggleMapType: () -> Unit,
    onSave: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val context = LocalContext.current
    val fusedLocationClient = remember { LocationServices.getFusedLocationProviderClient(context) }
    val keyboardController = LocalSoftwareKeyboardController.current

    var searchQuery by remember { mutableStateOf("") }
    var searchError by remember { mutableStateOf(false) }
    // Hide the instruction overlay once the farmer places their first boundary tap
    var showInstructions by remember { mutableStateOf(true) }
    // Tracks whether we are still waiting for the GPS fix
    var isLocating by remember { mutableStateOf(false) }
    // Tracks if device GPS is physically turned on (after prompt)
    var gpsEnabled by remember { mutableStateOf(false) }

    val locationPermissionState = rememberPermissionState(
        Manifest.permission.ACCESS_FINE_LOCATION,
    )

    // Request permission on launch
    LaunchedEffect(Unit) {
        if (!locationPermissionState.status.isGranted) {
            locationPermissionState.launchPermissionRequest()
        }
    }

    // If permission is granted, check if GPS is actually turned on
    if (locationPermissionState.status.isGranted) {
        dev.korryr.shambaguard.core.util.EnableLocationEffect(
            onLocationEnabled = { gpsEnabled = true },
            onLocationDeclined = { gpsEnabled = true } // Proceed anyway; getCurrentLocation will just fail gracefully
        )
    }

    val cameraState = rememberCameraPositionState()

    // Snap camera to real GPS as a coroutine — no callbacks, no race conditions.
    // Uses getCurrentLocation(HIGH_ACCURACY) directly so it always gets a fresh fix,
    // even on cold start or emulators. No static fallback — if GPS fails, map stays
    // centered wherever it is.
    LaunchedEffect(locationPermissionState.status.isGranted, gpsEnabled) {
        if (locationPermissionState.status.isGranted && gpsEnabled) {
            isLocating = true
            val cts = CancellationTokenSource()
            try {
                val location = suspendCancellableCoroutine { cont ->
                    fusedLocationClient
                        .getCurrentLocation(Priority.PRIORITY_HIGH_ACCURACY, cts.token)
                        .addOnSuccessListener { loc -> cont.resume(loc) }
                        .addOnFailureListener { cont.resume(null) }
                    cont.invokeOnCancellation { cts.cancel() }
                }
                if (location != null) {
                    val latLng = LatLng(location.latitude, location.longitude)
                    cameraState.animate(
                        com.google.android.gms.maps.CameraUpdateFactory
                            .newLatLngZoom(latLng, DEFAULT_ZOOM)
                    )
                    onCameraMoved(latLng)
                }
            } finally {
                isLocating = false
            }
        }
    }

    // Listen to camera movements
    LaunchedEffect(cameraState.isMoving) {
        if (!cameraState.isMoving) {
            onCameraMoved(cameraState.position.target)
        }
    }

    val mapProperties = remember(mapType) {
        MapProperties(
            mapType = when (mapType) {
                com.google.android.gms.maps.GoogleMap.MAP_TYPE_NORMAL -> com.google.maps.android.compose.MapType.NORMAL
                com.google.android.gms.maps.GoogleMap.MAP_TYPE_TERRAIN -> com.google.maps.android.compose.MapType.TERRAIN
                com.google.android.gms.maps.GoogleMap.MAP_TYPE_HYBRID -> com.google.maps.android.compose.MapType.HYBRID
                else -> com.google.maps.android.compose.MapType.SATELLITE
            },
        )
    }

    val mapUiSettings = remember {
        MapUiSettings(
            zoomControlsEnabled = false,
            myLocationButtonEnabled = false,
            mapToolbarEnabled = false,
        )
    }

    Column(modifier = modifier.fillMaxSize()) {
        topBar()

        // Map fills remaining space
        Box(modifier = Modifier.weight(1f)) {
            GoogleMap(
                modifier = Modifier.fillMaxSize(),
                cameraPositionState = cameraState,
                properties = mapProperties,
                uiSettings = mapUiSettings,
                onMapClick = { latLng ->
                    showInstructions = false // Dismiss instructions on first tap
                    onMapTapped(latLng)
                },
            ) {
                // Draw dashed outline as we go
                if (polygonPoints.size >= 2) {
                    Polyline(
                        points = polygonPoints,
                        color = PolygonGreen,
                        width = 6f,
                        pattern = listOf(Dash(20f), Gap(10f)),
                    )
                }

                // Filled polygon once closed (>= 3 points)
                if (polygonPoints.size >= 3) {
                    Polygon(
                        points = polygonPoints,
                        strokeColor = PolygonGreen,
                        strokeWidth = 6f,
                        fillColor = PolygonFill,
                        geodesic = true,
                    )
                }

                // Corner markers
                polygonPoints.forEach { point ->
                    Marker(
                        state = MarkerState(position = point),
                    )
                }
            }

            // Search bar — overlaid at the very top of the map
            LocationSearchBar(
                query = searchQuery,
                hasError = searchError,
                onQueryChange = {
                    searchQuery = it
                    searchError = false
                },
                onSearch = {
                    keyboardController?.hide()
                    if (searchQuery.isNotBlank()) {
                        val geocoder = Geocoder(context)
                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                            geocoder.getFromLocationName(searchQuery, 1) { results ->
                                val result = results.firstOrNull()
                                if (result != null) {
                                    val latLng = LatLng(result.latitude, result.longitude)
                                    cameraState.position = CameraPosition.fromLatLngZoom(latLng, DEFAULT_ZOOM)
                                    onCameraMoved(latLng)
                                } else {
                                    searchError = true
                                }
                            }
                        } else {
                            @Suppress("DEPRECATION")
                            val results = geocoder.getFromLocationName(searchQuery, 1)
                            val result = results?.firstOrNull()
                            if (result != null) {
                                val latLng = LatLng(result.latitude, result.longitude)
                                cameraState.position = CameraPosition.fromLatLngZoom(latLng, DEFAULT_ZOOM)
                                onCameraMoved(latLng)
                            } else {
                                searchError = true
                            }
                        }
                    }
                },
                modifier = Modifier
                    .align(Alignment.TopCenter)
                    .padding(top = 12.dp, start = 16.dp, end = 16.dp),
            )

            // Floating instruction card — visible only until the first tap
            if (showInstructions) {
                InstructionCard(
                    modifier = Modifier
                        .align(Alignment.TopCenter)
                        .padding(top = 76.dp, start = 16.dp, end = 16.dp),
                )
            }

            // GPS acquiring badge — shown while waiting for a location fix
            if (isLocating) {
                Row(
                    modifier = Modifier
                        .align(Alignment.TopCenter)
                        .padding(top = if (showInstructions) 140.dp else 76.dp)
                        .shadow(4.dp, RoundedCornerShape(50))
                        .background(MaterialTheme.colorScheme.surface, RoundedCornerShape(50))
                        .padding(horizontal = 14.dp, vertical = 8.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                ) {
                    CircularProgressIndicator(
                        modifier = Modifier.size(14.dp),
                        strokeWidth = 2.dp,
                        color = MaterialTheme.colorScheme.primary,
                    )
                    Text(
                        text = "Finding your location…",
                        style = MaterialTheme.typography.labelSmall.copy(
                            color = MaterialTheme.colorScheme.onSurface,
                            fontWeight = FontWeight.Medium,
                        ),
                    )
                }
            }

            // Area label in the polygon centre
            if (estimatedAcres != null) {
                AreaLabel(
                    acres = estimatedAcres,
                    modifier = Modifier.align(Alignment.Center),
                )
            }

            // Undo button
            if (polygonPoints.isNotEmpty()) {
                IconButton(
                    onClick = onUndo,
                    modifier = Modifier
                        .align(Alignment.BottomEnd)
                        .padding(bottom = 140.dp, end = 16.dp)
                        .size(44.dp)
                        .clip(CircleShape)
                        .background(MaterialTheme.colorScheme.surface),
                ) {
                    Icon(
                        imageVector = Icons.AutoMirrored.Filled.Undo,
                        contentDescription = stringResource(R.string.farm_step2_undo),
                        tint = MaterialTheme.colorScheme.primary,
                    )
                }
            }

            // Hint shown until 3 points are placed
            if (polygonPoints.size < 3) {
                Text(
                    text = stringResource(R.string.farm_step2_min_points_hint),
                    style = MaterialTheme.typography.labelSmall.copy(
                        color = Color.White,
                    ),
                    modifier = Modifier
                        .align(Alignment.BottomCenter)
                        .padding(bottom = 140.dp)
                        .background(
                            color = Color.Black.copy(alpha = 0.55f),
                            shape = RoundedCornerShape(50),
                        )
                        .padding(horizontal = 14.dp, vertical = 6.dp),
                )
            }
        }

        // Bottom confirm sheet
        ConfirmBoundarySheet(
            estimatedAcres = estimatedAcres,
            currentRegionName = currentRegionName,
            canSave = canSave,
            saveCtaText = saveCtaText,
            onSave = onSave,
        )
    }
}

// Floating card that explains what the user should do on the map
@Composable
private fun InstructionCard(modifier: Modifier = Modifier) {
    Card(
        modifier = modifier
            .fillMaxWidth()
            .shadow(4.dp, RoundedCornerShape(12.dp)),
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface.copy(alpha = 0.95f),
        ),
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.padding(horizontal = 14.dp, vertical = 10.dp),
        ) {
            Box(
                modifier = Modifier
                    .size(36.dp)
                    .clip(CircleShape)
                    .background(MaterialTheme.colorScheme.primaryContainer),
                contentAlignment = Alignment.Center,
            ) {
                Icon(
                    imageVector = Icons.Filled.LocationOn,
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.primary,
                    modifier = Modifier.size(20.dp),
                )
            }

            Spacer(modifier = Modifier.width(10.dp))

            Column {
                Text(
                    text = stringResource(R.string.farm_step2_instruction_title),
                    style = MaterialTheme.typography.labelMedium.copy(
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onSurface,
                    ),
                )
                Text(
                    text = stringResource(R.string.farm_step2_instruction_subtitle),
                    style = MaterialTheme.typography.bodySmall.copy(
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                    ),
                )
            }
        }
    }
}

// Green pill label shown at the polygon centre with the estimated acreage
@Composable
private fun AreaLabel(acres: Double, modifier: Modifier = Modifier) {
    val formatted = stringResource(R.string.farm_step2_area_acres_format, acres)
    Box(
        modifier = modifier
            .shadow(4.dp, RoundedCornerShape(50))
            .background(PolygonGreen, RoundedCornerShape(50))
            .padding(horizontal = 14.dp, vertical = 6.dp),
        contentAlignment = Alignment.Center,
    ) {
        Text(
            text = "▲ $formatted",
            style = MaterialTheme.typography.labelMedium.copy(
                color = Color.White,
                fontWeight = FontWeight.Bold,
            ),
        )
    }
}

// Bottom white sheet with confirm info and save CTA
@Composable
private fun ConfirmBoundarySheet(
    estimatedAcres: Double?,
    currentRegionName: String,
    canSave: Boolean,
    saveCtaText: String,
    onSave: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Surface(
        modifier = modifier.fillMaxWidth(),
        color = MaterialTheme.colorScheme.surface,
        shadowElevation = 16.dp,
        shape = RoundedCornerShape(topStart = 20.dp, topEnd = 20.dp),
    ) {
        Column(modifier = Modifier.padding(horizontal = 24.dp, vertical = 20.dp)) {
            // Drag handle
            Box(
                modifier = Modifier
                    .width(40.dp)
                    .height(4.dp)
                    .clip(RoundedCornerShape(50))
                    .background(MaterialTheme.colorScheme.outlineVariant)
                    .align(Alignment.CenterHorizontally),
            )

            Spacer(modifier = Modifier.height(16.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.Bottom,
            ) {
                // Left: heading + district
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = stringResource(R.string.farm_step2_confirm_heading),
                        style = MaterialTheme.typography.headlineSmall.copy(
                            fontWeight = FontWeight.ExtraBold,
                            color = MaterialTheme.colorScheme.onSurface,
                        ),
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            imageVector = Icons.Filled.LocationOn,
                            contentDescription = null,
                            tint = MaterialTheme.colorScheme.primary,
                            modifier = Modifier.size(14.dp),
                        )
                        Spacer(modifier = Modifier.width(4.dp))
                        Text(
                            text = currentRegionName,
                            style = MaterialTheme.typography.bodySmall.copy(
                                color = MaterialTheme.colorScheme.onSurfaceVariant,
                            ),
                        )
                    }
                }

                // Right: estimated size
                Column(horizontalAlignment = Alignment.End) {
                    Text(
                        text = stringResource(R.string.farm_step2_estimated_label),
                        style = MaterialTheme.typography.labelSmall.copy(
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                            letterSpacing = 0.5.sp,
                        ),
                    )
                    Text(
                        text = if (estimatedAcres != null) {
                            stringResource(R.string.farm_step2_area_acres_format, estimatedAcres)
                        } else {
                            "— ac"
                        },
                        style = MaterialTheme.typography.headlineMedium.copy(
                            fontWeight = FontWeight.ExtraBold,
                            color = MaterialTheme.colorScheme.primary,
                        ),
                    )
                }
            }

            Spacer(modifier = Modifier.height(20.dp))

            ShambaButton(
                text = saveCtaText,
                onClick = onSave,
                enabled = canSave,
                modifier = Modifier.fillMaxWidth(),
                textStyle = MaterialTheme.typography.labelLarge.copy(
                    fontWeight = FontWeight.Bold,
                    fontSize = 16.sp,
                ),
            )
        }
    }
}

@Composable
private fun LocationSearchBar(
    query: String,
    hasError: Boolean,
    onQueryChange: (String) -> Unit,
    onSearch: () -> Unit,
    modifier: Modifier = Modifier,
) {
    OutlinedTextField(
        value = query,
        onValueChange = onQueryChange,
        modifier = modifier.fillMaxWidth(),
        placeholder = {
            Text(
                text = "Search location (e.g. Meru, Laare...)",
                style = MaterialTheme.typography.bodyMedium.copy(
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                ),
            )
        },
        leadingIcon = {
            Icon(
                imageVector = Icons.Filled.Search,
                contentDescription = "Search",
                tint = if (hasError) MaterialTheme.colorScheme.error
                       else MaterialTheme.colorScheme.primary,
            )
        },
        trailingIcon = {
            if (hasError) {
                Text(
                    text = "Not found",
                    style = MaterialTheme.typography.labelSmall.copy(
                        color = MaterialTheme.colorScheme.error,
                    ),
                    modifier = Modifier.padding(end = 8.dp),
                )
            }
        },
        singleLine = true,
        shape = RoundedCornerShape(28.dp),
        colors = OutlinedTextFieldDefaults.colors(
            focusedContainerColor = MaterialTheme.colorScheme.surface,
            unfocusedContainerColor = MaterialTheme.colorScheme.surface,
            focusedBorderColor = MaterialTheme.colorScheme.primary,
            unfocusedBorderColor = Color.Transparent,
            errorBorderColor = MaterialTheme.colorScheme.error,
        ),
        isError = hasError,
        keyboardOptions = KeyboardOptions(imeAction = ImeAction.Search),
        keyboardActions = KeyboardActions(onSearch = { onSearch() }),
    )
}
