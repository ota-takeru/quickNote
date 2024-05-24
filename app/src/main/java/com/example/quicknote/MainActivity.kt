package com.example.quicknote

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.compositionLocalOf

import androidx.compose.ui.Modifier
import androidx.navigation.NavController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.example.quicknote.domain.model.NotesOrderEntity
import com.example.quicknote.ui.theme.QuickNoteTheme
import com.example.quicknote.ui.screen.*
import com.example.quicknote.ui.viewModel.EditScreenViewModel
import com.example.quicknote.ui.viewModel.NotesScreenViewModel
import dagger.hilt.android.AndroidEntryPoint

val LocalNavController = compositionLocalOf<NavController> {
    error("NavController not provided")
}

@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    private val notesScreenViewModel: NotesScreenViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            QuickNoteTheme {
                val navController = rememberNavController()
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    CompositionLocalProvider(LocalNavController provides navController) {
                        NavHost(navController = navController, startDestination = "NotesScreen") {
                            composable("NotesScreen") {
                                NotesScreen(notesScreenViewModel)
                            }
                            composable(
                                "EditNoteScreen/{noteId}",
                                arguments = listOf(navArgument("noteId") {
                                    type = NavType.StringType
                                })
                            ) { backStackEntry ->
                                val noteId = backStackEntry.arguments?.getString("noteId") ?: ""
                                val editScreenViewModel: EditScreenViewModel by viewModels()
                                val note = if (noteId == "idCreateNote") {
                                  //Note()
                                    NotesOrderEntity()
                                } else {
                                    //notesScreenViewModel.notes.collectAsState(initial = emptyList()).value.find { it.id == noteId }
                                    notesScreenViewModel.notesOrder.collectAsState(initial = emptyList()).value.find { it.firebaseId == noteId }
                                }

                                note?.let {
                                    EditNoteScreen(it, editScreenViewModel)
                                }
                            }
                            // 他の画面遷移もここで定義
                        }
                    }
                }
            }
        }
    }
}

