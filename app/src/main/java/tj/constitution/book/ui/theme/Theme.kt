package tj.constitution.book.ui.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Typography
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.sp
import tj.constitution.book.data.ReaderTheme

val GoldAccent = Color(0xFFD9B44A)
val InkNavy = Color(0xFF1B2430)

private val LightColors = lightColorScheme(
    primary = InkNavy,
    secondary = GoldAccent,
    background = Color(0xFFFDFDFB),
    surface = Color(0xFFFDFDFB),
    onBackground = Color(0xFF1B1B1B),
    onSurface = Color(0xFF1B1B1B)
)

private val DarkColors = darkColorScheme(
    primary = GoldAccent,
    secondary = GoldAccent,
    background = Color(0xFF121212),
    surface = Color(0xFF1A1A1A),
    onBackground = Color(0xFFEDEDED),
    onSurface = Color(0xFFEDEDED)
)

private val SepiaColors = lightColorScheme(
    primary = InkNavy,
    secondary = GoldAccent,
    background = Color(0xFFF7F0DE),
    surface = Color(0xFFF2E9D2),
    onBackground = Color(0xFF3A2E1F),
    onSurface = Color(0xFF3A2E1F)
)

private val AppTypography = Typography(
    bodyLarge = TextStyle(fontFamily = FontFamily.Serif, fontWeight = FontWeight.Normal, fontSize = 17.sp, lineHeight = 26.sp),
    titleLarge = TextStyle(fontFamily = FontFamily.Serif, fontWeight = FontWeight.Bold, fontSize = 24.sp, lineHeight = 30.sp),
    titleMedium = TextStyle(fontFamily = FontFamily.Serif, fontWeight = FontWeight.SemiBold, fontSize = 19.sp, lineHeight = 25.sp)
)

@Composable
fun KonstitutsiyaTheme(readerTheme: ReaderTheme, content: @Composable () -> Unit) {
    val colors = when (readerTheme) {
        ReaderTheme.LIGHT -> LightColors
        ReaderTheme.DARK -> DarkColors
        ReaderTheme.SEPIA -> SepiaColors
    }
    MaterialTheme(colorScheme = colors, typography = AppTypography, content = content)
}

@Composable
fun isDarkReaderTheme(readerTheme: ReaderTheme): Boolean =
    readerTheme == ReaderTheme.DARK || (readerTheme != ReaderTheme.SEPIA && readerTheme != ReaderTheme.LIGHT && isSystemInDarkTheme())
