package com.mapbox.search.sample.api;

import android.os.Bundle;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.mapbox.geojson.Point;
import com.mapbox.search.AsyncOperationTask;
import com.mapbox.search.CompletionCallback;
import com.mapbox.search.MapboxSearchSdk;
import com.mapbox.search.OfflineSearchEngine;
import com.mapbox.search.OfflineSearchEngine.EngineReadyCallback;
import com.mapbox.search.OfflineSearchOptions;
import com.mapbox.search.OfflineTileRegion;
import com.mapbox.search.ResponseInfo;
import com.mapbox.search.SearchRequestTask;
import com.mapbox.search.SearchSelectionCallback;
import com.mapbox.search.result.SearchResult;
import com.mapbox.search.result.SearchSuggestion;

import java.util.List;

public class OfflineSearchJavaExampleActivity extends AppCompatActivity {

    private OfflineSearchEngine searchEngine;
    private AsyncOperationTask tilesLoadingTask;
    @Nullable
    private SearchRequestTask searchRequestTask;

    private final EngineReadyCallback engineReadyCallback = new EngineReadyCallback() {
        @Override
        public void onEngineReady(@NonNull List<OfflineTileRegion> offlineTileRegions) {
            Log.i("SearchApiExample", "Engine is ready, available regions: " + offlineTileRegions);
        }

        @Override
        public void onError(@NonNull Exception e) {
            Log.i("SearchApiExample", "Error during engine initialization", e);
        }
    };

    private final SearchSelectionCallback searchCallback = new SearchSelectionCallback() {

        @Override
        public void onSuggestions(@NonNull List<? extends SearchSuggestion> suggestions, @NonNull ResponseInfo responseInfo) {
            if (suggestions.isEmpty()) {
                Log.i("SearchApiExample", "No suggestions found");
            } else {
                Log.i("SearchApiExample", "Search suggestions: " + suggestions + "\nSelecting first...");
                searchRequestTask = searchEngine.select(suggestions.get(0), this);
            }
        }

        @Override
        public void onResult(@NonNull SearchSuggestion suggestion, @NonNull SearchResult result, @NonNull ResponseInfo info) {
            Log.i("SearchApiExample", "Search result: " + result);
        }

        @Override
        public void onCategoryResult(@NonNull SearchSuggestion suggestion, @NonNull List<? extends SearchResult> results, @NonNull ResponseInfo responseInfo) {
            Log.i("SearchApiExample", "Category search results: " + results);
        }

        @Override
        public void onError(@NonNull Exception e) {
            Log.i("SearchApiExample", "Search error: ", e);
        }
    };

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        searchEngine = MapboxSearchSdk.getOfflineSearchEngine();
        searchEngine.addEngineReadyCallback(engineReadyCallback);

        Log.i("SearchApiExample", "Loading tiles...");
        tilesLoadingTask = searchEngine.loadTileRegion(
            "Washington DC",
            Point.fromLngLat(-77.0339911055176, 38.899920004207516),
            new CompletionCallback<List<OfflineTileRegion>>() {
                @Override
                public void onComplete(List<OfflineTileRegion> result) {
                    Log.i("SearchApiExample", "Tiles successfully loaded");

                    searchRequestTask = searchEngine.search(
                        "Cafe",
                        new OfflineSearchOptions(),
                        searchCallback
                    );
                }

                @Override
                public void onError(@NonNull Exception e) {
                    Log.i("SearchApiExample", "Tiles loading error", e);
                }
            }
        );
    }

    @Override
    protected void onDestroy() {
        searchEngine.removeEngineReadyCallback(engineReadyCallback);
        tilesLoadingTask.cancel();
        if (searchRequestTask != null) {
            searchRequestTask.cancel();
        }
        super.onDestroy();
    }
}
