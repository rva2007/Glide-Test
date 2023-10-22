package com.example.glidetest

import android.net.Uri
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.bumptech.glide.Glide
import com.example.glidetest.databinding.ActivityMainBinding
import java.io.IOException

class MainActivity : AppCompatActivity() {
    lateinit var binding: ActivityMainBinding
    lateinit var uri: Uri

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        uri = getUriFromAssets()
        Glide.with(this)
            .load(uri)
            .into(binding.initImage)

    }

    private fun getUriFromAssets():Uri {
        try {
            val assetManager = assets
            val files = assetManager.list("img")
            val path = "file:///android_asset/img/" + (files!![4 % files.size]).toString()
            uri = Uri.parse(path)
        } catch (e: IOException) {
            e.printStackTrace()
            Toast.makeText(this, e.localizedMessage, Toast.LENGTH_SHORT).show()
        }
        return uri
    }


}