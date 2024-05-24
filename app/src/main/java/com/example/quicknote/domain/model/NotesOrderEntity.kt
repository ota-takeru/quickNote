package com.example.quicknote.domain.model

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "note_order")
data class NotesOrderEntity(
    @PrimaryKey(autoGenerate = true) val id: Int? = 0,
    @ColumnInfo(name = "order") var order: Int = 0,
    @ColumnInfo(name = "firebaseId") val firebaseId: String = "",
    @ColumnInfo(name = "title") val title: String = "",
    @ColumnInfo(name = "content") val content: String = "",

    )