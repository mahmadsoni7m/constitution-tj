package tj.constitution.book.data

/** One flowed page of the reader. */
data class Page(
    val index: Int,               // 0-based
    val chapterId: String?,       // null only for the very first (title/preamble) pages before chapter 1
    val isChapterStart: Boolean,  // true if this page opens a new chapter
    val leadArticleNumber: Int?,  // lowest article number that starts or continues on this page
    val blocks: List<PageBlock>
)

sealed class PageBlock {
    data class Heading(val text: String) : PageBlock()
    data class ArticleText(val articleNumber: Int, val text: String, val isContinuation: Boolean) : PageBlock()
    data class Plain(val text: String) : PageBlock()
}

/**
 * Builds a stable page list for a given "characters per page" budget.
 * The budget should be derived from the current font-size / line-height settings so that
 * larger text produces more, shorter pages - exactly like a real e-reader reflow.
 */
object Paginator {

    fun paginate(document: ConstitutionDocument, charsPerPage: Int): List<Page> {
        val pages = mutableListOf<Page>()
        var currentBlocks = mutableListOf<PageBlock>()
        var currentChars = 0
        var currentChapterId: String? = null
        var currentLeadArticle: Int? = null
        var chapterJustStarted = false

        fun flush() {
            if (currentBlocks.isNotEmpty()) {
                pages.add(
                    Page(
                        index = pages.size,
                        chapterId = currentChapterId,
                        isChapterStart = chapterJustStarted,
                        leadArticleNumber = currentLeadArticle,
                        blocks = currentBlocks.toList()
                    )
                )
            }
            currentBlocks = mutableListOf()
            currentChars = 0
            currentLeadArticle = null
            chapterJustStarted = false
        }

        // Title + preamble page(s)
        currentBlocks.add(PageBlock.Heading(document.documentTitle))
        currentChars += document.documentTitle.length
        for (chunk in splitToBudget(document.preamble, charsPerPage - currentChars)) {
            if (currentChars + chunk.length > charsPerPage && currentBlocks.isNotEmpty()) flush()
            currentBlocks.add(PageBlock.Plain(chunk))
            currentChars += chunk.length
        }
        flush()

        for (chapter in document.chapters) {
            // Force a fresh page at the start of every chapter, like a real book.
            currentChapterId = chapter.id
            chapterJustStarted = true
            currentBlocks.add(PageBlock.Heading(chapter.title))
            currentChars += chapter.title.length

            for (articleId in chapter.articleIds) {
                val article = document.articleFor(articleId) ?: continue
                var isContinuation = false
                for (chunk in splitToBudget(article.text, charsPerPage)) {
                    val prefixLen = if (!isContinuation) 12 else 0 // room for "Моддаи N. "
                    if (currentChars + chunk.length + prefixLen > charsPerPage && currentBlocks.isNotEmpty()) {
                        flush()
                        currentChapterId = chapter.id
                    }
                    if (currentLeadArticle == null) currentLeadArticle = article.number
                    currentBlocks.add(PageBlock.ArticleText(article.number, chunk, isContinuation))
                    currentChars += chunk.length + prefixLen
                    isContinuation = true
                }
            }
            flush()
        }

        return if (pages.isEmpty()) {
            listOf(Page(0, null, true, null, listOf(PageBlock.Plain(""))))
        } else {
            pages
        }
    }

    /** Breaks long text into budget-sized chunks on paragraph/sentence boundaries where possible. */
    private fun splitToBudget(text: String, budget: Int): List<String> {
        val safeBudget = budget.coerceAtLeast(200)
        if (text.length <= safeBudget) return listOf(text)

        val result = mutableListOf<String>()
        var remaining = text
        while (remaining.length > safeBudget) {
            var cut = remaining.lastIndexOf(". ", safeBudget)
            if (cut < safeBudget / 2) cut = remaining.lastIndexOf(' ', safeBudget)
            if (cut <= 0) cut = safeBudget
            val piece = remaining.substring(0, cut + 1).trimEnd()
            result.add(piece)
            remaining = remaining.substring(cut + 1).trimStart()
        }
        if (remaining.isNotEmpty()) result.add(remaining)
        return result
    }

    /** Locates the page index whose lead content covers the given article number. */
    fun pageIndexForArticle(pages: List<Page>, articleNumber: Int): Int {
        var best = 0
        for (page in pages) {
            val matches = page.blocks.any { it is PageBlock.ArticleText && it.articleNumber == articleNumber }
            if (matches) return page.index
            val lead = page.leadArticleNumber
            if (lead != null && lead <= articleNumber) best = page.index
        }
        return best
    }

    fun pageIndexForChapter(pages: List<Page>, chapterId: String): Int =
        pages.firstOrNull { it.chapterId == chapterId && it.isChapterStart }?.index ?: 0
}
