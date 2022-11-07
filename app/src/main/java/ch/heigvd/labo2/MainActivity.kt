package ch.heigvd.labo2

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.ArrayAdapter
import android.widget.RadioGroup
import android.widget.Spinner
import androidx.constraintlayout.widget.Group

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        // Nationalities management
        val spinner = findViewById<Spinner>(R.id.spinner)

        // https://developer.android.com/develop/ui/views/components/spinner
        // Create an ArrayAdapter using the string array and a default spinner layout
        ArrayAdapter.createFromResource(
            this,
            R.array.nationalities,
            android.R.layout.simple_spinner_item
        ).also { adapter ->
            // Specify the layout to use when the list of choices appears
            adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
            // Apply the adapter to the spinner
            spinner.adapter = adapter
        }

        val studentGroup = findViewById<Group>(R.id.student_group)
        val workerGroup = findViewById<Group>(R.id.worker_group)
        val radioGroup = findViewById<RadioGroup>(R.id.radio_group)
        radioGroup.setOnCheckedChangeListener { _, choiceId ->
            when (choiceId) {
                R.id.student_choice -> {
                    studentGroup.visibility = View.VISIBLE
                }
                R.id.worker_choice -> {
                    workerGroup.visibility = View.VISIBLE
                }
            }
        }
    }
}