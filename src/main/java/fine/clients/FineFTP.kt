package fine.clients

import android.annotation.SuppressLint
import android.os.Build
import fine.model.FineFile
import org.apache.commons.net.ftp.FTPClient
import org.apache.commons.net.ftp.FTPClientConfig
import org.apache.commons.net.ftp.FTPFile
import org.apache.commons.net.ftp.FTPReply
import java.io.*
import java.nio.charset.StandardCharsets
import java.util.*

/**
 * FTP封装类.构造函数.
 *
 * @param host
 * hostName 服务器名
 * @param user
 * userName 用户名
 * @param pass
 * password 密码
 */
class FineFTP constructor(
    private val hostName: String,
    private val userName: String,
    private val password: String
) {
    /**
     * FTP连接.
     */
    private val ftpClient: FTPClient = FTPClient()

    /**
     * FTP列表.
     */
    private val filelist: MutableList<FTPFile> = ArrayList()

    /**
     * FTP当前目录.
     */
    private var currentPath = ""

    /**
     * 统计流量.
     */
    private var response = 0.0

    /**
     * 打开FTP服务.
     *
     * @throws IOException
     */
    @Throws(IOException::class)
    fun openConnect(): Boolean {

        // 中文转码
        ftpClient.setControlEncoding("UTF-8")
        var reply: Int // 服务器响应值
        // 连接至服务器
        ftpClient.connect(hostName)
        // 获取响应值
        reply = ftpClient.getReplyCode()
        if (!FTPReply.isPositiveCompletion(reply)) {
            // 断开连接
            ftpClient.disconnect()
            throw IOException("connect fail: $reply")
        }
        // 登录到服务器
        val login: Boolean = ftpClient.login(userName, password)
        // 获取响应值
        reply = ftpClient.getReplyCode()
        if (!FTPReply.isPositiveCompletion(reply)) {
            // 断开连接
            ftpClient.disconnect()
            throw IOException("connect fail: $reply")
        } else {
            // 获取登录信息
            val config =
                FTPClientConfig(ftpClient.getSystemType().split(" ").toTypedArray().get(0))
            config.setServerLanguageCode("zh")
            ftpClient.configure(config)
            // 使用被动模式设为默认
            ftpClient.enterLocalPassiveMode()
            // 二进制文件支持
            ftpClient.setFileType(org.apache.commons.net.ftp.FTP.BINARY_FILE_TYPE)
            println("login")
        }
        return login
    }

    /**
     * 关闭FTP服务.
     *
     * @throws IOException
     */
    @Throws(IOException::class)
    fun closeConnect() {
        if (ftpClient != null) {
            if (ftpClient.isConnected()) {
                // 登出FTP
                ftpClient.logout()
                // 断开连接
                ftpClient.disconnect()
                println("logout")
            }
        }
    }

    /**
     * 列出FTP下所有文件.
     *
     * @param remotePath
     * 服务器目录
     * @return FTPFile集合
     * @throws IOException
     */
    @Throws(IOException::class)
    fun listFiles(remotePath: String?): List<FTPFile> {
        if (ftpClient != null) {
            // 获取文件
            try {
                val files: Array<FTPFile> = ftpClient.listFiles(remotePath)
                if (files != null && files.size > 0) {
                    // 遍历并且添加到集合
                    for (file in files) {
                        filelist.add(file)
                    }
                }
            } catch (e: Exception) {
//                Log.e("TAG", "请稍等...")
            }
        }
        return filelist
    }

    /**
     * 下载文件，可实现断点下载.
     *
     * @param serverPath
     * Ftp目录及文件路径
     * @param localPath
     * 本地目录
     * @param fileName
     * 下载之后的文件名称
     * @param listener
     * 监听器
     * @throws IOException
     */
    @SuppressLint("NewApi")
    @Throws(java.lang.Exception::class)
    fun downloadFile(
        serverPath: String,
        files: List<FineFile>,
        listener: DownLoadProgressListener
    ) {

        // 打开FTP服务
        ftpClient.changeWorkingDirectory(serverPath)

        var isexist = false
        for (file in files) {
            val inin = ftpClient.listFiles(
                String(
                    file.dealFileName.toByteArray(StandardCharsets.UTF_8),
                    StandardCharsets.ISO_8859_1
                )
            )
            if (inin.size == 0) continue
            // 找到需要下载的文件
            if (!inin[0].getName().equals(file.dealFileName))
                continue
            val serverSize = inin[0].size // 获取远程文件的长度
            val localFile = file.getFile()
            var localSize: Long = 0
            if (localFile.exists()) {
                localSize = localFile.length() // 如果本地文件存在，获取本地文件的长度
                if (localSize != serverSize) {
//                    val file = File(localPath)
                    file.getFile().delete()
                } else {
//                            listener.onDownLoadProgress(fileNames.indexOf(fileName),
//                                STEP_SUCCESS, 0)
                    isexist = true
                    continue
                }
            }

            // 进度
            val step = serverSize / 100
            var process: Long = 0
            var currentSize: Long = 0
            // 开始准备下载文件
            val out: OutputStream = FileOutputStream(localFile, true)
//                    ftpClient.restartOffset = localSize
            val input = ftpClient.retrieveFileStream(inin[0].name)
            val b = ByteArray(1024)
            var length = 0
            while (input.read(b).also { length = it } != -1) {
                out.write(b, 0, length)
                currentSize = currentSize + length
                if (currentSize / step != process) {
                    process = currentSize / step
                    if (process >= 100) {
                        listener.onDownLoadProgress(
                            files.indexOf(file),
                            STEP_SUCCESS,
                            100
                        )
                    }
                    else if (process % 5 == 0L) {  //每隔%5的进度返回一次
                        listener.onDownLoadProgress(
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
            // 此方法是来确保流处理完毕，如果没有此方法，可能会造成现程序死掉
            listener.onDownloadComplete(files)
            isexist = ftpClient.completePendingCommand()
        }
        if (isexist) listener.onDownloadComplete(files)
    }

    fun clear(local:File,files:List<FineFile>){
        val filenames=Array<String>(files.size){
            files[it].dealFileName
        }
        local.listFiles().forEach {
            if (!filenames.contains(it.name))
                it.delete()
        }
    }

    /**
     * 上传.
     *
     * @param localFile
     * 本地文件
     * @param remotePath
     * FTP目录
     * @return Result
     * @throws IOException
     */
    @Throws(IOException::class)
    fun uploading(localFile: File, remotePath: String): Boolean {
        var flag = true
        var result = false
        // 初始化FTP当前目录
        currentPath = remotePath
        // 初始化当前流量
        response = 0.0
        // 二进制文件支持
        ftpClient.setFileType(org.apache.commons.net.ftp.FTP.BINARY_FILE_TYPE)
        // 使用被动模式设为默认
        ftpClient.enterLocalPassiveMode()
        // 设置模式
        ftpClient.setFileTransferMode(org.apache.commons.net.ftp.FTP.STREAM_TRANSFER_MODE)
        // 改变FTP目录
        ftpClient.changeWorkingDirectory(REMOTE_PATH)
        flag = if (localFile.isDirectory) {
            // 上传多个文件
            uploadingMany(localFile)
        } else {
            // 上传单个文件
            uploadingSingle(localFile)
        }
        return flag
    }

    /*
	 * 下载进度监听
	 */
    interface DownLoadProgressListener {
        fun onDownLoadProgress(position: Int, currentStep: String, downProcess: Long)

        fun onDownloadComplete(files:List<FineFile>)
    }

    /**
     * 上传单个文件.
     *
     * @param localFile
     * 本地文件
     * @return true上传成功, false上传失败
     * @throws IOException
     */
    @Throws(IOException::class)
    private fun uploadingSingle(localFile: File): Boolean {
        var flag = true
        // 创建输入流
        val inputStream: InputStream = FileInputStream(localFile)
        // 统计流量
        response += inputStream.available().toDouble() / 1
        // 上传单个文件
        flag = ftpClient.storeFile(localFile.name, inputStream)
        // 关闭文件流
        inputStream.close()
        return flag
    }

    /**
     * 上传多个文件.
     *
     * @param localFile
     * 本地文件夹
     * @return true上传成功, false上传失败
     * @throws IOException
     */
    @Throws(IOException::class)
    private fun uploadingMany(localFile: File): Boolean {
        var flag = true
        // FTP当前目录
        currentPath = if (currentPath != REMOTE_PATH) {
            currentPath + REMOTE_PATH + localFile.name
        } else {
            currentPath + localFile.name
        }
        // FTP下创建文件夹
        ftpClient.makeDirectory(currentPath)
        // 更改FTP目录
        ftpClient.changeWorkingDirectory(currentPath)
        // 得到当前目录下所有文件
        val files = localFile.listFiles()
        // 遍历得到每个文件并上传
        for (file in files) {
            if (file.isHidden) {
                continue
            }
            flag = if (file.isDirectory) {
                // 上传多个文件
                uploadingMany(file)
            } else {
                // 上传单个文件
                uploadingSingle(file)
            }
        }
        return flag
    }

    companion object {
        /**
         * FTP根目录.
         */
        const val REMOTE_PATH = "\\"
        const val REMOTE_PATH2 = "\\bbb\\"
        const val STEP_DOWNLOADING = "正在下载"
        const val STEP_SUCCESS = "下载成功"
        const val STEP_FAILED = "下载失败"
    }
}