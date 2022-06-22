package fr.ec.sequence1.ui.adapter

import android.content.ClipData
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.beaux_arts.R
import com.example.beaux_arts.donnees.Collection
import com.example.beaux_arts.donnees.Itineraire

class ItemAdapter(
    private val dataSet: List<Itineraire>,
    val clickListener:(Itineraire) -> Unit
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
//        holder.bind(item = dataSet[position], clickListener)
        // Set the data to textview and imageview.
        val recyclerData = dataSet[position]
        holder.title.text = recyclerData.nom
        holder.image.setImageDrawable(recyclerData.image)
        holder.subtitle.text = "Thème : " + recyclerData.type
        holder.duree.text = "Durée (min) : " + recyclerData.duree.toString()

        holder.bind(dataSet[position], clickListener)

    }

    companion object {
        const val HEADER_ITEM_ID = 0
        const val ITEM_ID = 1
    }
    class ItemViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {

        val image = itemView.findViewById<ImageView>(R.id.image)
        val title = itemView.findViewById<TextView>(R.id.title)
        val subtitle = itemView.findViewById<TextView>(R.id.subtitle)
        val duree = itemView.findViewById<TextView>(R.id.duree)

        fun bind(item: Itineraire, clickListener: (Itineraire) -> Unit) {
            title.text = item.nom
            subtitle.text = item.type
            image.setImageDrawable(item.image)

            title.setOnClickListener { clickListener(item) }
            subtitle.setOnClickListener { clickListener(item) }
            image.setOnClickListener { clickListener(item) }
        }

    }

}