package com.example.myapplication.Fragments

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
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.myapplication.R
import com.example.myapplication.User
import com.example.myapplication.UserAdapter
import com.google.firebase.auth.FirebaseAuth
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


class SearchFragment :  Fragment() {
    private var userAdapter: UserAdapter? = null
    private var mUsers: List<User>? = null

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view: View = inflater.inflate(R.layout.new_message_layout,container,false)
        mUsers = ArrayList()
        retrieveAllUsers()
        return view
    }

    private fun retrieveAllUsers() {
        var firbaseUserID = FirebaseAuth.getInstance().currentUser!!.uid
        val database = Firebase.database("https://ceptechat-default-rtdb.europe-west1.firebasedatabase.app/")
        val ref = database.getReference("/users")

        ref.addValueEventListener(object: ValueEventListener{
            override fun onDataChange(snapshot: DataSnapshot) {
                (mUsers as ArrayList<User>).clear()
                snapshot.children.forEach{
                    val user: User? = snapshot.getValue(User::class.java)
                    if(user?.uid != FirebaseAuth.getInstance().uid){
                        if (user != null) {
                            (mUsers as ArrayList<User>).add(user)
                        }
                    }
                }
                userAdapter = UserAdapter(context!!,mUsers!!,false)
            }

            override fun onCancelled(error: DatabaseError) {
            }

        })

    }
    private fun searchForUsers(str: String){
        var firbaseUserID = FirebaseAuth.getInstance().currentUser!!.uid
        val database = Firebase.database("https://ceptechat-default-rtdb.europe-west1.firebasedatabase.app/")
        val queryUsers = database.getReference("/users").orderByChild("/username")
            .startAt(str).endAt(str + "\uf8ff")
        queryUsers.addValueEventListener(object : ValueEventListener{
            override fun onDataChange(snapshot: DataSnapshot) {
                (mUsers as ArrayList<User>).clear()
                snapshot.children.forEach{
                    val user: User? = snapshot.getValue(User::class.java)
                    if(user?.uid != FirebaseAuth.getInstance().uid){
                        if (user != null) {
                            (mUsers as ArrayList<User>).add(user)
                        }
                    }
                }
                userAdapter = UserAdapter(context!!,mUsers!!,false)
            }

            override fun onCancelled(error: DatabaseError) {
            }

        })
    }
}