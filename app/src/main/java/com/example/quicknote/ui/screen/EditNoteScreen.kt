package com.example.quicknote.ui.screen

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.detectVerticalDragGestures
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.unit.Density
import androidx.compose.ui.unit.dp
import com.example.quicknote.LocalNavController
import com.example.quicknote.domain.model.Note
import com.example.quicknote.domain.model.NotesOrderEntity
import com.example.quicknote.ui.viewModel.EditScreenViewModel
import kotlin.math.absoluteValue


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun EditNoteScreen(note: NotesOrderEntity, viewModel: EditScreenViewModel) {
    var title by remember { mutableStateOf(note.title) }
    var content by remember { mutableStateOf(note.content) }
    val navController = LocalNavController.current

    val dragAmount by remember { mutableFloatStateOf(0f) }
    val density = LocalDensity.current
    // スワイプの距離に応じて透明度を計算
    val alpha = calculateAlpha(dragAmount, density)
    // 背景の透明度を更新
    val backgroundColor = Color.Green.copy(alpha = alpha)

    Scaffold(
        topBar = {
            TopAppBar(
                title = {},
                modifier = Modifier.pointerInput(Unit) {
                    detectVerticalDragGestures { _, dragAmountChange ->
                        if (dragAmountChange > 50.dp.toPx()) { // スワイプの閾値を設定
                            navController.navigate("NotesScreen")
                        }
                    }
                },
                actions = {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(56.dp),
                        horizontalArrangement = Arrangement.Start,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        IconButton(onClick = {
                            onBackPress(note, title, content, viewModel)
                            navController.navigate("NotesScreen")
                        }) {
                            Icon(
                                Icons.Default.ArrowBack,
                                contentDescription = "Localized description",
                                modifier = Modifier.size(34.dp),
                                )
                        }
                        Spacer(modifier = Modifier.weight(1f))
                        IconButton(onClick = {
                            if(note.firebaseId != "") {
                                viewModel.deleteNote(
                                    note.firebaseId
                                )
                            }
                            navController.navigate("NotesScreen")
                        }) {
                            Icon(
                                Icons.Default.Delete,
                                contentDescription = "Localized description", modifier = Modifier.size(34.dp),
                                )
                        }
                    }
                },
            )
        }
    ) { innerPadding ->
        Box(modifier = Modifier
            .padding(innerPadding)
            .background(backgroundColor)) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(16.dp)
            ) {
                TextField(note, title, content, viewModel, onTitleChange = { title = it }, onContentChange = { content = it })
            }
        }
    }

}

//@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TextField(note: NotesOrderEntity, title: String, content: String, viewModel: EditScreenViewModel, onTitleChange: (String) -> Unit, onContentChange: (String) -> Unit) {
    val navController = LocalNavController.current

    BackHandler {
        onBackPress(note, title, content, viewModel)
        navController.popBackStack()
    }

    Column {
        OutlinedTextField(
            value = title,
            onValueChange = { onTitleChange(it)},
            label = { Text("Title") },
            modifier = Modifier
                .fillMaxWidth()
        )
        OutlinedTextField(
            value = content,
            onValueChange = { onContentChange(it)},
            label = { Text("Content") },
            modifier = Modifier
                .fillMaxWidth()
                .fillMaxHeight()
        )
    }
}

fun calculateAlpha(dragAmount: Float, density: Density): Float {
    val maxDrag = 100.dp // 最大ドラッグ量を定義
    val maxDragPx = with(density) { maxDrag.toPx() }
    return 1f - (dragAmount.absoluteValue / maxDragPx).coerceIn(0f, 1f)
}

fun onBackPress(note: NotesOrderEntity, title: String, content: String, viewModel: EditScreenViewModel) {
    if(note.firebaseId == "") {
        if(title != "" || content != ""){
            println("createNote")
            viewModel.createNote(
                title,
                content
            )
        }
    } else {
        viewModel.updateNote(
            note.firebaseId,
            title,
            content
        )
    }
}
