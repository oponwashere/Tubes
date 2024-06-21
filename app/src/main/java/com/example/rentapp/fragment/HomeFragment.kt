package com.example.rentapp.fragment

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.example.rentapp.InfoCarActivity
import com.example.rentapp.InfoMotorcycleActivity
import com.example.rentapp.databinding.FragmentHomeBinding
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener

class HomeFragment : Fragment() {

    private var _binding: FragmentHomeBinding? = null

    private val binding get() = _binding!!

    private lateinit var auth: FirebaseAuth
    private lateinit var database: FirebaseDatabase

    private var username: String = ""

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        _binding = FragmentHomeBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        auth = FirebaseAuth.getInstance()
        database = FirebaseDatabase.getInstance()

        //dapatkan instance pengguna yang saat ini login
        val currentUser: FirebaseUser? = auth.currentUser

        //cek apakah ada pengguna login
        currentUser?.let { user ->
            val currentUserUid = user.uid

            val userRef = database.getReference("users").child(currentUserUid)

            userRef.addValueEventListener(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    if (snapshot.exists()) {
                        //ambil data username
                        username = snapshot.child("username").getValue(String::class.java) ?: ""

                        // tampilkan nama
                        binding.tvNameWelcome.text = "$username"
                    }
                }

                override fun onCancelled(error: DatabaseError) {
                    Log.e("FirebaseError", "Error: ${error.message}")
                }
            })
        }

        binding.btnInfoMobil.setOnClickListener {
            startActivity(Intent(requireContext(), InfoCarActivity::class.java))
        }

        binding.btnInfoMotor.setOnClickListener {
            startActivity(Intent(requireContext(), InfoMotorcycleActivity::class.java))
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}