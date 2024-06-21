package com.example.rentapp.data

import java.io.Serializable

class Mobil : Serializable {
    var gambarMobil: String? = null
    var hargaSewaMobil: String? = null
    var namaMobil: String? = null

    constructor(gambarMobil: String?, hargaSewaMobil: String?, namaMobil: String? ) {

        this.gambarMobil = gambarMobil
        this.hargaSewaMobil = hargaSewaMobil
        this.namaMobil = namaMobil
    }
    constructor() {

    }
}




