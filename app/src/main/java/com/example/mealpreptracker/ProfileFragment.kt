package com.example.mealpreptracker

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import com.google.firebase.Firebase
import com.google.firebase.auth.FirebaseAuth
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.database

private const val TAG = "Profile"

class ProfileFragment : Fragment() {
    private lateinit var title: TextView
    private lateinit var welcomeTitle: TextView
    private lateinit var updateProfileBtn: Button
    private lateinit var database: DatabaseReference

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_profile, container, false)

        title = view.findViewById(R.id.title)
        welcomeTitle = view.findViewById(R.id.welcomeTitle)

        val uid = FirebaseAuth.getInstance().currentUser?.uid
        database = Firebase.database.reference

        database.child("users").orderByChild("uid").equalTo(uid).get()
            .addOnSuccessListener {
                    snapshot ->

                val userData = snapshot.children.map{
                        dataSnapshot ->  dataSnapshot.getValue(User::class.java)
                }

                var fname = userData.map { it?.firstName }.joinToString(", ")
                var lname = userData.map { it?.lastName }.joinToString(", ")

                var welcomeMessage = "Hello $fname $lname!"
                welcomeTitle.text = welcomeMessage
            }
            .addOnFailureListener{
                Log.e(TAG, "Error getting summary data", it)
            }

        updateProfileBtn = view.findViewById(R.id.updateProfileButton)

        updateProfileBtn.setOnClickListener {
            val intent = Intent(requireContext(), UpdateProfileActivity::class.java)
            startActivity(intent)
        }

        return view
    }
    companion object {
        fun newInstance(): ProfileFragment {
            return ProfileFragment()
        }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
    }
}