package com.example.quicknote.data.notesOrderDB

import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.LiveData
import androidx.lifecycle.MediatorLiveData
import androidx.lifecycle.Observer
import com.example.quicknote.data.firebase.repository.FirebaseRepository
import com.example.quicknote.domain.model.Note
import com.example.quicknote.domain.model.NotesOrderEntity
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.combine

class DatabaseRepository(
    private val dao: Dao,
    private val firebase: FirebaseRepository
) {
    fun getAll(): Flow<List<NotesOrderEntity>> {
        return dao.getAll()
    }
    // LiveDataを返す場合、suspendは不要
    fun findOrderById(id: String): Flow<List<NotesOrderEntity>> {
        return dao.findOrderById(id)
    }

    suspend fun insert(note: NotesOrderEntity) {
        dao.insert(note)
    }

    suspend fun insertAll(notes: List<NotesOrderEntity>) {
        dao.insertAll(notes)
    }

    suspend fun updateAll(notes: List<NotesOrderEntity>) {
        dao.updateNotes(notes)
    }

    suspend fun removeAll(notes: List<NotesOrderEntity>){
        dao.removeAll(notes)
    }

    suspend fun removeById(id : Int) {
        dao.removeById(id)
    }

    suspend fun deleteAll() {
        dao.deleteAll()
    }

}

//fun endTransaction() {
//    dao.endTransaction()
//}
//
//fun setTransactionSuccessful() {
//    dao.setTransactionSuccessful()
//}
//
//fun beginTransaction() {
//    dao.beginTransaction()
//}

