package com.example.rentapp

import android.content.Intent
import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import com.example.rentapp.databinding.ActivityRentBinding
import com.example.rentapp.fragment.DashboardFragment
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*

class RentActivity : AppCompatActivity() {

    private lateinit var binding: ActivityRentBinding
    private lateinit var database: DatabaseReference
    private lateinit var userId: String

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityRentBinding.inflate(layoutInflater)
        setContentView(binding.root)
        supportActionBar?.hide()

        userId = FirebaseAuth.getInstance().currentUser?.uid ?: ""

        binding.toolbar.setNavigationOnClickListener {
            val intent = Intent(this, DashboardFragment::class.java)
            intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_SINGLE_TOP
            startActivity(intent)
        }

        // Setup database reference
        database = FirebaseDatabase.getInstance().reference.child("sewa")

        // Check if user has rental data
        checkUserRentalData()
    }

    private fun checkUserRentalData() {
        if (userId.isNotEmpty()) {
            database.orderByChild("userId").equalTo(userId).addListenerForSingleValueEvent(object : ValueEventListener {
                override fun onDataChange(dataSnapshot: DataSnapshot) {
                    if (dataSnapshot.exists()) {
                        // User has rental data
                        for (data in dataSnapshot.children) {
                            val rentalData = data.getValue(RentalData::class.java)
                            rentalData?.let {
                                displayRentalData(it)
                            }
                        }
                        binding.cardView.visibility = View.VISIBLE
                        binding.btnBatal.visibility = View.VISIBLE
                        binding.notFound.visibility = View.GONE
                    } else {
                        // User does not have rental data
                        binding.cardView.visibility = View.GONE
                        binding.btnBatal.visibility = View.GONE
                        binding.notFound.visibility = View.VISIBLE
                    }
                }

                override fun onCancelled(databaseError: DatabaseError) {
                    // Handle possible errors.
                }
            })
        }
    }

    private fun displayRentalData(rentalData: RentalData) {
        binding.tvNama.text = rentalData.nama
        binding.tvAlamat.text = rentalData.alamat
        binding.tvNomor.text = rentalData.noTelp
        binding.tvKendaraanJns.text = rentalData.jenisKendaraan
        binding.tvKendaraanMerk.text = rentalData.merkKendaraan
        binding.tvTanggal.text = "${rentalData.lamaSewa} hari"

        binding.btnBatal.setOnClickListener {
            database.orderByChild("userId").equalTo(userId).addListenerForSingleValueEvent(object : ValueEventListener {
                override fun onDataChange(dataSnapshot: DataSnapshot) {
                    for (data in dataSnapshot.children) {
                        data.ref.removeValue()
                    }
                    binding.notFound.visibility = View.VISIBLE
                    binding.cardView.visibility = View.GONE
                    binding.btnBatal.visibility = View.GONE
                }

                override fun onCancelled(databaseError: DatabaseError) {
                    // Handle possible errors.
                }
            })
        }
    }

    data class RentalData(
        val userId: String = "",
        val nama: String = "",
        val alamat: String = "",
        val noTelp: String = "",
        val jenisKendaraan: String = "",
        val merkKendaraan: String = "",
        val lamaSewa: String = ""
    )
}
