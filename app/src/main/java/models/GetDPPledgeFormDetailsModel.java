package models;

public class GetDPPledgeFormDetailsModel {


    String ClientCode;
    String BeneficiaryAccountNumber;
    String  ISIN;
    String  CompName;
    String  DPQuantity;
    String  ConfirmedDPQuantity;
    String  PendingDPQuantity;
    String  VBQuantity;
    String  ConfirmedVBQuantity;
    String  PendingVBQuantity;
    String  Close;
    String  BuyingPower;
    String  PledgeAllow;

    public String getSetDpMargin() {
        return SetDpMargin;
    }

    public void setSetDpMargin(String setDpMargin) {
        SetDpMargin = setDpMargin;
    }

    String  SetDpMargin;

    public String getSetPledgeQty() {
        return SetPledgeQty;
    }

    public void setSetPledgeQty(String setPledgeQty) {
        SetPledgeQty = setPledgeQty;
    }

    public boolean isSelected() {
        return selected;
    }

    public void setSelected(boolean selected) {
        this.selected = selected;
    }

    String  SetPledgeQty;
    public boolean selected=false;

    public String getClientCode() {
        return ClientCode;
    }

    public void setClientCode(String clientCode) {
        ClientCode = clientCode;
    }

    public String getBeneficiaryAccountNumber() {
        return BeneficiaryAccountNumber;
    }

    public void setBeneficiaryAccountNumber(String beneficiaryAccountNumber) {
        BeneficiaryAccountNumber = beneficiaryAccountNumber;
    }

    public String getISIN() {
        return ISIN;
    }

    public void setISIN(String ISIN) {
        this.ISIN = ISIN;
    }

    public String getCompName() {
        return CompName;
    }

    public void setCompName(String compName) {
        CompName = compName;
    }

    public String getDPQuantity() {
        return DPQuantity;
    }

    public void setDPQuantity(String DPQuantity) {
        this.DPQuantity = DPQuantity;
    }

    public String getConfirmedDPQuantity() {
        return ConfirmedDPQuantity;
    }

    public void setConfirmedDPQuantity(String confirmedDPQuantity) {
        ConfirmedDPQuantity = confirmedDPQuantity;
    }

    public String getPendingDPQuantity() {
        return PendingDPQuantity;
    }

    public void setPendingDPQuantity(String pendingDPQuantity) {
        PendingDPQuantity = pendingDPQuantity;
    }

    public String getVBQuantity() {
        return VBQuantity;
    }

    public void setVBQuantity(String VBQuantity) {
        this.VBQuantity = VBQuantity;
    }

    public String getConfirmedVBQuantity() {
        return ConfirmedVBQuantity;
    }

    public void setConfirmedVBQuantity(String confirmedVBQuantity) {
        ConfirmedVBQuantity = confirmedVBQuantity;
    }

    public String getPendingVBQuantity() {
        return PendingVBQuantity;
    }

    public void setPendingVBQuantity(String pendingVBQuantity) {
        PendingVBQuantity = pendingVBQuantity;
    }

    public String getClose() {
        return Close;
    }

    public void setClose(String close) {
        Close = close;
    }

    public String getBuyingPower() {
        return BuyingPower;
    }

    public void setBuyingPower(String buyingPower) {
        BuyingPower = buyingPower;
    }

    public String getPledgeAllow() {
        return PledgeAllow;
    }

    public void setPledgeAllow(String pledgeAllow) {
        PledgeAllow = pledgeAllow;
    }

    public GetDPPledgeFormDetailsModel() {

    }
}
