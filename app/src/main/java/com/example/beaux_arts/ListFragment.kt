package com.example.beaux_arts

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.beaux_arts.donnees.Collection
import com.example.beaux_arts.donnees.Produit
import com.example.recyclerviewusinggridlayoutmanager.CollectionAdapter


class ListFragment : Fragment() {

    private val CAT: String = "collection page"

    //private var recyclerView: RecyclerView? = null
    private var recyclerDataArrayList: ArrayList<Collection>? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        Log.i(CAT,"onCreate")
    }
    override fun onStart() {
        super.onStart()
        Log.i(CAT,"onStart")
        var recyclerView: RecyclerView? = view?.findViewById(R.id.idCourseRV)

        recyclerDataArrayList = ArrayList()

        // added data to array list
        val produitsPiece1 = mutableListOf<Produit>(Produit("Copie", R.drawable.img1))
        recyclerDataArrayList!!.add(Collection("Saint Rémy", R.drawable.img1, produitsPiece1))
        recyclerDataArrayList!!.add(Collection("Eglise d'Auvers-sur-Oise", R.drawable.img2))
        recyclerDataArrayList!!.add(Collection("La Nuit étoilée", R.drawable.img3))
        recyclerDataArrayList!!.add(Collection("Les Tournesols", R.drawable.img4))
        recyclerDataArrayList!!.add(Collection("Saint Rémy", R.drawable.img1))
        recyclerDataArrayList!!.add(Collection("Eglise d'Auvers-sur-Oise", R.drawable.img2))
        recyclerDataArrayList!!.add(Collection("La Nuit étoilée", R.drawable.img3))
        recyclerDataArrayList!!.add(Collection("Les Tournesols", R.drawable.img4))
        recyclerDataArrayList!!.add(Collection("Saint Rémy", R.drawable.img1))
        recyclerDataArrayList!!.add(Collection("Eglise d'Auvers-sur-Oise", R.drawable.img2))
        recyclerDataArrayList!!.add(Collection("La Nuit étoilée", R.drawable.img3))
        recyclerDataArrayList!!.add(Collection("Les Tournesols", R.drawable.img4))
        recyclerDataArrayList!!.add(Collection("Saint Rémy", R.drawable.img1))
        recyclerDataArrayList!!.add(Collection("Eglise d'Auvers-sur-Oise", R.drawable.img2))
        recyclerDataArrayList!!.add(Collection("La Nuit étoilée", R.drawable.img3))
        recyclerDataArrayList!!.add(Collection("Les Tournesols", R.drawable.img4))

        // added data from arraylist to adapter class.
        val adapter = CollectionAdapter(recyclerDataArrayList!!)

        // setting grid layout manager to implement grid view.
        // in this method '2' represents number of columns to be displayed in grid view.
        val layoutManager = GridLayoutManager(activity, 2)

        // at last set adapter to recycler view.
        recyclerView?.setLayoutManager(layoutManager)
        recyclerView?.setAdapter(adapter)


    }





    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_list, container, false)
    }

}