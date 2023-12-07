package enums;

public enum eMFJsonTag {
    CLINETCODE(1,"clientcode"), ASSET(2,"assettype"), SCHEMECODE(3,"schemecode")
    ,OPTION(4,"Option"),FOLIONO(5,"foliono"),SCHNAME(6,"schemename"),INVESTAMT(7,"investamt")
    ,REDEEMOPT(8,"RedeemOpt"),REDEEM(9,"Redeem"),LIQUIDREDEEM(10,"LiquidRedeemPayout")
    ,WITHDRAWAL(11,"Withdrawal"),FREQUENCY(12,"Frequency"),EXECUTIONDAY(13,"ExecutionDay")
    ,STARTDATE(14,"StartDate"),ENDDATE(15,"EndDate"),PERIOD(16,"Period"),TOSCHEMECODE(17,"ToSchemeCode")
    ,TRANSFERAMT(18,"TransferAmount"),SWITCHAMT(19,"SwitchAmount"),SWITCHUNITS(20,"SwitchUnits")
    ,REDEEMDATE(21,"RedemptionDate"),SIPAMT(22,"SIPAmount"),VALIDUNTILCANCEL(23,"ValidUntilCancelled")
    ,JDATA(24,"jsondata"),FORMSCR(25,"fromscreen"),KEYWORD(26,"keyword"),SUBCATEGORY(27,"subcategory")
    ,AMCCODE(28,"amc"),FAMILYCODE(29,"family"),FINANCIALYR(30,"financialyear"),MASTERTYPE(31,"mastertype")
    ,BANKID(32,"bankid"),CATEGORY(33,"category"),TSRNO(34,"TSrNo"),SIPSRNO(35,"SIPSrNo"),SIPTYPE(36,"SIPType")
    ,SIPMANDATE(37,"SIPMandateCode"),PAN(38,"pan"),ACCOUNTNO(39,"AccountNo"),ACCOUNTTYPE(40,"AccountType")
    ,BANK_BRANCH_NAME(41,"BankBranchName"),BANK_CITY(42,"BankBranchCity"),BANK_NAME(43,"BankName")
    ,MICR_CODE(44,"MICRCode"),IFSC_CODE(45,"IFSCCode"),ACCOUNT_HOLDER_NAME(46,"HolderName"),MANDATE_AMT(47,"MandateAmt")
    ,BANK_EXISTS(48,"BankExists"),PDF_FILE_NAME(49,"PdfFilename")
    ,PERSONID(50,"personId"),CLIENTTYPE(51,"clienttype"),IPOCode(52,"IPOCode")
    ,UPICode(53,"UPICode"),BidQty(54,"BidQty"),Bidamt(55,"Bidamt"),
    Category(56,"Category"),INSTNAME(57,"InstName"),entrymode(58,"entrymode"),
    SIPMANDATENAME(59,"SIPMandate"),ORDERNO(60,"OrderNo"),TRANSADATE(61,"TransDate"),
    InvType(62,"InvType"),BidPrice(63,"BidPrice"),
    ;

    public short value;
    public String name;

    eMFJsonTag(int value, String name) {
        this.value = (short)value;
        this.name=name;
    }
}
