package com.example.beaux_arts

import android.content.Intent
import android.database.sqlite.SQLiteDatabase
import android.graphics.BitmapFactory
import android.graphics.drawable.BitmapDrawable
import android.graphics.drawable.Drawable
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.graphics.drawable.toBitmap
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.beaux_arts.adapter.ProduitAdapter
import com.example.beaux_arts.donnees.Collection
import com.example.beaux_arts.donnees.ImportDB
import com.example.beaux_arts.donnees.Produit

class ShopFragment : Fragment() {

    private val CAT: String = "Shop page"

    private var recyclerDataArrayList: ArrayList<Produit>? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        Log.i(CAT,"onCreate")
    }
    override fun onStart() {
        super.onStart()
        Log.i(CAT,"onStart")
        val recyclerView: RecyclerView? = view?.findViewById(R.id.souvenirList)

        recyclerDataArrayList = ArrayList()

        val database = SQLiteDatabase.openOrCreateDatabase(ImportDB.DB_PATH+ "/" + ImportDB.DB_NAME, null)
        Log.i("test","Open database")

        val cursor = database.rawQuery("SELECT * FROM Produit",null)

        //val produitsPiece1 = mutableListOf<Produit>(Produit("Poster", R.drawable.img1))
        if(cursor.moveToFirst()){
            do {
                val Pr_id = cursor.getInt(cursor.getColumnIndex("id"))
                val Pr_nom = cursor.getString(cursor.getColumnIndex("nom"))
                val Pr_mescollection = null
                val Pr_type = cursor.getString(cursor.getColumnIndex("type"))
                val Pr_prix = cursor.getDouble(cursor.getColumnIndex("prix"))
                val img_b = cursor.getBlob(cursor.getColumnIndex("image"))
                val img_bitmap = BitmapFactory.decodeByteArray(img_b,0,img_b.size,null)
                val img_bitmapDrawable = BitmapDrawable(resources,img_bitmap)
                val Pr_image = img_bitmapDrawable
                val newProduit = Produit(
                    Pr_id,
                    Pr_nom,
                    Pr_type,
                    Pr_prix,
                    Pr_image,
                    Pr_mescollection
                )
                // added data to array list
                recyclerDataArrayList!!.add(newProduit)
            } while (cursor.moveToNext())
        }
        cursor.close()
        database.close()

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
        activiteVisee.putExtra("id", produit.id)

        startActivity(activiteVisee)

    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_shop, container, false)
    }

}