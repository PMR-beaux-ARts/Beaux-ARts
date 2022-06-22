package com.example.beaux_arts.donnees

import android.graphics.drawable.Drawable
import com.example.beaux_arts.R

data class Produit(

    var id: Int,
    var nom: String,
    var type: String,
    var prix: Double,
    var image: Drawable,
    var mescollections: MutableList<Collection>? = null

    ) {

    override fun toString(): String {
        return "Produit(id=$id, nom='$nom', type='$type', prix=$prix, image='$image', mescollections=$mescollections)"
    }


}
