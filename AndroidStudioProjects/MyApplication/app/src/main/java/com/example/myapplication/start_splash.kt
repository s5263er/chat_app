package com.example.myapplication

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.WindowManager
import kotlinx.android.synthetic.main.start_splash_layout.*

class start_splash : AppCompatActivity() {
    private var SPLASH_SCREEN_TIME: Long = 3500
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        window.setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,WindowManager.LayoutParams.FLAG_FULLSCREEN)
        setContentView(R.layout.start_splash_layout)

       /* Handler(Looper.myLooper()!!).postDelayed({
            val intent = Intent(this,MainActivity::class.java)
            startActivity(intent)
            finish()
        }, SPLASH_SCREEN_TIME)*/
        start_teb_imgview.alpha = 0f
        start_teb_imgview.animate().setDuration(5200).alpha(0.9f).withEndAction {
            val intent = Intent(this,MainActivity::class.java)
            overridePendingTransition(android.R.anim.fade_in,android.R.anim.fade_out)
            startActivity(intent)
            finish()
        }

    }
}