package com.mapbox.search.demo.use_case

import com.mapbox.search.MapboxSearchSdk
import com.mapbox.search.SearchEngine
import com.mapbox.search.SearchOptions
import com.mapbox.search.SearchRequestTask
import com.mapbox.search.SearchSelectionCallback
import com.mapbox.search.demo.use_case.ApiSampleUseCase
import com.mapbox.search.result.SearchResult
import com.mapbox.search.result.SearchSuggestion

class SearchUseCaseKotlin : ApiSampleUseCase() {

    private lateinit var searchEngine: SearchEngine
    private var searchRequestTask: SearchRequestTask? = null

    private val searchCallback = object : SearchSelectionCallback {
        override fun onSuggestions(suggestions: List<SearchSuggestion>) {
            if (suggestions.isEmpty()) {
                printResult("No suggestions found")
            } else {
                printResult("Search suggestions: $suggestions")

                if (isAttached) {
                    printResult("Selecting first...")
                    searchRequestTask = searchEngine.select(suggestions.first(), this)
                }
            }
        }

        override fun onResult(result: SearchResult) {
            printResult("Search result: $result")
        }

        override fun onError(e: Exception) {
            printResult("Search error: " + e.message)
        }
    }

    override fun onCreate() {
        searchEngine = MapboxSearchSdk.createSearchEngine()
    }

    override fun onRunSample() {
        searchRequestTask?.cancel()

        searchRequestTask = searchEngine.search(
            SEARCH_QUERY,
            SearchOptions(limit = 5),
            searchCallback
        )
    }

    override fun onDestroy() {
        searchRequestTask?.cancel()
    }

    private companion object {
        private const val SEARCH_QUERY = "Paris Eiffel Tower"
    }
}
