package com.example.recyclerviewusinggridlayoutmanager

import android.content.Context
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
        holder.courseTV.text = recyclerData.title
        holder.courseIV.setImageResource(recyclerData.imageRes)
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
    }
}
