package com.example.beaux_arts.donnees

data class Itineraire(

    val numero : Int,
    val nom : String,
    val type : String,
    val decription : String,
    val duree: Int,
    val mescollections: MutableList<Collection>,
    val carte : String

){
    override fun toString(): String {
        return "Itineraires(numero=$numero, nom='$nom', type='$type', decription='$decription', duree=$duree, mescollections=$mescollections, carte='$carte')"
    }
}
