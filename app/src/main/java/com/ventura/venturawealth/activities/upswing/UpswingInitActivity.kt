package com.ventura.venturawealth.activities.upswing

import android.os.Build
import android.util.Log
import androidx.annotation.RequiresApi
import one.upswing.sdk.CustomerInitiationResponse
import one.upswing.sdk.FailureCustomerInitiationResponse
import one.upswing.sdk.IUpswingInitiateCustomer
import one.upswing.sdk.SuccessCustomerInitiationResponse
import org.json.JSONArray
import org.json.JSONObject
import utils.GlobalClass
import utils.SharedPref
import utils.UserSession
import wealth.new_mutualfund.ipo.AddUpiFragment.TAG
import java.io.BufferedReader
import java.io.InputStreamReader
import java.net.HttpURLConnection
import java.net.URL
import javax.net.ssl.HttpsURLConnection

@RequiresApi(api = Build.VERSION_CODES.M)
class UpswingInitActivity : IUpswingInitiateCustomer {

    override fun initiateCustomer(responseCallback: (CustomerInitiationResponse) -> Unit) {
        try {
            Log.d("Test", "This")
//            val URL = "https://vw.ventura1.com/authrestapi/getUpSwingFDkey?ucc=" + UserSession.getLoginDetailsModel().userID
            val sURL = "https://vw.ventura1.com/authrestapi/getUpSwingFDLivekey?ucc=" + UserSession.getLoginDetailsModel().userID
            val url = URL(sURL)
            val connection = url.openConnection() as HttpURLConnection
            connection.requestMethod = "GET"
            val responseCode = connection.responseCode
            if (responseCode == HttpURLConnection.HTTP_OK) {
                val `in` = BufferedReader(InputStreamReader(connection.inputStream))
                var inputLine: String?
                val response = java.lang.StringBuilder()
                while (`in`.readLine().also { inputLine = it } != null) {
                    response.append(inputLine)
                }
                `in`.close()
                val response1 = response.toString()
                try {
                    val jsonObject = JSONObject(response1)
                    val internalCustomerId = jsonObject.optString("ici")
                    val guestSessionToken = jsonObject.optString("guestSessionToken")
                    Log.d("TAG", "initiateCustomer: $internalCustomerId");
                    Log.d("TAG", "guestSessionToken: $guestSessionToken");
                    responseCallback(
                        SuccessCustomerInitiationResponse(
                            internalCustomerId,
                            guestSessionToken
                        )
                    )
                    callVenturaUpswingAPI(guestSessionToken)
                } catch (e: Exception) {
                    e.printStackTrace()
                }
            } else {
                Log.d("error", "HTTP error code: $responseCode")
                responseCallback(FailureCustomerInitiationResponse)
            }
        } catch (e: Exception) {
            e.printStackTrace()
            responseCallback(FailureCustomerInitiationResponse)
        }
    }


//    var temp1 = "";
//    var temp2 = "";
//    override fun initiateCustomer(response: CustomerInitiationResponseReceiver) {
//        lifecycleScope.launch {
//            try {
//                Log.d("Test", "This");
//                val URL =
//                    "https://vw.ventura1.com/authrestapi/getUpSwingFDkey?ucc=" + UserSession.getLoginDetailsModel()
//                        .getUserID()
//                val queue = Volley.newRequestQueue(this@UpswingInitActivity)
//                val request = StringRequest(Request.Method.GET, URL, { response1 ->
//
//                    try {
//                        val jsonObject = JSONObject(response1)
//                        temp1 = jsonObject.optString("ici")
//                        temp2 = jsonObject.optString("guestSessionToken")
//                        println("temp1 : " + temp1 + " temp2 : " + temp2)
//
//                        val internalCustomerId = temp1;//apiResult.response.get("ici") as String
//                        val guestSessionToken =
//                            temp2;//apiResult.response.get("guestSessionToken") as String
//                        response.receive(
//                            SuccessCustomerInitiationResponse(
//                                internalCustomerId,
//                                guestSessionToken
//                            )
//                        )
//
//                        callVenturaUpswingAPI();
//
//                    } catch (e: Exception) {
//                        e.printStackTrace()
//                    }
//                }) { error ->
//                    Log.d("error", error.toString())
//                    response.receive(FailureCustomerInitiationResponse)
//                }
//                queue.add(request)
//            } catch (e: Exception) {
//                e.printStackTrace()
//                response.receive(FailureCustomerInitiationResponse)
//            }
//        }

    private fun callVenturaUpswingAPI(guestSessionToken: String) {
        testAPI(guestSessionToken);
    }

    private fun testAPI(guestSessionToken:String) {
        val tables = ArrayList<DataModelTable>()
        try {
            // API endpoint URL
            val url = URL("https://ocrapi.ventura1.com/ClientProfile/GetKYCDataFD")
            val connection = url.openConnection() as HttpURLConnection
            connection.requestMethod = "POST"

            // Headers
            connection.setRequestProperty("Content-Type", "application/json")
            connection.setRequestProperty("OCRAPIKey", "OCRFD1:e5EPWugygD93WnWGalDiuv7NrUKBK_Fd")
            connection.doOutput = true

            // Body
            val userId = UserSession.getLoginDetailsModel().userID;
            val requestBody = "{\"userid\":\"$userId\"}"
            val outputStream = connection.outputStream
            outputStream.write(requestBody.toByteArray())
            outputStream.flush()
            outputStream.close()

            // Get response
            val responseCode = connection.responseCode
            val reader = BufferedReader(InputStreamReader(connection.inputStream))
            var line: String?
            val response = java.lang.StringBuilder()
            while (reader.readLine().also { line = it } != null) {
                response.append(line)
            }
            reader.close()
            Log.d("TEST", "Status Code: $responseCode");

            val data = formatString(response.toString());
            if (data != null) {
                Log.d("TEST", "Data : $data")
            };

            Log.e("VTest", "ventura api response: $response");

            val jsonResponse = JSONObject(response.toString())
            val response1 = jsonResponse.getJSONObject("response")
            val tableData = response1.getJSONArray("Table")
            for (i in 0 until tableData.length()) {
                val obj = tableData[i] as JSONObject

                tables.add(DataModelTable(
                    if (obj.isNull("ClientCode")) "NULL" else obj.getString("ClientCode")
                        .trim { it <= ' ' },
                    if (obj.isNull("firstName")) "NULL" else obj.getString("firstName")
                        .trim { it <= ' ' },
                    if (obj.isNull("middleName")) "NULL" else obj.getString("middleName")
                        .trim { it <= ' ' },
                    if (obj.isNull("lastName")) "NULL" else obj.getString("lastName")
                        .trim { it <= ' ' },
                    if (obj.isNull("gender")) "NULL" else obj.getString("gender")
                        .trim { it <= ' ' },
                    if (obj.isNull("email")) "NULL" else obj.getString("email").trim { it <= ' ' },
                    if (obj.isNull("pan")) "NULL" else obj.getString("pan").trim { it <= ' ' },
                    if (obj.isNull("dateOfBirth")) "NULL" else obj.getString("dateOfBirth")
                        .trim { it <= ' ' },
                    if (obj.isNull("occupation")) "NULL" else obj.getString("occupation")
                        .trim { it <= ' ' },
                    if (obj.isNull("income")) "NULL" else obj.getString("income")
                        .trim { it <= ' ' },
                    if (obj.isNull("maritalStatus")) "NULL" else obj.getString("maritalStatus")
                        .trim { it <= ' ' },
                    if (obj.isNull("addressLine1")) "NULL" else obj.getString("addressLine1")
                        .trim { it <= ' ' },
                    if (obj.isNull("addressLine2")) "NULL" else obj.getString("addressLine2")
                        .trim { it <= ' ' },
                    if (obj.isNull("addressLine3")) "NULL" else obj.getString("addressLine3")
                        .trim { it <= ' ' },
                    if (obj.isNull("postalCode")) "NULL" else obj.getString("postalCode")
                        .trim { it <= ' ' },
                    if (obj.isNull("MotherName")) "NULL" else obj.getString("MotherName")
                        .trim { it <= ' ' },
                    if (obj.isNull("MotherMiddleName")) "NULL" else obj.getString("MotherMiddleName")
                        .trim { it <= ' ' },
                    if (obj.isNull("MothertLastName")) "NULL" else obj.getString("MothertLastName")
                        .trim { it <= ' ' },
                    if (obj.isNull("FatherName")) "NULL" else obj.getString("FatherName")
                        .trim { it <= ' ' },
                    if (obj.isNull("FatherMiddleName")) "NULL" else obj.getString("FatherMiddleName")
                        .trim { it <= ' ' },
                    if (obj.isNull("FatherLastName")) "NULL" else obj.getString("FatherLastName")
                        .trim { it <= ' ' },
                    if (obj.isNull("bankAccountNumber")) "NULL" else obj.getString("bankAccountNumber")
                        .trim { it <= ' ' },
                    if (obj.isNull("ifsc")) "NULL" else obj.getString("ifsc").trim { it <= ' ' },
                    if (obj.isNull("Nominee1_Status")) "NULL" else obj.getString("Nominee1_Status")
                        .trim { it <= ' ' },
                    if (obj.isNull("Nominee1_fullName")) "NULL" else obj.getString("Nominee1_fullName")
                        .trim { it <= ' ' },
                    if (obj.isNull("Nominee1_relation")) "NULL" else obj.getString("Nominee1_relation")
                        .trim { it <= ' ' },
                    if (obj.isNull("Nominee1_dateOfBirth")) "NULL" else obj.getString("Nominee1_dateOfBirth")
                        .trim { it <= ' ' },
                    if (obj.isNull("Nominee1_phoneNumber")) "NULL" else obj.getString("Nominee1_phoneNumber")
                        .trim { it <= ' ' },
                    if (obj.isNull("Nominee1_email")) "NULL" else obj.getString("Nominee1_email")
                        .trim { it <= ' ' },
                    if (obj.isNull("Nominee1_address")) "NULL" else obj.getString("Nominee1_address")
                        .trim { it <= ' ' },
                    if (obj.isNull("Nominee1_postalCode")) "NULL" else obj.getString("Nominee1_postalCode")
                        .trim { it <= ' ' },
                    if (obj.isNull("Nominee1_Guardian_fullName")) "NULL" else obj.getString("Nominee1_Guardian_fullName")
                        .trim { it <= ' ' },
                    if (obj.isNull("Nominee1_Guardian_relation")) "NULL" else obj.getString("Nominee1_Guardian_relation")
                        .trim { it <= ' ' },
                    if (obj.isNull("Nominee1_Guardian_phoneNumber")) "NULL" else obj.getString("Nominee1_Guardian_phoneNumber")
                        .trim { it <= ' ' },
                    if (obj.isNull("Nominee1_Guardian_address")) "NULL" else obj.getString("Nominee1_Guardian_address")
                        .trim { it <= ' ' },
                    if (obj.isNull("Nominee1_Guardian_postalCode")) "NULL" else obj.getString("Nominee1_Guardian_postalCode")
                        .trim { it <= ' ' },
                    if (obj.isNull("Nominee2_Status")) "NULL" else obj.getString("Nominee2_Status")
                        .trim { it <= ' ' },
                    if (obj.isNull("Nominee2_fullName")) "NULL" else obj.getString("Nominee2_fullName")
                        .trim { it <= ' ' },
                    if (obj.isNull("Nominee2_relation")) "NULL" else obj.getString("Nominee2_relation")
                        .trim { it <= ' ' },
                    if (obj.isNull("Nominee2_dateOfBirth")) "NULL" else obj.getString("Nominee2_dateOfBirth")
                        .trim { it <= ' ' },
                    if (obj.isNull("Nominee2_phoneNumber")) "NULL" else obj.getString("Nominee2_phoneNumber")
                        .trim { it <= ' ' },
                    if (obj.isNull("Nominee2_email")) "NULL" else obj.getString("Nominee2_email")
                        .trim { it <= ' ' },
                    if (obj.isNull("Nominee2_address")) "NULL" else obj.getString("Nominee2_address")
                        .trim { it <= ' ' },
                    if (obj.isNull("Nominee2_postalCode")) "NULL" else obj.getString("Nominee2_postalCode")
                        .trim { it <= ' ' },
                    if (obj.isNull("Nominee2_Guardian_fullName")) "NULL" else obj.getString("Nominee2_Guardian_fullName")
                        .trim { it <= ' ' },
                    if (obj.isNull("Nominee2_Guardian_relation")) "NULL" else obj.getString("Nominee2_Guardian_relation")
                        .trim { it <= ' ' },
                    if (obj.isNull("Nominee2_Guardian_phoneNumber")) "NULL" else obj.getString("Nominee2_Guardian_phoneNumber")
                        .trim { it <= ' ' },
                    if (obj.isNull("Nominee2_Guardian_address")) "NULL" else obj.getString("Nominee2_Guardian_address")
                        .trim { it <= ' ' },
                    if (obj.isNull("Nominee2_Guardian_postalCode")) "NULL" else obj.getString("Nominee2_Guardian_postalCode")
                        .trim { it <= ' ' },
                    if (obj.isNull("Nominee3_Status")) "NULL" else obj.getString("Nominee3_Status")
                        .trim { it <= ' ' },
                    if (obj.isNull("Nominee3_fullName")) "NULL" else obj.getString("Nominee3_fullName")
                        .trim { it <= ' ' },
                    if (obj.isNull("Nominee3_relation")) "NULL" else obj.getString("Nominee3_relation")
                        .trim { it <= ' ' },
                    if (obj.isNull("Nominee3_dateOfBirth")) "NULL" else obj.getString("Nominee3_dateOfBirth")
                        .trim { it <= ' ' },
                    if (obj.isNull("Nominee3_phoneNumber")) "NULL" else obj.getString("Nominee3_phoneNumber")
                        .trim { it <= ' ' },
                    if (obj.isNull("Nominee3_email")) "NULL" else obj.getString("Nominee3_email")
                        .trim { it <= ' ' },
                    if (obj.isNull("Nominee3_address")) "NULL" else obj.getString("Nominee3_address")
                        .trim { it <= ' ' },
                    if (obj.isNull("Nominee3_postalCode")) "NULL" else obj.getString("Nominee3_postalCode")
                        .trim { it <= ' ' },
                    if (obj.isNull("Nominee3_Guardian_fullName")) "NULL" else obj.getString("Nominee3_Guardian_fullName")
                        .trim { it <= ' ' },
                    if (obj.isNull("Nominee3_Guardian_relation")) "NULL" else obj.getString("Nominee3_Guardian_relation")
                        .trim { it <= ' ' },
                    if (obj.isNull("Nominee3_Guardian_phoneNumber")) "NULL" else obj.getString("Nominee3_Guardian_phoneNumber")
                        .trim { it <= ' ' },
                    if (obj.isNull("Nominee3_Guardian_address")) "NULL" else obj.getString("Nominee3_Guardian_address")
                        .trim { it <= ' ' },
                    if (obj.isNull("Nominee3_Guardian_postalCode")) "NULL" else obj.getString("Nominee3_Guardian_postalCode")
                        .trim { it <= ' ' }
                ))
            }
            connection.disconnect()
            callNewUpswingApi(tables, guestSessionToken)
        } catch (e: java.lang.Exception) {
            println(e.toString() + "")
            e.printStackTrace()
        }
    }

    fun callNewUpswingApi(list: ArrayList<DataModelTable>, guestSessionToken: String) {
        Log.d("TEST", "Calling new API ...");
        try {
            val body = getJsonObject(list)
            val data = formatString(body.toString())

            Log.d("TEST", "new data: $body");

            // API endpoint URL
            // val url = URL("https://partner.api.uat-upswing.one/v1/term-deposit/partnerData/ingest?pci="+UserSession.getLoginDetailsModel().userID)
            val url = URL("https://partner.api.upswing.one/v1/term-deposit/partnerData/ingest?pci="+UserSession.getLoginDetailsModel().userID)

            val connection = url.openConnection() as HttpsURLConnection
            connection.requestMethod = "POST"

            // Headers
            connection.setRequestProperty("Content-Type", "application/json")
            connection.setRequestProperty("partner_jwt", guestSessionToken)
            connection.doOutput = true

            // Body
            try {
                connection.outputStream.use { os ->
                    val input = body.toString().toByteArray(charset("utf-8"))
                    os.write(input, 0, input.size)
                    os.flush()
                    os.close()
                }
            } catch (ex: java.lang.Exception) {
                ex.printStackTrace()
            }

            // Get response
            val responseCode = connection.responseCode
            Log.e("TEST", "callNewUpswingApi: error: "+ connection.responseMessage);
            val reader = BufferedReader(InputStreamReader(connection.inputStream))
            var line: String?
            val response = StringBuilder()
            while (reader.readLine().also { line = it } != null) {
                response.append(line)
            }
            reader.close()
            println("Status Code: $responseCode")
            val resdata = formatString(response.toString());
            Log.d("TEST", "new data: $resdata");

        } catch (e: java.lang.Exception) {
//            e.printStackTrace();
            Log.d("TEST", "error: $e");
            println(e)
        }
    }

    fun getJsonObject(list: ArrayList<DataModelTable>): JSONObject {
        val body = JSONObject()
        val fullName = JSONObject()
        if (list[0].firstName != "NULL" && !list[0].firstName!!.isEmpty()) fullName.put(
            "firstName",
            list[0].firstName
        )
        if (list[0].middleName != "NULL" && !list[0].middleName!!.isEmpty()) fullName.put(
            "middleName",
            list[0].middleName
        )
        if (list[0].lastName != "NULL" && !list[0].lastName!!.isEmpty()) fullName.put(
            "lastName",
            list[0].lastName
        )
        if (list[0].firstName != "NULL" && !list[0].firstName!!.isEmpty()) {
            body.put("fullName", fullName)
        }
        if (list[0].gender != "NULL" && !list[0].gender!!.isEmpty()) {
            if (list[0].gender == "M") {
                body.put("gender", "MALE")
            } else if (list[0].gender == "F") {
                body.put("gender", "FEMALE")
            } else if (list[0].gender == "T") {
                body.put("gender", "TRANS_GENDER")
            } else if (list[0].gender == "O") {
                body.put("gender", "OTHERS")
            }
        }
        if (list[0].email != "NULL" && !list[0].email!!.isEmpty()) body.put("email", list[0].email)
        if (list[0].pan != "NULL" && !list[0].pan!!.isEmpty()) body.put("pan", list[0].pan)
        if (list[0].dateOfBirth != "NULL" && !list[0].dateOfBirth!!.isEmpty()) body.put(
            "dateOfBirth",
            list[0].dateOfBirth
        )
        if (list[0].occupation != "NULL" && !list[0].occupation!!.isEmpty()) body.put(
            "occupation",
            list[0].occupation
        )
        if (list[0].income != "NULL" && !list[0].income!!.isEmpty()) body.put(
            "income",
            list[0].income
        )
        if (list[0].maritalStatus != "NULL" && !list[0].maritalStatus!!.isEmpty()) {
            if (list[0].maritalStatus == "S") {
                body.put("maritalStatus", "SINGLE")
            } else if (list[0].maritalStatus == "M") {
                body.put("maritalStatus", "MARRIED")
            } else if (list[0].maritalStatus == "O") {
                body.put("maritalStatus", "OTHERS")
            }
        }
        val addressArray = JSONArray()
        val address = JSONObject()
        if (list[0].addressLine1 != "NULL" && !list[0].addressLine1!!.isEmpty()) address.put(
            "addressLine1",
            list[0].addressLine1
        )
        if (list[0].addressLine2 != "NULL" && !list[0].addressLine2!!.isEmpty()) address.put(
            "addressLine2",
            list[0].addressLine2
        )
        if (list[0].addressLine3 != "NULL" && !list[0].addressLine3!!.isEmpty()) address.put(
            "addressLine3",
            list[0].addressLine3
        )
        if (list[0].postalCode != "NULL" && !list[0].postalCode!!.isEmpty()) address.put(
            "postalCode",
            list[0].postalCode
        )
        if (list[0].addressLine1 != "NULL" && !list[0].addressLine1!!.isEmpty()) {
            addressArray.put(address)
            body.put("address", addressArray)
        }
        val motherName = JSONObject()
        if (list[0].MotherName != "NULL" && !list[0].MotherName!!.isEmpty()) motherName.put(
            "firstName",
            list[0].MotherName
        )
        if (list[0].MotherMiddleName != "NULL" && !list[0].MotherMiddleName!!.isEmpty()) motherName.put(
            "middleName",
            list[0].MotherMiddleName
        )
        if (list[0].MothertLastName != "NULL" && !list[0].MothertLastName!!.isEmpty()) motherName.put(
            "lastName",
            list[0].MothertLastName
        )
        if (list[0].MotherName != "NULL" && !list[0].MotherName!!.isEmpty()) {
            body.put("motherName", motherName)
        }
        val fatherName = JSONObject()
        if (list[0].FatherName != "NULL" && !list[0].FatherName!!.isEmpty()) fatherName.put(
            "firstName",
            list[0].FatherName
        )
        if (list[0].FatherMiddleName != "NULL" && !list[0].FatherMiddleName!!.isEmpty()) fatherName.put(
            "middleName",
            list[0].FatherMiddleName
        )
        if (list[0].FatherLastName != "NULL" && !list[0].FatherLastName!!.isEmpty()) fatherName.put(
            "lastName",
            list[0].FatherLastName
        )
        if (list[0].FatherName != "NULL" && !list[0].FatherName!!.isEmpty()) {
            body.put("fatherName", fatherName)
        }
        val withdrawalBankArray = JSONArray()
        val withdrawalBank = JSONObject()
        if (list[0].bankAccountNumber != "NULL" && !list[0].bankAccountNumber!!.isEmpty()) withdrawalBank.put(
            "bankAccountNumber",
            list[0].bankAccountNumber
        )
        if (list[0].ifsc != "NULL" && !list[0].ifsc!!.isEmpty()) withdrawalBank.put(
            "ifsc",
            list[0].ifsc
        )
        if (list[0].bankAccountNumber != "NULL") {
            withdrawalBankArray.put(withdrawalBank)
            body.put("withdrawalBank", withdrawalBankArray)
        }
        val nominee = JSONArray()
        val nominee1 = JSONObject()
        if (list[0].Nominee1_fullName != "NULL" && !list[0].Nominee1_fullName!!.isEmpty()) nominee1.put(
            "fullName",
            list[0].Nominee1_fullName
        )
        if (list[0].Nominee1_relation != "NULL" && !list[0].Nominee1_relation!!.isEmpty()) nominee1.put(
            "relation",
            list[0].Nominee1_relation
        )
        if (list[0].Nominee1_dateOfBirth != "NULL" && !list[0].Nominee1_dateOfBirth!!.isEmpty()) nominee1.put(
            "dateOfBirth",
            list[0].Nominee1_dateOfBirth
        )
        if (list[0].Nominee1_phoneNumber != "NULL" && !list[0].Nominee1_phoneNumber!!.isEmpty()) nominee1.put(
            "phoneNumber",
            list[0].Nominee1_phoneNumber
        )
        if (list[0].Nominee1_email != "NULL" && !list[0].Nominee1_email!!.isEmpty()) nominee1.put(
            "email",
            list[0].Nominee1_email
        )
        if (list[0].Nominee1_address != "NULL" && !list[0].Nominee1_address!!.isEmpty() &&
            list[0].addressLine1 != "NULL" && !list[0].addressLine1!!.isEmpty()
        ) {
            nominee1.put("isAddressSimilar", list[0].Nominee1_address == list[0].addressLine1)
        }
        val nomineeAddress = JSONObject()
        if (list[0].Nominee1_address != "NULL" && !list[0].Nominee1_address!!.isEmpty()) nomineeAddress.put(
            "addressLine1",
            list[0].Nominee1_address
        )
        //        nomineeAddress.put("addressLine2","NULL");
//        nomineeAddress.put("addressLine3","NULL");
        if (list[0].Nominee1_postalCode != "NULL" && !list[0].Nominee1_postalCode!!.isEmpty()) nomineeAddress.put(
            "postalCode",
            list[0].Nominee1_postalCode
        )
        if (list[0].Nominee1_address != "NULL" && !list[0].Nominee1_address!!.isEmpty()
            && list[0].Nominee1_postalCode != "NULL" && !list[0].Nominee1_postalCode!!.isEmpty()
        ) {
            nominee1.put("address", nomineeAddress)
        }
        val nominee1GuardianInfo = JSONObject()
        if (list[0].Nominee1_Guardian_fullName != "NULL" && !list[0].Nominee1_Guardian_fullName!!.isEmpty()) nominee1GuardianInfo.put(
            "fullName",
            list[0].Nominee1_Guardian_fullName
        )
        if (list[0].Nominee1_Guardian_relation != "NULL" && !list[0].Nominee1_Guardian_relation!!.isEmpty()) nominee1GuardianInfo.put(
            "relation",
            list[0].Nominee1_Guardian_relation
        )
        //        nominee1GuardianInfo.put("dateOfBirth","NULL");
        if (list[0].Nominee1_Guardian_phoneNumber != "NULL" && !list[0].Nominee1_Guardian_phoneNumber!!.isEmpty()) nominee1GuardianInfo.put(
            "phoneNumber",
            list[0].Nominee1_Guardian_phoneNumber
        )
        if (list[0].Nominee1_Guardian_address != "NULL" && !list[0].Nominee1_Guardian_address!!.isEmpty()
            && list[0].addressLine1 != "NULL" && !list[0].addressLine1!!.isEmpty()
        ) {
            nominee1GuardianInfo.put(
                "isAddressSimilar",
                list[0].Nominee1_Guardian_address == list[0].addressLine1
            )
        }
        val nominee1GuardianInfoAddress = JSONObject()
        if (list[0].Nominee1_Guardian_address != "NULL" && !list[0].Nominee1_Guardian_address!!.isEmpty()) {
            nominee1GuardianInfoAddress.put("addressLine1", list[0].Nominee1_Guardian_address)
        }
        //        nominee1GuardianInfoAddress.put("addressLine2","NULL");
//        nominee1GuardianInfoAddress.put("addressLine3","NULL");
        if (list[0].Nominee1_Guardian_postalCode != "NULL" && !list[0].Nominee1_Guardian_postalCode!!.isEmpty()) {
            nominee1GuardianInfoAddress.put("postalCode", list[0].Nominee1_Guardian_postalCode)
        }
        if (list[0].Nominee1_Guardian_address != "NULL" && !list[0].Nominee1_Guardian_address!!.isEmpty()
            && list[0].Nominee1_Guardian_postalCode != "NULL" && !list[0].Nominee1_Guardian_postalCode!!.isEmpty()
        ) {
            nominee1GuardianInfo.put("address", nominee1GuardianInfoAddress)
        }
        if (list[0].Nominee1_Guardian_fullName != "NULL" && !list[0].Nominee1_Guardian_fullName!!.isEmpty()) {
            nominee1.put("guardianInfo", nominee1GuardianInfo)
        }
        val nominee2 = JSONObject()
        if (list[0].Nominee2_fullName != "NULL" && list[0].Nominee2_fullName != "NULL") nominee2.put(
            "fullName",
            list[0].Nominee2_fullName
        )
        if (list[0].Nominee2_relation != "NULL" && list[0].Nominee2_relation != "NULL") nominee2.put(
            "relation",
            list[0].Nominee2_relation
        )
        if (list[0].Nominee2_dateOfBirth != "NULL" && !list[0].Nominee2_dateOfBirth!!.isEmpty()) {
            nominee2.put("dateOfBirth", list[0].Nominee2_dateOfBirth)
        }
        if (list[0].Nominee2_phoneNumber != "NULL" && !list[0].Nominee2_phoneNumber!!.isEmpty()) {
            nominee2.put("phoneNumber", list[0].Nominee2_phoneNumber)
        }
        if (list[0].addressLine1 != "NULL" && !list[0].addressLine1!!.isEmpty()
            && list[0].Nominee2_address != "NULL" && !list[0].Nominee2_address!!.isEmpty()
        ) {
            nominee2.put("isAddressSimilar", list[0].Nominee2_address == list[0].addressLine1)
        }
        if (list[0].Nominee1_fullName != "NULL" && !list[0].Nominee1_fullName!!.isEmpty()) {
            nominee.put(nominee1)
        }
        if (list[0].Nominee2_fullName != "NULL" && !list[0].Nominee2_fullName!!.isEmpty()) {
            nominee.put(nominee2)
        }
        val relatedFsi = JSONArray()
        relatedFsi.put("AXISIN")
        relatedFsi.put("STFCIN")
        relatedFsi.put("BJFLIN")

        if (list[0].Nominee1_fullName != "NULL" && !list[0].Nominee1_fullName!!.isEmpty()) {
            body.put("nominee", nominee)
        }
        body.put("relatedFsi", relatedFsi)
        return body
    }

    fun formatString(text: String): String {
        val json = java.lang.StringBuilder()
        var indentString = ""
        for (i in 0 until text.length) {
            val letter = text[i]
            when (letter) {
                '{', '[' -> {
                    json.append(
                        """
                        
                        $indentString$letter
                        
                        """.trimIndent()
                    )
                    indentString = indentString + "\t"
                    json.append(indentString)
                }

                '}', ']' -> {
                    indentString = indentString.replaceFirst("\t".toRegex(), "")
                    json.append(
                        """
                        
                        $indentString$letter
                        """.trimIndent()
                    )
                }

                ',' -> json.append(
                    """
                    $letter
                    $indentString
                    """.trimIndent()
                )

                else -> json.append(letter)
            }
        }
        return json.toString()
    }
}