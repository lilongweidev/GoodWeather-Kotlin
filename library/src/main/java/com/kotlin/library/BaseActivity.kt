
import android.app.Activity
import android.app.Dialog
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.kotlin.library.R
import com.kotlin.library.util.LogD

/**
 * 基类Activity
 */
open class BaseActivity : AppCompatActivity() {

    //加载弹窗
    private var mDialog: Dialog? = null

    companion object{
        //上下文
        lateinit var context: Activity
    }

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
        mDialog!!.setContentView(R.layout.dialog_loading)
        mDialog!!.setCancelable(false)
        mDialog!!.window!!.setBackgroundDrawableResource(R.color.transparent)
        mDialog!!.show()
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

}