package com.example.myapplication

import android.content.Intent
import android.graphics.Color
import android.os.Bundle
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.DividerItemDecoration
import com.example.myapplication.new_message.Companion.USER_KEY
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.ChildEventListener
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.ValueEventListener
import com.google.firebase.database.collection.LLRBNode
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase
import com.squareup.picasso.Picasso
import com.xwray.groupie.GroupAdapter
import com.xwray.groupie.GroupieViewHolder
import com.xwray.groupie.Item
import kotlinx.android.synthetic.main.latest_msg_row.view.*
import kotlinx.android.synthetic.main.menu_layout.*
import java.text.SimpleDateFormat
import java.util.*
import kotlin.collections.HashMap


class Menu : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.menu_layout)
        /*bottom_navigation.setOnNavigationItemReselectedListener {
            when(it?.itemId){
                R.id.menu_search1 -> {
                    val intent = Intent(this, new_message::class.java)
                    startActivity(intent)
                }
                R.id.menu_logout1 -> {
                    FirebaseAuth.getInstance().signOut()
                    val intent = Intent(this, MainActivity::class.java)
                    intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TASK.or(Intent.FLAG_ACTIVITY_NEW_TASK)
                    startActivity(intent)
                }
            }

        }*/
        bottom_navigation.setOnItemSelectedListener {
            when(it.itemId){
                R.id.menu_search1 -> {
                    val intent = Intent(this, new_message::class.java)
                    startActivity(intent)
                }
                R.id.menu_logout1 -> {
                    FirebaseAuth.getInstance().signOut()
                    val intent = Intent(this, MainActivity::class.java)
                    intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TASK.or(Intent.FLAG_ACTIVITY_NEW_TASK)
                    startActivity(intent)
                }
            }
            true
        }
        /*bottom_navigation.setOnItemSelectedListener {
            when(it?.itemId){
                R.id.menu_search1 -> {
                    val intent = Intent(this, new_message::class.java)
                    startActivity(intent)
                }
            }
        }*/
        listview_menu.adapter = adapter
        listview_menu.addItemDecoration(DividerItemDecoration(this, DividerItemDecoration.VERTICAL))
        adapter.setOnItemClickListener { item, view ->
            val intent = Intent(this, Chat_Screen::class.java)
            val row = item as LatestMsgRow
            intent.putExtra(USER_KEY, row.chatPartnerUser)
            startActivity(intent)
        }
        listenLatestMsg()
    }
    //The last commit is on 20 March (At the bottom of the page)
    val latestMsgMap = HashMap<String, Message>()
    private fun refreshListViewMsg(){
        adapter.clear()
        val sortedmap = latestMsgMap.values.toSortedSet(compareByDescending { it.time })
        val sortedmap2 = sortedmap.reversed()

        sortedmap2.forEach {
            adapter.add(LatestMsgRow(it))
        }
    }
    private fun listenLatestMsg(){
        val from = FirebaseAuth.getInstance().uid
        val database = Firebase.database("https://ceptechat-default-rtdb.europe-west1.firebasedatabase.app/")
        val ref = database.getReference("/latest-messages/$from")
        ref.addChildEventListener(object : ChildEventListener {
            override fun onCancelled(error: DatabaseError) {
            }

            override fun onChildAdded(snapshot: DataSnapshot, previousChildName: String?) {
                val chatMsg = snapshot.getValue(Message::class.java)
                latestMsgMap[snapshot.key!!] = chatMsg!!
                refreshListViewMsg()
                //adapter.add(LatestMsgRow(chatMsg!!))
            }

            override fun onChildChanged(snapshot: DataSnapshot, previousChildName: String?) {
                val chatMsg = snapshot.getValue(Message::class.java)
                latestMsgMap[snapshot.key!!] = chatMsg!!
                refreshListViewMsg()
                //adapter.add(LatestMsgRow(chatMsg!!))
            }

            override fun onChildRemoved(snapshot: DataSnapshot) {
            }

            override fun onChildMoved(snapshot: DataSnapshot, previousChildName: String?) {
            }

        })
    }
    private fun isLogged(){
        val uid = FirebaseAuth.getInstance().uid
        if(uid == null)
        {
            val intent = Intent(this, MainActivity::class.java)
            intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TASK.or(Intent.FLAG_ACTIVITY_NEW_TASK)
            startActivity(intent)
        }
    }

    /*fun convertDate(dateInMilliseconds: String, dateFormat: String?): String? {
        return DateFormat.format(dateFormat, dateInMilliseconds.toLong()).toString()
    }*/



    class LatestMsgRow(val chatMsg: Message) : Item<GroupieViewHolder>() {
        var chatPartnerUser: User? = null
        override fun bind(viewHolder: GroupieViewHolder, position: Int) {
            Log.d("menu","latest msg type = ${chatMsg.type}")
            if(chatMsg.type == "text"){
                if(chatMsg.data.length > 30){
                    viewHolder.itemView.latest_msg_text.text = "${chatMsg.data.subSequence(0,27)}..."
                }
                else{
                    viewHolder.itemView.latest_msg_text.text = chatMsg.data
                }


            }
            if(chatMsg.type == "img"){
                viewHolder.itemView.latest_msg_text.text = "Image Data"
                viewHolder.itemView.latest_msg_text.setTextColor(Color.parseColor("#571954"))
            }
            if(chatMsg.type == "map"){
                viewHolder.itemView.latest_msg_text.text = "Geo Data"
                viewHolder.itemView.latest_msg_text.setTextColor(Color.parseColor("#070952"))
            }

            val seconds = (chatMsg.time / 1000) % 60
            seconds.toInt().toString()
            val minutes = ((chatMsg.time / (60*1000)) % 60)
            val hours = ((chatMsg.time / (60*60*1000))%24)
            val time = "${hours.toInt()}:${minutes.toInt()}:${seconds.toInt()}"

           // val timer = DateFormat.getDateInstance().format(time)
            val timer = getDate(chatMsg.time,"dd/MM/yyyy")
            val saatci = "${hours+3}:$minutes"
            val rolex = "$timer $saatci"
            viewHolder.itemView.latest_msg_time.text = rolex.toString()

            //viewHolder.itemView.latest_msg_time.text
            var chatPartner: String
            if(chatMsg.from == FirebaseAuth.getInstance().uid){
                chatPartner = chatMsg.to
            }
            else{
                chatPartner = chatMsg.from
            }

            val database = Firebase.database("https://ceptechat-default-rtdb.europe-west1.firebasedatabase.app/")
            val ref = database.getReference("/users/$chatPartner")
            ref.addListenerForSingleValueEvent(object : ValueEventListener {
                override fun onCancelled(error: DatabaseError) {
                }

                override fun onDataChange(snapshot: DataSnapshot) {
                    chatPartnerUser = snapshot.getValue(User::class.java)
                    viewHolder.itemView.latest_msg_username.text = chatPartnerUser?.username
                    Picasso.get().load((chatPartnerUser?.profileImg)).into(viewHolder.itemView.latest_msg_imageview)
                }
            })



        }

        private fun getDate(milliSeconds: Long, dateFormat: String?): String? {
            // Create a DateFormatter object for displaying date in specified format.
            val formatter = SimpleDateFormat(dateFormat)

            // Create a calendar object that will convert the date and time value in milliseconds to date.
            val calendar: Calendar = Calendar.getInstance()
            calendar.setTimeInMillis(milliSeconds)
            return formatter.format(calendar.getTime())
        }

        override fun getLayout(): Int {
            return R.layout.latest_msg_row
        }

    }
    val adapter = GroupAdapter<GroupieViewHolder>()



    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when(item?.itemId){
            R.id.menu_search -> {
                val intent = Intent(this, new_message::class.java)
                startActivity(intent)
            }
            R.id.menu_logout -> {
                FirebaseAuth.getInstance().signOut()
                val intent = Intent(this, MainActivity::class.java)
                intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TASK.or(Intent.FLAG_ACTIVITY_NEW_TASK)
                startActivity(intent)
            }
        }
        return super.onOptionsItemSelected(item)
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.menu_res, menu)
        return super.onCreateOptionsMenu(menu)
    }
}