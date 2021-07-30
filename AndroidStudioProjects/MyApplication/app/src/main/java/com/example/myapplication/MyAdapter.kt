package com.example.myapplication

import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.recyclerview.widget.RecyclerView
import com.squareup.picasso.Picasso
import kotlinx.android.synthetic.main.message_row_user.view.*

class CompanyAdapter(val companies : MutableList<User>) : RecyclerView.Adapter<CompanyAdapter.CompanyViewHolder>() {
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