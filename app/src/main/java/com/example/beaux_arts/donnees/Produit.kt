package com.example.beaux_arts.donnees

data class Produit (

    val numero : Int,
    val nom : String,
    val type : String,
    val prix : Float,
    val image: String,
    val mescollections: MutableList<Collection>

    ) {
    override fun toString(): String {
        return "Produit(numero=$numero, nom='$nom', type='$type', prix=$prix, image='$image', mescollections=$mescollections)"
    }
}
