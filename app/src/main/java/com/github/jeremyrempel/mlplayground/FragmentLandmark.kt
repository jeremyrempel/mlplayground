package com.github.jeremyrempel.mlplayground

import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.google.firebase.ml.vision.FirebaseVision
import com.google.firebase.ml.vision.cloud.landmark.FirebaseVisionCloudLandmark
import com.google.firebase.ml.vision.common.FirebaseVisionImage
import com.squareup.picasso.Picasso
import kotlinx.android.synthetic.main.fragment_landmark.*
import kotlinx.coroutines.*
import java.net.URL

class FragmentLandmark : Fragment() {

    private val job = Job()
    private val uiScope = CoroutineScope(Dispatchers.Main + job)

    //    val url = "https://images.unsplash.com/photo-1550837725-bdcb030d1e54?ixlib=rb-1.2.1&q=85&fm=jpg&crop=entropy&cs=srgb
    val eiffel =
        "https://images.unsplash.com/photo-1502783897899-5958a31ed82b?ixlib=rb-1.2.1&q=85&fm=jpg&crop=entropy&cs=srgb"
    val disneyland =
        "https://images.unsplash.com/photo-1556950961-8c092986258e?ixlib=rb-1.2.1&q=85&fm=jpg&crop=entropy&cs=srgb"
    val statueOfLiberty =
        "https://images.unsplash.com/photo-1503572327579-b5c6afe5c5c5?ixlib=rb-1.2.1&q=85&fm=jpg&crop=entropy&cs=srgb"

    val empire = "https://upload.wikimedia.org/wikipedia/commons/1/10/Empire_State_Building_%28aerial_view%29.jpg"

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_landmark, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        btn_disney.setOnClickListener { edt_url.setText(disneyland) }
        btn_statue.setOnClickListener { edt_url.setText(statueOfLiberty) }
        btn_tower.setOnClickListener { edt_url.setText(eiffel) }
        btn_nyc.setOnClickListener { edt_url.setText(empire) }

        btn_go.setOnClickListener { loadFromUrl(edt_url.text.toString()) }
    }

    private fun loadFromUrl(url: String) {
        uiScope.launch {
            val imageView = image
            Picasso.get().load(url).resize(1000, 1000).into(imageView)

            val result = getBitmapFromURL(url)
            setupFireBase(result)
        }
    }

    private fun setupFireBase(bitmap: Bitmap) {

        text1.text = ""
        loading.visibility = View.VISIBLE

        try {
            val image = FirebaseVisionImage.fromBitmap(bitmap)
            val detector = FirebaseVision.getInstance().visionCloudLandmarkDetector

            detector.detectInImage(image)
                .addOnSuccessListener { result: List<FirebaseVisionCloudLandmark> ->
                    for (landmark in result) {
                        val bounds = landmark.boundingBox
                        val landmarkName = landmark.landmark
                        val entityId = landmark.entityId
                        val confidence = landmark.confidence

                        // Multiple locations are possible, e.g., the location of the depicted
                        // landmark and the location the picture was taken.
                        for (loc in landmark.locations) {
                            val latitude = loc.latitude
                            val longitude = loc.longitude
                        }

                        Log.d("jeremy", "landmark name: $landmarkName")
                    }

                    if (result.isNotEmpty()) {
                        val str =
                            "Landmark: ${result[0].landmark}\nConfidence: ${result[0].confidence}\nLat,Long: {${result[0].locations[0].latitude} ${result[0].locations[0].longitude}}"

                        text1.text = str
                        loading.visibility = View.GONE
                    }
                }
                .addOnFailureListener {
                    text1.text = "Location could not be identified"
                    loading.visibility = View.GONE
                }

        } catch (e: Exception) {
            text1.text = e.message
            loading.visibility = View.GONE
        }
    }


    private suspend fun getBitmapFromURL(src: String): Bitmap = withContext(Dispatchers.IO) {
        val connection = URL(src).openConnection()
        connection.getInputStream().use(BitmapFactory::decodeStream)
    }
}
