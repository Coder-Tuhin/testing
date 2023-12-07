package com.ventura.venturawealth.activities.homescreen;

import com.ventura.venturawealth.R;
import java.util.ArrayList;
import java.util.Arrays;
import Structure.Response.AuthRelated.ClientLoginResponse;
import utils.GlobalClass;
import utils.PreferenceHandler;
import utils.UserSession;
import utils.VenturaException;

public class NavigationMenuHandler {

    public enum NavChildMenus{

        PAY_IN("Pay In",R.drawable.payin),
        PAY_OUT("Pay Out",R.drawable.payout),
        INTERNAL("Internal",R.drawable.interna_icon),
        UPI("UPI",R.drawable.bhim),
        FACEBOOK("Facebook",R.drawable.facebook),
        TWITTER("Twitter",R.drawable.twitter),
        VENTURA_BLOG("Ventura Blog",R.drawable.wordpress),
        YOUTUBE("Youtube",R.drawable.youtube),
        INSTAGRAM("Instagram",R.drawable.instagram),
        LINKEDINE("Linkedin",R.drawable.linkedin),
        ;
        public String _title;
        public int _icon;
        NavChildMenus(String _title,int icon) {
            this._title=_title;
            this._icon = icon;
        }

        public static NavChildMenus getNavChild(String name){
            NavChildMenus _ncm = PAY_IN;
            for (NavChildMenus ncm : NavChildMenus.values()){
                if (ncm._title.equals(name)){
                    _ncm = ncm;
                    break;
                }
            }
            return _ncm;
        }
    }

    public enum NavMenus{

        DEACTIVATE_MARGIN_TRADING("Deactivate Margin Trading", R.drawable.new_icon,null),
        ACTIVATE_MARGIN_TRADING("Activate Margin Trading", R.drawable.new_icon,null),
        NPS("NPS", R.drawable.nps_new,null),
        PARK_EARN("Park & Earn", R.drawable.nps,null),
        FUND_TRANSFER("Fund Transfer", R.drawable.add,getFundTransferMenus()),
        DASHBOARD("Reports Dashboard", R.drawable.dashboard,null),
        MY_LEDGER("My Ledger", R.drawable.ic_computer_white_24dp,null),
        EDIS("EDIS", R.drawable.ic_computer_white_24dp,null),
        PLEDGE("Pledge", R.drawable.pledge,null),
        PROFILE("Profile", R.drawable.img_man,null),//
        TRADE_SUMMARY("Trade Summary", R.drawable.ic_computer_white_24dp,null),
        HOLDINGS("Holdings", R.drawable.ic_computer_white_24dp,null),
        NOTIFICATION("Notification", R.drawable.ic_notifications_none_white_24dp,null),
        RESEARCH("Research", R.drawable.idea,null),
        OPT_CHAIN("Option Chain", R.drawable.idea,null),//
        ALERTS("Alerts", R.drawable.ic_alert_24,null),
        CHANGE_PIN("Change PIN", R.drawable.ic_vpn_key_white_24dp,null),//
        SETTING("Settings", R.drawable.ic_setting_new24,null),
        AADHAAR_UPDATE("Aadhaar Update", R.drawable.adhar,null),
        RATE_US("Rate Us", R.drawable.ic_star_half_white_24dp,null),
        SHARE_APP("Share App", R.drawable.ic_share_white_24dp,null),
        CONNECT_SOCIAL("Connect on Social Media ", R.drawable.add,getSocialMenus()),
        HELP("Help", R.drawable.ic_help_outline_white_24dp,null),
        Escalation_Matrix("Escalation Matrix", R.drawable.escalation_matrix,null),

        GOOGLEAUTH("Setup Google Authenticator",R.drawable.googleplusb,null),
        GOOGLEAUTH2("Setup Google Authenticator",R.drawable.googleplusb,null),
        LOGOUT("Log Out", R.drawable.ic_power_settings_new_white_24dp,null),
        EXIT("Exit", R.drawable.exit,null);



        public String _title;
        public int _icon;
        public ArrayList<NavChildMenus> _childList;

        NavMenus(String _title, int _icon, ArrayList<NavChildMenus> _childList) {
            this._title=_title;
            this._icon = _icon;
            this._childList = _childList;
        }
        public static void setFundTransferList(){
            FUND_TRANSFER._childList = getFundTransferMenus();
        }

        public static ArrayList<NavMenus> getNavList(){
            return new ArrayList<>(Arrays.asList(NavMenus.values()));
        }
    }

    public static ArrayList<NavMenus> getNavList(){
        NavMenus.setFundTransferList();
        ArrayList<NavMenus> _totalList = NavMenus.getNavList();
        try {
            String googleauthTag = PreferenceHandler.getSSOCreateAuth();
            if(googleauthTag.equalsIgnoreCase("1")){
                _totalList.remove(NavMenus.GOOGLEAUTH);
            }else{
                _totalList.remove(NavMenus.GOOGLEAUTH2);
            }

            if (UserSession.getClientResponse()!=null){
                if (!UserSession.getClientResponse().isActivatemargin()){
                    _totalList.remove(NavMenus.ACTIVATE_MARGIN_TRADING);
                }
                if (!UserSession.getClientResponse().isDeactivatemargin()){
                    _totalList.remove(NavMenus.DEACTIVATE_MARGIN_TRADING);
                }
                if (UserSession.getClientResponse().getAadharUpdate()
                                == ClientLoginResponse.eAadharUpdate.FALSE){
                    _totalList.remove(NavMenus.AADHAAR_UPDATE);
                }
                if(!UserSession.getClientResponse().isEDISActive() || GlobalClass.isCommodity()){
                    _totalList.remove(NavMenus.EDIS);
                }
                if(!UserSession.getClientResponse().isPledgeActive() || GlobalClass.isCommodity()){
                    _totalList.remove(NavMenus.PLEDGE);
                }
                if(GlobalClass.isCommodity()){
                    _totalList.remove(NavMenus.DASHBOARD);
                }
            }else{
                //for GUEST USER
                _totalList.remove(NavMenus.ACTIVATE_MARGIN_TRADING);
                _totalList.remove(NavMenus.DEACTIVATE_MARGIN_TRADING);
                _totalList.remove(NavMenus.AADHAAR_UPDATE);
                _totalList.remove(NavMenus.OPT_CHAIN);
                _totalList.remove(NavMenus.FUND_TRANSFER);
                _totalList.remove(NavMenus.EDIS);
                _totalList.remove(NavMenus.PLEDGE);
                _totalList.remove(NavMenus.MY_LEDGER);
                _totalList.remove(NavMenus.OPT_CHAIN);
                _totalList.remove(NavMenus.PARK_EARN);
            }
            if (!UserSession.getLoginDetailsModel().isActiveUser()){
                _totalList.remove(NavMenus.DASHBOARD);
                _totalList.remove(NavMenus.NOTIFICATION);
                _totalList.remove(NavMenus.PROFILE);
                _totalList.remove(NavMenus.ALERTS);
            }
        }catch (Exception e){
            _totalList.remove(NavMenus.ACTIVATE_MARGIN_TRADING);
            _totalList.remove(NavMenus.DEACTIVATE_MARGIN_TRADING);
            _totalList.remove(NavMenus.AADHAAR_UPDATE);
            _totalList.remove(NavMenus.OPT_CHAIN);
            _totalList.remove(NavMenus.FUND_TRANSFER);
            _totalList.remove(NavMenus.CHANGE_PIN);
            _totalList.remove(NavMenus.EDIS);
            _totalList.remove(NavMenus.PLEDGE);
            _totalList.remove(NavMenus.MY_LEDGER);
            _totalList.remove(NavMenus.OPT_CHAIN);
            _totalList.remove(NavMenus.PARK_EARN);
            VenturaException.Print(e);
        }
        return _totalList;
    }

    public static ArrayList<NavChildMenus> getFundTransferMenus(){
        ArrayList<NavChildMenus> _mlist = new ArrayList<>();
        _mlist.add(NavChildMenus.PAY_IN);
        _mlist.add(NavChildMenus.PAY_OUT);
        if(UserSession.getClientResponse()!=null &&
                UserSession.getClientResponse().getAccountType()
                        == ClientLoginResponse.ClientAccountType.EQUITYCOMM){
            _mlist.add(NavChildMenus.INTERNAL);
        }
        _mlist.add(NavChildMenus.UPI);
        return _mlist;
    }

    public static ArrayList<NavChildMenus> getSocialMenus(){
        ArrayList<NavChildMenus> _mlist = new ArrayList<>();
        _mlist.add(NavChildMenus.FACEBOOK);
        _mlist.add(NavChildMenus.TWITTER);
        _mlist.add(NavChildMenus.VENTURA_BLOG);
        _mlist.add(NavChildMenus.YOUTUBE);
        _mlist.add(NavChildMenus.INSTAGRAM);
        _mlist.add(NavChildMenus.LINKEDINE);
        return _mlist;
    }
}