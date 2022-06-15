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
import android.widget.Toast
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AlertDialog
import com.google.zxing.integration.android.IntentIntegrator

class ChartFragment : Fragment() {

    private val CAT: String = "chart page"

    private lateinit var buttonQR: ImageButton
    private lateinit var intentScanner: IntentIntegrator
    private lateinit var zxingActivityResultLauncher: ActivityResultLauncher<Intent>

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        Log.i(CAT,"onCreate")

    }

    override fun onStart() {
        super.onStart()
        Log.i(CAT,"onStart")
        buttonQR = view?.findViewById(R.id.QRCodeButton)!!
        buttonQR.setOnClickListener{
            Log.i(CAT,"setOnclickListener")
            intentScanner = IntentIntegrator(this.activity).apply {
                Log.i(CAT,"setBeepEnabled")
                setBeepEnabled(true)
                Log.i(CAT,"setOrientationLocked")
                setOrientationLocked(true)
                Log.i(CAT,"setDesiredBarcodeFormats")
                setDesiredBarcodeFormats(IntentIntegrator.QR_CODE)
                Log.i(CAT,"setCaptureActivity")
                setCaptureActivity(Capture::class.java)
                Log.i(CAT,"setTimeout")
                setTimeout(6000)
            }
            Log.i(CAT,"zxingActivityResultLauncher")
            zxingActivityResultLauncher = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) {
                Log.i(CAT,"IntentIntegrator.parseActivityResult")
                val intentResult = IntentIntegrator.parseActivityResult(it.resultCode, it.data)
                Log.i(CAT,"if (intentResult.contents != null)")
                if (intentResult.contents != null) {
                    Log.i(CAT,"val dialog = AlertDialog.Builder(this.requireContext())")
                    val dialog = AlertDialog.Builder(this.requireContext())
                    Log.i(CAT,"dialog.apply")
                    dialog.apply {
                        setTitle("Résultat du scan")
                        setMessage(intentResult.contents)
                        setPositiveButton("Ok", AlertDialogListener())
                        show()
                    }
                } else {
                    Toast.makeText(this.context, "Rien n'a été scanné !", Toast.LENGTH_SHORT).show()
                }
            }


        }
    }

    fun onClick(view: View?) {
        zxingActivityResultLauncher.launch(intentScanner.createScanIntent())
    }

    class AlertDialogListener : DialogInterface.OnClickListener {
        override fun onClick(dialogInterface: DialogInterface?, int: Int) {
            dialogInterface?.dismiss()
        }

    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_chart, container, false)
    }

}