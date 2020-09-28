package de.zebbel.canbus.activitys

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.os.Bundle
import android.view.View
import android.view.inputmethod.EditorInfo
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import de.zebbel.canbus.R

class CanBoxSettingsActivity : AppCompatActivity() {
    var startConditionSpinner: Spinner? = null
    private var stopConditionSpinner: Spinner? = null
    var rearCameraSwitch: CheckBox? = null
    var resendVolumeSwitch: CheckBox? = null
    var resendVolumeTime: TextView? = null
    private var saveButton: Button? = null
    private var defaultButton: Button? = null

    var startCondition = 0
    var stopCondition = 0
    var rearCamera = 0
    var resendVolume = 0
    var resendTime = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_can_box_settings)

        registerReceiver(mReceiver, IntentFilter("de.zebbel.canbus.boxConfig"))

        val intent = Intent("de.zebbel.canbus.config")
        intent.putExtra("boxConfig", "getBoxConfig")
        applicationContext.sendBroadcast(intent)

        startConditionInit()
        stopConditionInit()
        rearCameraInit()
        resendVolumeInit()
        saveDefaultButtonsInit()
    }

    private val mReceiver: BroadcastReceiver = object : BroadcastReceiver() {
        override fun onReceive(context: Context, intent: Intent) {
            startCondition = intent.getIntExtra("startCondition", 0)
            stopCondition = intent.getIntExtra("stopCondition", 0)
            rearCamera = intent.getIntExtra("rearCamera", 0)
            resendVolume = intent.getIntExtra("resendVolume", 0)
            resendTime = intent.getIntExtra("resendVolumeTime", 0)

            startConditionSpinner?.setSelection(startCondition)
            startConditionSpinner?.setSelection(stopCondition)

            if (rearCamera == 1) { rearCameraSwitch?.isChecked = true }
            if (rearCamera == 0) { rearCameraSwitch?.isChecked = false }

            if (resendVolume == 1) { resendVolumeSwitch?.isChecked = true }
            if (resendVolume == 0) { resendVolumeSwitch?.isChecked = false }

            resendVolumeTime?.text = resendTime.toString()
        }
    }

    private fun startConditionInit(){
        startConditionSpinner = findViewById(R.id.startConditionSpinner)


        val adapter = ArrayAdapter.createFromResource(
            applicationContext, R.array.startCondition,
            android.R.layout.simple_spinner_item
        )
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        startConditionSpinner!!.adapter = adapter



        startConditionSpinner!!.onItemSelectedListener = object: AdapterView.OnItemSelectedListener{
            override fun onItemSelected(parent: AdapterView<*>, view: View, position: Int, id: Long){
                startCondition = position
                setConfig()
            }
            override fun onNothingSelected(parent: AdapterView<*>){
                // Another interface callback
            }
        }
    }

    private fun stopConditionInit(){
        stopConditionSpinner = findViewById(R.id.stopConditionSpinner)

        val adapter = ArrayAdapter.createFromResource(
            applicationContext, R.array.stopCondition,
            android.R.layout.simple_spinner_item
        )
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        stopConditionSpinner!!.adapter = adapter


        stopConditionSpinner!!.onItemSelectedListener = object: AdapterView.OnItemSelectedListener{
            override fun onItemSelected(parent: AdapterView<*>, view: View, position: Int, id: Long){
                stopCondition = position
                setConfig()
            }
            override fun onNothingSelected(parent: AdapterView<*>){
                // Another interface callback
            }
        }
    }

    private fun rearCameraInit(){
        rearCameraSwitch = findViewById(R.id.rearCameraSwitch)

        rearCameraSwitch!!.setOnClickListener {
            if(rearCameraSwitch!!.isChecked){ rearCamera = 1}
            if(!rearCameraSwitch!!.isChecked){ rearCamera = 0}
            setConfig()
        }
    }

    private fun resendVolumeInit(){
        resendVolumeSwitch = findViewById(R.id.resendVolumeSwitch)

        resendVolumeSwitch!!.setOnClickListener {
            if(resendVolumeSwitch!!.isChecked){ resendVolume = 1}
            if(!resendVolumeSwitch!!.isChecked){ resendVolume = 0}
            setConfig()
        }

        resendVolumeTime = findViewById(R.id.editTextTimeResendVolumeTime)


        resendVolumeTime!!.setOnEditorActionListener { _, actionId, _ ->
            if (actionId == EditorInfo.IME_ACTION_DONE) {
                resendTime = Integer.parseInt(resendVolumeTime!!.text.toString())
                resendVolumeTime!!.clearFocus()
                setConfig()
            }
            false
        }
    }

    fun setConfig(){
        val intent = Intent("de.zebbel.canbus.config")
        intent.putExtra("boxConfig", "setBoxConfig")
        intent.putExtra("startCondition", startCondition)
        intent.putExtra("stopCondition", stopCondition)
        intent.putExtra("rearCamera", rearCamera)
        intent.putExtra("resendVolume", resendVolume)
        intent.putExtra("resendVolumeTime", resendTime)
        applicationContext.sendBroadcast(intent)
    }

    private fun saveDefaultButtonsInit(){
        saveButton = findViewById(R.id.saveButton)
        defaultButton = findViewById(R.id.defaultButton)

        saveButton!!.setOnClickListener {
            val intent = Intent("de.zebbel.canbus.config")
            intent.putExtra("boxConfig", "saveConfig")
            applicationContext.sendBroadcast(intent)
        }

        defaultButton!!.setOnClickListener {
            val intent = Intent("de.zebbel.canbus.config")
            intent.putExtra("boxConfig", "resetConfig")
            applicationContext.sendBroadcast(intent)
        }
    }

}