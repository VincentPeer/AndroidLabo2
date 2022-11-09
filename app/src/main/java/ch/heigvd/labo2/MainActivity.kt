package ch.heigvd.labo2

import android.os.Bundle
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import androidx.constraintlayout.widget.Group
import ch.heigvd.labo2.Model.Person


class MainActivity : AppCompatActivity() {
    //val people: List<Person> = listOf<Person>()

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


        val btnCancel = findViewById<Button>(R.id.btn_cancel)
        val parent = btnCancel.parent
        btnCancel.setOnClickListener {
            clearForm(parent as ViewGroup)
        }

        val btnOk = findViewById<Button>(R.id.btn_ok)
        btnOk.setOnClickListener {
            addNewPerson()
        }
    }

    private fun addNewPerson() {
        val name: String = findViewById<EditText>(R.id.main_base_name).toString()
        System.out.println("name : $name")
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