package com.example.myapplication

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase
import com.squareup.picasso.Picasso
import com.xwray.groupie.GroupAdapter
import com.xwray.groupie.GroupieAdapter

import com.xwray.groupie.GroupieViewHolder
import com.xwray.groupie.Item
import kotlinx.android.synthetic.main.message_row_user.view.*
import kotlinx.android.synthetic.main.new_message_layout.*
import kotlinx.android.synthetic.main.select_photo.view.*


class new_message : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.new_message_layout)

        supportActionBar?.title = "Start a chat"

        retrieveUsers()
    }

    private fun retrieveUsers(){
        val database = Firebase.database("https://ceptechat-default-rtdb.europe-west1.firebasedatabase.app/")
        val ref = database.getReference("/users")
        ref.addListenerForSingleValueEvent(object: ValueEventListener{

            override fun onDataChange(snapshot: DataSnapshot) {
                val adapter = GroupAdapter<GroupieViewHolder>()

                snapshot.children.forEach {
                    Log.d("new_msg","${it.toString()}")
                   val user = it.getValue(User::class.java)
                    if(user != null)
                    {
                        adapter.add(UserItem(user))
                        Log.d("new_message","User is not null: ${user.username}")
                    }
                    else
                    {
                        Log.d("new_message","user is null can't retrieve data")
                    }

                }
                listview_new_msg.adapter = adapter
            }
            override fun onCancelled(error: DatabaseError) {
            }


        })

    }
}
class UserItem(val user: User): Item<GroupieViewHolder>(){
    override fun bind(viewHolder: GroupieViewHolder, position: Int) {
        viewHolder.itemView.search_username.text = user.username
        if(user.profileImg != null){
            Picasso.get().load(user.profileImg).into(viewHolder.itemView.search_profileImg)
        }

        Log.d("new_message","Usernames are: ${user.username}")
    }

    override fun getLayout(): Int {
        return R.layout.message_row_user
    }

}

