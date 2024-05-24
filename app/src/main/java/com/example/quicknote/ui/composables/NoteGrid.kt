package com.example.quicknote.ui.composables

import android.graphics.PointF
import androidx.compose.foundation.gestures.detectDragGesturesAfterLongPress
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyGridState
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.rememberLazyGridState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.runtime.snapshotFlow
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.pointer.pointerInput
import com.example.quicknote.domain.model.Note
import com.example.quicknote.domain.model.NotesOrderEntity
import com.example.quicknote.ui.viewModel.NotesScreenViewModel
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.launch
import kotlin.math.absoluteValue
import kotlin.math.roundToInt

@Composable
fun NotesGrid(notes: List<Note>, notesOrder: List<NotesOrderEntity>, notesScreenViewModel: NotesScreenViewModel) {
    val sortedNotes = notesOrder.sortedBy { it.order }
    val gridState = rememberLazyGridState()
    var position by remember { mutableStateOf<PointF?>(null) }
    var draggedItem by remember { mutableStateOf<Int?>(null) }
    var isDeleteIconVisible by remember { mutableStateOf(false) }
//    var isDeleteIconVisible by remember { mutableStateOf(true) }

    val coroutineScope = rememberCoroutineScope()


    val indexWithOffset by remember{ // ドラッグ中のアイテムのインデックスとオフセットを取得して中心のオフセットを計算
        derivedStateOf {
            draggedItem.let { draggedIndex ->
                gridState.layoutInfo.visibleItemsInfo
                    .firstOrNull { it.index == draggedIndex }
                    ?.let { itemInfo ->
                        val centerOffsetX = (position?.x ?: 0f) - (itemInfo.offset.x + itemInfo.size.width / 2f)
                        val centerOffsetY = (position?.y ?: 0f) - (itemInfo.offset.y + itemInfo.size.height / 2f)
                        Pair(itemInfo.index, PointF(centerOffsetX, centerOffsetY))
                    }
            }
        }
    }

    LaunchedEffect(gridState, position) {
        combine(
            snapshotFlow { gridState.layoutInfo },
            snapshotFlow { position }.distinctUntilChanged()
        ) { state, pos ->
            pos?.let { draggedCenter ->
                state.visibleItemsInfo
                    .minByOrNull {
                        (draggedCenter.x - (it.offset.x + it.size.width / 2)).absoluteValue +
                                (draggedCenter.y - (it.offset.y + it.size.height / 2)).absoluteValue
                    }
            }?.index
        }
            .distinctUntilChanged()
            .collect { near ->
                draggedItem = when {
                    near == null -> null
                    draggedItem == null -> near
                    else -> near.also { notesScreenViewModel.updateNoteOrder(draggedItem!!, it) }
                }
            }


    }
    Box(modifier = Modifier.fillMaxSize()) {
        LazyVerticalGrid(
//            columns = GridCells.Adaptive(minSize = 128.dp),
            // 2列にする
            columns = GridCells.Fixed(2),
            state = gridState,
            modifier = Modifier
                .fillMaxSize()
//                .padding(0.dp, 40.dp, 0.dp, 0.dp)
//                .background(color = Color.Cyan)
                .pointerInput(Unit) {
                    detectDragGesturesAfterLongPress(
                        onDragStart = { offset ->
                            isDeleteIconVisible = false
                            gridState.layoutInfo.visibleItemsInfo
                                .firstOrNull {
                                    offset.x.toInt() in it.offset.x..it.offset.x + it.size.width &&
                                            offset.y.toInt() in it.offset.y..it.offset.y + it.size.height
                                }
                                ?.also {
                                    //                                position.value = PointF(
                                    position = PointF(
                                        (it.offset.x + it.size.width / 2).toFloat(),
                                        (it.offset.y + it.size.height / 2).toFloat()
                                    )
                                }
                            isDeleteIconVisible = true
                        },
                        onDrag = { change, dragAmount ->
                            position = position.let {
                                PointF(
                                    (it?.x ?: 0f) + dragAmount.x.roundToInt(),
                                    (it?.y ?: 0f) + dragAmount.y.roundToInt()
                                )
                            }

                            if ((position?.y ?: 0f) !in 0f..(size.height.toFloat()))
                                {
                                coroutineScope.launch {
                                    draggedItem?.let {
                                        gridState.scrollToItem(it, -200)
                                    }
                                }
                            }
                            change.consume()
                        },
                        onDragEnd = {
                            position = null
                            draggedItem = null
                            isDeleteIconVisible = false
                        },
                    )
                },
        ) {
            items(sortedNotes.size) { index ->
                val note = sortedNotes[index]
                val offset = remember {
                    derivedStateOf { indexWithOffset?.takeIf { it.first == index }?.second }
                }
                NoteCard(note, offset)
            }
        }
        if (isDeleteIconVisible) {
            DeleteIcon()
        }
    }


}

private fun calculateNearestItem(gridState: LazyGridState, position: PointF?): Int? {
    return gridState.layoutInfo.visibleItemsInfo
        .minByOrNull {
            ((position?.x ?: 0f) - (it.offset.x + it.size.width / 2)).absoluteValue +
                    ((position?.y ?: 0f) - (it.offset.y + it.size.height / 2)).absoluteValue
        }
        ?.index
}

private fun updateDraggedItem(near: Int?, draggedItem: MutableState<Int?>, viewModel: NotesScreenViewModel) {
    when {
        near == null -> draggedItem.value = null
        draggedItem.value == null -> draggedItem.value = near
        draggedItem.value != near -> {
            viewModel.updateNoteOrder(draggedItem.value!!, near)
            draggedItem.value = near
        }
    }
}

