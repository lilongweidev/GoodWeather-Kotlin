package com.kotlin.weather.ui

import BaseActivity
import android.annotation.SuppressLint
import android.os.Bundle
import android.text.Editable
import android.text.TextUtils
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.view.View.OnFocusChangeListener
import android.view.inputmethod.EditorInfo
import android.widget.ArrayAdapter
import android.widget.AutoCompleteTextView
import android.widget.LinearLayout
import android.widget.TextView
import androidx.lifecycle.ViewModelProviders
import androidx.recyclerview.widget.LinearLayoutManager
import com.kotlin.library.util.*
import com.kotlin.library.util.Constant.BOTTOM
import com.kotlin.library.view.dialog.AlertDialog
import com.kotlin.library.view.flowlayout.FlowLayout
import com.kotlin.library.view.flowlayout.RecordsDao
import com.kotlin.library.view.flowlayout.TagAdapter
import com.kotlin.weather.R
import com.kotlin.weather.adapter.SearchCityAdapter
import com.kotlin.weather.eventbus.SearchCityEvent
import com.kotlin.weather.model.LocationBean
import com.kotlin.weather.utils.SpeechUtil
import com.kotlin.weather.viewmodel.SearchCityViewModel
import io.reactivex.Observable
import io.reactivex.ObservableEmitter
import io.reactivex.ObservableOnSubscribe
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers.io
import kotlinx.android.synthetic.main.activity_search_city.*
import org.greenrobot.eventbus.EventBus
import java.util.*


/**
 * 搜索城市
 *
 * @author llw
 * @date 2021/4/27 14:24
 */
class SearchCityActivity : BaseActivity(), View.OnClickListener {

    private val ALL_RECORD = "all"

    /**
     * V7数据源
     */
    var mList: MutableList<LocationBean> = ArrayList()

    /**
     * 适配器
     */
    var mAdapter: SearchCityAdapter? = null

    /**
     * 记录条数
     */
    private val RECORD_NUM = 50

    private var mRecordsDao: RecordsDao? = null

    //默然展示词条个数
    private val DEFAULT_RECORD_NUMBER = 10
    private var recordList: MutableList<String?> = ArrayList()
    private var mRecordsAdapter: TagAdapter<String>? = null
    private val mHistoryContent: LinearLayout? = null

    /**
     * 提示弹窗
     */
    private var tipDialog: AlertDialog? = null

    private val searchCityViewModel by lazy {
        ViewModelProviders.of(this).get(SearchCityViewModel::class.java)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_search_city)
        //白色状态栏
        StatusBarUtil.setStatusBarColor(context, R.color.white)
        //黑色字体
        StatusBarUtil.StatusBarLightMode(context)
        back(toolbar)

        initView() //初始化页面数据

        initAutoComplete("history", editQuery)
        //初始化语音播报
        SpeechUtil.init(this)
    }

    private fun initView() {

        ivClearSearch.setOnClickListener(this)
        clearAllRecords.setOnClickListener(this)
        voiceSearch.setOnClickListener(this)
        ivArrow.setOnClickListener(this)

        //默认账号
        val username = "007"
        //初始化数据库
        mRecordsDao = RecordsDao(this, username)
        initTagFlowLayout()

        //创建历史标签适配器
        //为标签设置对应的内容
        mRecordsAdapter = object : TagAdapter<String>(recordList) {
            override fun getView(
                parent: FlowLayout?,
                position: Int,
                s: String?
            ): View? {
                val tv = LayoutInflater.from(context).inflate(
                    R.layout.tv_history,
                    flSearchRecords, false
                ) as TextView
                //为标签设置对应的内容
                tv.text = s
                return tv
            }
        }
        //添加输入监听
        editQuery.addTextChangedListener(textWatcher)
        editQuery.setOnEditorActionListener { _, actionId, _ ->
            if (actionId == EditorInfo.IME_ACTION_SEARCH) {
                val location = editQuery.text.toString()
                if (!TextUtils.isEmpty(location)) {
                    //搜索城市
                    searchCity(location)
                } else {
                    "请输入搜索关键词".showToast()
                }
            }
            false
        }
        flSearchRecords.adapter = mRecordsAdapter
        flSearchRecords.setOnTagClickListener { _, position, _ -> //清空editText之前的数据
            editQuery.setText("")
            //将获取到的字符串传到搜索结果界面,点击后搜索对应条目内容
            editQuery.setText(recordList[position])
            editQuery.setSelection(editQuery.length())
        }
        //长按删除某个条目
        flSearchRecords.setOnLongClickListener { view, position ->
            showTipDialog(
                position,
                "确定要删除该条历史记录？"
            )
        }

        //view加载完成时回调
        flSearchRecords.viewTreeObserver
            .addOnGlobalLayoutListener {
                val isOverFlow = flSearchRecords.isOverFlow
                val isLimit = flSearchRecords.isLimit
                if (isLimit && isOverFlow) {
                    ivArrow.visibility = View.VISIBLE
                } else {
                    ivArrow.visibility = View.GONE
                }
            }

        //初始化搜索返回的数据列表
        mAdapter = SearchCityAdapter(R.layout.item_search_city_list, mList)
        rv.layoutManager = LinearLayoutManager(context)
        rv.adapter = mAdapter
        mAdapter!!.setOnItemChildClickListener { _, _, position ->
            mList[position].name.putString(Constant.LOCATION)
            //发送消息
            EventBus.getDefault()
                .post(SearchCityEvent(mList[position].name, mList[position].adm2)) //Adm2 代表市
            finish()
        }

    }

    /**
     * 搜索城市
     */
    private fun searchCity(location: String) {
        showLoadingDialog()
        //添加数据
        mRecordsDao!!.addRecords(location)

        searchCityViewModel.apply {
            //搜索城市
            searchCity(location)
            searchCityLiveData.observe(
                this@SearchCityActivity,
                androidx.lifecycle.Observer { result ->
                    val searchCityResponse = result.getOrNull()
                    if (searchCityResponse != null) {
                        locationBean.clear()
                        locationBean += searchCityResponse.location
                        mList.clear()
                        mList.addAll(locationBean)
                        mAdapter!!.notifyDataSetChanged()
                        rv.showAnimation(BOTTOM)
                    } else {
                        "搜索不到该城市".showToast()
                    }
                    dismissLoadingDialog()
                })

        }
        //数据保存
        saveHistory("history", editQuery)
    }

    /**
     * 历史记录布局
     */
    @SuppressLint("CheckResult")
    private fun initTagFlowLayout() {
        Observable.create(
            ObservableOnSubscribe { emitter: ObservableEmitter<List<String?>> ->
                emitter.onNext(mRecordsDao!!.getRecordsByNumber(DEFAULT_RECORD_NUMBER))
            } as ObservableOnSubscribe<List<String?>>
        ).subscribeOn(io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe { s: List<String?> ->
                recordList.clear()
                recordList = s.toMutableList()

                llHistoryContent.visibility = if (recordList.size == 0) View.GONE else View.VISIBLE

                if (mRecordsAdapter != null) {
                    mRecordsAdapter!!.setData(recordList)
                    mRecordsAdapter!!.notifyDataChanged()
                }
            }
    }


    /**
     * 使 AutoCompleteTextView在一开始获得焦点时自动提示
     *
     * @param field                保存在sharedPreference中的字段名
     * @param autoCompleteTextView 要操作的AutoCompleteTextView
     */
    private fun initAutoComplete(
        field: String,
        autoCompleteTextView: AutoCompleteTextView
    ) {
        val sp = getSharedPreferences("sp_history", 0)
        //获取缓存
        val etHistory = sp.getString("history", "深圳")
        //通过,号分割成String数组
        val histories: Array<String?> = etHistory!!.split(",").toTypedArray()
        var adapter =
            ArrayAdapter(this, R.layout.item_tv_history, histories)

        // 只保留最近的50条的记录
        if (histories.size > RECORD_NUM) {
            val newHistories = arrayOfNulls<String>(50)
            System.arraycopy(histories, 0, newHistories, 0, 50)
            adapter = ArrayAdapter(this, R.layout.item_tv_history, newHistories)
        }
        //AutoCompleteTextView可以直接设置数据适配器，并且在获得焦点的时候弹出，
        //通常是在用户第一次进入页面的时候，点击输入框输入的时候出现，如果每次都出现
        //是会应用用户体验的，这里不推荐这么做
        autoCompleteTextView.setAdapter(adapter)
        autoCompleteTextView.onFocusChangeListener =
            OnFocusChangeListener { v, hasFocus ->
                val view = v as AutoCompleteTextView
                if (hasFocus) { //出现历史输入记录
                    view.showDropDown()
                }
            }
    }


    /**
     * 把指定AutoCompleteTextView中内容保存到sharedPreference中指定的字符段
     * 每次输入完之后调用此方法保存输入的值到缓存里
     *
     * @param field                保存在sharedPreference中的字段名
     * @param autoCompleteTextView 要操作的AutoCompleteTextView
     */
    private fun saveHistory(
        field: String,
        autoCompleteTextView: AutoCompleteTextView
    ) {

        //输入的值
        val text = autoCompleteTextView.text.toString()
        val sp = getSharedPreferences("sp_history", 0)
        val tvHistory = sp.getString(field, "深圳")

        //如果历史缓存中不存在输入的值则
        if (!tvHistory!!.contains("$text,")) {
            val sb = StringBuilder(tvHistory)
            sb.insert(0, "$text,")
            //写入缓存
            sp.edit().putString("history", sb.toString()).apply()
        }
    }

    /**
     * 显示提示弹窗
     *
     * @param data    数据
     * @param content 内容
     */
    private fun showTipDialog(data: Any, content: String) {
        val builder = AlertDialog.Builder(context)
            .addDefaultAnimation()
            .setCancelable(true)
            .setContentView(R.layout.dialog_tip)
            .setWidthAndHeight(
                SizeUtils.dp2px(context, 270F),
                LinearLayout.LayoutParams.WRAP_CONTENT
            )
            .setText(R.id.tv_content, content)
            .setOnClickListener(R.id.tv_cancel, View.OnClickListener {
                tipDialog!!.dismiss()
            }).setOnClickListener(R.id.tv_sure, View.OnClickListener {
                //传入all则删除所有
                if (ALL_RECORD == data) {
                    flSearchRecords.isLimit = true
                    //清除所有数据
                    mRecordsDao!!.deleteUsernameAllRecords()
                    llHistoryContent.visibility = View.GONE
                } else {
                    //删除某一条记录  传入单个的position
                    mRecordsDao!!.deleteRecord(recordList[(data as Int)])
                    initTagFlowLayout()
                }
                tipDialog!!.dismiss()
            })
        tipDialog = builder.create()
        tipDialog!!.show()
    }

    /**
     * 输入监听
     */
    private val textWatcher: TextWatcher = object : TextWatcher {
        override fun beforeTextChanged(s: CharSequence, start: Int, count: Int, after: Int) {}

        override fun onTextChanged(s: CharSequence, start: Int, before: Int, count: Int) {}

        override fun afterTextChanged(s: Editable) {
            ivClearSearch.visibility = if ("" != s.toString()) View.VISIBLE else View.GONE
        }
    }

    /**
     * 页面点击事件
     */
    override fun onClick(v: View) {
        when (v.id) {
            R.id.ivClearSearch -> {
                ivClearSearch.visibility = View.GONE
                editQuery.setText("")
            }
            R.id.clearAllRecords -> {
                showTipDialog("all", "确定要删除全部历史记录？")
            }
            R.id.voiceSearch -> {
                SpeechUtil.startDictation(object : SpeechUtil.SpeechCallback {
                    override fun dictationResults(cityName: String?) {
                        //判断字符串是否包含句号
                        if (!cityName!!.contains("。")) {
                            editQuery.setText(cityName)
                            //搜索城市
                            searchCity(cityName)
                        }
                    }
                })
            }
            R.id.ivArrow -> {
                flSearchRecords.isLimit = false
                mRecordsAdapter!!.notifyDataChanged()
            }
        }
    }
}