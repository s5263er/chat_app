package com.example.myapplication

import android.content.DialogInterface
import android.content.Intent
import android.graphics.Color
import android.os.Bundle
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import android.widget.ImageView
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.DividerItemDecoration
import com.example.myapplication.new_message.Companion.USER_KEY
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
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
import kotlinx.android.synthetic.main.chat_row.view.*
import kotlinx.android.synthetic.main.latest_msg_row.*
import kotlinx.android.synthetic.main.latest_msg_row.view.*
import kotlinx.android.synthetic.main.menu_layout.*
import java.text.SimpleDateFormat
import java.util.*
import kotlin.collections.HashMap


class Menu : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.menu_layout)

        bottom_navigation.setOnItemSelectedListener {
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
                    Log.d("story","girdik")
                    val intent = Intent(this, StoryActivity::class.java)
                    startActivity(intent)
                    finish()
                }
            }
            true
        }
        listview_menu.adapter = adapter
        listview_menu.addItemDecoration(DividerItemDecoration(this, DividerItemDecoration.VERTICAL))
        supportActionBar?.title = "Chat Inn"


        adapter.setOnItemClickListener { item, view ->
            val intent = Intent(this, Chat_Screen::class.java)
            val intent_story = Intent(this,story_splash_screen::class.java)
            val row = item as LatestMsgRow
            if(checkStoryValid(row.chatPartnerUser)){
                val dialogBuilder = AlertDialog.Builder(this)
                dialogBuilder.setMessage("${row.chatPartnerUser?.username} shared a story ${System.currentTimeMillis()/(1000*60)-(row.chatPartnerUser?.storyTime)!!/(1000*60) } minutes ago do you want to watch ?")
                    // if the dialog is cancelable
                    .setCancelable(true)
                    // positive button text and action
                    .setPositiveButton("Story", DialogInterface.OnClickListener {
                            dialog, id ->
                        Log.d("story","profileimg click algilandi")
                        intent_story.putExtra(USER_KEY,row.chatPartnerUser)
                        startActivity(intent_story)
                        finish()
                    })
                    // negative button text and action
                    .setNegativeButton("Chat", DialogInterface.OnClickListener {
                            dialog, id ->
                        intent.putExtra(USER_KEY, row.chatPartnerUser)
                        startActivity(intent)

                        dialog.cancel()
                    })

                // create dialog box
                val alert = dialogBuilder.create()
                // set title for alert dialog box
                alert.setTitle("${row.chatPartnerUser!!.username}'s Story")
                // show alert dialog
                alert.show()
            }
            else{
                intent.putExtra(USER_KEY, row.chatPartnerUser)
                startActivity(intent)
            }

        }
        listenLatestMsg()
    }
    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when(item.itemId){
            R.id.menu_logout -> {
                FirebaseAuth.getInstance().signOut()
                val intent = Intent(this, MainActivity::class.java)
                intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TASK.or(Intent.FLAG_ACTIVITY_NEW_TASK)
                startActivity(intent)
                finish()
            }
        }
        return super.onOptionsItemSelected(item)
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.menu_res, menu)
        return super.onCreateOptionsMenu(menu)
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
                viewHolder.itemView.latest_msg_text.setTextColor(Color.DKGRAY)


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
            if(minutes < 10 && hours+3 >= 10){
                val saatci = "${hours+3}:0$minutes"
                val rolex = "$timer $saatci"
                viewHolder.itemView.latest_msg_time.text = rolex.toString()
            }
            else if(hours+3 < 10 && minutes >= 10){
                val saatci = "0${hours+3}:$minutes"
                val rolex = "$timer $saatci"
                viewHolder.itemView.latest_msg_time.text = rolex.toString()
            }
            else if(hours+3 >= 10 && minutes >= 10){
                val saatci = "${hours+3}:$minutes"
                val rolex = "$timer $saatci"
                viewHolder.itemView.latest_msg_time.text = rolex.toString()
            }
            else if(hours+3 < 10 && minutes < 10) {
                val saatci = "0${hours+3}:0$minutes"
                val rolex = "$timer $saatci"
                viewHolder.itemView.latest_msg_time.text = rolex.toString()
            }





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
                    val story_valid = checkStoryValid(chatPartnerUser!!)

                    if(story_valid!!){
                        viewHolder.itemView.latest_msg_imageview.borderColor = Color.MAGENTA
                        viewHolder.itemView.latest_msg_imageview.borderWidth = 30

                    }

                    Picasso.get().load((chatPartnerUser?.profileImg)).into(viewHolder.itemView.latest_msg_imageview)
                }
            })



        }

        private fun checkStoryValid(user: User) : Boolean?{
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
}