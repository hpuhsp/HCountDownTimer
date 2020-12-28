package com.reesehu.hcountdowntimer

import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import androidx.appcompat.app.AppCompatActivity
import com.google.android.material.snackbar.Snackbar
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.content_main.*

/**
 * 仿日历微型翻页效果的数字倒计时器实现
 */
class MainActivity : AppCompatActivity() {
//
//    private val mNumCdView by lazy {
//        findViewById<NumberCDView>(R.id.num_cd_view)
//    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        setSupportActionBar(toolbar)

        fab.setOnClickListener { view ->
            Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                .setAction("Action", null).show()
        }
        bit_flip.flip(0) // 设置默认为0
        btn_test.setOnClickListener {
            //            var string = et_seconds.text
//            var number = Integer.parseInt(string?.toString())
            bit_flip.smoothFlip(1, false)
        }
//
//        mNumCdView.setCountDownValues(43203000)
//        mNumCdView.start()
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        // Inflate the menu; this adds items to the action bar if it is present.
        menuInflater.inflate(R.menu.menu_main, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        return when (item.itemId) {
            R.id.action_settings -> true
            else -> super.onOptionsItemSelected(item)
        }
    }
}
