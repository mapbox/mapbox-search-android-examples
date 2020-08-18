package com.mapbox.search.demo

import android.annotation.SuppressLint
import android.os.Bundle
import android.view.View
import android.widget.ScrollView
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.SwitchCompat
import com.mapbox.geojson.Point
import com.mapbox.search.demo.use_case.ApiSampleUseCase
import com.mapbox.search.demo.use_case.CategorySearchUseCaseJava
import com.mapbox.search.demo.use_case.CategorySearchUseCaseKotlin
import com.mapbox.search.demo.use_case.ReverseGeocodingUseCaseJava
import com.mapbox.search.demo.use_case.ReverseGeocodingUseCaseKotlin
import com.mapbox.search.demo.use_case.SearchUseCaseJava
import com.mapbox.search.demo.use_case.SearchUseCaseKotlin

class ApiSamplesActivity : AppCompatActivity() {

    private lateinit var outputScrollView: ScrollView
    private lateinit var outputTextView: TextView
    private lateinit var javaSwitch: SwitchCompat

    private val useKotlin: Boolean
        get() = !javaSwitch.isChecked

    private val sampleEnvironment = object : ApiSampleUseCase.SampleEnvironment {
        override fun requestLocation(callback: (Point) -> Unit) {
            callback(Point.fromLngLat(2.294434, 48.858349))
        }

        @SuppressLint("SetTextI18n")
        override fun showTextResult(output: String) {
            outputTextView.text = "${outputTextView.text}\n\n$output"
            outputScrollView.post {
                outputScrollView.fullScroll(View.FOCUS_DOWN)
            }
        }
    }

    private val searchUseCaseKotlin = SearchUseCaseKotlin()
    private val searchUseCaseJava = SearchUseCaseJava()
    private val categorySearchUseCaseKotlin = CategorySearchUseCaseKotlin()
    private val categorySearchUseCaseJava = CategorySearchUseCaseJava()
    private val reverseGeocodingKotlin = ReverseGeocodingUseCaseKotlin()
    private val reverseGeocodingJava = ReverseGeocodingUseCaseJava()

    private val sampleUseCases = listOf(
        searchUseCaseKotlin, searchUseCaseJava,
        categorySearchUseCaseKotlin, categorySearchUseCaseJava,
        reverseGeocodingKotlin, reverseGeocodingJava
    )

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_api_samples)

        outputScrollView = findViewById(R.id.output_scroll)
        outputTextView = findViewById(R.id.api_output)

        javaSwitch = findViewById<SwitchCompat>(R.id.java_switch).apply {
            val currentLanguageText = this@ApiSamplesActivity.findViewById<TextView>(R.id.current_language)

            fun onCheckedChanged(isChecked: Boolean) {
                if (isChecked) {
                    currentLanguageText.setText(R.string.lang_java)
                } else {
                    currentLanguageText.setText(R.string.lang_kotlin)
                }
            }

            setOnCheckedChangeListener { _, isChecked -> onCheckedChanged(isChecked) }
            onCheckedChanged(isChecked)
        }

        findViewById<View>(R.id.run_search).setOnClickListener {
            if (useKotlin) {
                searchUseCaseKotlin.onRunSample()
            } else {
                searchUseCaseJava.onRunSample()
            }
        }

        findViewById<View>(R.id.run_category_search).setOnClickListener {
            if (useKotlin) {
                categorySearchUseCaseKotlin.onRunSample()
            } else {
                categorySearchUseCaseJava.onRunSample()
            }
        }

        findViewById<View>(R.id.run_reverse_geocoding).setOnClickListener {
            if (useKotlin) {
                reverseGeocodingKotlin.onRunSample()
            } else {
                reverseGeocodingJava.onRunSample()
            }
        }

        findViewById<View>(R.id.clear_output).setOnClickListener {
            outputTextView.setText(R.string.output_view_title)
        }

        sampleUseCases.forEach { it.onCreate() }
    }

    override fun onStart() {
        super.onStart()
        sampleUseCases.forEach { it.onViewAttached(sampleEnvironment) }
    }

    override fun onPause() {
        sampleUseCases.forEach { it.onDestroy() }
        super.onPause()
    }
}
