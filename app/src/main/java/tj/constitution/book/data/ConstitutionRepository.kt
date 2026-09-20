package tj.constitution.book.data

import android.content.Context
import org.json.JSONObject

/**
 * Loads the Constitution text from the offline asset bundle and runs the
 * data-validation checks required before the content is shown to the reader:
 * chapter ordering, article numbering, duplicate/missing articles and empty text.
 */
object ConstitutionRepository {

    private const val ASSET_NAME = "constitution.json"

    @Volatile
    private var cached: ConstitutionDocument? = null

    fun load(context: Context): ConstitutionDocument {
        cached?.let { return it }
        synchronized(this) {
            cached?.let { return it }
            val raw = context.assets.open(ASSET_NAME).bufferedReader(Charsets.UTF_8).use { it.readText() }
            val json = JSONObject(raw)

            val chaptersJson = json.getJSONArray("chapters")
            val chapters = ArrayList<Chapter>(chaptersJson.length())
            for (i in 0 until chaptersJson.length()) {
                val c = chaptersJson.getJSONObject(i)
                val idsJson = c.getJSONArray("articleIds")
                val ids = ArrayList<String>(idsJson.length())
                for (j in 0 until idsJson.length()) ids.add(idsJson.getString(j))
                chapters.add(
                    Chapter(
                        id = c.getString("id"),
                        number = c.getInt("number"),
                        title = c.getString("title"),
                        articleIds = ids
                    )
                )
            }

            val articlesJson = json.getJSONArray("articles")
            val articles = ArrayList<Article>(articlesJson.length())
            for (i in 0 until articlesJson.length()) {
                val a = articlesJson.getJSONObject(i)
                articles.add(
                    Article(
                        id = a.getString("id"),
                        number = a.getInt("number"),
                        chapterId = a.getString("chapterId"),
                        text = a.getString("text")
                    )
                )
            }

            val document = ConstitutionDocument(
                documentTitle = json.getString("documentTitle"),
                preamble = json.getString("preamble"),
                chapters = chapters.sortedBy { it.number },
                articles = articles.sortedBy { it.number }
            )

            validate(document)
            cached = document
            return document
        }
    }

    /** Article-count / numbering / duplicate / empty-text checks (see project spec §25). */
    private fun validate(document: ConstitutionDocument) {
        val numbers = document.articles.map { it.number }
        val duplicates = numbers.groupingBy { it }.eachCount().filter { it.value > 1 }.keys
        check(duplicates.isEmpty()) { "Duplicate article numbers found: $duplicates" }

        val expected = (1..document.articles.size).toSet()
        val actual = numbers.toSet()
        check(actual == expected) { "Missing or non-contiguous article numbers: ${expected - actual}" }

        val empties = document.articles.filter { it.text.isBlank() }.map { it.id }
        check(empties.isEmpty()) { "Empty article text found: $empties" }

        check(document.chapters.isNotEmpty()) { "No chapters parsed" }
        val chapterNumbers = document.chapters.map { it.number }
        check(chapterNumbers == chapterNumbers.sorted()) { "Chapters are out of order" }

        val coveredArticles = document.chapters.flatMap { it.articleIds }.toSet()
        check(coveredArticles.size == document.articles.size) { "Chapter/article mapping mismatch" }
    }
}
