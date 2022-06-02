package com.example.beaux_arts.donnees

data class Collection(

//    val id: Int = 0,
//    val nom: String = "null",
//    val auteur: String = "anonyme",
//    val type: String = "Tableau",
//    val desciption: String = "un tableau",
//    val salle: Int = 1,
//    val mesproduits: MutableList<Produit> = arrayListOf(),
//    val position: Array<Double> = arrayOf(0.00,0.00),
//    val image: String = "image1.jpg"

    val title : String,
    val auteur: String = "unknown",
    val imageRes: Int,
    val description: String = "un tableau",
    val produits: MutableList<Produit>? = null

){
    constructor(title : String,imageRes : Int) : this(title, "unknown", imageRes, "Un tableau")
//    override fun toString(): String {
//        return "Collection(id=$id, nom='$nom', auteur='$auteur', type='$type', desciption='$desciption', salle=$salle, mesproduits=$mesproduits, position=${position.contentToString()}, image='$image')"
//    }
    constructor(title : String, imageRes : Int, produits: MutableList<Produit>?) : this(title, "unknown", imageRes, "Un tableau", produits)
}


