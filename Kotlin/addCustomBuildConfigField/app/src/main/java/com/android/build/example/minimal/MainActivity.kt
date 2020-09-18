package com.android.build.example.minimal

import android.annotation.SuppressLint
import android.app.Activity
import android.os.Bundle
import android.widget.TextView

class MainActivity : Activity() {

    @SuppressLint("SetTextI18n")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        findViewById<TextView>(R.id.text_float).text = "Hello ${BuildConfig.FloatValue}"
        findViewById<TextView>(R.id.text_long).text = "Hello ${BuildConfig.LongValue}"
        findViewById<TextView>(R.id.text_variant).text = "Hello ${BuildConfig.VariantName}"
    }
}