package com.mapbox.search.demo.maps

import android.content.res.Resources
import android.graphics.Color
import android.graphics.drawable.ShapeDrawable
import android.graphics.drawable.shapes.OvalShape
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.core.graphics.drawable.DrawableCompat
import com.mapbox.geojson.Point
import com.mapbox.mapboxsdk.Mapbox
import com.mapbox.mapboxsdk.camera.CameraPosition
import com.mapbox.mapboxsdk.geometry.LatLng
import com.mapbox.mapboxsdk.geometry.LatLngBounds
import com.mapbox.mapboxsdk.maps.MapView
import com.mapbox.mapboxsdk.maps.MapboxMap
import com.mapbox.mapboxsdk.maps.Style
import com.mapbox.mapboxsdk.plugins.annotation.SymbolManager
import com.mapbox.mapboxsdk.plugins.annotation.SymbolOptions
import com.mapbox.search.demo.BuildConfig
import com.mapbox.search.demo.R
import com.mapbox.search.demo.SearchViewBottomSheetsMediator
import com.mapbox.search.result.SearchResult
import com.mapbox.search.ui.view.SearchBottomSheetView
import com.mapbox.search.ui.view.category.Category
import com.mapbox.search.ui.view.category.SearchCategoriesBottomSheetView
import com.mapbox.search.ui.view.place.SearchPlace
import com.mapbox.search.ui.view.place.SearchPlaceBottomSheetView

class MapsIntegrationExampleActivity : AppCompatActivity() {

    private lateinit var mapView: MapView
    private lateinit var mapboxMap: MapboxMap

    private lateinit var searchBottomSheetView: SearchBottomSheetView
    private lateinit var searchPlaceView: SearchPlaceBottomSheetView
    private lateinit var searchCategoriesView: SearchCategoriesBottomSheetView

    private lateinit var cardsMediator: SearchViewBottomSheetsMediator

    private lateinit var symbolManager: SymbolManager

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        Mapbox.getInstance(this, BuildConfig.MAPBOX_API_TOKEN)

        setContentView(R.layout.activity_maps_integration)

        mapView = findViewById(R.id.map_view)
        mapView.onCreate(savedInstanceState)
        mapView.getMapAsync { mapboxMap ->
            this.mapboxMap = mapboxMap
            mapboxMap.setStyle(Style.MAPBOX_STREETS) { style ->
                symbolManager = SymbolManager(mapView, mapboxMap, style)
                style.addImage(SEARCH_PIN_IMAGE_NAME, createSearchPinDrawable())
            }
        }

        searchBottomSheetView = findViewById(R.id.search_view)
        searchBottomSheetView.initializeSearch(savedInstanceState, SearchBottomSheetView.Configuration())

        searchPlaceView = findViewById<SearchPlaceBottomSheetView>(R.id.search_place_view).apply {
            isNavigateButtonVisible = false
            isShareButtonVisible = false
            isFavoriteButtonVisible = false
        }

        searchCategoriesView = findViewById(R.id.search_categories_view)

        cardsMediator = SearchViewBottomSheetsMediator(
            searchBottomSheetView,
            searchPlaceView,
            searchCategoriesView
        )

        savedInstanceState?.let {
            cardsMediator.onRestoreInstanceState(it)
        }

        cardsMediator.addSearchBottomSheetsEventsListener(object : SearchViewBottomSheetsMediator.SearchBottomSheetsEventsListener {
            override fun onOpenPlaceBottomSheet(place: SearchPlace) {
                showMarker(place.coordinate)
            }

            override fun onOpenCategoriesBottomSheet(category: Category) {}

            override fun onBackToMainBottomSheet() {
                clearMarkers()
            }
        })

        searchCategoriesView.addCategoryLoadingStateListener(object :
            SearchCategoriesBottomSheetView.CategoryLoadingStateListener {
            override fun onLoadingStart(category: Category) {}

            override fun onCategoryResultsLoaded(category: Category, searchResults: List<SearchResult>) {
                showMarkers(searchResults.mapNotNull { it.coordinate })
            }

            override fun onLoadingError(category: Category) {}
        })
    }

    override fun onBackPressed() {
        if (!cardsMediator.handleOnBackPressed()) {
            super.onBackPressed()
        }
    }

    override fun onStart() {
        super.onStart()
        mapView.onStart()
    }

    override fun onResume() {
        super.onResume()
        mapView.onResume()
    }

    override fun onPause() {
        super.onPause()
        mapView.onPause()
    }

    override fun onStop() {
        super.onStop()
        mapView.onStop()
    }

    override fun onSaveInstanceState(outState: Bundle) {
        mapView.onSaveInstanceState(outState)
        cardsMediator.onSaveInstanceState(outState)
        super.onSaveInstanceState(outState)
    }

    override fun onLowMemory() {
        super.onLowMemory()
        mapView.onLowMemory()
    }

    override fun onDestroy() {
        super.onDestroy()
        mapView.onDestroy()
    }

    private fun showMarkers(coordinates: List<Point>) {
        if (coordinates.isEmpty()) {
            clearMarkers()
            return
        } else if (coordinates.size == 1) {
            showMarker(coordinates.first())
            return
        }

        val cameraPosition = mapboxMap.getCameraForLatLngBounds(
            createLatLngBounds(coordinates),
            markersPaddings
        )

        if (cameraPosition == null) {
            clearMarkers()
            return
        }

        val symbolOptions = coordinates.map { createSymbolOptions(it) }
        showMarkers(cameraPosition, symbolOptions)
    }

    private fun showMarker(coordinate: Point) {
        val cameraPosition = CameraPosition.Builder()
            .target(coordinate.toMapCoordinate())
            .zoom(10.0)
            .build()

        showMarkers(cameraPosition, listOf(createSymbolOptions(coordinate)))
    }

    private fun showMarkers(cameraPosition: CameraPosition, symbolsOptions: List<SymbolOptions>) {
        clearMarkers()
        symbolsOptions.forEach { symbolManager.create(it) }
        mapboxMap.cameraPosition = cameraPosition
    }

    private fun clearMarkers() {
        symbolManager.deleteAll()
    }

    private companion object {

        const val SEARCH_PIN_IMAGE_NAME = "search.pin.image.name"

        private val markersPaddings: IntArray = dpToPx(64).let { mapPadding ->
            intArrayOf(mapPadding, mapPadding, mapPadding, mapPadding)
        }

        fun createLatLngBounds(coordinates: List<Point>): LatLngBounds {
            check(coordinates.size >= 2)
            return LatLngBounds.Builder()
                .includes(coordinates.map { it.toMapCoordinate() })
                .build()
        }

        fun createSearchPinDrawable(): ShapeDrawable {
            val size = dpToPx(24)
            val drawable = ShapeDrawable(OvalShape())
            drawable.intrinsicWidth = size
            drawable.intrinsicHeight = size
            DrawableCompat.setTint(drawable, Color.RED)
            return drawable
        }

        fun createSymbolOptions(coordinate: Point): SymbolOptions {
            return SymbolOptions()
                .withLatLng(coordinate.toMapCoordinate())
                .withIconImage(SEARCH_PIN_IMAGE_NAME)
                .withIconSize(1f)
                .withSymbolSortKey(10.0f)
        }

        fun dpToPx(dp: Int): Int {
            return (dp * Resources.getSystem().displayMetrics.density).toInt()
        }

        fun Point.toMapCoordinate(): LatLng = LatLng(latitude(), longitude())
    }
}
