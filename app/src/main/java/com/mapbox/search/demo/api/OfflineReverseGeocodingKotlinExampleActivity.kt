package com.mapbox.search.demo.api

import android.app.Activity
import android.os.Bundle
import android.util.Log
import com.mapbox.geojson.Point
import com.mapbox.search.MapboxSearchSdk
import com.mapbox.search.OfflineReverseGeoOptions
import com.mapbox.search.OfflineSearchEngine
import com.mapbox.search.ResponseInfo
import com.mapbox.search.SearchCallback
import com.mapbox.search.SearchRequestTask
import com.mapbox.search.result.SearchResult
import java.io.File

class OfflineReverseGeocodingKotlinExampleActivity : Activity() {

    private lateinit var searchEngine: OfflineSearchEngine
    private lateinit var searchRequestTask: SearchRequestTask

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

        // Change function arguments to what's available on your device
        searchEngine.addOfflineRegion(
            path = File(filesDir, "offline_data/germany").path,
            mapsFileNames = listOf("germany.map"),
            boundaryFileName = "germany.boundary"
        )

        searchRequestTask = searchEngine.reverseGeocoding(
            OfflineReverseGeoOptions(center = Point.fromLngLat(13.409450, 52.520831)),
            searchCallback
        )
    }

    override fun onDestroy() {
        searchRequestTask.cancel()
        super.onDestroy()
    }
}
