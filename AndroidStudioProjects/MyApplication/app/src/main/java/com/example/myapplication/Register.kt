package com.example.myapplication

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.navigation.NavController
//import com.example.myapplication.databinding.ActivityMainBinding
import com.google.android.material.textfield.TextInputEditText
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.register_layout.*

class Register: AppCompatActivity()
{
    private lateinit var navController: NavController
    //private lateinit var binding : ActivityMainBinding
    private lateinit var database : DatabaseReference

    //val navHostFragment = supportFragmentManager.findFragmentById(R.id.nav_host_fragment)
    //navController = navHostFragment.findNav

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.register_layout)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.title = "ChatteCep"
        supportActionBar?.setDisplayShowTitleEnabled(true)


        register_button.setOnClickListener {
            register()

        }
    }

    //class User(val uid: String, val username: String, val profileImageUrl: String)


    private fun saveUserToFirebaseDatabase(uid2: String) {


        val uid = FirebaseAuth.getInstance().uid
        val database = Firebase.database("https://ceptechat-default-rtdb.europe-west1.firebasedatabase.app/")
        val ref = database.getReference("/users/$uid2")

        val user = User(uid2, register_username.text.toString(),"https://upload.wikimedia.org/wikipedia/commons/b/b2/BNP_Paribas.png","",0,0,"")


        ref.setValue(user).addOnSuccessListener {
                Log.d("TAG", "Finally we saved the user to Firebase Database")
            val intent = Intent(this, select_photo::class.java)
            intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TASK.or(Intent.FLAG_ACTIVITY_NEW_TASK)
            startActivity(intent)

            }
            .addOnFailureListener {
                Log.d("TAG", "Failed to set value to database: ${it.message}")
            }
    }

    private fun register() {

        val etUsername = findViewById<TextInputEditText>(R.id.register_username)
        val etEmail = findViewById<TextInputEditText>(R.id.register_email)
        val etPass1 = findViewById<TextInputEditText>(R.id.register_pass1)
        val etPass2 = findViewById<TextInputEditText>(R.id.register_pass2)
        val email = etEmail.text.toString()
        val username = etUsername.text.toString()
        val password = etPass1.text.toString()
        val password2 = etPass2.text.toString()
        
        if(password != password2)
        {
            Toast.makeText(this, "Passwords must match", Toast.LENGTH_SHORT).show()
            return
        }
        FirebaseAuth.getInstance().createUserWithEmailAndPassword(email,password).addOnCompleteListener {
            if(it.isSuccessful == false) return@addOnCompleteListener
            else{
                val uidyolla = FirebaseAuth.getInstance().uid
                Log.d("Register", "Successful ${it.result?.user?.uid}")
                if (uidyolla != null) {
                    saveUserToFirebaseDatabase(uidyolla)
                }
            }
        }.addOnFailureListener { exception ->
            Toast.makeText(applicationContext,exception.localizedMessage,Toast.LENGTH_LONG).show()
        }
    }


}