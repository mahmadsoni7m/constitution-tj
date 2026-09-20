package tj.constitution.book.ui.screens

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.selection.selectable
import androidx.compose.foundation.selection.selectableGroup
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.RadioButton
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
import tj.constitution.book.data.FontSize
import tj.constitution.book.data.LineHeight
import tj.constitution.book.data.ReaderTheme

@Composable
fun SettingsScreen(viewModel: AppViewModel, onBack: () -> Unit) {
    val settings by viewModel.settings.collectAsState()

    Column(modifier = Modifier.fillMaxSize()) {
        TopAppBar(
            title = { Text("Танзимот") },
            navigationIcon = { IconButton(onClick = onBack) { Icon(Icons.Filled.ArrowBack, contentDescription = null) } }
        )

        Column(modifier = Modifier.fillMaxSize().padding(horizontal = 20.dp)) {
            SettingsSection(title = "Андозаи ҳарф")
            SegmentedOptions(
                options = listOf(
                    FontSize.SMALL to "Хурд",
                    FontSize.MEDIUM to "Миёна",
                    FontSize.LARGE to "Калон",
                    FontSize.EXTRA_LARGE to "Хеле калон"
                ),
                selected = settings.fontSize,
                onSelect = viewModel::setFontSize
            )

            SettingsSection(title = "Фосилаи сатрҳо")
            SegmentedOptions(
                options = listOf(
                    LineHeight.COMPACT to "Фишурда",
                    LineHeight.NORMAL to "Муқаррарӣ",
                    LineHeight.COMFORTABLE to "Фарох"
                ),
                selected = settings.lineHeight,
                onSelect = viewModel::setLineHeight
            )

            SettingsSection(title = "Мавзӯъ")
            SegmentedOptions(
                options = listOf(
                    ReaderTheme.LIGHT to "\u2600 Равшан",
                    ReaderTheme.DARK to "\uD83C\uDF19 Торик",
                    ReaderTheme.SEPIA to "\uD83D\uDCDC Сепия"
                ),
                selected = settings.theme,
                onSelect = viewModel::setTheme
            )
        }
    }
}

@Composable
private fun SettingsSection(title: String) {
    Text(
        text = title,
        style = MaterialTheme.typography.titleMedium,
        fontWeight = FontWeight.Bold,
        modifier = Modifier.padding(top = 22.dp, bottom = 8.dp)
    )
}

@Composable
private fun <T> SegmentedOptions(options: List<Pair<T, String>>, selected: T, onSelect: (T) -> Unit) {
    Column(modifier = Modifier.fillMaxWidth().selectableGroup()) {
        options.forEach { (value, label) ->
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .selectable(selected = value == selected, onClick = { onSelect(value) })
                    .padding(vertical = 6.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                RadioButton(selected = value == selected, onClick = { onSelect(value) })
                Text(text = label, modifier = Modifier.padding(start = 8.dp))
            }
        }
    }
}
