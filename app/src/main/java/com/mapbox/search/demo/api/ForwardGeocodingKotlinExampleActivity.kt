package com.mapbox.search.demo.api

import android.app.Activity
import android.os.Bundle
import android.util.Log
import com.mapbox.search.MapboxSearchSdk
import com.mapbox.search.SearchEngine
import com.mapbox.search.SearchOptions
import com.mapbox.search.SearchRequestTask
import com.mapbox.search.SearchSelectionCallback
import com.mapbox.search.result.SearchResult
import com.mapbox.search.result.SearchSuggestion

class ForwardGeocodingKotlinExampleActivity : Activity() {

    private lateinit var searchEngine: SearchEngine
    private lateinit var searchRequestTask: SearchRequestTask

    private val searchCallback = object : SearchSelectionCallback {

        override fun onSuggestions(suggestions: List<SearchSuggestion>) {
            if (suggestions.isEmpty()) {
                Log.i("SearchApiExample", "No suggestions found")
            } else {
                Log.i("SearchApiExample", "Search suggestions: $suggestions.\nSelecting first suggestion...")
                searchRequestTask = searchEngine.select(suggestions.first(), this)
            }
        }

        override fun onResult(result: SearchResult) {
            Log.i("SearchApiExample", "Search result: $result")
        }

        override fun onCategoryResult(results: List<SearchResult>) {
            Log.i("SearchApiExample", "Category search results: $results")
        }

        override fun onError(e: Exception) {
            Log.i("SearchApiExample", "Search error", e)
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        searchEngine = MapboxSearchSdk.createSearchEngine()

        searchRequestTask = searchEngine.search(
            "Paris Eiffel Tower",
            SearchOptions(limit = 5),
            searchCallback
        )
    }

    override fun onDestroy() {
        searchRequestTask.cancel()
        super.onDestroy()
    }
}
