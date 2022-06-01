package com.example.beaux_arts.donnees

data class Collection(

    val id: Int = 0,
    val nom: String = "null",
    val auteur: String = "anonyme",
    val type: String = "Tableau",
    val desciption: String = "un tableau",
    val salle: Int = 1,
    val mesproduits: MutableList<Produit> = arrayListOf(),
    val position: Array<Double> = arrayOf(0.00,0.00),
    val image: String = "image1.jpg"

){
    override fun toString(): String {
        return "Collection(id=$id, nom='$nom', auteur='$auteur', type='$type', desciption='$desciption', salle=$salle, mesproduits=$mesproduits, position=${position.contentToString()}, image='$image')"
    }

}


