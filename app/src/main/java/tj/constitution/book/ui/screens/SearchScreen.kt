package tj.constitution.book.ui.screens

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.Divider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.dp
import tj.constitution.book.AppViewModel
import tj.constitution.book.data.Paginator
import tj.constitution.book.data.SearchEngine
import tj.constitution.book.data.SearchResult
import tj.constitution.book.ui.theme.GoldAccent

@Composable
fun SearchScreen(
    viewModel: AppViewModel,
    onResultSelected: (Int) -> Unit,
    onBack: () -> Unit
) {
    var query by remember { mutableStateOf("") }
    val document = viewModel.document
    val pages by viewModel.pages.collectAsState()
    val results: List<SearchResult> = remember(query) { SearchEngine.search(document, query) }

    Column(modifier = Modifier.fillMaxSize()) {
        TopAppBar(
            title = { Text("Ҷустуҷӯ") },
            navigationIcon = { IconButton(onClick = onBack) { Icon(Icons.Filled.ArrowBack, contentDescription = null) } }
        )
        OutlinedTextField(
            value = query,
            onValueChange = { query = it },
            modifier = Modifier.fillMaxWidth().padding(horizontal = 16.dp, vertical = 8.dp),
            placeholder = { Text("Калима, ҷумла ё \u00abМоддаи 1\u00bb") },
            leadingIcon = { Icon(Icons.Filled.Search, contentDescription = null) },
            singleLine = true
        )

        if (query.isNotBlank() && results.isEmpty()) {
            Text(
                text = "Натиҷае ёфт нашуд",
                modifier = Modifier.padding(16.dp),
                color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f)
            )
        }

        LazyColumn(modifier = Modifier.fillMaxSize()) {
            items(results) { result ->
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable { onResultSelected(Paginator.pageIndexForArticle(pages, result.articleNumber)) }
                        .padding(horizontal = 16.dp, vertical = 12.dp)
                ) {
                    Text(
                        text = "Моддаи ${result.articleNumber}",
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.primary
                    )
                    if (result.chapterTitle.isNotBlank()) {
                        Text(
                            text = result.chapterTitle,
                            style = MaterialTheme.typography.labelSmall,
                            color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f)
                        )
                    }
                    Text(text = highlightedSnippet(result), style = MaterialTheme.typography.bodyMedium)
                }
                Divider()
            }
        }
    }
}

@Composable
private fun highlightedSnippet(result: SearchResult) = buildAnnotatedString {
    val s = result.snippet
    if (result.matchStart in s.indices && result.matchEnd in 0..s.length && result.matchEnd > result.matchStart) {
        append(s.substring(0, result.matchStart))
        withStyle(SpanStyle(background = GoldAccent.copy(alpha = 0.45f), fontWeight = FontWeight.Bold)) {
            append(s.substring(result.matchStart, result.matchEnd))
        }
        append(s.substring(result.matchEnd))
    } else {
        append(s)
    }
}
