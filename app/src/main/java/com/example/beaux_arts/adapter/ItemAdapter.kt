package com.example.beaux_arts.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.beaux_arts.R
import com.example.beaux_arts.donnees.Itineraire

class ItemAdapter(
    private val dataSet: List<Itineraire>,
    private val actionListener: ActionListener
) : RecyclerView.Adapter<ItemAdapter.ItemViewHolder>() {

    override fun getItemCount(): Int = dataSet.size

    override fun getItemViewType(position: Int): Int {
        return if (position == 0) HEADER_ITEM_ID else ITEM_ID
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ItemViewHolder {
        val itemView =
            if (viewType == HEADER_ITEM_ID) {
                LayoutInflater.from(parent.context)
                    .inflate(R.layout.item_layout, parent, false)

            } else {
                LayoutInflater.from(parent.context).inflate(R.layout.item_layout, parent, false)

            }

        return ItemViewHolder(itemView = itemView)
    }

    override fun onBindViewHolder(holder: ItemViewHolder, position: Int) {
        holder.bind(item = dataSet[position])
    }

    companion object {
        const val HEADER_ITEM_ID = 0
        const val ITEM_ID = 1
    }
    inner class ItemViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {

        private val image = itemView.findViewById<ImageView>(R.id.image)
        private val title = itemView.findViewById<TextView>(R.id.title)
        private val subtitle = itemView.findViewById<TextView>(R.id.subtitle)

        fun bind(item: Itineraire) {
            title.text = item.title
            subtitle.text = item.subTitle
            image.setImageResource(item.imageRes)
        }

        init {
            itemView.setOnClickListener{

                val postPosition = adapterPosition
                if (postPosition != RecyclerView.NO_POSITION){
                    val clickedPosition = dataSet[adapterPosition]
                    actionListener.onItemClicked(clickedPosition,postPosition)
                }


            }
        }

    }
    interface ActionListener{
        fun onItemClicked(listItem: Itineraire, position: Int)
    }

}