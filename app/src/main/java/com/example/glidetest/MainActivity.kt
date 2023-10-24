package com.example.glidetest

import android.content.Intent
import android.graphics.Bitmap
import android.net.Uri
import android.os.Bundle
import android.widget.AdapterView
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.FileProvider
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.withStarted
import com.bumptech.glide.Glide
import com.example.glidetest.databinding.ActivityMainBinding
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import java.io.File
import java.io.IOException


class MainActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMainBinding
    private lateinit var bitmap: Bitmap
    private var latestTmpUri: Uri? = null

    private val selectImageFromGalleryResult =
        registerForActivityResult(ActivityResultContracts.GetContent()) { uri: Uri? ->
            uri?.let {
                runBlocking {
                    launch(Dispatchers.IO) {
                        bitmap = Glide.with(applicationContext)
                            .asBitmap()
                            .load("$uri")
                            .submit()
                            .get()
                    }
                }

                val screenOrientation =
                    if (bitmap.width > bitmap.height) "landscape" else "portrait"

                val intent = Intent(applicationContext, SettingsActivity::class.java)
                intent.putExtra("orientation", screenOrientation)
                intent.putExtra("gallery", uri.toString())
                startActivity(intent)
                bitmap.recycle()
                finish()
            }
        }

    private val takeImageResult =
        registerForActivityResult(ActivityResultContracts.TakePicture()) { isSuccess ->
            if (isSuccess) {
                latestTmpUri?.let { uri ->

                    runBlocking {
                        launch(Dispatchers.IO) {
                            bitmap = Glide.with(applicationContext)
                                .asBitmap()
                                .load("$uri")
                                .submit()
                                .get()
                        }
                    }

                    val screenOrientation =
                        if (bitmap.width > bitmap.height) "landscape" else "portrait"

                    val intent = Intent(applicationContext, SettingsActivity::class.java)
                    intent.putExtra("orientation", screenOrientation)
                    intent.putExtra("camera", uri.toString())
                    startActivity(intent)
                    bitmap.recycle()
                    finish()
                }
            }
        }


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setClickListeners()
        getImagesFromAssets()

    }

    private fun selectImageFromGallery() = selectImageFromGalleryResult.launch("image/*")

    private fun takeImage() {
        lifecycleScope.launch {
            // Suspend until you are STARTED
            withStarted { }
            // Run your code that happens after you become STARTED here
            getTmpFileUri().let { uri ->
                latestTmpUri = uri
                takeImageResult.launch(uri)
            }
            // Note: your code will continue to run even if the Lifecycle falls below STARTED
        }

    }

    private fun getTmpFileUri(): Uri {
        val tmpFile = File.createTempFile("tmp_image_file", ".png", cacheDir).apply {
            createNewFile()
            deleteOnExit()
        }
        return FileProvider.getUriForFile(
            applicationContext,
            "${BuildConfig.APPLICATION_ID}.provider",
            tmpFile
        )
    }


    private fun setClickListeners() {
        binding.buttonCamera.setOnClickListener { takeImage() }
        binding.buttonGallery.setOnClickListener { selectImageFromGallery() }
    }

    private fun getImagesFromAssets() {
        val assetManager = assets

        try {
            val files = assetManager.list("img")

            binding.gridView.adapter = GridViewAdapter(this@MainActivity)
            binding.gridView.onItemClickListener = AdapterView.OnItemClickListener { _, _, i, _ ->
                val path = "img/" + (files!![i % files.size]).toString()
                val uri = Uri.parse(path)

                runBlocking {
                    launch(Dispatchers.IO) {
                        bitmap = Glide.with(applicationContext)
                            .asBitmap()
                            .load("file:///android_asset/$uri")
                            .submit()
                            .get()
                    }
                }

                val screenOrientation =
                    if (bitmap.width > bitmap.height) "landscape" else "portrait"

                val intent = Intent(applicationContext, SettingsActivity::class.java)
                intent.putExtra("orientation", screenOrientation)
                intent.putExtra("assets", uri.toString())
                startActivity(intent)
                bitmap.recycle()
                finish()
            }
        } catch (e: IOException) {
            e.printStackTrace()
            Toast.makeText(this, e.localizedMessage, Toast.LENGTH_SHORT).show()
        }
    }

}