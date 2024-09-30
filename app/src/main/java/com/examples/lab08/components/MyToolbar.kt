package com.josuerdx.lab08.components

import androidx.compose.foundation.layout.height
import androidx.compose.material3.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MyToolbar() {
    TopAppBar(
        title = {
            Text(text = "Hoy", style = MaterialTheme.typography.titleMedium)
        },
        navigationIcon = {
            IconButton(onClick = { /* Acción del menú */ }) {
                Icon(
                    imageVector = Icons.Default.Menu,
                    contentDescription = "Menú"
                )
            }
        },
        actions = {
            IconButton(onClick = { /* Acción del menú de opciones */ }) {
                Icon(
                    imageVector = Icons.Default.MoreVert,
                    contentDescription = "Más opciones"
                )
            }
        },
        colors = TopAppBarDefaults.mediumTopAppBarColors(containerColor = Color.White),
        modifier = androidx.compose.ui.Modifier.height(56.dp)
    )
}
