/**
 *  * DAA Laboratory 2
 * @author      : Damien Maier, Jean-François Pasche, Vincent Peer
 * Date         : 16.11.2022
 * Description  : Offers a form with multiple fields about user information. The user can
 *                be a student or a worker and has multiple field to edit concerning his personal data.
 */

package ch.heigvd.labo2

import android.os.Bundle
import android.util.Log
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import androidx.constraintlayout.widget.Group
import androidx.core.view.iterator
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

private const val BIRTHDAY_KEY = "birthday"

/**
 * Creates the form that needs to be completed by a user, or print information from an existed user.
 */
class MainActivity : AppCompatActivity() {
    private var birthdateTimeStamp : Long? = null
    private lateinit var person: Person

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        birthdateTimeStamp = savedInstanceState?.getLong(BIRTHDAY_KEY)
        updateBirthdateTextField()

        // Date selection
        findViewById<ImageButton>(R.id.cake_button).setOnClickListener {

            val constraints = CalendarConstraints.Builder()
                .setStart(Calendar.getInstance().apply { add(Calendar.YEAR, -100) }.timeInMillis)
                .setEnd(Calendar.getInstance().timeInMillis)
                .build()

            MaterialDatePicker.Builder.datePicker()
                .setCalendarConstraints(constraints)
                .setSelection(birthdateTimeStamp ?: Calendar.getInstance().timeInMillis)
                .build()
                .apply{
                    addOnPositiveButtonClickListener {
                        birthdateTimeStamp = it
                        updateBirthdateTextField()
                    }
                    show(supportFragmentManager, null)
                }
        }

        // User type selection
        findViewById<RadioGroup>(R.id.radio_group).setOnCheckedChangeListener { _, _ ->
            findViewById<Group>(R.id.student_group).visibility =
                if (getPersonType() == PersonType.STUDENT) View.VISIBLE else View.GONE
            findViewById<Group>(R.id.worker_group).visibility =
                if (getPersonType() == PersonType.WORKER) View.VISIBLE else View.GONE
        }

        // Delete every field when cancel button is applied
        val btnCancel = findViewById<Button>(R.id.btn_cancel)
        btnCancel.setOnClickListener {
            for (view in findViewById<ViewGroup>(R.id.main_layout))
                when (view) {
                    is EditText -> view.setText("")
                    is Spinner -> view.setSelection(0) // Reset to "selection" state
                    is RadioGroup -> view.clearCheck() // Reset radioGroup with no state chosen
                }

            birthdateTimeStamp = null
            updateBirthdateTextField()
        }

        // Create a new person from written data when ok button ok is applied
        findViewById<Button>(R.id.btn_ok).setOnClickListener {
            if (noEmptyField()) {
                try {
                    createPerson()
                } catch (e: Exception) {
                    Log.e(LOG_TAG, e.toString())
                }
            } else {
                Log.e(LOG_TAG, "Empty field(s)...")
            }
        }

        // Test with existing user
        // readFromExistingUser(exampleStudent)
        // readFromExistingUser(exampleWorker)

    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        if (birthdateTimeStamp != null)
            outState.putLong(BIRTHDAY_KEY, birthdateTimeStamp!!)
    }

    private fun updateBirthdateTextField() {
        findViewById<EditText>(R.id.main_base_birthdate).setText(
            if (birthdateTimeStamp == null) ""
            else SimpleDateFormat(DATE_FORMAT, Locale.US).format(birthdateTimeStamp).toString()
        )
    }

    private fun getPersonType() =
        when(findViewById<RadioGroup>(R.id.radio_group).checkedRadioButtonId) {
            R.id.student_choice -> PersonType.STUDENT
            R.id.worker_choice -> PersonType.WORKER
            else -> null
        }

    /**
     * Get data from the form to create a new Person
     */
    private fun createPerson() {
        person = when (getPersonType()) {
            PersonType.STUDENT -> Student(
                findViewById<EditText>(R.id.main_base_name).text.toString(),
                findViewById<EditText>(R.id.main_base_firstname).text.toString(),
                Calendar.getInstance().apply{timeInMillis = birthdateTimeStamp?:0},
                findViewById<Spinner>(R.id.nationality_spinner).selectedItem.toString(),
                findViewById<EditText>(R.id.main_specific_school).text.toString(),
                findViewById<EditText>(R.id.main_specific_graduationyear).text.toString().toInt(),
                findViewById<EditText>(R.id.additional_mail).text.toString(),
                findViewById<EditText>(R.id.additional_remarks).text.toString()
            )

            PersonType.WORKER -> Worker(
                findViewById<EditText>(R.id.main_base_name).text.toString(),
                findViewById<EditText>(R.id.main_base_firstname).text.toString(),
                Calendar.getInstance().apply{timeInMillis = birthdateTimeStamp?:0},
                findViewById<Spinner>(R.id.nationality_spinner).selectedItem.toString(),
                findViewById<EditText>(R.id.main_specific_compagny).text.toString(),
                findViewById<Spinner>(R.id.sector_spinner).selectedItem.toString(),
                findViewById<EditText>(R.id.main_specific_experience).text.toString().toInt(),
                findViewById<EditText>(R.id.additional_mail).text.toString(),
                findViewById<EditText>(R.id.additional_remarks).text.toString()
            )

            else -> throw Exception("Bad person type ... cannot create a Person object.")
        }

        Log.d(LOG_TAG, person.toString())
    }


    private fun noEmptyField(): Boolean {
        if (
            findViewById<EditText>(R.id.main_base_name).text.isEmpty() ||
            findViewById<EditText>(R.id.main_base_firstname).text.isEmpty() ||
            findViewById<EditText>(R.id.additional_mail).text.isEmpty()
        ) {
            return false
        }

        if (getPersonType() == PersonType.WORKER) {
            if (findViewById<EditText>(R.id.main_specific_compagny).text.isEmpty() ||
                findViewById<EditText>(R.id.main_specific_experience).text.isEmpty()) {
                return false
            }
        }
        if (getPersonType() == PersonType.STUDENT) {
            if (findViewById<EditText>(R.id.main_specific_school).text.isEmpty() ||
                findViewById<EditText>(R.id.main_specific_graduationyear).text.isEmpty()) {
                return false
            }
        }
        return true
    }


    // Specific person type
    private enum class PersonType {
        STUDENT,
        WORKER,
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

        // Set birthday
        birthdateTimeStamp = person.birthDay.timeInMillis
        updateBirthdateTextField()

        // Set nationality
        val adapterNationality = ArrayAdapter.createFromResource(
            this,
            R.array.nationalities,
            android.R.layout.simple_spinner_item
        )
        adapterNationality.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)

        findViewById<Spinner>(R.id.nationality_spinner).apply {
            adapter = adapterNationality
            setSelection(adapterNationality.getPosition(person.nationality))
        }

        // Set specific data
        val radioGroup = findViewById<RadioGroup>(R.id.radio_group)
        when (person) {
            is Student -> {
                radioGroup.check(R.id.student_choice)
                findViewById<EditText>(R.id.main_specific_school).setText(person.university)
                findViewById<EditText>(R.id.main_specific_graduationyear).setText(person.graduationYear.toString())
            }
            is Worker -> {
                radioGroup.check(R.id.worker_choice)
                findViewById<EditText>(R.id.main_specific_compagny).setText(person.company)
                findViewById<EditText>(R.id.main_specific_experience).setText(person.experienceYear.toString())

                val adapterSector = ArrayAdapter.createFromResource(
                    this,
                    R.array.sectors,
                    android.R.layout.simple_spinner_item
                )
                adapterSector.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)

                findViewById<Spinner>(R.id.sector_spinner).apply {
                    adapter = adapterSector
                    setSelection(adapterSector.getPosition(person.sector))
                }
            }
        }
    }
}
