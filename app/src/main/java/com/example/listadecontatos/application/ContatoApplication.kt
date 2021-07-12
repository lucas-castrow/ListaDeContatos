package com.example.listadecontatos.application

import android.app.Application
import com.example.listadecontatos.helpers.BancoHelper

class ContatoApplication : Application(){

    var bancoHelper: BancoHelper? = null
        private set
    companion object{
        lateinit var instance: ContatoApplication

    }
    override fun onCreate() {
        super.onCreate()
        instance = this
        bancoHelper = BancoHelper(this)
    }
}