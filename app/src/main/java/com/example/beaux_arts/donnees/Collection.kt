package com.example.beaux_arts.donnees

data class Collection(

    val numero: Int = 0,
    val nom: String = "null",
    val auteur: String = "anonyme",
    val type: String = "Tableau",
    val desciption: String = "un tableau",
    val salle: Int = 1,
    val mesproduits: MutableList<Produit> = arrayListOf(),
    val position: Array<Double> = arrayOf(0.00,0.00),
    val image: String = "image1"

){
    override fun toString(): String {
        return "Collection(numero=$numero, nom='$nom', auteur='$auteur', type='$type', desciption='$desciption', salle=$salle, mesproduits=$mesproduits, position=${position.contentToString()}, image='$image')"
    }

}


