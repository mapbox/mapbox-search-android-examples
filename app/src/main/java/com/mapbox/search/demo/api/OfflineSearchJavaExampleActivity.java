package com.mapbox.search.demo.api;

import android.os.Bundle;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.mapbox.search.MapboxSearchSdk;
import com.mapbox.search.OfflineSearchEngine;
import com.mapbox.search.OfflineSearchOptions;
import com.mapbox.search.ResponseInfo;
import com.mapbox.search.SearchRequestTask;
import com.mapbox.search.SearchSelectionCallback;
import com.mapbox.search.result.SearchResult;
import com.mapbox.search.result.SearchSuggestion;

import java.io.File;
import java.util.Collections;
import java.util.List;

public class OfflineSearchJavaExampleActivity extends AppCompatActivity {

    private OfflineSearchEngine searchEngine;
    private SearchRequestTask searchRequestTask;

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

        /*
         * TODO Change function arguments to what's available on your device.
         * Make sure each region added only once.
         */
        searchEngine.addOfflineRegion(
            new File(getFilesDir(), "offline_data/germany").getPath(),
            Collections.singletonList("germany.map"),
            "germany.boundary",
            new OfflineSearchEngine.AddRegionCallback() {
                @Override
                public void onAdded() {
                    Log.i("SearchApiExample", "Offline region has been added");
                }

                @Override
                public void onError(@NonNull Exception e) {
                    Log.i("SearchApiExample", "Unable to add offline region", e);
                }
            }
        );

        final OfflineSearchOptions options = new OfflineSearchOptions.Builder()
            .build();

        searchRequestTask = searchEngine.search("Berlin", options, searchCallback);
    }

    @Override
    protected void onDestroy() {
        searchRequestTask.cancel();
        super.onDestroy();
    }
}
