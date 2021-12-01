package com.mapbox.search.sample

import android.app.Application
import com.mapbox.android.core.location.LocationEngineProvider
import com.mapbox.common.TileStore
import com.mapbox.search.MapboxSearchSdk
import com.mapbox.search.OfflineSearchSettings

class SearchDemoApplication : Application() {

    override fun onCreate() {
        super.onCreate()

        MapboxSearchSdk.initialize(
            application = this,
            accessToken = BuildConfig.MAPBOX_API_TOKEN,
            locationEngine = LocationEngineProvider.getBestLocationEngine(this),
            offlineSearchSettings = OfflineSearchSettings(tileStore = TileStore.create()),
        )
    }
}
