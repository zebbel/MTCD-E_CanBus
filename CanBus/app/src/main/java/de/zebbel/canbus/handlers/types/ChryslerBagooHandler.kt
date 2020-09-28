package de.zebbel.canbus.handlers.types

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.microntek.CarManager
import android.util.Log
import de.zebbel.canbus.handlers.CanbusHandler

@ExperimentalUnsignedTypes
class ChryslerBagooHandler(private val applicationContext: Context, private val carManager: CarManager) : CanbusHandler(applicationContext, carManager) {

    // init canBus Handler
    init {
        // init broadcast receiver for canBox messages
        val configReceiver: BroadcastReceiver? = object : BroadcastReceiver() {
            override fun onReceive(context: Context, intent: Intent) { checkBroadcast(intent) }
        }

        // register broadcast receiver for canBus config messages
        applicationContext.registerReceiver(configReceiver, IntentFilter("de.zebbel.canbus.config"))
    }

    // check what canBus config broadcast message we got
    fun checkBroadcast(intent: Intent){
        if(intent.hasExtra("boxConfig")){
            when(intent.getStringExtra("boxConfig")){
                // request conBox config from canBox
                "getBoxConfig" -> carManager.setParameters("canbus_rsp=46,0,1,0,254")
                // send boxConfig to canBox
                "setBoxConfig" -> sendBoxConfig(intent)
                // request amplifier config from canBox
                "getAmpConfig" -> carManager.setParameters("canbus_rsp=46,0,1,2,252")
                // save config to canBox eeprom
                "saveConfig" -> carManager.setParameters("canbus_rsp=46,0,1,4,250")
                // reset settings to default values
                "resetConfig" -> carManager.setParameters("canbus_rsp=46,0,1,5,249")
            }
        }
    }


    // check id what message is to process
    override fun processMessage(message: Array<Int>){
        when(message[0]){
            0x00 -> processBoxConfig(message)
            0x02 -> processRpm(message)
            0x03 -> processSpeed(message)
            0x05 -> processFuel(message)
            0x06 -> processDoors(message)
            0x07 -> processCoolant(message)
            0x08 -> processOilTemp(message)
            0x09 -> processOdometer(message)
            0x0A -> processAmbientTemp(message)
            else -> Log.d("canId", message[0].toString())
        }
    }

    // rpm canBus message received
    private fun processRpm(message: Array<Int>){
        val rpm = (message[1] shl 8) + (message[2].toUByte()).toInt()
        sendBroadcast("rpm", rpm.toString())
    }

    // speed canBus message received
    private fun processSpeed(message: Array<Int>){
        val speed = ((message[1] shl 8) + (message[2].toUByte()).toInt()) / 125
        sendBroadcast("speed", speed.toString())
    }

    // fuel tank canBus message received
    private fun processFuel(message: Array<Int>){
        val fuel = (message[1] / 10) * 3.78541
        sendBroadcast("fuel", fuel.toString())
    }

    // door contact canBus message received
    private fun processDoors(message: Array<Int>){
        showDoorsActivity((message[1] and (1 shl(3))) > 0,
            (message[1] and (1 shl(7))) > 0,
            (message[1] and (1 shl(4))) > 0,
            (message[1] and (1 shl(6))) > 0,
            (message[1] and (1 shl(5))) > 0)
    }

    // coolant temperature canBus message received
    private fun processCoolant(message: Array<Int>){
        val coolant = message[1] - 40
        sendBroadcast("coolantTemp", coolant.toString())
    }

    // oil temperature canBus message received
    private fun processOilTemp(message: Array<Int>){
        val oilTemp = message[1] - 40
        sendBroadcast("oilTemp", oilTemp.toString())
    }

    // odometer canBus message received
    private fun processOdometer(message: Array<Int>){
        val odometer = (message[1] shl 16) + (message[2] shl 8) + message[3]
        sendBroadcast("odometer", odometer.toString())
    }

    // ambient temperature canBus message received
    private fun processAmbientTemp(message: Array<Int>){
        //var temp = (((message[1] shl 8) + (message[2].toUByte()).toInt())-4000).toFloat() / 100
        // round to .5
        //temp  = kotlin.math.round(temp * 2) / 2

        val temp = (((message[1] shl 8) + (message[2].toUByte()).toInt())-4000) / 100
        sendBroadcast("ambientTemp", temp.toString())
    }

    // check what config send from canBox is to process
    private fun processBoxConfig(message: Array<Int>){
        when(message[1]){
            0x00 -> broadcastBoxConfig(message)
            0x01 -> broadcastAmpConfig(message)
        }
    }

    // canBox config message received
    private fun broadcastBoxConfig(message: Array<Int>){
        val intent = Intent("de.zebbel.canbus.boxConfig")
        intent.putExtra("version", message[2])
        intent.putExtra("startCondition", message[3])
        intent.putExtra("stopCondition", message[4])
        intent.putExtra("rearCamera", message[5])
        intent.putExtra("resendVolume", message[6])
        intent.putExtra("resendVolumeTime", message[7])
        applicationContext.sendBroadcast(intent)
    }

    // amplifier config message received
    private fun broadcastAmpConfig(message: Array<Int>){
        val intent = Intent("de.zebbel.canbus.ampConfig")
        intent.putExtra("ampVolume", message[2])
        intent.putExtra("ampBalance", message[3])
        intent.putExtra("ampFade", message[4])
        intent.putExtra("ampBass", message[5])
        intent.putExtra("ampMid", message[6])
        intent.putExtra("ampTreble", message[7])
        applicationContext.sendBroadcast(intent)
    }
}