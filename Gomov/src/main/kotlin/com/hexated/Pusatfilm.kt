package com.hexated

import com.lagradost.cloudstream3.Episode
import com.lagradost.cloudstream3.LoadResponse
import com.lagradost.cloudstream3.TvSeriesLoadResponse
import com.lagradost.cloudstream3.*

class Pusatfilm : Gomov() {

    override var mainUrl = "https://pf21.biz"

    override var name = "Pusatfilm"
    override val mainPage = mainPageOf(
        "trending/page/%d/" to "Trending",
        "film-terbaru/page/%d/" to "Film Terbaru",
        "country/philippines/page/%d/" to "Film Philippines",
        "series-terbaru/page/%d/" to "Series Terbaru",
        "series-netflix/page/%d/" to "Series Netflix",
        "drama-korea/page/%d/" to "Drama Korea",
        "drama-china/page/%d/" to "Drama China",
        "west-series/page/%d/" to "Drama West",
    )

    override suspend fun load(url: String): LoadResponse {
        return super.load(url).apply {
            when (this) {
                is TvSeriesLoadResponse -> {
                    val document = app.get(url).document
                    this.episodes = document.select("div.vid-episodes a, div.gmr-listseries a").map { eps ->
                        val href = fixUrl(eps.attr("href"))
                        val name = eps.attr("title")
                        val episode = "Episode\\s*(\\d+)".toRegex().find(name)?.groupValues?.get(1)
                        val season = "Season\\s*(\\d+)".toRegex().find(name)?.groupValues?.get(1)
                        Episode(
                            href,
                            name,
                            season = season?.toIntOrNull(),
                            episode = episode?.toIntOrNull(),
                        )
                    }.filter { it.episode != null }
                }
            }
        }
    }

}
