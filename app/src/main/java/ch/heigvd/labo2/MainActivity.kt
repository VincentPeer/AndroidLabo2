package ch.heigvd.labo2

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.ArrayAdapter
import android.widget.RadioGroup
import android.widget.Spinner
import android.widget.TextView
import androidx.constraintlayout.widget.ConstraintLayout
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

        val additionnalData = findViewById<TextView>(R.id.additional_title)
        val additionnalDataParams = additionnalData.layoutParams as ConstraintLayout.LayoutParams
        val studentGroup = findViewById<Group>(R.id.student_group)
        val workerGroup = findViewById<Group>(R.id.worker_group)
        val radioGroup = findViewById<RadioGroup>(R.id.radio_group)
        radioGroup.setOnCheckedChangeListener { _, choiceId ->
            when (choiceId) {
                R.id.student_choice -> {
                    studentGroup.visibility = View.VISIBLE
                    workerGroup.visibility = View.GONE
                    val graduationYear = findViewById<TextView>(R.id.main_specific_graduationyear_title)
                    additionnalDataParams.topToBottom = graduationYear.id
                }
                R.id.worker_choice -> {
                    workerGroup.visibility = View.VISIBLE
                    studentGroup.visibility = View.GONE
                    val experienceYear = findViewById<TextView>(R.id.main_specific_experience_title)
                    additionnalDataParams.topToBottom = experienceYear.id
                }
            }
        }
    }
}