package com.example.floatplayer

import android.Manifest
import android.content.Intent
import android.graphics.PixelFormat
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.provider.Settings
import android.util.TypedValue
import android.view.Gravity
import android.view.WindowManager
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import com.google.android.material.imageview.ShapeableImageView
import kotlinx.android.synthetic.main.activity_main.*


open class MainActivity : AppCompatActivity() {

    private val mLauncherPermission =
        registerForActivityResult(ActivityResultContracts.RequestPermission()) {
            if (it) {
                initFloatPlayer()
            } else {
                toast(R.string.please_grant_permission)
            }
        }
    private val mLauncherOverlay = registerForActivityResult(
        ActivityResultContracts.StartActivityForResult()
    ) {
        if (it.resultCode == RESULT_OK) {
            toast("数据OK")
        } else {
            toast("数据问题")
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        initView()
    }

    private fun initView() {

        btnInit.setOnClickListener {
//            initContactDialog()
            initFloatPlayer()
//           requestPermission()
        }

        btnNewPage.setOnClickListener {
            toast("打开新页面")
            startActivity(Intent(this, FirstActivity::class.java))
        }
    }

    private fun requestPermission() {

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (Settings.canDrawOverlays(this)) {
                initFloatPlayer()
            } else {
                requestOverlayPermission()
            }
        } else {
            toast("系统版本低于23")
        }
        mLauncherPermission.launch(Manifest.permission.SYSTEM_ALERT_WINDOW)
    }

    //请求悬浮窗权限
    @RequiresApi(Build.VERSION_CODES.M)
    private fun requestOverlayPermission() {

        val intent = Intent(Settings.ACTION_MANAGE_OVERLAY_PERMISSION)
        intent.data = Uri.parse("package:$packageName")
        mLauncherOverlay.launch(intent)
    }

    //
    private fun initContactDialog() {

        //TODO
        val mContactUtil = ContactWindowUtil(this)
        mContactUtil.setDialogListener {
            toast(it)
        }
        mContactUtil.showContactView()
    }

    private fun initFloatPlayer() {

        val layoutParam = WindowManager.LayoutParams()
        layoutParam.width = WindowManager.LayoutParams.WRAP_CONTENT
        layoutParam.height = WindowManager.LayoutParams.WRAP_CONTENT
        //弹窗层级
        layoutParam.type = WindowManager.LayoutParams.TYPE_APPLICATION
        layoutParam.gravity = Gravity.START or Gravity.BOTTOM
        //背景透明
        layoutParam.format = PixelFormat.TRANSPARENT
        //可以点击外部区域
        layoutParam.flags = WindowManager.LayoutParams.FLAG_NOT_TOUCH_MODAL
        layoutParam.x =
            TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 24f, resources.displayMetrics)
                .toInt()
        layoutParam.y =
            TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 80f, resources.displayMetrics)
                .toInt()

        val mViewFloat = layoutInflater.inflate(R.layout.float_player_view, null)
        mViewFloat.findViewById<ShapeableImageView>(R.id.sivPlayerCover).setOnClickListener {
            toast("播放器图片")
        }

        windowManager.addView(mViewFloat, layoutParam)
    }

    private fun toast(resId: Int) {
        toast(getString(resId))
    }

    private fun toast(message: String) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show()
    }
}