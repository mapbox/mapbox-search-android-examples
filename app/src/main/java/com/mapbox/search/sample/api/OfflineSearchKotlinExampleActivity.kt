package com.mapbox.search.sample.api

import android.app.Activity
import android.os.Bundle
import android.util.Log
import com.mapbox.geojson.Point
import com.mapbox.search.AsyncOperationTask
import com.mapbox.search.CompletionCallback
import com.mapbox.search.MapboxSearchSdk
import com.mapbox.search.OfflineSearchEngine
import com.mapbox.search.OfflineSearchOptions
import com.mapbox.search.OfflineTileRegion
import com.mapbox.search.ResponseInfo
import com.mapbox.search.SearchRequestTask
import com.mapbox.search.SearchSelectionCallback
import com.mapbox.search.result.SearchResult
import com.mapbox.search.result.SearchSuggestion

class OfflineSearchKotlinExampleActivity : Activity() {

    private lateinit var searchEngine: OfflineSearchEngine
    private lateinit var tilesLoadingTask: AsyncOperationTask
    private var searchRequestTask: SearchRequestTask? = null

    private val engineReadyCallback = object : OfflineSearchEngine.EngineReadyCallback {
        override fun onEngineReady(offlineTileRegions: List<OfflineTileRegion>) {
            Log.i("SearchApiExample", "Engine is ready, available regions: $offlineTileRegions")
        }

        override fun onError(e: Exception) {
            Log.i("SearchApiExample", "Error during engine initialization", e)
        }
    }

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
        searchEngine.addEngineReadyCallback(engineReadyCallback)

        Log.i("SearchApiExample", "Loading tiles...")
        tilesLoadingTask = searchEngine.loadTileRegion(
            groupId = "Washington DC",
            geometry = Point.fromLngLat(-77.0339911055176, 38.899920004207516),
            progressCallback = { progress ->
                Log.i("SearchApiExample", "Loading progress: $progress")
            },
            completionCallback = object : CompletionCallback<List<OfflineTileRegion>> {
                override fun onComplete(result: List<OfflineTileRegion>) {
                    Log.i("SearchApiExample", "Tiles successfully loaded")

                    searchRequestTask = searchEngine.search(
                        "Cafe",
                        OfflineSearchOptions(),
                        searchCallback
                    )
                }

                override fun onError(e: Exception) {
                    Log.i("SearchApiExample", "Tiles loading error", e)
                }
            }
        )
    }

    override fun onDestroy() {
        searchEngine.removeEngineReadyCallback(engineReadyCallback)
        tilesLoadingTask.cancel()
        searchRequestTask?.cancel()
        super.onDestroy()
    }
}
