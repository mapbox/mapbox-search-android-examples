package com.mapbox.search.sample

import android.util.Log
import com.mapbox.annotation.module.MapboxModule
import com.mapbox.annotation.module.MapboxModuleType
import com.mapbox.common.module.LibraryLoader

@MapboxModule(MapboxModuleType.CommonLibraryLoader)
class MyLibraryLoader : LibraryLoader {
    override fun load(libraryName: String) {
        Log.i("SearchApiExample", "Load native library: $libraryName")
        System.loadLibrary(libraryName)
    }
}
