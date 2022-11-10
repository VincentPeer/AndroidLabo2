package ch.heigvd.labo2

import android.os.Bundle
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import androidx.constraintlayout.widget.Group
import ch.heigvd.labo2.Model.Person
import ch.heigvd.labo2.Model.Student
import com.google.android.material.datepicker.CalendarConstraints
import com.google.android.material.datepicker.MaterialDatePicker
import java.util.*


class MainActivity : AppCompatActivity() {
    private var people = mutableListOf<Person>(Person.exampleStudent, Person.exampleWorker)
    private var personId: Int = -1

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val setBirthday = findViewById<ImageButton>(R.id.cake_button)
        setBirthday.setOnClickListener {

            val today = MaterialDatePicker.todayInUtcMilliseconds()
            val calendar = Calendar.getInstance(TimeZone.getTimeZone("UTC"))

            calendar.timeInMillis = today
            calendar[Calendar.DAY_OF_MONTH] = 1
            calendar[Calendar.MONTH] = Calendar.JANUARY
            calendar[Calendar.YEAR] = 1996
            val defaultDate = calendar.timeInMillis

            val constraintsBuilder =
                CalendarConstraints.Builder()
                    .setOpenAt(defaultDate)
            constraintsBuilder.build()
            MaterialDatePicker.Builder.datePicker().setSelection(defaultDate)
            .setCalendarConstraints(constraintsBuilder.build()).build().show(supportFragmentManager, "tag");
        }

        val radioGroup = findViewById<RadioGroup>(R.id.radio_group)
        radioGroup.setOnCheckedChangeListener { _, choiceId ->
            manageUserType(choiceId)
        }

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
        val name: String = findViewById<EditText>(R.id.main_base_name).text.toString()
        val firstName: String = findViewById<EditText>(R.id.main_base_firstname).text.toString()
        //val birthday: Calendar = findViewById<EditText>(R.id.main_base_firstname).text.toString()
        val birthday: Calendar = Calendar.getInstance().apply {
            set(Calendar.YEAR, 1996)
            set(Calendar.MONTH, Calendar.JUNE)
            set(Calendar.DAY_OF_MONTH, 12) }
        val nationality = getNationality()
        val email: String = findViewById<EditText>(R.id.additional_mail).text.toString()
        val remark: String = findViewById<EditText>(R.id.additional_remarks).text.toString()
        if(personId == findViewById<RadioButton>(R.id.student_group).id) {
            val university: String = findViewById<EditText>(R.id.main_specific_school).text.toString()
            val graduationYear: Int = findViewById<EditText>(R.id.main_specific_graduationyear).text.toString().toInt()
            val student =  Student(name, firstName,birthday, nationality, university, graduationYear, email, remark)
            people.add(student)
        }
    }


    // Nationalities management
    private fun getNationality() : String {
        val spinner = findViewById<Spinner>(R.id.nationality_spinner)
        var nationality: String = ""
        spinner.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(
                adapterView: AdapterView<*>?,
                view: View?,
                position: Int,
                id: Long
            ) {
                nationality = spinner.getItemAtPosition(position).toString()
            }

            override fun onNothingSelected(adapterView: AdapterView<*>?) {
                // Le Spinner ne permet pas de « désélectionner » une sélection
                // ce callback est appelé uniquement dans le cas ou l’option sélectionnée est retirée du Spinner
            }
        }
        return nationality
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
    private fun manageUserType(choiceId: Int) {
        val studentGroup = findViewById<Group>(R.id.student_group)
        val workerGroup = findViewById<Group>(R.id.worker_group)
        if(choiceId == R.id.student_choice) {
            studentGroup.visibility = View.VISIBLE
            workerGroup.visibility = View.GONE
        } else {
            workerGroup.visibility = View.VISIBLE
            studentGroup.visibility = View.GONE
        }
        this.personId = choiceId
    }
}
