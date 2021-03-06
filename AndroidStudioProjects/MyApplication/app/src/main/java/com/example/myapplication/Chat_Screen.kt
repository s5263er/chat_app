package com.example.myapplication
import android.Manifest
import android.annotation.SuppressLint
import android.app.Activity
import android.app.AlertDialog
import android.content.Intent
import android.content.pm.PackageManager
import android.location.Location
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import android.widget.ArrayAdapter
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat.startActivity
import com.example.myapplication.Chat_Screen.Companion.currentUser
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

import com.google.android.gms.location.LocationServices
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.FirebaseStorage
import com.squareup.picasso.Picasso
import com.xwray.groupie.GroupAdapter
import com.xwray.groupie.GroupieViewHolder
import com.xwray.groupie.Item
import kotlinx.android.synthetic.main.activity_chat__screen.*
import kotlinx.android.synthetic.main.activity_chat__screen.view.*
import kotlinx.android.synthetic.main.activity_maps.view.*
import kotlinx.android.synthetic.main.chat_row.view.*
import kotlinx.android.synthetic.main.chat_row_img.view.*
import kotlinx.android.synthetic.main.chat_row_img_right.view.*
import kotlinx.android.synthetic.main.chat_row_map.view.*
import kotlinx.android.synthetic.main.chat_row_map_right.view.*
import kotlinx.android.synthetic.main.chat_row_right.view.*
import kotlinx.android.synthetic.main.latest_msg_row.view.*
import java.text.SimpleDateFormat
import java.util.*
import kotlin.concurrent.timer


class Chat_Screen : AppCompatActivity(), OnMapReadyCallback {
    var userTo: User? = null
    private lateinit var mMap: GoogleMap
   private lateinit var fusedLocationClient: FusedLocationProviderClient
    companion object{
        var currentUser: User? = null
    }

    @SuppressLint("MissingPermission")
    //var currentLocation: Location =
        //LocationServices.FusedLocationApi.getLastLocation(googleApiClient)

    private fun currUser(){
        val uid = FirebaseAuth.getInstance().uid
        val database = Firebase.database("https://ceptechat-default-rtdb.europe-west1.firebasedatabase.app/")
        val ref = database.getReference("/users/$uid")
        ref.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                currentUser = snapshot.getValue(User::class.java)
                Log.d("chat", "current user is ${currentUser!!.username}")
            }

            override fun onCancelled(error: DatabaseError) {

            }
        })
    }


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_chat__screen)
        currUser()

        listview_chat_screen.adapter = adapter





        userTo = intent.getParcelableExtra<User>(new_message.USER_KEY)
        supportActionBar?.title = userTo?.username.toString()



        send_button.setOnClickListener {
            sendMsgToFirebase()
        }
        select_photo_chat.setOnClickListener {
            val builder = AlertDialog.Builder(this)

            val arrayAdapter = ArrayAdapter<String>(
                    this,
                    android.R.layout.select_dialog_singlechoice)
            arrayAdapter.add("Send Image")
            arrayAdapter.add("Send Current Location")
            arrayAdapter.add("Send PDF")

            builder.setTitle("Select One Option")

            builder.setAdapter( arrayAdapter
            ) { _, i ->
                if (i == 0){
                    val intent = Intent(Intent.ACTION_PICK)
                    intent.type = "image/*"
                    startActivityForResult(intent, 0)
                }

                if (i == 1){
                    fusedLocationClient = LocationServices.getFusedLocationProviderClient(this)


                    if (ActivityCompat.checkSelfPermission(
                                    this,
                                    Manifest.permission.ACCESS_FINE_LOCATION
                            ) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(
                                    this,
                                    Manifest.permission.ACCESS_COARSE_LOCATION
                            ) != PackageManager.PERMISSION_GRANTED
                    ) {
                        if (ActivityCompat.shouldShowRequestPermissionRationale(this,
                                        android.Manifest.permission.ACCESS_FINE_LOCATION)) {
                        } else {
                            // No explanation needed, we can request the permission.
                            ActivityCompat.requestPermissions(this,
                                    arrayOf(android.Manifest.permission.ACCESS_FINE_LOCATION),
                                    0)
                        }
                    }
                    else{
                        fusedLocationClient.lastLocation
                                .addOnSuccessListener { location : Location? ->
                                    Log.d("location","${location.toString()},${location?.latitude}")
                                    sendGPS(location!!.latitude,location!!.longitude)
                                    Log.d("location","girdik4")
                                }
                        fusedLocationClient.lastLocation.addOnFailureListener {
                            Log.d("location bad","${it.toString()}, ${it.message}")
                        }
                    }
                }

                if (i == 2){
                    val intent = Intent(Intent.ACTION_GET_CONTENT)
                    intent.type = "application/pdf"
                    startActivityForResult(Intent.createChooser(intent, "Select File Documents"),440)
                }
            }
            builder.show()

        }
        adapter.setOnItemClickListener { item, view ->
            Log.d("adapter","click girdi")
            val intent = Intent(this, MapsActivity::class.java)
            Log.d("adapter","click girdi")
            if(item is ChatRowMap){
            val row = item as ChatRowMap
                if(row.latitude != null){
                    intent.putExtra("username",currentUser?.username)
                    intent.putExtra("Long",row.longitude)
                    intent.putExtra("Lat",row.latitude)
                    startActivity(intent) }
            }
            if (item is ChatRowMapRight){
                val row = item as ChatRowMapRight
                if(row.latitude != null){
                    intent.putExtra("username", userTo?.username)
                    intent.putExtra("Long",row.longitude)
                    intent.putExtra("Lat",row.latitude)
                    startActivity(intent)}
            }

            }




        chatListener()
    }
    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when(item.itemId){
            R.id.chat_to_menu -> {
                val intent = Intent(this, com.example.myapplication.Menu::class.java)
                startActivity(intent)
                finish()
            }
        }
        return super.onOptionsItemSelected(item)
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.menu_chat, menu)
        return super.onCreateOptionsMenu(menu)
    }
    val adapter = GroupAdapter<GroupieViewHolder>()
    private fun local(latitudeFinal: String, longitudeFinal: String): String {
        return "https://maps.googleapis.com/maps/api/staticmap?center=$latitudeFinal,$longitudeFinal&zoom=18&size=280x280&markers=color:red|$latitudeFinal,$longitudeFinal"
    }
    private fun clickImageMapChat(latitude: String, longitude: String) {
        val uri = String.format("geo:%s,%s?z=17&q=%s,%s", latitude, longitude, latitude, longitude)
        val intent = Intent(Intent.ACTION_VIEW, Uri.parse(uri))
        startActivity(intent)
    }
     override fun onMapReady(googleMap: GoogleMap) {
        mMap = googleMap
        // Add a marker in Sydney and move the camera
        val sydney = LatLng(-34.0, 151.0)
        mMap.addMarker(MarkerOptions().position(sydney).title("Marker in Sydney"))
        mMap.moveCamera(CameraUpdateFactory.newLatLng(sydney))
    }
    private fun sendGPS(latitude: Double, longitude: Double){
        val user = intent.getParcelableExtra<User>(new_message.USER_KEY)
        val from = FirebaseAuth.getInstance().uid
        val to = user?.uid
        val database = Firebase.database("https://ceptechat-default-rtdb.europe-west1.firebasedatabase.app/")
        //val ref = database.getReference("/messages").push()
        val ref = database.getReference("/user-user/$from/$to").push()

        val toRef = database.getReference("/user-user/$to/$from").push()

        val chatMsg = Message("map", ref.key!!, "$latitude,$longitude", from!!, to!!, System.currentTimeMillis())

        ref.setValue(chatMsg).addOnFailureListener {
            Log.d("Chat_Screen", "Failed to send msg to firebase")
        }
        toRef.setValue(chatMsg).addOnSuccessListener {
            listview_chat_screen.scrollToPosition(adapter.itemCount - 1)
        }
        val latestMsg = database.getReference("/latest-messages/$from/$to")
        val latestMsgSender = database.getReference("/latest-messages/$to/$from")
        latestMsg.setValue(chatMsg)
        latestMsgSender.setValue(chatMsg)
    }
    var selectedPhotoUri: Uri? = null
    private fun uploadImageToFirebase(){

        if(selectedPhotoUri == null) return
        val filename = UUID.randomUUID().toString()

        val ref = FirebaseStorage.getInstance().getReference("/images/$filename")

        ref.putFile(selectedPhotoUri!!).addOnSuccessListener {
            Log.d("select photo", "uploaded image:")
            ref.downloadUrl.addOnSuccessListener {
                Log.d("Select photo", "url downloaded correctly location $it")
                val urimiz = it.toString()
                val user = intent.getParcelableExtra<User>(new_message.USER_KEY)
                val from = FirebaseAuth.getInstance().uid
                val to = user?.uid
                val database = Firebase.database("https://ceptechat-default-rtdb.europe-west1.firebasedatabase.app/")

                val refChatImg = database.getReference("/user-user/$from/$to").push()

                val refToChatImg = database.getReference("/user-user/$to/$from").push()

                val chatMsg = Message(
                    "img",
                    refChatImg.key!!,
                    it.toString(),
                    from!!,
                    to!!,
                    System.currentTimeMillis()
                )

                val uid = FirebaseAuth.getInstance().uid
                refToChatImg.setValue(chatMsg).addOnSuccessListener {
                    //adapter.add(ChatRowImg(urimiz, currentUser!!))
                    val latestMsg = database.getReference("/latest-messages/$from/$to")
                    val latestMsgSender = database.getReference("/latest-messages/$to/$from")
                    latestMsg.setValue(chatMsg)
                    latestMsgSender.setValue(chatMsg)
                    Log.d("uploadimgto", "Sent msg to firebaseimg adapter ekledik bakalim")
                    listview_chat_screen.scrollToPosition(adapter.itemCount - 1)
                }
                refChatImg.setValue(chatMsg).addOnSuccessListener {

                    //adapter.add(ChatRowImg(urimiz, userTo!!))
                    Log.d("uploadimgfrom", "Sent msg to firebaseimg adapter ekledik bakalim")
                    listview_chat_screen.scrollToPosition(adapter.itemCount - 1)
                }
                //val ref = database.getReference("/user-user/$uid/profileImg")
                //ref.setValue(it.toString())

            }

        }

    }
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if(requestCode == 0 && resultCode == Activity.RESULT_OK && data != null){
            selectedPhotoUri = data.data

            val bitmap = MediaStore.Images.Media.getBitmap(contentResolver, selectedPhotoUri)
            uploadImageToFirebase()

            //selectphoto_circle_image.setImageBitmap(bitmap)
        }
    }
    private fun sendMsgToFirebase(){
        val user = intent.getParcelableExtra<User>(new_message.USER_KEY)
        val from = FirebaseAuth.getInstance().uid
        val to = user?.uid
        val text = send_chat_box.text.toString()
        val database = Firebase.database("https://ceptechat-default-rtdb.europe-west1.firebasedatabase.app/")
        //val ref = database.getReference("/messages").push()
        val ref = database.getReference("/user-user/$from/$to").push()

        val toRef = database.getReference("/user-user/$to/$from").push()

        val chatMsg = Message("text", ref.key!!, text, from!!, to!!, System.currentTimeMillis())

        ref.setValue(chatMsg).addOnFailureListener {
            Log.d("Chat_Screen", "Failed to send msg to firebase")
        }
        toRef.setValue(chatMsg).addOnSuccessListener {
            Log.d("Chat_Screen", "Sent msg to firebase")
            send_chat_box.text.clear()
            listview_chat_screen.scrollToPosition(adapter.itemCount - 1)
        }
        val latestMsg = database.getReference("/latest-messages/$from/$to")
        val latestMsgSender = database.getReference("/latest-messages/$to/$from")
        latestMsg.setValue(chatMsg)
        latestMsgSender.setValue(chatMsg)


    }

    private fun bringData(){
        val adapter = GroupAdapter<GroupieViewHolder>()
        //adapter.add(ChatRow("dummy"))

        listview_chat_screen.adapter = adapter
    }
    private fun chatListener(){
        val from = FirebaseAuth.getInstance().uid
        val to = userTo!!.uid
        val database = Firebase.database("https://ceptechat-default-rtdb.europe-west1.firebasedatabase.app/")
        val ref = database.getReference("/user-user/$from/$to")

        val urlref = FirebaseDatabase.getInstance("https://ceptechat-default-rtdb.europe-west1.firebasedatabase.app/").getReference(
            "/user-user/$from/$to"
        )
        urlref.addChildEventListener(object : ChildEventListener {
            override fun onChildAdded(snapshot: DataSnapshot, previousChildName: String?) {
                val msg = snapshot.getValue(Message::class.java)

                if (msg == null) {
                    return
                }
                Log.d("chat listener", "${msg.type} type bu ")
                if (msg.type == "text") {
                    val time = clockCu(msg)
                    if (msg != null) {
                        if (msg.from == FirebaseAuth.getInstance().uid) {
                            adapter.add(ChatRow(msg.data, currentUser!!,time))
                            Log.d("chat", "yollanan")
                        } else {
                            //val userFr = intent.getParcelableExtra<User>(new_message.USER_KEY)
                            adapter.add(ChatRowGelen(msg.data, userTo!!,time))
                            Log.d("chat", "gelen")
                        }

                    }
                } else if (msg.type == "img") {
                    val time = clockCu(msg)
                    if (msg != null) {
                        if (msg.from == FirebaseAuth.getInstance().uid) {
                            Log.d("yollanan img chat listener", "${msg.data} data bu")
                            adapter.add(ChatRowImg(msg.data, currentUser!!,time))
                        } else {
                            //val userFr = intent.getParcelableExtra<User>(new_message.USER_KEY)
                            adapter.add(ChatRowImgRight(msg.data, userTo!!,time))
                            Log.d("chat", "gelenimg")
                            Log.d("chat", "${userTo!!.username} userto bu")
                        }
                    }

                }
                else if (msg.type == "map") {
                    if (msg != null) {
                        val index = msg.data.indexOf(",")
                        val lat = msg.data.substring(0,index).toDouble()
                        val long = msg.data.substring(index+1).toDouble()
                        val time = clockCu(msg)

                        if (msg.from == FirebaseAuth.getInstance().uid) {
                            adapter.add(ChatRowMap(lat,long, currentUser!!,time))
                        } else {
                            adapter.add(ChatRowMapRight(lat,long,userTo!!,time))
                        }
                    }
                }
                listview_chat_screen.scrollToPosition(adapter.itemCount - 1)

            }

            override fun onChildChanged(snapshot: DataSnapshot, previousChildName: String?) {

            }

            override fun onCancelled(error: DatabaseError) {

            }

            override fun onChildMoved(snapshot: DataSnapshot, previousChildName: String?) {

            }

            override fun onChildRemoved(snapshot: DataSnapshot) {

            }
        })
        //val ref2 = FirebaseDatabase.getInstance().getReference("/messages")

    }
}
private fun getDate(milliSeconds: Long, dateFormat: String?): String? {
    // Create a DateFormatter object for displaying date in specified format.
    val formatter = SimpleDateFormat(dateFormat)

    // Create a calendar object that will convert the date and time value in milliseconds to date.
    val calendar: Calendar = Calendar.getInstance()
    calendar.setTimeInMillis(milliSeconds)
    return formatter.format(calendar.getTime())
}
private fun clockCu(chatMsg: Message) : String{
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
        return rolex.toString()
    }
    else if(hours+3 < 10 && minutes >= 10){
        val saatci = "0${hours+3}:$minutes"
        val rolex = "$timer $saatci"
        return rolex.toString()
    }
    else if(hours+3 >= 10 && minutes >= 10){
        val saatci = "${hours+3}:$minutes"
        val rolex = "$timer $saatci"
        return rolex.toString()
    }
    else if(hours+3 < 10 && minutes < 10) {
        val saatci = "0${hours+3}:0$minutes"
        val rolex = "$timer $saatci"
        return rolex.toString()

    }
    return "09:05 atam dolmabahcede impossible case"


}

class ChatRowMap(val latitude: Double,val longitude: Double,val user1: User,val time: String)  : Item<GroupieViewHolder>() {
    override fun bind(viewHolder: GroupieViewHolder, position: Int) {
        var uri = currentUser!!.profileImg
        val img = viewHolder.itemView.profileImgMap
        Picasso.get().load(uri).into(img)

        viewHolder.itemView.map_text_saat.text = time
        var uri2 = "https://1000logos.net/wp-content/uploads/2020/05/Google-Maps-logo.jpg"

        val img2 = viewHolder.itemView.map_button
        Picasso.get().load(uri2).into(img2)
    }

    override fun getLayout(): Int {
        return R.layout.chat_row_map
    }
}


class ChatRowMapRight(val latitude: Double,val longitude: Double,val user1: User,val time: String)  : Item<GroupieViewHolder>() {
    override fun bind(viewHolder: GroupieViewHolder, position: Int) {
        var uri = user1!!.profileImg
        val img = viewHolder.itemView.profileImgMapRight
        Picasso.get().load(uri).into(img)

        viewHolder.itemView.map_text_saat_right.text = time

        var uri2 = "https://1000logos.net/wp-content/uploads/2020/05/Google-Maps-logo.jpg"

        val img2 = viewHolder.itemView.map_button_right
        Picasso.get().load(uri2).into(img2)


    }

    override fun getLayout(): Int {
        return R.layout.chat_row_map_right
    }


}


class ChatRow(val text: String, val user1: User,val time: String) : Item<GroupieViewHolder>() {
    override fun bind(viewHolder: GroupieViewHolder, position: Int) {
        viewHolder.itemView.chat_text.text = text
        viewHolder.itemView.chat_row_saat.text = time

        var uri = currentUser!!.profileImg
        val img = viewHolder.itemView.profileImg
        Picasso.get().load(uri).into(img)

    }

    override fun getLayout(): Int {
        return R.layout.chat_row
    }
}

class ChatRowImg(val text: String, val user1: User,val time: String) : Item<GroupieViewHolder>() {
    override fun bind(viewHolder: GroupieViewHolder, position: Int) {
        var uri_img = text
        val img_img = viewHolder.itemView.imageView_chat
        Picasso.get().load(uri_img).into(img_img)
        viewHolder.itemView.img_text_saat.text = time
        Log.d("chatrowimgright", "${user1.username} userimiz bu bakalim tutmus mu yollanan")
        var uri = currentUser!!.profileImg
        val img = viewHolder.itemView.profileImgImg
        Picasso.get().load(uri).into(img)
    }

    override fun getLayout(): Int {
        return R.layout.chat_row_img
    }
}

class ChatRowImgRight(val text: String, val user1: User,val time: String) : Item<GroupieViewHolder>() {
    override fun bind(viewHolder: GroupieViewHolder, position: Int) {
        var uri_img = text
        val img_img = viewHolder.itemView.imageView_chat_right
        Picasso.get().load(uri_img).into(img_img)
        viewHolder.itemView.img_text_saat_right.text = time

        Log.d("chatrowimgright", "${user1.username} userimiz bu bakalim tutmus mu")

        var uri = user1.profileImg
        val img = viewHolder.itemView.profileImgImg_right
        Picasso.get().load(uri).into(img)
    }

    override fun getLayout(): Int {
        return R.layout.chat_row_img_right
    }
}

class ChatRowGelen(val text: String, val user: User,val time: String) : Item<GroupieViewHolder>() {
    override fun bind(viewHolder: GroupieViewHolder, position: Int) {
        viewHolder.itemView.chat_text_right.text = text
        viewHolder.itemView.chat_row_right_time.text = time
        var uri = user.profileImg
        val img = viewHolder.itemView.profileImgRight
        Picasso.get().load(uri).into(img)
    }

    override fun getLayout(): Int {
        return R.layout.chat_row_right
    }
}