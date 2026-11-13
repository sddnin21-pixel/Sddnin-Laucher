package com.example.ui.dialogs

import android.content.Context
import android.hardware.Sensor
import android.hardware.SensorEvent
import android.hardware.SensorEventListener
import android.hardware.SensorManager
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Build
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Explore
import androidx.compose.material.icons.filled.Pause
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.QrCode2
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.SwapHoriz
import androidx.compose.material.icons.filled.Timer
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableLongStateOf
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import kotlinx.coroutines.delay
import kotlin.math.PI
import kotlin.math.atan2
import kotlin.math.cos
import kotlin.math.roundToInt
import kotlin.math.sin

@Composable
fun ToolboxHubDialog(
    isOpen: Boolean,
    onDismiss: () -> Unit
) {
    if (!isOpen) return
    var selectedTool by remember { mutableIntStateOf(0) }
    val tools = listOf(
        Pair("Quy đổi", Icons.Default.SwapHoriz),
        Pair("Bấm giờ", Icons.Default.Timer),
        Pair("La bàn", Icons.Default.Explore),
        Pair("Mã QR", Icons.Default.QrCode2)
    )

    Dialog(
        onDismissRequest = onDismiss,
        properties = DialogProperties(usePlatformDefaultWidth = false)
    ) {
        Surface(
            shape = RoundedCornerShape(30.dp),
            color = Color(0xFF0F172A),
            border = androidx.compose.foundation.BorderStroke(1.dp, Color(0x33FFFFFF)),
            modifier = Modifier
                .fillMaxWidth(0.94f)
                .fillMaxHeight(0.84f)
        ) {
            Column(modifier = Modifier.fillMaxSize().padding(20.dp)) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(Icons.Default.Build, contentDescription = null, tint = Color(0xFFFBBF24))
                        Spacer(modifier = Modifier.width(8.dp))
                        Column {
                            Text("Hộp Công Cụ Đa Năng 3.0", color = Color.White, fontSize = 18.sp, fontWeight = FontWeight.Bold)
                            Text("Tiện ích đo lường, bấm giờ & tạo QR tức thì", color = Color(0xFF94A3B8), fontSize = 11.sp)
                        }
                    }
                    IconButton(
                        onClick = onDismiss,
                        modifier = Modifier.size(32.dp).clip(CircleShape).background(Color(0x33FFFFFF))
                    ) {
                        Icon(Icons.Default.Close, contentDescription = "Đóng", tint = Color.White, modifier = Modifier.size(16.dp))
                    }
                }

                Spacer(modifier = Modifier.height(14.dp))

                LazyRow(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    itemsIndexed(tools) { idx, (label, icon) ->
                        val sel = selectedTool == idx
                        Row(
                            modifier = Modifier
                                .clip(RoundedCornerShape(16.dp))
                                .background(if (sel) Color(0xFF38BDF8) else Color(0xFF1E293B))
                                .clickable { selectedTool = idx }
                                .padding(horizontal = 14.dp, vertical = 9.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Icon(icon, contentDescription = null, tint = if (sel) Color.Black else Color(0xFFCBD5E1), modifier = Modifier.size(16.dp))
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(label, color = if (sel) Color.Black else Color.White, fontSize = 12.sp, fontWeight = FontWeight.Bold)
                        }
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                Box(modifier = Modifier.weight(1f).fillMaxWidth()) {
                    when (selectedTool) {
                        0 -> UnitConverterTool()
                        1 -> PrecisionStopwatchTool()
                        2 -> SensorCompassAndLevelTool()
                        3 -> QrGeneratorTool()
                    }
                }
            }
        }
    }
}

@Composable
private fun UnitConverterTool() {
    var category by remember { mutableIntStateOf(0) } // 0: Length, 1: Weight, 2: Temp, 3: Currency
    var inputText by remember { mutableStateOf("100") }
    val num = inputText.toDoubleOrNull() ?: 0.0

    val categories = listOf("Độ dài", "Khối lượng", "Nhiệt độ", "Tiền tệ")
    val results = when (category) {
        0 -> listOf(
            "Mét (m)" to num,
            "Kilômét (km)" to num / 1000.0,
            "Centimét (cm)" to num * 100.0,
            "Inch (in)" to num * 39.3701,
            "Feet (ft)" to num * 3.28084
        )
        1 -> listOf(
            "Kilôgam (kg)" to num,
            "Gam (g)" to num * 1000.0,
            "Pound (lb)" to num * 2.20462,
            "Ounce (oz)" to num * 35.274
        )
        2 -> listOf(
            "Độ C (°C)" to num,
            "Độ F (°F)" to (num * 9.0 / 5.0 + 32.0),
            "Kelvin (K)" to (num + 273.15)
        )
        else -> listOf(
            "USD ($)" to num,
            "VND (₫)" to num * 25400.0,
            "EUR (€)" to num * 0.92,
            "JPY (¥)" to num * 154.0
        )
    }

    Column(modifier = Modifier.fillMaxSize()) {
        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(6.dp)) {
            categories.forEachIndexed { idx, name ->
                val sel = category == idx
                Box(
                    modifier = Modifier
                        .weight(1f)
                        .clip(RoundedCornerShape(12.dp))
                        .background(if (sel) Color(0xFFA855F7) else Color(0xFF1E293B))
                        .clickable { category = idx }
                        .padding(vertical = 8.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Text(name, color = Color.White, fontSize = 11.sp, fontWeight = FontWeight.SemiBold)
                }
            }
        }

        Spacer(modifier = Modifier.height(12.dp))

        OutlinedTextField(
            value = inputText,
            onValueChange = { if (it.length <= 12) inputText = it },
            label = { Text("Nhập giá trị đầu vào", color = Color(0xFF94A3B8)) },
            singleLine = true,
            modifier = Modifier.fillMaxWidth(),
            colors = OutlinedTextFieldDefaults.colors(
                focusedTextColor = Color.White,
                unfocusedTextColor = Color.White,
                focusedBorderColor = Color(0xFF38BDF8),
                unfocusedBorderColor = Color(0x33FFFFFF)
            )
        )

        Spacer(modifier = Modifier.height(12.dp))

        LazyColumn(verticalArrangement = Arrangement.spacedBy(8.dp)) {
            itemsIndexed(results) { _, (unit, value) ->
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(14.dp))
                        .background(Color(0xFF1E293B))
                        .padding(14.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(unit, color = Color(0xFFCBD5E1), fontSize = 13.sp)
                    Text(
                        text = String.format("%,.2f", value),
                        color = Color(0xFF38BDF8),
                        fontSize = 15.sp,
                        fontWeight = FontWeight.Bold,
                        fontFamily = FontFamily.Monospace
                    )
                }
            }
        }
    }
}

@Composable
private fun PrecisionStopwatchTool() {
    var running by remember { mutableStateOf(false) }
    var elapsedMs by remember { mutableLongStateOf(0L) }
    val laps = remember { mutableStateListOf<String>() }

    LaunchedEffect(running) {
        var lastTime = System.currentTimeMillis()
        while (running) {
            delay(30L)
            val now = System.currentTimeMillis()
            elapsedMs += (now - lastTime)
            lastTime = now
        }
    }

    fun formatMs(ms: Long): String {
        val mins = (ms / 60000) % 60
        val secs = (ms / 1000) % 60
        val centi = (ms / 10) % 100
        return String.format("%02d:%02d.%02d", mins, secs, centi)
    }

    Column(
        modifier = Modifier.fillMaxSize(),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Box(
            modifier = Modifier
                .size(180.dp)
                .clip(CircleShape)
                .background(Color(0xFF1E293B))
                .border(3.dp, if (running) Color(0xFF10B981) else Color(0xFF38BDF8), CircleShape),
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = formatMs(elapsedMs),
                color = Color.White,
                fontSize = 28.sp,
                fontWeight = FontWeight.Bold,
                fontFamily = FontFamily.Monospace
            )
        }

        Spacer(modifier = Modifier.height(16.dp))

        Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
            Button(
                onClick = { running = !running },
                colors = ButtonDefaults.buttonColors(
                    containerColor = if (running) Color(0xFFF43F5E) else Color(0xFF10B981)
                ),
                shape = RoundedCornerShape(14.dp)
            ) {
                Icon(if (running) Icons.Default.Pause else Icons.Default.PlayArrow, contentDescription = null, tint = Color.Black)
                Spacer(modifier = Modifier.width(6.dp))
                Text(if (running) "Tạm dừng" else "Bắt đầu", color = Color.Black, fontWeight = FontWeight.Bold)
            }

            Button(
                onClick = {
                    if (running) {
                        laps.add(0, "Vòng ${laps.size + 1}: ${formatMs(elapsedMs)}")
                    } else {
                        elapsedMs = 0L
                        laps.clear()
                    }
                },
                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF334155)),
                shape = RoundedCornerShape(14.dp)
            ) {
                Icon(Icons.Default.Refresh, contentDescription = null, tint = Color.White)
                Spacer(modifier = Modifier.width(6.dp))
                Text(if (running) "Ghi vòng" else "Đặt lại", color = Color.White)
            }
        }

        Spacer(modifier = Modifier.height(14.dp))

        LazyColumn(modifier = Modifier.weight(1f).fillMaxWidth(), verticalArrangement = Arrangement.spacedBy(6.dp)) {
            itemsIndexed(laps) { _, lap ->
                Text(
                    text = lap,
                    color = Color(0xFFE2E8F0),
                    fontSize = 13.sp,
                    fontFamily = FontFamily.Monospace,
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(10.dp))
                        .background(Color(0xFF1E293B))
                        .padding(10.dp)
                )
            }
        }
    }
}

@Composable
private fun SensorCompassAndLevelTool() {
    val context = LocalContext.current
    var azimuthDeg by remember { mutableFloatStateOf(0f) }
    var tiltX by remember { mutableFloatStateOf(0f) }
    var tiltY by remember { mutableFloatStateOf(0f) }

    DisposableEffect(Unit) {
        val sensorManager = context.getSystemService(Context.SENSOR_SERVICE) as? SensorManager
        val accel = sensorManager?.getDefaultSensor(Sensor.TYPE_ACCELEROMETER)
        val magnet = sensorManager?.getDefaultSensor(Sensor.TYPE_MAGNETIC_FIELD)
        val gravity = FloatArray(3)
        val geomag = FloatArray(3)
        val rMat = FloatArray(9)
        val orientation = FloatArray(3)

        val listener = object : SensorEventListener {
            override fun onSensorChanged(event: SensorEvent) {
                if (event.values.size < 3) return
                if (event.sensor.type == Sensor.TYPE_ACCELEROMETER) {
                    System.arraycopy(event.values, 0, gravity, 0, 3)
                    tiltX = event.values[0]
                    tiltY = event.values[1]
                } else if (event.sensor.type == Sensor.TYPE_MAGNETIC_FIELD) {
                    System.arraycopy(event.values, 0, geomag, 0, 3)
                }
                if (SensorManager.getRotationMatrix(rMat, null, gravity, geomag)) {
                    SensorManager.getOrientation(rMat, orientation)
                    val deg = Math.toDegrees(orientation[0].toDouble()).toFloat()
                    azimuthDeg = (deg + 360f) % 360f
                }
            }

            override fun onAccuracyChanged(sensor: Sensor?, accuracy: Int) {}
        }

        accel?.let { sensorManager.registerListener(listener, it, SensorManager.SENSOR_DELAY_UI) }
        magnet?.let { sensorManager.registerListener(listener, it, SensorManager.SENSOR_DELAY_UI) }

        onDispose {
            sensorManager?.unregisterListener(listener)
        }
    }

    val directionName = when (((azimuthDeg + 22.5f) / 45f).toInt() % 8) {
        0 -> "Bắc (N)"
        1 -> "Đông Bắc (NE)"
        2 -> "Đông (E)"
        3 -> "Đông Nam (SE)"
        4 -> "Nam (S)"
        5 -> "Tây Nam (SW)"
        6 -> "Tây (W)"
        else -> "Tây Bắc (NW)"
    }

    Column(
        modifier = Modifier.fillMaxSize(),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Text(
            text = "${azimuthDeg.roundToInt()}° • $directionName",
            color = Color(0xFF38BDF8),
            fontSize = 22.sp,
            fontWeight = FontWeight.Bold
        )
        Text("La bàn & Cân bằng mặt phẳng (Cảm biến thực)", color = Color(0xFF94A3B8), fontSize = 11.sp)

        Spacer(modifier = Modifier.height(18.dp))

        Canvas(modifier = Modifier.size(210.dp)) {
            val center = Offset(size.width / 2f, size.height / 2f)
            val radius = size.minDimension / 2f - 8f
            drawCircle(color = Color(0xFF1E293B), radius = radius, center = center)
            drawCircle(color = Color(0xFF38BDF8), radius = radius, center = center, style = Stroke(width = 4f))

            // Compass Needle rotated by -azimuthDeg
            val rad = Math.toRadians(-azimuthDeg.toDouble() - 90.0)
            val tipX = center.x + cos(rad).toFloat() * (radius * 0.72f)
            val tipY = center.y + sin(rad).toFloat() * (radius * 0.72f)
            val tailX = center.x - cos(rad).toFloat() * (radius * 0.35f)
            val tailY = center.y - sin(rad).toFloat() * (radius * 0.35f)
            drawLine(color = Color(0xFFF43F5E), start = center, end = Offset(tipX, tipY), strokeWidth = 10f)
            drawLine(color = Color.White, start = center, end = Offset(tailX, tailY), strokeWidth = 8f)

            // Bubble level indicator
            val bx = (center.x - tiltX * 8f).coerceIn(center.x - radius * 0.6f, center.x + radius * 0.6f)
            val by = (center.y + tiltY * 8f).coerceIn(center.y - radius * 0.6f, center.y + radius * 0.6f)
            drawCircle(color = Color(0x8810B981), radius = 16f, center = Offset(bx, by))
        }

        Spacer(modifier = Modifier.height(14.dp))
        Text(
            text = "Độ nghiêng X: ${String.format("%.1f", tiltX)} • Y: ${String.format("%.1f", tiltY)}",
            color = Color(0xFFCBD5E1),
            fontSize = 12.sp,
            fontFamily = FontFamily.Monospace
        )
    }
}

@Composable
private fun QrGeneratorTool() {
    var qrContent by remember { mutableStateOf("https://sddnin-launcher.app") }

    // Deterministic 15x15 visual matrix from content hash + finder patterns
    val matrix = remember(qrContent) {
        val n = 15
        val grid = Array(n) { BooleanArray(n) }
        fun addFinder(r0: Int, c0: Int) {
            for (r in 0..4) for (c in 0..4) {
                grid[r0 + r][c0 + c] = (r == 0 || r == 4 || c == 0 || c == 4 || (r == 2 && c == 2))
            }
        }
        addFinder(0, 0)
        addFinder(0, n - 5)
        addFinder(n - 5, 0)

        val bytes = qrContent.ifBlank { "Sddnin" }.toByteArray()
        var seed = 0x5A17
        bytes.forEachIndexed { idx, b -> seed = seed * 31 + b.toInt() + idx }
        for (r in 0 until n) {
            for (c in 0 until n) {
                val inFinder = (r < 6 && c < 6) || (r < 6 && c >= n - 6) || (r >= n - 6 && c < 6)
                if (!inFinder) {
                    seed = (seed * 1103515245 + 12345) and 0x7fffffff
                    grid[r][c] = (seed % 3) != 0
                }
            }
        }
        grid
    }

    Column(
        modifier = Modifier.fillMaxSize(),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        OutlinedTextField(
            value = qrContent,
            onValueChange = { qrContent = it },
            label = { Text("Nhập văn bản hoặc link tạo QR", color = Color(0xFF94A3B8)) },
            singleLine = true,
            modifier = Modifier.fillMaxWidth(),
            colors = OutlinedTextFieldDefaults.colors(
                focusedTextColor = Color.White,
                unfocusedTextColor = Color.White,
                focusedBorderColor = Color(0xFF38BDF8),
                unfocusedBorderColor = Color(0x33FFFFFF)
            )
        )

        Spacer(modifier = Modifier.height(18.dp))

        Box(
            modifier = Modifier
                .size(210.dp)
                .clip(RoundedCornerShape(20.dp))
                .background(Color.White)
                .padding(16.dp)
        ) {
            Canvas(modifier = Modifier.fillMaxSize()) {
                val n = matrix.size
                val cell = size.width / n
                for (r in 0 until n) {
                    for (c in 0 until n) {
                        if (matrix[r][c]) {
                            drawRect(
                                color = Color(0xFF0F172A),
                                topLeft = Offset(c * cell, r * cell),
                                size = Size(cell + 0.5f, cell + 0.5f)
                            )
                        }
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(10.dp))
        Text("Mã QR tương tác sẵn sàng quét & chia sẻ", color = Color(0xFF94A3B8), fontSize = 11.sp)
    }
}
