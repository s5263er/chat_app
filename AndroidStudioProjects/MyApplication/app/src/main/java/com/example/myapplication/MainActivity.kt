package com.example.myapplication


import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.CheckBox
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.android.material.textfield.TextInputEditText
import com.google.android.material.textfield.TextInputLayout
import com.google.firebase.auth.FirebaseAuth
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.activity_main.view.*
import kotlin.reflect.jvm.internal.impl.util.Check


class MainActivity : AppCompatActivity() {


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        val pref = getPreferences(Context.MODE_PRIVATE)
        val email = pref.getString("Email","")
        supportActionBar?.title = "Login"

        login_username.setText(email)
        val pass = pref.getString("Password","")
        login_password.setText(pass)
        val isChecked = pref.getBoolean("Remember me",false)
        rememberme.isChecked = isChecked











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
                    val intent = Intent(this, Menu::class.java)
                    intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TASK.or(Intent.FLAG_ACTIVITY_NEW_TASK)
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

    fun onSave(view: View) {
        Log.d("login","remember me basildi ${rememberme.isChecked}")
        if(view is CheckBox)
        {
            val view: Boolean = rememberme.isChecked
        }
        val pref = getPreferences(Context.MODE_PRIVATE)

        if(rememberme.isChecked)
        {
            val editor = pref.edit()

            editor.putString("Email",login_username.text.toString())
            editor.putString("Password",login_password.text.toString())
            editor.putBoolean("Remember me",rememberme.isChecked)
            editor.commit()
        }
        else{
            val editor = pref.edit()
            editor.clear()
            editor.commit()
            //login_username.setText("")
            //login_password.setText("")
        }





    }

}