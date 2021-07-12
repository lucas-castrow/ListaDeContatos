package com.example.listadecontatos.helpers

import android.content.ContentValues
import android.content.Context
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper
import com.example.listadecontatos.feature.listacontatos.model.ContatosVO

class BancoHelper(
    context: Context,

) : SQLiteOpenHelper(context, NOME_BANCO, null, VERSAO_BANCO) {

    companion object{
        private val VERSAO_BANCO = 1
        private val NOME_BANCO = "contato.db"

    }

    val TABLE_CONTATO = "contato"
    val DROP_TABLE_CONTATO = "DROP TABLE IF EXISTS $TABLE_CONTATO"
    val CL_CONTATO_ID = "_id"
    val CL_CONTATO_NOME = "nome"
    val CL_CONTATO_TELEFONE = "telefone"

    val CREATE_TABLE = "CREATE TABLE $TABLE_CONTATO (" +
            "$CL_CONTATO_ID INTEGER NOT NULL,"+
            "$CL_CONTATO_NOME TEXT NOT NULL,"+
            "$CL_CONTATO_TELEFONE TEXT NOT NULL,"+
            "PRIMARY KEY($CL_CONTATO_ID AUTOINCREMENT)"+
            ")"

    val SELECT_CONTATO = "SELECT * FROM $TABLE_CONTATO"
    override fun onCreate(db: SQLiteDatabase?) {
        db?.execSQL(CREATE_TABLE)
    }

    override fun onUpgrade(db: SQLiteDatabase?, oldVersion: Int, newVersion: Int) {
        if(oldVersion != newVersion){
            db?.execSQL(DROP_TABLE_CONTATO)
        }
        onCreate(db)

    }

    fun buscarContatos(busca: String, isBuscaPorID: Boolean = false): List<ContatosVO> {
        val db = this.readableDatabase ?: return mutableListOf()
        var lista = mutableListOf<ContatosVO>()
        var where: String? = null
        var args: Array<String> = arrayOf()
        //val result = db.rawQuery(SELECT_CONTATO, null)

        if(isBuscaPorID){
            where = "$CL_CONTATO_ID = ?"
            args = arrayOf("$busca")
        }else{
            where = "$CL_CONTATO_NOME LIKE ?"
            args = arrayOf("%$busca%")
        }
        var result = db.query(TABLE_CONTATO,null,where,args,null,null,null)
        if (result == null){
            db.close()
            return mutableListOf()
        }
        if (result.moveToFirst()) {
            do {
                val id = result.getInt(result.getColumnIndex(CL_CONTATO_ID))
                val nome = result.getString(result.getColumnIndex(CL_CONTATO_NOME))
                val telefone = result.getString(result.getColumnIndex(CL_CONTATO_TELEFONE))

                val contato = ContatosVO(id, nome, telefone)
                lista.add(contato)
            } while (result.moveToNext())
        }

        result.close()
        db.close()

        return lista
    }

    fun salvarContato(contato: ContatosVO) {
        val db = writableDatabase ?: return
        var content = ContentValues()
        content.put(CL_CONTATO_NOME,contato.nome)
        content.put(CL_CONTATO_TELEFONE,contato.telefone)
        db.insert(TABLE_CONTATO,null,content)
        db.close()
    }
    fun deletarContato(id: Int) {
        val db = writableDatabase ?: return
        val sql = "DELETE FROM $TABLE_CONTATO WHERE $CL_CONTATO_ID = ?"
        val arg = arrayOf("$id")
        db.execSQL(sql,arg)
        db.close()
    }

    fun updateContato(contato: ContatosVO) {
        val db = writableDatabase ?: return
        val sql = "UPDATE $TABLE_CONTATO SET $CL_CONTATO_NOME = ?, $CL_CONTATO_TELEFONE = ? WHERE $CL_CONTATO_ID = ?"
        val arg = arrayOf(contato.nome,contato.telefone,contato.id)
        db.execSQL(sql,arg)
        db.close()
    }

}