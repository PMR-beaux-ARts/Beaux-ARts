package com.example.beaux_arts

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.ImageView
import android.widget.ScrollView
import android.widget.TextView
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.beaux_arts.adapter.ProduitAdapter
import com.example.beaux_arts.donnees.Collection
import com.example.beaux_arts.donnees.Produit
import com.example.recyclerviewusinggridlayoutmanager.CollectionAdapter

class PieceActivity : AppCompatActivity() {

    private var recyclerProduitsArrayList: ArrayList<Produit>? = null

    fun onCreate(savedInstanceState: Bundle?, piece : Collection) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_piece)

        val pieceName = findViewById<TextView>(R.id.pieceName)
        val pieceImage = findViewById<ImageView>(R.id.pieceImageView)
        val pieceDescription = findViewById<TextView>(R.id.pieceDescription)
        val pieceProduits = findViewById<RecyclerView>(R.id.pieceRecyclerview)

        pieceName.text = piece.title
        pieceImage.setImageResource(piece.imageRes)
        pieceDescription.text = piece.description

        val produits: ArrayList<Produit>? = null
        for (produit in piece.produits!!) {
            produits?.add(produit)
        }
        val adapter = ProduitAdapter(produits!!)
        val layoutManager = GridLayoutManager(this, 4)
        pieceProduits?.setLayoutManager(layoutManager)
        pieceProduits?.setAdapter(adapter)
    }
}