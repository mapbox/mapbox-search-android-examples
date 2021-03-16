package com.mapbox.search.demo.api

import android.app.Activity
import android.os.Bundle
import android.util.Log
import com.mapbox.search.MapboxSearchSdk
import com.mapbox.search.OfflineSearchEngine
import com.mapbox.search.OfflineSearchOptions
import com.mapbox.search.ResponseInfo
import com.mapbox.search.SearchRequestTask
import com.mapbox.search.SearchSelectionCallback
import com.mapbox.search.result.SearchResult
import com.mapbox.search.result.SearchSuggestion
import java.io.File

class OfflineSearchKotlinExampleActivity : Activity() {

    private lateinit var searchEngine: OfflineSearchEngine
    private lateinit var searchRequestTask: SearchRequestTask

    private val searchCallback = object : SearchSelectionCallback {

        override fun onSuggestions(suggestions: List<SearchSuggestion>, responseInfo: ResponseInfo) {
            if (suggestions.isEmpty()) {
                Log.i("SearchApiExample", "No suggestions found")
            } else {
                Log.i("SearchApiExample", "Search suggestions: $suggestions.\nSelecting first suggestion...")
                searchRequestTask = searchEngine.select(suggestions.first(), this)
            }
        }

        override fun onResult(
            suggestion: SearchSuggestion,
            result: SearchResult,
            responseInfo: ResponseInfo
        ) {
            Log.i("SearchApiExample", "Search result: $result")
        }

        override fun onCategoryResult(
            suggestion: SearchSuggestion,
            results: List<SearchResult>,
            responseInfo: ResponseInfo
        ) {
            Log.i("SearchApiExample", "Category search results: $results")
        }

        override fun onError(e: Exception) {
            Log.i("SearchApiExample", "Search error", e)
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        searchEngine = MapboxSearchSdk.getOfflineSearchEngine()

        // Change function arguments to what's available on your device
        searchEngine.addOfflineRegion(
            path = File(filesDir, "offline_data/germany").path,
            mapsFileNames = listOf("germany.map"),
            boundaryFileName = "germany.boundary"
        )

        searchRequestTask = searchEngine.search(
            "Berlin",
            OfflineSearchOptions(),
            searchCallback
        )
    }

    override fun onDestroy() {
        searchRequestTask.cancel()
        super.onDestroy()
    }
}
