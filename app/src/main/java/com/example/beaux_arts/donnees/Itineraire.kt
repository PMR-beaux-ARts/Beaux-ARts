package com.example.beaux_arts.donnees

data class Itineraire(

    val id : Int,
    val nom : String,
    val type : String,
    val decription : String,
    val duree: Int,
    val mescollections: MutableList<Collection>,
    val carte : String

){
    override fun toString(): String {
        return "Itineraires(id=$id, nom='$nom', type='$type', decription='$decription', duree=$duree, mescollections=$mescollections, carte='$carte')"
    }
}
