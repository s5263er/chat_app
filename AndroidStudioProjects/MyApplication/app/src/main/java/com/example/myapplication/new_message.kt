package com.example.myapplication

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.renderscript.ScriptGroup
import android.util.Log
import android.view.LayoutInflater
import android.view.Menu
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
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
        val adapter = GroupAdapter<GroupieViewHolder>()

        supportActionBar?.title = "Start a chat"

        usersArray = arrayListOf<User>()
        tempUsers = arrayListOf<User>()

        retrieveUsers()
        Log.d("retrieve sonrasi" ,"${tempUsers.size} tempusersin buyuklugu")






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

    private fun retrieveUsers() {
        val database = Firebase.database("https://ceptechat-default-rtdb.europe-west1.firebasedatabase.app/")
        val ref = database.getReference("/users")
        ref.addListenerForSingleValueEvent(object : ValueEventListener {

            override fun onDataChange(snapshot: DataSnapshot) {

                snapshot.children.forEach {
                    val user = it.getValue(User::class.java)
                    usersArray.add(user!!)
                }
                tempUsers.addAll(usersArray)
                Log.d("alttaki tempuser","alttaki tempuser da bu kadar ${tempUsers.size}")
                listview_new_msg.adapter = CompanyAdapter(getUsers(tempUsers))
                /*tempUsers.forEach {
                    adapter.add(UserItem(it))
                    Log.d("search","kac kere girecek bakalim")
                }
                adapter.addAll(UserItem(tempUsers))*/
                //listview_new_msg.adapter = adapter


                //adapter.clear()


                /*snapshot.children.forEach {
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

                    }*/


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
    }

    override fun getLayout(): Int {
        return R.layout.message_row_user
    }

}
private fun getUsers(users: ArrayList<User>): MutableList<User> {
    val companies = users.toMutableList()

    return companies
}


abstract class CompanyAdapter2 (val companies : MutableList<User>) : RecyclerView.Adapter<CompanyAdapter.CompanyViewHolder>() {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CompanyViewHolder {
        val v = LayoutInflater.from(parent.context).inflate(R.layout.new_message_layout,parent,false)
        return CompanyViewHolder(v)

    }

    override fun getItemCount(): Int {
        return companies.size
    }
    fun add(item:User, position:Int) {
        companies.add(position, item)
        notifyItemInserted(position)
    }
    override fun onBindViewHolder(holder: CompanyViewHolder, position: Int) {
        val company = companies[position]
        Log.d("myadapter","${companies.size} bakalim company sizeina")
        //holder.itemView.search_username.text= "company.username"
        if(company != null){
            //****************************************************************************\\
            holder.itemView.search_username.text= company!!.username //error burada
            Picasso.get().load(company!!.profileImg).into(holder.itemView.search_profileImg)
        }
    }


    class CompanyViewHolder(itemView: View): RecyclerView.ViewHolder(itemView)
}


