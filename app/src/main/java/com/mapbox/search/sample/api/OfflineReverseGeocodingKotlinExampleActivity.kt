package com.mapbox.search.sample.api

import android.app.Activity
import android.os.Bundle
import android.util.Log
import com.mapbox.geojson.Point
import com.mapbox.search.AsyncOperationTask
import com.mapbox.search.CompletionCallback
import com.mapbox.search.MapboxSearchSdk
import com.mapbox.search.OfflineReverseGeoOptions
import com.mapbox.search.OfflineSearchEngine
import com.mapbox.search.OfflineTileRegion
import com.mapbox.search.ResponseInfo
import com.mapbox.search.SearchCallback
import com.mapbox.search.SearchRequestTask
import com.mapbox.search.result.SearchResult

class OfflineReverseGeocodingKotlinExampleActivity : Activity() {

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

    private val searchCallback = object : SearchCallback {

        override fun onResults(results: List<SearchResult>, responseInfo: ResponseInfo) {
            Log.i("SearchApiExample", "Results: $results")
        }

        override fun onError(e: Exception) {
            Log.i("SearchApiExample", "Search error", e)
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        searchEngine = MapboxSearchSdk.getOfflineSearchEngine()
        searchEngine.addEngineReadyCallback(engineReadyCallback)

        val dcLocation = Point.fromLngLat(-77.0339911055176, 38.899920004207516)

        Log.i("SearchApiExample", "Loading tiles...")
        tilesLoadingTask = searchEngine.loadTileRegion(
            groupId = "Washington DC",
            geometry = dcLocation,
            progressCallback = { progress ->
                Log.i("SearchApiExample", "Loading progress: $progress")
            },
            completionCallback = object : CompletionCallback<List<OfflineTileRegion>> {
                override fun onComplete(result: List<OfflineTileRegion>) {
                    Log.i("SearchApiExample", "Tiles successfully loaded")

                    searchRequestTask = searchEngine.reverseGeocoding(
                        OfflineReverseGeoOptions(center = dcLocation),
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
