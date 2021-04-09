package fine.eliyah

class Config {
    var ip = "192.168.1.108"
        private set
    var port = 8080
        private set
    var ftpIp = "192.168.1.108"
        private set
    var ftpPort = 21
        private set
    var ftpUser = "MES"
        private set
    var ftpPsw = "962464ff."
        private set
    var picPort = 8080
        private set
    var project: String? = null
        private set

    fun setFtpPort(ftpPort: Int): Config {
        this.ftpPort = ftpPort
        return this
    }

    fun setPicPort(picPort: Int): Config {
        this.picPort = picPort
        return this
    }

    fun setIp(ip: String): Config {
        this.ip = ip
        return this
    }

    fun setPort(port: Int): Config {
        this.port = port
        return this
    }

    fun setFtpIp(ftpIp: String): Config {
        this.ftpIp = ftpIp
        return this
    }

    fun setFtpUser(ftpUser: String): Config {
        this.ftpUser = ftpUser
        return this
    }

    fun setFtpPsw(ftpPsw: String): Config {
        this.ftpPsw = ftpPsw
        return this
    }

    fun setProject(project: String?): Config {
        this.project = project
        return this
    }
}