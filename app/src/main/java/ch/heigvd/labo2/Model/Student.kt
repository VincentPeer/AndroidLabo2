package ch.heigvd.labo2.Model

import java.util.*

class Student(name: String,
              firstName: String,
              birthDay : Calendar,
              nationality: String,
              var university : String,
              var graduationYear : Int,
              email : String,
              remark: String) : Person(name, firstName, birthDay, nationality, email, remark) {

    override fun toString(): String {
        return "Student - ${super.superToString()}, university: $university, graduationYear: $graduationYear"
    }
}