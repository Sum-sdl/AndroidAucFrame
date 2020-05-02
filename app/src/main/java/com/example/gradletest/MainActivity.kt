package com.example.gradletest

import android.content.Intent
import android.os.Bundle
import android.transition.TransitionManager
import android.view.ViewGroup
import androidx.appcompat.app.AppCompatActivity
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.constraintlayout.widget.ConstraintSet
import com.example.goods.IApiGoodFinder
import com.example.gradletest.api.IApiFeature1
import com.example.gradletest.api.IApiFeature2
import com.example.gradletest.databinding.ActivityMainBinding
import com.example.salebill.cart.CartActivity
import com.example.salebill.comment.IModuleBill
import com.zp.apt.api.ApiFinder

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


        //pins 工程测试
        binding.button4.setOnClickListener {
            val intent = Intent(this, CartActivity::class.java)
            startActivity(intent)
        }

        //Api隔离实现
        binding.api.setOnClickListener {
             ApiFinder.findApi(IApiFeature1::class.java).fun1()
             ApiFinder.findApi(IApiFeature2::class.java).fun1()
             ApiFinder.findApi(IModuleBill::class.java).update()
             ApiFinder.findApi(IApiGoodFinder::class.java).print()
        }

    }

    fun animateToKeyframeTwo() {
        val constraintSet = ConstraintSet()
        constraintSet.load(this, R.layout.activity_main2)
        TransitionManager.beginDelayedTransition(binding.root as ViewGroup?)
        constraintSet.applyTo(constraintLayout)
    }
}
