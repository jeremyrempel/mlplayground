package com.github.jeremyrempel.mlplayground

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
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
            val frag: Fragment = when(it.itemId) {
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
                beginTransaction()
                    .setCustomAnimations(android.R.anim.slide_in_left, android.R.anim.slide_out_right)
                    .replace(R.id.fragment_container, frag).commit()
            }

            true
        }
    }
}