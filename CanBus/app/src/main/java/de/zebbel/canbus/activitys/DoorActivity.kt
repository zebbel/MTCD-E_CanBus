package de.zebbel.canbus.activitys

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.widget.ImageView
import androidx.appcompat.app.AppCompatActivity
import de.zebbel.canbus.R


class DoorActivity : AppCompatActivity() {

    val handler = Handler(Looper.myLooper()!!)
    val finishRunnable = Runnable { finish() }

    private var doorbg: ImageView? = null
    private var fleftdoor: ImageView? = null
    private var bleftdoor: ImageView? = null
    private var frightdoor: ImageView? = null
    private var brightdoor: ImageView? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_door)

        registerReceiver(mReceiver, IntentFilter("de.zebbel.canbus.doors"))

        doorbg = findViewById(R.id.doorbg)
        fleftdoor = findViewById(R.id.fleftdoor)
        bleftdoor = findViewById(R.id.bleftdoor)
        frightdoor = findViewById(R.id.frightdoor)
        brightdoor = findViewById(R.id.brightdoor)

        val intent = intent
        checkDoors(intent.getBooleanArrayExtra("doors"))

        handler.postDelayed(finishRunnable, 2000)
    }

    override fun onDestroy() {
        // Unregister since the activity is about to be closed.
        unregisterReceiver(mReceiver)
        super.onDestroy()
    }

    private val mReceiver: BroadcastReceiver = object : BroadcastReceiver() {
        override fun onReceive(context: Context, intent: Intent) {
            handler.removeCallbacks(finishRunnable)

            checkDoors(intent.getBooleanArrayExtra("doors"))

            handler.postDelayed(finishRunnable, 2000)
        }
    }

    fun checkDoors(door: BooleanArray?){
        if(door != null) {
            if (door[0]) doorbg?.setImageResource(R.drawable.car_1)
            else doorbg?.setImageResource(R.drawable.car_0)

            if (door[1]) fleftdoor?.setImageResource(R.drawable.lf_1)
            else fleftdoor?.setImageResource(R.drawable.lf_0)

            if (door[2]) bleftdoor?.setImageResource(R.drawable.lr_1)
            else bleftdoor?.setImageResource(R.drawable.lr_0)

            if (door[3]) frightdoor?.setImageResource(R.drawable.rf_1)
            else frightdoor?.setImageResource(R.drawable.rf_0)

            if (door[4]) brightdoor?.setImageResource(R.drawable.rr_1)
            else brightdoor?.setImageResource(R.drawable.rr_0)
        }
    }
}