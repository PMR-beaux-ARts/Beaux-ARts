package com.example.beaux_arts.donnees

//class created for a demo of speech recognition
class CollectionCoord {

    fun transfer(collectionName: String): Array<Double> {
        return when(collectionName){
            "Mona Lisa"-> arrayOf(1.0, 2.5, 3.4)
            else -> arrayOf(0.0,4.2,5.1)
        }
    }
}