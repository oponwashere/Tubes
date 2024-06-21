package com.example.rentapp.data

import java.io.Serializable

class Motor : Serializable{
    var gambarMotor: String? = null
    var hargaSewaMotor: String? = null
    var namaMotor: String? = null

    constructor(gambarMotor: String?, hargaSewaMotor: String?, namaMotor: String? ) {

        this.gambarMotor = gambarMotor
        this.hargaSewaMotor = hargaSewaMotor
        this.namaMotor = namaMotor
    }
    constructor() {

    }
}