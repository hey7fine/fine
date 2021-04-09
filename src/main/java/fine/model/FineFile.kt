package fine.model

import java.io.File

data class FineFile(
    val id:Int,
    val fileName:String,
    val dealFileName:String,
    val local:String
){
    fun getFile():File{
        return File(local,dealFileName)
    }
}