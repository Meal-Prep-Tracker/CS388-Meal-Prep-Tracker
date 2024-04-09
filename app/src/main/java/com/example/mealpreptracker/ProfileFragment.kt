package com.example.mealpreptracker

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class ProfileFragment : Fragment() {
    private lateinit var title: TextView
    private lateinit var updateProfileBtn: Button

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_profile, container, false)

        title = view.findViewById(R.id.title)

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

    private fun fetchUserData() {
        lifecycleScope.launch(Dispatchers.IO) {
            var user = "test"
            var titleText = "Hello $user!"

            withContext(Dispatchers.Main) {
                title.text = titleText
            }
        }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        fetchUserData()
    }
}