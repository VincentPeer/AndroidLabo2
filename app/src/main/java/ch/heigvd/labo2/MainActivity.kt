/**
 *  * DAA Laboratory 2
 * @author      : Damien Maier, Jean-François Pasche, Vincent Peer
 * Date         : 16.11.2022
 * Description  : Offers a form interface with multiple fields about user information. The user can
 *                be a student or a worker and has multiple field to edit concerning his personal data.
 */

package ch.heigvd.labo2

import android.os.Bundle
import android.provider.MediaStore.Audio.Radio
import android.util.Log
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import androidx.constraintlayout.widget.Group
import ch.heigvd.labo2.Model.Person
import ch.heigvd.labo2.Model.Person.Companion.exampleStudent
import ch.heigvd.labo2.Model.Person.Companion.exampleWorker
import ch.heigvd.labo2.Model.Student
import ch.heigvd.labo2.Model.Worker
import com.google.android.material.datepicker.CalendarConstraints
import com.google.android.material.datepicker.MaterialDatePicker
import java.text.SimpleDateFormat
import java.util.*

const val DATE_FORMAT = "dd MMMM yyyy"
const val LOG_TAG = "MainActivity"

/**
 * Creates the form that need to be complete by a user, or print information from an existed user.
 */
class MainActivity : AppCompatActivity() {
    private lateinit var customDatePickerBuilder: CustomDatePickerBuilder
    private lateinit var person: Person
    private var sector = ""

    private var personType: PersonType = PersonType.NONE

    // General fields
    private lateinit var nameField: EditText
    private lateinit var firstnameField: EditText
    private lateinit var birthdateField: EditText
    private lateinit var nationalityField: Spinner
    private var nationalitySelectedVal: String = ""
    private lateinit var remark: EditText
    private lateinit var mailAddressField: EditText

    // Student fields
    private lateinit var schoolField: EditText
    private lateinit var gradYearField: EditText
    private lateinit var sectorField: Spinner
    private var sectorSelected = ""
    private lateinit var experienceYearField: EditText

    // Worker Fields
    private lateinit var companyField: EditText

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        // General fields initialisation
        nameField = findViewById(R.id.main_base_name)
        firstnameField = findViewById(R.id.main_base_firstname)
        birthdateField = findViewById(R.id.main_base_birthdate)
        remark = findViewById(R.id.additional_remarks)
        mailAddressField = findViewById(R.id.additional_mail)
        nationalityField = findViewById(R.id.nationality_spinner)

        // Student fields initialisation
        schoolField = findViewById(R.id.main_specific_school)
        gradYearField = findViewById(R.id.main_specific_graduationyear)

        // Worker fields initialisation
        companyField = findViewById(R.id.main_specific_compagny)
        sectorField = findViewById(R.id.sector_spinner)
        experienceYearField = findViewById(R.id.main_specific_experience)


        // Date selection
        customDatePickerBuilder = CustomDatePickerBuilder(
            MaterialDatePicker.Builder.datePicker())

        findViewById<ImageButton>(R.id.cake_button).setOnClickListener {
            customDatePickerBuilder.getPicker().show(supportFragmentManager, null)
        }

        // Nationality selection
        nationalityField.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(parent: AdapterView<*>, view: View?, pos: Int, id: Long) {
                if(pos == 0)
                    return
                nationalitySelectedVal = parent.getItemAtPosition(pos).toString()
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
        sectorField.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
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
            if (noEmptyField()) {
                try {
                    addNewPerson()
                } catch (e: Exception) {
                    Log.e(LOG_TAG, e.toString())
                }
            } else {
                Log.e(LOG_TAG, "Empty field(s)...")
            }
        }

        // Test with existing user
        //readFromExistingUser(exampleStudent)
        readFromExistingUser(exampleWorker)

    }


    /**
     * Get data form the form to create a new Person
     */
    private fun addNewPerson() {
        if(personType == PersonType.STUDENT) {
            person =  Student(
                nameField.text.toString(),
                firstnameField.text.toString(),
                customDatePickerBuilder.getCalendar(),
                nationalitySelectedVal, schoolField.text.toString(),
                gradYearField.text.toString().toInt(),
                mailAddressField.text.toString(),
                remark.text.toString())
        } else if (personType == PersonType.WORKER) {
            val company = findViewById<EditText>(R.id.main_specific_compagny).text.toString()
            val experience = findViewById<EditText>(R.id.main_specific_experience).text.toString().toInt()
            person = Worker(nameField.text.toString(),
                firstnameField.text.toString(),
                customDatePickerBuilder.getCalendar(),
                nationalitySelectedVal,
                companyField.text.toString(),
                sectorSelected,
                experienceYearField.text.toString().toInt(),
                mailAddressField.text.toString(),
                remark.text.toString())
        } else {
            throw Exception("Bad person type ... cannot create a Person objetc.")
        }
        println(person)
        Log.d(LOG_TAG, person.toString())
    }

    private fun noEmptyField(): Boolean {
        if (
            nameField.text.isEmpty() ||
                    firstnameField.text.isEmpty() ||
                    nationalitySelectedVal.isEmpty() ||
                    mailAddressField.text.isEmpty()
        ) {
            return false
        }

        if (personType == PersonType.WORKER) {
            if (companyField.text.isEmpty() ||
                    sectorSelected.isEmpty() ||
                    experienceYearField.text.isEmpty()) {
                return false
            }
        }
        if (personType == PersonType.STUDENT) {
            if (schoolField.text.isEmpty() ||
                    gradYearField.text.isEmpty()) {
                return false
            }
        }
        return true
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
            } else if(view is RadioGroup) {
                view.clearCheck() // Reset radioGroup with no state chosen
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
            personType = PersonType.STUDENT
        } else if(choiceId == R.id.worker_choice){
            workerGroup.visibility = View.VISIBLE
            studentGroup.visibility = View.GONE
            personType = PersonType.WORKER
        } else {
            studentGroup.visibility = View.GONE
            workerGroup.visibility = View.GONE
            personType = PersonType.NONE
        }
    }



    private inner class CustomDatePickerBuilder(
        val matDatePicker: MaterialDatePicker.Builder<Long>
        ) {
        private val maxAge = 80
        private val minAge = 15
        private val today = MaterialDatePicker.todayInUtcMilliseconds()

        private var constraints = CalendarConstraints.Builder()

        fun getCalendar(): Calendar {
            val sdf = SimpleDateFormat(DATE_FORMAT, Locale.US)
            if (!birthdateField.text.isEmpty()) {
                sdf.parse(birthdateField.text.toString())
            }
            var calendar = Calendar.getInstance()
            calendar.setTime(sdf.parse(birthdateField.text.toString()))
            return calendar

        }

        fun getPicker(): MaterialDatePicker<Long> {

            val openAt: Calendar
            val sdf = SimpleDateFormat(DATE_FORMAT, Locale.US)
            if (!birthdateField.text.isEmpty()) {
                sdf.parse(birthdateField.text.toString())
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
                birthdateField.setText(date.toString())
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

    private enum class PersonType {
        STUDENT,
        WORKER,
        NONE
    }

    /**
     * Read data from an existing user and fill the form with the corresponding value
     */
    private fun readFromExistingUser(person: Person) {
        this.person = person

        // Set main data
        findViewById<EditText>(R.id.main_base_name).setText(person.name)
        findViewById<EditText>(R.id.main_base_firstname).setText(person.firstName)
        findViewById<EditText>(R.id.additional_mail).setText(person.email)
        findViewById<EditText>(R.id.additional_remarks).setText(person.remark)
        findViewById<Spinner>(R.id.nationality_spinner).setSelection(findPositionInSpinner(person.nationality))

        // Set birthday
        val sdf = SimpleDateFormat("dd MMMM yyyy", Locale.US)
        findViewById<EditText>(R.id.main_base_birthdate).setText(sdf.format(person.birthDay.time))

        // Set nationality

        // Set specific data
        val radioGroup = findViewById<RadioGroup>(R.id.radio_group)
        if(getUserType(person) == Student::class.java) {
            person as Student
            radioGroup.check(R.id.student_choice)
            findViewById<EditText>(R.id.main_specific_school).setText(person.university)
            findViewById<EditText>(R.id.main_specific_graduationyear).setText(person.graduationYear.toString())
        } else {
            person as Worker
            radioGroup.check(R.id.worker_choice)
            findViewById<EditText>(R.id.main_specific_compagny).setText(person.company)
            findViewById<EditText>(R.id.main_specific_experience).setText(person.experienceYear.toString())
        }
    }

    /**
     * Return the class type of the user
     */
    private fun getUserType(person: Person) : Class<*> {
        return if(person is Student) {
            Student::class.java
        } else if(person is Worker) {
            Worker::class.java
        } else { // todo exception ou rien faire?
            throw java.lang.RuntimeException("Invalid user type")
        }
    }

    private fun findPositionInSpinner(string: String) : Int {
        val a = arrayOf(R.array.nationalities)
        findViewById<Spinner>(R.id.nationality_spinner).setSelection(a[0])
        return  0
    }
}