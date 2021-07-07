package com.example.myapplication

import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.navigation.NavController
import com.example.myapplication.databinding.ActivityMainBinding
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
    private lateinit var binding : ActivityMainBinding
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
            saveUserToFirebaseDatabase()
        }
    }

    //class User(val uid: String, val username: String, val profileImageUrl: String)

    private fun saveUserToFirebaseDatabase() {


        val uid = FirebaseAuth.getInstance().uid ?: ""
        val ref = FirebaseDatabase.getInstance().getReference("/users/$uid")

        val user = User(uid, register_username.text.toString())

        Log.d("TAG", "Buraya girdi mi ${user.uid}")

        ref.setValue(user).addOnSuccessListener {
                Log.d("TAG", "Finally we saved the user to Firebase Database")
            }
            .addOnFailureListener {
                Log.d("TAG", "Failed to set value to database: ${it.message}")
            }
    }

    private fun register() {
        val email = register_email.text.toString()
        val username = register_username.text.toString()
        val password = register_pass1.text.toString()
        val password2 = register_pass2.text.toString()
        if(password != password2)
        {
            Toast.makeText(this, "Passwords must match", Toast.LENGTH_SHORT).show()
            return
        }
        FirebaseAuth.getInstance().createUserWithEmailAndPassword(email,password).addOnCompleteListener {
            if(it.isSuccessful == false) return@addOnCompleteListener
            else{

                val db = Firebase.database
                val myRef = db.getReference("message")

                myRef.setValue("Hello, World!").addOnCanceledListener { Log.d("as","${myRef.setValue("Hello, World!").addOnFailureListener { it.message }}") }
                Log.d("sa","${myRef.setValue("Hello, World!").addOnFailureListener { it.message }}")
                Log.d("sa","${myRef.setValue("Hello, World!").addOnFailureListener { it.localizedMessage }}")
                Log.d("sa","${myRef.setValue("Hello, World!").addOnFailureListener { it.stackTraceToString() }}")
                Log.d("sa","${myRef.setValue("Hello, World!").addOnFailureListener { it.cause}}")
                database = FirebaseDatabase.getInstance().getReference("users")
                val User = User(it.result?.user!!.uid,username)
                database.child(it.result?.user!!.uid).setValue(User).addOnSuccessListener {
                    Toast.makeText(this,"Successfully Saved",Toast.LENGTH_SHORT).show()

                }.addOnFailureListener{
                    Log.d("nedenmis", "${it.message}")

                    Toast.makeText(this,"Failed",Toast.LENGTH_SHORT).show()


                }
                Log.d("Register", "Successful ${it.result?.user?.uid}")}

        }.addOnFailureListener { exception ->
            Toast.makeText(applicationContext,exception.localizedMessage,Toast.LENGTH_LONG).show()
        }
    }


}