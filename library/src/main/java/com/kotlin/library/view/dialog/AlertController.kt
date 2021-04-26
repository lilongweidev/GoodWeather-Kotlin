package com.kotlin.library.view.dialog

import android.content.Context
import android.content.DialogInterface
import android.graphics.Bitmap
import android.util.SparseArray
import android.view.Gravity
import android.view.View
import android.view.View.OnLongClickListener
import android.view.Window

/**
 * 弹窗控制
 * @author llw
 */
class AlertController(
    val dialog: AlertDialog,
    val window: Window
) {
    private var mViewHelper: DialogViewHelper? = null

    fun setDialogViewHelper(dialogViewHelper: DialogViewHelper?) {
        mViewHelper = dialogViewHelper
    }

    fun setText(viewId: Int, text: CharSequence?) {
        mViewHelper!!.setText<View>(viewId, text)
    }

    fun setIcon(viewId: Int, resId: Int) {
        mViewHelper!!.setIcon(viewId, resId)
    }

    fun <T : View> getView(viewId: Int): T? {
        return mViewHelper!!.getView(viewId)
    }

    fun setOnClickListener(
        viewId: Int,
        onClickListener: View.OnClickListener?
    ) {
        mViewHelper!!.setOnClickListener(viewId, onClickListener)
    }

    //-------------------------------------------------------------------------------------------------
    class AlertParams(
        var mContext: Context, //对话框主题背景
        var mThemeResId: Int
    ) {

        @JvmField
        var mCancelable = false
        @JvmField
        var mOnCancelListener: DialogInterface.OnCancelListener? = null
        @JvmField
        var mOnDismissListener: DialogInterface.OnDismissListener? = null
        @JvmField
        var mOnKeyListener: DialogInterface.OnKeyListener? = null

        //文本颜色
        @JvmField
        var mTextColorArray = SparseArray<Int>()

        //存放文本的更改
        @JvmField
        var mTextArray = SparseArray<CharSequence>()

        //存放点击事件
        @JvmField
        var mClickArray =
            SparseArray<View.OnClickListener>()

        //存放长按点击事件
        @JvmField
        var mLondClickArray = SparseArray<OnLongClickListener>()

        //存放对话框图标
        @JvmField
        var mIconArray = SparseArray<Int>()

        //存放对话框图片
        @JvmField
        var mBitmapArray = SparseArray<Bitmap>()

        //对话框布局资源id
        @JvmField
        var mLayoutResId = 0

        //对话框的view
        @JvmField
        var mView: View? = null

        //对话框宽度
        @JvmField
        var mWidth = 0

        //对话框高度
        @JvmField
        var mHeight = 0

        //对话框垂直外边距
        @JvmField
        var mHeightMargin = 0

        //对话框横向外边距
        @JvmField
        var mWidthMargin = 0

        //动画
        @JvmField
        var mAnimation = 0

        //对话框显示位置
        @JvmField
        var mGravity = Gravity.CENTER
        fun apply(alert: AlertController) {
            //设置对话框布局
            var dialogViewHelper: DialogViewHelper? = null
            if (mLayoutResId != 0) {
                dialogViewHelper = DialogViewHelper(mContext, mLayoutResId)
            }
            if (mView != null) {
                dialogViewHelper = DialogViewHelper()
                dialogViewHelper.contentView = mView
            }
            requireNotNull(dialogViewHelper) { "please set layout" }
            //将对话框布局设置到对话框
            alert.dialog.setContentView(dialogViewHelper.contentView!!)

            //设置DialogViewHelper辅助类
            alert.setDialogViewHelper(dialogViewHelper)
            //设置文本
            for (i in 0 until mTextArray.size()) {
                alert.setText(mTextArray.keyAt(i), mTextArray.valueAt(i))
            }
            //设置图标
            for (i in 0 until mIconArray.size()) {
                alert.setIcon(mIconArray.keyAt(i), mIconArray.valueAt(i))
            }
            //设置点击
            for (i in 0 until mClickArray.size()) {
                alert.setOnClickListener(mClickArray.keyAt(i), mClickArray.valueAt(i))
            }
            //配置自定义效果，底部弹出，宽高，动画，全屏
            val window = alert.window
            window.setGravity(mGravity) //显示位置
            if (mAnimation != 0) {
                window.setWindowAnimations(mAnimation) //设置动画
            }
            //设置宽高
            val params = window.attributes
            params.width = mWidth
            params.height = mHeight
            params.verticalMargin = mHeightMargin.toFloat()
            params.horizontalMargin = mWidthMargin.toFloat()
            window.attributes = params
        }

    }

}