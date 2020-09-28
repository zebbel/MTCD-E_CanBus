@file:Suppress("BlockingMethodInNonBlockingContext")

package de.zebbel.canbus

import android.app.Service
import android.content.Intent
import android.microntek.CarManager
import android.os.IBinder
import android.util.Log
import de.zebbel.canbus.handlers.CanbusHandler
import de.zebbel.canbus.handlers.SerialHandler
import de.zebbel.canbus.handlers.types.ChryslerBagooHandler


@ExperimentalUnsignedTypes
class CanBusServer : Service() {
    // init CarManager
    private var carManager: CarManager = CarManager()
    private var canbusType: Int = 0
    private var canbusHandler: CanbusHandler? = null
    private var serialHandler: SerialHandler? = null

    // on service started
    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        initSerial()
        serialHandler?.startSerial()

        //readMCUData()

        return START_STICKY
    }

    // on service bind
    override fun onBind(intent: Intent): IBinder? {
        return null
    }

    private fun initSerial(){
        canbusType = Integer.parseInt(carManager.getParameters("cfg_canbus="))

        when(canbusType){
            30 -> canbusHandler = ChryslerBagooHandler(applicationContext, carManager)
        }

        if(canbusHandler != null){
            serialHandler = SerialHandler(applicationContext, canbusHandler!!)
        }else{
            Log.e("CanBusServer", "canbus type not supported!")
        }
    }

    fun readMCUData(){
        val mcuVersion = carManager.getParameters("sta_mcu_version=")                   // mcu version
        Log.d("sta_mcu_version", mcuVersion.toString())
        val canbusType = Integer.parseInt(carManager.getParameters("cfg_canbus="))          //canbus type setting in factory settings
        Log.d("cfg_canbus", canbusType.toString())
        val driverPosition = Integer.parseInt(carManager.getParameters("cfg_rudder="))      //rudder setting in factory settings
        Log.d("cfg_rudder", driverPosition.toString())
        val tpms = Integer.parseInt(carManager.getParameters("cfg_tpms="))                  //tpms setting in factory settings
        Log.d("cfg_tpms", tpms.toString())
        val maxVolume = Integer.parseInt(carManager.getParameters("cfg_maxvolume="))        //max volume setting in factory settings
        Log.d("cfg_maxvolume", maxVolume.toString())
        val sta_mcu_o = carManager.getParameters("sta_mcu_o=")                            // ???
        Log.d("sta_mcu_o", sta_mcu_o.toString())
        val cfg_org = carManager.getParameters("cfg_org=")                                          //??? gets called when "sta_mcu_o=" is yes
        Log.d("cfg_org", cfg_org)
        val cfg_cansub_1 = carManager.getParameters("cfg_cansub_1=")                           //???
        Log.d("cfg_cansub_1", cfg_cansub_1)
        val cfg_cansub_3 = carManager.getParameters("cfg_cansub_3=")                                       //??? something about bluetooth phone number
        Log.d("cfg_cansub_3", cfg_cansub_3)
        val cfg_cansub_7 = carManager.getParameters("cfg_cansub_7=")                                       //??? something about rudder for air condition
        Log.d("cfg_cansub_7", cfg_cansub_7)
        val cfg_cansub_11 = carManager.getParameters("cfg_cansub_11=")                                     //??? reverse camera setting in factory settings
        Log.d("cfg_cansub_11", cfg_cansub_11)
        val cfg_cansub_6 = carManager.getParameters("cfg_cansub_6=")                                     //??? something about rudder setting
        Log.d("cfg_cansub_6", cfg_cansub_6)
        val cfg_cansub_8 = carManager.getParameters("cfg_cansub_8=")                                     //??? something about rudder setting
        Log.d("cfg_cansub_8", cfg_cansub_8)
    }



}