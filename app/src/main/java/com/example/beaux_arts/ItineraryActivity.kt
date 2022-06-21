package com.example.beaux_arts

import android.content.Intent
import android.graphics.*
import android.graphics.drawable.BitmapDrawable
import android.graphics.drawable.ShapeDrawable
import android.graphics.drawable.shapes.OvalShape
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.ImageView
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

        createMap()

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

    fun createMap() {

        val imageDeFond = findViewById<ImageView>(R.id.itineraireMapView)
        //on crée une bitmap vide, on spécifie qu'on qu'on dessine dessus
        val bitmap: Bitmap = Bitmap.createBitmap(700, 800, Bitmap.Config.ARGB_8888)
        val canvas: Canvas = Canvas(bitmap)




        /*exemples
        *
        *
        *
        * */

        val contourPiece = Path()
        val paint = Paint()
        paint.setColor(Color.parseColor("#aeebed"))

        contourPiece.moveTo(100f,100f)

        contourPiece.lineTo(100f,100f)
        contourPiece.lineTo(100f,800f)
        contourPiece.lineTo(650f,800f)
        contourPiece.lineTo(650f,100f)

        canvas.drawPath(contourPiece,paint)

        placerOeuvre(canvas,200,700,"oeuvre1")
        placerOeuvre(canvas,422,400,"oeuvre2")
        placerOeuvre(canvas,511,600,"oeuvre3")

        //ici liste des points de l'itineraire (à voir pour la structure de la bdd)
        var listeEtapes = arrayOf<Float>(200F+20,700F+20,422F+20,400F+20,422F+20,400F+20,511F+20,600F+20)

        //ici on convertit le array<float> en FloatArray
        val listeEtapesFinal = listeEtapes.toFloatArray()
        placerItineraire(canvas, listeEtapesFinal)

        /*exemples
        *
        *
        *
        * */

        //afficher la bitmap modifiée précédement

        imageDeFond.background = BitmapDrawable(getResources(), bitmap)
    }
}