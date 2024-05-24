package com.example.quicknote.ui.composables

import android.graphics.PointF
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.State
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.unit.dp
import androidx.compose.ui.zIndex
import com.example.quicknote.LocalNavController
import com.example.quicknote.domain.model.NotesOrderEntity

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun NoteCard(note: NotesOrderEntity, offset: State<PointF?>) {
    val navController = LocalNavController.current
    offset.let { 1f }.let {
        Modifier
            .padding(8.dp)
            .zIndex(it)
            .graphicsLayer {
                translationY = offset.value?.y ?: 0f
                translationX = offset.value?.x ?: 0f
            }
    }.let {
        Card(
            modifier = it,
            onClick = { navController.navigate("EditNoteScreen/${note.firebaseId}") },
            shape = MaterialTheme.shapes.medium,
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant)
        ) {
            Text(modifier = Modifier.padding(6.dp),
                text = note.title,
                style = MaterialTheme.typography.headlineSmall)
            Text(modifier = Modifier.padding(6.dp),
                text = note.content,
                style = MaterialTheme.typography.bodyMedium)
        }
    }
}
