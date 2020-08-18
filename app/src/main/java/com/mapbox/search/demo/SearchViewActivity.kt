package com.mapbox.search.demo

import android.Manifest
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.content.res.Configuration
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.Toast
import androidx.appcompat.widget.Toolbar
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.mapbox.search.result.SearchResult
import com.mapbox.search.ui.view.SearchBottomSheetView
import com.mapbox.search.ui.view.category.Category
import com.mapbox.search.ui.view.category.SearchCategoriesBottomSheetView
import com.mapbox.search.ui.view.place.SearchPlace
import com.mapbox.search.ui.view.place.SearchPlaceBottomSheetView

class SearchViewActivity : AppCompatActivity() {

    private lateinit var searchBottomSheetView: SearchBottomSheetView
    private lateinit var searchPlaceView: SearchPlaceBottomSheetView
    private lateinit var searchCategoriesView: SearchCategoriesBottomSheetView

    private lateinit var cardsMediator: SearchViewBottomSheetsMediator

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_search_view)

        findViewById<Toolbar>(R.id.toolbar).apply {
            title = ""
            setSupportActionBar(this)
        }

        searchBottomSheetView = findViewById(R.id.search_view)
        val configuration = if (resources.configuration.orientation == Configuration.ORIENTATION_LANDSCAPE) {
            SearchBottomSheetView.Configuration(
                collapsedStateAnchor = SearchBottomSheetView.CollapsedStateAnchor.SEARCH_BAR
            )
        } else {
            SearchBottomSheetView.Configuration()
        }
        searchBottomSheetView.initializeSearch(savedInstanceState, configuration)
        searchBottomSheetView.isHideableByDrag = true

        searchPlaceView = findViewById(R.id.search_place_view)
        searchCategoriesView = findViewById(R.id.search_categories_view)

        cardsMediator = SearchViewBottomSheetsMediator(
            searchBottomSheetView,
            searchPlaceView,
            searchCategoriesView
        )

        savedInstanceState?.let {
            cardsMediator.onRestoreInstanceState(it)
        }

        findViewById<View>(R.id.root).setOnClickListener {
            if (searchPlaceView.isHidden() && searchCategoriesView.isHidden()) {
                if (searchBottomSheetView.isHidden()) {
                    searchBottomSheetView.open()
                } else {
                    searchBottomSheetView.hide()
                }
            }
        }

        // Process bottom sheets events
        cardsMediator.addSearchBottomSheetsEventsListener(object : SearchViewBottomSheetsMediator.SearchBottomSheetsEventsListener {
            override fun onOpenPlaceBottomSheet(place: SearchPlace) {}

            override fun onOpenCategoriesBottomSheet(category: Category) {}

            override fun onBackToMainBottomSheet() {}
        })

        searchPlaceView.addOnNavigateClickListener(object : SearchPlaceBottomSheetView.OnNavigateClickListener {
            override fun onNavigateClick(searchPlace: SearchPlace) {
                startActivity(IntentUtils.geoIntent(searchPlace.coordinate))
            }
        })

        searchPlaceView.addOnShareClickListener(object : SearchPlaceBottomSheetView.OnShareClickListener {
            override fun onShareClick(searchPlace: SearchPlace) {
                startActivity(IntentUtils.shareIntent(searchPlace))
            }
        })

        searchCategoriesView.addCategoryLoadingStateListener(object : SearchCategoriesBottomSheetView.CategoryLoadingStateListener {
            override fun onLoadingStart(category: Category) {}

            override fun onCategoryResultsLoaded(category: Category, searchResults: List<SearchResult>) {
                showToast("Loaded ${searchResults.size} results for $category")
            }

            override fun onLoadingError(category: Category) {}
        })

        if (!isPermissionGranted(Manifest.permission.ACCESS_FINE_LOCATION)) {
            ActivityCompat.requestPermissions(
                this,
                arrayOf(Manifest.permission.ACCESS_FINE_LOCATION),
                PERMISSIONS_REQUEST_LOCATION
            )
        }
    }

    override fun onBackPressed() {
        if (!cardsMediator.handleOnBackPressed()) {
            super.onBackPressed()
        }
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.main_activity_options_menu, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            R.id.open_simple_ui -> {
                startActivity(Intent(this, SimpleUiSearchActivity::class.java))
                true
            }
            R.id.open_api_samples -> {
                startActivity(Intent(this, ApiSamplesActivity::class.java))
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }

    override fun onSaveInstanceState(outState: Bundle) {
        cardsMediator.onSaveInstanceState(outState)
        super.onSaveInstanceState(outState)
    }

    private fun showToast(message: String) {
        Toast.makeText(applicationContext, message, Toast.LENGTH_SHORT).show()
    }

    private companion object {
        private const val PERMISSIONS_REQUEST_LOCATION = 0

        fun Context.isPermissionGranted(permission: String): Boolean {
            return ContextCompat.checkSelfPermission(this, permission) == PackageManager.PERMISSION_GRANTED
        }
    }
}
