package fine.clients

import fine.verifyCRC16
import java.net.DatagramPacket
import java.net.DatagramSocket
import java.net.InetAddress
import java.net.SocketException
import java.util.concurrent.Executors
import java.util.concurrent.ScheduledExecutorService
import java.util.concurrent.TimeUnit

class FineUDP constructor(
    private val host: String,
    private val port: Int,
    private var timeout:Int = 100,
    private var delay:Long = 1000,
    private val onError:(Exception)->Unit = {},
    private var run : FineUDP.()->Runnable
) {
    private val receiveByte = ByteArray(BUFFER_LENGTH)
    private var client: DatagramSocket = DatagramSocket()
    private var receivePacket: DatagramPacket = DatagramPacket(receiveByte, BUFFER_LENGTH)
    private val mThreadPool: ScheduledExecutorService = Executors.newScheduledThreadPool(2)
    private lateinit var runnable :Runnable

    fun startUDPSocket() {
        try {
            runnable = run()
            client.soTimeout = timeout
            mThreadPool.schedule(runnable,delay,TimeUnit.MILLISECONDS)
        } catch (e: SocketException) {
            e.printStackTrace()
            onError(e)
        }
    }
    fun stopUDPSocket() {
        client.close()
    }

    fun sendMessage(message: String, onError: (Exception) -> Unit = {}) {
        try {
            client.send(
                DatagramPacket(
                    message.toByteArray(),
                    message.length,
                    InetAddress.getByName(host),
                    port
                )
            )
        } catch (e: Exception) {
            onError(e)
        }
    }

    fun sendMessage(message: ByteArray, onError: (Exception) -> Unit = {}) {
        try {
            client.send(
                DatagramPacket(
                    message,
                    message.size,
                    InetAddress.getByName(host),
                    port
                )
            )
        } catch (e: Exception) {
            onError(e)
        }
    }

    fun receiveMessage(onError: (Exception) -> Unit = {}, onReceive:(ByteArray,String)->Unit) {
        try {
            client.receive(receivePacket)
            if (receivePacket.data.verifyCRC16()) onReceive(receivePacket.data,String(receivePacket.data, 0, receivePacket.length))
        }catch (e:Exception){
//            e.printStackTrace()
            onError(e)
        }
    }


    companion object {
        private const val TAG = "FineUDP"
        private const val BUFFER_LENGTH = 1024
    }
}