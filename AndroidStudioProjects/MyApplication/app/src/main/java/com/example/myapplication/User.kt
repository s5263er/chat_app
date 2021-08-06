package com.example.myapplication

import android.os.Parcelable
import com.xwray.groupie.GroupieViewHolder
import com.xwray.groupie.Item
import kotlinx.android.parcel.Parcelize



data class Map(val latitude:String, val longitude: String){
    constructor():this("","")
}

@Parcelize
class User(val uid : String? = null,val username : String? = null,val profileImg : String? = null,val storyUri : String? = null,val storyTime : Long,val storyDuration: Int,val storyDesc: String): Parcelable{
    constructor() : this("","","https://blog.konusarakogren.com/wp-content/uploads/2018/10/201651316480.png","",
    0,0,"")
}
class Message(val type: String,val id: String, val data: String, val from: String,val to:String, val time: Long){
    constructor() : this("text","","","","",System.currentTimeMillis())
}





