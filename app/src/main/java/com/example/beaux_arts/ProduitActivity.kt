package com.example.beaux_arts

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView

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
        produitImage.setImageResource(intent.getIntExtra("imageRes", 0))
        produitDescription.text = intent.getStringExtra("nom")
        produitPrix.text = "Prix : ${intent.getIntExtra("prix",0)}"
    }
}