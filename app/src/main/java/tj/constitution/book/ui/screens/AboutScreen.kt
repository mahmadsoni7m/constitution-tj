package tj.constitution.book.ui.screens

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp

@Composable
fun AboutScreen(onBack: () -> Unit) {
    Column(modifier = Modifier.fillMaxSize()) {
        TopAppBar(
            title = { Text("Дар бораи барнома") },
            navigationIcon = { IconButton(onClick = onBack) { Icon(Icons.Filled.ArrowBack, contentDescription = null) } }
        )
        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
                .padding(20.dp)
        ) {
            Text(
                text = "Конститутсияи Ҷумҳурии Тоҷикистон",
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.Bold
            )
            Spacer()
            Text(text = "Нусхаи рақамии матни Конститутсия.")
            Spacer()
            Text(
                text = "Ин барнома барои хондан ва ҷустуҷӯи матни Конститутсияи Ҷумҳурии Тоҷикистон дар шакли электронии офлайн таҳия шудааст. Барнома пас аз насб бидуни пайвасти интернет пурра кор мекунад.",
                style = MaterialTheme.typography.bodyMedium
            )
            Spacer()
            Text(
                text = "Манбаи матни ҳуқуқӣ",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold
            )
            Spacer()
            Text(
                text = "Матни асосии Конститутсия аз Маркази миллии қонунгузории назди Президенти Ҷумҳурии Тоҷикистон (mmk.tj) ва пойгоҳи расмии WIPO Lex гирифта шудааст. Матн бо нусхаи расмии чопшуда муқоиса ва санҷида шудааст.",
                style = MaterialTheme.typography.bodyMedium
            )
            Spacer()
            Text(
                text = "Версияи барнома: 1.0.0",
                style = MaterialTheme.typography.labelMedium,
                color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f)
            )
        }
    }
}

@Composable
private fun Spacer() {
    androidx.compose.foundation.layout.Spacer(modifier = Modifier.padding(6.dp))
}
