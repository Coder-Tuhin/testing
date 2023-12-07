package wealth.new_mutualfund.menus.modelclass;

import com.ventura.venturawealth.R;

import org.json.JSONObject;

import wealth.new_mutualfund.MfUtils;

public class Mandatemodel {

    String BankName = "";
    String AvailableAmt = "";
    String AccountNo = "";
    String MandateAmt = "";
    String MandateCode = "";
    String SIPMandateEndDate = "";
    String SIPMandateStartDate = "";
    String SIPType = "";
    String TransactionAmt = "";
    String MandateStatus = "";
    public Mandatemodel(JSONObject jObj){
        BankName = MfUtils.getString(R.string.BankName,jObj);
        AvailableAmt = MfUtils.getString(R.string.AvailableAmt,jObj);
        AccountNo = MfUtils.getString(R.string.AccountNo,jObj);
        MandateAmt = MfUtils.getString(R.string.MandateAmt,jObj);
        MandateCode = MfUtils.getString(R.string.MandateCode,jObj);
        SIPMandateEndDate = MfUtils.getString(R.string.SIPMandateEndDate,jObj);
        SIPMandateStartDate = MfUtils.getString(R.string.SIPMandateStartDate,jObj);
        SIPType = MfUtils.getString(R.string.SIPType,jObj);
        TransactionAmt = MfUtils.getString(R.string.TransactionAmt,jObj);
        MandateStatus = MfUtils.getString(R.string.MandateStatus,jObj);
    }

    public String getMandateStatus() {
        return MandateStatus;
    }

    public String getBankName() {
        return BankName;
    }


    public String getAvailableAmt() {
        return AvailableAmt;
    }

    public String getAccountNo() {
        return AccountNo;
    }

    public String getMandateAmt() {
        return MandateAmt;
    }

    public String getMandateCode() {
        return MandateCode;
    }

    public String getSIPMandateEndDate() {
        return SIPMandateEndDate;
    }

    public String getSIPMandateStartDate() {
        return SIPMandateStartDate;
    }

    public String getSIPType() {
        return SIPType;
    }

    public String getTransactionAmt() {
        return TransactionAmt;
    }
}
