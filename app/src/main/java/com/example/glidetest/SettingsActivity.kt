package com.example.glidetest

import android.content.pm.ActivityInfo
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.bumptech.glide.Glide
import com.example.glidetest.databinding.ActivitySettingsBinding

class SettingsActivity : AppCompatActivity() {
    lateinit var binding: ActivitySettingsBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        binding = ActivitySettingsBinding.inflate(layoutInflater)
        setContentView(binding.root)
        val intent = intent
        val screenOrientation = intent.getStringExtra("orientation")
        if (screenOrientation != null) getScreenOrientation(screenOrientation)

        super.onCreate(savedInstanceState)

        binding.settingsImageView.post {
            val imageFromAssets = intent.getStringExtra("assets")
            if (imageFromAssets != null) {
                Glide.with(applicationContext)
                    .asBitmap()
                    .load("file:///android_asset/$imageFromAssets")
                    .into(binding.settingsImageView)
            }
        }
    }


    private fun getScreenOrientation(screenOrientation: String) {
        if (screenOrientation.equals("landscape")) {
            requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE
        } else if (screenOrientation.equals("portrait")) {
            requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_PORTRAIT
        }
    }


}