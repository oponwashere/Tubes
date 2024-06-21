package com.example.rentapp.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.rentapp.R
import com.example.rentapp.data.Motor

class MotorAdapter(private val motorList: List<Motor>) : RecyclerView.Adapter<MotorAdapter.ViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.list_motorcycle, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val motor = motorList[position]
        holder.bind(motor)
    }

    override fun getItemCount(): Int {
        return motorList.size
    }

    inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val tvNamaMotor: TextView = itemView.findViewById(R.id.tvNamaMotor)
        private val ivGambarMotor: ImageView = itemView.findViewById(R.id.imageMotor)
        private val tvHargaSewa: TextView = itemView.findViewById(R.id.tvHargaSewaMotor)

        fun bind(motor: Motor) {
            tvNamaMotor.text = motor.namaMotor
            // Set gambar mobil
            Glide.with(itemView.context).load(motor.gambarMotor).into(ivGambarMotor)
            tvHargaSewa.text = motor.hargaSewaMotor
        }
    }
}