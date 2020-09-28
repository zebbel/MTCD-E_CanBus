package de.zebbel.canbus.handlers

import android.annotation.SuppressLint
import android.content.Context
import android.hardware.SerialManager
import android.util.Log
import kotlinx.coroutines.*
import java.nio.ByteBuffer

@Suppress("BlockingMethodInNonBlockingContext")
@ExperimentalUnsignedTypes
class SerialHandler constructor(private var applicationContext: Context, private var canbusHandler: CanbusHandler) {
    private val uiScope = CoroutineScope(Dispatchers.Main)
    //var context: Context? = applicationContext

    fun startSerial() {
        uiScope.launch {
            serialTask()
        }
    }

    // coroutine for serial read
    @SuppressLint("WrongConstant")
    private suspend fun serialTask() {
        withContext(Dispatchers.Default) {
            var resp: String? = null

            val serialManager = applicationContext.getSystemService("serial") as SerialManager
            val serialPort = serialManager.openSerialPort("/dev/ttyV0", 38400)
            val buffer  = ByteBuffer.allocate(1024)

            try {
                var serialException = 0
                // run while serialException >=0
                while (serialException >= 0) {
                    try {
                        // clear buffer
                        buffer.clear()

                        delay(500)

                        // read serial to buffer and set number of read bytes
                        val numBytes: Int = serialPort!!.read(buffer)
                        // if there was data to read process data
                        if (numBytes > 0) {
                            // do until all bytes of buffer are processed
                            for(x in 0 until numBytes){
                                // reset dataArray
                                val dataArray = Array(10){0}

                                // if byte is startByte message starts
                                if(buffer.get().toInt() == 0x2E){
                                    // message id
                                    dataArray[0] = buffer.get().toInt()
                                    // number of data bytes in message
                                    val len = buffer.get().toInt()
                                    // read data bytes to dataArray
                                    for(y in 0 until len){
                                        dataArray[y + 1] = buffer.get().toInt()
                                    }
                                    // read checksum
                                    buffer.get()

                                    // process message
                                    canbusHandler.processMessage(dataArray)
                                }
                            }
                        }
                        serialException = numBytes
                    } catch (e2: Exception) {
                        Log.i("CanBusServer", "mtc >>> read canbus data Exception !")
                        Log.i("read canbus Exception: ", e2.toString())
                    }
                }
            } catch (e: InterruptedException) {
                e.printStackTrace()
                resp = e.message
            } catch (e: Exception) {
                e.printStackTrace()
                resp = e.message
            }
            Log.d("serial exception",resp!!)
        }

    }
}