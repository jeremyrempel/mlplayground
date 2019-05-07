package com.github.jeremyrempel.mlplayground

import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.google.firebase.ml.vision.FirebaseVision
import com.google.firebase.ml.vision.common.FirebaseVisionImage
import com.squareup.picasso.Picasso
import kotlinx.android.synthetic.main.fragment_image.*
import kotlinx.android.synthetic.main.fragment_landmark.*
import kotlinx.android.synthetic.main.fragment_landmark.btn_go
import kotlinx.android.synthetic.main.fragment_landmark.edt_url
import kotlinx.coroutines.*
import java.net.URL
import kotlinx.android.synthetic.main.fragment_landmark.image as image1
import kotlinx.android.synthetic.main.fragment_landmark.loading as loading1
import kotlinx.android.synthetic.main.fragment_landmark.text1 as text11

class FragmentImage : Fragment() {

    private val job = Job()
    private val uiScope = CoroutineScope(Dispatchers.Main + job)

    val apple =
        "https://images.unsplash.com/photo-1513677785800-9df79ae4b10b?ixlib=rb-1.2.1&q=85&fm=jpg&crop=entropy&cs=srgb"
    val monkey =
        "https://images.unsplash.com/photo-1540573133985-87b6da6d54a9?ixlib=rb-1.2.1&q=85&fm=jpg&crop=entropy&cs=srgb"

    val baseball = "https://images.unsplash.com/photo-1519152638844-5f96fded9237?ixlib=rb-1.2.1&q=85&fm=jpg&crop=entropy&cs=srgb"

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_image, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        btn_apple.setOnClickListener { edt_url.setText(apple) }
        btn_monkey.setOnClickListener { edt_url.setText(monkey) }
        btn_feet.setOnClickListener { edt_url.setText(baseball) }

        btn_go.setOnClickListener { loadFromUrl(edt_url.text.toString()) }
    }

    private fun loadFromUrl(url: String) {
        uiScope.launch {
            val imageView = image
            Picasso.get().load(url).resize(500, 500).into(imageView)

            val result = getBitmapFromURL(url)

            setupFireBase(result, ondevice.isChecked)
        }
    }

    private fun setupFireBase(bitmap: Bitmap, useDevice: Boolean = true) {

        text1.text = ""
        loading.visibility = View.VISIBLE

        val image = FirebaseVisionImage.fromBitmap(bitmap)
        val labeler = if(!useDevice) {
            FirebaseVision.getInstance().cloudImageLabeler
        } else {
            FirebaseVision.getInstance().onDeviceImageLabeler
        }

        labeler.processImage(image).addOnSuccessListener { labels ->

            val labelsTxt = labels
                .map { "Label: ${it.text}, Confidence: ${it.confidence}" }
                .reduce { acc, label ->
                    "$acc\n$label"
                }

            text1.text = labelsTxt
            loading.visibility = View.GONE
        }
            .addOnFailureListener {
                text1.text = "Location could not be identified"
                loading.visibility = View.GONE
            }

    }

    private suspend fun getBitmapFromURL(src: String): Bitmap = withContext(Dispatchers.IO) {
        val connection = URL(src).openConnection()
        connection.getInputStream().use(BitmapFactory::decodeStream)
    }
}