package com.example.beaux_arts

import android.annotation.SuppressLint
import android.content.Intent
import android.database.sqlite.SQLiteDatabase
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.TextView
import android.widget.Toast
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.viewpager2.widget.ViewPager2
import com.example.beaux_arts.adapter.TabPageAdapter
import com.example.beaux_arts.donnees.ImportDB
import com.example.beaux_arts.donnees.Musee
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.google.android.material.tabs.TabLayout
import com.google.zxing.integration.android.IntentIntegrator
import kotlinx.android.synthetic.main.activity_main.*

class MainActivity : AppCompatActivity(), View.OnClickListener {

    lateinit var importDB: ImportDB

    private lateinit var buttonQR: FloatingActionButton
    private lateinit var intentScanner: IntentIntegrator
    private lateinit var zxingActivityResultLauncher: ActivityResultLauncher<Intent>

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        setUpTabBar()
        setUpQRCodeButton(timeout = 6000) // temps (en ms) pendant lequel on cherche le QR Code
        //Initialiser la database
        Init()

    }


    private fun setUpQRCodeButton(timeout : Long) {
        // On récupère le bouton QR Code
        buttonQR = findViewById(R.id.QRCode_floattingButton)
        buttonQR.setOnClickListener(this)

        // On prépare l'intent (qui se lancera quand on cliquera sur le bouton)
        intentScanner = IntentIntegrator(this).apply {
            setBeepEnabled(true)
            setOrientationLocked(true)
            setDesiredBarcodeFormats(IntentIntegrator.QR_CODE)
            captureActivity = Capture::class.java
            setTimeout(6000)
        }

        // On prépare le launcher / récupérateur d'activité (la caméra)
        zxingActivityResultLauncher = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) {
            val intentResult = IntentIntegrator.parseActivityResult(it.resultCode, it.data)
            if (intentResult.contents != null) {
                val qrCodeId: Int = try {intentResult.contents.toInt()} catch (e: NumberFormatException) {-1}
                openInfoFromId(id=qrCodeId)
            } else {
                Toast.makeText(this, "Rien n'a été scanné !", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun Init() {
        importDB = ImportDB(this)
        importDB.openDatabase()
    }

    private fun setUpTabBar(){
        val adapter = TabPageAdapter(this, tablayout.tabCount)
        viewPager.adapter = adapter

        viewPager.registerOnPageChangeCallback(object : ViewPager2.OnPageChangeCallback() {
            override fun onPageSelected(position: Int) {
                tablayout.selectTab(tablayout.getTabAt(position))
            }
        })
        tablayout.addOnTabSelectedListener(object : TabLayout.OnTabSelectedListener{
            override fun onTabSelected(tab: TabLayout.Tab) {
                viewPager.currentItem = tab.position
            }

            override fun onTabUnselected(tab: TabLayout.Tab?) {
            }

            override fun onTabReselected(tab: TabLayout.Tab?) {
            }

        })
    }

    override fun onClick(view: View?) {
        when (view?.id) {
            R.id.QRCode_floattingButton -> {
                launchCamera()
            }
            else -> {}
        }
    }

    private fun launchCamera() {
        zxingActivityResultLauncher.launch(intentScanner.createScanIntent())
    }


    @SuppressLint("Range")
    private fun openInfoFromId(id: Int) {
        val database = SQLiteDatabase.openOrCreateDatabase(ImportDB.DB_PATH+ "/" + ImportDB.DB_NAME, null)
        Log.i("test","[QR CODE] Open database.")
        var cursor = database.rawQuery("SELECT * FROM Collection WHERE id = $id",null)

        if (cursor.moveToFirst()) {
            Log.i("test","[QR CODE] Database with id $id not empty.")
            val activiteVisee = Intent(this, PieceActivity::class.java)
            activiteVisee.putExtra("title", cursor.getString(cursor.getColumnIndex("nom")))
            activiteVisee.putExtra("author", cursor.getString(cursor.getColumnIndex("auteur")))
            activiteVisee.putExtra("id", cursor.getInt(cursor.getColumnIndex("id")))
            activiteVisee.putExtra("description", cursor.getString(cursor.getColumnIndex("description")))
            Log.i("test","[QR CODE] Launch activity.")
            tablayout.selectTab(tablayout.getTabAt(1))  // aller à la page "Collection"
            startActivity(activiteVisee)
        } else {
            Log.i("test", "[QR CODE] No id $id exists in the current database")
            Toast.makeText(this, "No correspondance between QRCode and item", Toast.LENGTH_SHORT).show()
        }
    }

}