package com.example.quicknote.data.notesOrderDB

import com.example.quicknote.data.firebase.repository.FirebaseRepository
import com.example.quicknote.domain.model.Note
import com.example.quicknote.domain.model.NotesOrderEntity
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock
import kotlinx.coroutines.withContext

class SyncLocalDataUseCase(private val databaseRepository: DatabaseRepository, private val firebaseRepository: FirebaseRepository) {
    private var isSyncing = false
    private val mutex = Mutex()
    suspend operator fun invoke() {
        if (isSyncing) return
        isSyncing = true
        try {
            val firebaseNotesFlow = firebaseRepository.fetchNotes()
            val localNotes = databaseRepository.getAll().first()

            firebaseNotesFlow.collect { firebaseNotes ->
                syncNotes(firebaseNotes, localNotes)
            }
        } finally {
            isSyncing = false
        }
    }

    private suspend fun syncNotes(firebaseNotes: List<Note>, localNotes: List<NotesOrderEntity>) {
        println("syncNotes")
        try {
            val (updates, newNotes, toBeDeleted) = prepareSyncData(firebaseNotes, localNotes)
            if (updates.isEmpty() && newNotes.isEmpty() && toBeDeleted.isEmpty()) {
                return
            }
            updateDatabase(updates, newNotes, toBeDeleted)
        } catch (e: Exception) {
            println("Error during sync: $e")
        }
    }

    private fun prepareSyncData(
        firebaseNotes: List<Note>,
        localNotes: List<NotesOrderEntity>
    ): Triple<List<NotesOrderEntity>, List<NotesOrderEntity>, List<NotesOrderEntity>> {
        println("prepareSyncData")
        val firebaseNotesMap = firebaseNotes.associateBy { it.id }
        val localNotesMap = localNotes.associateBy { it.firebaseId }

        val updates = mutableListOf<NotesOrderEntity>()
        val newNotes = mutableListOf<NotesOrderEntity>()
        var nextOrder = (localNotes.maxOfOrNull { it.order } ?: 0) + 1

        firebaseNotes.forEach { firebaseNote ->
            val localNote = localNotesMap[firebaseNote.id]
            if (localNote != null && (localNote.title != firebaseNote.title || localNote.content != firebaseNote.content)) {
                updates.add(localNote.copy(title = firebaseNote.title, content = firebaseNote.content))
            } else if (localNote == null) {
                newNotes.add(
                    NotesOrderEntity(
                        id = null,
                        firebaseId = firebaseNote.id,
                        title = firebaseNote.title,
                        content = firebaseNote.content,
                        order = nextOrder++
                    )
                )
            }
        }
        val toBeDeleted = localNotes.filterNot { it.firebaseId in firebaseNotesMap.keys }
        return Triple(updates, newNotes, toBeDeleted)
    }

    private suspend fun updateDatabase(
        updates: List<NotesOrderEntity>,
        newNotes: List<NotesOrderEntity>,
        toBeDeleted: List<NotesOrderEntity>
    ) {
        println("updateDatabase")
        withContext(Dispatchers.IO) {
            if (updates.isNotEmpty()) databaseRepository.updateAll(updates)
        }
        withContext(Dispatchers.IO) {
            if (newNotes.isNotEmpty()) databaseRepository.insertAll(newNotes)
        }
        withContext(Dispatchers.IO) {
            if (toBeDeleted.isNotEmpty()) databaseRepository.removeAll(toBeDeleted)
        }

    }
}