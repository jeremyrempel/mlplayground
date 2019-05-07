package com.github.jeremyrempel.mlplayground

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import kotlinx.android.synthetic.main.activity_main.*
import java.lang.IllegalArgumentException

class MainActivity: AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContentView(R.layout.activity_main)

        if(savedInstanceState == null) {
            supportFragmentManager.apply {
                val frag = FragmentLandmark()
                beginTransaction().add(R.id.fragment_container, frag).commit()
            }
        }

        bottom_navigation.setOnNavigationItemSelectedListener {
            val frag = when(it.itemId) {
                R.id.navlandmark -> {
                    title = "Landmark"
                    FragmentLandmark()
                }
                R.id.navimage -> {
                    title = "Image"
                    FragmentImage()
                }
                else -> throw IllegalArgumentException()
            }

            supportFragmentManager.apply {
                beginTransaction().replace(R.id.fragment_container, frag).commit()
            }

            true
        }
    }
}