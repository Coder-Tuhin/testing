package models;

public class GetSRPledgeFormDetailsModel {
    String ClientCode;
    String BeneficiaryAccountNumber;
    String ISIN;
    String CompName;
    String TotalQuantity;
    String AvailableQuantity;
    String ConfirmedSRQuantity;
    String PendingSRQuantity;
    String Close;
    String DATE;
    String BuyingPower;
    String PledgeAllow;
    String ConfirmedFAQuantity;
    String PendingFAQuantity;
    String FundingPerShare;
    String MaxFAQuantity;
    String AvailableFAQuantity;

    public String getUtilisedMargin() {
        return UtilisedMargin;
    }

    public void setUtilisedMargin(String utilisedMargin) {
        UtilisedMargin = utilisedMargin;
    }

    String UtilisedMargin;

    public String getRiskMargin() {
        return RiskMargin;
    }

    public void setRiskMargin(String riskMargin) {
        RiskMargin = riskMargin;
    }

    String RiskMargin;
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

    public String getTotalQuantity() {
        return TotalQuantity;
    }

    public void setTotalQuantity(String totalQuantity) {
        TotalQuantity = totalQuantity;
    }

    public String getAvailableQuantity() {
        return AvailableQuantity;
    }

    public void setAvailableQuantity(String availableQuantity) {
        AvailableQuantity = availableQuantity;
    }

    public String getConfirmedSRQuantity() {
        return ConfirmedSRQuantity;
    }

    public void setConfirmedSRQuantity(String confirmedSRQuantity) {
        ConfirmedSRQuantity = confirmedSRQuantity;
    }

    public String getPendingSRQuantity() {
        return PendingSRQuantity;
    }

    public void setPendingSRQuantity(String pendingSRQuantity) {
        PendingSRQuantity = pendingSRQuantity;
    }

    public String getClose() {
        return Close;
    }

    public void setClose(String close) {
        Close = close;
    }

    public String getDATE() {
        return DATE;
    }

    public void setDATE(String DATE) {
        this.DATE = DATE;
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

    public String getConfirmedFAQuantity() {
        return ConfirmedFAQuantity;
    }

    public void setConfirmedFAQuantity(String confirmedFAQuantity) {
        ConfirmedFAQuantity = confirmedFAQuantity;
    }

    public String getPendingFAQuantity() {
        return PendingFAQuantity;
    }

    public void setPendingFAQuantity(String pendingFAQuantity) {
        PendingFAQuantity = pendingFAQuantity;
    }

    public String getFundingPerShare() {
        return FundingPerShare;
    }

    public void setFundingPerShare(String fundingPerShare) {
        FundingPerShare = fundingPerShare;
    }

    public String getMaxFAQuantity() {
        return MaxFAQuantity;
    }

    public void setMaxFAQuantity(String maxFAQuantity) {
        MaxFAQuantity = maxFAQuantity;
    }

    public String getAvailableFAQuantity() {
        return AvailableFAQuantity;
    }

    public void setAvailableFAQuantity(String availableFAQuantity) {
        AvailableFAQuantity = availableFAQuantity;
    }



    // table2
    String MaxFunding;
    String MaxFundingPerScrip;
    String TotalAmountFunded;
    String MaxFundingPerScripOverall;
    String MaxTotalFunding;
    String IsFundingAllowed;

    public String getMaxFunding() {
        return MaxFunding;
    }

    public void setMaxFunding(String maxFunding) {
        MaxFunding = maxFunding;
    }

    public String getMaxFundingPerScrip() {
        return MaxFundingPerScrip;
    }

    public void setMaxFundingPerScrip(String maxFundingPerScrip) {
        MaxFundingPerScrip = maxFundingPerScrip;
    }

    public String getTotalAmountFunded() {
        return TotalAmountFunded;
    }

    public void setTotalAmountFunded(String totalAmountFunded) {
        TotalAmountFunded = totalAmountFunded;
    }

    public String getMaxFundingPerScripOverall() {
        return MaxFundingPerScripOverall;
    }

    public void setMaxFundingPerScripOverall(String maxFundingPerScripOverall) {
        MaxFundingPerScripOverall = maxFundingPerScripOverall;
    }

    public String getMaxTotalFunding() {
        return MaxTotalFunding;
    }

    public void setMaxTotalFunding(String maxTotalFunding) {
        MaxTotalFunding = maxTotalFunding;
    }

    public String getIsFundingAllowed() {
        return IsFundingAllowed;
    }

    public void setIsFundingAllowed(String isFundingAllowed) {
        IsFundingAllowed = isFundingAllowed;
    }

    public GetSRPledgeFormDetailsModel() {

    }
}
