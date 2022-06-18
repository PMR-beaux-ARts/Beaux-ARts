package com.example.recyclerviewusinggridlayoutmanager

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.beaux_arts.R
import com.example.beaux_arts.donnees.Collection

class CollectionAdapter(
    private val courseDataArrayList: List<Collection>,
    val clickListener:(Collection) -> Unit
//    private val mcontext: Context
) :
    RecyclerView.Adapter<CollectionAdapter.RecyclerViewHolder>() {
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

        fun bind(collection : Collection, clickListener: (Collection) -> Unit){
            courseTV.text = collection.nom
            courseTV.setOnClickListener {clickListener(collection)}
            courseIV.setOnClickListener {clickListener(collection)}
        }
    }
}
