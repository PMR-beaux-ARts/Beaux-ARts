package com.example.beaux_arts

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.Spinner
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.RecyclerView
import kotlinx.android.synthetic.main.fragment_home.*
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView.VERTICAL
import com.example.beaux_arts.donnees.Itineraire
import fr.ec.sequence1.ui.adapter.ItemAdapter


class HomeFragment : Fragment() {



    val CAT : String = "homepage"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        Log.i(CAT,"onCreate")





    }
    fun provideDataSet(): List<Itineraire> {
        val result = mutableListOf<Itineraire>()
        repeat(1_000) { intex ->
            val item = Itineraire(
                imageRes = R.mipmap.ic_launcher,
                title = "Titre $intex",
                subTitle = "Sous-Titre $intex",
            )

            result.add(item)
        }
        return result
    }



    override fun onStart() {
        super.onStart()
        Log.i(CAT,"onStart")

        val list = view?.findViewById<RecyclerView>(R.id.list)
        list?.adapter = ItemAdapter(dataSet = provideDataSet())
        list?.layoutManager = LinearLayoutManager(activity, VERTICAL, false)

        spinner1.onItemSelectedListener = object : AdapterView.OnItemSelectedListener{
            override fun onItemSelected(
                parent: AdapterView<*>?,
                view: View?,
                position: Int,
                id: Long
            ) {
                Toast.makeText(activity,
                    "you selected ${parent?.getItemAtPosition(position).toString()}",
                    Toast.LENGTH_SHORT).show()
            }

            override fun onNothingSelected(parent: AdapterView<*>?) {

            }
        }
        spinner2.onItemSelectedListener = object : AdapterView.OnItemSelectedListener{
            override fun onItemSelected(
                parent: AdapterView<*>?,
                view: View?,
                position: Int,
                id: Long
            ) {
                Toast.makeText(activity,
                    "you selected ${parent?.getItemAtPosition(position).toString()}",
                    Toast.LENGTH_SHORT).show()
            }

            override fun onNothingSelected(parent: AdapterView<*>?) {

            }
        }




        //Todo next recyclerview



    }


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        return inflater.inflate(R.layout.fragment_home, container, false)
    }

}