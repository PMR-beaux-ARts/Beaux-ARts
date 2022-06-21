package com.example.beaux_arts

import android.content.Intent
import android.database.sqlite.SQLiteDatabase
import android.graphics.BitmapFactory
import android.graphics.drawable.BitmapDrawable
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.RecyclerView
import kotlinx.android.synthetic.main.fragment_itin.*
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView.VERTICAL
import com.example.beaux_arts.donnees.ImportDB
import com.example.beaux_arts.donnees.Itineraire
import fr.ec.sequence1.ui.adapter.ItemAdapter


class ItineraireFragment : Fragment() {

    val CAT : String = "homepage"
    private var recyclerDataArrayList: ArrayList<Itineraire>? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        Log.i(CAT,"onCreate")

    }

    override fun onStart() {
        super.onStart()
        Log.i(CAT,"onStart")
        val list = view?.findViewById<RecyclerView>(R.id.list)

        recyclerDataArrayList = ArrayList()

        //Initialiser la database
        var database = SQLiteDatabase.openOrCreateDatabase(ImportDB.DB_PATH+ "/" + ImportDB.DB_NAME, null)
        Log.i("test","Open database")

        var cursor = database.rawQuery("SELECT * FROM Itineraire",null)

        if(cursor.moveToFirst()){
            do {
                var It_id = cursor.getInt(cursor.getColumnIndex("id"))
                //Log.i("test","$It_id")
                var It_nom = cursor.getString(cursor.getColumnIndex("nom"))
                var It_type = cursor.getString(cursor.getColumnIndex("type"))
                var It_description = cursor.getString(cursor.getColumnIndex("description"))
                var It_duree = cursor.getInt(cursor.getColumnIndex("duree"))
                val img_b = cursor.getBlob(cursor.getColumnIndex("image"))
                val img_bitmap = BitmapFactory.decodeByteArray(img_b,0,img_b.size,null)
                val It_image= BitmapDrawable(resources,img_bitmap)
                var It_mescollection = null

                var newItineraire = Itineraire(
                    It_id,
                    It_nom,
                    It_type,
                    It_description,
                    It_duree,
                    It_mescollection,
                    "carte",
                    It_image
                )
                // added data to array list
                recyclerDataArrayList!!.add(newItineraire)
                Log.i("test","$recyclerDataArrayList")
            } while (cursor.moveToNext())
        }
        cursor.close()
        database.close()

        list?.adapter = ItemAdapter(recyclerDataArrayList!!,{itineraire : Itineraire -> itineraireClicked(itineraire)})
        list?.layoutManager = LinearLayoutManager(activity, VERTICAL, false)

        spinner1.onItemSelectedListener = object : AdapterView.OnItemSelectedListener{
            override fun onItemSelected(
                parent: AdapterView<*>?,
                view: View?,
                position: Int,
                id: Long
            ) {
                Toast.makeText(activity,
                    "you selected ${parent?.getItemAtPosition(position).toString()}",
                    Toast.LENGTH_SHORT).show()
            }

            override fun onNothingSelected(parent: AdapterView<*>?) {

            }
        }
        spinner2.onItemSelectedListener = object : AdapterView.OnItemSelectedListener{
            override fun onItemSelected(
                parent: AdapterView<*>?,
                view: View?,
                position: Int,
                id: Long
            ) {
                Toast.makeText(activity,
                    "you selected ${parent?.getItemAtPosition(position).toString()}",
                    Toast.LENGTH_SHORT).show()
            }

            override fun onNothingSelected(parent: AdapterView<*>?) {

            }
        }
    }

    private fun itineraireClicked(itineraire: Itineraire) {
        Toast.makeText(this.context, "Clicked: ${itineraire.nom}", Toast.LENGTH_SHORT).show()
        val activiteVisee = Intent(this.context, ItineraryActivity::class.java)

        activiteVisee.putExtra("id", itineraire.id)

        startActivity(activiteVisee)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_itin, container, false)
    }
}