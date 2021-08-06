package com.example.myapplication

import android.animation.ObjectAnimator
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import androidx.core.animation.doOnEnd
import com.squareup.picasso.Picasso
import kotlinx.android.synthetic.main.chat_row_img.view.*
import kotlinx.android.synthetic.main.story_splash_screen_layout.*
import android.view.WindowManager as WindowMar1

class story_splash_screen : AppCompatActivity() {
    var userTo: User? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        window.setFlags(android.view.WindowManager.LayoutParams.FLAG_FULLSCREEN,android.view.WindowManager.LayoutParams.FLAG_FULLSCREEN)
        setContentView(R.layout.story_splash_screen_layout)
        userTo = intent.getParcelableExtra<User>(new_message.USER_KEY)
        var uri = userTo?.storyUri

        Picasso.get().load(uri).into(story_image)
        val username = userTo?.username
        val description = userTo?.storyDesc
        val sent_time = userTo?.storyTime
        val duration = userTo?.storyDuration
        val currTime = System.currentTimeMillis()
        val gecenSure = (currTime/(60*1000)) - (sent_time!!/(60*1000))
        val time_left = duration !!- gecenSure

        story_desc.text = description.toString()
        val rolex_hadibas: String= "Story of ${userTo?.username}, ${time_left} minutes left..."

        story_countdown.max = 1000
        val currProgress = 1000
        supportActionBar?.title = rolex_hadibas



        story_image.alpha = 0f
        ObjectAnimator.ofInt(story_countdown,"progress",currProgress).setDuration(5000).start()
        story_image.animate().setDuration(5200).alpha(1f).withEndAction {
            val intent = Intent(this,Menu::class.java)
            overridePendingTransition(android.R.anim.slide_in_left,android.R.anim.slide_out_right)
            startActivity(intent)
            finish()

        }

    }
}