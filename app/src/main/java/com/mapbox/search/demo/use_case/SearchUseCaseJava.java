package com.mapbox.search.demo.use_case;

import com.mapbox.search.MapboxSearchSdk;
import com.mapbox.search.SearchEngine;
import com.mapbox.search.SearchOptions;
import com.mapbox.search.SearchRequestTask;
import com.mapbox.search.SearchSelectionCallback;
import com.mapbox.search.demo.use_case.ApiSampleUseCase;
import com.mapbox.search.result.SearchResult;
import com.mapbox.search.result.SearchSuggestion;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;

public class SearchUseCaseJava extends ApiSampleUseCase {

    private static final String SEARCH_QUERY = "Paris Eiffel Tower";

    private SearchEngine searchEngine;
    private @Nullable SearchRequestTask searchRequestTask;

    private final SearchSelectionCallback searchCallback = new SearchSelectionCallback() {

        @Override
        public void onSuggestions(@NotNull List<? extends SearchSuggestion> suggestions) {
            if (suggestions.isEmpty()) {
                printResult("No suggestions found");
            } else {
                printResult("Search suggestions: " + suggestions);

                if (isAttached()) {
                    printResult("Selecting first...");
                    searchRequestTask = searchEngine.select(suggestions.get(0), this);
                }
            }
        }

        @Override
        public void onResult(@NotNull SearchResult result) {
            printResult("Search result: " + result);
        }

        @Override
        public void onError(@NotNull Exception e) {
            printResult("Search error: " + e.getMessage());
        }
    };

    @Override
    public void onCreate() {
        searchEngine = MapboxSearchSdk.createSearchEngine();
    }

    @Override
    public void onRunSample() {
        if (searchRequestTask != null) {
            searchRequestTask.cancel();
        }

        final SearchOptions options = new SearchOptions.Builder()
                .limit(5)
                .build();
        searchRequestTask = searchEngine.search(SEARCH_QUERY, options, searchCallback);
    }

    @Override
    public void onDestroy() {
        if (searchRequestTask != null) {
            searchRequestTask.cancel();
            searchRequestTask = null;
        }
    }
}
