package fine.clients

import fine.model.FineFile
import org.apache.commons.net.ftp.FTPClient
import org.apache.commons.net.ftp.FTPClientConfig
import org.apache.commons.net.ftp.FTPFile
import org.apache.commons.net.ftp.FTPReply
import java.io.*
import java.nio.charset.StandardCharsets

class FineFTP constructor(
    private val host: String,
    private val username: String,
    private val password: String
) {
    private val ftpClient: FTPClient = FTPClient()

    private var fileList : Array<FTPFile> = arrayOf()
    private var currentPath = ""
    private var response = 0.0

    fun openConnect(): Boolean {
        ftpClient.controlEncoding = "UTF-8"
        var reply: Int = ftpClient.replyCode
        ftpClient.connect(host)
        if (!FTPReply.isPositiveCompletion(reply)) {
            ftpClient.disconnect()
            throw IOException("connect fail: $reply")
        }
        val login: Boolean = ftpClient.login(username, password)
        reply = ftpClient.replyCode
        if (!FTPReply.isPositiveCompletion(reply)) {
            ftpClient.disconnect()
            throw IOException("connect fail: $reply")
        } else {
            val config = FTPClientConfig(ftpClient.systemType.split(" ").toTypedArray()[0])
            config.serverLanguageCode = "zh"
            ftpClient.configure(config)
            ftpClient.enterLocalPassiveMode()
            ftpClient.setFileType(org.apache.commons.net.ftp.FTP.BINARY_FILE_TYPE)
            fileList = ftpClient.listFiles(host)
            println("login")
        }
        return login
    }

    fun closeConnect() {
        if (ftpClient.isConnected) {
            ftpClient.logout()
            ftpClient.disconnect()
            println("logout")
        }
    }

    fun downloadFile(
        serverPath: String,
        files: List<FineFile>,
        onDownloading : (position: Int, currentStep: String, downProcess: Long)->Unit,
        onComplete : (List<FineFile>)->Unit
    ) {
        // 打开FTP服务
        ftpClient.changeWorkingDirectory(serverPath)

        var isExist = false
        for (file in files) {
            val filter = ftpClient.listFiles(
                String(
                    file.dealFileName.toByteArray(StandardCharsets.UTF_8),
                    StandardCharsets.ISO_8859_1
                )
            )
            if (filter.isEmpty()) continue
            if (!filter[0].name.equals(file.dealFileName))
                continue
            val serverSize = filter[0].size
            val localFile = file.file
            var localSize: Long
            if (localFile.exists()) {
                localSize = localFile.length()
                if (localSize != serverSize) {
                    file.file.delete()
                } else {
                    isExist = true
                    continue
                }
            }

            val step = serverSize / 100
            var process: Long = 0
            var currentSize: Long = 0
            val out: OutputStream = FileOutputStream(localFile, true)
            val input = ftpClient.retrieveFileStream(filter[0].name)
            val b = ByteArray(1024)
            var length: Int
            while (input.read(b).also { length = it } != -1) {
                out.write(b, 0, length)
                currentSize += length
                if (currentSize / step != process) {
                    process = currentSize / step
                    if (process >= 100) {
                        onDownloading(
                            files.indexOf(file),
                            STEP_SUCCESS,
                            100
                        )
                    }
                    else if (process % 5 == 0L) {  //每隔%5的进度返回一次
                        onDownloading(
                            files.indexOf(file),
                            STEP_DOWNLOADING,
                            process
                        )
                    }
                }
            }
            out.flush()
            out.close()
            input.close()

            onComplete(files)
            isExist = ftpClient.completePendingCommand()
        }
        if (isExist) onComplete(files)
    }

    fun clear(local:File,files:List<FineFile>){
        val filenames=Array(files.size){
            files[it].dealFileName
        }
        local.listFiles()?.forEach {
            if (!filenames.contains(it.name))
                it.delete()
        }
    }

    fun upload(localFile: File, remotePath: String): Boolean {
        currentPath = remotePath
        response = 0.0
        ftpClient.setFileType(org.apache.commons.net.ftp.FTP.BINARY_FILE_TYPE)
        ftpClient.enterLocalPassiveMode()
        ftpClient.setFileTransferMode(org.apache.commons.net.ftp.FTP.STREAM_TRANSFER_MODE)
        ftpClient.changeWorkingDirectory(REMOTE_PATH)
        return if (localFile.isDirectory)
            uploadMany(localFile)
        else
            uploadSingle(localFile)
    }

    private fun uploadSingle(localFile: File): Boolean {
        val inputStream: InputStream = FileInputStream(localFile)
        response += inputStream.available().toDouble() / 1
        val flag = ftpClient.storeFile(localFile.name, inputStream)
        inputStream.close()
        return flag
    }

    private fun uploadMany(localFile: File): Boolean {
        currentPath = if (currentPath != REMOTE_PATH) {
            currentPath + REMOTE_PATH + localFile.name
        } else {
            currentPath + localFile.name
        }
        ftpClient.makeDirectory(currentPath)
        ftpClient.changeWorkingDirectory(currentPath)
        val files = localFile.listFiles()?: arrayOf()
        var flag = true
        for (file in files) {
            if (file.isHidden) {
                continue
            }
            flag = if (file.isDirectory)
                uploadMany(file)
            else
                uploadSingle(file)
        }
        return flag
    }

    companion object {
        const val REMOTE_PATH = "\\"
        const val STEP_DOWNLOADING = "正在下载"
        const val STEP_SUCCESS = "下载成功"
        const val STEP_FAILED = "下载失败"
    }
}