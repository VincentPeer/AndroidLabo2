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
import java.text.DateFormat
import java.text.SimpleDateFormat
import java.util.*



class MainActivity : AppCompatActivity() {


    private lateinit var customDatePickerBuilder: CustomDatePickerBuilder

    private lateinit var personType: String
    private lateinit var person: Person
    private var nationality = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        // Date selection
        customDatePickerBuilder = CustomDatePickerBuilder(
            MaterialDatePicker.Builder.datePicker(),
            findViewById(R.id.main_base_birthdate))

        findViewById<ImageButton>(R.id.cake_button).setOnClickListener {
            customDatePickerBuilder.getPicker().show(supportFragmentManager, null)
        }

        // Nationality selection
        val spinner = findViewById<Spinner>(R.id.nationality_spinner)
        spinner.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(parent: AdapterView<*>, view: View?, pos: Int, id: Long) {
                if(pos == 0)
                    return
                nationality = parent.getItemAtPosition(pos).toString()
                // An item was selected. You can retrieve the selected item using
            }

            override fun onNothingSelected(parent: AdapterView<*>) {
            }
        }

        // User type selection
        val radioGroup = findViewById<RadioGroup>(R.id.radio_group)
        radioGroup.setOnCheckedChangeListener { _, choiceId ->
            manageUserType(choiceId)
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
            val university: String = findViewById<EditText>(R.id.main_specific_school).text.toString()
            val graduationYear: Int = findViewById<EditText>(R.id.main_specific_graduationyear).text.toString().toInt()
            person =  Student(name, firstName,birthday, this.nationality, university, graduationYear, email, remark)
            System.out.println(person.toString())
        }
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

    private class CustomDatePickerBuilder(
        val matDatePicker: MaterialDatePicker.Builder<Long>,
        val dateField: EditText
        ) {
        private val dateFormat: String = "dd MMMM yyyy"
        private val maxAge = 80
        private val minAge = 15
        private val today = MaterialDatePicker.todayInUtcMilliseconds()

        private var constraints = CalendarConstraints.Builder()

        fun getPicker(): MaterialDatePicker<Long> {

            val openAt: Calendar
            val sdf = SimpleDateFormat(dateFormat, Locale.US)
            if (!dateField.text.isEmpty()) {
                sdf.parse(dateField.text.toString())
                openAt = sdf.calendar
            } else {
                openAt = endDate()
            }

            constraints
                .setOpenAt(openAt.timeInMillis)
                .setStart(startDate().timeInMillis)
                .setEnd(endDate().timeInMillis)

            val picker = matDatePicker
                .setCalendarConstraints(constraints.build())
                .build()

            picker.addOnPositiveButtonClickListener {
                val parser = SimpleDateFormat(dateFormat, Locale.getDefault())
                val date = parser.format(it)
                dateField.setText(date.toString())
            }

            return picker
        }

        private fun startDate(): Calendar {
            val out = Calendar.getInstance(TimeZone.getTimeZone("UTC"))
            out.timeInMillis = today
            out[Calendar.YEAR] = out[Calendar.YEAR] - maxAge
            return out
        }

        private fun endDate(): Calendar {
            val out = Calendar.getInstance(TimeZone.getTimeZone("UTC"))
            out.timeInMillis = today
            out[Calendar.YEAR] = out[Calendar.YEAR] - minAge
            return out
        }
    }
}