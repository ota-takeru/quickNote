package com.example.quicknote.data.notesOrderDB

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.example.quicknote.domain.model.NotesOrderEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface Dao {
    @Query("SELECT * FROM note_order")
    fun getAll(): Flow<List<NotesOrderEntity>>

    @Query("SELECT * FROM note_order WHERE id = :id")
    fun findOrderById(id: String): Flow<List<NotesOrderEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(note: NotesOrderEntity)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(notes: List<NotesOrderEntity>)

    @Update
    suspend fun updateNotes(notes: List<NotesOrderEntity>)

    @Query("DELETE FROM note_order WHERE id = :id")
    suspend fun removeById(id: Int)

    @Delete
    suspend fun removeAll(notes: List<NotesOrderEntity>)

    @Query("DELETE FROM note_order")
    suspend fun deleteAll()

//    fun endTransaction()
//    fun setTransactionSuccessful()
//    fun beginTransaction()
}