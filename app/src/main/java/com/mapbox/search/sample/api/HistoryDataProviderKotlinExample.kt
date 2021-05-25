package com.mapbox.search.sample.api

import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import com.mapbox.search.MapboxSearchSdk.serviceProvider
import com.mapbox.search.record.HistoryRecord
import com.mapbox.search.record.IndexableDataProvider.CompletionCallback
import java.util.concurrent.Future

class HistoryDataProviderKotlinExample : AppCompatActivity() {

    private val historyDataProvider = serviceProvider.historyDataProvider()

    private lateinit var retrieveTask: Future<List<HistoryRecord>>

    private val callback: CompletionCallback<List<HistoryRecord>> = object : CompletionCallback<List<HistoryRecord>> {
        override fun onComplete(result: List<HistoryRecord>) {
            Log.i("SearchApiExample", "History records: $result")
        }

        override fun onError(e: Exception) {
            Log.i("SearchApiExample", "Unable to retrieve history records", e)
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        retrieveTask = historyDataProvider.getAll(callback)
    }

    override fun onDestroy() {
        retrieveTask.cancel(true)
        super.onDestroy()
    }
}
