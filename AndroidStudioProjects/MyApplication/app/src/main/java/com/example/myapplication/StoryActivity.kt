package com.example.myapplication

import android.app.Activity
import android.content.DialogInterface
import android.content.Intent
import android.graphics.Color
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.provider.MediaStore
import android.util.Log
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.ValueEventListener
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.FirebaseStorage
import com.squareup.picasso.Picasso
import kotlinx.android.synthetic.main.activity_settings__page.*
import kotlinx.android.synthetic.main.activity_story.*
import kotlinx.android.synthetic.main.latest_msg_row.view.*
import kotlinx.android.synthetic.main.select_photo.*
import java.util.*

class StoryActivity : AppCompatActivity() {
    private var currUser: User? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_story)
        retrieveUsers()
        supportActionBar?.title = "Share your stories"

        bottom_navigation_story.setOnItemSelectedListener {
            when(it.itemId){
                R.id.menu_search1 -> {
                    val intent = Intent(this, new_message::class.java)
                    startActivity(intent)
                }
                R.id.menu_chat -> {
                    val intent = Intent(this, com.example.myapplication.Menu::class.java)
                    startActivity(intent)
                    finish()
                }
                R.id.menu_settings ->{
                    val intent = Intent(this, settings_Page::class.java)
                    intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TASK.or(Intent.FLAG_ACTIVITY_NEW_TASK)
                    startActivity(intent)
                    finish()
                }
                R.id.menu_story -> {
                }
            }
            true
        }



        select_story.setOnClickListener {
            if(checkStoryValid(currUser)){
                val intent_story = Intent(this,story_splash_screen::class.java)
                val dialogBuilder = AlertDialog.Builder(this)
                dialogBuilder.setMessage("You shared a story ${System.currentTimeMillis()/(1000*60)-(currUser?.storyTime)!!/(1000*60) } minutes ago do you want to watch or add a new story?")
                        // if the dialog is cancelable
                        .setCancelable(true)
                        // positive button text and action
                        .setPositiveButton("Watch Story", DialogInterface.OnClickListener {
                            dialog, id ->
                            intent_story.putExtra(new_message.USER_KEY,currUser)
                            startActivity(intent_story)
                            finish()
                        })
                        // negative button text and action
                        .setNegativeButton("Add a new story", DialogInterface.OnClickListener {
                            dialog, id ->
                            val intent = Intent(Intent.ACTION_PICK)
                            intent.type = "image/*"
                            startActivityForResult(intent, 0)
                            dialog.cancel()
                        })
                val alert = dialogBuilder.create()
                alert.setTitle("Decision Box")
                alert.show()
            }
            else{
                val intent = Intent(Intent.ACTION_PICK)
                intent.type = "image/*"
                startActivityForResult(intent, 0)
            }

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
            Toast.makeText(this, "Successful! story added for ${story_duration.text.toString()} minutes", Toast.LENGTH_LONG).show()
            startActivity(intent)
            finish()
        }
    }
    private fun checkStoryValid(user: User?): Boolean {
        if(user?.storyUri!!.isEmpty() || user?.storyUri == ""){
            return false
        }
        if(user?.storyDesc!!.isEmpty() || user?.storyDesc == ""){
            return false
        }
        if(!(user?.storyDuration!!.toInt() > 0))
        {
            return false
        }
        val duration = user.storyDuration.toInt()
        val sentTime = user.storyTime.toLong()
        val currTime = System.currentTimeMillis()
        val time_went = (currTime/(60*1000)) -  (sentTime/(60*1000))
        Log.d("time_left_story","${time_went}")
        return time_went <= duration
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
                var uri = it
                val img = story_select_after
                Picasso.get().load(uri).into(img)
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
            story_select_after.borderWidth = 4
            story_select_after.borderColor = Color.BLACK
        }
    }
    private fun retrieveUsers() {
        val database = Firebase.database("https://ceptechat-default-rtdb.europe-west1.firebasedatabase.app/")
        val uid = FirebaseAuth.getInstance().uid
        val ref = database.getReference("/users/$uid")
        ref.addListenerForSingleValueEvent(object: ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                currUser = snapshot.getValue(User::class.java)!!
                Log.d("current_user","basariyla alindi ${currUser!!.username}")
                if(checkStoryValid(currUser)){
                    select_story.alpha = 0f
                    story_select_after.borderWidth = 30
                    story_select_after.borderColor = Color.MAGENTA

                    var uri = currUser?.storyUri
                    val img = story_select_after
                    Picasso.get().load(uri).into(img)
                }

            }
            override fun onCancelled(error: DatabaseError) {
            }

        })
    }
}