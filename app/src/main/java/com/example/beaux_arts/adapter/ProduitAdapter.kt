package com.example.beaux_arts.adapter

import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.beaux_arts.R
import com.example.beaux_arts.donnees.Produit

class ProduitAdapter(
    private val courseDataArrayList: List<Produit>,
    val clickListener:(Produit) -> Unit
//    private val mcontext: Context
) :
    RecyclerView.Adapter<ProduitAdapter.RecyclerViewHolder>() {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerViewHolder {
        // Inflate Layout
        val view = LayoutInflater.from(parent.context).inflate(R.layout.card_layout, parent, false)
        return RecyclerViewHolder(view)
    }

    override fun onBindViewHolder(holder: RecyclerViewHolder, position: Int) {
        // Set the data to textview and imageview.
        val recyclerData = courseDataArrayList[position]
        holder.courseTV.text = recyclerData.nom
        holder.courseIV.setImageDrawable(recyclerData.image)
        holder.bind(courseDataArrayList[position], clickListener)
    }

    override fun getItemCount(): Int {
        // this method returns the size of recyclerview
        Log.i("test","courseDataArrayList?.size = $courseDataArrayList?.size")
        return courseDataArrayList.size
    }

    // View Holder Class to handle Recycler View.
    inner class RecyclerViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val courseTV: TextView
        val courseIV: ImageView

        init {
            courseTV = itemView.findViewById(R.id.idTVCourse)
            courseIV = itemView.findViewById(R.id.idIVcourseIV)
        }
        fun bind(produit : Produit, clickListener: (Produit) -> Unit){
            courseTV.text = produit.nom
            courseIV.setOnClickListener {clickListener(produit)}
        }
    }


}
