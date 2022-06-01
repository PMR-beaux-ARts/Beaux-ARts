package com.example.beaux_arts.donnees

data class Produit (

    val id : Int,
    val nom : String,
    val type : String,
    val prix : Float,
    val image: String,
    val mescollections: MutableList<Collection>

    ) {
    override fun toString(): String {
        return "Produit(id=$id, nom='$nom', type='$type', prix=$prix, image='$image', mescollections=$mescollections)"
    }
}
