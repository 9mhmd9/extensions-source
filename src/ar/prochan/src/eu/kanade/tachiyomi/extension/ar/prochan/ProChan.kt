package eu.kanade.tachiyomi.extension.ar.prochan

import eu.kanade.tachiyomi.network.GET
import eu.kanade.tachiyomi.source.model.FilterList
import eu.kanade.tachiyomi.source.model.Page
import eu.kanade.tachiyomi.source.model.SChapter
import eu.kanade.tachiyomi.source.model.SManga
import eu.kanade.tachiyomi.source.online.ParsedHttpSource
import okhttp3.Request
import org.jsoup.nodes.Document
import org.jsoup.nodes.Element

class ProChan : ParsedHttpSource() {

    override val name = "Pro Chan"
    override val baseUrl = "https://prochan.net"
    override val lang = "ar"
    override val supportsLatest = true

    override fun popularMangaRequest(page: Int): Request = GET("$baseUrl/series?page=$page", headers)
    override fun popularMangaSelector() = "div.series-card"
    override fun popularMangaFromElement(element: Element): SManga = SManga.create().apply {
        setUrlWithoutDomain(element.select("a").attr("href"))
        title = element.select("h3").text().trim()
        thumbnail_url = element.select("img").attr("abs:src")
    }
    override fun popularMangaNextPageSelector() = "ul.pagination li.next:not(.disabled)"

    override fun latestUpdatesRequest(page: Int): Request = popularMangaRequest(page)
    override fun latestUpdatesSelector() = popularMangaSelector()
    override fun latestUpdatesFromElement(element: Element): SManga = popularMangaFromElement(element)
    override fun latestUpdatesNextPageSelector() = popularMangaNextPageSelector()

    override fun searchMangaRequest(page: Int, query: String, filters: FilterList): Request {
        return GET("$baseUrl/search?q=$query&page=$page", headers)
    }
    override fun searchMangaSelector() = popularMangaSelector()
    override fun searchMangaFromElement(element: Element): SManga = popularMangaFromElement(element)
    override fun searchMangaNextPageSelector() = popularMangaNextPageSelector()

    override fun mangaDetailsParse(document: Document): SManga = SManga.create().apply {
        title = document.select("h1.title").text().trim()
        description = document.select("p.story").text().trim()
        genre = document.select("div.genres a").joinToString { it.text() }
        status = SManga.ONGOING
        thumbnail_url = document.select("div.poster img").attr("abs:src")
    }

    override fun chapterListSelector() = "div.chapters-list a"
    override fun chapterFromElement(element: Element): SChapter = SChapter.create().apply {
        setUrlWithoutDomain(element.attr("href"))
        name = element.select("span.chapter-name").text().trim()
    }

    override fun pageListParse(document: Document): List<Page> {
        return document.select("div.reader-container img, div.chapter-content img").mapIndexed { i, element ->
            Page(i, "", element.attr("abs:src"))
        }
    }

    override fun imageUrlParse(document: Document): String = throw UnsupportedOperationException("Not used")
}
