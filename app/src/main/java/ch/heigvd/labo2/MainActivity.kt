package ch.heigvd.labo2

import android.os.Bundle
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import androidx.constraintlayout.widget.Group


class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        // Nationalities management
        val spinner = findViewById<Spinner>(R.id.nationality_spinner)

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


        manageUserType()


        val btn_cancel = findViewById<Button>(R.id.btn_cancel)
        val parent = btn_cancel.parent
        btn_cancel.setOnClickListener {
            clearForm(parent as ViewGroup)
        }
    }

    private fun clearForm(group: ViewGroup) {
        var i = 0
        val count = group.childCount
        while (i < count) {
            val view = group.getChildAt(i)
            if (view is EditText) {
                view.setText("")
            } else if(view is Spinner) {
                view.setSelection(0) // Reset to "selection" state
            }
            ++i
        }
    }

    /**
     * Managing specific data whether the user is a student or a worker
     */
    private fun manageUserType() {
        val studentGroup = findViewById<Group>(R.id.student_group)
        val workerGroup = findViewById<Group>(R.id.worker_group)
        val radioGroup = findViewById<RadioGroup>(R.id.radio_group)
        radioGroup.setOnCheckedChangeListener { _, choiceId ->
            if(choiceId == R.id.student_choice) {
                studentGroup.visibility = View.VISIBLE
                workerGroup.visibility = View.GONE
            } else {
                workerGroup.visibility = View.VISIBLE
                studentGroup.visibility = View.GONE
            }
        }
    }

}