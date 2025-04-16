package com.example.chatapp.ui.components

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.tooling.preview.Preview

@Composable
fun EmojiPicker(
    emojiList: List<String>,
    onEmojiSelected: (String) -> Unit,
    onDismiss: () -> Unit
) {
    Dialog(onDismissRequest = onDismiss) {
        Card(
            shape = MaterialTheme.shapes.extraLarge,
            elevation = CardDefaults.cardElevation(8.dp),
            modifier = Modifier.fillMaxWidth(0.9f)
        ) {
            Column(
                modifier = Modifier.padding(16.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    text = "Select an Emoji",
                    style = MaterialTheme.typography.titleLarge,
                    modifier = Modifier.padding(bottom = 16.dp)
                )

                LazyVerticalGrid(
                    columns = GridCells.Fixed(6),
                    verticalArrangement = Arrangement.spacedBy(8.dp),
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    modifier = Modifier.heightIn(max = 300.dp)
                ) {
                    items(emojiList) { emoji ->
                        Text(
                            text = emoji,
                            style = MaterialTheme.typography.bodyLarge,
                            modifier = Modifier
                                .size(48.dp)
                                .clickable {
                                    onEmojiSelected(emoji)
                                    onDismiss()
                                },
                            textAlign = TextAlign.Center
                        )
                    }
                }
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun EmojiPickerPreview() {
    val sampleEmojis = listOf(
        "😀", "😎", "🤩", "😍", "🐶", "🐱", "🌻", "🍕",
        "🚀", "🎮", "🎸", "📚", "🏀", "⚽", "🎨", "🌟"
    )

    EmojiPicker(
        emojiList = sampleEmojis,
        onEmojiSelected = { /* No-op for preview */ },
        onDismiss = { /* No-op for preview */ }
    )
}