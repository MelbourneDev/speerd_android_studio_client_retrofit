package com.mattvu.speerdcompanionapp.splash

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.view.animation.Animation
import android.view.animation.AnimationUtils
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.mattvu.speerdcompanionapp.R
import com.mattvu.speerdcompanionapp.login.LoginActivity

class SplashActivity : AppCompatActivity() {
/* SplashActivity is the opening screen for the app
it utilises animation functions to have an inspiring opening screen



 */
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_splash)
        supportActionBar?.hide()
        // Initializing TextViews and ImageView for animations
        val textView = findViewById<TextView>(R.id.splashpageTextView)
        val textViewSmall = findViewById<TextView>(R.id.splashpageTextViewSmall)
        val logoAnimationView = findViewById<ImageView>(R.id.logoView)
        // Loading animations from resources
        val rotateAnimation = AnimationUtils.loadAnimation(this, R.anim.rotate_logo)
        val textFadeAnimation = AnimationUtils.loadAnimation(this, R.anim.text_fade_splash)
        val textSmallFadeAnimation = AnimationUtils.loadAnimation(this, R.anim.text_fade_splash)
        // Setting an animation listener on rotateAnimation
        rotateAnimation.setAnimationListener(object : Animation.AnimationListener {
            override fun onAnimationStart(animation: Animation?) {}
            override fun onAnimationEnd(animation: Animation?) {
                textView.visibility = View.VISIBLE
                textView.startAnimation(textFadeAnimation)
            }
            override fun onAnimationRepeat(animation: Animation?) {}
        })
        // Setting an animation listener on textFadeAnimation
        textFadeAnimation.setAnimationListener(object : Animation.AnimationListener {
            override fun onAnimationStart(animation: Animation?) {}
            override fun onAnimationEnd(animation: Animation?) {
                textViewSmall.visibility = View.VISIBLE
                textViewSmall.startAnimation(textSmallFadeAnimation)
            }
            override fun onAnimationRepeat(animation: Animation?) {}
        })
        // Setting an animation listener on textSmallFadeAnimation
        textSmallFadeAnimation.setAnimationListener(object : Animation.AnimationListener {
            override fun onAnimationStart(animation: Animation?) {}
            override fun onAnimationEnd(animation: Animation?) {
                startActivity(Intent(this@SplashActivity, LoginActivity::class.java))
            }
            override fun onAnimationRepeat(animation: Animation?) {}
        })

        logoAnimationView.startAnimation(rotateAnimation)
    }
}


