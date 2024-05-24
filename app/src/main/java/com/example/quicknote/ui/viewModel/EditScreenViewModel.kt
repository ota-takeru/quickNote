package com.example.quicknote.ui.viewModel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.quicknote.data.firebase.repository.FirebaseRepository
import com.example.quicknote.data.notesOrderDB.DatabaseRepository
import com.example.quicknote.domain.model.Note
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

@HiltViewModel
class EditScreenViewModel @Inject constructor(private val firebase: FirebaseRepository): ViewModel() {
    fun updateNote (id: String, title: String, content: String) {
        firebase.updateNote (id, title, content)
    }
    fun getNote (id: String): Flow<Note>{
        return firebase.getNote (id)
    }
    fun createNote (title: String, content: String) {
        firebase.createNote (title, content)
    }
    fun deleteNote (id: String) {
        firebase.deleteNote (id)
    }
}