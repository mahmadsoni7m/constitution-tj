package tj.constitution.book.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.MenuBook
import androidx.compose.material.icons.filled.Bookmarks
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.List
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import tj.constitution.book.AppViewModel
import tj.constitution.book.ui.theme.GoldAccent
import tj.constitution.book.ui.theme.InkNavy

@Composable
fun HomeScreen(
    viewModel: AppViewModel,
    onOpenReader: (Int) -> Unit,
    onOpenToc: () -> Unit,
    onOpenSearch: () -> Unit,
    onOpenBookmarks: () -> Unit,
    onOpenSettings: () -> Unit,
    onOpenAbout: () -> Unit
) {
    val lastPage by viewModel.lastPage.collectAsState()
    val hasProgress = lastPage > 0

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Brush.verticalGradient(listOf(InkNavy, MaterialTheme.colorScheme.background)))
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 28.dp)
                .padding(top = 64.dp, bottom = 28.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            BookCover()

            Spacer(modifier = Modifier.height(28.dp))

            Text(
                text = "Конститутсияи Ҷумҳурии Тоҷикистон",
                style = MaterialTheme.typography.titleLarge,
                color = MaterialTheme.colorScheme.onBackground,
                textAlign = TextAlign.Center
            )
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = "Матни расмии Сарқонуни давлат",
                color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.7f),
                textAlign = TextAlign.Center
            )

            Spacer(modifier = Modifier.height(32.dp))

            Button(
                onClick = { onOpenReader(lastPage) },
                modifier = Modifier.fillMaxWidth().height(52.dp),
                shape = RoundedCornerShape(14.dp),
                colors = ButtonDefaults.buttonColors(containerColor = GoldAccent, contentColor = InkNavy)
            ) {
                Icon(Icons.AutoMirrored.Filled.MenuBook, contentDescription = null)
                Spacer(modifier = Modifier.size(8.dp))
                Text(
                    text = if (hasProgress) "Идома додани хондан" else "Хондани китоб",
                    fontWeight = FontWeight.Bold
                )
            }

            Spacer(modifier = Modifier.height(12.dp))

            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                HomeChip(icon = Icons.Filled.Search, label = "Ҷустуҷӯ", modifier = Modifier.weight(1f), onClick = onOpenSearch)
                HomeChip(icon = Icons.Filled.List, label = "Мундариҷа", modifier = Modifier.weight(1f), onClick = onOpenToc)
            }
            Spacer(modifier = Modifier.height(10.dp))
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                HomeChip(icon = Icons.Filled.Bookmarks, label = "Захирашудаҳо", modifier = Modifier.weight(1f), onClick = onOpenBookmarks)
                HomeChip(icon = Icons.Filled.Settings, label = "Танзимот", modifier = Modifier.weight(1f), onClick = onOpenSettings)
            }

            Spacer(modifier = Modifier.height(18.dp))

            OutlinedButton(onClick = onOpenAbout, modifier = Modifier.fillMaxWidth()) {
                Icon(Icons.Filled.Info, contentDescription = null, modifier = Modifier.size(18.dp))
                Spacer(modifier = Modifier.size(8.dp))
                Text("Дар бораи барнома")
            }
        }
    }
}

@Composable
private fun BookCover() {
    Box(
        modifier = Modifier
            .size(width = 190.dp, height = 260.dp)
            .background(
                Brush.linearGradient(listOf(InkNavy, Color3)),
                shape = RoundedCornerShape(10.dp)
            )
            .padding(18.dp),
        contentAlignment = Alignment.Center
    ) {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Box(
                modifier = Modifier
                    .size(52.dp)
                    .background(GoldAccent, CircleShape)
            )
            Spacer(modifier = Modifier.height(18.dp))
            Text(
                text = "КОНСТИТУТСИЯИ\nҶУМҲУРИИ\nТОҶИКИСТОН",
                color = GoldAccent,
                fontWeight = FontWeight.Bold,
                textAlign = TextAlign.Center,
                style = MaterialTheme.typography.titleMedium
            )
        }
    }
}

@Composable
private fun HomeChip(
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    label: String,
    modifier: Modifier = Modifier,
    onClick: () -> Unit
) {
    OutlinedButton(
        onClick = onClick,
        modifier = modifier.height(56.dp),
        contentPadding = PaddingValues(horizontal = 10.dp)
    ) {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Icon(icon, contentDescription = null, modifier = Modifier.size(18.dp))
            Text(text = label, style = MaterialTheme.typography.labelSmall)
        }
    }
}

private val Color3 = androidx.compose.ui.graphics.Color(0xFF2B3B52)
