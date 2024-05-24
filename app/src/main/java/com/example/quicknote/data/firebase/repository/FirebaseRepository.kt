package com.example.quicknote.data.firebase.repository

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.example.quicknote.domain.model.Note
import com.google.android.gms.tasks.OnFailureListener
import com.google.android.gms.tasks.OnSuccessListener
import com.google.firebase.database.*
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import javax.inject.Inject

interface FirebaseRepository {
    fun fetchNotes(): Flow<List<Note>>
    fun getNote( id: String): Flow<Note>
    fun updateNote(id: String, title: String, content: String)
    fun createNote(title: String , content: String)
    fun deleteNote (id: String)
}


class FirebaseRepositoryImpl @Inject constructor(): FirebaseRepository {
    private val database = FirebaseDatabase.getInstance()
    private val reference = database.getReference("memos")

    override fun fetchNotes(): Flow<List<Note>> = callbackFlow {
        val listener = object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                val notes = snapshot.children.mapNotNull { child ->
                    child.getValue(Note::class.java)?.copy(id = child.key ?: "")
                }
                trySend(notes).isSuccess
            }

            override fun onCancelled(databaseError: DatabaseError) {
                close(databaseError.toException())
            }
        }

        reference.addValueEventListener(listener)
        awaitClose { reference.removeEventListener(listener) }
    }

    override fun getNote(id: String): Flow<Note> = callbackFlow {
        val successListener = OnSuccessListener<DataSnapshot> { dataSnapshot ->
            val note = dataSnapshot.getValue(Note::class.java)?.copy(id = dataSnapshot.key ?: "")
            if (note != null) {
                trySend(note).isSuccess
            }
        }
        val failureListener = OnFailureListener {
            close(it)
        }
        reference.child(id).get().addOnSuccessListener(successListener).addOnFailureListener(failureListener)

        awaitClose {
            // ここにはリソースのクリーンアップが必要であれば記述します。
        }
    }

    override fun updateNote(id: String, title: String, content: String) {
        reference.child(id).child("title").setValue(title)
        reference.child(id).child("content").setValue(content)
    }

    override fun createNote( title: String, content: String) {
        val key = reference.push().key
        reference.child(key!!).child("title").setValue(title)
        reference.child(key).child("content").setValue(content)
    }

    override fun deleteNote (id: String) {
        reference.child(id).removeValue()
    }
}

