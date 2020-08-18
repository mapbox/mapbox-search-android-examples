package com.mapbox.search.demo.use_case

import com.mapbox.search.CategorySearchEngine
import com.mapbox.search.MapboxSearchSdk
import com.mapbox.search.SearchCallback
import com.mapbox.search.SearchOptions
import com.mapbox.search.SearchRequestTask
import com.mapbox.search.demo.use_case.ApiSampleUseCase
import com.mapbox.search.result.SearchResult

class CategorySearchUseCaseKotlin : ApiSampleUseCase() {

    private lateinit var categorySearchEngine: CategorySearchEngine
    private var searchRequestTask: SearchRequestTask? = null

    private val searchCallback: SearchCallback = object : SearchCallback {
        override fun onResults(results: List<SearchResult>) {
            if (results.isEmpty()) {
                printResult("No results for category $CATEGORY_CAFE")
            } else {
                printResult("Search result for category $CATEGORY_CAFE: ${results.first()}")
            }
        }

        override fun onError(e: Exception) {
            printResult("Search error: " + e.message)
        }
    }

    override fun onCreate() {
        categorySearchEngine = MapboxSearchSdk.createCategorySearchEngine()
    }

    override fun onRunSample() {
        searchRequestTask?.cancel()

        searchRequestTask = categorySearchEngine.search(
            CATEGORY_CAFE,
            SearchOptions(limit = 1),
            searchCallback
        )
    }

    override fun onDestroy() {
        searchRequestTask?.cancel()
    }

    companion object {
        private const val CATEGORY_CAFE = "cafe"
    }
}
