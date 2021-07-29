package com.example.myapplication

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.Menu
import androidx.appcompat.widget.SearchView
import androidx.recyclerview.widget.DividerItemDecoration
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
import java.util.*
import kotlin.collections.ArrayList


class new_message : AppCompatActivity() {
    private lateinit var usersArray: ArrayList<User>
    private lateinit var tempUsers:  ArrayList<User>
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.new_message_layout)

        supportActionBar?.title = "Start a chat"

        usersArray = arrayListOf<User>()
        tempUsers = arrayListOf<User>()

        retrieveUsers()

    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.menu_search,menu)
        val item = menu?.findItem(R.id.search_user)
        val searchView = item?.actionView as SearchView
        searchView.setOnQueryTextListener(object: SearchView.OnQueryTextListener{
            override fun onQueryTextChange(newText: String?): Boolean {
                tempUsers.clear()
                val searchText = newText!!.toLowerCase(Locale.getDefault())
                if(searchText.isNotEmpty()){
                    usersArray.forEach{
                        Log.d("new_Msg","user arrayda arastirma")
                        if(it.username?.toLowerCase(Locale.getDefault())!!.contains(searchText)){
                            Log.d("new_Msg","TUTAN VARRR")
                            tempUsers.add(it)
                        }
                    }
                    listview_new_msg.adapter?.notifyDataSetChanged()
                }
                else{
                    Log.d("new_Msg","user arrayda arastirma SEARCH EMPTY")
                    tempUsers.clear()
                    tempUsers.addAll(usersArray)
                    listview_new_msg.adapter?.notifyDataSetChanged()
                }
                return false
            }

            override fun onQueryTextSubmit(query: String?): Boolean {
                TODO("Not yet implemented")
            }
        })
        return super.onCreateOptionsMenu(menu)
    }

    companion object{
        val USER_KEY = "USER_KEY"
    }

    private fun retrieveUsers(){
        val database = Firebase.database("https://ceptechat-default-rtdb.europe-west1.firebasedatabase.app/")
        val ref = database.getReference("/users")
        ref.addListenerForSingleValueEvent(object: ValueEventListener{

            override fun onDataChange(snapshot: DataSnapshot) {
                val adapter = GroupAdapter<GroupieViewHolder>()

                snapshot.children.forEach{
                    val user = it.getValue(User::class.java)
                    usersArray.add(user!!)
                }
                tempUsers.addAll(usersArray)




                //adapter.clear()


                    snapshot.children.forEach {
                        //Log.d("new_msg","tempUser boss")
                        val user = it.getValue(User::class.java)

                        if(user != null)
                        {
                            /*if(tempUsers.contains(user))
                            {
                                adapter.add(UserItem(user))
                                Log.d("new_message","tempuserlar eklendi bakalim")
                            }*/
                            adapter.add(UserItem(user))


                            adapter.setOnItemClickListener { item, view ->

                                val userItem = item as UserItem
                                val intent = Intent(view.context,Chat_Screen::class.java)
                                //intent.putExtra(USER_KEY, userItem.user.username)
                                intent.putExtra(USER_KEY,userItem.user)
                                startActivity(intent)
                                finish()
                            }


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

                //Log.d("new_message","Usernames are: ${user.username}")
    }

    override fun getLayout(): Int {
        return R.layout.message_row_user
    }

}

