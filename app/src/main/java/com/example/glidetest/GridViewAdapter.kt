package com.example.glidetest

import android.annotation.SuppressLint
import android.content.Context
import android.content.res.AssetManager
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Rect
import android.os.Handler
import android.os.Looper
import android.view.View
import android.view.ViewGroup
import android.widget.BaseAdapter
import android.widget.ImageView
import java.io.IOException
import java.util.concurrent.Executors
import kotlin.math.min


class GridViewAdapter(private val context: Context) : BaseAdapter() {
    private val assetManager: AssetManager = context.assets
    private var files: Array<String>? = null

    override fun getCount(): Int = files!!.size

    override fun getItem(position: Int): Any? {
        return null
    }

    override fun getItemId(position: Int): Long = 0

    @SuppressLint("ViewHolder")
    override fun getView(position: Int, view: View?, viewGroup: ViewGroup?): View {
        val mView = View.inflate(context, R.layout.gridview_item, null)
        val imageView = mView.findViewById<ImageView>(R.id.imageViewSource)
        imageView.post {
            val executor = Executors.newSingleThreadExecutor()
            val handler = Handler(Looper.getMainLooper())
            executor.execute {
                //background work here
                val bitmap = getPicFromAsset(imageView, files!![position])
                handler.post {
                    //UI thread work here
                    imageView.setImageBitmap(bitmap)
                }
            }
        }
        return mView
    }

    private fun getPicFromAsset(imageView: ImageView?, assetName: String): Bitmap? {
        val targetW = imageView!!.width
        val targetH = imageView.height

        return if (targetW == 0 || targetH == 0) {
            //view has no dimension set
            null
        } else try {
            var `is` = assetManager.open("img/$assetName")
            val bmOptions = BitmapFactory.Options()
            bmOptions.inJustDecodeBounds = true

            BitmapFactory.decodeStream(`is`, Rect(-1, -1, -1, -1), bmOptions)

            val photoW = bmOptions.outWidth
            val photoH = bmOptions.outHeight

            //determine how much to scale down the imageView
            val scaleFactor = min(photoW / targetW, photoH / targetH)

            //decode the image file into a Bitmap size to fill the view
            bmOptions.inJustDecodeBounds = false
            bmOptions.inSampleSize = scaleFactor
            bmOptions.inBitmap
            `is` = assetManager.open("img/$assetName")
            BitmapFactory.decodeStream(`is`, Rect(-1, -1, -1, -1), bmOptions)
        } catch (e: IOException) {
            e.printStackTrace()
            null
        }
    }

    init {
        try {
            files = assetManager.list("img")
        } catch (e: IOException) {
            e.printStackTrace()
        }
    }

}
