package com.gbnsolutions.projectfreelancingadmin.fragments

import android.Manifest
import android.app.Activity
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.gbnsolutions.projectfreelancingadmin.R
import com.gbnsolutions.projectfreelancingadmin.model.Domain
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.google.firebase.storage.FirebaseStorage
import de.hdodenhof.circleimageview.CircleImageView

class CreateFragment : Fragment() {

    lateinit var domainName: EditText
    lateinit var createDomain: Button
    lateinit var image: CircleImageView
    lateinit var refUsers: DatabaseReference
    lateinit var imageLink: String
    lateinit var randomKey: String

    private var REQUEST_SELECT_IMAGE = 101
    lateinit var uri: Uri
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val view =  inflater.inflate(R.layout.fragment_create, container, false)
        domainName = view.findViewById(R.id.domain)
        createDomain = view.findViewById(R.id.createdomain)
        image = view.findViewById(R.id.domainpic)
        refUsers = FirebaseDatabase.getInstance().reference.child("Domains")
//        imageLink = "https://firebasestorage.googleapis.com/v0/b/gbn-avengers.appspot.com/o/Login%2Fic_profile.png?alt=media&token=7ca935a8-3409-48eb-a425-37aced50b88f"
        randomKey  = refUsers.push().key.toString()

        image.setOnClickListener {
            if (ContextCompat.checkSelfPermission(context as Activity,
                    Manifest.permission.READ_EXTERNAL_STORAGE)
                != PackageManager.PERMISSION_GRANTED) {

                // Permission is not granted
                // Should we show an explanation?
                if (ActivityCompat.shouldShowRequestPermissionRationale(
                        context as Activity,
                        Manifest.permission.READ_EXTERNAL_STORAGE)) {
                    // Show an explanation to the user *asynchronously* -- don't block
                    // this thread waiting for the user's response! After the user
                    // sees the explanation, try again to request the permission.
                } else {
                    // No explanation needed, we can request the permission.
                    ActivityCompat.requestPermissions(context as Activity,
                        arrayOf(Manifest.permission.READ_EXTERNAL_STORAGE),
                        42)
                }
            } else {
                // Permission has already been granted
                takePicture()
            }
        }

        createDomain.setOnClickListener {
            if (!uri!!.equals(null)) {
                Upload(uri)
            }
            if (domainName.text.equals("")){
                Toast.makeText(context,"Please Enter Domain Name",Toast.LENGTH_SHORT).show()
            } else {
                val hashMap = HashMap<String,Any>()
                hashMap["domain"] = domainName.text.toString()
                hashMap["domainpic"] = imageLink
                refUsers.child(randomKey).updateChildren(hashMap)
                    .addOnCompleteListener { task ->
                        if (task.isSuccessful) {
                            Toast.makeText(
                                context,
                                "Domain Created successfully!",
                                Toast.LENGTH_SHORT
                            ).show()
                        } else {
                            Toast.makeText(context, "Something went wrong!", Toast.LENGTH_SHORT)
                                .show()
                        }
                    }
            }
        }
        return view
    }
    private fun takePicture() {
        val i = Intent(Intent.ACTION_PICK).setType("image/*")
        startActivityForResult(i,REQUEST_SELECT_IMAGE)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode==REQUEST_SELECT_IMAGE&&resultCode== AppCompatActivity.RESULT_OK){
            uri = data?.data!!
            image.setImageURI(uri)
        }
    }
    private fun Upload(uri: Uri) {
        val firebaseStorage = FirebaseStorage.getInstance().reference.child("pics").child(
            refUsers.toString()
        )
        firebaseStorage.putFile(uri).addOnSuccessListener {
            firebaseStorage.downloadUrl.addOnSuccessListener {i->
                imageLink = i.toString()
            }
        }
    }
}