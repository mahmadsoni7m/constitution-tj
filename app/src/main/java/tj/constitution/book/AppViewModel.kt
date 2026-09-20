package tj.constitution.book

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import tj.constitution.book.data.ConstitutionDocument
import tj.constitution.book.data.ConstitutionRepository
import tj.constitution.book.data.FontSize
import tj.constitution.book.data.LineHeight
import tj.constitution.book.data.Page
import tj.constitution.book.data.Paginator
import tj.constitution.book.data.ReaderSettings
import tj.constitution.book.data.ReaderTheme
import tj.constitution.book.data.UserDataStore

class AppViewModel(application: Application) : AndroidViewModel(application) {

    private val userData = UserDataStore(application)

    val document: ConstitutionDocument = ConstitutionRepository.load(application)

    val settings: StateFlow<ReaderSettings> = userData.settingsFlow
        .stateIn(viewModelScope, SharingStarted.Eagerly, ReaderSettings())

    val bookmarks: StateFlow<Set<Int>> = userData.bookmarksFlow
        .stateIn(viewModelScope, SharingStarted.Eagerly, emptySet())

    val lastPage: StateFlow<Int> = userData.lastPageFlow
        .stateIn(viewModelScope, SharingStarted.Eagerly, 0)

    /** Pages reflow whenever font size changes; line-height only affects visual spacing, not the budget. */
    val pages: StateFlow<List<Page>> = settings
        .map { it.fontSize }
        .distinctUntilChanged()
        .map { fontSize -> Paginator.paginate(document, charsPerPageFor(fontSize)) }
        .stateIn(viewModelScope, SharingStarted.Eagerly, Paginator.paginate(document, charsPerPageFor(FontSize.MEDIUM)))

    private fun charsPerPageFor(fontSize: FontSize): Int = when (fontSize) {
        FontSize.SMALL -> 1400
        FontSize.MEDIUM -> 1100
        FontSize.LARGE -> 850
        FontSize.EXTRA_LARGE -> 620
    }

    fun setFontSize(value: FontSize) = viewModelScope.launch { userData.setFontSize(value) }
    fun setLineHeight(value: LineHeight) = viewModelScope.launch { userData.setLineHeight(value) }
    fun setTheme(value: ReaderTheme) = viewModelScope.launch { userData.setTheme(value) }
    fun saveLastPage(pageIndex: Int) = viewModelScope.launch { userData.setLastPage(pageIndex) }
    fun toggleBookmark(pageIndex: Int) = viewModelScope.launch { userData.toggleBookmark(pageIndex) }
}
