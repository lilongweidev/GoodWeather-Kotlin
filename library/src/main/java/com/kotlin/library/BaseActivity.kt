import android.app.Activity
import android.app.Dialog
import android.app.ProgressDialog.show
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import com.kotlin.library.R
import com.kotlin.library.util.LogD

/**
 * 基类Activity
 */
open class BaseActivity : AppCompatActivity() {

    //加载弹窗
    private var mDialog: Dialog? = null

    companion object {
        //上下文
        lateinit var context: Activity
    }

    //快速点击延迟时间
    private val FAST_CLICK_DELAY_TIME = 500

    //最后点击时间
    private var lastClickTime: Long = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        javaClass.simpleName.LogD("BaseActivity")
        ActivityManager.addActivity(this)
        context = this
    }

    override fun onDestroy() {
        super.onDestroy()
        ActivityManager.removeActivity(this)
    }

    /**
     * 弹窗出现
     */
    protected fun showLoadingDialog() {
        if (mDialog == null) {
            mDialog = Dialog(context, R.style.loading_dialog)
        }

        mDialog?.apply {
            setContentView(R.layout.dialog_loading)
            setCancelable(false)
            window?.setBackgroundDrawableResource(R.color.transparent)
        }
        if(!context.isFinishing){
            mDialog!!.show()
        }
    }

    /**
     * 弹窗消失
     */
    protected fun dismissLoadingDialog() {
        if (mDialog != null) {
            mDialog!!.dismiss()
        }
        mDialog = null
    }

    /**
     * 返回
     *
     * @param toolbar
     */
    protected open fun back(toolbar: Toolbar) {
        toolbar.setNavigationOnClickListener {
            context.finish()
            if (!isFastClick()) {
                context.finish()
            }
        }
    }

    /**
     * 两次点击间隔不能少于500ms
     *
     * @return flag
     */
    protected open fun isFastClick(): Boolean {
        var flag = true
        val currentClickTime = System.currentTimeMillis()
        if (currentClickTime - lastClickTime >= FAST_CLICK_DELAY_TIME) {
            flag = false
        }
        lastClickTime = currentClickTime
        return flag
    }

}