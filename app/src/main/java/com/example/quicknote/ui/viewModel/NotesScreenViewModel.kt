package com.example.quicknote.ui.viewModel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.quicknote.data.firebase.repository.FirebaseRepository
import com.example.quicknote.data.notesOrderDB.DatabaseRepository
import com.example.quicknote.data.notesOrderDB.SyncLocalDataUseCase
import com.example.quicknote.data.notesOrderDB.UpdateOrderUseCase
import com.example.quicknote.domain.model.Note
import com.example.quicknote.domain.model.NotesOrderEntity
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.SharingStarted.Companion.Lazily
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject


interface NotesScreenViewModelAbstract {
    val notes: Flow<List<Note>>
    val notesOrder: Flow<List<NotesOrderEntity>>
    fun fetchNotes()
    fun syncLocalData()
    fun insertLocalData()
    fun updateNoteOrder(from: Int, to: Int)
    fun deleteNoteOrder()
}

@HiltViewModel
class NotesScreenViewModel
@Inject constructor(
    private val firebase: FirebaseRepository,
    private val database: DatabaseRepository,
    private val syncLocalDataUseCase: SyncLocalDataUseCase,
    private val updateOrderUseCase: UpdateOrderUseCase
) : ViewModel(), NotesScreenViewModelAbstract {
    override val notes = firebase.fetchNotes()
//    override val notesOrder: StateFlow<List<NotesOrderEntity>> = database.getAll().stateIn(
//        scope = viewModelScope, // コルーチンスコープ
//        started = SharingStarted.Lazily, // 遅延開始
//        initialValue = emptyList()
//    )
    override val notesOrder = database.getAll()

    override fun fetchNotes() {
        TODO("Not yet implemented")
    }

    override fun deleteNoteOrder() {
        viewModelScope.launch {
            database.deleteAll()
        }
    }

    override fun syncLocalData() {
        viewModelScope.launch {
            syncLocalDataUseCase()
        }
    }
    override fun insertLocalData() {
        viewModelScope.launch {
            database.insert(NotesOrderEntity(
                id = 1,
                firebaseId = "idCreateNote",
                title = "",
                content = ""
            ))
        }
    }

    override fun updateNoteOrder(from: Int, to: Int) {
        viewModelScope.launch {
            updateOrderUseCase(from, to)
        }
    }

}



