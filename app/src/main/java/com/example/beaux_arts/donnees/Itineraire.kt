package com.example.beaux_arts.donnees

import android.graphics.drawable.Drawable
import android.media.Image

data class Itineraire(

    val id: Int,
    val nom: String,
    val type: String,
    val decription: String,
    val duree: Int,
    val mescollections: MutableList<Produit>? = null,
    val carte: String="carte",
    val image: Drawable? = null
// test varable below should be replaced after
//    var title : String,
//    var subTitle : String,
//    var imageRes: Int,

){
//    override fun toString(): String {
//        return "Itineraires(id=$id, nom='$nom', type='$type', decription='$decription', duree=$duree, mescollections=$mescollections, carte='$carte')"
//    }
}
