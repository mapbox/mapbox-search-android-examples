package com.mapbox.search.demo.use_case;

import com.mapbox.search.ReverseGeocodingSearchEngine;
import com.mapbox.search.MapboxSearchSdk;
import com.mapbox.search.ReverseGeoOptions;
import com.mapbox.search.ReverseMode;
import com.mapbox.search.SearchRequestTask;
import com.mapbox.search.SearchCallback;
import com.mapbox.search.demo.use_case.ApiSampleUseCase;
import com.mapbox.search.result.SearchResult;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;

public class ReverseGeocodingUseCaseJava extends ApiSampleUseCase {

    private ReverseGeocodingSearchEngine reverseGeocoding;
    private @Nullable SearchRequestTask searchRequestTask;

    private final SearchCallback searchCallback = new SearchCallback() {

        @Override
        public void onResults(@NotNull List<? extends SearchResult> results) {
            if (results.isEmpty()) {
                printResult("No reverse geocoding result");
            } else {
                printResult("Reverse geocoding result: " + results.get(0));
            }
        }

        @Override
        public void onError(@NotNull Exception e) {
            printResult("Reverse geocoding error: " + e.getMessage());
        }
    };

    @Override
    public void onCreate() {
        reverseGeocoding = MapboxSearchSdk.createReverseGeocodingSearchEngine();
    }

    @Override
    public void onRunSample() {
        if (searchRequestTask != null) {
            searchRequestTask.cancel();
        }

        requestLocation(location -> {
            final ReverseGeoOptions options = new ReverseGeoOptions.Builder(location)
                    .limit(1)
                    .reverseMode(ReverseMode.DISTANCE)
                    .build();

            searchRequestTask = reverseGeocoding.search(options, searchCallback);
            return null;
        });
    }

    @Override
    public void onDestroy() {
        if (searchRequestTask != null) {
            searchRequestTask.cancel();
            searchRequestTask = null;
        }
    }
}
