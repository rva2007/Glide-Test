package com.example.glidetest

import android.content.Intent
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import android.os.Bundle
import android.widget.AdapterView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.bumptech.glide.Glide
import com.example.glidetest.databinding.ActivityMainBinding
import java.io.FileNotFoundException
import java.io.IOException
import java.io.InputStream

class MainActivity : AppCompatActivity() {
    lateinit var binding: ActivityMainBinding
    private var bitmap: Bitmap? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        getImagesFromAssets()

//        uri = getUriFromAssets()
//        Glide.with(this)
//            .load(uri)
//            .into(binding.initImage)

    }

//    private fun getUriFromAssets():Uri {
//        try {
//            val assetManager = assets
//            val files = assetManager.list("img")
//            val path = "file:///android_asset/img/" + (files!![4 % files.size]).toString()
//            uri = Uri.parse(path)
//        } catch (e: IOException) {
//            e.printStackTrace()
//            Toast.makeText(this, e.localizedMessage, Toast.LENGTH_SHORT).show()
//        }
//        return uri
//    }



    private fun getImagesFromAssets() {
        val assetManager = assets

        try {
            val files = assetManager.list("img")

            binding.gridView.adapter = GridViewAdapter(this@MainActivity)
            binding.gridView.onItemClickListener = AdapterView
                .OnItemClickListener { _, _, i, _ ->
                    val path = "img/" + (files!![i % files.size]).toString()
                    val uri = Uri.parse(path)
                    bitmap = getAssetsBitmap(path)
                    val intent = Intent(applicationContext, SettingsActivity::class.java)
                    intent.putExtra("orientation", getOrientationScreen(bitmap!!))
                    intent.putExtra("assets", uri.toString())
                    startActivity(intent)
                    bitmap!!.recycle()
                    finish()
                }
        } catch (e: IOException) {
            e.printStackTrace()
            Toast.makeText(this, e.localizedMessage, Toast.LENGTH_SHORT).show()
        }
    }

    private fun getAssetsBitmap(str: String): Bitmap? {
        val inputStream: InputStream
        var bitmap: Bitmap? = null
        try {
            inputStream = applicationContext.assets.open(str)
            bitmap = BitmapFactory.decodeStream(inputStream)
        } catch (e: FileNotFoundException) {
            e.printStackTrace()
            Toast.makeText(this, e.localizedMessage, Toast.LENGTH_SHORT).show()
        }
        return bitmap
    }

    private fun getOrientationScreen(bitmap: Bitmap): String {
        return if (bitmap.width > bitmap.height) "landscape" else "portrait"
    }



}