package com.example.beaux_arts

import android.content.DialogInterface
import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import android.widget.TextView
import android.widget.Toast
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AlertDialog
import com.example.beaux_arts.donnees.Musee
import com.google.zxing.integration.android.IntentIntegrator

class InfoFragment : Fragment() {

    private val CAT: String = "chart page"


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        Log.i(CAT,"onCreate")
    }


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_info, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setUpInfoTexts()
    }

    private fun setUpInfoTexts() {
        val description = view?.findViewById<TextView>(R.id.info_description_corpus)
        val horaires = view?.findViewById<TextView>(R.id.info_horaires_corpus)
        val adresse = view?.findViewById<TextView>(R.id.info_adresse_corpus)
        val transport = view?.findViewById<TextView>(R.id.info_transport_corpus)
        val contact = view?.findViewById<TextView>(R.id.info_contact_corpus)
        val tickets = view?.findViewById<TextView>(R.id.info_tickets_corpus)

        description?.text = Musee.info
        horaires?.text = Musee.horaires
        adresse?.text = Musee.adresse
        transport?.text = Musee.transport
        contact?.text = Musee.contact
        tickets?.text = Musee.ticket
    }

}
