package ch.heigvd.labo2.Model

import java.util.*

class Worker(name: String,
             firstName: String,
             birthDay : Calendar,
             nationality: String,
             var company : String,
             var sector : String,
             var experienceYear : Int,
             email : String,
             remark: String) : Person(name, firstName, birthDay, nationality, email, remark) {

    override fun toString(): String {
        return "Worker - ${super.superToString()}, company: $company, sector: $sector, experienceYear: $experienceYear"
    }
}