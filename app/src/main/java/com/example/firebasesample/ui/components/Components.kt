package com.example.firebasesample.ui.components

import androidx.compose.material.BottomNavigation
import androidx.compose.material.BottomNavigationItem
import androidx.compose.material.Icon
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import com.example.firebasesample.SampleScreen

// Don't know why icons are invisible.. have to do it manually
@Composable
fun OverviewTabRow(
    allScreens: List<SampleScreen>,
    onTabSelected: (SampleScreen) -> Unit,
    currentScreen: SampleScreen
) {
    BottomNavigation {
        allScreens.forEach() { screen ->
            BottomNavigationItem(
                icon = {
                    Icon(
                        screen.icon, contentDescription = null,
                        tint =  if (currentScreen == screen) Color.White else Color.White.copy(alpha = 0.60f)) },
                label = {
                    Text(
                        text = screen.name,
                        color = if (currentScreen == screen) Color.White else Color.White.copy(alpha = 0.60f)) },
                selected = currentScreen == screen,
                onClick = { onTabSelected(screen) }
            )
        }
    }
}