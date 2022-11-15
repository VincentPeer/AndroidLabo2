/**
 *  * DAA Laboratory 2
 * @author      : Damien Maier, Jean-François Pasche, Vincent Peer
 * Date         : 16.11.2022
 * Description  : Offers a form interface with multiple fields about user information. The user can
 *                be a student or a worker and has multiple field to edit concerning his personal data.
 */

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

const val DATE_FORMAT = "dd MMMM yyyy"

/**
 * Creates the form that need to be complete by a user, or print information from an existed user.
 */
class MainActivity : AppCompatActivity() {
    private lateinit var customDatePickerBuilder: CustomDatePickerBuilder
    private lateinit var personType: String
    private lateinit var person: Person
    private var nationality = ""
    private var sector = ""


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
        findViewById<Spinner>(R.id.nationality_spinner).onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(parent: AdapterView<*>, view: View?, pos: Int, id: Long) {
                if(pos == 0)
                    return
                nationality = parent.getItemAtPosition(pos).toString()
            }
            // Does nothing if no selection was done, but the function is required
            override fun onNothingSelected(parent: AdapterView<*>) {
            }
        }

        // User type selection
        findViewById<RadioGroup>(R.id.radio_group).setOnCheckedChangeListener { _, choiceId ->
            manageUserType(choiceId)
        }

        // Worker sector selection
        findViewById<Spinner>(R.id.sector_spinner).onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
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
        btnCancel.setOnClickListener {
            clearForm(btnCancel.parent as ViewGroup)
        }

        // Create a new person from written data when ok button ok is applied
        findViewById<Button>(R.id.btn_ok).setOnClickListener {
            addNewPerson()
        }

        // readFromExistingUser(exampleStudent)

    }


    /**
     * Get data form the form to create a new Person
     */
    private fun addNewPerson() {
        val name: String = findViewById<EditText>(R.id.main_base_name).text.toString()
        val firstName: String = findViewById<EditText>(R.id.main_base_firstname).text.toString()
        val email: String = findViewById<EditText>(R.id.additional_mail).text.toString()
        val remark: String = findViewById<EditText>(R.id.additional_remarks).text.toString()
        if(personType == "student") {
            val university = findViewById<EditText>(R.id.main_specific_school).text.toString()
            val graduationYear = findViewById<EditText>(R.id.main_specific_graduationyear).text.toString().toInt()
            person =  Student(name, firstName, customDatePickerBuilder.getCalendar(), this.nationality, university, graduationYear, email, remark)
        } else {
            val company = findViewById<EditText>(R.id.main_specific_compagny).text.toString()
            val experience = findViewById<EditText>(R.id.main_specific_experience).text.toString().toInt()
            person = Worker(name, firstName, customDatePickerBuilder.getCalendar(), nationality, company, sector, experience, email, remark)
        }
        println(person)
    }

    /**
     * Clears every flied of editText and put spinners to the default value
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

    private enum class PersonType {
        WORKER,
        STUDENT,
        NONE
    }

    private class CustomForm(val activity: MainActivity) {

        var personType: PersonType = PersonType.NONE
        val nationalityField = activity.findViewById<Spinner>(R.id.nationality_spinner)
        var selectedNationality = ""
        val studentGroup = activity.findViewById<Group>(R.id.student_group)
        val workerGroup = activity.findViewById<Group>(R.id.worker_group)
        val persTypeSelector = activity.findViewById<RadioGroup>(R.id.radio_group)

        // General fields
        val name = activity.findViewById<EditText>(R.id.main_base_name)
        val firstname = activity.findViewById<EditText>(R.id.main_base_firstname)
        val birthdate = activity.findViewById<EditText>(R.id.main_base_birthdate)
        val mail = activity.findViewById<EditText>(R.id.additional_mail)
        val remark = activity.findViewById<EditText>(R.id.additional_remarks)

        // Student Specific fields
        val school = activity.findViewById<EditText>(R.id.main_specific_school)
        val graduationyear = activity.findViewById<EditText>(R.id.main_specific_graduationyear)

        // Worker Specific fields
        val workerSector = activity.findViewById<Spinner>(R.id.sector_spinner)
        var workerSectorSelected = ""
        val workerCompany = activity.findViewById<EditText>(R.id.main_specific_compagny)
        val workerExperience = activity.findViewById<EditText>(R.id.main_specific_experience)

        init {

            persTypeSelector.setOnCheckedChangeListener { _, choiceId ->
                manageUserType(choiceId)
            }

            nationalityField.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
                override fun onItemSelected(parent: AdapterView<*>, view: View?, pos: Int, id: Long) {
                    if(pos == 0)
                        return
                    selectedNationality = parent.getItemAtPosition(pos).toString()
                }
                // Does nothing if no selection was done, but the function is required
                override fun onNothingSelected(parent: AdapterView<*>) {
                }
            }

            workerSector.onItemSelectedListener = object : AdapterView.OnItemSelectedListener{
                override fun onItemSelected(parent: AdapterView<*>, view: View?, pos: Int, id: Long) {
                    if(pos == 0)
                        return
                    workerSectorSelected = parent.getItemAtPosition(pos).toString()
                }
                override fun onNothingSelected(parent: AdapterView<*>) {
                }
            }
        }



        fun createPerson(): Person {
            if (!noEmptyFields()) {
                throw Exception("Empty fileds !")
            }

            if(personType == PersonType.STUDENT) {

                return Student(
                    name.text.toString(),
                    firstname.text.toString(),
                    getBirthdateCalendar(),
                    selectedNationality,
                    school.text.toString(),
                    graduationyear.text.toString().toInt(),
                    mail.text.toString(),
                    remark.text.toString())
            }
            else if (personType == PersonType.WORKER){
                return Worker(
                    name.text.toString(),
                    firstname.text.toString(),
                    getBirthdateCalendar(),
                    selectedNationality,
                    workerCompany.text.toString(),
                    workerSectorSelected,
                    workerExperience.text.toString().toInt(),
                    mail.text.toString(),
                    remark.text.toString()
                )
            } else {
                throw Exception("Unvalid peson type.")
            }

        }


        fun getBirthdateCalendar(): Calendar {
            val sdf = SimpleDateFormat(DATE_FORMAT, Locale.US)

            if (!birthdate.text.isEmpty()) {
                sdf.parse(birthdate.text.toString())
            }
            val calendar = Calendar.getInstance()
            sdf.parse(birthdate.text.toString())?.let { calendar.setTime(it) }
            return calendar

        }

        private fun noEmptyFields(): Boolean {

            if (
            name.text.isEmpty() ||
            firstname.text.isEmpty() ||
            birthdate.text.isEmpty() ||
            mail.text.isEmpty() ||
            remark.text.isEmpty()
            ) {
                return false
            }

            if (personType == PersonType.WORKER) {
                if (
                    workerSectorSelected.isEmpty() ||
                    workerCompany.text.isEmpty() ||
                    workerExperience.text.isEmpty()
                ) {
                    return false
                }
            }

            if (personType == PersonType.STUDENT) {
                if (
                    graduationyear.text.isEmpty() ||
                            school.text.isEmpty()
                ) {
                    return false
                }
            }
            return true
        }

        /**
         * Managing specific data whether the user is a student or a worker
         */
        private fun manageUserType(choiceId: Int) {
            if(choiceId == R.id.student_choice) {
                studentGroup.visibility = View.VISIBLE
                workerGroup.visibility = View.GONE
                personType = PersonType.STUDENT
            } else {
                workerGroup.visibility = View.VISIBLE
                studentGroup.visibility = View.GONE
                personType = PersonType.WORKER
            }
        }
    }

    private class CustomDatePickerBuilder(
        val matDatePicker: MaterialDatePicker.Builder<Long>,
        val dateField: EditText,
        ) {
        private val maxAge = 80
        private val minAge = 15
        private val today = MaterialDatePicker.todayInUtcMilliseconds()

        private var constraints = CalendarConstraints.Builder()

        fun getCalendar(): Calendar {
            val sdf = SimpleDateFormat(DATE_FORMAT, Locale.US)
            if (!dateField.text.isEmpty()) {
                sdf.parse(dateField.text.toString())
            }
            var calendar = Calendar.getInstance()
            calendar.setTime(sdf.parse(dateField.text.toString()))
            return calendar

        }

        fun getPicker(): MaterialDatePicker<Long> {

            val openAt: Calendar
            val sdf = SimpleDateFormat(DATE_FORMAT, Locale.US)
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
                val parser = SimpleDateFormat(DATE_FORMAT, Locale.getDefault())
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

    /**
     *
     */
    private fun readFromExistingUser(person: Person) {
        findViewById<EditText>(R.id.main_base_name).setText(person.name)
        findViewById<EditText>(R.id.main_base_firstname).setText(person.firstName)
        findViewById<EditText>(R.id.main_base_birthdate).setText(person.birthDay.toString())
    }
}