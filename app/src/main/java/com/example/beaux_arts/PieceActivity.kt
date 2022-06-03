package com.example.beaux_arts

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.method.ScrollingMovementMethod
import android.util.Log
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.beaux_arts.adapter.ProduitAdapter
import com.example.beaux_arts.donnees.Produit

class PieceActivity() : AppCompatActivity() {

    private var recyclerProduitsArrayList: ArrayList<Produit>? = null
// title, "unknown", imageRes, "Un tableau", produits, nom: String, image: Int

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_piece)
        /*
        val prods: MutableList<Produit> = mutableListOf<Produit>( Produit("Poster", intent.getIntExtra("produits.imageRes", 0)))
        var title: String? = intent.getStringExtra("title")
        if(title==null) title = "Unknown"
        val piece = Collection (title,intent.getIntExtra("imageRes",0), prods)*/
        /*
        Log.i("test","avant la création de la liste de produits et de piece")
        recyclerProduitsArrayList = ArrayList()

        recyclerProduitsArrayList!!.add(Produit("Poster 1", R.drawable.img1))
        recyclerProduitsArrayList!!.add(Produit("Poster 2", R.drawable.img2))
        val piece = Collection ("Eglise d'Auvers-sur-Oise",R.drawable.img1)

        Log.i("test","avant les findviewbyid")
        val pieceName = findViewById<TextView>(R.id.pieceName)
        val pieceImage = findViewById<ImageView>(R.id.pieceImageView)
        val pieceDescription = findViewById<TextView>(R.id.pieceDescription)
        val pieceProduits = findViewById<RecyclerView>(R.id.pieceRecyclerview)
        Log.i("test","avant de set les texts et image")
        pieceName.text = piece.title
        pieceImage.setImageResource(piece.imageRes)
        pieceDescription.text = piece.description
        Log.i("test","avant l'affectation de la liste de produits")

        val adapter = ProduitAdapter(recyclerProduitsArrayList!!,{ produit : Produit -> produitClicked(produit)})
        val layoutManager = GridLayoutManager(this, 2)
        pieceProduits.setLayoutManager(layoutManager)
        pieceProduits.setAdapter(adapter)

         */

        val pieceName = findViewById<TextView>(R.id.pieceName)
        val pieceImage = findViewById<ImageView>(R.id.pieceImageView)
        val pieceDescription = findViewById<TextView>(R.id.pieceDescription)
        val pieceProduits = findViewById<RecyclerView>(R.id.pieceRecyclerview)

        pieceName.text = intent.getStringExtra("title")
        pieceImage.setImageResource(intent.getIntExtra("imageRes", 0))
        pieceDescription.text = intent.getStringExtra("description")
        pieceDescription.movementMethod = ScrollingMovementMethod()

        var i: Int = intent.getIntExtra("nbProduits",0)
        Log.i("test","on obtient $i produits")

        if (intent.getIntExtra("nbProduits",0) > 0) {

            recyclerProduitsArrayList = ArrayList()
            var nomProduit: String
            for(i in 1..(intent.getIntExtra("nbProduits",0))) {

                if(intent.getStringExtra("produit.nom$i") != null) {
                    nomProduit = intent.getStringExtra("produit.nom$i")!!
                }
                else {
                    nomProduit = "Produit"
                }

                var img = intent.getIntExtra("produit.imageRes$i", 0)

                recyclerProduitsArrayList!!.add(Produit(nomProduit, img))
            }
            val adapter = ProduitAdapter(recyclerProduitsArrayList!!,{ produit : Produit -> produitClicked(produit)})
            val layoutManager = GridLayoutManager(this, 2)
            pieceProduits.setLayoutManager(layoutManager)
            pieceProduits.setAdapter(adapter)
        }

    }

    private fun produitClicked(produit: Produit) {
        Toast.makeText(this, "Clicked: ${produit.nom}", Toast.LENGTH_SHORT).show()
        //val activiteVisee = Intent(this, PieceActivity::class.java)

        //startActivity(activiteVisee)

    }
}