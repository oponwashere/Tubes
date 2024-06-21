package com.example.rentapp

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.rentapp.adapter.MotorAdapter
import com.example.rentapp.data.Motor
import com.example.rentapp.databinding.ActivityInfoMotorcycleBinding
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener

class InfoMotorcycleActivity : AppCompatActivity() {

    private lateinit var binding : ActivityInfoMotorcycleBinding
    private lateinit var database: DatabaseReference
    private lateinit var motorAdapter: MotorAdapter
    private lateinit var recyclerView: RecyclerView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityInfoMotorcycleBinding.inflate(layoutInflater)
        setContentView(binding.root)
        supportActionBar?.hide()

        binding.toolbar.setNavigationOnClickListener { onBackPressed() }

        recyclerView = binding.recyclerView
        recyclerView.layoutManager = LinearLayoutManager(this)
        motorAdapter = MotorAdapter(emptyList()) // Atur adapter dengan list kosong awal
        recyclerView.adapter = motorAdapter

        // Inisialisasi database
        database = FirebaseDatabase.getInstance().reference

        binding.btnSewaMotor.setOnClickListener {
            startActivity(Intent(this, VehicleRentActivity::class.java))
        }

        // Ambil data mobil dari Firebase Realtime Database
        ambilDataMotor()
    }

    private fun ambilDataMotor() {
        database.child("motor").addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                val motorList = mutableListOf<Motor>()
                for (motorSnapshot in snapshot.children) {
                    val gambarMotor = motorSnapshot.child("gambarMotor").getValue(String::class.java) ?: ""
                    val hargaSewaMotor = motorSnapshot.child("hargaSewaMotor").getValue(String::class.java) ?: "" // Ambil sebagai Long
                    val namaMobil = motorSnapshot.child("namaMotor").getValue(String::class.java) ?: ""
                    val motor = Motor(gambarMotor, hargaSewaMotor, namaMobil)
                    motorList.add(motor)
                }
                motorAdapter = MotorAdapter(motorList)
                recyclerView.adapter = motorAdapter
            }

            override fun onCancelled(error: DatabaseError) {
                // Handle error
            }
        })
    }
}