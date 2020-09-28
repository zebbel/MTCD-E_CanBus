package de.zebbel.canbus.handlers

import android.content.Context
import android.content.Intent
import android.microntek.CarManager
import android.util.Log
import androidx.core.content.ContextCompat
import de.zebbel.canbus.activitys.DoorActivity

@ExperimentalUnsignedTypes
open class CanbusHandler(private val applicationContext: Context, private val carManager: CarManager) {
    open fun processMessage(message: Array<Int>){
    }

    fun sendBroadcast(name: String, value: String){
        val intent = Intent("de.zebbel.canbus")
        intent.putExtra(name, value)
        applicationContext.sendBroadcast(intent)
    }

    // show doors activity
    fun showDoorsActivity(trunk: Boolean, frontLeft: Boolean, rearLeft: Boolean, frontRight: Boolean, rearRight: Boolean){
        val doors = booleanArrayOf(trunk, frontLeft, rearLeft, frontRight, rearRight)

        val intent = Intent(applicationContext, DoorActivity::class.java)
        intent.putExtra("doors", doors)
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
        intent.addFlags(Intent.FLAG_ACTIVITY_EXCLUDE_FROM_RECENTS)
        ContextCompat.startActivity(applicationContext, intent, null)

        val intent2 = Intent("de.zebbel.canbus.doors")
        intent2.putExtra("doors", doors)
        applicationContext.sendBroadcast(intent2)
    }

    // send config to canBox
    fun sendBoxConfig(intent: Intent){
        var str = "canbus_rsp=46,0,6,1,"

        var checksum = 6 + 1

        for(element in intent.extras?.keySet()!!){
            if(element != "boxConfig") checksum += intent.getIntExtra(element,0)
        }

        str += intent.getIntExtra("startCondition",0).toString() + ","
        str += intent.getIntExtra("stopCondition",0).toString() + ","
        str += intent.getIntExtra("rearCamera",0).toString() + ","
        str += intent.getIntExtra("resendVolume",0).toString() + ","
        str += intent.getIntExtra("resendVolumeTime",0).toString() + ","
        str += (checksum.toUByte()).inv()

        Log.d("send canBox settings", str)
        carManager.setParameters(str)
    }
}