package com.example.beaux_arts.donnees

data class Produit(

    val id: Int,
    val nom: String,
    val type: String,
    val prix: Double,
    val image: Int,
    val mescollections: MutableList<Collection>?

    ) {

    constructor(nom: String, image: Int) : this(0, nom, "type", 4.5, 0, null)

    override fun toString(): String {
        return "Produit(id=$id, nom='$nom', type='$type', prix=$prix, image='$image', mescollections=$mescollections)"
    }


}
