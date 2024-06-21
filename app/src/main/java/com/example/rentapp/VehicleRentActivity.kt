package com.example.rentapp

import android.content.Intent
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.View
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.rentapp.databinding.ActivityVehicleRentBinding
import com.example.rentapp.fragment.DashboardFragment
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*

class VehicleRentActivity : AppCompatActivity() {

    private lateinit var binding: ActivityVehicleRentBinding
    private lateinit var database: FirebaseDatabase
    private lateinit var jenisKendaraanRef: DatabaseReference
    private lateinit var merkKendaraanRef: DatabaseReference

    private lateinit var jenisKendaraanAdapter: ArrayAdapter<String>
    private lateinit var merkKendaraanAdapter: ArrayAdapter<String>

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityVehicleRentBinding.inflate(layoutInflater)
        setContentView(binding.root)
        supportActionBar?.hide()

        database = FirebaseDatabase.getInstance()
        jenisKendaraanRef = database.reference

        setupSpinners()
        setupButtonState()
        setupLamaSewaEditText()

        binding.toolbar.setOnClickListener {
            startActivity(Intent(this, DashboardFragment::class.java))
        }

        binding.btnSewa.setOnClickListener {
            saveDataToFirebase()
        }
    }

    private fun setupSpinners() {
        jenisKendaraanAdapter = ArrayAdapter(this, android.R.layout.simple_spinner_item, arrayOf("mobil", "motor"))
        jenisKendaraanAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        binding.spinnerJns.adapter = jenisKendaraanAdapter

        merkKendaraanAdapter = ArrayAdapter(this, android.R.layout.simple_spinner_item, mutableListOf())
        merkKendaraanAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        binding.spinnerMerk.adapter = merkKendaraanAdapter

        binding.spinnerJns.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
                val selectedJenis = jenisKendaraanAdapter.getItem(position)
                selectedJenis?.let {
                    merkKendaraanRef = database.reference.child(it)
                    loadMerkKendaraan()
                }
                setupButtonState()
            }

            override fun onNothingSelected(parent: AdapterView<*>?) {}
        }
    }

    private fun setupLamaSewaEditText() {
        binding.eTLamaSewa.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                if (s != null && s.isNotEmpty() && !s.endsWith(" hari")) {
                    val input = s.toString().replace(Regex("[^0-9]"), "")
                    if (input.isNotEmpty()) {
                        binding.eTLamaSewa.setText("$input hari")
                        binding.eTLamaSewa.setSelection(binding.eTLamaSewa.text.length - 5)
                    }
                }
                setupButtonState()
            }

            override fun afterTextChanged(s: Editable?) {}
        })
    }

    private fun setupButtonState() {
        binding.btnSewa.isEnabled = isFormValid()
    }

    private fun isFormValid(): Boolean {
        return binding.eTNama.text.isNotEmpty() &&
                binding.eTAlamat.text.isNotEmpty() &&
                binding.eTHP.text.isNotEmpty() &&
                binding.eTLamaSewa.text.isNotEmpty() &&
                binding.spinnerJns.selectedItemPosition != AdapterView.INVALID_POSITION &&
                binding.spinnerMerk.selectedItemPosition != AdapterView.INVALID_POSITION
    }

    private fun loadMerkKendaraan() {
        merkKendaraanRef.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                val merkKendaraanList = mutableListOf<String>()
                for (dataSnapshot in snapshot.children) {
                    val namaMobil = dataSnapshot.child("namaMobil").getValue(String::class.java)
                    val namaMotor = dataSnapshot.child("namaMotor").getValue(String::class.java)

                    // Check if value is Long and convert to String
                    val nama = when {
                        namaMobil != null -> namaMobil
                        namaMotor != null -> namaMotor
                        else -> null
                    }

                    nama?.let { merkKendaraanList.add(it) }
                }
                merkKendaraanAdapter.clear()
                merkKendaraanAdapter.addAll(merkKendaraanList)
                setupButtonState()
            }

            override fun onCancelled(error: DatabaseError) {
                Toast.makeText(this@VehicleRentActivity, "Gagal memuat data merk kendaraan", Toast.LENGTH_SHORT).show()
            }
        })
    }

    private fun saveDataToFirebase() {
        val database = FirebaseDatabase.getInstance()
        val reference = database.reference.child("sewa")

        val userId = FirebaseAuth.getInstance().currentUser?.uid // Dapatkan ID pengguna yang login

        val nama = binding.eTNama.text.toString()
        val alamat = binding.eTAlamat.text.toString()
        val noTelp = binding.eTHP.text.toString()
        val jenisKendaraan = binding.spinnerJns.selectedItem.toString()
        val merkKendaraan = binding.spinnerMerk.selectedItem.toString()
        val lamaSewa = binding.eTLamaSewa.text.toString().replace(" hari", "")

        val currentTime = System.currentTimeMillis()
        val lamaSewaDays = lamaSewa.toIntOrNull() ?: 0
        val endTime = currentTime + lamaSewaDays * 24 * 60 * 60 * 1000 // convert days to milliseconds

        val dataSewa = hashMapOf(
            "userId" to userId,
            "nama" to nama,
            "alamat" to alamat,
            "noTelp" to noTelp,
            "jenisKendaraan" to jenisKendaraan,
            "merkKendaraan" to merkKendaraan,
            "lamaSewa" to lamaSewa,
            "endTime" to endTime
        )

        reference.push().setValue(dataSewa)
            .addOnSuccessListener {
                Toast.makeText(this, "Data berhasil disimpan", Toast.LENGTH_SHORT).show()
                clearForm()
                navigateToRentActivity(userId, nama, alamat, noTelp, jenisKendaraan, merkKendaraan, lamaSewa)
            }
            .addOnFailureListener {
                Toast.makeText(this, "Data gagal disimpan", Toast.LENGTH_SHORT).show()
            }
    }

    private fun navigateToRentActivity(userId: String?, nama: String, alamat: String, noTelp: String, jenisKendaraan: String, merkKendaraan: String, lamaSewa: String) {
        val intent = Intent(this, RentActivity::class.java).apply {
            putExtra("userId", userId)
            putExtra("nama", nama)
            putExtra("alamat", alamat)
            putExtra("noTelp", noTelp)
            putExtra("jenisKendaraan", jenisKendaraan)
            putExtra("merkKendaraan", merkKendaraan)
            putExtra("lamaSewa", lamaSewa)
        }
        startActivity(intent)
    }

    private fun clearForm() {
        binding.eTNama.text.clear()
        binding.eTAlamat.text.clear()
        binding.eTHP.text.clear()
        binding.eTLamaSewa.text.clear()
        setupButtonState()
    }
}

