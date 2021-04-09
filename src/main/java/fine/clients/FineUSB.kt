package fine.clients

import android.hardware.usb.*
import com.hoho.android.usbserial.driver.UsbSerialDriver
import com.hoho.android.usbserial.driver.UsbSerialPort
import com.hoho.android.usbserial.driver.UsbSerialProber
import com.hoho.android.usbserial.util.SerialInputOutputManager
import java.util.concurrent.Executors


class FineUSB (
    private var manager:UsbManager
) {
    var driver:UsbSerialDriver ?=null
    var targetDevice:UsbDevice ?=null
    var targetPort:UsbSerialPort ?=null

    val availableDrivers = UsbSerialProber.getDefaultProber().findAllDrivers(manager)

    fun getDevice(deviceName:String):UsbDevice?{
        availableDrivers.forEach{
            if(deviceName.equals(it.device.deviceName)){
                driver=it
                targetDevice=it.device
            }
        }
        return targetDevice
    }

    fun getDevice(deviceID:Int):UsbDevice?{
        availableDrivers.forEach{
            if(deviceID.equals(it.device.deviceId)){
                driver=it
                targetDevice=it.device
            }
        }
        return targetDevice
    }

    fun getPort(portNum:Int):UsbSerialPort?{
        driver?.ports?.forEach {
            if (it.portNumber.equals(portNum)) targetPort=it
        }
        return targetPort
    }

    fun setPortParameters(port: UsbSerialPort,a:Int,b:Int,c:Int,d:Int){
        port.setParameters(a,b,c,d)
    }

    fun openConnect(listener:SerialInputOutputManager.Listener){
        if (targetDevice!=null){
            val connection=manager.openDevice(targetDevice)
            targetPort?.open(connection)
            targetPort?.setParameters(115200, 8, UsbSerialPort.STOPBITS_1, UsbSerialPort.PARITY_NONE)

            val usbIoManager = SerialInputOutputManager(targetPort, listener)
            Executors.newSingleThreadExecutor().submit(usbIoManager)
        }
    }

    fun closeConnect(){
        targetPort?.close()
    }

}