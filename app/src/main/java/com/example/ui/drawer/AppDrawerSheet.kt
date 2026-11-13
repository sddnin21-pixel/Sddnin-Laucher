package com.example.ui.drawer

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.gestures.detectVerticalDragGestures
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.lazy.grid.rememberLazyGridState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.FilterList
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.SortByAlpha
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.ScrollableTabRow
import androidx.compose.material3.Tab
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.AppCategory
import com.example.data.model.AppInfo
import com.example.data.model.IconPackStyle
import com.example.data.model.IconShape
import com.example.ui.components.AppIconView
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class, androidx.compose.foundation.ExperimentalFoundationApi::class)
@Composable
fun AppDrawerSheet(
    isOpen: Boolean,
    apps: List<AppInfo>,
    searchQuery: String,
    selectedCategory: AppCategory,
    sortMode: String,
    iconShape: IconShape,
    iconPackStyle: IconPackStyle = IconPackStyle.DEFAULT,
    showLabels: Boolean,
    onSearchChange: (String) -> Unit,
    onCategoryChange: (AppCategory) -> Unit,
    onSortChange: (String) -> Unit,
    onAppClick: (AppInfo) -> Unit,
    onAppLongClick: (AppInfo) -> Unit,
    onClose: () -> Unit
) {
    AnimatedVisibility(
        visible = isOpen,
        enter = slideInVertically(initialOffsetY = { it }) + fadeIn(),
        exit = slideOutVertically(targetOffsetY = { it }) + fadeOut()
    ) {
        val gridState = rememberLazyGridState()
        val coroutineScope = rememberCoroutineScope()
        var showSortMenu by remember { mutableStateOf(false) }

        // Alphabet index list for fast jump
        val alphabetLetters = remember(apps) {
            apps.map { it.displayLabel.take(1).uppercase() }
                .distinct()
                .filter { it.matches(Regex("[A-Z0-9]")) }
                .sorted()
        }

        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(Color(0xE60A0E17))
                .statusBarsPadding()
                .navigationBarsPadding()
                .pointerInput(Unit) {
                    var totalDragY = 0f
                    var dismissed = false
                    detectVerticalDragGestures(
                        onDragStart = {
                            totalDragY = 0f
                            dismissed = false
                        },
                        onDragEnd = {
                            totalDragY = 0f
                            dismissed = false
                        },
                        onDragCancel = {
                            totalDragY = 0f
                            dismissed = false
                        }
                    ) { _, dragAmount ->
                        totalDragY += dragAmount
                        // Swipe down to close drawer if near top
                        if (!dismissed && totalDragY > 60f && gridState.firstVisibleItemIndex == 0) {
                            dismissed = true
                            onClose()
                        }
                    }
                }
        ) {
            Column(modifier = Modifier.fillMaxSize()) {
                // Drag handle bar
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 8.dp, bottom = 4.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Box(
                        modifier = Modifier
                            .width(40.dp)
                            .height(4.dp)
                            .clip(RoundedCornerShape(2.dp))
                            .background(Color(0x66FFFFFF))
                    )
                }

                // Search Bar & Sort button
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp, vertical = 6.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    OutlinedTextField(
                        value = searchQuery,
                        onValueChange = onSearchChange,
                        modifier = Modifier
                            .weight(1f)
                            .height(52.dp),
                        placeholder = {
                            Text(
                                "Tìm ứng dụng (hỗ trợ không dấu)...",
                                color = Color(0xFF94A3B8),
                                fontSize = 13.sp
                            )
                        },
                        leadingIcon = {
                            Icon(
                                imageVector = Icons.Default.Search,
                                contentDescription = "Tìm kiếm",
                                tint = Color(0xFF38BDF8),
                                modifier = Modifier.size(20.dp)
                            )
                        },
                        trailingIcon = {
                            if (searchQuery.isNotEmpty()) {
                                IconButton(onClick = { onSearchChange("") }) {
                                    Icon(
                                        imageVector = Icons.Default.Close,
                                        contentDescription = "Xóa",
                                        tint = Color(0xFFCBD5E1),
                                        modifier = Modifier.size(18.dp)
                                    )
                                }
                            }
                        },
                        singleLine = true,
                        shape = RoundedCornerShape(26.dp),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedTextColor = Color.White,
                            unfocusedTextColor = Color.White,
                            focusedBorderColor = Color(0xFF38BDF8),
                            unfocusedBorderColor = Color(0x33FFFFFF),
                            focusedContainerColor = Color(0x331E293B),
                            unfocusedContainerColor = Color(0x331E293B)
                        ),
                        textStyle = androidx.compose.ui.text.TextStyle(fontSize = 14.sp)
                    )

                    Spacer(modifier = Modifier.width(8.dp))

                    // Sort menu button
                    Box {
                        IconButton(
                            onClick = { showSortMenu = true },
                            modifier = Modifier
                                .size(48.dp)
                                .clip(CircleShape)
                                .background(Color(0x331E293B))
                        ) {
                            Icon(
                                imageVector = Icons.Default.SortByAlpha,
                                contentDescription = "Sắp xếp",
                                tint = Color(0xFF38BDF8),
                                modifier = Modifier.size(20.dp)
                            )
                        }

                        DropdownMenu(
                            expanded = showSortMenu,
                            onDismissRequest = { showSortMenu = false },
                            modifier = Modifier.background(Color(0xFF1E293B))
                        ) {
                            DropdownMenuItem(
                                text = { Text("Theo tên (A-Z)", color = if (sortMode == "ALPHABETICAL") Color(0xFF38BDF8) else Color.White) },
                                onClick = {
                                    onSortChange("ALPHABETICAL")
                                    showSortMenu = false
                                }
                            )
                            DropdownMenuItem(
                                text = { Text("Dùng nhiều nhất", color = if (sortMode == "MOST_USED") Color(0xFF38BDF8) else Color.White) },
                                onClick = {
                                    onSortChange("MOST_USED")
                                    showSortMenu = false
                                }
                            )
                            DropdownMenuItem(
                                text = { Text("Mới sử dụng gần đây", color = if (sortMode == "RECENT") Color(0xFF38BDF8) else Color.White) },
                                onClick = {
                                    onSortChange("RECENT")
                                    showSortMenu = false
                                }
                            )
                        }
                    }

                    Spacer(modifier = Modifier.width(4.dp))
                    IconButton(
                        onClick = onClose,
                        modifier = Modifier
                            .size(48.dp)
                            .clip(CircleShape)
                            .background(Color(0x331E293B))
                    ) {
                        Icon(
                            imageVector = Icons.Default.KeyboardArrowDown,
                            contentDescription = "Đóng",
                            tint = Color.White,
                            modifier = Modifier.size(24.dp)
                        )
                    }
                }

                // Category Tabs
                ScrollableTabRow(
                    selectedTabIndex = AppCategory.entries.indexOf(selectedCategory),
                    edgePadding = 16.dp,
                    containerColor = Color.Transparent,
                    contentColor = Color(0xFF38BDF8),
                    divider = {}
                ) {
                    AppCategory.entries.forEach { cat ->
                        val isSelected = cat == selectedCategory
                        Tab(
                            selected = isSelected,
                            onClick = { onCategoryChange(cat) },
                            text = {
                                Text(
                                    text = cat.title,
                                    fontSize = 12.sp,
                                    fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal,
                                    color = if (isSelected) Color(0xFF38BDF8) else Color(0xFF94A3B8)
                                )
                            }
                        )
                    }
                }

                Spacer(modifier = Modifier.height(8.dp))

                // App Grid with Fast Alphabet Index Scroll
                Row(modifier = Modifier.fillMaxSize()) {
                    LazyVerticalGrid(
                        columns = GridCells.Fixed(4),
                        state = gridState,
                        modifier = Modifier
                            .weight(1f)
                            .padding(horizontal = 8.dp),
                        verticalArrangement = Arrangement.spacedBy(16.dp),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        items(apps, key = { it.packageName }) { app ->
                            Column(
                                horizontalAlignment = Alignment.CenterHorizontally,
                                modifier = Modifier
                                    .clip(RoundedCornerShape(14.dp))
                                    .combinedClickable(
                                        onClick = { onAppClick(app) },
                                        onLongClick = { onAppLongClick(app) }
                                    )
                                    .padding(vertical = 4.dp, horizontal = 2.dp)
                            ) {
                                AppIconView(app = app, iconShape = iconShape, size = 52.dp, iconPackStyle = iconPackStyle)
                                if (showLabels) {
                                    Spacer(modifier = Modifier.height(6.dp))
                                    Text(
                                        text = app.displayLabel,
                                        color = Color.White,
                                        fontSize = 11.sp,
                                        maxLines = 1,
                                        overflow = TextOverflow.Ellipsis,
                                        textAlign = TextAlign.Center
                                    )
                                }
                            }
                        }
                    }

                    // Alphabet index fast-scroll rail
                    if (alphabetLetters.size > 3 && searchQuery.isBlank() && sortMode == "ALPHABETICAL") {
                        Column(
                            modifier = Modifier
                                .fillMaxHeight()
                                .width(22.dp)
                                .padding(end = 4.dp),
                            verticalArrangement = Arrangement.Center,
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            alphabetLetters.forEach { letter ->
                                Text(
                                    text = letter,
                                    color = Color(0xFF94A3B8),
                                    fontSize = 10.sp,
                                    fontWeight = FontWeight.Bold,
                                    modifier = Modifier
                                        .clickable {
                                            val index = apps.indexOfFirst {
                                                it.displayLabel.startsWith(letter, ignoreCase = true)
                                            }
                                            if (index >= 0) {
                                                coroutineScope.launch {
                                                    gridState.animateScrollToItem(index)
                                                }
                                            }
                                        }
                                        .padding(vertical = 1.dp)
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}
