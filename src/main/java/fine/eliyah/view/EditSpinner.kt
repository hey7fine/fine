package fine.eliyah.view

import android.annotation.SuppressLint
import android.content.Context
import android.content.res.ColorStateList
import android.graphics.drawable.Drawable
import android.os.Build
import android.text.Editable
import android.text.InputFilter
import android.text.InputFilter.LengthFilter
import android.text.TextUtils
import android.text.TextWatcher
import android.util.AttributeSet
import android.util.TypedValue
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.WindowManager
import android.view.animation.Animation
import android.view.animation.RotateAnimation
import android.widget.*
import androidx.annotation.ColorInt
import androidx.annotation.DrawableRes
import fine.R
import fine.databinding.LayoutEditSpinnerBinding
import fine.eliyah.adapter.BaseEditSpinnerAdapter
import fine.eliyah.adapter.EditSpinnerAdapter

class EditSpinner constructor(
    context: Context,attrs: AttributeSet?
) :
    FrameLayout(context,attrs),
    AdapterView.OnItemClickListener,
    TextWatcher {
    private var mPopupWindow: ListPopupWindow? = null
    private var mAdapter: BaseEditSpinnerAdapter? = null
    private var mPopupWindowHideTime: Long = 0
    private lateinit var mAnimation: Animation
    private lateinit var mResetAnimation: Animation
    private var mOnItemClickListener: AdapterView.OnItemClickListener? = null
    private var mMaxLine = DEFAULT_MAX_LINE
    private var mDropDownBg: Drawable? = null
    private var mPopAnimStyle = 0
    private var mIsShowFilterData = true
    private var mIsFilterKey = false
    private lateinit var binding :LayoutEditSpinnerBinding
    

    private fun initView(context: Context) {
        val view = LayoutInflater.from(context).inflate(R.layout.layout_edit_spinner, this)
        binding = LayoutEditSpinnerBinding.bind(view)
        binding.btnExpand.setOnClickListener {
            togglePopupWindow()
        }
        binding.txtInput.addTextChangedListener(this)
    }

    private fun initAnimation() {
        mAnimation = RotateAnimation(
            0F,
            (-180).toFloat(), Animation.RELATIVE_TO_SELF, 0.5f, Animation.RELATIVE_TO_SELF, 0.5f
        )
        mAnimation.duration = 300
        mAnimation.fillAfter = true
        mResetAnimation = RotateAnimation(
            (-180).toFloat(),
            0F,
            Animation.RELATIVE_TO_SELF,
            0.5f,
            Animation.RELATIVE_TO_SELF,
            0.5f
        )
        mResetAnimation.duration = 300
        mResetAnimation.fillAfter = true
    }

    private fun setBaseAdapter(adapter: BaseAdapter?) {
        if (mPopupWindow == null) {
            initPopupWindow()
        }
        mPopupWindow!!.setAdapter(adapter)
    }

    @SuppressLint("UseCompatLoadingForDrawables")
    private fun initPopupWindow() {
        mPopupWindow = object : ListPopupWindow(context) {
            override fun show() {
                super.show()
                binding.btnExpand.startAnimation(mAnimation)
            }

            override fun dismiss() {
                super.dismiss()
            }
        }
        if (mPopAnimStyle != -1) {
            mPopupWindow?.animationStyle = mPopAnimStyle
        }
        mPopupWindow?.setOnItemClickListener(this)
        mPopupWindow?.inputMethodMode = PopupWindow.INPUT_METHOD_NOT_NEEDED
        mPopupWindow?.softInputMode = WindowManager.LayoutParams.SOFT_INPUT_ADJUST_NOTHING
        mPopupWindow?.promptPosition = ListPopupWindow.POSITION_PROMPT_BELOW
        mPopupWindow?.width = ViewGroup.LayoutParams.WRAP_CONTENT
        mPopupWindow?.height = ViewGroup.LayoutParams.WRAP_CONTENT
        mPopupWindow?.anchorView = binding.txtInput
//        mPopupWindow?.setVerticalOffset(
//            ThemeUtils.resolveDimension(
//                context,
//                R.attr.ms_dropdown_offset
//            )
//        )
//        mPopupWindow?.setListSelector(
//            ResUtils.getDrawable(
//                context,
//                R.drawable.xui_config_list_item_selector
//            )
//        )
        mPopupWindow?.setOnDismissListener {
            mPopupWindowHideTime = System.currentTimeMillis()
            binding.btnExpand.startAnimation(mResetAnimation)
        }
        if (mDropDownBg != null) {
            mPopupWindow?.setBackgroundDrawable(mDropDownBg)
        } else {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
//                mPopupWindow?.setBackgroundDrawable(context.getDrawable(R.drawable.bg_alarm_arg))
            }
        }
    }

    private fun togglePopupWindow() {
        if (System.currentTimeMillis() - mPopupWindowHideTime > TOGGLE_POPUP_WINDOW_INTERVAL) {
            if (mAdapter == null || mPopupWindow == null) {
                return
            }
            showFilterData("")
        }
    }

    override fun onItemClick(parent: AdapterView<*>, view: View, position: Int, id: Long) {
        binding.txtInput.setText((parent.adapter as BaseEditSpinnerAdapter).getItemString(position))
        if (mPopupWindow != null) {
            mPopupWindow!!.dismiss()
        }
        if (mOnItemClickListener != null) {
            mOnItemClickListener!!.onItemClick(parent, view, position, id)
        }
    }

    override fun beforeTextChanged(s: CharSequence, start: Int, count: Int, after: Int) {}
    override fun onTextChanged(s: CharSequence, start: Int, before: Int, count: Int) {}
    override fun afterTextChanged(s: Editable) {
        val key = s.toString()
        binding.txtInput.setSelection(key.length)
        if (!TextUtils.isEmpty(key)) {
            if (mIsShowFilterData) {
                showFilterData(key)
            }
        } else {
            if (mPopupWindow != null) {
                mPopupWindow!!.dismiss()
            }
        }
    }

    private fun showFilterData(key: String) {
        if (mPopupWindow == null || mAdapter == null || mAdapter?.editSpinnerFilter == null) {
            if (mPopupWindow != null) {
                mPopupWindow!!.dismiss()
            }
            return
        }
        if (mAdapter?.editSpinnerFilter?.onFilter(key) == true) {
            mPopupWindow!!.dismiss()
        } else {
            mPopupWindow!!.show()
        }
    }
    //==============对外接口=====================//
    /**
     * 获取编辑的内容
     *
     * @return
     */
    val text: String
        get() = "${binding.txtInput.text}"


    /**
     * 设置默认可选项集合
     *
     * @param data
     * @return
     */
    fun setItems(data: Array<String>): EditSpinner {
        mAdapter = EditSpinnerAdapter(context, data)
            .setTextColor(binding.txtInput.textColors.defaultColor)
            .setTextSize(binding.txtInput.textSize)
            .setIsFilterKey(mIsFilterKey)
        setAdapter(mAdapter)
        return this
    }

    /**
     * 设置默认可选项集合
     *
     * @param data
     * @return
     */
    fun setItems(data: List<String>): EditSpinner {
        mAdapter = EditSpinnerAdapter(context, data.toMutableList())
            .setTextColor(binding.txtInput.textColors.defaultColor)
            .setTextSize(binding.txtInput.textSize)
            .setIsFilterKey(mIsFilterKey)
        setAdapter(mAdapter)
        return this
    }

    /**
     * 设置下拉框条目点击监听
     *
     * @param listener
     * @return
     */
    fun setOnItemClickListener(listener: AdapterView.OnItemClickListener?): EditSpinner {
        mOnItemClickListener = listener
        return this
    }

    /**
     * 设置默认内容
     *
     * @param text
     * @return
     */
    fun setText(text: String): EditSpinner {
        //可以传空字符串
        binding.txtInput.removeTextChangedListener(this)
        binding.txtInput.setText(text)
        binding.txtInput.addTextChangedListener(this)
        return this
    }

    /**
     * 设置输入框字体的颜色
     *
     * @param colors
     * @return
     */
    fun setTextColors(colors: ColorStateList?): EditSpinner {
        if (colors != null) {
            binding.txtInput.setTextColor(colors)
            if (mAdapter != null && mAdapter is EditSpinnerAdapter) {
                (mAdapter as EditSpinnerAdapter).setTextColor(colors.defaultColor)
            }
        }
        return this
    }

    /**
     * 设置输入框字体的颜色
     *
     * @param color
     * @return
     */
    fun setTextColor(@ColorInt color: Int): EditSpinner {
        binding.txtInput.setTextColor(color)
        if (mAdapter != null && mAdapter is EditSpinnerAdapter) {
            (mAdapter as EditSpinnerAdapter).setTextColor(color)
        }
        return this
    }

    /**
     * 设置输入框的背景颜色
     *
     * @param backgroundSelector
     * @return
     */
    fun setBackgroundSelector(@DrawableRes backgroundSelector: Int): EditSpinner {
        binding.txtInput.setBackgroundResource(backgroundSelector)
        return this
    }

    /**
     * 设置是否显示key为醒目的颜色
     *
     * @param isFilterKey
     * @return
     */
    fun setIsFilterKey(isFilterKey: Boolean): EditSpinner {
        if (mAdapter != null && mAdapter is EditSpinnerAdapter) {
            (mAdapter as EditSpinnerAdapter).setIsFilterKey(isFilterKey)
        }
        return this
    }

    /**
     * 设置输入框的字体大小
     *
     * @param textSize
     * @return
     */
    fun setTextSize(textSize: Float): EditSpinner {
        binding.txtInput.setTextSize(TypedValue.COMPLEX_UNIT_PX, textSize)
        if (mAdapter != null && mAdapter is EditSpinnerAdapter) {
            (mAdapter as EditSpinnerAdapter).setTextSize(textSize)
        }
        return this
    }

    /**
     * 设置输入框的提示信息
     *
     * @param hint
     * @return
     */
    fun setHint(hint: String?): EditSpinner {
        binding.txtInput.hint = hint
        return this
    }

    fun setArrowImageDrawable(drawable: Drawable?): EditSpinner {
        binding.btnExpand.setImageDrawable(drawable)
        return this
    }

    /**
     * 设置箭头图片
     *
     * @param res
     * @return
     */
    fun setArrowImageResource(@DrawableRes res: Int): EditSpinner {
        binding.btnExpand.setImageResource(res)
        return this
    }

    /**
     * 设置下拉框适配器
     *
     * @param adapter
     * @return
     */
    fun setAdapter(adapter: BaseEditSpinnerAdapter?): EditSpinner {
        mAdapter = adapter
        setBaseAdapter(mAdapter)
        return this
    }

    /**
     * 设置输入框最大的行数
     *
     * @param maxLine
     * @return
     */
    fun setMaxLine(maxLine: Int): EditSpinner {
        mMaxLine = maxLine
        binding.txtInput.maxLines = mMaxLine
        return this
    }

    /**
     * 设置输入框的高度
     *
     * @param dp
     * @return
     */
    fun setEditTextHeight(dp: Int): EditSpinner {
        //            binding.txtInput.layoutParams =
//                LayoutParams(
//                    ViewGroup.LayoutParams.MATCH_PARENT, DensityUtils.dp2px(
//                        context, dp
//                    )
//                )
        return this
    }

    /**
     * 设置输入框的宽度
     *
     * @param dp
     * @return
     */
    fun setEditTextWidth(dp: Int): EditSpinner {
        val params = layoutParams
//        params.width = DensityUtils.dp2px(context, dp)
        layoutParams = params
        return this
    }

    /**
     * 设置enable
     *
     * @param enabled
     * @return
     */
    override fun setEnabled(enabled: Boolean) {
        binding.txtInput.isFocusable = enabled
        binding.txtInput.isFocusableInTouchMode = enabled
        binding.txtInput.isEnabled = enabled
        binding.btnExpand.isEnabled = enabled
    }

    /**
     * 设置输入框的最大字符长度
     *
     * @param maxLength
     * @return
     */
    fun setMaxLength(maxLength: Int): EditSpinner {
        if (maxLength > 0) {
            val filters = arrayOf<InputFilter>(LengthFilter(maxLength))
            binding.txtInput.filters = filters
        }
        return this
    }

    /**
     * 设置输入框的最大字符宽度
     *
     * @param maxEms
     * @return
     */
    fun setMaxEms(maxEms: Int): EditSpinner {
        if (maxEms > 0) {
            binding.txtInput.maxEms = maxEms
        }
        return this
    }

    /**
     * 增加文字监听
     *
     * @param watcher
     * @return
     */
    fun addTextChangedListener(watcher: TextWatcher?): EditSpinner {
        binding.txtInput.addTextChangedListener(watcher)
        return this
    }

    /**
     * 设置输入的类型
     *
     * @param type
     * @return
     */
    fun setInputType(type: Int): EditSpinner {
        binding.txtInput.inputType = type
        return this
    }

    companion object {
        private const val DEFAULT_MAX_LINE = 1
        private const val TOGGLE_POPUP_WINDOW_INTERVAL = 200
    }

    init {
        initView(context)
        initAnimation()
    }
}