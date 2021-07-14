package com.example.myapplication

import android.os.Parcelable
import com.xwray.groupie.GroupieViewHolder
import com.xwray.groupie.Item
import kotlinx.android.parcel.Parcelize
@Parcelize
class User(val uid : String? = null,val username : String? = null,val profileImg : String? = null): Parcelable{
    constructor() : this("","","https://blog.konusarakogren.com/wp-content/uploads/2018/10/201651316480.png")
}
class Message(val id: String, val data: String, val from: String,val to:String, val time: Long){
    constructor() : this("","","","",System.currentTimeMillis())
}





