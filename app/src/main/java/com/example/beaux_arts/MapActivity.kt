package com.example.beaux_arts

import android.Manifest
import android.annotation.SuppressLint
import android.app.AlertDialog
import android.content.pm.PackageManager
import android.database.sqlite.SQLiteDatabase
import android.graphics.BitmapFactory
import android.graphics.drawable.BitmapDrawable
import android.os.Build
import android.os.Bundle
import android.os.Handler
import android.util.Log
import android.view.View
import android.widget.*
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Observer
import com.example.beaux_arts.donnees.ImportDB
import com.example.beaux_arts.donnees.MapCoord
import com.example.beaux_arts.donnees.Produit
import com.example.beaux_arts.utils.FileUtils
import com.example.beaux_arts.utils.ViewHelper
import com.fengmap.android.analysis.navi.FMNaviAnalyser
import com.fengmap.android.exception.FMObjectException
import com.fengmap.android.map.*
import com.fengmap.android.map.animator.FMLinearInterpolator
import com.fengmap.android.map.event.OnFMMapClickListener
import com.fengmap.android.map.event.OnFMMapInitListener
import com.fengmap.android.map.event.OnFMMapThemeListener
import com.fengmap.android.map.event.OnFMSwitchGroupListener
import com.fengmap.android.map.geometry.FMMapCoord
import com.fengmap.android.map.layer.FMImageLayer
import com.fengmap.android.map.layer.FMLineLayer
import com.fengmap.android.map.layer.FMLocationLayer
import com.fengmap.android.map.marker.FMLineMarker
import com.fengmap.android.map.marker.FMLocationMarker
import com.fengmap.android.map.marker.FMSegment
import com.fengmap.android.utils.FMLocateCoordTransformer
import com.google.android.material.floatingactionbutton.FloatingActionButton
import org.altbeacon.beacon.Beacon
import org.altbeacon.beacon.BeaconManager
import org.json.JSONObject
import java.io.FileNotFoundException
import java.util.*
import kotlin.collections.ArrayList


class MapActivity : AppCompatActivity(),
    OnFMMapInitListener,
    CompoundButton.OnCheckedChangeListener,
    OnFMMapThemeListener,
    View.OnClickListener,
    OnFMMapClickListener{



    lateinit var beaconReferenceApplication: MainApp

    var neverAskAgainPermissions = ArrayList<String>()

    //distance entre le telephone et trois beacons, par defaut la meme position que 55
    var distance_20 = 6.46
    var distance_26 = 15.9
    var distance_55 = 0.00

    //Coordonn??es de trois beacons
    //55 est au coin superieur gauche de la cafet et 20 au sup-droite, 26 inf-gauche
    var coor_b20_x = 6.46
    var coor_b20_y = 0.00
//    var coor_b20_z = 0.00
//    var coor_b26_x = 0.00
    var coor_b26_y = 15.9
//    var coor_b26_z = 0.00
//    var coor_b55_x = 0.00
//    var coor_b55_y = 0.00
//    var coor_b55_z = 0.00

    val CAT : String  = "mapActivity"
    var mMap: FMMap? = null
    var themeLoadSuccess = false

    private var mGroupControl: CheckBox? = null
    private lateinit var mRadioButtons: Array<RadioButton?>
    private val mHandler = Handler()

    private val mLevel = 20
    private val mRotate = 60f
    private val mTilt = 45f
    private val mGroupId = 1
    private var myPoint = FMMapCoord(349201.1972920726,6518687.134458933)


    private val mButtons = arrayOfNulls<Button>(2)

    private var mPosition = 1

    /**
     * ???????????????
     */
    protected var mLineLayer: FMLineLayer? = null



    /**
     * ????????????
     */
    protected var mNaviAnalyser: FMNaviAnalyser? = null

    /**
     * ????????????
     */
    var btnMyLocation: FloatingActionButton? = null

    /**
     * ????????????
     */
    protected var mStartCoord: FMMapCoord? = null

    /**
     * ?????????
     */
    private var mLocationLayer: FMLocationLayer? = null

    /**
     * ?????????
     */
    private var mLocationMarker: FMLocationMarker? = null

    /**
     * ????????????
     */
    protected var mStartGroupId = 0

    /**
     * ????????????
     */
    protected var mStartImageLayer: FMImageLayer? = null

    /**
     * ????????????
     */
    protected var mEndCoord: FMMapCoord? = null

    /**
     * ????????????id
     */
    protected var mEndGroupId = 0

    /**
     * ????????????
     */
    protected var mEndImageLayer: FMImageLayer? = null

    /**
     * ????????????????????????????????????
     */
    private var defaultVisitPoints : ArrayList<MapCoord> =   ArrayList()



    override fun onDestroy() {
        if (mMap != null) {
            mMap!!.onDestroy()
        }
        super.onDestroy()
    }


    private val rangingObserver = Observer<Collection<Beacon>> { beacons ->
//        Log.d(TAG, "Ranged: ${beacons.count()} beacons")
        if (BeaconManager.getInstanceForApplication(this).rangedRegions.isNotEmpty()) {
            for (item in beacons){
                if (item.id3.toString() == "26" ) {
                    Log.i(CAT,"distance 26 = ${item.distance}")
                    distance_26 = item.distance

                }
                if (item.id3.toString() == "55" ) {
                    Log.i(CAT,"distance 55 = ${item.distance}")
                    distance_55 = item.distance
                }
                if (item.id3.toString() == "20" ) {
                    Log.i(CAT,"distance 20 = ${item.distance}")
                    distance_20 = item.distance
                }
            }

//            beaconCountTextView.text = "Ranging enabled: ${beacons.count()} beacon(s) detected"
//            beaconListView.adapter = ArrayAdapter(this, android.R.layout.simple_list_item_1,
//                beacons
//                    .sortedBy { it.id3 }
//                    .map { "id3: ${it.id3}\ndistance: ${it.distance} m" }.toTypedArray())
        }
    }





    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        for (i in 1..permissions.size-1) {
            Log.d(TAG, "onRequestPermissionResult for "+permissions[i]+":" +grantResults[i])
            if (grantResults[i] != PackageManager.PERMISSION_GRANTED) {
                //check if user select "never ask again" when denying any permission
                if (!shouldShowRequestPermissionRationale(permissions[i])) {
                    neverAskAgainPermissions.add(permissions[i])
                }
            }
        }
    }

    override fun onResume() {
        super.onResume()
        checkPermissions()
    }


    @SuppressLint("Range")
    @RequiresApi(Build.VERSION_CODES.S)
    override fun onCreate(savedInstanceState: Bundle?) {
        //ouvrir database
        val database = SQLiteDatabase.openOrCreateDatabase(ImportDB.DB_PATH+ "/" + ImportDB.DB_NAME, null)
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_map)

        // Obtenir id de cette itineraires, si id=0, c'est une piece
        val itiId = intent.getIntExtra("id",0)
        if(itiId!=0) {
            val cursor1 = database.rawQuery("SELECT * FROM Itineraire WHERE id = ${itiId} ", null)
            if (cursor1 != null && cursor1.moveToFirst()) {
                val collctions_json =
                    JSONObject(cursor1.getString(cursor1.getColumnIndex("mescollections")))
                Log.i("test", "on obtient collections")
                val keys = collctions_json.keys()
                Log.i("test", "keys of json${keys}")
                while (keys.hasNext()) {
                    var key = keys.next().toString()
                    //chercher id dans le database
                    val collecId = collctions_json.getInt(key)
                    Log.i("test", "collectionId ${collecId}")
                    val cursor2 =
                        database.rawQuery("SELECT * FROM Collection WHERE id = ${collecId} ", null)
                    if (cursor2.moveToFirst()) {
                        do {
                            var etage: Int = 1 // par defaut au F0 premier etage
                            when (cursor2.getInt(cursor2.getColumnIndex("salle"))) {
                                in 1..9 -> etage = 1 //salle 0-9 dans F0
                                in 10..18 -> etage = 2 //salle 10-18 dans F1
                                else -> etage = 3 //salle 19-27 dans F2
                            }
                            val position_json =
                                JSONObject(cursor2.getString(cursor2.getColumnIndex("position")))
                            Log.i("test", "on obtient positions")
                            defaultVisitPoints.add(
                                MapCoord(
                                    etage,
                                    FMMapCoord(
                                        position_json.getDouble("x"),
                                        position_json.getDouble("y")
                                    )
                                )
                            )
                        } while (cursor2.moveToNext())
                    }
                    cursor2.close()
                }
            }
            cursor1.close()
        }
        else{
            //defaultVisitPoints.add(MapCoord(1,FMMapCoord(349202.78765743406,6518697.495674456)))
            //defaultVisitPoints.add(MapCoord(1,FMMapCoord(349196.10620590183,6518687.596318541)))
            //defaultVisitPoints.add(MapCoord(1,FMMapCoord(349204.8465219906,6518682.05897545)))
            var etage: Int
            val salle = intent.getIntExtra("salle",1)
            when (salle) {
                in 1..9 -> etage = 1 //salle 0-9 dans F0
                in 10..18 -> etage = 2 //salle 10-18 dans F1
                else -> etage = 3 //salle 19-27 dans F2
            }
//            Log.i("nav","$itiId")
             //updatemylocation()
              //defaultVisitPoints.add(MapCoord(1,FMMapCoord(349202.78765743406,6518697.495674456)))
            defaultVisitPoints.add(MapCoord(1,FMMapCoord(349201.1972920726,6518687.134458933)))
            defaultVisitPoints.set(0,MapCoord(1,myPoint))
//            var coord_x = intent.getDoubleExtra("position_x",0.00)
//            var coord_y = intent.getDoubleExtra("position_y",0.00)
//            Log.i("nav","$coord_x,$coord_y")
            defaultVisitPoints.add(
                MapCoord(
                    etage,
                    FMMapCoord(
                        intent.getDoubleExtra("position_x",0.00),intent.getDoubleExtra("position_y",0.00)
                    )
                )
            )
//            Log.i("nav","${defaultVisitPoints.size}")
//            Log.i("nav","${defaultVisitPoints.get(0)}")
        }


        database.close()

        beaconReferenceApplication = application as MainApp

        // Set up a Live Data observer for beacon data
        val regionViewModel = BeaconManager.getInstanceForApplication(this).getRegionViewModel(beaconReferenceApplication.region)
        // observer will be called each time a new list of beacons is ranged (typically ~1 second in the foreground)
        regionViewModel.rangedBeacons.observe(this, rangingObserver)
//        beaconListView = findViewById<ListView>(R.id.beaconList)
//        beaconCountTextView = findViewById<TextView>(R.id.beaconCount)
//        beaconCountTextView.text = "No beacons detected"
//        beaconListView.adapter = ArrayAdapter(this, android.R.layout.simple_list_item_1, arrayOf("--"))
        val mapView =  findViewById<FMMapView>(R.id.mapView)
        mMap = mapView.fmMap

        mMap?.setOnFMMapInitListener(this@MapActivity)
        mMap?.onFMMapThemeListener = this


//        //??????????????????
//        val path = FileUtils.getDefaultMapPath(this)
//        mMap?.openMapByPath(path)

//        //??????????????????????????????????????????????????????
        var bid = "1537849394373218305" // centrale E
//        var bid = "10347"
        mMap?.openMapById(bid,true)



        val angle = 60f
        mMap!!.rotateAngle = angle //???????????????60???


////        ????????????
//        // ???????????????????????????????????????
//        val bt_change_language = findViewById<View>(R.id.bt_change_language) as Button
//        bt_change_language.setOnClickListener { // ???????????????????????????????????????
//            Log.i(CAT,"btn clicked $themeLoadSuccess")
//            if (themeLoadSuccess) {
//                if (mMap?.mapLanguage == FMLableField.NAME) {
//                    mMap?.mapLanguage = FMLableField.ENAME
//
//                } else {
//                    mMap?.mapLanguage = FMLableField.NAME
//
//                }
//            }
//        }

        mMap!!.setOnFMMapClickListener(this)



        // ????????????Button
        btnMyLocation = findViewById<FloatingActionButton>(R.id.btn_my_location)
        btnMyLocation?.setOnClickListener(View.OnClickListener { // ???????????????
            updatemylocation()
            updateLocationMarker()
//            val visteur_co = calculPositionActu(distance_20,distance_26,distance_55)
//            val x = visteur_co[0]
//            val y = visteur_co[1]
//            val z = visteur_co[2]
//            myPoint = transformtoFMMap(x,y)
            Toast.makeText(applicationContext,
                "Myposition_x:${myPoint.x} m\n" +
                        "Myposition_y:${myPoint.y} m\n" +
                        "distance20 = ${distance_20} m\n" +
                        "distance55 = ${distance_55} m\n"+
                        "distance26 = ${distance_26} m",Toast.LENGTH_SHORT).show()
        })

    }
    private fun updatemylocation(){
// Calculer les positions suite ?? la distance obtenue par trois beacons
        var visiteur_x = (coor_b20_x*coor_b20_x+distance_55*distance_55-distance_20*distance_20)/2/coor_b20_x
        var visiteur_y = (coor_b26_y*coor_b26_y+distance_55*distance_55-distance_26*distance_26)/2/coor_b26_y
        var visiteur_z = Math.sqrt(distance_55*distance_55-visiteur_y*visiteur_y-visiteur_x*visiteur_x)

        myPoint = transformtoFMMap(visiteur_x,visiteur_y)
        //myPoint = FMMapCoord(visiteur_x,visiteur_y)
        //defaultVisitPoints.set(0,MapCoord(1,myPoint))
    }

    private fun transformtoFMMap(x:Double,y:Double):FMMapCoord{
        val transformer = FMLocateCoordTransformer()
        // beacon 50,20,26 sur Fengmap
        transformer.setFengmapCoordinate(FMMapCoord(349201.1939747968,6518687.166971912), FMMapCoord(349207.3655576721,6518685.264394011), FMMapCoord(349196.4963470237,6518672.06893436));
        // beacon 50,20,26 dans le syst??me coordon??es
        transformer.setLocateCoordinate(0.0, 0.0,6.46,15.9);
        val coord = transformer.transform(x, y)
        return coord
    }



    private fun updateLocationMarker() {
        if (mLocationMarker == null) {
            val groupId = mMap!!.focusGroupId
            mLocationMarker = FMLocationMarker(groupId, myPoint)
            //?????????????????????
            mLocationMarker?.setActiveImageFromAssets("active.png")
            Log.i(CAT,"268 here")
            //????????????????????????
            mLocationMarker?.markerWidth = 90
            mLocationMarker?.markerHeight = 90
            mLocationLayer?.addMarker(mLocationMarker)
        } else {
            //??????????????????????????????
            val groupId = mMap!!.focusGroupId
            mLocationMarker = FMLocationMarker(groupId, myPoint)
            Log.i(CAT,"275 here")
            val angle = 0f
            mLocationMarker?.updateAngleAndPosition(angle, myPoint)
        }
    }


    override fun onMapInitSuccess(p0: String?) {

        //????????????????????????
        //mMap?.loadThemeByPath(FileUtils.getDefaultThemePath(this));
        //????????????????????????
        mMap?.loadThemeById("1539001530494816258")

        //?????????
        this.mLineLayer = mMap!!.fmLayerProxy.fmLineLayer
        mMap!!.addLayer(this.mLineLayer)



//        defaultVisitPoints.add(MapCoord(1,FMMapCoord(349202.78765743406,6518697.495674456)))
//        defaultVisitPoints.add(MapCoord(1,FMMapCoord(349196.10620590183,6518687.596318541)))
//        defaultVisitPoints.add(MapCoord(1,FMMapCoord(349204.8465219906,6518682.05897545)))
//        defaultVisitPoints.add(MapCoord(3,FMMapCoord(349196.10620590183,6518687.596318541)))
//        defaultVisitPoints.add(MapCoord(3,FMMapCoord(349201.86748218216,6518674.551672029)))


        //??????????????????
        mLocationLayer = mMap!!.fmLayerProxy.fmLocationLayer
        mMap!!.addLayer(mLocationLayer)


        //????????????????????????????????????id?????????????????????1000???
        try {
//            mNaviAnalyser = FMNaviAnalyser.getFMNaviAnalyserById(FileUtils.DEFAULT_MAP_ID)
            var bid = "1537849394373218305" // centrale E
//            var bid = "10347"
            mNaviAnalyser = FMNaviAnalyser.getFMNaviAnalyserById(bid)

        } catch (e: FileNotFoundException) {
            e.printStackTrace()
        } catch (e: FMObjectException) {
            e.printStackTrace()
        }

        val mapInfo = mMap!!.fmMapInfo
        val groups = mapInfo.groups
        displayGroupView(groups)



        //2D????????????
//        mMap!!.setFMViewMode(FMViewMode.FMVIEW_MODE_2D)

        //????????????
        mMap!!.setZoomLevel(mLevel.toFloat(), false)

        //????????????
        mMap!!.rotateAngle = mRotate

        //??????
        mMap!!.tiltAngle = mTilt

        //???????????????
        mMap!!.mapCenter = myPoint

        multiDisplayFloor()



        visitroad(defaultVisitPoints)


    }

    private fun displayGroupView(groups: ArrayList<FMGroupInfo>) {
        val radioGroup: RadioGroup = ViewHelper.getView(this@MapActivity, R.id.rg_groups)
        val count = groups.size
        mRadioButtons = arrayOfNulls(count)
        for (index in 0 until count) {
            val position = radioGroup.childCount - index - 1
            mRadioButtons[index]= radioGroup.getChildAt(position) as RadioButton
            val groupInfo = groups[index]
            mRadioButtons[index]?.tag = groupInfo.groupId
            mRadioButtons[index]?.text = groupInfo.groupName.uppercase(Locale.getDefault())
            mRadioButtons[index]?.setOnCheckedChangeListener(this)
        }
        mRadioButtons[count-1]?.isChecked = true


        //??????????????????
        mGroupControl = ViewHelper.getView(this@MapActivity, R.id.cb_groups)
        mGroupControl?.setOnCheckedChangeListener(CompoundButton.OnCheckedChangeListener { buttonView, isChecked ->
            if (isChecked) {
                multiDisplayFloor()
            } else {
                singleDisplayFloor()
            }
        })


        setViewMode()

        val view: LinearLayout = ViewHelper.getView(this@MapActivity, R.id.layout_mode)
        for (i in 0 until view.childCount) {
            mButtons[i] = view.getChildAt(i) as Button
            mButtons[i]!!.tag = i
            mButtons[i]!!.isEnabled = true
            mButtons[i]!!.setOnClickListener(this)
        }
        mButtons[mPosition]!!.isEnabled = false
    }

    /**
     * ????????????????????????
     */
    private fun setViewMode() {
        if (mPosition == 0) {
            mMap!!.setFMViewMode(FMViewMode.FMVIEW_MODE_2D) //????????????2D????????????
        } else {
            mMap!!.setFMViewMode(FMViewMode.FMVIEW_MODE_3D) //????????????3D????????????
        }
    }

    /**
     * ????????????
     */
    private fun singleDisplayFloor() {
        val groupId = mMap!!.focusGroupId
        singleDisplayFloor(groupId)
    }

    /**
     * ????????????
     *
     * @param groupId ??????id
     */
    private fun singleDisplayFloor(groupId: Int) {
        val showFloors = IntArray(1) // ?????????????????????
        showFloors[0] = groupId
        // ??????????????????,????????????
        mMap!!.setMultiDisplay(showFloors, 0, null)
    }

    /**
     * ????????????
     *
     * @param groupId ?????????id
     */
    private fun multiDisplayFloor(groupId: Int) {
        val showFloors = mMap!!.mapGroupIds
        // ??????????????????,????????????
        val focus = convertToFocus(groupId)
        mMap!!.setMultiDisplay(showFloors, focus, null)
    }

    /**
     * ????????????
     */
    private fun multiDisplayFloor() {
        val focusGroupId = mMap!!.focusGroupId
        multiDisplayFloor(focusGroupId)
    }

    /**
     * ?????????id????????????????????????
     *
     * @param focusGroupId ?????????id
     * @return
     */
    private fun convertToFocus(focusGroupId: Int): Int {
        val mapInfo = mMap!!.fmMapInfo
        val size = mapInfo.groups.size
        var focus = 0
        for (i in 0 until size) {
            val groupId = mapInfo.groups[i].groupId
            if (focusGroupId == groupId) {
                focus = i
                break
            }
        }
        return focus
    }

    override fun onCheckedChanged(buttonView: CompoundButton, isChecked: Boolean) {
        if (isChecked) {
            val groupId = buttonView.tag as Int
            mMap!!.setFocusByGroupIdAnimated(
                groupId,
                FMLinearInterpolator(),
                object : OnFMSwitchGroupListener {
                    override fun beforeGroupChanged() {
                        setRadioButtonEnable(false)
                    }

                    override fun afterGroupChanged() {
                        setRadioButtonEnable(true)
                    }
                })
        }
    }

    /**
     * ????????????????????????
     *
     * @param enable true ???????????????
     * false ???????????????
     */
    private fun setRadioButtonEnable(enable: Boolean) {
        mHandler.post {
            for (i in mRadioButtons.indices) {
                mRadioButtons[i]!!.isEnabled = enable
            }
        }
    }
    override fun onMapInitFailure(p0: String?, p1: Int) {

        Log.i(CAT,"onMapInitFailure")
    }

    override fun onUpgrade(p0: FMMapUpgradeInfo?): Boolean {


        //TODO ????????????????????????????????????????????????????????????????????????
        return false
    }




    override fun onSuccess(p0: String?) {
        themeLoadSuccess = true
        Log.i(CAT,"on themeLoadSuccess")

    }

    override fun onFailure(p0: String?, p1: Int) {
        TODO("Not yet implemented")
    }

    override fun onClick(v: View) {
        val button = v as Button
        val position = button.tag as Int
        setPosition(position)
        setViewMode()
    }

    /**
     * ??????2D???3D????????????
     *
     * @param position ????????????
     */
    private fun setPosition(position: Int) {
        if (mPosition == position) {
            return
        }
        mButtons[position]!!.isEnabled = false
        mButtons[mPosition]!!.isEnabled = true
        mPosition = position
    }

    override fun onMapClick(x: Float, y: Float) {
        // ???????????????????????????????????????
        val mapCoordResult = mMap!!.pickMapCoord(x, y) ?: return

        // ??????
        if (mStartCoord == null) {
            clear()
            mStartCoord = mapCoordResult.mapCoord    //????????????
            mStartGroupId = mapCoordResult.groupId  //????????????
            createStartImageMarker()
            return
        }

        // ??????
        if (mEndCoord == null) {
            mEndCoord = mapCoordResult.mapCoord
            mEndGroupId = mapCoordResult.groupId
            createEndImageMarker()
        }
        analyzeNavigation()

        // ????????????
        mStartCoord = null
        mEndCoord = null
    }

    private var lineMarker : FMLineMarker? = null
    /**
     * ???????????????
     */
    protected fun addLineMarker() {
        val results = mNaviAnalyser!!.naviResults
        // ??????????????????
        val segments = ArrayList<FMSegment>()
        for (r in results) {
            val groupId = r.groupId
            val s = FMSegment(groupId, r.pointList)
            segments.add(s)
        }
        //??????LineMarker
        lineMarker = FMLineMarker(segments)
        lineMarker?.lineWidth = 1.5f
        this.mLineLayer!!.addMarker(lineMarker)
    }

    private var visitLineMarker : FMLineMarker? = null


    /**
     * ???????????????
     */
    protected fun addLineMarker2() {
        val results = mNaviAnalyser!!.naviResults
        // ??????????????????
        val segments = ArrayList<FMSegment>()
        for (r in results) {
            val groupId = r.groupId
            val s = FMSegment(groupId, r.pointList)
            segments.add(s)
        }
        //??????LineMarker
        visitLineMarker = FMLineMarker(segments)
        visitLineMarker?.lineWidth = 1.5f
        this.mLineLayer!!.addMarker(visitLineMarker)

    }






    /**
     * ??????????????????
     */
    protected fun createStartImageMarker() {
        clearStartImageLayer()
        // ??????????????????
        mStartImageLayer = mMap!!.fmLayerProxy.getFMImageLayer(mStartGroupId)
        // ???????????????
        val imageMarker =
            ViewHelper.buildImageMarker(resources, mStartCoord, R.drawable.ic_marker_start)
        mStartImageLayer?.addMarker(imageMarker)
    }

    /**
     * ??????????????????
     */
    protected fun createEndImageMarker() {
        clearEndImageLayer()
        // ??????????????????
        mEndImageLayer = mMap!!.fmLayerProxy.getFMImageLayer(mEndGroupId)
        // ???????????????
        val imageMarker =
            ViewHelper.buildImageMarker(resources, mEndCoord, R.drawable.ic_marker_end)
        mEndImageLayer?.addMarker(imageMarker)
    }

    /**
     * ???????????????????????????
     */
    protected fun clear() {
        clearLineLayer()
        clearStartImageLayer()
        clearEndImageLayer()
    }

    /**
     * ???????????????
     */
    protected fun clearLineLayer() {
        if (this.mLineLayer != null) {
//            mLineLayer!!.removeAll()
            this.mLineLayer!!.removeMarker(lineMarker)
        }
    }

    /**
     * ??????????????????
     */
    protected fun clearStartImageLayer() {
        if (mStartImageLayer != null) {
            mStartImageLayer!!.removeAll()
            mMap!!.removeLayer(mStartImageLayer) // ????????????
            mStartImageLayer = null
        }
    }

    /**
     * ??????????????????
     */
    protected fun clearEndImageLayer() {
        if (mEndImageLayer != null) {
            mEndImageLayer!!.removeAll()
            mMap!!.removeLayer(mEndImageLayer) // ????????????
            mEndImageLayer = null
        }
    }

    /**
     * ??????????????????
     */
    private fun analyzeNavigation() {
        val type = mNaviAnalyser!!.analyzeNavi(
            mStartGroupId, mStartCoord, mEndGroupId, mEndCoord,
            FMNaviAnalyser.FMNaviModule.MODULE_SHORTEST
        )

        if (type == FMNaviAnalyser.FMRouteCalcuResult.ROUTE_SUCCESS) {
            addLineMarker()
        }
    }

    /**
     * ???????????????????????????????????????
     */
    private fun analyzeNavigation2(StartGroupId:Int, StartCoord:FMMapCoord, EndGroupId:Int, EndCoord:FMMapCoord) {
        val type = mNaviAnalyser!!.analyzeNavi(
            StartGroupId, StartCoord, EndGroupId, EndCoord,
            FMNaviAnalyser.FMNaviModule.MODULE_SHORTEST
        )

        if (type == FMNaviAnalyser.FMRouteCalcuResult.ROUTE_SUCCESS) {
            addLineMarker2()
        }
    }

    private fun visitroad(defaultVisitPoints : ArrayList<MapCoord>){
        for ( i in 0 until defaultVisitPoints.size-1){
            analyzeNavigation2(defaultVisitPoints[i].groupId,defaultVisitPoints[i].mapCoord,defaultVisitPoints[i+1].groupId,defaultVisitPoints[i+1].mapCoord)
        }
    }



    fun checkPermissions() {
        // basepermissions are for M and higher
        var permissions = arrayOf( Manifest.permission.ACCESS_FINE_LOCATION)
        var permissionRationale ="This app needs fine btn_my_location permission to detect beacons.  Please grant this now."
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            permissions = arrayOf( Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.BLUETOOTH_SCAN)
            permissionRationale ="This app needs fine location permission, and bluetooth scan permission to detect beacons.  Please grant all of these now."
        }
        else if (android.os.Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
            if ((checkSelfPermission(Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED)) {
                permissions = arrayOf( Manifest.permission.ACCESS_FINE_LOCATION)
                permissionRationale ="This app needs fine location permission to detect beacons.  Please grant this now."
            }
            else {
                permissions = arrayOf( Manifest.permission.ACCESS_BACKGROUND_LOCATION)
                permissionRationale ="This app needs background location permission to detect beacons in the background.  Please grant this now."
            }
        }
        else if (android.os.Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            permissions = arrayOf( Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_BACKGROUND_LOCATION)
            permissionRationale ="This app needs both fine location permission and background location permission to detect beacons in the background.  Please grant both now."
        }
        var allGranted = true
        for (permission in permissions) {
            if (checkSelfPermission(permission) != PackageManager.PERMISSION_GRANTED) allGranted = false;
        }
        if (!allGranted) {
            if (neverAskAgainPermissions.count() == 0) {
                val builder =
                    AlertDialog.Builder(this)
                builder.setTitle("This app needs permissions to detect beacons")
                builder.setMessage(permissionRationale)
                builder.setPositiveButton(android.R.string.ok, null)
                builder.setOnDismissListener {
                    requestPermissions(
                        permissions,
                        PERMISSION_REQUEST_FINE_LOCATION
                    )
                }
                builder.show()
            }
            else {
                val builder =
                    AlertDialog.Builder(this)
                builder.setTitle("Functionality limited")
                builder.setMessage("Since location and device permissions have not been granted, this app will not be able to discover beacons.  Please go to Settings -> Applications -> Permissions and grant location and device discovery permissions to this app.")
                builder.setPositiveButton(android.R.string.ok, null)
                builder.setOnDismissListener { }
                builder.show()
            }
        }
        else {
            if (android.os.Build.VERSION.SDK_INT > Build.VERSION_CODES.P) {
                if (checkSelfPermission(Manifest.permission.ACCESS_BACKGROUND_LOCATION)
                    != PackageManager.PERMISSION_GRANTED
                ) {
                    if (shouldShowRequestPermissionRationale(Manifest.permission.ACCESS_BACKGROUND_LOCATION)) {
                        val builder =
                            AlertDialog.Builder(this)
                        builder.setTitle("This app needs background location access")
                        builder.setMessage("Please grant location access so this app can detect beacons in the background.")
                        builder.setPositiveButton(android.R.string.ok, null)
                        builder.setOnDismissListener {
                            requestPermissions(
                                arrayOf(Manifest.permission.ACCESS_BACKGROUND_LOCATION),
                                PERMISSION_REQUEST_BACKGROUND_LOCATION
                            )
                        }
                        builder.show()
                    } else {
                        val builder =
                            AlertDialog.Builder(this)
                        builder.setTitle("Functionality limited")
                        builder.setMessage("Since background location access has not been granted, this app will not be able to discover beacons in the background.  Please go to Settings -> Applications -> Permissions and grant background location access to this app.")
                        builder.setPositiveButton(android.R.string.ok, null)
                        builder.setOnDismissListener { }
                        builder.show()
                    }
                }
            }
            else if (android.os.Build.VERSION.SDK_INT > Build.VERSION_CODES.S &&
                (checkSelfPermission(Manifest.permission.BLUETOOTH_SCAN)
                        != PackageManager.PERMISSION_GRANTED)) {
                if (shouldShowRequestPermissionRationale(Manifest.permission.BLUETOOTH_SCAN)) {
                    val builder =
                        AlertDialog.Builder(this)
                    builder.setTitle("This app needs bluetooth scan permission")
                    builder.setMessage("Please grant scan permission so this app can detect beacons.")
                    builder.setPositiveButton(android.R.string.ok, null)
                    builder.setOnDismissListener {
                        requestPermissions(
                            arrayOf(Manifest.permission.BLUETOOTH_SCAN),
                            PERMISSION_REQUEST_BLUETOOTH_SCAN
                        )
                    }
                    builder.show()
                } else {
                    val builder =
                        AlertDialog.Builder(this)
                    builder.setTitle("Functionality limited")
                    builder.setMessage("Since bluetooth scan permission has not been granted, this app will not be able to discover beacons  Please go to Settings -> Applications -> Permissions and grant bluetooth scan permission to this app.")
                    builder.setPositiveButton(android.R.string.ok, null)
                    builder.setOnDismissListener { }
                    builder.show()
                }
            }
            else {
                if (android.os.Build.VERSION.SDK_INT > Build.VERSION_CODES.P) {
                    if (checkSelfPermission(Manifest.permission.ACCESS_BACKGROUND_LOCATION)
                        != PackageManager.PERMISSION_GRANTED
                    ) {
                        if (shouldShowRequestPermissionRationale(Manifest.permission.ACCESS_BACKGROUND_LOCATION)) {
                            val builder =
                                AlertDialog.Builder(this)
                            builder.setTitle("This app needs background location access")
                            builder.setMessage("Please grant location access so this app can detect beacons in the background.")
                            builder.setPositiveButton(android.R.string.ok, null)
                            builder.setOnDismissListener {
                                requestPermissions(
                                    arrayOf(Manifest.permission.ACCESS_BACKGROUND_LOCATION),
                                    PERMISSION_REQUEST_BACKGROUND_LOCATION
                                )
                            }
                            builder.show()
                        } else {
                            val builder =
                                AlertDialog.Builder(this)
                            builder.setTitle("Functionality limited")
                            builder.setMessage("Since background location access has not been granted, this app will not be able to discover beacons in the background.  Please go to Settings -> Applications -> Permissions and grant background location access to this app.")
                            builder.setPositiveButton(android.R.string.ok, null)
                            builder.setOnDismissListener { }
                            builder.show()
                        }
                    }
                }
            }
        }

    }


    private fun calculPositionActu(distance_20:Double,distance_26:Double,distance_55:Double) : List<Double>{
        var visiteur_x = (coor_b20_x*coor_b20_x+distance_55*distance_55-distance_20*distance_20)/2/coor_b20_x
        var visiteur_y = (coor_b26_y*coor_b26_y+distance_55*distance_55-distance_26*distance_26)/2/coor_b26_y
        var visiteur_z = Math.sqrt(distance_55*distance_55-visiteur_y*visiteur_y-visiteur_x*visiteur_x)
        val visteur_co = listOf<Double>(visiteur_x,visiteur_y,visiteur_z)
        return visteur_co
    }

    companion object {
        val TAG = "MainActivity"
        val PERMISSION_REQUEST_BACKGROUND_LOCATION = 0
        val PERMISSION_REQUEST_BLUETOOTH_SCAN = 1
        val PERMISSION_REQUEST_BLUETOOTH_CONNECT = 2
        val PERMISSION_REQUEST_FINE_LOCATION = 3
    }
}