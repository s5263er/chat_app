package com.example.myapplication

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase
import com.squareup.picasso.Picasso
import com.xwray.groupie.GroupAdapter
import com.xwray.groupie.GroupieViewHolder
import com.xwray.groupie.Item
import kotlinx.android.synthetic.main.activity_chat__screen.*
import kotlinx.android.synthetic.main.chat_row.view.*
import kotlinx.android.synthetic.main.chat_row.view.chat_text
import kotlinx.android.synthetic.main.chat_row_right.view.*

class Chat_Screen : AppCompatActivity() {
    val adapter = GroupAdapter<GroupieViewHolder>()
    var userTo: User? = null
    companion object{
        var currentUser: User? = null
    }

    private fun currUser(){
        val uid = FirebaseAuth.getInstance().uid
        val database = Firebase.database("https://ceptechat-default-rtdb.europe-west1.firebasedatabase.app/")
        val ref = database.getReference("/users/$uid")
        ref.addListenerForSingleValueEvent(object: ValueEventListener{
            override fun onDataChange(snapshot: DataSnapshot) {
                currentUser = snapshot.getValue(User::class.java)
                Log.d("chat","current user is ${currentUser!!.username}")
            }

            override fun onCancelled(error: DatabaseError) {

            }
        })
    }
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_chat__screen)
        currUser()

        listview_chat_screen.adapter = adapter



        userTo = intent.getParcelableExtra<User>(new_message.USER_KEY)
        supportActionBar?.title = userTo?.username.toString()

        val adapter = GroupAdapter<GroupieViewHolder>()

        //adapter.add(ChatRowGelen())

        chatListener()

        send_button.setOnClickListener {
            sendMsgToFirebase()

        }
    }

    private fun sendMsgToFirebase(){
        val user = intent.getParcelableExtra<User>(new_message.USER_KEY)
        val from = FirebaseAuth.getInstance().uid
        val to = user?.uid
        val text = send_chat_box.text.toString()
        val database = Firebase.database("https://ceptechat-default-rtdb.europe-west1.firebasedatabase.app/")
        val ref = database.getReference("/messages").push()

        val chatMsg = Message(ref.key!!,text,from!!,to!!,System.currentTimeMillis())

        ref.setValue(chatMsg).addOnFailureListener {
            Log.d("Chat_Screen","Failed to send msg to firebase")
        }

    }

    private fun bringData(){
        val adapter = GroupAdapter<GroupieViewHolder>()
        //adapter.add(ChatRow("dummy"))

        listview_chat_screen.adapter = adapter

    }
    private fun chatListener(){
        val database = Firebase.database("https://ceptechat-default-rtdb.europe-west1.firebasedatabase.app/")
        val ref = database.getReference("messages")

        val urlref = FirebaseDatabase.getInstance("https://ceptechat-default-rtdb.europe-west1.firebasedatabase.app/").getReference("/messages")
        urlref.addChildEventListener(object: ChildEventListener{
            override fun onChildAdded(snapshot: DataSnapshot, previousChildName: String?) {
                val msg = snapshot.getValue(Message::class.java)

                if(msg == null){return}

                if(msg != null){
                    if(msg.from == FirebaseAuth.getInstance().uid){
                        adapter.add(ChatRow(msg.data, userTo!!))
                        Log.d("chat","yollanan")
                    }
                    else{
                        //val userFr = intent.getParcelableExtra<User>(new_message.USER_KEY)
                        adapter.add(ChatRowGelen(msg.data, currentUser!!))
                        Log.d("chat","gelen")
                    }

                }

            }

            override fun onChildChanged(snapshot: DataSnapshot, previousChildName: String?) {

            }

            override fun onCancelled(error: DatabaseError) {

            }

            override fun onChildMoved(snapshot: DataSnapshot, previousChildName: String?) {

            }

            override fun onChildRemoved(snapshot: DataSnapshot) {

            }
        })
        //val ref2 = FirebaseDatabase.getInstance().getReference("/messages")

    }
}


class ChatRow(val text: String, val user: User) : Item<GroupieViewHolder>() {
    override fun bind(viewHolder: GroupieViewHolder, position: Int) {
        viewHolder.itemView.chat_text.text = text

        var uri = user.profileImg
        val img = viewHolder.itemView.profileImg
        Picasso.get().load(uri).into(img)
    }

    override fun getLayout(): Int {
        return R.layout.chat_row
    }
}

class ChatRowGelen(val text: String,val user: User) : Item<GroupieViewHolder>() {
    override fun bind(viewHolder: GroupieViewHolder, position: Int) {
        viewHolder.itemView.chat_text_right.text = text
        var uri = user.profileImg
        val img = viewHolder.itemView.profileImgRight
        Picasso.get().load(uri).into(img)
    }

    override fun getLayout(): Int {
        return R.layout.chat_row_right
    }
}