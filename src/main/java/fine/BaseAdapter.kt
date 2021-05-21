package fine

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView

abstract class BaseAdapter<T>(
    private var count: Int = 1,
    private var bgColors: List<Int> = listOf()
) :RecyclerView.Adapter<BaseAdapter.ViewHolder>() {
    
    private lateinit var context:Context
    private var data :MutableList<T> = ArrayList()

    //更新数据内容
    fun setData(data:List<T>){
        this.data=data.toMutableList()
        notifyDataSetChanged()
    }

    fun getData() = this.data

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        this.context=parent.context
        return ViewHolder(LayoutInflater.from(parent.context).inflate(layoutRes,parent,false))
    }

    override fun getItemCount(): Int {
        //铺满空布局
        if (count!=0 && data.size%count!=0) return data.size+(count-data.size%count)
        return data.size
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        if (bgColors.isNotEmpty())
            holder.itemView.setBackgroundColor(bgColors[position%bgColors.size])
        if (position < data.size)
            fill(holder.itemView,position,data[position])
    }

    abstract val layoutRes:Int

    open fun fill(view : View,position: Int, it: T){}

    open fun clear(view : View,position: Int){}

    class ViewHolder(itemView: View):RecyclerView.ViewHolder(itemView)
}