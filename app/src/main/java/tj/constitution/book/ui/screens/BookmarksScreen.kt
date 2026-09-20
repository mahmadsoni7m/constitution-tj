package tj.constitution.book.ui.screens

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Bookmark
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.Divider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import tj.constitution.book.AppViewModel
import tj.constitution.book.data.PageBlock
import tj.constitution.book.ui.theme.GoldAccent

@Composable
fun BookmarksScreen(
    viewModel: AppViewModel,
    onPageSelected: (Int) -> Unit,
    onBack: () -> Unit
) {
    val bookmarks by viewModel.bookmarks.collectAsState()
    val pages by viewModel.pages.collectAsState()
    val sorted = bookmarks.sorted()

    Column(modifier = Modifier.fillMaxSize()) {
        TopAppBar(
            title = { Text("Саҳифаҳои захирашуда") },
            navigationIcon = { IconButton(onClick = onBack) { Icon(Icons.Filled.ArrowBack, contentDescription = null) } }
        )

        if (sorted.isEmpty()) {
            Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                Text(
                    text = "Ҳанӯз ягон саҳифа захира нашудааст",
                    color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f)
                )
            }
            return@Column
        }

        LazyColumn(modifier = Modifier.fillMaxSize()) {
            items(sorted) { pageIndex ->
                val page = pages.getOrNull(pageIndex)
                val chapterTitle = page?.chapterId?.let { viewModel.document.chapterFor(it)?.title }
                val articleLabel = page?.leadArticleNumber?.let { "Моддаи $it" }
                val preview = page?.blocks
                    ?.filterIsInstance<PageBlock.ArticleText>()
                    ?.firstOrNull()?.text
                    ?: page?.blocks?.filterIsInstance<PageBlock.Plain>()?.firstOrNull()?.text
                    ?: ""

                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable { onPageSelected(pageIndex) }
                        .padding(horizontal = 16.dp, vertical = 12.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column(modifier = Modifier.weight(1f)) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(Icons.Filled.Bookmark, contentDescription = null, tint = GoldAccent, modifier = Modifier.padding(end = 6.dp))
                            Text(text = "Саҳифа ${pageIndex + 1}", fontWeight = FontWeight.Bold)
                        }
                        if (chapterTitle != null) {
                            Text(text = chapterTitle, style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f))
                        }
                        if (articleLabel != null) {
                            Text(text = articleLabel, style = MaterialTheme.typography.labelSmall)
                        }
                        Text(
                            text = preview.take(90) + if (preview.length > 90) "\u2026" else "",
                            style = MaterialTheme.typography.bodySmall,
                            maxLines = 2
                        )
                    }
                    IconButton(onClick = { viewModel.toggleBookmark(pageIndex) }) {
                        Icon(Icons.Filled.Close, contentDescription = "Хориҷ кардан")
                    }
                }
                Divider()
            }
        }
    }
}
