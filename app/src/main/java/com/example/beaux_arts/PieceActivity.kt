package com.example.beaux_arts

import android.content.Intent
import android.database.sqlite.SQLiteDatabase
import android.graphics.BitmapFactory
import android.graphics.drawable.BitmapDrawable
import android.media.MediaPlayer
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.method.ScrollingMovementMethod
import android.util.Log
import android.view.View
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.beaux_arts.adapter.ProduitAdapter
import com.example.beaux_arts.donnees.ImportDB
import com.example.beaux_arts.donnees.Produit
import kotlinx.android.synthetic.main.activity_piece.*
import kotlinx.android.synthetic.main.activity_produit.*
import org.json.JSONObject

class PieceActivity() : AppCompatActivity() {

    private var recyclerProduitsArrayList: ArrayList<Produit>? = null
    private var mediaPlayer: MediaPlayer? = null
    private var play: Boolean = false

    override fun onCreate(savedInstanceState: Bundle?) {
        val database = SQLiteDatabase.openOrCreateDatabase(ImportDB.DB_PATH+ "/" + ImportDB.DB_NAME, null)
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_piece)
        supportActionBar?.hide()

        val pieceName = findViewById<TextView>(R.id.pieceName)
        val pieceImage = findViewById<ImageView>(R.id.pieceImageView)
        val pieceDescription = findViewById<TextView>(R.id.pieceDescription)
        val pieceProduits = findViewById<RecyclerView>(R.id.pieceRecyclerview)

        pieceName.text = intent.getStringExtra("title")
        pieceDescription.text = intent.getStringExtra("description")
        pieceDescription.movementMethod = ScrollingMovementMethod()
        val pieceId = intent.getIntExtra("id",1)
        val cursor1 = database.rawQuery("SELECT * FROM Collection WHERE id = ${pieceId} ",null)
        Log.i("test","Searched succeed ${cursor1}")
        if(cursor1 != null &&cursor1.moveToFirst()) {
            Log.i("test","in cursors1")
            val img_b_collection = cursor1.getBlob(cursor1.getColumnIndex("image"))
            val img_bitmap_collection =
                BitmapFactory.decodeByteArray(img_b_collection, 0, img_b_collection.size, null)
            val Co_image = BitmapDrawable(resources, img_bitmap_collection)
            pieceImage.setImageDrawable(Co_image)

            val nomproduit = cursor1.getString(cursor1.getColumnIndex("nom"))
            Log.i("test","Item ${nomproduit}")

            recyclerProduitsArrayList = ArrayList()
            val produits_json = JSONObject(cursor1.getString(cursor1.getColumnIndex("mesproduits")))
            Log.i("test","on obtient produits")
            val keys = produits_json.keys()
            Log.i("test","keys of json${keys}")
            while (keys.hasNext()) {
                var key = keys.next().toString()
                //chercher id dans le database
                val produitId = produits_json.getInt(key)
                Log.i("test","produitId ${produitId}")
                val cursor2 = database.rawQuery("SELECT * FROM Produit WHERE id = ${produitId} ",null)
                if(cursor2.moveToFirst()){
                    do {
                        val Pr_id = cursor2.getInt(cursor2.getColumnIndex("id"))
                        val Pr_nom = cursor2.getString(cursor2.getColumnIndex("nom"))
                        //TODO : Penser a un moyen de lieer aux collections
                        val Pr_mescollection = null
                        val Pr_type = cursor2.getString(cursor2.getColumnIndex("type"))
                        val Pr_prix = cursor2.getDouble(cursor2.getColumnIndex("prix"))
                        val img_b = cursor2.getBlob(cursor2.getColumnIndex("image"))
                        val img_bitmap = BitmapFactory.decodeByteArray(img_b,0,img_b.size,null)
                        val Pr_image = BitmapDrawable(resources,img_bitmap)
                        var newProduit= Produit(
                            Pr_id,
                            Pr_nom,
                            Pr_type,
                            Pr_prix,
                            Pr_image,
                            Pr_mescollection
                        )
                        //var img = intent.getIntExtra("produit.imageRes$i", 0)
                        recyclerProduitsArrayList!!.add(newProduit)
                    } while (cursor2.moveToNext())
                }
                cursor2.close()
            }
        }
        cursor1.close()
        database.close()

            val adapter = ProduitAdapter(recyclerProduitsArrayList!!,{ produit : Produit -> produitClicked(produit)})
            val layoutManager = GridLayoutManager(this, 2)
            pieceProduits.setLayoutManager(layoutManager)
            pieceProduits.setAdapter(adapter)

        mediaPlayer = MediaPlayer.create(this, R.raw.amphore_a_spirales)
        mediaPlayer?.setOnPreparedListener { println("Ready to go")}
        pieceAudio.setOnClickListener{event -> handleClick(event)}

        }

    private fun handleClick(event: View?) {
        if (play) {
            mediaPlayer?.pause()
            play = false
        } else {
            mediaPlayer?.start()
            play = true
        }
    }


    private fun produitClicked(produit: Produit) {
        Toast.makeText(this, "Clicked: ${produit.nom}", Toast.LENGTH_SHORT).show()
        val activiteVisee = Intent(this, ProduitActivity::class.java)
        activiteVisee.putExtra("nom", produit.nom)
        activiteVisee.putExtra("prix", produit.prix)
        activiteVisee.putExtra("id", produit.id)

        startActivity(activiteVisee)

    }

    override fun onPause() {
        super.onPause()
        if(play) {
            mediaPlayer?.pause()
            play = false
        }
    }
}


