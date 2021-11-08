package com.mapbox.search.sample

import android.app.Application
import com.mapbox.common.TileStore
import com.mapbox.search.MapboxSearchSdk
import com.mapbox.search.OfflineSearchSettings
import com.mapbox.search.location.DefaultLocationProvider

class SearchDemoApplication : Application() {

    override fun onCreate() {
        super.onCreate()

        MapboxSearchSdk.initialize(
            application = this,
            accessToken = BuildConfig.MAPBOX_API_TOKEN,
            locationProvider = DefaultLocationProvider(this),
            offlineSearchSettings = OfflineSearchSettings(tileStore = TileStore.create()),
        )
    }
}
