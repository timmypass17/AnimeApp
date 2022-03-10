package com.example.firebasesample.ui.overview

import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.tooling.preview.Preview
import kotlin.math.exp

// Top app bar: https://developer.android.com/reference/kotlin/androidx/compose/material/package-summary#TopAppBar(kotlin.Function0,androidx.compose.ui.Modifier,kotlin.Function0,kotlin.Function1,androidx.compose.ui.graphics.Color,androidx.compose.ui.graphics.Color,androidx.compose.ui.unit.Dp)
@Composable
fun OverviewBody(
    onClickLogout: () -> Unit = {}
) {
    var expanded by rememberSaveable { mutableStateOf(false) }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Overview") },
                navigationIcon = {
                    IconButton(onClick = { /* doSomething() */ }) {
                        Icon(Icons.Filled.Menu, contentDescription = null)
                    }
                },
                actions = {
                    // RowScope here, so these icons will be placed horizontally
                    IconButton(onClick = { expanded = true }) {
                        Icon(Icons.Filled.MoreVert, contentDescription = "Localized description")
                    }
                    DropdownMenu(
                        expanded = expanded,
                        onDismissRequest = { expanded = false }
                    ) {
                        DropdownMenuItem(onClick = onClickLogout) {
                            Text("Logout")
                        }
                    }
                }
            )
        },
    ) {

    }
}

@Preview
@Composable
fun PreviewOverviewBody() {
    OverviewBody()
}