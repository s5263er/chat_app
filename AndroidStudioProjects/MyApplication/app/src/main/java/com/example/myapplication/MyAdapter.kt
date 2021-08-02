package com.example.myapplication

import android.content.Context
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import android.widget.Toast
import androidx.recyclerview.widget.RecyclerView

import com.squareup.picasso.Picasso
import de.hdodenhof.circleimageview.CircleImageView
import kotlinx.android.synthetic.main.message_row_user.view.*

class UserAdapter(
        mContext: Context,
        mUsers: List<User>,
        isChatCheck: Boolean
) : RecyclerView.Adapter<UserAdapter.ViewHolder?>()
{
    private val mContext: Context
    private val mUsers: List<User>
    private var isChatCheck: Boolean

    init {
        this.mUsers = mUsers
        this.mContext = mContext
        this.isChatCheck = isChatCheck
    }
    override fun onCreateViewHolder(viewGroup: ViewGroup, viewType: Int): ViewHolder {
        val view: View = LayoutInflater.from(mContext).inflate(R.layout.new_message_layout,viewGroup,false)
        return UserAdapter.ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val user: User = mUsers[position]
        holder.itemView.search_username.text = user.username
        if(user.profileImg != null){
            Picasso.get().load(user.profileImg).into(holder.itemView.search_profileImg)
            //Picasso.get().load(user.profileImg).into(holder.profileImg)
        }

    }

    override fun getItemCount(): Int {
        return mUsers.size
    }

    class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView){
        var userName: TextView
        var profileImg: CircleImageView


    init {
        userName = itemView.findViewById(R.id.search_username)
        profileImg = itemView.findViewById(R.id.search_profileImg)
    }

    }


}


/*class CompanyAdapter(val companies : MutableList<User>) : RecyclerView.Adapter<CompanyAdapter.CompanyViewHolder>() {
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
}*/