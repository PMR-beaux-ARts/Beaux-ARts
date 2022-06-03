package com.example.beaux_arts.donnees

data class Produit(

    var id: Int,
    var nom: String,
    var type: String,
    var prix: Double,
    var image: Int,
    var mescollections: MutableList<Collection>?

    ) {

    constructor(nom: String, image: Int) : this(0, nom, "type", 4.5, image, null)

    override fun toString(): String {
        return "Produit(id=$id, nom='$nom', type='$type', prix=$prix, image='$image', mescollections=$mescollections)"
    }


}
