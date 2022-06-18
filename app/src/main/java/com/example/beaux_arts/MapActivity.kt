package com.example.beaux_arts

import android.Manifest
import android.annotation.SuppressLint
import android.app.AlertDialog
import android.bluetooth.BluetoothAdapter
import android.bluetooth.BluetoothAdapter.LeScanCallback
import android.bluetooth.BluetoothManager
import android.content.Context
import android.content.pm.PackageManager

import android.os.Build
import android.os.Bundle
import android.os.Handler
import android.util.Log
import android.view.View
import android.widget.*
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import com.example.beaux_arts.utils.FileUtils
import com.example.beaux_arts.utils.ViewHelper
import com.fengmap.android.FMMapSDK
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
import com.fengmap.android.map.marker.FMLineMarker
import com.fengmap.android.map.marker.FMSegment
import com.google.android.material.floatingactionbutton.FloatingActionButton
import org.altbeacon.beacon.BeaconManager
import org.altbeacon.beacon.MonitorNotifier
import org.altbeacon.beacon.Region
import java.io.FileNotFoundException
import java.util.*
import androidx.lifecycle.Observer
import org.altbeacon.beacon.Beacon


class MapActivity : AppCompatActivity(),
    OnFMMapInitListener,
    CompoundButton.OnCheckedChangeListener ,
    OnFMMapThemeListener,
    View.OnClickListener,
    OnFMMapClickListener{

    lateinit var beaconListView: ListView
    lateinit var beaconCountTextView: TextView
    lateinit var monitoringButton: Button
    lateinit var rangingButton: Button
    lateinit var beaconReferenceApplication: MainApp
    var alertDialog: AlertDialog? = null
    var neverAskAgainPermissions = ArrayList<String>()

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
    private val CENTER_COORD = FMMapCoord(1.296164E7, 4861800.0)

    private val mButtons = arrayOfNulls<Button>(2)
    private var mPosition = 1


    protected var mLineLayer: FMLineLayer? = null

    /**
     * 导航分析
     */
    protected var mNaviAnalyser: FMNaviAnalyser? = null

    /**
     * 蓝牙适配器
     */
    protected var mBluetoothAdapter: BluetoothAdapter? = null

    /**
     * 是否正在进行扫描
     */
    protected var isScanning = false




    lateinit var region: Region

    /**
     * 定位按钮
     */
    var btnMyLocation: FloatingActionButton? = null

    /**
     * 起点坐标
     */
    protected var mSartCoord: FMMapCoord? = null

    /**
     * 起点楼层
     */
    protected var mStartGroupId = 0

    /**
     * 起点图层
     */
    protected var mStartImageLayer: FMImageLayer? = null

    /**
     * 终点坐标
     */
    protected var mEndCoord: FMMapCoord? = null

    /**
     * 终点楼层id
     */
    protected var mEndGroupId = 0

    /**
     * 终点图层
     */
    protected var mEndImageLayer: FMImageLayer? = null


    override fun onDestroy() {
        if (mMap != null) {
            mMap!!.onDestroy()
        }
        super.onDestroy()
    }
    val monitoringObserver = Observer<Int> { state ->
        var dialogTitle = "Beacons detected"
        var dialogMessage = "didEnterRegionEvent has fired"
        var stateString = "inside"
        if (state == MonitorNotifier.OUTSIDE) {
            dialogTitle = "No beacons detected"
            dialogMessage = "didExitRegionEvent has fired"
            stateString == "outside"
            beaconCountTextView.text = "Outside of the beacon region -- no beacons detected"
            beaconListView.adapter = ArrayAdapter(this, android.R.layout.simple_list_item_1, arrayOf("--"))
        }
        else {
            beaconCountTextView.text = "Inside the beacon region."
        }
        Log.d(TAG, "monitoring state changed to : $stateString")
        val builder =
            AlertDialog.Builder(this)
        builder.setTitle(dialogTitle)
        builder.setMessage(dialogMessage)
        builder.setPositiveButton(android.R.string.ok, null)
        alertDialog?.dismiss()
        alertDialog = builder.create()
        alertDialog?.show()
    }

    val rangingObserver = Observer<Collection<Beacon>> { beacons ->
        Log.d(TAG, "Ranged: ${beacons.count()} beacons")
        if (BeaconManager.getInstanceForApplication(this).rangedRegions.size > 0) {
            beaconCountTextView.text = "Ranging enabled: ${beacons.count()} beacon(s) detected"
            beaconListView.adapter = ArrayAdapter(this, android.R.layout.simple_list_item_1,
                beacons
                    .sortedBy { it.distance }
                    .map { "${it.id1}\nid2: ${it.id2} id3:  rssi: ${it.rssi}\nest. distance: ${it.distance} m" }.toTypedArray())
        }
    }

    fun rangingButtonTapped(view: View) {
        val beaconManager = BeaconManager.getInstanceForApplication(this)
        if (beaconManager.rangedRegions.size == 0) {
            beaconManager.startRangingBeacons(beaconReferenceApplication.region)
            rangingButton.text = "Stop Ranging"
            beaconCountTextView.text = "Ranging enabled -- awaiting first callback"
        }
        else {
            beaconManager.stopRangingBeacons(beaconReferenceApplication.region)
            rangingButton.text = "Start Ranging"
            beaconCountTextView.text = "Ranging disabled -- no beacons detected"
            beaconListView.adapter = ArrayAdapter(this, android.R.layout.simple_list_item_1, arrayOf("--"))
        }
    }

    fun monitoringButtonTapped(view: View) {
        var dialogTitle = ""
        var dialogMessage = ""
        val beaconManager = BeaconManager.getInstanceForApplication(this)
        if (beaconManager.monitoredRegions.size == 0) {
            beaconManager.startMonitoring(beaconReferenceApplication.region)
            dialogTitle = "Beacon monitoring started."
            dialogMessage = "You will see a dialog if a beacon is detected, and another if beacons then stop being detected."
            monitoringButton.text = "Stop Monitoring"

        }
        else {
            beaconManager.stopMonitoring(beaconReferenceApplication.region)
            dialogTitle = "Beacon monitoring stopped."
            dialogMessage = "You will no longer see dialogs when becaons start/stop being detected."
            monitoringButton.text = "Start Monitoring"
        }
        val builder =
            AlertDialog.Builder(this)
        builder.setTitle(dialogTitle)
        builder.setMessage(dialogMessage)
        builder.setPositiveButton(android.R.string.ok, null)
        alertDialog?.dismiss()
        alertDialog = builder.create()
        alertDialog?.show()

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


    @RequiresApi(Build.VERSION_CODES.S)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_map)


        beaconReferenceApplication = application as MainApp

        // Set up a Live Data observer for beacon data
        val regionViewModel = BeaconManager.getInstanceForApplication(this).getRegionViewModel(beaconReferenceApplication.region)
        // observer will be called each time the monitored regionState changes (inside vs. outside region)
        regionViewModel.regionState.observe(this, monitoringObserver)
        // observer will be called each time a new list of beacons is ranged (typically ~1 second in the foreground)
        regionViewModel.rangedBeacons.observe(this, rangingObserver)
        rangingButton = findViewById<Button>(R.id.rangingButton)
        monitoringButton = findViewById<Button>(R.id.monitoringButton)
        beaconListView = findViewById<ListView>(R.id.beaconList)
        beaconCountTextView = findViewById<TextView>(R.id.beaconCount)
        beaconCountTextView.text = "No beacons detected"
        beaconListView.adapter = ArrayAdapter(this, android.R.layout.simple_list_item_1, arrayOf("--"))
        val mapView =  findViewById<FMMapView>(R.id.mapView)
        mMap = mapView.fmMap

        mMap?.setOnFMMapInitListener(this@MapActivity)
        mMap?.onFMMapThemeListener = this


//        //加载离线数据
//        val path = FileUtils.getDefaultMapPath(this)
//        mMap?.openMapByPath(path)

        //加载在线地图数据，并自动更新地图数据
        var bid = "1537848990359105538"
        mMap?.openMapById(bid,true)



        val angle = 60f
        mMap!!.rotateAngle = angle //设置地图偏60度


        // 这个设置要放到主题回调里面
        val bt_change_language = findViewById<View>(R.id.bt_change_language) as Button
        bt_change_language.setOnClickListener { // 这个设置要放到主题回调里面
            Log.i(CAT,"btn clicked $themeLoadSuccess")
            if (themeLoadSuccess) {
                if (mMap?.mapLanguage == FMLableField.NAME) {
                    mMap?.mapLanguage = FMLableField.ENAME

                } else {
                    mMap?.mapLanguage = FMLableField.NAME

                }
            }
        }

        mMap!!.setOnFMMapClickListener(this)








        // 点击定位Button
        btnMyLocation = findViewById(R.id.btn_my_location) as FloatingActionButton
        btnMyLocation?.setOnClickListener(View.OnClickListener { // 默认无问题

            // 打开扫描器
            Log.i(CAT,"$isScanning here 232")


        })

    }








    override fun onMapInitSuccess(p0: String?) {

        //加载离线主题文件
       mMap?.loadThemeByPath(FileUtils.getDefaultThemePath(this));
        //加载在线主题文件
//        mFMMap?.loadThemeById("2001")

        //线图层
        mLineLayer = mMap!!.fmLayerProxy.fmLineLayer
        mMap!!.addLayer(mLineLayer)

        //导航分析

        //导航分析
        try {
            mNaviAnalyser = FMNaviAnalyser.getFMNaviAnalyserById(FileUtils.DEFAULT_MAP_ID)
        } catch (e: FileNotFoundException) {
            e.printStackTrace()
        } catch (e: FMObjectException) {
            e.printStackTrace()
        }

        val mapInfo = mMap!!.fmMapInfo
        val groups = mapInfo.groups
        displayGroupView(groups)



        //2D显示模式
//        mMap!!.setFMViewMode(FMViewMode.FMVIEW_MODE_2D)
        //缩放级别
        mMap!!.setZoomLevel(mLevel.toFloat(), false)

        //旋转角度
        mMap!!.rotateAngle = mRotate

        //倾角
        mMap!!.tiltAngle = mTilt

        //地图中心点
        mMap!!.mapCenter = CENTER_COORD
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


        //单、多层控制
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
     * 切换地图显示模式
     */
    private fun setViewMode() {
        if (mPosition == 0) {
            mMap!!.setFMViewMode(FMViewMode.FMVIEW_MODE_2D) //设置地图2D显示模式
        } else {
            mMap!!.setFMViewMode(FMViewMode.FMVIEW_MODE_3D) //设置地图3D显示模式
        }
    }

    /**
     * 单层显示
     */
    private fun singleDisplayFloor() {
        val groupId = mMap!!.focusGroupId
        singleDisplayFloor(groupId)
    }

    /**
     * 单层显示
     *
     * @param groupId 楼层id
     */
    private fun singleDisplayFloor(groupId: Int) {
        val showFloors = IntArray(1) // 需要显示的楼层
        showFloors[0] = groupId
        // 设置单层显示,及焦点层
        mMap!!.setMultiDisplay(showFloors, 0, null)
    }

    /**
     * 多层显示
     *
     * @param groupId 焦点层id
     */
    private fun multiDisplayFloor(groupId: Int) {
        val showFloors = mMap!!.mapGroupIds
        // 设置多层显示,及焦点层
        val focus = convertToFocus(groupId)
        mMap!!.setMultiDisplay(showFloors, focus, null)
    }

    /**
     * 多层显示
     */
    private fun multiDisplayFloor() {
        val focusGroupId = mMap!!.focusGroupId
        multiDisplayFloor(focusGroupId)
    }

    /**
     * 焦点层id转换成焦点层索引
     *
     * @param focusGroupId 焦点层id
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
     * 设置楼层是否可用
     *
     * @param enable true 可以被点击
     * false 不可被点击
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


        //TODO 获取到最新地图更新的信息，可以进行地图的下载操作
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
     * 设置2D、3D选择效果
     *
     * @param position 按钮索引
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
        // 获取屏幕点击位置的地图坐标
        val mapCoordResult = mMap!!.pickMapCoord(x, y) ?: return

        // 起点
        if (mSartCoord == null) {
            clear()
            mSartCoord = mapCoordResult.mapCoord
            mStartGroupId = mapCoordResult.groupId
            createStartImageMarker()
            return
        }

        // 终点
        if (mEndCoord == null) {
            mEndCoord = mapCoordResult.mapCoord
            mEndGroupId = mapCoordResult.groupId
            createEndImageMarker()
        }
        analyzeNavigation()

        // 画完置空
        mSartCoord = null
        mEndCoord = null
    }

    /**
     * 添加线标注
     */
    protected fun addLineMarker() {
        val results = mNaviAnalyser!!.naviResults
        // 填充导航数据
        val segments = ArrayList<FMSegment>()
        for (r in results) {
            val groupId = r.groupId
            val s = FMSegment(groupId, r.pointList)
            segments.add(s)
        }
        //添加LineMarker
        val lineMarker = FMLineMarker(segments)
        lineMarker.lineWidth = 3f
        mLineLayer!!.addMarker(lineMarker)
    }

    /**
     * 创建起点图标
     */
    protected fun createStartImageMarker() {
        clearStartImageLayer()
        // 添加起点图层
        mStartImageLayer = mMap!!.fmLayerProxy.getFMImageLayer(mStartGroupId)
        // 标注物样式
        val imageMarker =
            ViewHelper.buildImageMarker(resources, mSartCoord, R.drawable.ic_marker_start)
        mStartImageLayer?.addMarker(imageMarker)
    }

    /**
     * 创建终点图层
     */
    protected fun createEndImageMarker() {
        clearEndImageLayer()
        // 添加起点图层
        mEndImageLayer = mMap!!.fmLayerProxy.getFMImageLayer(mEndGroupId)
        // 标注物样式
        val imageMarker =
            ViewHelper.buildImageMarker(resources, mEndCoord, R.drawable.ic_marker_end)
        mEndImageLayer?.addMarker(imageMarker)
    }

    /**
     * 清理所有的线与图层
     */
    protected fun clear() {
        clearLineLayer()
        clearStartImageLayer()
        clearEndImageLayer()
    }

    /**
     * 清除线图层
     */
    protected fun clearLineLayer() {
        if (mLineLayer != null) {
            mLineLayer!!.removeAll()
        }
    }

    /**
     * 清除起点图层
     */
    protected fun clearStartImageLayer() {
        if (mStartImageLayer != null) {
            mStartImageLayer!!.removeAll()
            mMap!!.removeLayer(mStartImageLayer) // 移除图层
            mStartImageLayer = null
        }
    }

    /**
     * 清除终点图层
     */
    protected fun clearEndImageLayer() {
        if (mEndImageLayer != null) {
            mEndImageLayer!!.removeAll()
            mMap!!.removeLayer(mEndImageLayer) // 移除图层
            mEndImageLayer = null
        }
    }

    /**
     * 开始分析导航
     */
    private fun analyzeNavigation() {
        val type = mNaviAnalyser!!.analyzeNavi(
            mStartGroupId, mSartCoord, mEndGroupId, mEndCoord,
            FMNaviAnalyser.FMNaviModule.MODULE_SHORTEST
        )

        if (type == FMNaviAnalyser.FMRouteCalcuResult.ROUTE_SUCCESS) {
            addLineMarker()
        }
    }



    fun checkPermissions() {
        // basepermissions are for M and higher
        var permissions = arrayOf( Manifest.permission.ACCESS_FINE_LOCATION)
        var permissionRationale ="This app needs fine location permission to detect beacons.  Please grant this now."
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

    companion object {
        val TAG = "MainActivity"
        val PERMISSION_REQUEST_BACKGROUND_LOCATION = 0
        val PERMISSION_REQUEST_BLUETOOTH_SCAN = 1
        val PERMISSION_REQUEST_BLUETOOTH_CONNECT = 2
        val PERMISSION_REQUEST_FINE_LOCATION = 3
    }
}