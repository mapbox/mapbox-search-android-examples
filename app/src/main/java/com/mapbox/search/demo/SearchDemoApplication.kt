package com.mapbox.search.demo

import android.app.Application
import com.mapbox.search.MapboxSearchSdk
import com.mapbox.search.location.DefaultLocationProvider

class SearchDemoApplication : Application() {

    override fun onCreate() {
        super.onCreate()
        MapboxSearchSdk.initialize(this, BuildConfig.MAPBOX_API_TOKEN, DefaultLocationProvider(this))
    }
}
