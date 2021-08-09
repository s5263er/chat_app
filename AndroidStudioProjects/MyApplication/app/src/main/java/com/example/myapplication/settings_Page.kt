package com.example.myapplication

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.ValueEventListener
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase
import com.squareup.picasso.Picasso
import kotlinx.android.synthetic.main.activity_settings__page.*
import kotlinx.android.synthetic.main.chat_row_right.view.*
import kotlinx.android.synthetic.main.menu_layout.*

class settings_Page : AppCompatActivity() {
    private var currUser: User? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_settings__page)
        supportActionBar?.title = "Settings"
        bottom_navigation_settings.selectedItemId = R.id.menu_settings

        bottom_navigation_settings.setOnItemSelectedListener {

            when (it.itemId) {
                R.id.menu_search1 -> {
                    val intent = Intent(this, new_message::class.java)
                    startActivity(intent)
                }
                R.id.menu_chat -> {
                    val intent = Intent(this, Menu::class.java)
                    startActivity(intent)
                    finish()
                }
                R.id.menu_settings -> {

                }
                R.id.menu_story -> {
                    val intent = Intent(this, StoryActivity::class.java)
                    startActivity(intent)
                    finish()
                }
            }
            true
        }


        val email: String = FirebaseAuth.getInstance().currentUser?.email.toString()

        retrieveUsers()

        settings_change_pass.setOnClickListener {
            FirebaseAuth.getInstance().sendPasswordResetEmail(email)
            Toast.makeText(this, "Password change request sent to ${email}", Toast.LENGTH_LONG).show()
        }
        settings_security.setOnClickListener {
            MaterialAlertDialogBuilder(this)
                    .setTitle("Security")
                    .setMessage("Your data is secure with SHA-1 hashing algorithm")
                    .setIcon(R.drawable.ic_baseline_security_24)
                    .setCancelable(true)
                    .setNegativeButton("Cool") { dialog, which ->
                        dialog.cancel()
                    }
                    .show()
        }
    }

    private fun retrieveUsers() {
        val database = Firebase.database("https://ceptechat-default-rtdb.europe-west1.firebasedatabase.app/")
        val uid = FirebaseAuth.getInstance().uid
        val ref = database.getReference("/users/$uid")
        ref.addListenerForSingleValueEvent(object: ValueEventListener{
            override fun onDataChange(snapshot: DataSnapshot) {
                currUser = snapshot.getValue(User::class.java)!!
                val email: String = FirebaseAuth.getInstance().currentUser?.email.toString()
                Log.d("current_user","basariyla alindi ${currUser!!.username}")
                settings_email.setText(email)
                settings_username.setText(currUser?.username)

                var uri = currUser?.profileImg
                val img = settings_profileImg
                Picasso.get().load(uri).into(img)
            }
            override fun onCancelled(error: DatabaseError) {
            }

        })
    }
}