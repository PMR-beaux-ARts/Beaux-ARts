package com.example.beaux_arts

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.TextView
import android.widget.Toast
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.beaux_arts.donnees.Collection
import com.example.recyclerviewusinggridlayoutmanager.CollectionAdapter

class ItineraryActivity : AppCompatActivity() {

    private var recyclerDataArrayList: ArrayList<Collection>? = ArrayList()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_itinerary)
        supportActionBar?.hide()

        var description: TextView = findViewById(R.id.itineraireDescription)
        //TODO : ajouter la description d'un itinéraire (depuis la DB ?)

        var recyclerView: RecyclerView = findViewById(R.id.oeuvresCollectionVIew)
        //TODO : ajouter les oeuvres de l'itinéraire dans recyclerDataArrayList (depuis la DB ?)
        val adapter = CollectionAdapter(recyclerDataArrayList!!,{collection : Collection -> collectionClicked(collection)})
        val layoutManager = GridLayoutManager(this, 4)
        recyclerView?.setLayoutManager(layoutManager)
        recyclerView?.setAdapter(adapter)

    }

    private fun collectionClicked(collection: Collection) {
        Toast.makeText(this, "Clicked: ${collection.nom}", Toast.LENGTH_SHORT).show()
        val activiteVisee = Intent(this, PieceActivity::class.java)
        activiteVisee.putExtra("title", collection.nom)
        activiteVisee.putExtra("author", collection.auteur)
        activiteVisee.putExtra("id", collection.id)
        activiteVisee.putExtra("description", collection.description)

        startActivity(activiteVisee)

    }
}