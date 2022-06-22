package com.example.beaux_arts

import android.annotation.SuppressLint
import android.content.Intent
import android.database.sqlite.SQLiteDatabase
import android.graphics.BitmapFactory
import android.graphics.drawable.BitmapDrawable
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import com.example.beaux_arts.donnees.Collection
import com.example.beaux_arts.donnees.ImportDB

class ProduitActivity : AppCompatActivity() {
    @SuppressLint("Range")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_produit)
        supportActionBar?.hide()

        val produitName = findViewById<TextView>(R.id.produitName)
        val produitImage = findViewById<ImageView>(R.id.produitImageView)
        val produitDescription = findViewById<TextView>(R.id.produitDescription)
        val produitPrix = findViewById<TextView>(R.id.produitPrix)
        val pieceButton = findViewById<Button>(R.id.pieceButton)


        produitName.text = intent.getStringExtra("nom")
        produitDescription.text = intent.getStringExtra("nom")



        //chercher id dans le database et retourner l'image
        val produitId = intent.getIntExtra("id",0)
        val database = SQLiteDatabase.openOrCreateDatabase(ImportDB.DB_PATH+ "/" + ImportDB.DB_NAME, null)
        val cursor = database.rawQuery("SELECT * FROM Produit WHERE id = ${produitId} ",null)
        var collectionId : Int = 1
        if(cursor != null &&cursor.moveToFirst()) {
            produitPrix.text = "Prix : ${cursor.getDouble(cursor.getColumnIndex("prix"))} €"
            val img_b = cursor.getBlob(cursor.getColumnIndex("image"))
            val img_bitmap = BitmapFactory.decodeByteArray(img_b, 0, img_b.size, null)
            val img_bitmapDrawable = BitmapDrawable(resources, img_bitmap)
            produitImage.setImageDrawable(img_bitmapDrawable)
            collectionId = cursor.getInt(cursor.getColumnIndex("mescollections"))
            Log.i("text","afficher l'image")

        }
        cursor.close()
        database.close()

        pieceButton.setOnClickListener {
        val activiteVisee = Intent(this, PieceActivity::class.java)
        activiteVisee.putExtra("id", collectionId)
        startActivity(activiteVisee)
        }

//        fun collectionClicked(collectionId:Int) {
//            Toast.makeText(this, "Clicked: ${collectionId.nom}", Toast.LENGTH_SHORT).show()
//            val activiteVisee = Intent(this, PieceActivity::class.java)
//            activiteVisee.putExtra("id", collectionId)
//            startActivity(activiteVisee)
//
//        }

    }


//    override fun onCreateView(
//        inflater: LayoutInflater, container: ViewGroup?,
//        savedInstanceState: Bundle?
//    ): View? {
//        return inflater.inflate(R.layout.fragment_colle, container, false)
//    }

}

