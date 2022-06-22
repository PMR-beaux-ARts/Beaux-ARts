package com.example.beaux_arts

import android.annotation.SuppressLint
import android.content.Intent
import android.database.sqlite.SQLiteDatabase
import android.graphics.*
import android.graphics.drawable.BitmapDrawable
import android.graphics.drawable.ShapeDrawable
import android.graphics.drawable.shapes.OvalShape
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.method.ScrollingMovementMethod
import android.util.Log
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.beaux_arts.donnees.Collection
import com.example.beaux_arts.donnees.ImportDB
import com.example.beaux_arts.donnees.Produit
import com.example.recyclerviewusinggridlayoutmanager.CollectionAdapter
import kotlinx.android.synthetic.main.activity_itinerary.*
import org.json.JSONObject

class ItineraryActivity : AppCompatActivity() {

    private var recyclerDataArrayList: ArrayList<Collection>? = ArrayList()

    @SuppressLint("Range")
    override fun onCreate(savedInstanceState: Bundle?) {
        val database = SQLiteDatabase.openOrCreateDatabase(ImportDB.DB_PATH+ "/" + ImportDB.DB_NAME, null)
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_itinerary)
        supportActionBar?.hide()

        var itineraireId = intent.getIntExtra("id",1)
        Log.i("text","itineraireId:$itineraireId")

        val ItineraireName = findViewById<TextView>(R.id.itineraryName)
        var description: TextView = findViewById(R.id.itineraireDescription)
        var recyclerView: RecyclerView = findViewById(R.id.oeuvresCollectionVIew)

        val cursor1 = database.rawQuery("SELECT * FROM Itineraire WHERE id = ${itineraireId} ",null)
        Log.i("test","Searched succeed ${cursor1}")
        if(cursor1 != null &&cursor1.moveToFirst()) {
            val nameIt = cursor1.getString(cursor1.getColumnIndex("nom"))
            val descripIt  = cursor1.getString(cursor1.getColumnIndex("description"))
            ItineraireName.text = nameIt
            description.text = descripIt
            description.movementMethod = ScrollingMovementMethod()

            recyclerDataArrayList = ArrayList()
            val collection_json = JSONObject(cursor1.getString(cursor1.getColumnIndex("mescollections")))
            Log.i("test","on obtient produits")
            val keys = collection_json.keys()
            Log.i("test","keys of json${keys}")
            while (keys.hasNext()) {
                var key = keys.next().toString()
                //chercher id dans le database
                val collectionId = collection_json.getInt(key)
                Log.i("test","produitId ${collectionId}")
                val cursor = database.rawQuery("SELECT * FROM Collection WHERE id = ${collectionId} ",null)
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
                        var Co_mesproduits = null
                        var newCollection= Collection(
                            Co_id,
                            Co_nom,
                            Co_auteur,
                            Co_type,
                            Co_description,
                            Co_salle,
                            Co_mesproduits,
                            Co_image
                        )
                        //var img = intent.getIntExtra("produit.imageRes$i", 0)
                        recyclerDataArrayList!!.add(newCollection)
                    } while (cursor.moveToNext())
                }
                cursor.close()
            }
        }
        cursor1.close()
        database.close()

        val adapter = CollectionAdapter(recyclerDataArrayList!!,{collection : Collection -> collectionClicked(collection)})
        val layoutManager = GridLayoutManager(this, 4)
        recyclerView?.setLayoutManager(layoutManager)
        recyclerView?.setAdapter(adapter)

        //createMap()



        button.setOnClickListener(){
            val activiteVisee = Intent(this@ItineraryActivity, MapActivity::class.java)

            val itiId = intent.getIntExtra("id",1)
            activiteVisee.putExtra("id", itiId)

            startActivity(activiteVisee)
        }

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

    fun placerOeuvre(canvas: Canvas, x: Int, y:Int, nom : String){

        //on crée le cercle, la taille est modifiable ci dessous
        var point: ShapeDrawable
        var taille: Int
        taille = 40
        point = ShapeDrawable(OvalShape())
        point.setBounds( x, y, x+taille, y+taille)
        point.getPaint().setColor(Color.parseColor("#34c9eb"))
        point.draw(canvas)

        //on crée la légende à côté
        val paint = Paint()
        paint.setStyle(Paint.Style.FILL);
        paint.color = Color.BLACK
        paint.textSize = 16f
        canvas.drawText(nom, (x+taille*1.2).toFloat(), (y+taille/1.5).toFloat(), paint)


    }

    fun placerItineraire(canvas: Canvas, listePoints:FloatArray){

        var paint = Paint()
        paint.setStyle(Paint.Style.FILL);
        paint.color = Color.RED
        paint.strokeWidth = 16f
        canvas.drawLines(listePoints, paint)

    }

//    fun createMap() {
//
//        val imageDeFond = findViewById<ImageView>(R.id.itineraireMapView)
//        //on crée une bitmap vide, on spécifie qu'on qu'on dessine dessus
//        val bitmap: Bitmap = Bitmap.createBitmap(700, 800, Bitmap.Config.ARGB_8888)
//        val canvas: Canvas = Canvas(bitmap)
//
//
//
//
//        /*exemples
//        *
//        *
//        *
//        * */
//
//        val contourPiece = Path()
//        val paint = Paint()
//        paint.setColor(Color.parseColor("#aeebed"))
//
//        contourPiece.moveTo(100f,100f)
//
//        contourPiece.lineTo(100f,100f)
//        contourPiece.lineTo(100f,800f)
//        contourPiece.lineTo(650f,800f)
//        contourPiece.lineTo(650f,100f)
//
//        canvas.drawPath(contourPiece,paint)
//
//        placerOeuvre(canvas,200,700,"oeuvre1")
//        placerOeuvre(canvas,422,400,"oeuvre2")
//        placerOeuvre(canvas,511,600,"oeuvre3")
//
//        //ici liste des points de l'itineraire (à voir pour la structure de la bdd)
//        var listeEtapes = arrayOf<Float>(200F+20,700F+20,422F+20,400F+20,422F+20,400F+20,511F+20,600F+20)
//
//        //ici on convertit le array<float> en FloatArray
//        val listeEtapesFinal = listeEtapes.toFloatArray()
//        placerItineraire(canvas, listeEtapesFinal)
//
//        /*exemples
//        *
//        *
//        *
//        * */
//
//        //afficher la bitmap modifiée précédement
//
//        imageDeFond.background = BitmapDrawable(getResources(), bitmap)
//    }
}