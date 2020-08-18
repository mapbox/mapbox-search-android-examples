package com.mapbox.search.demo.use_case

import com.mapbox.search.MapboxSearchSdk
import com.mapbox.search.ReverseGeoOptions
import com.mapbox.search.ReverseGeocodingSearchEngine
import com.mapbox.search.ReverseMode
import com.mapbox.search.SearchCallback
import com.mapbox.search.SearchRequestTask
import com.mapbox.search.demo.use_case.ApiSampleUseCase
import com.mapbox.search.result.SearchResult

class ReverseGeocodingUseCaseKotlin : ApiSampleUseCase() {

    private lateinit var reverseGeocoding: ReverseGeocodingSearchEngine
    private var searchRequestTask: SearchRequestTask? = null

    private val searchCallback = object : SearchCallback {
        override fun onResults(results: List<SearchResult>) {
            if (results.isEmpty()) {
                printResult("No reverse geocoding result")
            } else {
                printResult("Reverse geocoding result: ${results.first()}")
            }
        }

        override fun onError(e: Exception) {
            printResult("Reverse geocoding error: ${e.message}")
        }
    }

    override fun onCreate() {
        reverseGeocoding = MapboxSearchSdk.createReverseGeocodingSearchEngine()
    }

    override fun onRunSample() {
        searchRequestTask?.cancel()

        requestLocation { location ->
            val options = ReverseGeoOptions(
                center = location,
                limit = 1,
                reverseMode = ReverseMode.DISTANCE
            )
            searchRequestTask = reverseGeocoding.search(options, searchCallback)
        }
    }

    override fun onDestroy() {
        searchRequestTask?.cancel()
    }
}
