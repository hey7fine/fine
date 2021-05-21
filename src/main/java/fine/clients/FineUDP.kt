package fine.clients

import android.util.Log
import java.io.IOException
import java.net.DatagramPacket
import java.net.DatagramSocket
import java.net.InetAddress
import java.net.SocketException
import java.util.concurrent.ExecutorService
import java.util.concurrent.Executors

class FineUDP constructor(
    private val host: String,
    private val port: Int,
    private var onParserComplete : (data: DatagramPacket?)->Unit = {}
) {
    private val receiveByte = ByteArray(BUFFER_LENGTH)
    private var isThreadRunning = false
    private var client: DatagramSocket? = null
    private var receivePacket: DatagramPacket? = null
    private val mThreadPool: ExecutorService = Executors.newFixedThreadPool(Runtime.getRuntime().availableProcessors() * POOL_SIZE)
    private var clientThread: Thread? = null

    private fun startUDPSocket() {
        if (client != null) return
        try {
//            表明这个 Socket 在设置的端口上监听数据。
            client = DatagramSocket(port)
            if (receivePacket == null) {
                receivePacket = DatagramPacket(
                    receiveByte,
                    BUFFER_LENGTH
                )
            }
            startSocketThread()
        } catch (e: SocketException) {
            e.printStackTrace()
        }
    }

    private fun startSocketThread() {
        clientThread = Thread {
            Log.d(TAG, "clientThread is running...")
            receiveMessage()
        }
        isThreadRunning = true
        clientThread!!.start()
    }

    private fun receiveMessage() {
        while (isThreadRunning) {
            if (client != null) {
                try {
                    client!!.receive(receivePacket)
                } catch (e: IOException) {
                    Log.e(TAG, "UDP数据包接收失败！线程停止")
                    stopUDPSocket()
                    e.printStackTrace()
                    return
                }
            }
            if (receivePacket == null || receivePacket!!.length == 0) {
                Log.e(TAG, "无法接收UDP数据或者接收到的UDP数据为空")
                continue
            }
            val strReceive =
                String(receivePacket!!.data, 0, receivePacket!!.length)
            Log.d(
                TAG,
                "$strReceive from " + receivePacket!!.address
                    .hostAddress + ":" + receivePacket!!.port
            )
            onParserComplete(receivePacket)
            if (receivePacket != null) {
                receivePacket!!.length =
                    BUFFER_LENGTH
            }
        }
    }

    private fun stopUDPSocket() {
        isThreadRunning = false
        receivePacket = null
        if (clientThread != null) {
            clientThread!!.interrupt()
        }
        if (client != null) {
            client!!.close()
            client = null
        }
        onParserComplete = {}
    }

    fun sendMessage(message: String) {
        if (client == null) {
            startUDPSocket()
        }
        mThreadPool.execute {
            try {
                val targetAddress =
                    InetAddress.getByName(host)
                val packet = DatagramPacket(
                    message.toByteArray(),
                    message.length,
                    targetAddress,
                    port
                )
                client!!.send(packet)
            } catch (e: IOException) {
                e.printStackTrace()
            }
        }
    }

    companion object {
        var udp: FineUDP? = null
            get() {
                if (field == null) {
                    synchronized(FineUDP::class.java) {
                        if (field == null) {
                            field = FineUDP("", 0)
                        }
                    }
                }
                return field
            }
            private set
        private const val TAG = "UDPBuild"

        //    单个CPU线程池大小
        private const val POOL_SIZE = 5
        private const val BUFFER_LENGTH = 1024

    }
}