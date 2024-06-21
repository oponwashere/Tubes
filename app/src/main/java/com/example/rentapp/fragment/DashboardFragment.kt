package com.example.rentapp.fragment

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.example.rentapp.LoginActivity
import com.example.rentapp.RentActivity
import com.example.rentapp.VehicleRentActivity
import com.example.rentapp.databinding.FragmentDashboardBinding
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener

class DashboardFragment : Fragment() {

    private var _binding: FragmentDashboardBinding? = null
    private lateinit var auth: FirebaseAuth
    private lateinit var database: FirebaseDatabase

    private val binding get() = _binding!!

    override fun onCreateView(inflater: LayoutInflater,container: ViewGroup?,savedInstanceState: Bundle?): View {
        _binding = FragmentDashboardBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // inisialisasi database
        auth = FirebaseAuth.getInstance()
        database = FirebaseDatabase.getInstance()

        //mendapatkan UID pengguna yang sedang masuk
        val currentUserUid = auth.currentUser?.uid

        //menampilkan nama dan email dari database
        getUserData(currentUserUid)

        binding.btnSewaan.setOnClickListener {
            startActivity(Intent(context, RentActivity::class.java))
        }

        binding.btnLogout.setOnClickListener {
            signOut()
        }

    }

    // Fungsi untuk mendapatkan data pengguna dari Firebase Database
    private fun getUserData(uid: String?) {
        val userRef = database.getReference("users").child(uid.orEmpty())

        userRef.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                if (snapshot.exists()) {
                    val username = snapshot.child("username").getValue(String::class.java)
                    val email = snapshot.child("email").getValue(String::class.java)

                    // Menampilkan nama pengguna dan email
                    binding.txtUsername.text = "$username"
                    binding.txtEmail.text = "$email"
                }
            }

            override fun onCancelled(error: DatabaseError) {
                // Handle error
            }
        })
    }

    private fun signOut() {
        auth.signOut()
        val i = Intent(context, LoginActivity::class.java)
        startActivity(i)
        activity?.finish()
    }
}