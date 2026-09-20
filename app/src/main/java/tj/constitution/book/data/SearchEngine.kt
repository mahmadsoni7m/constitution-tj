package tj.constitution.book.data

data class SearchResult(
    val articleNumber: Int,
    val chapterTitle: String,
    val snippet: String,
    val matchStart: Int,
    val matchEnd: Int
)

object SearchEngine {

    /** Searches article text, chapter titles and "Моддаи N" style queries. */
    fun search(document: ConstitutionDocument, rawQuery: String): List<SearchResult> {
        val query = rawQuery.trim()
        if (query.length < 2) return emptyList()

        // "Моддаи 42" / "модда 42" / bare number -> jump straight to that article.
        val numberOnly = Regex("^\\d{1,3}$").matches(query)
        val moddaQuery = Regex("(?i)модда[а-яёӣўғқҳҷ]*\\s*(\\d{1,3})").find(query)
        val directArticleNumber = when {
            moddaQuery != null -> moddaQuery.groupValues[1].toIntOrNull()
            numberOnly -> query.toIntOrNull()
            else -> null
        }
        if (directArticleNumber != null) {
            val article = document.articles.firstOrNull { it.number == directArticleNumber }
            if (article != null) {
                val chapter = document.chapterFor(article.chapterId)
                return listOf(
                    SearchResult(
                        articleNumber = article.number,
                        chapterTitle = chapter?.title.orEmpty(),
                        snippet = article.text.take(160),
                        matchStart = 0,
                        matchEnd = 0
                    )
                )
            }
        }

        val needle = query.lowercase()
        val results = mutableListOf<SearchResult>()
        for (article in document.articles) {
            val haystack = article.text.lowercase()
            var idx = haystack.indexOf(needle)
            while (idx >= 0) {
                val chapter = document.chapterFor(article.chapterId)
                val start = (idx - 40).coerceAtLeast(0)
                val end = (idx + needle.length + 60).coerceAtMost(article.text.length)
                results.add(
                    SearchResult(
                        articleNumber = article.number,
                        chapterTitle = chapter?.title.orEmpty(),
                        snippet = article.text.substring(start, end),
                        matchStart = idx - start,
                        matchEnd = idx - start + needle.length
                    )
                )
                idx = haystack.indexOf(needle, idx + needle.length)
                if (results.size > 200) break // sane cap
            }
        }
        return results
    }
}
