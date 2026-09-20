package tj.constitution.book.data

/** A single article of the Constitution. */
data class Article(
    val id: String,
    val number: Int,
    val chapterId: String,
    val text: String
)

/** A chapter grouping a contiguous range of articles. */
data class Chapter(
    val id: String,
    val number: Int,
    val title: String,
    val articleIds: List<String>
)

/** The full structured document, parsed once from assets/constitution.json. */
data class ConstitutionDocument(
    val documentTitle: String,
    val preamble: String,
    val chapters: List<Chapter>,
    val articles: List<Article>
) {
    private val articlesById = articles.associateBy { it.id }
    private val chaptersById = chapters.associateBy { it.id }

    fun articleFor(id: String): Article? = articlesById[id]
    fun chapterFor(id: String): Chapter? = chaptersById[id]
    fun chapterForArticleNumber(number: Int): Chapter? =
        chapters.firstOrNull { chapter -> articles.any { it.chapterId == chapter.id && it.number == number } }
}
