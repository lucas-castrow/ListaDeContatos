package com.example.listadecontatos.feature.listacontatos

import android.content.ContentValues.TAG
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.Menu
import android.view.View
import android.widget.SearchView
import android.widget.Toast
import androidx.core.widget.addTextChangedListener
import androidx.core.widget.doOnTextChanged
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.listadecontatos.R
import com.example.listadecontatos.application.ContatoApplication
import com.example.listadecontatos.bases.BaseActivity
import com.example.listadecontatos.feature.contato.ContatoActivity
import com.example.listadecontatos.feature.listacontatos.adapter.ContatoAdapter
import com.example.listadecontatos.feature.listacontatos.model.ContatosVO
import kotlinx.android.synthetic.main.activity_main.*
import java.net.URLEncoder


class MainActivity : BaseActivity() {

    private var adapter:ContatoAdapter? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        setupToolBar(toolBar, "Lista de contatos",false)
        setupListView()
        setupOnClicks()
    }

    private fun setupOnClicks(){
        fab.setOnClickListener { onClickAdd() }
        ivBuscar.setOnClickListener { onClickBuscar() }
    }

    private fun setupListView(){
        recyclerView.layoutManager = LinearLayoutManager(this)
    }

    override fun onResume() {
        super.onResume()
        onClickBuscar()
    }

    private fun onClickAdd(){
        val intent = Intent(this, ContatoActivity::class.java)
        startActivity(intent)
    }

    private fun onClickItemRecyclerView(index: Int){
        val intent = Intent(this,ContatoActivity::class.java)
        intent.putExtra("index", index)
        startActivity(intent)
    }

    private fun onClickItemWhats(number: String){
        sendToWhatsapp(number)
    }

    private fun onClickBuscar(){
        val busca = etBuscar.text.toString()
        progress.visibility = View.VISIBLE
        Thread(Runnable {
            Thread.sleep(1500)
            var listaFiltrada: List<ContatosVO> = mutableListOf()
            try {
                listaFiltrada = ContatoApplication.instance.bancoHelper?.buscarContatos(busca) ?: mutableListOf()
            }catch (ex: Exception){
                ex.printStackTrace()
            }
            runOnUiThread {
                adapter = ContatoAdapter(this,listaFiltrada,  { (onClickItemWhats(it)) }) {onClickItemRecyclerView(it)}
                recyclerView.adapter = adapter
                progress.visibility = View.GONE
                Toast.makeText(this,"Buscando por $busca",Toast.LENGTH_SHORT).show()
            }

        }).start()
    }

    private fun sendToWhatsapp(numero: String){
        try {

            val packageManager = this.packageManager
            val i = Intent(Intent.ACTION_VIEW)
            val url = "https://api.whatsapp.com/send?phone=" +"$+55 $numero" + "&text=" +
                    URLEncoder.encode("Olá, fui encaminhado para o whatsapp")
            i.setPackage("com.gbwhatsapp")
            i.data = Uri.parse(url)
            if (i.resolveActivity(packageManager) != null){
                startActivity(i)
            }else{
                Toast.makeText(this, "Você precisa instalar o WhatsApp", Toast.LENGTH_SHORT).show()
            }

        }catch (e: Exception){
            Toast.makeText(this, ""+e.stackTrace, Toast.LENGTH_SHORT).show()
        }
    }






}