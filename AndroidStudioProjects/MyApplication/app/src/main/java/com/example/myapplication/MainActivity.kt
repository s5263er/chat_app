package com.example.myapplication


import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.android.material.textfield.TextInputEditText
import com.google.android.material.textfield.TextInputLayout
import com.google.firebase.auth.FirebaseAuth
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.activity_main.view.*


class MainActivity : AppCompatActivity() {


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)










        button_login.setOnClickListener {
            //val password = login_username.text.toString()
            //val username = login_password.text.toString()
            val etUsername = findViewById<TextInputEditText>(R.id.login_username)
            val etPassword = findViewById<TextInputEditText>(R.id.login_password)
            val password = etPassword.text.toString()
            val username = etUsername.text.toString()
            Log.d("TAG", "${username} username bu ${password} bu email")


            if(username.isEmpty() || password.isEmpty())
            {
                Toast.makeText(this, "E-mail or password cannot be empty", Toast.LENGTH_LONG).show()
            }

            FirebaseAuth.getInstance().signInWithEmailAndPassword(username, password).addOnCompleteListener {
                if(it.isSuccessful){
                    Log.d("Login", "Login is Successful ")
                    val intent = Intent(this, Register::class.java)
                    startActivity(intent)
                }
            }.addOnFailureListener { exception ->
                Toast.makeText(applicationContext, exception.localizedMessage, Toast.LENGTH_LONG).show()
            }

        }

        go_to_register_login.setOnClickListener {
            val intent = Intent(this, Register::class.java)
            startActivity(intent)
        }
    }

}