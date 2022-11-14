package ch.heigvd.labo2

import android.os.Bundle
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import androidx.constraintlayout.widget.Group
import ch.heigvd.labo2.Model.Person
import ch.heigvd.labo2.Model.Student
import ch.heigvd.labo2.Model.Worker
import com.google.android.material.datepicker.CalendarConstraints
import com.google.android.material.datepicker.MaterialDatePicker
import java.text.SimpleDateFormat
import java.util.*


class MainActivity : AppCompatActivity() {
    private lateinit var personType: String
    private lateinit var person: Person
    private var nationality = ""
    private var sector = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        // Date selection
        val date = findViewById<ImageButton>(R.id.cake_button)
        date.setOnClickListener {
            manageCalendar()
        }

        // Nationality selection
        val nationalitySpinner = findViewById<Spinner>(R.id.nationality_spinner)
        nationalitySpinner.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(parent: AdapterView<*>, view: View?, pos: Int, id: Long) {
                if(pos == 0)
                    return
                nationality = parent.getItemAtPosition(pos).toString()
            }
            override fun onNothingSelected(parent: AdapterView<*>) {
            }
        }

        // User type selection
        val radioGroup = findViewById<RadioGroup>(R.id.radio_group)
        radioGroup.setOnCheckedChangeListener { _, choiceId ->
            manageUserType(choiceId)
        }

        // Worker sector selection
        val sectorSpinner = findViewById<Spinner>(R.id.sector_spinner)
        sectorSpinner.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(parent: AdapterView<*>, view: View?, pos: Int, id: Long) {
                if(pos == 0)
                    return
                sector = parent.getItemAtPosition(pos).toString()
            }
            override fun onNothingSelected(parent: AdapterView<*>) {
            }
        }

        // Delete every field when cancel button is applied
        val btnCancel = findViewById<Button>(R.id.btn_cancel)
        val parent = btnCancel.parent
        btnCancel.setOnClickListener {
            clearForm(parent as ViewGroup)
        }

        // Create a new person from written data when ok button ok is applied
        val btnOk = findViewById<Button>(R.id.btn_ok)
        btnOk.setOnClickListener {
            addNewPerson()
        }
    }


    /**
     *
     */
    private fun addNewPerson() {
        val name: String = findViewById<EditText>(R.id.main_base_name).text.toString()
        val firstName: String = findViewById<EditText>(R.id.main_base_firstname).text.toString()
        //val birthday: Calendar = findViewById<EditText>(R.id.main_base_firstname).text.toString()
        val birthday: Calendar = Calendar.getInstance().apply {
            set(Calendar.YEAR, 1996)
            set(Calendar.MONTH, Calendar.JUNE)
            set(Calendar.DAY_OF_MONTH, 12) }
        val email: String = findViewById<EditText>(R.id.additional_mail).text.toString()
        val remark: String = findViewById<EditText>(R.id.additional_remarks).text.toString()
        if(personType == "student") {
            val university = findViewById<EditText>(R.id.main_specific_school).text.toString()
            val graduationYear = findViewById<EditText>(R.id.main_specific_graduationyear).text.toString().toInt()
            person =  Student(name, firstName,birthday, this.nationality, university, graduationYear, email, remark)
        } else {
            val company = findViewById<EditText>(R.id.main_specific_compagny).text.toString()
            val experience = findViewById<EditText>(R.id.main_specific_experience).text.toString().toInt()
            person = Worker(name, firstName, birthday, nationality, company, sector, experience, email, remark)
        }
        println(person)
    }

    /**
     *
     */
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
     *
     */
    private fun manageCalendar() {
        val constraintsBuilder = CalendarConstraints.Builder()
        val datePicker: MaterialDatePicker<Long> = MaterialDatePicker()
        constraintsBuilder.setEnd(MaterialDatePicker.thisMonthInUtcMilliseconds())
        MaterialDatePicker.Builder.datePicker().setSelection(MaterialDatePicker.todayInUtcMilliseconds()).build()
            .show(supportFragmentManager, "materialDatePicker")
        datePicker.addOnPositiveButtonClickListener {
            val sdf = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault())
            val date = sdf.format(it)
            println(date)
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
            this.personType = "student"
        } else {
            workerGroup.visibility = View.VISIBLE
            studentGroup.visibility = View.GONE
            this.personType = "worker"
        }
    }
}

