package com.example.gradletest

import android.content.Intent
import android.os.Bundle
import android.transition.TransitionManager
import android.view.ViewGroup
import androidx.appcompat.app.AppCompatActivity
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.constraintlayout.widget.ConstraintSet
import com.example.gradletest.databinding.ActivityMainBinding
import com.example.salebill.cart.CartActivity

class MainActivity : AppCompatActivity() {

    lateinit var binding: ActivityMainBinding

    lateinit var constraintLayout: ConstraintLayout

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        constraintLayout = binding.cl

        binding.button2.setOnClickListener {

            animateToKeyframeTwo()
        }

        binding.button3.setOnClickListener {

            animateToKeyframeTwo()
        }


        binding.button4.setOnClickListener {
            val intent = Intent(MainActivity@this, CartActivity::class.java)
            startActivity(intent)
        }

    }

    fun animateToKeyframeTwo() {
        val constraintSet = ConstraintSet()
        constraintSet.load(this, R.layout.activity_main2)
        TransitionManager.beginDelayedTransition(binding.root as ViewGroup?)
        constraintSet.applyTo(constraintLayout)
    }
}
