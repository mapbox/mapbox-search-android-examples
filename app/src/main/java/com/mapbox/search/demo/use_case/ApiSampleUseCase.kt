package com.mapbox.search.demo.use_case

import com.mapbox.geojson.Point

abstract class ApiSampleUseCase {

    private lateinit var sampleEnvironment: SampleEnvironment
    protected var isAttached: Boolean = false
        private set

    abstract fun onCreate()

    fun onViewAttached(sampleEnvironment: SampleEnvironment) {
        this.sampleEnvironment = sampleEnvironment
        isAttached = true
    }

    abstract fun onRunSample()

    fun onViewDetached() {
        isAttached = false
    }

    abstract fun onDestroy()

    protected fun printResult(message: String) {
        sampleEnvironment.showTextResult(message)
    }

    protected fun requestLocation(callback: (Point) -> Unit) {
        sampleEnvironment.requestLocation(callback)
    }

    interface SampleEnvironment {
        fun requestLocation(callback: (Point) -> Unit)
        fun showTextResult(output: String)
    }
}
