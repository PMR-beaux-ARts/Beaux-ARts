package com.example.beaux_arts

import android.content.Intent
import android.database.sqlite.SQLiteDatabase
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
import com.example.beaux_arts.donnees.DBManager
import com.example.beaux_arts.donnees.ImportDB
import com.example.beaux_arts.donnees.Produit
import com.example.recyclerviewusinggridlayoutmanager.CollectionAdapter


class CollectionFragment : Fragment() {

    private val CAT: String = "collection page"
    lateinit var importDB:ImportDB
    //private var recyclerView: RecyclerView? = null
    private var recyclerDataArrayList: ArrayList<Collection>? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        Log.i(CAT,"onCreate")

    }

    private fun Init() {
        importDB = ImportDB(this)
        importDB.openDatabase()
    }

    override fun onStart() {
        super.onStart()
        Log.i(CAT,"onStart")
        var recyclerView: RecyclerView? = view?.findViewById(R.id.idCourseRV)

        recyclerDataArrayList = ArrayList()


        //Initialiser la database
        Init()
        var database = SQLiteDatabase.openOrCreateDatabase(ImportDB.DB_PATH+ "/" + ImportDB.DB_NAME, null)
        Log.i("test","Open database")

        var cursor = database.rawQuery("SELECT * FROM Collection",null)

        val produitsPiece1 = mutableListOf<Produit>(Produit("Poster", R.drawable.img1))
        if(cursor.moveToFirst()){
            do {
                var Co_id = cursor.getInt(cursor.getColumnIndex("id"))
                var Co_nom = cursor.getString(cursor.getColumnIndex("nom"))
                var Co_auteur = cursor.getString(cursor.getColumnIndex("auteur"))
                var Co_type = cursor.getString(cursor.getColumnIndex("type"))
                var Co_description = cursor.getString(cursor.getColumnIndex("description"))
                var Co_salle = cursor.getInt(cursor.getColumnIndex("salle"))
                //var Co_mesproduits = cursor.getString(cursor.getColumnIndex("mesproduits"))
                var Co_mesproduits = produitsPiece1
                var Co_position = cursor.getString(cursor.getColumnIndex("position"))
                //var Co_image = cursor.getBlob(cursor.getColumnIndex("image"))
                var newCollection = Collection(
                    Co_id,
                    Co_nom,
                    Co_auteur,
                    Co_type,
                    Co_description,
                    Co_salle,
                    Co_mesproduits,
                    R.drawable.img1
                )
                recyclerDataArrayList!!.add(newCollection)
            } while (cursor.moveToNext())
        }
        cursor.close()

        // added data to array list
        //val produitsPiece1 = mutableListOf<Produit>(Produit("Poster", R.drawable.img1))
        val produitsPiece2 = mutableListOf<Produit>(Produit("Poster", R.drawable.img2))
        val produitsPiece3 = mutableListOf<Produit>(Produit("Poster", R.drawable.img3),Produit("Mug", R.drawable.img3_1),Produit("Puzzle", R.drawable.img3_2))
        val produitsPiece4 = mutableListOf<Produit>(Produit("Poster", R.drawable.img4))
        //val piece1 = Collection("Saint Rémy", R.drawable.img1, produitsPiece1)
        //val piece2 = Collection("Eglise d'Auvers-sur-Oise", R.drawable.img2, produitsPiece2)
        //val piece3 = Collection("La Nuit étoilée", R.drawable.img3, "La Nuit étoilée (en néerlandais De sterrennacht) est une peinture de l'artiste peintre postimpressionniste néerlandais Vincent van Gogh. Le tableau représente ce que Van Gogh pouvait voir et extrapoler de la chambre qu'il occupait dans l'asile du monastère Saint-Paul-de-Mausole à Saint-Rémy-de-Provence en mai 1889. Souvent présenté comme son grand œuvre, le tableau a été reproduit à de très nombreuses reprises." +
//                "\n\n" + "Génèse de l'oeuvre" +
//                "\n\n" + "À l'automne 1888, alors que Van Gogh réside à Arles, il réalise une peinture connue sous le nom de Nuit étoilée sur le Rhône. Presque un an après, en juin 1889, il annonce « une nouvelle étude d'un ciel étoilé ». En mi-septembre 1889, après avoir été admis dans l'asile du monastère Saint-Paul-de-Mausole dû à une crise de nerfs qui a duré de mi-juin jusqu'à fin août, il inclut cette nuit étoilée dans un des travaux qu'il envoie à son frère, Théo, à Paris.\n" +
//                "\n" +
//                "Pour le peintre, « la nuit est beaucoup plus vivante et richement colorée que le jour ».\n" +
//                "\n" +
//                "Le ciel représenté dans le tableau correspondrait à la configuration céleste visible à Saint-Rémy-de-Provence, le 25 mai 1889, à 4:40 précisément.",produitsPiece3)
//        val piece4 = Collection("Les Tournesols", R.drawable.img4, produitsPiece4)
//        recyclerDataArrayList!!.add(piece1)
//        recyclerDataArrayList!!.add(piece2)
//        recyclerDataArrayList!!.add(piece3)

//        recyclerDataArrayList!!.add(Collection("Saint Rémy", R.drawable.img1))
//        recyclerDataArrayList!!.add(Collection("Eglise d'Auvers-sur-Oise", R.drawable.img2))
//        recyclerDataArrayList!!.add(Collection("La Nuit étoilée", R.drawable.img3))
//        recyclerDataArrayList!!.add(Collection("Les Tournesols", R.drawable.img4))
//        recyclerDataArrayList!!.add(piece1)
//        recyclerDataArrayList!!.add(piece2)
//        recyclerDataArrayList!!.add(piece3)
//        recyclerDataArrayList!!.add(piece4)
//        recyclerDataArrayList!!.add(Collection("Saint Rémy", R.drawable.img1))
//        recyclerDataArrayList!!.add(Collection("Eglise d'Auvers-sur-Oise", R.drawable.img2))
//        recyclerDataArrayList!!.add(Collection("La Nuit étoilée", R.drawable.img3))
//        recyclerDataArrayList!!.add(Collection("Les Tournesols", R.drawable.img4))

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
        activiteVisee.putExtra("imageRes", collection.image)
        activiteVisee.putExtra("description", collection.description)

        var i = 0
        Log.i("test","avant vérification if(!collection.mesproduits!!.isEmpty())")
        if(collection.mesproduits != null) {
            Log.i("test","dans if(!collection.produits!!.isEmpty())")
            for (produit in collection.mesproduits!!) {
                Log.i("test","dans la boucle for")
                i++
                activiteVisee.putExtra("produit.nom$i", produit.nom)
                activiteVisee.putExtra("produit.imageRes$i", produit.image)
                var j = produit.image
                Log.i("test","ListFragment : le code de l'image $j")
            }
        }
        Log.i("test","il y a $i produits")
        activiteVisee.putExtra("nbProduits", i)

        startActivity(activiteVisee)

    }


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_colle, container, false)
    }

}