package com.example.quicknote.ui.screen

import com.example.quicknote.ui.composables.NotesGrid
import androidx.compose.runtime.Composable
import androidx.compose.material3.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.*
import androidx.compose.foundation.lazy.grid.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.Alignment

import androidx.navigation.compose.*

import com.example.quicknote.domain.model.*
import com.example.quicknote.ui.viewModel.NotesScreenViewModel
import androidx.lifecycle.*
import com.example.quicknote.LocalNavController
import java.util.UUID


//@Preview
@Composable
fun NotesScreen(notesScreenViewModel: NotesScreenViewModel) {
    val notes by notesScreenViewModel.notes.collectAsState(initial = emptyList())
    val notesOrder by notesScreenViewModel.notesOrder.collectAsState(initial = emptyList())
    val navController = LocalNavController.current

    LaunchedEffect(Unit) {
        notesScreenViewModel.syncLocalData()
    }
    Box(modifier = Modifier.fillMaxSize()) {
        Scaffold(
            topBar = { MainTopAppBar() },
            content = { innerPadding ->
                Box(modifier = Modifier
                    .padding(innerPadding)
                    .fillMaxSize()) {
                    NotesGrid(notes = notes, notesOrder = notesOrder, notesScreenViewModel)
//                    VerticalReorderGrid()
                }
            }
        )

        // 中央下にFABを配置
        FloatingActionButton(
            onClick = { navController.navigate("EditNoteScreen/${"idCreateNote"}") },
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .padding(bottom = 32.dp)
                .width(100.dp)
                .height(72.dp)
        ) {
            Text(text = "追加")
        }

//        FloatingActionButton(
//            onClick = {
//                notesScreenViewModel.deleteNoteOrder()
//            },
//            modifier = Modifier
//                .align(Alignment.BottomStart)
//                .padding(bottom = 32.dp, start = 32.dp)
//                .width(100.dp)
//                .height(72.dp)
//        ) {
//            Text(text = "削除")
//        }

//            FloatingActionButton(
//            onClick = {
////                notesScreenViewModel.insertLocalData()
////                notesScreenViewModel.syncLocalData()
//                notesScreenViewModel.updateNoteOrder(1, 3)
////                print("order${notesOrder}")
//                      },
//            modifier = Modifier
//                .align(Alignment.BottomEnd)
//                .padding(bottom = 32.dp, end = 32.dp)
//                .width(100.dp)
//                .height(72.dp)
//        ) {
//            Text(text = "ログ")
//        }

    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MainTopAppBar() {
    TopAppBar(
        modifier = Modifier.height(50.dp),
        title = { Text(text = "QuickNote") },
        actions = {
            IconButton(onClick = { /*TODO*/ }) {
                Icon(Icons.Filled.Search, contentDescription = "Search")
            }
            IconButton(onClick = { /*TODO*/ }) {
                Icon(Icons.Filled.Settings, contentDescription = "Settings")
            }
        }
    )
}








