package com.example.beaux_arts

import android.content.Intent
import android.database.sqlite.SQLiteDatabase
import android.graphics.BitmapFactory
import android.graphics.drawable.BitmapDrawable
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.beaux_arts.donnees.Collection
import com.example.beaux_arts.donnees.ImportDB
import com.example.beaux_arts.donnees.Produit
import com.example.recyclerviewusinggridlayoutmanager.CollectionAdapter


class CollectionFragment : Fragment() {

    private val CAT: String = "collection page"
//    lateinit var importDB:ImportDB
    //private var recyclerView: RecyclerView? = null
    private var recyclerDataArrayList: ArrayList<Collection>? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        Log.i(CAT,"onCreate")

    }

//    private fun Init() {
//        importDB = ImportDB(this)
//        importDB.openDatabase()
//    }

    override fun onStart() {
        super.onStart()
        Log.i(CAT,"onStart")
        var recyclerView: RecyclerView? = view?.findViewById(R.id.idCourseRV)

        recyclerDataArrayList = ArrayList()


//       //Initialiser la database
//        Init()
        var database = SQLiteDatabase.openOrCreateDatabase(ImportDB.DB_PATH+ "/" + ImportDB.DB_NAME, null)
        Log.i("test","Open database")

        var cursor = database.rawQuery("SELECT * FROM Collection",null)

        //val produitsPiece1 = mutableListOf<Produit>(Produit("Poster", R.drawable.img1))
        if(cursor.moveToFirst()){
            do {
                var Co_id = cursor.getInt(cursor.getColumnIndex("id"))
                var Co_nom = cursor.getString(cursor.getColumnIndex("nom"))
                var Co_auteur = cursor.getString(cursor.getColumnIndex("auteur"))
                var Co_type = cursor.getString(cursor.getColumnIndex("type"))
                var Co_description = cursor.getString(cursor.getColumnIndex("description"))
                var Co_salle = cursor.getInt(cursor.getColumnIndex("salle"))
                var Co_position = cursor.getString(cursor.getColumnIndex("position"))
                val img_b = cursor.getBlob(cursor.getColumnIndex("image"))
                val img_bitmap = BitmapFactory.decodeByteArray(img_b,0,img_b.size,null)
                val Co_image=BitmapDrawable(resources,img_bitmap)
                //lieer les produits aux collections, dans database, mesproduits est une liste en texte
                //var Co_mesproduits = cursor.getString(cursor.getColumnIndex("mesproduits"))
                //var Co_mesproduits = mutableListOf<Produit>(Produit("Poster", R.drawable.img1))
                var Co_mesproduits = null

                var newCollection = Collection(
                    Co_id,
                    Co_nom,
                    Co_auteur,
                    Co_type,
                    Co_description,
                    Co_salle,
                    Co_mesproduits,
                    Co_image
                )
                // added data to array list
                recyclerDataArrayList!!.add(newCollection)
            } while (cursor.moveToNext())
        }
        cursor.close()
        database.close()

        // added data from arraylist to adapter class.
        val adapter = CollectionAdapter(recyclerDataArrayList!!,{collection : Collection -> collectionClicked(collection)})

        // setting grid layout manager to implement grid view.
        // in this method '2' represents number of columns to be displayed in grid view.
        val layoutManager = GridLayoutManager(activity, 2)

        // at last set adapter to recycler view.
        recyclerView?.setLayoutManager(layoutManager)
        recyclerView?.setAdapter(adapter)


    }

    private fun collectionClicked(collection: Collection) {
        Toast.makeText(this.context, "Clicked: ${collection.nom}", Toast.LENGTH_SHORT).show()
        val activiteVisee = Intent(this.context, PieceActivity::class.java)
        activiteVisee.putExtra("title", collection.nom)
        activiteVisee.putExtra("author", collection.auteur)
        activiteVisee.putExtra("id", collection.id)
        activiteVisee.putExtra("description", collection.description)

//        var i = 0
//        Log.i("test","avant vérification if(!collection.mesproduits!!.isEmpty())")
//        if(collection.mesproduits != null) {
//            Log.i("test","dans if(!collection.produits!!.isEmpty())")
//            for (produit in collection.mesproduits!!) {
//                Log.i("test","dans la boucle for")
//                i++
//                activiteVisee.putExtra("produit.nom$i", produit.nom)
//                activiteVisee.putExtra("produit.id$i", produit.id)
//                //var j = produit.image
//                //Log.i("test","ListFragment : le code de l'image $j")
//            }
//        }
//        Log.i("test","il y a $i produits")
//        activiteVisee.putExtra("nbProduits", i)

        startActivity(activiteVisee)

    }


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_colle, container, false)
    }

}