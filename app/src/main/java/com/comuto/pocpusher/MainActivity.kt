package com.comuto.pocpusher

import android.content.Intent
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import kotlinx.android.synthetic.main.activity_main.*

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        button.setOnClickListener {
            val intent = Intent(this, ChatActivity::class.java)
            intent.putExtra("name", editText.text.toString())
            intent.putExtra("room", editText2.text.toString())
            intent.putExtra("create", switch1.isChecked)
            startActivity(intent)
        }
    }
}
