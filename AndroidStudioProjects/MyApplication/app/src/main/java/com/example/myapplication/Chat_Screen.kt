package com.example.myapplication

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.recyclerview.widget.RecyclerView
import com.xwray.groupie.GroupAdapter
import com.xwray.groupie.GroupieViewHolder
import com.xwray.groupie.Item

class Chat_Screen : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_chat__screen)



        val user = intent.getParcelableExtra<User>(new_message.USER_KEY)
        supportActionBar?.title = user?.username.toString()

        val adapter = GroupAdapter<GroupieViewHolder>()
    }
}

class ChatRow(id: Long) : Item<GroupieViewHolder>(id) {
    override fun bind(viewHolder: GroupieViewHolder, position: Int) {
        TODO("Not yet implemented")
    }

    override fun getLayout(): Int {
        return R.layout.chat_row
    }
}

class ChatRowGelen(id: Long) : Item<GroupieViewHolder>(id) {
    override fun bind(viewHolder: GroupieViewHolder, position: Int) {
        TODO("Not yet implemented")
    }

    override fun getLayout(): Int {
        return R.layout.chat_row_right
    }
}