/**
 *  * DAA Laboratory 2
 * @author      : Damien Maier, Jean-François Pasche, Vincent Peer
 * Date         : 16.11.2022
 * Description  : Offers a form interface with multiple fields about user information. The user can
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
import ch.heigvd.labo2.Model.Person
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
    private lateinit var datePicker : MaterialDatePicker<Long?>

    private lateinit var person: Person

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)



        // Date selection

        datePicker = MaterialDatePicker.Builder.datePicker()
            .setCalendarConstraints(
                CalendarConstraints.Builder()
                    .setStart(Calendar.getInstance().apply { add(Calendar.YEAR, -100) }.timeInMillis)
                    .setEnd(Calendar.getInstance().apply { add(Calendar.YEAR, -10) }.timeInMillis)
                    .build()
            )
            .build()

        datePicker.addOnPositiveButtonClickListener {
            updateBirthdateTextField()
        }


        findViewById<ImageButton>(R.id.cake_button).setOnClickListener {
            datePicker.show(supportFragmentManager, null)
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

    fun updateBirthdateTextField() {
        findViewById<EditText>(R.id.main_base_birthdate).setText(
            SimpleDateFormat(
                DATE_FORMAT,
                Locale.US
            ).format(datePicker.selection).toString()
        )
    }

    private fun getPersonType() =
        when(findViewById<RadioGroup>(R.id.radio_group).checkedRadioButtonId) {
            R.id.student_choice -> PersonType.STUDENT
            R.id.worker_choice -> PersonType.WORKER
            else -> null
        }

    /**
     * Get data form the form to create a new Person
     */
    private fun addNewPerson() {
        if(getPersonType() == PersonType.STUDENT) {
            person =  Student(
                findViewById<EditText>(R.id.main_base_name).text.toString(),
                findViewById<EditText>(R.id.main_base_firstname).text.toString(),
                Calendar.getInstance().apply{timeInMillis = datePicker.selection?:0},
                findViewById<Spinner>(R.id.nationality_spinner).selectedItem.toString(),
                findViewById<EditText>(R.id.main_specific_school).text.toString(),
                findViewById<EditText>(R.id.main_specific_graduationyear).text.toString().toInt(),
                findViewById<EditText>(R.id.additional_mail).text.toString(),
                findViewById<EditText>(R.id.additional_remarks).text.toString())
        } else if (getPersonType() == PersonType.WORKER) {
            person = Worker(
                findViewById<EditText>(R.id.main_base_name).text.toString(),
                findViewById<EditText>(R.id.main_base_firstname).text.toString(),
                Calendar.getInstance().apply{timeInMillis = datePicker.selection?:0},
                findViewById<Spinner>(R.id.nationality_spinner).selectedItem.toString(),
                findViewById<EditText>(R.id.main_specific_compagny).text.toString(),
                findViewById<Spinner>(R.id.sector_spinner).selectedItem.toString(),
                findViewById<EditText>(R.id.main_specific_experience).text.toString().toInt(),
                findViewById<EditText>(R.id.additional_mail).text.toString(),
                findViewById<EditText>(R.id.additional_remarks).text.toString())
        } else {
            throw Exception("Bad person type ... cannot create a Person object.")
        }
        println(person)
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

    // TODO à quoi ça sert ???
    private fun findPositionInSpinner(string: String) : Int {
        val a = arrayOf(R.array.nationalities)
        findViewById<Spinner>(R.id.nationality_spinner).setSelection(a[0])
        return  0
    }
}