package com.mapbox.search.sample.api;

import android.os.Bundle;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.mapbox.geojson.Point;
import com.mapbox.search.MapboxSearchSdk;
import com.mapbox.search.OfflineReverseGeoOptions;
import com.mapbox.search.OfflineSearchEngine;
import com.mapbox.search.ResponseInfo;
import com.mapbox.search.SearchCallback;
import com.mapbox.search.SearchRequestTask;
import com.mapbox.search.result.SearchResult;

import java.io.File;
import java.util.Collections;
import java.util.List;

public class OfflineReverseGeocodingJavaExampleActivity extends AppCompatActivity {

    private OfflineSearchEngine searchEngine;
    private SearchRequestTask searchRequestTask;

    private final SearchCallback searchCallback = new SearchCallback() {

        @Override
        public void onResults(@NonNull List<? extends SearchResult> results, @NonNull ResponseInfo responseInfo) {
            Log.i("SearchApiExample", "Results: " + results);
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

        searchRequestTask = searchEngine.reverseGeocoding(
            new OfflineReverseGeoOptions(Point.fromLngLat(13.409450, 52.520831)),
            searchCallback
        );
    }

    @Override
    protected void onDestroy() {
        searchRequestTask.cancel();
        super.onDestroy();
    }
}
