package tj.constitution.book.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Bookmark
import androidx.compose.material.icons.filled.BookmarkBorder
import androidx.compose.material.icons.filled.ChevronLeft
import androidx.compose.material.icons.filled.ChevronRight
import androidx.compose.material.icons.filled.List
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.CompositingStrategy
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import kotlinx.coroutines.launch
import tj.constitution.book.AppViewModel
import tj.constitution.book.data.FontSize
import tj.constitution.book.data.LineHeight
import tj.constitution.book.data.PageBlock

@Composable
fun ReaderScreen(
    viewModel: AppViewModel,
    startPage: Int,
    onBack: () -> Unit,
    onOpenToc: () -> Unit,
    onOpenSearch: () -> Unit,
    onOpenBookmarks: () -> Unit,
    onOpenSettings: () -> Unit
) {
    val pages by viewModel.pages.collectAsState()
    val settings by viewModel.settings.collectAsState()
    val bookmarks by viewModel.bookmarks.collectAsState()
    val safeStart = startPage.coerceIn(0, (pages.size - 1).coerceAtLeast(0))
    val pagerState = rememberPagerState(initialPage = safeStart) { pages.size }
    val scope = rememberCoroutineScopeSafe()

    // Persist reading position as the user turns pages.
    LaunchedEffect(pagerState.currentPage) {
        viewModel.saveLastPage(pagerState.currentPage)
    }

    Column(modifier = Modifier.fillMaxSize()) {
        ReaderTopBar(
            onBack = onBack,
            onOpenToc = onOpenToc,
            onOpenSearch = onOpenSearch,
            onOpenSettings = onOpenSettings,
            isBookmarked = bookmarks.contains(pagerState.currentPage),
            onToggleBookmark = { viewModel.toggleBookmark(pagerState.currentPage) }
        )

        LinearProgressIndicator(
            progress = { if (pages.isEmpty()) 0f else (pagerState.currentPage + 1) / pages.size.toFloat() },
            modifier = Modifier.fillMaxWidth()
        )

        Box(modifier = Modifier.weight(1f)) {
            HorizontalPager(
                state = pagerState,
                modifier = Modifier.fillMaxSize()
            ) { pageIndex ->
                val page = pages.getOrNull(pageIndex)
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .pageFlipTransform(pagerState, pageIndex)
                ) {
                    if (page != null) {
                        PageContent(blocks = page.blocks, fontSize = settings.fontSize, lineHeight = settings.lineHeight)
                    }
                }
            }

            // Tap zones + arrow buttons for non-swipe navigation.
            Row(modifier = Modifier.fillMaxSize()) {
                Box(
                    modifier = Modifier
                        .weight(1f)
                        .fillMaxSize()
                        .clickableGoTo(scope, pagerState, pagerState.currentPage - 1),
                    contentAlignment = Alignment.CenterStart
                ) {}
                Box(
                    modifier = Modifier
                        .weight(1f)
                        .fillMaxSize()
                        .clickableGoTo(scope, pagerState, pagerState.currentPage + 1),
                    contentAlignment = Alignment.CenterEnd
                ) {}
            }
        }

        ReaderFooter(
            currentPage = pagerState.currentPage,
            totalPages = pages.size,
            onPrev = { scope.launch { if (pagerState.currentPage > 0) pagerState.animateScrollToPage(pagerState.currentPage - 1) } },
            onNext = { scope.launch { if (pagerState.currentPage < pages.size - 1) pagerState.animateScrollToPage(pagerState.currentPage + 1) } }
        )
    }
}

@Composable
private fun ReaderTopBar(
    onBack: () -> Unit,
    onOpenToc: () -> Unit,
    onOpenSearch: () -> Unit,
    onOpenSettings: () -> Unit,
    isBookmarked: Boolean,
    onToggleBookmark: () -> Unit
) {
    Row(
        modifier = Modifier.fillMaxWidth().padding(horizontal = 4.dp, vertical = 2.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        IconButton(onClick = onBack) { Icon(Icons.Filled.ArrowBack, contentDescription = "Бозгашт") }
        Row {
            IconButton(onClick = onOpenSearch) { Icon(Icons.Filled.Search, contentDescription = "Ҷустуҷӯ") }
            IconButton(onClick = onOpenToc) { Icon(Icons.Filled.List, contentDescription = "Мундариҷа") }
            IconButton(onClick = onToggleBookmark) {
                Icon(
                    if (isBookmarked) Icons.Filled.Bookmark else Icons.Filled.BookmarkBorder,
                    contentDescription = "Захира",
                    tint = MaterialTheme.colorScheme.secondary
                )
            }
            IconButton(onClick = onOpenSettings) { Icon(Icons.Filled.Settings, contentDescription = "Танзимот") }
        }
    }
}

@Composable
private fun ReaderFooter(currentPage: Int, totalPages: Int, onPrev: () -> Unit, onNext: () -> Unit) {
    Row(
        modifier = Modifier.fillMaxWidth().padding(horizontal = 12.dp, vertical = 10.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        IconButton(onClick = onPrev) {
            Icon(Icons.Filled.ChevronLeft, contentDescription = "Пешина")
        }
        Text(
            text = "Саҳифа ${(currentPage + 1).coerceAtMost(totalPages)} / $totalPages",
            style = MaterialTheme.typography.labelLarge,
            fontWeight = FontWeight.Medium
        )
        IconButton(onClick = onNext) {
            Icon(Icons.Filled.ChevronRight, contentDescription = "Баъдӣ")
        }
    }
}

@Composable
private fun PageContent(blocks: List<PageBlock>, fontSize: FontSize, lineHeight: LineHeight) {
    val bodySize = when (fontSize) {
        FontSize.SMALL -> 14
        FontSize.MEDIUM -> 17
        FontSize.LARGE -> 20
        FontSize.EXTRA_LARGE -> 24
    }
    val lineMultiplier = when (lineHeight) {
        LineHeight.COMPACT -> 1.25f
        LineHeight.NORMAL -> 1.5f
        LineHeight.COMFORTABLE -> 1.8f
    }

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 26.dp, vertical = 18.dp),
        verticalArrangement = Arrangement.spacedBy(14.dp)
    ) {
        items(blocks) { block ->
            when (block) {
                is PageBlock.Heading -> Text(
                    text = block.text,
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.primary
                )
                is PageBlock.Plain -> Text(
                    text = block.text,
                    fontSize = bodySize.sp(),
                    lineHeight = (bodySize * lineMultiplier).sp()
                )
                is PageBlock.ArticleText -> {
                    val prefix = if (!block.isContinuation) "Моддаи ${block.articleNumber}. " else ""
                    Text(
                        text = prefix + block.text,
                        fontSize = bodySize.sp(),
                        lineHeight = (bodySize * lineMultiplier).sp()
                    )
                }
            }
        }
        item { Spacer(modifier = Modifier.height(24.dp)) }
    }
}

private fun Int.sp() = androidx.compose.ui.unit.TextUnit(this.toFloat(), androidx.compose.ui.unit.TextUnitType.Sp)
private fun Float.sp() = androidx.compose.ui.unit.TextUnit(this, androidx.compose.ui.unit.TextUnitType.Sp)

/**
 * Realistic book-style page-turn: as a page scrolls off-screen it rotates around its
 * vertical spine edge and darkens slightly, like paper catching a shadow mid-flip.
 */
private fun Modifier.pageFlipTransform(
    pagerState: androidx.compose.foundation.pager.PagerState,
    pageIndex: Int
): Modifier = this.graphicsLayer {
    val pageOffset = ((pagerState.currentPage - pageIndex) + pagerState.currentPageOffsetFraction)
    val absOffset = pageOffset.coerceIn(-1f, 1f)

    cameraDistance = 24f * density
    transformOrigin = if (absOffset < 0) {
        androidx.compose.ui.graphics.TransformOrigin(0f, 0.5f)
    } else {
        androidx.compose.ui.graphics.TransformOrigin(1f, 0.5f)
    }
    rotationY = -absOffset * 90f
    alpha = 1f - (kotlin.math.abs(absOffset) * 0.15f)
    compositingStrategy = CompositingStrategy.Offscreen
}

private fun Modifier.clickableGoTo(
    scope: kotlinx.coroutines.CoroutineScope,
    pagerState: androidx.compose.foundation.pager.PagerState,
    target: Int
): Modifier = this.then(
    Modifier.clickable(indication = null, interactionSource = null) {
        if (target in 0 until pagerState.pageCount) {
            scope.launch { pagerState.animateScrollToPage(target) }
        }
    }
)

@Composable
private fun rememberCoroutineScopeSafe() = androidx.compose.runtime.rememberCoroutineScope()
