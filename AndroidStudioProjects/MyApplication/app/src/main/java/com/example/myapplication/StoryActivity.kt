package com.example.myapplication

import android.app.Activity
import android.content.Intent
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.provider.MediaStore
import android.util.Log
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.FirebaseStorage
import kotlinx.android.synthetic.main.activity_story.*
import kotlinx.android.synthetic.main.select_photo.*
import java.util.*

class StoryActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_story)

        select_story.setOnClickListener {
            val intent = Intent(Intent.ACTION_PICK)
            intent.type = "image/*"
            startActivityForResult(intent, 0)
        }
        story_share.setOnClickListener {

            val uid = FirebaseAuth.getInstance().uid
            val database = Firebase.database("https://ceptechat-default-rtdb.europe-west1.firebasedatabase.app/")
            val refDesc = database.getReference("/users/$uid/storyDesc")
            refDesc.setValue(story_description.text.toString())
            val refStoryTime = database.getReference("/users/$uid/storyTime")
            refStoryTime.setValue(System.currentTimeMillis())
            val refStoryDuration = database.getReference("/users/$uid/storyDuration")
            refStoryDuration.setValue(story_duration.text.toString().toInt())
            uploadImageToFirebase()
            val intent = Intent(this, Menu::class.java)
            startActivity(intent)
        }
    }
    private fun uploadImageToFirebase(){

        if(selectedPhotoUri == null) return
        val filename = UUID.randomUUID().toString()
        val ref = FirebaseStorage.getInstance().getReference("/images/$filename")

        ref.putFile(selectedPhotoUri!!).addOnSuccessListener {

            ref.downloadUrl.addOnSuccessListener {
                val uid = FirebaseAuth.getInstance().uid
                val database = Firebase.database("https://ceptechat-default-rtdb.europe-west1.firebasedatabase.app/")
                val ref = database.getReference("/users/$uid/storyUri")
                ref.setValue(it.toString())
            }
        }
    }
    var selectedPhotoUri: Uri? = null
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if(requestCode == 0 && resultCode == Activity.RESULT_OK && data != null){
            selectedPhotoUri = data.data

            val bitmap = MediaStore.Images.Media.getBitmap(contentResolver, selectedPhotoUri)

            story_select_after.setImageBitmap(bitmap)

            select_story.alpha = 0f
        }
    }
}