package com.josuerdx.lab08

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Add
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import com.josuerdx.lab08.data.model.Task
import com.josuerdx.viewmodel.TaskViewModel
import kotlinx.coroutines.launch

@Composable
fun TaskScreen(viewModel: TaskViewModel) {
    val tasks by viewModel.tasks.collectAsState()
    val coroutineScope = rememberCoroutineScope()
    var editingTask by remember { mutableStateOf<Task?>(null) }
    var filter by remember { mutableStateOf("All") } // All, Completed, Pending
    var searchQuery by remember { mutableStateOf("") }
    var sortBy by remember { mutableStateOf("Name") } // Name, Date, Status
    var showAddTaskDialog by remember { mutableStateOf(false) }

    // Combinar búsqueda, filtrado y ordenamiento
    val finalTasks = tasks
        .filter {
            it.description.contains(searchQuery, ignoreCase = true)
        }
        .filter {
            when (filter) {
                "Completed" -> it.isCompleted
                "Pending" -> !it.isCompleted
                else -> true
            }
        }
        .sortedWith(
            when (sortBy) {
                "Name" -> compareBy { it.description }
                "Date" -> compareBy { it.id } // Asumiendo que el ID representa la fecha de creación
                "Status" -> compareBy { it.isCompleted }
                else -> compareBy { it.id }
            }
        )

    // Interfaz de usuario
    Box(modifier = Modifier.fillMaxSize()) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp)
        ) {
            // Barra de búsqueda
            TextField(
                value = searchQuery,
                onValueChange = { searchQuery = it },
                label = { Text("Buscar tareas") },
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(modifier = Modifier.height(8.dp))

            // Filtros
            FilterRow(currentFilter = filter, onFilterChange = { filter = it })

            Spacer(modifier = Modifier.height(8.dp))

            // Ordenamiento
            SortRow(currentSort = sortBy, onSortChange = { sortBy = it })

            Spacer(modifier = Modifier.height(16.dp))

            // Lista de tareas
            if (finalTasks.isEmpty()) {
                EmptyStateView()
            } else {
                LazyColumn {
                    items(finalTasks) { task ->
                        TaskItem(
                            task = task,
                            onToggleCompletion = {
                                coroutineScope.launch { viewModel.toggleTaskCompletion(task) }
                            },
                            onEditTask = {
                                editingTask = task
                            },
                            onDeleteTask = {
                                coroutineScope.launch { viewModel.deleteTask(task) }
                            }
                        )
                    }
                }
            }
        }

        // Botón flotante para agregar una nueva tarea
        FloatingActionButton(
            onClick = { showAddTaskDialog = true },
            modifier = Modifier
                .align(Alignment.BottomEnd)
                .padding(16.dp)
        ) {
            Icon(Icons.Filled.Add, contentDescription = "Agregar tarea")
        }

        // Diálogo para agregar una nueva tarea
        if (showAddTaskDialog) {
            AddTaskDialog(
                onAdd = { description ->
                    coroutineScope.launch {
                        viewModel.addTask(description)
                        showAddTaskDialog = false
                    }
                },
                onDismiss = { showAddTaskDialog = false }
            )
        }

        // Diálogo para editar una tarea
        if (editingTask != null) {
            EditTaskDialog(
                task = editingTask!!,
                onEdit = { newDescription ->
                    coroutineScope.launch {
                        viewModel.updateTask(editingTask!!, newDescription)
                        editingTask = null
                    }
                },
                onDismiss = { editingTask = null }
            )
        }
    }
}

@Composable
fun FilterRow(currentFilter: String, onFilterChange: (String) -> Unit) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceEvenly
    ) {
        FilterButton(label = "Todas", isSelected = currentFilter == "All", onClick = { onFilterChange("All") })
        FilterButton(label = "Completadas", isSelected = currentFilter == "Completed", onClick = { onFilterChange("Completed") })
        FilterButton(label = "Pendientes", isSelected = currentFilter == "Pending", onClick = { onFilterChange("Pending") })
    }
}

@Composable
fun FilterButton(label: String, isSelected: Boolean, onClick: () -> Unit) {
    Button(
        onClick = onClick,
        colors = ButtonDefaults.buttonColors(
            containerColor = if (isSelected) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.secondaryContainer
        )
    ) {
        Text(label)
    }
}

@Composable
fun SortRow(currentSort: String, onSortChange: (String) -> Unit) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceEvenly
    ) {
        SortButton(label = "Nombre", isSelected = currentSort == "Name", onClick = { onSortChange("Name") })
        SortButton(label = "Fecha", isSelected = currentSort == "Date", onClick = { onSortChange("Date") })
        SortButton(label = "Estado", isSelected = currentSort == "Status", onClick = { onSortChange("Status") })
    }
}

@Composable
fun SortButton(label: String, isSelected: Boolean, onClick: () -> Unit) {
    Button(
        onClick = onClick,
        colors = ButtonDefaults.buttonColors(
            containerColor = if (isSelected) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.secondaryContainer
        )
    ) {
        Text(label)
    }
}

@Composable
fun TaskItem(
    task: Task,
    onToggleCompletion: () -> Unit,
    onEditTask: () -> Unit,
    onDeleteTask: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(8.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            // Descripción de la tarea
            Text(
                text = task.description,
                modifier = Modifier.weight(1f),
                color = if (task.isCompleted) Color.Gray else Color.Black,
                style = MaterialTheme.typography.bodyLarge
            )

            Spacer(modifier = Modifier.width(8.dp))

            // Botón para alternar estado de completado
            Button(onClick = onToggleCompletion) {
                Text(if (task.isCompleted) "Completada" else "Pendiente")
            }

            Spacer(modifier = Modifier.width(8.dp))

            // Botón de editar
            IconButton(onClick = onEditTask) {
                Icon(Icons.Filled.Edit, contentDescription = "Editar tarea")
            }

            // Botón de eliminar
            IconButton(onClick = onDeleteTask) {
                Icon(Icons.Filled.Delete, contentDescription = "Eliminar tarea")
            }
        }
    }
}

@Composable
fun AddTaskDialog(onAdd: (String) -> Unit, onDismiss: () -> Unit) {
    var taskDescription by remember { mutableStateOf("") }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text(text = "Agregar Nueva Tarea") },
        text = {
            TextField(
                value = taskDescription,
                onValueChange = { taskDescription = it },
                label = { Text("Descripción de la tarea") },
                singleLine = true
            )
        },
        confirmButton = {
            Button(
                onClick = {
                    if (taskDescription.isNotBlank()) {
                        onAdd(taskDescription)
                    }
                }
            ) {
                Text("Agregar")
            }
        },
        dismissButton = {
            Button(onClick = onDismiss) {
                Text("Cancelar")
            }
        }
    )
}

@Composable
fun EditTaskDialog(task: Task, onEdit: (String) -> Unit, onDismiss: () -> Unit) {
    var taskDescription by remember { mutableStateOf(task.description) }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text(text = "Editar Tarea") },
        text = {
            TextField(
                value = taskDescription,
                onValueChange = { taskDescription = it },
                label = { Text("Descripción de la tarea") },
                singleLine = true
            )
        },
        confirmButton = {
            Button(
                onClick = {
                    if (taskDescription.isNotBlank()) {
                        onEdit(taskDescription)
                    }
                }
            ) {
                Text("Guardar")
            }
        },
        dismissButton = {
            Button(onClick = onDismiss) {
                Text("Cancelar")
            }
        }
    )
}

@Composable
fun EmptyStateView() {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(32.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Image(
            painter = painterResource(id = R.drawable.tasksss),
            contentDescription = "No hay tareas",
            modifier = Modifier.size(150.dp),
            contentScale = ContentScale.FillBounds
        )
        Spacer(modifier = Modifier.height(16.dp))
        Text(text = "No hay tareas hoy", style = MaterialTheme.typography.headlineSmall)
        Text(
            text = "Sal a caminar",
            style = MaterialTheme.typography.bodyMedium,
            color = Color.Gray,
            modifier = Modifier.padding(top = 15.dp)
        )
    }
}

