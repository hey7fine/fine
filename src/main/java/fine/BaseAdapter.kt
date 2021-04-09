package eliyah.fine

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import java.util.*

abstract class BaseAdapter<T> :RecyclerView.Adapter<BaseAdapter.ViewHolder>() {
    private lateinit var context:Context
    private var data :List<T> = ArrayList()
    private var count=0
    private var bgColors = listOf<Int>()

    //更新数据内容
    fun setData(data:List<T>){
        this.data=data
        notifyDataSetChanged()
    }

    fun getData() = this.data

    //每页条目数量 & 背景颜色
    fun setParms(count:Int,colors:List<Int>?){
        this.count=count
        if(colors!=null) this.bgColors=colors
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        this.context=parent.context
        return ViewHolder(LayoutInflater.from(parent.context).inflate(LayoutId,parent,false))
    }

    override fun getItemCount(): Int {
        //铺满空布局
        if (count!=0 && data.size%count!=0) return data.size+(count-data.size%count)
        return data.size
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        if (bgColors.size>0)
            holder.itemView.setBackgroundColor(if (position%2==0) bgColors[0] else bgColors[1])
        if (position < data.size)
            fill(holder.itemView,position,data[position])
//        else clear(holder.itemView,position)
    }

    abstract val LayoutId:Int

    open fun fill(itemView : View,position: Int, item: T){
//        itemView.setOnClickListener { listener.onItemClick(position) }
    }

    open fun clear(itemView : View,position: Int){}

    interface OnItemClickListener{
        fun onItemClick(position: Int)
    }

    class ViewHolder(itemView: View):RecyclerView.ViewHolder(itemView){
        fun<T : View> getView(id:Int):T = itemView.findViewById<T>(id)
    }
}