package com.josuerdx.lab08

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.room.Room
import com.josuerdx.lab08.components.MyTabBarScreen
import com.josuerdx.lab08.components.MyToolbar
import com.josuerdx.lab08.data.database.TaskDatabase
import com.josuerdx.lab08.data.model.Task
import com.josuerdx.lab08.ui.theme.Lab08Theme
import com.josuerdx.viewmodel.TaskViewModel
import kotlinx.coroutines.launch

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            Lab08Theme {
                // Configurar la base de datos
                val db = Room.databaseBuilder(
                    applicationContext,
                    TaskDatabase::class.java,
                    "task_db"
                ).build()

                val taskDao = db.taskDao()
                val viewModel = TaskViewModel(taskDao)

                // Estructura principal con Toolbar y TabBar
                Scaffold(
                    topBar = { MyToolbar() },
                    floatingActionButton = { /* Puedes eliminar el FAB aquí si agregas tareas dentro de TaskScreen */ },
                    content = { paddingValues ->
                        Box(modifier = Modifier.padding(paddingValues)) {
                            MyTabBarScreen {
                                TaskScreen(viewModel)
                            }
                        }
                    }
                )
            }
        }
    }
}
