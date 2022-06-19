package com.example.beaux_arts

import android.app.Application
import com.fengmap.android.FMMapSDK
import org.altbeacon.beacon.*


class MainApp : Application() {



    lateinit var region: Region
    override fun onCreate() {
        super.onCreate()
        //初始化SDK
        FMMapSDK.init(this)
        // 自定义缓存目录，需要申请读写权限
        // FMMapSDK.init(this,path);


        val beaconManager = BeaconManager.getInstanceForApplication(this)
        BeaconManager.setDebug(true)
        beaconManager.beaconParsers.clear()

        beaconManager.beaconParsers.add(
            BeaconParser().
            setBeaconLayout("m:2-3=0215,i:4-19,i:20-21,i:22-23,p:24-24"))

        region = Region("all-beacons", null, null, null)
        beaconManager.startRangingBeacons(region)

    }

}