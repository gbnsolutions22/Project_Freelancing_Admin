package com.gbnsolutions.projectfreelancingadmin.fragments

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import com.gbnsolutions.projectfreelancingadmin.R
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase

class CreateSlotFragment : Fragment() {
    lateinit var slot: EditText
    lateinit var createSlot: Button
    lateinit var refUsers: DatabaseReference
    lateinit var randomKey: String
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val view = inflater.inflate(R.layout.fragment_create_slot, container, false)
        refUsers = FirebaseDatabase.getInstance().reference.child("Slots")
        randomKey  = refUsers.push().key.toString()
        slot = view.findViewById(R.id.enterslot)
        createSlot = view.findViewById(R.id.createslot)
        createSlot.setOnClickListener {
            if (slot.text.equals("")){
                Toast.makeText(context,"Please Enter Domain Name", Toast.LENGTH_SHORT).show()
            } else {
                val hashMap = HashMap<String, Any>()
                hashMap["slot"] = slot.text.toString()
                hashMap["slotid"] = randomKey
                refUsers.child(randomKey).updateChildren(hashMap)
                    .addOnCompleteListener { task ->
                        if (task.isSuccessful) {
                            Toast.makeText(
                                context,
                                "Slot Created successfully!",
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
}