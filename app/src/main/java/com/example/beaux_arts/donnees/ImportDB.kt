package com.example.beaux_arts.donnees

import android.content.Context
import android.database.sqlite.SQLiteDatabase
import android.util.Log
import com.example.beaux_arts.CollectionFragment
import com.example.beaux_arts.donnees.ImportDB
import com.example.beaux_arts.R
import java.io.File
import java.io.FileNotFoundException
import java.io.FileOutputStream
import java.io.IOException

class ImportDB(var context: CollectionFragment) {
    private val BUFFER_SIZE = 400000
    private var database: SQLiteDatabase? = null
    fun openDatabase() {
        database = this.openDatabase(DB_PATH + "/" + DB_NAME)
    }

    fun openDatabase(dbfile: String?): SQLiteDatabase? {
        try {
            if (!File(dbfile).exists()) {
                //没有创建文件夹
                val f = File(DB_PATH)
                if (!f.exists()) {
                    f.mkdir()
                }
                //判断数据库文件是否存在，若不存在则执行导入，否则直接打开数据库
                val `is` = context.resources.openRawResource(
                    R.raw.beauxarts
                ) //欲导入的数据库
                val fos =
                    FileOutputStream(File(dbfile))
                val buffer = ByteArray(BUFFER_SIZE)
                var count = 0
                while (`is`.read(buffer).also { count = it } > 0) {
                    fos.write(buffer, 0, count)
                }
                fos.close()
                `is`.close()
            }
            return SQLiteDatabase.openOrCreateDatabase(
                dbfile!!,
                null
            )
        } catch (e: FileNotFoundException) {
            Log.e("Database", "File not found")
            e.printStackTrace()
        } catch (e: IOException) {
            Log.e("Database", "IO exception")
            e.printStackTrace()
        }
        return null
    }

    //do something else h
    fun closeDatabase() {
        database!!.close()
    }

    companion object {
        const val DB_NAME = "beauxarts.db" //保存的数据库文件名
        const val PACKAGE_NAME = "com.example.beaux_arts" //此处改为自己应用的包名。
        const val DB_PATH = ("/data/data/"
                + PACKAGE_NAME + "/databases") //在手机里存放数据库的位置
    }
}