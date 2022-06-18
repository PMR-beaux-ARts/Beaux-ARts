package com.example.beaux_arts

import android.os.Bundle
import android.os.Handler
import android.util.Log
import android.view.View
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
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
import com.fengmap.android.map.marker.FMLineMarker
import com.fengmap.android.map.marker.FMSegment
import java.io.FileNotFoundException
import java.util.*


class MapActivity : AppCompatActivity(),
    OnFMMapInitListener,
    CompoundButton.OnCheckedChangeListener ,
    OnFMMapThemeListener,
    View.OnClickListener,
    OnFMMapClickListener{

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



    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_map)

        val mapView =  findViewById<FMMapView>(R.id.mapView)
        mMap = mapView.fmMap

        mMap?.setOnFMMapInitListener(this@MapActivity)
        mMap?.onFMMapThemeListener = this


        //加载离线数据
        val path = FileUtils.getDefaultMapPath(this)
        mMap?.openMapByPath(path)

        //加载在线地图数据，并自动更新地图数据
//        var bid = "10347"
//        mFMMap?.openMapById(bid,true)



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
}