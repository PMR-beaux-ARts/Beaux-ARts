package com.example.beaux_arts

import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.beaux_arts.adapter.ProduitAdapter
import com.example.beaux_arts.donnees.Produit

class SettingsFragment : Fragment() {

    private val CAT: String = "Shop page"

    private var recyclerDataArrayList: ArrayList<Produit>? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        Log.i(CAT,"onCreate")
    }
    override fun onStart() {
        super.onStart()
        Log.i(CAT,"onStart")
        var recyclerView: RecyclerView? = view?.findViewById(R.id.souvenirList)

        recyclerDataArrayList = ArrayList()

        recyclerDataArrayList!!.add(Produit("Poster, Saint Rémy",R.drawable.img1))
        recyclerDataArrayList!!.add(Produit("Carnet, Saint Rémy",R.drawable.img1_1))
        recyclerDataArrayList!!.add(Produit("Poster, Eglise d'Auvers-sur-Oise", R.drawable.img2))
        recyclerDataArrayList!!.add(Produit("Marque-page, Eglise d'Auvers-sur-Oise", R.drawable.img2_1))
        recyclerDataArrayList!!.add(Produit("Poster, La Nuit étoilée", R.drawable.img3))
        recyclerDataArrayList!!.add(Produit("Mug, La Nuit étoilée", R.drawable.img3_1))
        recyclerDataArrayList!!.add(Produit("Puzzle, La Nuit étoilée", R.drawable.img3_2))
        recyclerDataArrayList!!.add(Produit("Boîte, La Nuit étoilée", R.drawable.img3_6))
        recyclerDataArrayList!!.add(Produit("Poster, Les tournesols", R.drawable.img4))
        recyclerDataArrayList!!.add(Produit("Magnet, Les tournesols", R.drawable.img4_1))
        recyclerDataArrayList!!.add(Produit("Magnet, Les tournesols", R.drawable.img4_2))
        recyclerDataArrayList!!.add(Produit("Puzzle, Les tournesols", R.drawable.img4_3))
        recyclerDataArrayList!!.add(Produit("Ensemble carnet, marque-page et pins, Les tournesols", R.drawable.img4_4))
        recyclerDataArrayList!!.add(Produit("Pendentif, Les tournesols", R.drawable.img4_5))
        recyclerDataArrayList!!.add(Produit("Boîte, Les tournesols", R.drawable.img4_6))
        recyclerDataArrayList!!.add(Produit("Savon, Les tournesols", R.drawable.img4_7))

        // added data from arraylist to adapter class.
        val adapter = ProduitAdapter(recyclerDataArrayList!!,{produit : Produit -> produitClicked(produit)})

        // setting grid layout manager to implement grid view.
        // in this method '2' represents number of columns to be displayed in grid view.
        val layoutManager = GridLayoutManager(activity, 3)

        // at last set adapter to recycler view.
        recyclerView?.setLayoutManager(layoutManager)
        recyclerView?.setAdapter(adapter)


    }

    private fun produitClicked(produit: Produit) {
        Toast.makeText(this.context, "Clicked: ${produit.nom}", Toast.LENGTH_SHORT).show()


        val activiteVisee = Intent(this.context, ProduitActivity::class.java)
        activiteVisee.putExtra("nom", produit.nom)
        activiteVisee.putExtra("prix", produit.prix)
        activiteVisee.putExtra("imageRes", produit.image)

        startActivity(activiteVisee)

    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_settings, container, false)
    }

}