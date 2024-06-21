package com.example.rentapp.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.rentapp.R
import com.bumptech.glide.Glide
import com.example.rentapp.data.Mobil

class MobilAdapter(private val mobilList: List<Mobil>) : RecyclerView.Adapter<MobilAdapter.ViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.list_car, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val mobil = mobilList[position]
        holder.bind(mobil)
    }

    override fun getItemCount(): Int {
        return mobilList.size
    }

    inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val tvNamaMobil: TextView = itemView.findViewById(R.id.tvNamaMobil)
        private val ivGambarMobil: ImageView = itemView.findViewById(R.id.imageMobil)
        private val tvHargaSewa: TextView = itemView.findViewById(R.id.tvHargaSewaMobil)

        fun bind(mobil: Mobil) {
            tvNamaMobil.text = mobil.namaMobil
            // Set gambar mobil
            Glide.with(itemView.context).load(mobil.gambarMobil).into(ivGambarMobil)
            tvHargaSewa.text = mobil.hargaSewaMobil.toString() // Ubah harga sewa mobil menjadi String
        }
    }
}

