package com.example.floatplayer

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.example.floatplayer.databinding.ActivityFirstBinding

class FirstActivity : BaseActivity() {

    private lateinit var mBinding: ActivityFirstBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        mBinding = ActivityFirstBinding.inflate(layoutInflater)
        setContentView(mBinding.root)

        initView()
    }

    private fun initView() {

        mBinding.btnShowToast.setOnClickListener {
            showMessage("点击了我")
        }
    }
}