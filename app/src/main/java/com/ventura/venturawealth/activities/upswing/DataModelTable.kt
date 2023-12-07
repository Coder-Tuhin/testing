package com.ventura.venturawealth.activities.upswing

class DataModelTable {

    var ClientCode: String? = null
    var firstName: String? = null
    var middleName: String? = null
    var lastName: String? = null
    var gender: String? = null
    var email: String? = null
    var pan: String? = null
    var dateOfBirth: String? = null
    var occupation: String? = null
    var income: String? = null
    var maritalStatus: String? = null
    var addressLine1: String? = null
    var addressLine2: String? = null
    var addressLine3: String? = null
    var postalCode: String? = null
    var MotherName: String? = null
    var MotherMiddleName: String? = null
    var MothertLastName: String? = null
    var FatherName: String? = null
    var FatherMiddleName: String? = null
    var FatherLastName: String? = null
    var bankAccountNumber: String? = null
    var ifsc: String? = null
    var Nominee1_Status: String? = null
    var Nominee1_fullName: String? = null
    var Nominee1_relation: String? = null
    var Nominee1_dateOfBirth: String? = null
    var Nominee1_phoneNumber: String? = null
    var Nominee1_email: String? = null
    var Nominee1_address: String? = null
    var Nominee1_postalCode: String? = null
    var Nominee1_Guardian_fullName: String? = null
    var Nominee1_Guardian_relation: String? = null
    var Nominee1_Guardian_phoneNumber: String? = null
    var Nominee1_Guardian_address: String? = null
    var Nominee1_Guardian_postalCode: String? = null
    var Nominee2_Status: String? = null
    var Nominee2_fullName: String? = null
    var Nominee2_relation: String? = null
    var Nominee2_dateOfBirth: String? = null
    var Nominee2_phoneNumber: String? = null
    var Nominee2_email: String? = null
    var Nominee2_address: String? = null
    var Nominee2_postalCode: String? = null
    var Nominee2_Guardian_fullName: String? = null
    var Nominee2_Guardian_relation: String? = null
    var Nominee2_Guardian_phoneNumber: String? = null
    var Nominee2_Guardian_address: String? = null
    var Nominee2_Guardian_postalCode: String? = null
    var Nominee3_Status: String? = null
    var Nominee3_fullName: String? = null
    var Nominee3_relation: String? = null
    var Nominee3_dateOfBirth: String? = null
    var Nominee3_phoneNumber: String? = null
    var Nominee3_email: String? = null
    var Nominee3_address: String? = null
    var Nominee3_postalCode: String? = null
    var Nominee3_Guardian_fullName: String? = null
    var Nominee3_Guardian_relation: String? = null
    var Nominee3_Guardian_phoneNumber: String? = null
    var Nominee3_Guardian_address: String? = null
    var Nominee3_Guardian_postalCode: String? = null

//    fun DataModelTable(clientCode: String?) {
//        ClientCode = clientCode
//    }

//    fun DataModelTable() {}



    constructor(
        clientCode: String?,
        firstName: String?,
        middleName: String?,
        lastName: String?,
        gender: String?,
        email: String?,
        pan: String?,
        dateOfBirth: String?,
        occupation: String?,
        income: String?,
        maritalStatus: String?,
        addressLine1: String?,
        addressLine2: String?,
        addressLine3: String?,
        postalCode: String?,
        motherName: String?,
        motherMiddleName: String?,
        mothertLastName: String?,
        fatherName: String?,
        fatherMiddleName: String?,
        fatherLastName: String?,
        bankAccountNumber: String?,
        ifsc: String?,
        nominee1_Status: String?,
        nominee1_fullName: String?,
        nominee1_relation: String?,
        nominee1_dateOfBirth: String?,
        nominee1_phoneNumber: String?,
        nominee1_email: String?,
        nominee1_address: String?,
        nominee1_postalCode: String?,
        nominee1_Guardian_fullName: String?,
        nominee1_Guardian_relation: String?,
        nominee1_Guardian_phoneNumber: String?,
        nominee1_Guardian_address: String?,
        nominee1_Guardian_postalCode: String?,
        nominee2_Status: String?,
        nominee2_fullName: String?,
        nominee2_relation: String?,
        nominee2_dateOfBirth: String?,
        nominee2_phoneNumber: String?,
        nominee2_email: String?,
        nominee2_address: String?,
        nominee2_postalCode: String?,
        nominee2_Guardian_fullName: String?,
        nominee2_Guardian_relation: String?,
        nominee2_Guardian_phoneNumber: String?,
        nominee2_Guardian_address: String?,
        nominee2_Guardian_postalCode: String?,
        nominee3_Status: String?,
        nominee3_fullName: String?,
        nominee3_relation: String?,
        nominee3_dateOfBirth: String?,
        nominee3_phoneNumber: String?,
        nominee3_email: String?,
        nominee3_address: String?,
        nominee3_postalCode: String?,
        nominee3_Guardian_fullName: String?,
        nominee3_Guardian_relation: String?,
        nominee3_Guardian_phoneNumber: String?,
        nominee3_Guardian_address: String?,
        nominee3_Guardian_postalCode: String?
    ) {
        ClientCode = clientCode
        this.firstName = firstName
        this.middleName = middleName
        this.lastName = lastName
        this.gender = gender
        this.email = email
        this.pan = pan
        this.dateOfBirth = dateOfBirth
        this.occupation = occupation
        this.income = income
        this.maritalStatus = maritalStatus
        this.addressLine1 = addressLine1
        this.addressLine2 = addressLine2
        this.addressLine3 = addressLine3
        this.postalCode = postalCode
        MotherName = motherName
        MotherMiddleName = motherMiddleName
        MothertLastName = mothertLastName
        FatherName = fatherName
        FatherMiddleName = fatherMiddleName
        FatherLastName = fatherLastName
        this.ifsc = ifsc
        this.bankAccountNumber = bankAccountNumber
        Nominee1_Status = nominee1_Status
        Nominee1_fullName = nominee1_fullName
        Nominee1_relation = nominee1_relation
        Nominee1_dateOfBirth = nominee1_dateOfBirth
        Nominee1_phoneNumber = nominee1_phoneNumber
        Nominee1_email = nominee1_email
        Nominee1_address = nominee1_address
        Nominee1_postalCode = nominee1_postalCode
        Nominee1_Guardian_fullName = nominee1_Guardian_fullName
        Nominee1_Guardian_relation = nominee1_Guardian_relation
        Nominee1_Guardian_phoneNumber = nominee1_Guardian_phoneNumber
        Nominee1_Guardian_address = nominee1_Guardian_address
        Nominee1_Guardian_postalCode = nominee1_Guardian_postalCode
        Nominee2_Status = nominee2_Status
        Nominee2_fullName = nominee2_fullName
        Nominee2_relation = nominee2_relation
        Nominee2_dateOfBirth = nominee2_dateOfBirth
        Nominee2_phoneNumber = nominee2_phoneNumber
        Nominee2_email = nominee2_email
        Nominee2_address = nominee2_address
        Nominee2_postalCode = nominee2_postalCode
        Nominee2_Guardian_fullName = nominee2_Guardian_fullName
        Nominee2_Guardian_relation = nominee2_Guardian_relation
        Nominee2_Guardian_phoneNumber = nominee2_Guardian_phoneNumber
        Nominee2_Guardian_address = nominee2_Guardian_address
        Nominee2_Guardian_postalCode = nominee2_Guardian_postalCode
        Nominee3_Status = nominee3_Status
        Nominee3_fullName = nominee3_fullName
        Nominee3_relation = nominee3_relation
        Nominee3_dateOfBirth = nominee3_dateOfBirth
        Nominee3_phoneNumber = nominee3_phoneNumber
        Nominee3_email = nominee3_email
        Nominee3_address = nominee3_address
        Nominee3_postalCode = nominee3_postalCode
        Nominee3_Guardian_fullName = nominee3_Guardian_fullName
        Nominee3_Guardian_relation = nominee3_Guardian_relation
        Nominee3_Guardian_phoneNumber = nominee3_Guardian_phoneNumber
        Nominee3_Guardian_address = nominee3_Guardian_address
        Nominee3_Guardian_postalCode = nominee3_Guardian_postalCode
    }
}
