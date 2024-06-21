package com.example.rentapp

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.database.*
import com.example.rentapp.databinding.ActivityInfoCarBinding
import com.example.rentapp.adapter.MobilAdapter
import com.example.rentapp.data.Mobil

class InfoCarActivity : AppCompatActivity() {

    private lateinit var binding : ActivityInfoCarBinding
    private lateinit var database: DatabaseReference
    private lateinit var mobilAdapter: MobilAdapter
    private lateinit var recyclerView: RecyclerView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityInfoCarBinding.inflate(layoutInflater)
        setContentView(binding.root)
        supportActionBar?.hide()

        binding.toolbar.setNavigationOnClickListener { onBackPressed() }

        recyclerView = binding.recyclerView
        recyclerView.layoutManager = LinearLayoutManager(this)
        mobilAdapter = MobilAdapter(emptyList()) // Atur adapter dengan list kosong awal
        recyclerView.adapter = mobilAdapter

        // Inisialisasi database
        database = FirebaseDatabase.getInstance().reference

        binding.btnSewaMobil.setOnClickListener {
            startActivity(Intent(this, VehicleRentActivity::class.java))
        }

        // Ambil data mobil dari Firebase Realtime Database
        ambilDataMobil()
    }

    private fun ambilDataMobil() {
        database.child("mobil").addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                val mobilList = mutableListOf<Mobil>()
                for (mobilSnapshot in snapshot.children) {
                    val gambarMobil = mobilSnapshot.child("gambarMobil").getValue(String::class.java) ?: ""
                    val hargaSewaMobil = mobilSnapshot.child("hargaSewaMobil").getValue(String::class.java) ?: "" // Ambil sebagai Long
                    val namaMobil = mobilSnapshot.child("namaMobil").getValue(String::class.java) ?: ""
                    val mobil = Mobil(gambarMobil, hargaSewaMobil, namaMobil)
                    mobilList.add(mobil)
                }
                mobilAdapter = MobilAdapter(mobilList)
                recyclerView.adapter = mobilAdapter
            }

            override fun onCancelled(error: DatabaseError) {
                // Handle error
            }
        })
    }
}
