package com.example.beaux_arts.donnees

data class Collection(

    val id: Int = 0,
    val nom: String = "null",
    val auteur: String = "anonyme",
    val type: String = "Tableau",
    val description: String = "un tableau",
    val salle: Int = 1,
    //var mesproduits: MutableList<Produit>? = null,
    val mesproduits: MutableList<Produit> = arrayListOf(),
    //val position: Array<Double> = arrayOf(0.00,0.00),
    val image: Int
    //val image: String = "image1.jpg"

//    var title : String,
//    var author: String = "unknown",
//    var imageRes: Int,
//    var description: String = "un tableau",
//    var produits: MutableList<Produit>? = null

){
   // constructor(title : String,imageRes : Int) : this(title, "unknown", imageRes, "Un tableau")
//    override fun toString(): String {
//        return "Collection(id=$id, nom='$nom', auteur='$auteur', type='$type', desciption='$desciption', salle=$salle, mesproduits=$mesproduits, position=${position.contentToString()}, image='$image')"
//    }
    //constructor(title : String, imageRes : Int, produits: MutableList<Produit>?) : this(title, "unknown", imageRes, "Un tableau", produits)

    //constructor(title : String, imageRes : Int, description: String, produits: MutableList<Produit>?) : this(title, "unknown", imageRes, description, produits)
}


