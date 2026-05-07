package dev.korryr.shambaguard.navigation

import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable

@Composable
fun BottomNavBar(
    tabs: List<BottomTab>,
    currentKey: Any?,
    onTabSelected: (Any) -> Unit
) {
    NavigationBar {
        tabs.forEach { tab ->
            val isSelected = currentKey == tab.key
            NavigationBarItem(
                selected = isSelected,
                onClick = { onTabSelected(tab.key) },
                icon = {
                    Icon(
                        imageVector = if (isSelected) tab.selectedIcon else tab.unselectedIcon,
                        contentDescription = tab.title
                    )
                },
                label = { Text(tab.title) }
            )
        }
    }
}
