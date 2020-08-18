package com.mapbox.search.demo.use_case;

import com.mapbox.search.CategorySearchEngine;
import com.mapbox.search.MapboxSearchSdk;
import com.mapbox.search.SearchOptions;
import com.mapbox.search.SearchRequestTask;
import com.mapbox.search.SearchCallback;
import com.mapbox.search.demo.use_case.ApiSampleUseCase;
import com.mapbox.search.result.SearchResult;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;

public class CategorySearchUseCaseJava extends ApiSampleUseCase {

    private static final String CATEGORY_CAFE = "cafe";

    private CategorySearchEngine categorySearchEngine;
    private @Nullable SearchRequestTask searchRequestTask;

    private final SearchCallback searchCallback = new SearchCallback() {

        @Override
        public void onResults(@NotNull List<? extends SearchResult> results) {
            if (results.isEmpty()) {
                printResult("No results for category " + CATEGORY_CAFE);
            } else {
                printResult("Search results for category " + CATEGORY_CAFE + ": " + results.get(0));
            }
        }

        @Override
        public void onError(@NotNull Exception e) {
            printResult("Search error: " + e.getMessage());
        }
    };

    @Override
    public void onCreate() {
        categorySearchEngine = MapboxSearchSdk.createCategorySearchEngine();
    }

    @Override
    public void onRunSample() {
        if (searchRequestTask != null) {
            searchRequestTask.cancel();
        }

        final SearchOptions options = new SearchOptions.Builder()
                .limit(1)
                .build();
        searchRequestTask = categorySearchEngine.search(CATEGORY_CAFE, options, searchCallback);
    }

    @Override
    public void onDestroy() {
        if (searchRequestTask != null) {
            searchRequestTask.cancel();
            searchRequestTask = null;
        }
    }
}
