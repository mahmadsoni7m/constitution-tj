package tj.constitution.book.data

import android.content.Context
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.intPreferencesKey
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.core.stringSetPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

private val Context.dataStore by preferencesDataStore(name = "reader_prefs")

enum class FontSize { SMALL, MEDIUM, LARGE, EXTRA_LARGE }
enum class LineHeight { COMPACT, NORMAL, COMFORTABLE }
enum class ReaderTheme { LIGHT, DARK, SEPIA }

data class ReaderSettings(
    val fontSize: FontSize = FontSize.MEDIUM,
    val lineHeight: LineHeight = LineHeight.NORMAL,
    val theme: ReaderTheme = ReaderTheme.SEPIA
)

/** A saved page, kept together with enough context to render a preview in the bookmarks list. */
data class SavedPage(val pageIndex: Int)

class UserDataStore(private val context: Context) {

    private object Keys {
        val FONT_SIZE = stringPreferencesKey("font_size")
        val LINE_HEIGHT = stringPreferencesKey("line_height")
        val THEME = stringPreferencesKey("theme")
        val LAST_PAGE = intPreferencesKey("last_page")
        val BOOKMARKS = stringSetPreferencesKey("bookmarked_pages")
    }

    val settingsFlow: Flow<ReaderSettings> = context.dataStore.data.map { prefs ->
        ReaderSettings(
            fontSize = prefs[Keys.FONT_SIZE]?.let { runCatching { FontSize.valueOf(it) }.getOrNull() } ?: FontSize.MEDIUM,
            lineHeight = prefs[Keys.LINE_HEIGHT]?.let { runCatching { LineHeight.valueOf(it) }.getOrNull() } ?: LineHeight.NORMAL,
            theme = prefs[Keys.THEME]?.let { runCatching { ReaderTheme.valueOf(it) }.getOrNull() } ?: ReaderTheme.SEPIA
        )
    }

    val lastPageFlow: Flow<Int> = context.dataStore.data.map { it[Keys.LAST_PAGE] ?: 0 }

    val bookmarksFlow: Flow<Set<Int>> = context.dataStore.data.map { prefs ->
        prefs[Keys.BOOKMARKS]?.mapNotNull { it.toIntOrNull() }?.toSet() ?: emptySet()
    }

    suspend fun setFontSize(value: FontSize) {
        context.dataStore.edit { it[Keys.FONT_SIZE] = value.name }
    }

    suspend fun setLineHeight(value: LineHeight) {
        context.dataStore.edit { it[Keys.LINE_HEIGHT] = value.name }
    }

    suspend fun setTheme(value: ReaderTheme) {
        context.dataStore.edit { it[Keys.THEME] = value.name }
    }

    suspend fun setLastPage(pageIndex: Int) {
        context.dataStore.edit { it[Keys.LAST_PAGE] = pageIndex }
    }

    suspend fun toggleBookmark(pageIndex: Int) {
        context.dataStore.edit { prefs ->
            val current = prefs[Keys.BOOKMARKS]?.toMutableSet() ?: mutableSetOf()
            val key = pageIndex.toString()
            if (!current.remove(key)) current.add(key)
            prefs[Keys.BOOKMARKS] = current
        }
    }
}
