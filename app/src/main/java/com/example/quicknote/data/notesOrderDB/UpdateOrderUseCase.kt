package com.example.quicknote.data.notesOrderDB

import com.example.quicknote.domain.model.NotesOrderEntity
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.withContext


class UpdateOrderUseCase(private val repository: DatabaseRepository) {
    suspend operator fun invoke (fromIndex: Int, toIndex: Int) = withContext(Dispatchers.IO) {
            val notes = repository.getAll().first().toMutableList()
            if (fromIndex !in notes.indices || toIndex !in notes.indices) {
                println("Invalid index")
                return@withContext // Or throw an exception
            }
        notes.sortBy { it.order }
        val movedNote = notes.removeAt(fromIndex).apply {
            order = toIndex
        }
        notes.add(toIndex, movedNote)
        if (fromIndex < toIndex) {
            for (i in fromIndex until toIndex) {
                notes[i].order = i
            }
        } else {
            for (i in toIndex + 1..fromIndex) {
                notes[i].order = i
            }
        }

        val updatedNotes = notes.toList()

        repository.updateAll(updatedNotes)
    }
}