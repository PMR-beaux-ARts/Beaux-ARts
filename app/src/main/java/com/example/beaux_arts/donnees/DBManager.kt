package com.example.beaux_arts.donnees

import android.content.Context
import android.database.sqlite.SQLiteDatabase
import com.example.beaux_arts.donnees.DBManager
import com.example.beaux_arts.R
import android.os.Environment
import android.util.Log
import com.example.beaux_arts.CollectionFragment
import java.io.File
import java.io.FileNotFoundException
import java.io.FileOutputStream
import java.io.IOException

class DBManager(private val context: CollectionFragment) {
    private val BUFFER_SIZE = 400000
    var database: SQLiteDatabase? = null
    fun openDatabase() {
        println(DB_PATH + "/" + DB_NAME)
        database = this.openDatabase(DB_PATH + "/" + DB_NAME)
    }

    private fun openDatabase(dbfile: String): SQLiteDatabase? {
        try {
            if (!File(dbfile).exists()) {
                //Vérifier si database existe. S'il n'existe pas on l'importe, sinon on l'ouvre
                val `is` = context.resources.openRawResource(
                    R.raw.beauxarts
                )
                val fos = FileOutputStream(dbfile)
                val buffer = ByteArray(BUFFER_SIZE)
                var count = 0
                while (`is`.read(buffer).also { count = it } > 0) {
                    fos.write(buffer, 0, count)
                }
                fos.close()
                `is`.close()
            }
            return SQLiteDatabase.openOrCreateDatabase(dbfile, null)
        } catch (e: FileNotFoundException) {
            Log.e("Database", "File not found")
            e.printStackTrace()
        } catch (e: IOException) {
            Log.e("Database", "IO exception")
            e.printStackTrace()
        }
        return null
    }

    fun closeDatabase() {
        database!!.close()
    }

    companion object {
        const val DB_NAME = "beauxarts.db" //data base enregistré
        const val PACKAGE_NAME = "com.example.beaux_arts"
        val DB_PATH = ("/data/data"
                + Environment.getDataDirectory().absolutePath + "/"
                + PACKAGE_NAME+"/databases") //l'endroit où la database est sauvegardée dans le téléphone(/data/data/com.example.beaux_arts/beauxarts.db)
    }
}