package com.example.floatplayer

import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import kotlinx.android.synthetic.main.activity_main.*


open class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        initView()
    }

    override fun onResume() {
        super.onResume()
        FloatPlayer.getInstance().show(this@MainActivity)
    }

    override fun onPause() {
        super.onPause()
        FloatPlayer.getInstance().dismiss(this)
    }

    override fun onDestroy() {
        super.onDestroy()
        FloatPlayer.getInstance().close()
    }

    private fun initView() {

        btnNewPage.setOnClickListener {
            startActivity(Intent(this, FirstActivity::class.java))
        }

        btnOpenPlayer.setOnClickListener {
            FloatPlayer.getInstance().open(this)
        }
    }

    private fun toast(resId: Int) {
        toast(getString(resId))
    }

    private fun toast(message: String) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show()
    }
}