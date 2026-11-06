package com.tuapp.authfirebaseappvalcarcel

import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CrudScreen(onBackToHome: () -> Unit) {
    val context = LocalContext.current
    val auth = FirebaseAuth.getInstance()
    val db = FirebaseFirestore.getInstance()
    val currentUserUid = auth.currentUser?.uid ?: ""

    var title by remember { mutableStateOf("") }
    var description by remember { mutableStateOf("") }
    var taskList by remember { mutableStateOf<List<TaskItem>>(emptyList()) }
    var editingTaskId by remember { mutableStateOf<String?>(null) }
    var isLoading by remember { mutableStateOf(false) }

    // LEER DATOS EN TIEMPO REAL (Filtrados por el usuario autenticado)
    LaunchedEffect(currentUserUid) {
        if (currentUserUid.isNotEmpty()) {
            db.collection("tasks")
                .whereEqualTo("userId", currentUserUid)
                .addSnapshotListener { snapshot, e ->
                    if (e != null) return@addSnapshotListener
                    if (snapshot != null) {
                        taskList = snapshot.documents.map { doc ->
                            TaskItem(
                                id = doc.id,
                                title = doc.getString("title") ?: "",
                                description = doc.getString("description") ?: "",
                                userId = doc.getString("userId") ?: ""
                            )
                        }
                    }
                }
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = "Mis Cursos Académicos",
                        style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.Bold)
                    )
                },
                navigationIcon = {
                    IconButton(onClick = onBackToHome) {
                        Icon(
                            imageVector = Icons.Default.ArrowBack,
                            contentDescription = "Volver",
                            tint = MaterialTheme.colorScheme.primary
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.surface
                )
            )
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(MaterialTheme.colorScheme.background)
                .padding(innerPadding)
                .padding(16.dp)
        ) {
            // Formulario Card (Crear / Editar)
            Card(
                shape = RoundedCornerShape(20.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                elevation = CardDefaults.cardElevation(6.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp)
                ) {
                    Text(
                        text = if (editingTaskId == null) "Registrar Nuevo Curso" else "Actualizar Curso",
                        style = MaterialTheme.typography.titleMedium.copy(
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.primary
                        )
                    )
                    Spacer(Modifier.height(12.dp))

                    OutlinedTextField(
                        value = title,
                        onValueChange = { title = it },
                        label = { Text("Título del curso o asignatura") },
                        placeholder = { Text("Ej: Programación Móvil IV") },
                        singleLine = true,
                        shape = RoundedCornerShape(12.dp),
                        modifier = Modifier.fillMaxWidth()
                    )
                    Spacer(Modifier.height(10.dp))

                    OutlinedTextField(
                        value = description,
                        onValueChange = { description = it },
                        label = { Text("Descripción / Detalles") },
                        placeholder = { Text("Ej: Desarrollo de apps multiplataforma con Jetpack Compose") },
                        shape = RoundedCornerShape(12.dp),
                        modifier = Modifier.fillMaxWidth()
                    )
                    Spacer(Modifier.height(16.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        if (editingTaskId != null) {
                            OutlinedButton(
                                onClick = {
                                    title = ""
                                    description = ""
                                    editingTaskId = null
                                },
                                shape = RoundedCornerShape(12.dp),
                                modifier = Modifier.weight(1f)
                            ) {
                                Text("Cancelar")
                            }
                        }

                        Button(
                            onClick = {
                                if (title.isBlank() || description.isBlank()) {
                                    Toast.makeText(context, "Complete todos los campos", Toast.LENGTH_SHORT).show()
                                    return@Button
                                }

                                if (editingTaskId == null) {
                                    // CREAR
                                    isLoading = true
                                    val newTask = hashMapOf(
                                        "title" to title.trim(),
                                        "description" to description.trim(),
                                        "userId" to currentUserUid
                                    )
                                    db.collection("tasks").add(newTask)
                                        .addOnSuccessListener {
                                            isLoading = false
                                            Toast.makeText(context, "Curso creado exitosamente", Toast.LENGTH_SHORT).show()
                                            title = ""
                                            description = ""
                                        }
                                        .addOnFailureListener { e ->
                                            isLoading = false
                                            Toast.makeText(context, "Error: ${e.message}", Toast.LENGTH_LONG).show()
                                        }
                                } else {
                                    // ACTUALIZAR
                                    isLoading = true
                                    db.collection("tasks").document(editingTaskId!!).update(
                                        mapOf("title" to title.trim(), "description" to description.trim())
                                    ).addOnSuccessListener {
                                        isLoading = false
                                        Toast.makeText(context, "Curso actualizado correctamente", Toast.LENGTH_SHORT).show()
                                        title = ""
                                        description = ""
                                        editingTaskId = null
                                    }.addOnFailureListener { e ->
                                        isLoading = false
                                        Toast.makeText(context, "Error al actualizar: ${e.message}", Toast.LENGTH_LONG).show()
                                    }
                                }
                            },
                            shape = RoundedCornerShape(12.dp),
                            colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.primary),
                            modifier = Modifier.weight(2f)
                        ) {
                            Icon(
                                imageVector = if (editingTaskId == null) Icons.Default.Add else Icons.Default.Refresh,
                                contentDescription = null,
                                modifier = Modifier.size(18.dp)
                            )
                            Spacer(Modifier.width(6.dp))
                            Text(if (editingTaskId == null) "Guardar Curso" else "Actualizar Registro")
                        }
                    }
                }
            }

            Spacer(Modifier.height(16.dp))

            Text(
                text = "Lista de Cursos Registrados (${taskList.size})",
                style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                color = MaterialTheme.colorScheme.onBackground
            )
            Spacer(Modifier.height(8.dp))

            // LISTAR DATOS CON CARDS MODERNAS EN LAZYCOLUMN
            if (taskList.isEmpty()) {
                Box(
                    modifier = Modifier
                        .weight(1f)
                        .fillMaxWidth(),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = "No tienes cursos registrados aún.\n¡Agrega el primero arriba!",
                        style = MaterialTheme.typography.bodyLarge,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        textAlign = androidx.compose.ui.text.style.TextAlign.Center
                    )
                }
            } else {
                LazyColumn(
                    modifier = Modifier.weight(1f),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    items(taskList) { task ->
                        Card(
                            shape = RoundedCornerShape(16.dp),
                            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                            elevation = CardDefaults.cardElevation(4.dp),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Column(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(16.dp)
                            ) {
                                Text(
                                    text = task.title,
                                    style = MaterialTheme.typography.titleMedium.copy(
                                        fontWeight = FontWeight.Bold,
                                        color = MaterialTheme.colorScheme.primary
                                    )
                                )
                                Spacer(Modifier.height(4.dp))
                                Text(
                                    text = task.description,
                                    style = MaterialTheme.typography.bodyMedium,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                                Spacer(Modifier.height(12.dp))

                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.End,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    // Botón Editar
                                    TextButton(
                                        onClick = {
                                            title = task.title
                                            description = task.description
                                            editingTaskId = task.id
                                        }
                                    ) {
                                        Icon(
                                            imageVector = Icons.Default.Edit,
                                            contentDescription = "Editar",
                                            modifier = Modifier.size(16.dp),
                                            tint = MaterialTheme.colorScheme.primary
                                        )
                                        Spacer(Modifier.width(4.dp))
                                        Text("Editar", color = MaterialTheme.colorScheme.primary)
                                    }

                                    Spacer(Modifier.width(8.dp))

                                    // Botón Eliminar
                                    TextButton(
                                        onClick = {
                                            db.collection("tasks").document(task.id).delete()
                                                .addOnSuccessListener {
                                                    Toast.makeText(context, "Curso eliminado", Toast.LENGTH_SHORT).show()
                                                }
                                                .addOnFailureListener { e ->
                                                    Toast.makeText(context, "Error al eliminar: ${e.message}", Toast.LENGTH_SHORT).show()
                                                }
                                        }
                                    ) {
                                        Icon(
                                            imageVector = Icons.Default.Delete,
                                            contentDescription = "Eliminar",
                                            modifier = Modifier.size(16.dp),
                                            tint = MaterialTheme.colorScheme.error
                                        )
                                        Spacer(Modifier.width(4.dp))
                                        Text("Eliminar", color = MaterialTheme.colorScheme.error)
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}
