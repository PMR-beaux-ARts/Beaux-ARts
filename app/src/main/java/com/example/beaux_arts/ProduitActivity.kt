package com.example.beaux_arts

import android.database.sqlite.SQLiteDatabase
import android.graphics.BitmapFactory
import android.graphics.drawable.BitmapDrawable
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import com.example.beaux_arts.donnees.ImportDB

class ProduitActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_produit)
        supportActionBar?.hide()

        val produitName = findViewById<TextView>(R.id.produitName)
        val produitImage = findViewById<ImageView>(R.id.produitImageView)
        val produitDescription = findViewById<TextView>(R.id.produitDescription)
        val produitPrix = findViewById<TextView>(R.id.produitPrix)
        val pieceButton = findViewById<Button>(R.id.pieceButton)
        val buyButton = findViewById<Button>(R.id.button2)

        produitName.text = intent.getStringExtra("nom")
        produitDescription.text = intent.getStringExtra("nom")
        produitPrix.text = "Prix : ${intent.getIntExtra("prix",0)}"

        //chercher id dans le database et retourner l'image
        val produitId = intent.getIntExtra("id",0)
        val database = SQLiteDatabase.openOrCreateDatabase(ImportDB.DB_PATH+ "/" + ImportDB.DB_NAME, null)
        val cursor = database.rawQuery("SELECT * FROM Produit WHERE id = ${produitId} ",null)
        if(cursor != null &&cursor.moveToFirst()) {
            val img_b = cursor.getBlob(cursor.getColumnIndex("image"))
            val img_bitmap = BitmapFactory.decodeByteArray(img_b, 0, img_b.size, null)
            val img_bitmapDrawable = BitmapDrawable(resources, img_bitmap)
            produitImage.setImageDrawable(img_bitmapDrawable)
            Log.i("text","afficher l'image")
        }
        cursor.close()
        database.close()
    }
}

