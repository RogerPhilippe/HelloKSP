package br.com.phs.helloksp

import android.os.Bundle
import android.widget.Button
import androidx.appcompat.app.AppCompatActivity

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        findViewById<Button>(R.id.eventBtn)?.setOnClickListener {
            val model = TicketBookToClickedParams(
                "TicketBoockEvent",
                "EventTest",
                1234,
                "122-0"
            )
            EventUtils.postEvent(
                TicketBookToClickedParamsEvent(model)
            )
        }


    }
}