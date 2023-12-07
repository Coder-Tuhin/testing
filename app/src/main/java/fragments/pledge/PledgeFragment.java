package fragments.pledge;

//09DECSHIVA

import static utils.Formatter.DecimalLessIncludingComma;
import static utils.Formatter.TwoDecimalIncludingComma;

import android.annotation.SuppressLint;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Rect;
import android.graphics.Typeface;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.RelativeLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AlertDialog;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.ventura.venturawealth.R;
import com.ventura.venturawealth.VenturaApplication;
import com.ventura.venturawealth.activities.homescreen.HomeActivity;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.w3c.dom.Text;
import org.xml.sax.InputSource;
import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;
import org.xmlpull.v1.XmlPullParserFactory;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.StringReader;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Timer;
import java.util.Vector;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import Structure.Response.AuthRelated.ClientLoginResponse;
import adapters.PledgeAdapter;
import adapters.PledgeAdapterSr;
import connection.HttpProcessNew;
import enums.eConstant;
import enums.ePrefTAG;
import interfaces.KeyBoadInterface;
import interfaces.ModelTotalMargin;
import interfaces.PledgeMarginTotalCount;
import interfaces.RecyclerViewSmooth;
import models.GetDPPledgeFormDetailsModel;
import models.GetSRPledgeFormDetailsModel;
import utils.Constants;
import utils.Encriptions.ReusableLogics;
import utils.Formatter;
import utils.GlobalClass;
import utils.MobileInfo;
import utils.UserSession;
import utils.VenturaException;
import view.AlertBox;
import wealth.Dialogs;
import wealth.VenturaServerConnect;

public class PledgeFragment extends Fragment implements RecyclerViewSmooth, ModelTotalMargin, View.OnClickListener, PledgeMarginTotalCount, KeyBoadInterface {
    ArrayList<GetDPPledgeFormDetailsModel> arrayListDpp, arrayListVbb, arrayListDppCheck;
    ArrayList<GetSRPledgeFormDetailsModel> arrayListSR, arrayListSRCheck;
    ArrayList<GetSRPledgeFormDetailsModel> arrayListSRMargin;
    RecyclerView.LayoutManager mLayoutManager;
    ArrayList<String> arrayListDppString, arrayListVbbString, arrayListMarginString, arrayListFundingString;
    PledgeAdapter pledgeAdapter;
    PledgeAdapterSr pledgeAdapterSr;
    RecyclerViewSmooth recyclerViewSmooth;
    ModelTotalMargin modelTotalMargin;
    public final static String URL = "https://mobileapi.ventura1.com/Service.asmx";
    RecyclerView recyclerView;
    RadioGroup indexationRd, indexationRd2;
    RadioButton RbtsharesinDp, RbtsharesinVb, RbtnPledgeformargin, RbtnpledgefrFunding;
    View sharesinDpView, sharesinVbView, PledgeformarginView, pledgefrFundingView, sharesinDpView1, sharesinVbView1, PledgeformarginView1, pledgefrFundingView1;
    TextView txtSpinnerData, clCodeProfile, nodata_txtview, clNameProfile, txt_maxfundLabel, total_pledged_value, total_pledged_value1, txttotalpledgedValue, txtsharedinStock, txt_totalBuyingPowerPershareInDp, txt_totalBuyingPowerPershareInDp1, txt_totalBuyingPowerPershareInVb, txt_totalBuyingPowerPershareInVb1, txt_totalBuyingPowerPershareInF, txt_totalBuyingPowerPershareInF1, txt_totalBuyingPowerPershareInM, txt_totalBuyingPowerPershareInM1, txt_maxfund0, txt_maxfund1, txt_maxfund2;
    ImageView imgNote;
    LinearLayout lineartotal_pledged_value1, linear_maxFund1, linearLayoutbottm;

    //
    RelativeLayout relativeMTFmargin;
    TextView total_MTF_utilisedmargin1;
    LinearLayout lineartotal_pledged_value2;
    TextView total_MTF_utilisedmargin2;
    //
    String ActionUrl = "";
    private View mView;
    Button btnPledgeMargin;
    ArrayList<String> spinnerValues;
    Spinner spinner;
    String ClientId = "";
    Timer myTimer;
    double maxFundingAmount = 0;
    double availableMtfmargin = 0;

    String availableMTFmargin;
    StringBuffer stringBuffer;
    PledgeMarginTotalCount pledgeMarginTotalCount;

    public static boolean shouldRefreshOnResume = false;

    //    String userid = "18J018";
    int total_PledgedCountDp = 0;
    int total_PledgedCountVb = 0;
    int total_PledgedCountMargin = 0;
    double total_PledgedCountFunding = 0;

    KeyBoadInterface keyBoadInterface;
    public static AlertDialog.Builder malertDialog;
    static int alertCounter = 0;
    public static AlertDialog alertDialog;

    public static PledgeFragment newInstance() {
        PledgeFragment fragment = new PledgeFragment();
        return fragment;
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
    }

    String MaxFunding = "";
    String MaxFundingPerScrip = "";
    String TotalAmountFunded = "";
    String MaxFundingPerScripOverall = "";
    String MaxTotalFunding = "";
    String IsFundingAllowed = "";


    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        if (mView == null) {
            mView = inflater.inflate(R.layout.pledgefragmentlayout1, container, false);
            try {
                init_();
                new GetTaskFirst().execute();
            } catch (Exception e) {
                VenturaException.Print(e);
            }
        }


        return mView;
    }

    @Override
    public void totalMargin(String totalMarginCount) {
    }

    @Override
    public void Smooth(int i) {

    }


    private HttpProcessNew.OnHttpResponse _ohttprPlaceOrder = new HttpProcessNew.OnHttpResponse() {
        @Override
        public void onHttpResponse(String response) {
            GlobalClass.dismissdialog();
            try {
                boolean isSuccess = xmlDataPledgeOrderDetails(response);
            } catch (Exception e) {
                VenturaException.Print(e);

            }
        }
    };
    private HttpProcessNew.OnHttpResponse _ohttprDPVBresponse = new HttpProcessNew.OnHttpResponse() {
        @Override
        public void onHttpResponse(String response) {

            getActivity().runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    try {
                        boolean isSuccess = xmlDataDPVB(response);
                    } catch (Exception e) {
                        VenturaException.Print(e);

                    }

                }
            });

        }
    };
    private HttpProcessNew.OnHttpResponse _ohttActiveSessionBresponse = new HttpProcessNew.OnHttpResponse() {
        @Override
        public void onHttpResponse(String response) {

            getActivity().runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    try {
                        GlobalClass.log("activeSessionresponse", response);
                        boolean isSuccess = xmlDataActiveSession(response);
                    } catch (Exception e) {
                        VenturaException.Print(e);

                    }

                }
            });

        }
    };
    private HttpProcessNew.OnHttpResponse _ohttprDPIDresponse = new HttpProcessNew.OnHttpResponse() {
        @Override
        public void onHttpResponse(String response) {
            getActivity().runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    try {
                        boolean isSuccess = xmlDataClientID(response);
                    } catch (Exception e) {
                        VenturaException.Print(e);

                    }
                }
            });
        }
    };
    private HttpProcessNew.OnHttpResponse _ohttprSRresponse = new HttpProcessNew.OnHttpResponse() {
        @Override
        public void onHttpResponse(String response) {
            getActivity().runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    try {

                        Log.e("responseSR_data", response);
                        boolean isSuccess = xmlData1(response);

                    } catch (Exception e) {
                        VenturaException.Print(e);

                    }
                }
            });


        }
    };
    private HttpProcessNew.OnHttpResponse _ohttprSRresponse22 = new HttpProcessNew.OnHttpResponse() {
        @Override
        public void onHttpResponse(String response) {
            getActivity().runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    try {

                        Log.e("responzzzzzzzzzz", response);

                        boolean isSuccess = xmlData2(response);

                    } catch (Exception e) {
                        VenturaException.Print(e);


                    }
                }
            });


        }
    };

    @SuppressLint("LongLogTag")
    public boolean xmlDataDPVB(String str) {
        GlobalClass.dismissdialog();
        boolean isSuccess = false;
        Vector rowData = null;
        linear_maxFund1.setVisibility(View.INVISIBLE);
        txt_maxfundLabel.setVisibility(View.INVISIBLE);
        // new added.
        relativeMTFmargin.setVisibility(View.INVISIBLE);

        try {
            if (!str.equalsIgnoreCase("")) {
                if (str.contains("&#x0;")) {
                    str = str.replaceAll("&#x0;", "");
                }
                DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
                DocumentBuilder dBuilder = dbFactory.newDocumentBuilder();
                InputSource s = new InputSource(new StringReader(str));
                Document doc = dBuilder.parse(s);
                doc.getDocumentElement().normalize();
                rowData = new Vector();
                NodeList nList = doc.getElementsByTagName("NewDataSet");
                arrayListDpp.clear();
                arrayListVbb.clear();
                for (int temp = 0; temp < nList.getLength(); temp++) {
                    Node nNode = nList.item(temp);
                    if (nNode.getNodeType() == Node.ELEMENT_NODE && nNode.hasChildNodes()) {
                        Element eElement = (Element) nNode;
                        NodeList nodeTable = eElement.getChildNodes();
                        for (int i = 0; i < nodeTable.getLength(); i++) {
                            Vector data = new Vector();
                            GlobalClass.log("xmlDataNode: ", nodeTable.item(i).getNodeName());
                            GetDPPledgeFormDetailsModel getDPPledgeFormDetailsModel = new GetDPPledgeFormDetailsModel();
                            if (!nodeTable.item(i).getNodeName().equalsIgnoreCase("#text")) {
                                NodeList childNode = nodeTable.item(i).getChildNodes();
                                for (int j = 0; j < childNode.getLength(); j++) {
                                    String nodeName = childNode.item(j).getNodeName();
                                    if (!nodeName.equalsIgnoreCase("#text")) {
                                        GlobalClass.log(nodeName + " ::  " + childNode.item(j).getTextContent());
                                        if (nodeName.equals("ClientCode")) {
                                            getDPPledgeFormDetailsModel.setClientCode(String.valueOf(childNode.item(j).getTextContent()));
                                        } else if (nodeName.equals("BeneficiaryAccountNumber")) {
                                            getDPPledgeFormDetailsModel.setBeneficiaryAccountNumber(String.valueOf(childNode.item(j).getTextContent()));
                                        } else if (nodeName.equals("ISIN")) {
                                            getDPPledgeFormDetailsModel.setISIN(String.valueOf(childNode.item(j).getTextContent()));
                                        } else if (nodeName.equals("CompName")) {
                                            getDPPledgeFormDetailsModel.setCompName(String.valueOf(childNode.item(j).getTextContent()));
                                        } else if (nodeName.equals("DPQuantity")) {
                                            getDPPledgeFormDetailsModel.setDPQuantity(String.valueOf(childNode.item(j).getTextContent()));
                                        } else if (nodeName.equals("ConfirmedDPQuantity")) {
                                            getDPPledgeFormDetailsModel.setConfirmedDPQuantity(String.valueOf(childNode.item(j).getTextContent()));
                                        } else if (nodeName.equals("PendingDPQuantity")) {
                                            getDPPledgeFormDetailsModel.setPendingDPQuantity(String.valueOf(childNode.item(j).getTextContent()));
                                        } else if (nodeName.equals("VBQuantity")) {
                                            getDPPledgeFormDetailsModel.setVBQuantity(String.valueOf(childNode.item(j).getTextContent()));
                                        } else if (nodeName.equals("ConfirmedVBQuantity")) {
                                            getDPPledgeFormDetailsModel.setConfirmedVBQuantity(String.valueOf(childNode.item(j).getTextContent()));
                                        } else if (nodeName.equals("PendingVBQuantity")) {
                                            getDPPledgeFormDetailsModel.setPendingVBQuantity(String.valueOf(childNode.item(j).getTextContent()));
                                        } else if (nodeName.equals("Close")) {
                                            getDPPledgeFormDetailsModel.setClose(String.valueOf(childNode.item(j).getTextContent()));
                                        } else if (nodeName.equals("BuyingPower")) {
                                            getDPPledgeFormDetailsModel.setBuyingPower(String.valueOf(childNode.item(j).getTextContent()));
                                        } else if (nodeName.equals("PledgeAllow")) {
                                            getDPPledgeFormDetailsModel.setPledgeAllow(String.valueOf(childNode.item(j).getTextContent()));
                                        }
                                        data.add(nodeName);
                                        data.add(childNode.item(j).getTextContent());
                                    }
                                }

                                arrayListDpp.add(getDPPledgeFormDetailsModel);
                                arrayListVbb.add(getDPPledgeFormDetailsModel);


                                //checked*****
                                Iterator<GetDPPledgeFormDetailsModel> iterator = arrayListDpp.iterator();
                                while (iterator.hasNext()) {
                                    GetDPPledgeFormDetailsModel p = iterator.next();
                                    if (p.getDPQuantity().equalsIgnoreCase("0") && p.getConfirmedDPQuantity().equalsIgnoreCase("0")) {
                                        iterator.remove();
                                    }
                                }


                                Iterator<GetDPPledgeFormDetailsModel> iterator1 = arrayListVbb.iterator();
                                while (iterator1.hasNext()) {
                                    GetDPPledgeFormDetailsModel p1 = iterator1.next();
                                    if (p1.getVBQuantity().equalsIgnoreCase("0") && p1.getConfirmedVBQuantity().equalsIgnoreCase("0")) {
                                        iterator1.remove();
                                    }
                                }


                                rowData.add(data);
                            }
                        }
                    }
                }
                if (!rowData.isEmpty()) {
                    String msg = "";
                    for (Object folioV1 : rowData) {
                        Vector folioRow = (Vector) folioV1;
                        if (!folioRow.isEmpty()) {
                            for (int j = 0; j < folioRow.size(); j++) {
                                if (folioRow.get(j).toString().equalsIgnoreCase("Message")) {
                                    msg = folioRow.get(j + 1).toString();
                                    break;
                                }
                                j++;
                            }
                        }
                    }
                    if (!msg.equalsIgnoreCase("")) {
                        if (msg.toLowerCase().contains("success")) {
                            isSuccess = true;
                        }
                        GlobalClass.showAlertDialog(msg);
                    }

                }
            }

            GetSRPledgeFormDetails();

//            GetFundingPerScripOverall();

            getActivity().runOnUiThread(new Runnable() {
                @Override
                public void run() {

                    for (int k = 0; k < arrayListDpp.size(); k++) {
                        if (!arrayListDpp.get(k).getConfirmedDPQuantity().equalsIgnoreCase("0")) {
                            total_PledgedCountDp = total_PledgedCountDp + (Integer.parseInt(arrayListDpp.get(k).getConfirmedDPQuantity()) * Integer.parseInt(arrayListDpp.get(k).getBuyingPower()));
                        }
                    }
                    for (int k = 0; k < arrayListVbb.size(); k++) {
                        if (!arrayListVbb.get(k).getConfirmedVBQuantity().equalsIgnoreCase("0")) {
                            total_PledgedCountVb = total_PledgedCountVb + (Integer.parseInt(arrayListVbb.get(k).getConfirmedDPQuantity()) * Integer.parseInt(arrayListVbb.get(k).getBuyingPower()));
                        }
                    }

                    RbtsharesinDp.setTypeface(null, Typeface.BOLD);
                    RbtsharesinVb.setTypeface(null, Typeface.NORMAL);
                    RbtnPledgeformargin.setTypeface(null, Typeface.NORMAL);
                    RbtnpledgefrFunding.setTypeface(null, Typeface.NORMAL);
                    sharesinDpView.setVisibility(View.VISIBLE);
                    sharesinVbView.setVisibility(View.GONE);
                    sharesinVbView1.setVisibility(View.VISIBLE);
                    PledgeformarginView.setVisibility(View.GONE);
                    PledgeformarginView1.setVisibility(View.VISIBLE);
                    pledgefrFundingView.setVisibility(View.GONE);
                    pledgefrFundingView1.setVisibility(View.VISIBLE);

                    if (arrayListDpp.size() > 0) {
                       /* if (arrayListDpp.get(0).getPledgeAllow().equalsIgnoreCase("0")) {
//                            nodata_txtview.setVisibility(View.VISIBLE);
//                            nodata_txtview.setText("Pledge Services are available from Monday to Friday between 9:00 am to 4:00 pm");
                            Alert("Pledge Services are available from Monday to Friday between 9:00 am to 4:00 pm",GlobalClass.latestContext);

                        } else {

                            mLayoutManager = new LinearLayoutManager(getActivity());
                            recyclerView.setLayoutManager(mLayoutManager);
                            recyclerView.setItemAnimator(new DefaultItemAnimator());
                            recyclerView.setVisibility(View.VISIBLE);
                            nodata_txtview.setVisibility(View.GONE);
                            pledgeAdapter = new PledgeAdapter(recyclerView, keyBoadInterface, pledgeMarginTotalCount, arrayListDpp, getActivity(), recyclerViewSmooth, "DP", modelTotalMargin);
                            recyclerView.setAdapter(pledgeAdapter);
                            pledgeAdapter.notifyDataSetChanged();
                            btnPledgeMargin.setText("PLEDGE FOR MARGIN");
                            btnPledgeMargin.setTag("PLEDGE FOR DP MARGIN");
                        }*/

                        mLayoutManager = new LinearLayoutManager(getActivity());
                        recyclerView.setLayoutManager(mLayoutManager);
                        recyclerView.setItemAnimator(new DefaultItemAnimator());
                        recyclerView.setVisibility(View.VISIBLE);
                        nodata_txtview.setVisibility(View.GONE);
                        pledgeAdapter = new PledgeAdapter(recyclerView, keyBoadInterface, pledgeMarginTotalCount, arrayListDpp, getActivity(), recyclerViewSmooth, "DP", modelTotalMargin);
                        recyclerView.setAdapter(pledgeAdapter);
                        pledgeAdapter.notifyDataSetChanged();
                        btnPledgeMargin.setText("PLEDGE FOR MARGIN");
                        btnPledgeMargin.setTag("PLEDGE FOR DP MARGIN");


                    } else {
                        nodata_txtview.setVisibility(View.VISIBLE);
                        recyclerView.setVisibility(View.GONE);
                        SharesInVbMethod(1);

                    }
                }
            });

        } catch (Exception e) {
            VenturaException.Print(e);

        }
        return isSuccess;
    }

    @SuppressLint("LongLogTag")
    public boolean xmlDataActiveSession(String str) {
        GlobalClass.dismissdialog();
        boolean isSuccess = false;
        Vector rowData = null;
        linear_maxFund1.setVisibility(View.INVISIBLE);
        txt_maxfundLabel.setVisibility(View.INVISIBLE);
        ReusableLogics.setKeyPledgeActiveSession(getActivity(), "0");
        try {
            if (!str.equalsIgnoreCase("")) {
                if (str.contains("&#x0;")) {
                    str = str.replaceAll("&#x0;", "");
                }
                DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
                DocumentBuilder dBuilder = dbFactory.newDocumentBuilder();
                InputSource s = new InputSource(new StringReader(str));
                Document doc = dBuilder.parse(s);
                doc.getDocumentElement().normalize();
                rowData = new Vector();
                NodeList nList = doc.getElementsByTagName("NewDataSet");
                arrayListDpp.clear();
                arrayListVbb.clear();
                for (int temp = 0; temp < nList.getLength(); temp++) {
                    Node nNode = nList.item(temp);
                    if (nNode.getNodeType() == Node.ELEMENT_NODE && nNode.hasChildNodes()) {
                        Element eElement = (Element) nNode;
                        NodeList nodeTable = eElement.getChildNodes();
                        for (int i = 0; i < nodeTable.getLength(); i++) {
                            Vector data = new Vector();
                            GlobalClass.log("xmlDataNode: ", nodeTable.item(i).getNodeName());
                            GetDPPledgeFormDetailsModel getDPPledgeFormDetailsModel = new GetDPPledgeFormDetailsModel();
                            if (!nodeTable.item(i).getNodeName().equalsIgnoreCase("#text")) {
                                NodeList childNode = nodeTable.item(i).getChildNodes();
                                for (int j = 0; j < childNode.getLength(); j++) {
                                    String nodeName = childNode.item(j).getNodeName();
                                    if (!nodeName.equalsIgnoreCase("#text")) {
                                        GlobalClass.log(nodeName + " ::  " + childNode.item(j).getTextContent());
                                        if (nodeName.equals("Message")) {
                                            GlobalClass.log("Message", childNode.item(j).getTextContent());
                                            ReusableLogics.setKeyPledgeActiveSession(getActivity(), childNode.item(j).getTextContent());
                                        }
                                    }
                                }

                            }
                        }
                    }
                }

                sendActivateMarginReq();

            }

        } catch (Exception e) {
            VenturaException.Print(e);

        }
        return isSuccess;
    }


    @SuppressLint("LongLogTag")
    public boolean xmlDataPledgeOrderDetails(String str) {
        boolean isSuccess = false;
        Vector rowData = null;

        try {
            if (!str.equalsIgnoreCase("")) {
                if (str.contains("&#x0;")) {
                    str = str.replaceAll("&#x0;", "");
                }
                DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
                DocumentBuilder dBuilder = dbFactory.newDocumentBuilder();
                InputSource s = new InputSource(new StringReader(str));
                Document doc = dBuilder.parse(s);
                doc.getDocumentElement().normalize();
                rowData = new Vector();
                NodeList nList = doc.getElementsByTagName("NewDataSet");
                // arrayListDpp.clear();
                for (int temp = 0; temp < nList.getLength(); temp++) {
                    Node nNode = nList.item(temp);
                    if (nNode.getNodeType() == Node.ELEMENT_NODE && nNode.hasChildNodes()) {
                        Element eElement = (Element) nNode;
                        NodeList nodeTable = eElement.getChildNodes();
                        for (int i = 0; i < nodeTable.getLength(); i++) {
                            Vector data = new Vector();
                            GlobalClass.log("xmlDataNodePlaced: ", nodeTable.item(i).getNodeName());
                            if (!nodeTable.item(i).getNodeName().equalsIgnoreCase("#text")) {
                                NodeList childNode = nodeTable.item(i).getChildNodes();
                                for (int j = 0; j < childNode.getLength(); j++) {
                                    String nodeName = childNode.item(j).getNodeName();
                                    if (!nodeName.equalsIgnoreCase("#text")) {
                                        if (nodeName.equalsIgnoreCase("ActionUrl")) {
                                            ActionUrl = childNode.item(j).getTextContent();
                                        }
                                        data.add(nodeName);
                                        data.add(childNode.item(j).getTextContent());
                                    }
                                }
                                rowData.add(data);
                            }
                        }
                    }
                }
                if (!rowData.isEmpty()) {
                    String msg = "";
                    for (Object folioV1 : rowData) {
                        Vector folioRow = (Vector) folioV1;
                        if (!folioRow.isEmpty()) {
                            for (int j = 0; j < folioRow.size(); j++) {
                                if (folioRow.get(j).toString().equalsIgnoreCase("Message")) {
                                    msg = folioRow.get(j + 1).toString();
                                    break;
                                }
                                j++;
                            }
                        }
                    }

                    if (!msg.equalsIgnoreCase("")) {
                        if (msg.toLowerCase().contains("success")) {
                            isSuccess = true;
                        }
                        GlobalClass.showAlertDialog(msg);
                    }
                }

                if (ActionUrl.equalsIgnoreCase("")) {

                } else {
                    displayInWebView(ActionUrl);
                }

            }
        } catch (Exception e) {
            VenturaException.Print(e);

        }
        return isSuccess;
    }

    @SuppressLint("LongLogTag")
    public boolean xmlDataClientID(String str) {
        GlobalClass.dismissdialog();

        boolean isSuccess = false;
        Vector rowData = null;
        try {
            if (!str.equalsIgnoreCase("")) {
                if (str.contains("&#x0;")) {
                    str = str.replaceAll("&#x0;", "");
                }
                DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
                DocumentBuilder dBuilder = dbFactory.newDocumentBuilder();
                InputSource s = new InputSource(new StringReader(str));
                Document doc = dBuilder.parse(s);
                doc.getDocumentElement().normalize();
                rowData = new Vector();
                NodeList nList = doc.getElementsByTagName("NewDataSet");
                arrayListDpp.clear();
                for (int temp = 0; temp < nList.getLength(); temp++) {
                    Node nNode = nList.item(temp);
                    if (nNode.getNodeType() == Node.ELEMENT_NODE && nNode.hasChildNodes()) {
                        Element eElement = (Element) nNode;
                        NodeList nodeTable = eElement.getChildNodes();
                        for (int i = 0; i < nodeTable.getLength(); i++) {
                            Vector data = new Vector();
                            GlobalClass.log("xmlDataNode: ", nodeTable.item(i).getNodeName());
                            GetDPPledgeFormDetailsModel getDPPledgeFormDetailsModel = new GetDPPledgeFormDetailsModel();
                            if (!nodeTable.item(i).getNodeName().equalsIgnoreCase("#text")) {
                                NodeList childNode = nodeTable.item(i).getChildNodes();
                                for (int j = 0; j < childNode.getLength(); j++) {
                                    String nodeName = childNode.item(j).getNodeName();
                                    if (!nodeName.equalsIgnoreCase("#text")) {
                                        GlobalClass.log("nodeName", nodeName);
                                        if (nodeName.equals("ClientID")) {
                                            spinnerValues.add(childNode.item(j).getTextContent());
                                        }
                                        data.add(nodeName);
                                        data.add(childNode.item(j).getTextContent());
                                    }
                                }

                                if (spinnerValues.size() == 1) {
                                    getActivity().runOnUiThread(new Runnable() {
                                        @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
                                        @Override
                                        public void run() {
                                            ArrayAdapter<String> adapter = new ArrayAdapter<String>(getActivity(), R.layout.custom_spinner_item, spinnerValues);
                                            adapter.setDropDownViewResource(R.layout.custom_spinner_itemdropdown);
                                            spinner.setAdapter(adapter);
                                            spinner.setVisibility(View.VISIBLE);
                                            spinner.setEnabled(false);
                                            spinner.setBackground(null);
                                            txtSpinnerData.setVisibility(View.GONE);


                                            spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                                                @Override
                                                public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                                                    ClientId = spinner.getItemAtPosition(spinner.getSelectedItemPosition()).toString();
                                                    linear_maxFund1.setVisibility(View.INVISIBLE);
                                                    txt_maxfundLabel.setVisibility(View.INVISIBLE);
                                                    // new added
                                                    relativeMTFmargin.setVisibility(View.INVISIBLE);

                                                    RbtsharesinDp.setBackground(null);
                                                    RbtsharesinVb.setBackground(null);
                                                    RbtnPledgeformargin.setBackground(null);
                                                    RbtnpledgefrFunding.setBackground(null);
                                                    GetDPPledgeFormDetails(ClientId);
                                                }

                                                @Override
                                                public void onNothingSelected(AdapterView<?> adapterView) {
                                                }
                                            });


                                        }
                                    });


                                } else {
                                    getActivity().runOnUiThread(new Runnable() {
                                        @Override
                                        public void run() {
                                            ArrayAdapter<String> adapter = new ArrayAdapter<String>(getActivity(), R.layout.custom_spinner_item, spinnerValues);
                                            adapter.setDropDownViewResource(R.layout.custom_spinner_itemdropdown);
                                            spinner.setAdapter(adapter);
                                            spinner.setVisibility(View.VISIBLE);
                                            txtSpinnerData.setVisibility(View.GONE);
                                            // spinner.setEnabled(true);
                                        }
                                    });

                                }
                                rowData.add(data);
                            }
                        }
                    }
                }

                if (!rowData.isEmpty()) {
                    String msg = "";
                    for (Object folioV1 : rowData) {
                        Vector folioRow = (Vector) folioV1;
                        if (!folioRow.isEmpty()) {
                            for (int j = 0; j < folioRow.size(); j++) {
                                if (folioRow.get(j).toString().equalsIgnoreCase("Message")) {
                                    msg = folioRow.get(j + 1).toString();
                                    break;
                                }
                                j++;
                            }
                        }
                    }

                    if (!msg.equalsIgnoreCase("")) {
                        if (msg.toLowerCase().contains("success")) {
                            isSuccess = true;
                        }
                        new GetTaskFirst().execute();
                    }
                }
            }

        } catch (Exception e) {
            VenturaException.Print(e);

        }

        return isSuccess;
    }

    @SuppressLint("LongLogTag")
    public boolean xmlData1(String str) {
        GlobalClass.dismissdialog();
        boolean isSuccess = false;
        Vector rowData = null;
        try {
            if (!str.equalsIgnoreCase("")) {
                if (str.contains("&#x0;")) {
                    str = str.replaceAll("&#x0;", "");
                }
                DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
                DocumentBuilder dBuilder = dbFactory.newDocumentBuilder();
                InputSource s = new InputSource(new StringReader(str));
                Document doc = dBuilder.parse(s);
                doc.getDocumentElement().normalize();
                rowData = new Vector();
                NodeList nList = doc.getElementsByTagName("NewDataSet");
                GlobalClass.log("SR_RESPONSE", str);
                //Log.e("SR_RESPONSE 1: ", str);
                arrayListSR.clear();
                arrayListSRMargin.clear();
                for (int temp = 0; temp < nList.getLength(); temp++) {
                    Node nNode = nList.item(temp);
                    if (nNode.getNodeType() == Node.ELEMENT_NODE && nNode.hasChildNodes()) {
                        Element eElement = (Element) nNode;
                        NodeList nodeTable = eElement.getChildNodes();

                        for (int i = 0; i < nodeTable.getLength(); i++) {
                            Vector data = new Vector();
                            GetSRPledgeFormDetailsModel getSRPledgeFormDetailsModel = new GetSRPledgeFormDetailsModel();
                            GlobalClass.log("xmlData1NodeName: ", nodeTable.item(i).getNodeName());
//                            Log.e("xmlData1NodeName: ", nodeTable.item(i).getNodeName());

//                            Log.e("childNode00", nodeTable.item(i).getNodeName());

//                            if (!nodeTable.item(i).getNodeName().equalsIgnoreCase("#text") && !nodeTable.item(i).getNodeName().equalsIgnoreCase("Table1")) {
                            if (nodeTable.item(i).getNodeName().equalsIgnoreCase("Table")) {
                                NodeList childNode = nodeTable.item(i).getChildNodes();
                                // Log.e("childNode11", String.valueOf( nodeTable.item(i).getChildNodes()));
                                for (int j = 0; j < childNode.getLength(); j++) {
                                    String nodeName = childNode.item(j).getNodeName();
                                    // Log.e("nodeName",nodeName);

                                    if (!nodeName.equalsIgnoreCase("#text")) {
                                        if (nodeName.equals("ClientCode")) {
                                            getSRPledgeFormDetailsModel.setClientCode(String.valueOf(childNode.item(j).getTextContent()));
                                        } else if (nodeName.equals("BeneficiaryAccountNumber")) {
                                            getSRPledgeFormDetailsModel.setBeneficiaryAccountNumber(String.valueOf(childNode.item(j).getTextContent()));
                                        } else if (nodeName.equals("ISIN")) {
                                            getSRPledgeFormDetailsModel.setISIN(String.valueOf(childNode.item(j).getTextContent()));
                                        } else if (nodeName.equals("CompName")) {
                                            getSRPledgeFormDetailsModel.setCompName(String.valueOf(childNode.item(j).getTextContent()));
                                        } else if (nodeName.equals("TotalQuantity")) {
                                            getSRPledgeFormDetailsModel.setTotalQuantity(String.valueOf(childNode.item(j).getTextContent()));
                                        } else if (nodeName.equals("AvailableQuantity")) {
                                            getSRPledgeFormDetailsModel.setAvailableQuantity(String.valueOf(childNode.item(j).getTextContent()));
                                        } else if (nodeName.equals("ConfirmedSRQuantity")) {
                                            getSRPledgeFormDetailsModel.setConfirmedSRQuantity(String.valueOf(childNode.item(j).getTextContent()));
                                        } else if (nodeName.equals("PendingSRQuantity")) {
                                            getSRPledgeFormDetailsModel.setPendingSRQuantity(String.valueOf(childNode.item(j).getTextContent()));
                                        } else if (nodeName.equals("Close")) {
                                            getSRPledgeFormDetailsModel.setClose(String.valueOf(childNode.item(j).getTextContent()));
                                        } else if (nodeName.equals("DATE")) {
                                            getSRPledgeFormDetailsModel.setDATE(String.valueOf(childNode.item(j).getTextContent()));
                                        } else if (nodeName.equals("BuyingPower")) {
                                            //
                                            double d1 = Double.parseDouble(childNode.item(j).getTextContent());
                                            // convert into int
                                            int value = (int) d1;
                                            getSRPledgeFormDetailsModel.setBuyingPower(String.valueOf(value));
                                        } else if (nodeName.equals("PledgeAllow")) {
                                            getSRPledgeFormDetailsModel.setPledgeAllow(String.valueOf(childNode.item(j).getTextContent()));
                                        } else if (nodeName.equals("ConfirmedFAQuantity")) {
                                            getSRPledgeFormDetailsModel.setConfirmedFAQuantity(String.valueOf(childNode.item(j).getTextContent()));
                                        } else if (nodeName.equals("PendingFAQuantity")) {
                                            getSRPledgeFormDetailsModel.setPendingFAQuantity(String.valueOf(childNode.item(j).getTextContent()));
                                        } else if (nodeName.equals("FundingPerShare")) {
                                            getSRPledgeFormDetailsModel.setFundingPerShare(String.valueOf(childNode.item(j).getTextContent()));
//                                            GlobalClass.log("FundingPerShare", childNode.item(j).getTextContent());
                                            Log.e("FundingPerShare", String.valueOf(childNode.item(j).getTextContent()));

                                        } else if (nodeName.equals("MaxFAQuantity")) {
                                            getSRPledgeFormDetailsModel.setMaxFAQuantity(String.valueOf(childNode.item(j).getTextContent()));
//
                                        } else if (nodeName.equals("AvailableFAQuantity")) {
                                            getSRPledgeFormDetailsModel.setAvailableFAQuantity(String.valueOf(childNode.item(j).getTextContent()));
                                        } else if (nodeName.equals("RiskMargin")) {
                                            getSRPledgeFormDetailsModel.setRiskMargin(String.valueOf(childNode.item(j).getTextContent()));
                                        }
                                        data.add(nodeName);
                                        data.add(childNode.item(j).getTextContent());
                                    }
                                }

                                arrayListSR.add(getSRPledgeFormDetailsModel);
                                arrayListSRMargin.add(getSRPledgeFormDetailsModel);


                                //checked*****************
                                Iterator<GetSRPledgeFormDetailsModel> iterator = arrayListSR.iterator();   // funding ...
                                while (iterator.hasNext()) {
                                    GetSRPledgeFormDetailsModel p = iterator.next();
                                    if (p.getAvailableFAQuantity().equalsIgnoreCase("0") && p.getConfirmedFAQuantity().equalsIgnoreCase("0")) {
                                        iterator.remove();
                                    }
                                }

                                Iterator<GetSRPledgeFormDetailsModel> iterator1 = arrayListSRMargin.iterator();   // margin ...
                                while (iterator1.hasNext()) {
                                    GetSRPledgeFormDetailsModel p = iterator1.next();
                                    if (p.getAvailableQuantity().equalsIgnoreCase("0") && p.getConfirmedSRQuantity().equalsIgnoreCase("0")) {
                                        iterator1.remove();
                                    }
                                }

                                rowData.add(data);
                            }


                            // .. table 1
                            if (nodeTable.item(i).getNodeName().equalsIgnoreCase("Table1")) {
                                NodeList childNode = nodeTable.item(i).getChildNodes();
                                Log.e("childNode11", String.valueOf(childNode));
                                for (int j = 0; j < childNode.getLength(); j++) {
                                    String nodeName = childNode.item(j).getNodeName();
                                    if (!nodeName.equalsIgnoreCase("#text")) {
                                        if (nodeName.equals("AvailableAmount")) {
                                            availableMTFmargin = String.valueOf(childNode.item(j).getTextContent());
                                        }

                                    }
                                }
                            }


                            //  table2 ...
                            if (nodeTable.item(i).getNodeName().equalsIgnoreCase("Table2")) {
                                NodeList childNode = nodeTable.item(i).getChildNodes();
                                // Log.e("childNode11", String.valueOf(childNode));
                                for (int j = 0; j < childNode.getLength(); j++) {
                                    String nodeName = childNode.item(j).getNodeName();
                                    if (!nodeName.equalsIgnoreCase("#text")) {
                                        if (nodeName.equalsIgnoreCase("MaxFunding")) {
                                            MaxFunding = String.valueOf(childNode.item(j).getTextContent());
                                        } else if (nodeName.equalsIgnoreCase("MaxFundingPerScrip")) {
                                            MaxFundingPerScrip = String.valueOf(childNode.item(j).getTextContent());
                                            //one
                                            Log.e("MaxFundingPerScrip", MaxFundingPerScrip);
                                            VenturaApplication.getPreference().storeSharedPref("MaxFundingPerScrip", MaxFundingPerScrip);

                                        } else if (nodeName.equalsIgnoreCase("TotalAmountFunded")) {
                                            TotalAmountFunded = String.valueOf(childNode.item(j).getTextContent());
                                        } else if (nodeName.equalsIgnoreCase("MaxFundingPerScripOverall")) {
                                            MaxFundingPerScripOverall = String.valueOf(childNode.item(j).getTextContent());

                                            //two
                                            Log.e("MaxFundingPerScripOverall", MaxFundingPerScripOverall);
                                            VenturaApplication.getPreference().storeSharedPref("MaxFundingPerScripOverall", MaxFundingPerScripOverall);

                                        } else if (nodeName.equalsIgnoreCase("MaxTotalFunding")) {
                                            MaxTotalFunding = String.valueOf(childNode.item(j).getTextContent());
                                            Log.e("MaxTotalFunding11", MaxTotalFunding);
                                        } else if (nodeName.equalsIgnoreCase("IsFundingAllowed")) {
                                            IsFundingAllowed = String.valueOf(childNode.item(j).getTextContent());

                                            //three
                                            Log.e("IsFundingAllowed", IsFundingAllowed);
                                            VenturaApplication.getPreference().storeSharedPref("IsFundingAllowed", IsFundingAllowed);

                                        }
                                    }
                                }
                            }


                        }
                    }
                }
                if (!rowData.isEmpty()) {
                    String msg = "";
                    for (Object folioV1 : rowData) {
                        Vector folioRow = (Vector) folioV1;
                        if (!folioRow.isEmpty()) {
                            for (int j = 0; j < folioRow.size(); j++) {
                                if (folioRow.get(j).toString().equalsIgnoreCase("Message")) {
                                    msg = folioRow.get(j + 1).toString();
                                    break;
                                }
                                j++;
                            }
                        }
                    }
                    if (!msg.equalsIgnoreCase("")) {
                        if (msg.toLowerCase().contains("success")) {
                            isSuccess = true;
                        }
                        GlobalClass.showAlertDialog(msg);
                    }
                }

//                Log.e("00001111",MaxTotalFunding );

                // funding ..
                if (arrayListSR.size() > 0) {
                    for (int k = 0; k < arrayListSR.size(); k++) {
                        if (!arrayListSR.get(k).getConfirmedFAQuantity().equalsIgnoreCase("0")) {
                            //
                            total_PledgedCountFunding = total_PledgedCountFunding +
                                    (Double.parseDouble(arrayListSR.get(k).getConfirmedFAQuantity()) * Double.parseDouble(arrayListSR.get(k).getFundingPerShare()));
                        }
                    }

                }

                //margin..
                if (arrayListSRMargin.size() > 0) {
                    for (int k = 0; k < arrayListSRMargin.size(); k++) {
                        if (!arrayListSRMargin.get(k).getConfirmedSRQuantity().equalsIgnoreCase("0")) {
                            total_PledgedCountMargin = total_PledgedCountMargin + (Integer.parseInt(arrayListSRMargin.get(k).getConfirmedFAQuantity()) * Integer.parseInt(arrayListSRMargin.get(k).getBuyingPower()));
                        }
                    }

                }

                PledgedCountMethod(0, total_PledgedCountDp, total_PledgedCountVb, total_PledgedCountMargin, total_PledgedCountFunding);
                //funding amount

//                Log.e("0000",MaxTotalFunding );

                try {
                    FundingAmount(MaxTotalFunding); // max funding ...
                } catch (Exception e) {
                    Log.e("exception...", e.getMessage());
                }
                AvailableMTFmargin(availableMTFmargin); //available MTF margin ... ..
            }

        } catch (Exception e) {
            VenturaException.Print(e);
            Log.e("Exception..", e.getMessage());
//            GlobalClass.Alert(e.getMessage(), getActivity());

//            GlobalClass.log("SRSRSR", e.getMessage());
        }
        return isSuccess;
    }

    @SuppressLint("LongLogTag")
    public boolean xmlData2(String str) {
        GlobalClass.dismissdialog();
        boolean isSuccess = false;
        Vector rowData = null;
        try {
            if (!str.equalsIgnoreCase("")) {
                if (str.contains("&#x0;")) {
                    str = str.replaceAll("&#x0;", "");
                }
                DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
                DocumentBuilder dBuilder = dbFactory.newDocumentBuilder();
                InputSource s = new InputSource(new StringReader(str));
                Document doc = dBuilder.parse(s);
                doc.getDocumentElement().normalize();
                rowData = new Vector();
                NodeList nList = doc.getElementsByTagName("NewDataSet");
                GlobalClass.log("SR_RESPONSE", str);
                // Log.e("SR_RESPONSE: ", str);

                for (int temp = 0; temp < nList.getLength(); temp++) {
                    Node nNode = nList.item(temp);
                    if (nNode.getNodeType() == Node.ELEMENT_NODE && nNode.hasChildNodes()) {
                        Element eElement = (Element) nNode;
                        NodeList nodeTable = eElement.getChildNodes();
                        for (int i = 0; i < nodeTable.getLength(); i++) {
                            Vector data = new Vector();
                            GetSRPledgeFormDetailsModel getSRPledgeFormDetailsModel = new GetSRPledgeFormDetailsModel();
                            GlobalClass.log("xmlData1NodeName: ", nodeTable.item(i).getNodeName());
                            if (!nodeTable.item(i).getNodeName().equalsIgnoreCase("#text") && !nodeTable.item(i).getNodeName().equalsIgnoreCase("Table1")) {
                                NodeList childNode = nodeTable.item(i).getChildNodes();
                                for (int j = 0; j < childNode.getLength(); j++) {
                                    String nodeName = childNode.item(j).getNodeName();
                                    if (!nodeName.equalsIgnoreCase("#text")) {
                                        if (nodeName.equals("OverallScripValue")) {
                                            String OverallScripValue = childNode.item(j).getTextContent();
                                            Log.e("OverallScripValue", OverallScripValue);

                                            VenturaApplication.getPreference().storeSharedPref("OverallScripValue", OverallScripValue);


                                        }

                                    }

                                }
                            }
                        }
                    }
                }
            }

        } catch (Exception e) {
            VenturaException.Print(e);

            GlobalClass.log("SRSRSR", e.getMessage());
        }
        return isSuccess;
    }

    private void FundingAmount(String str) {
//        XmlPullParserHandler parser = new XmlPullParserHandler();
//        InputStream stream = new ByteArrayInputStream(str.getBytes(Charset.forName("UTF-8")));
//        String xmlOutput = parser.parse(stream);
        // Log.e("MaxTotalFunding22", str);
        maxFundingAmount = Double.parseDouble(str); // new added bu shiva .
        txt_maxfund1.setText(Formatter.convertDoubleToValue(maxFundingAmount) + "");
        if (maxFundingAmount > 0) {
            txt_maxfund2.setText((DecimalLessIncludingComma(String.valueOf(maxFundingAmount))) + "");
            txt_maxfund2.setVisibility(View.VISIBLE);
        } else {
            txt_maxfund2.setVisibility(View.GONE);
        }
    }

    private void AvailableMTFmargin(String str) {
//        XmlPullParserHandler parser = new XmlPullParserHandler();
//        InputStream stream = new ByteArrayInputStream(str.getBytes(Charset.forName("UTF-8")));
//        String xmlOutput = parser.parse(stream);
        // Log.e("MaxTotalFunding22", str);
        availableMtfmargin = Double.parseDouble(str); // new added bu shiva .
        total_MTF_utilisedmargin1.setText(Formatter.convertDoubleToValue(availableMtfmargin) + "");
        if (availableMtfmargin > 0) {
            total_MTF_utilisedmargin2.setText((DecimalLessIncludingComma(String.valueOf(availableMtfmargin))) + "");
            total_MTF_utilisedmargin2.setVisibility(View.VISIBLE);
        } else {
            total_MTF_utilisedmargin2.setVisibility(View.GONE);
        }
    }


    private void sendActivateMarginReq() {
        try {
            String _url = "https://mobileapi.ventura1.com/Service.asmx/GetClientIDList";
            HashMap<String, String> dataMap = new HashMap<>();
            dataMap.put("userid", UserSession.getLoginDetailsModel().getUserID()); //18J018
            dataMap.put("authenid", UserSession.getClientResponse().getAuthenticationId());
            HttpProcessNew _httpProcess = new HttpProcessNew(_url, HttpProcessNew.RequestMethod.POST, dataMap, _ohttprDPIDresponse);
            _httpProcess.execute();
        } catch (Exception e) {
            VenturaException.Print(e);
        }
    }
    //

    private void GetDPPledgeFormDetails(String clientID) {
        try {
            String _url = "https://mobileapi.ventura1.com/Service.asmx/GetDPPledgeFormDetails";
            HashMap<String, String> dataMap = new HashMap<>();
            dataMap.put("userid", UserSession.getLoginDetailsModel().getUserID()); //18J018
            dataMap.put("authenid", UserSession.getClientResponse().getAuthenticationId());
            dataMap.put("ClientID", clientID);
            HttpProcessNew _httpProcess = new HttpProcessNew(_url, HttpProcessNew.RequestMethod.POST, dataMap, _ohttprDPVBresponse);
            _httpProcess.execute();
        } catch (Exception e) {
            VenturaException.Print(e);
        }
    }

    private void GetSRPledgeFormDetails() {
        try {
            String _url = "https://mobileapi.ventura1.com/Service.asmx/GetSRPledgeFormDetailsNew";
            HashMap<String, String> dataMap = new HashMap<>();
            dataMap.put("userid", UserSession.getLoginDetailsModel().getUserID()); //18J018
            dataMap.put("authenid", UserSession.getClientResponse().getAuthenticationId());
            HttpProcessNew _httpProcess = new HttpProcessNew(_url, HttpProcessNew.RequestMethod.POST, dataMap, _ohttprSRresponse);
            _httpProcess.execute();
        } catch (Exception e) {
            VenturaException.Print(e);
        }
    }

    //

    private void GetFundingPerScripOverall(String ISIN) {
        try {
            String _url = "https://mobileapi.ventura1.com/Service.asmx/GetFundingPerScripOverall";
            HashMap<String, String> dataMap = new HashMap<>();
            dataMap.put("userid",UserSession.getLoginDetailsModel().getUserID()); //18J018
            dataMap.put("authenid", UserSession.getClientResponse().getAuthenticationId());
            dataMap.put("isin", ISIN);

            Log.e("GetFundingPerScrip", String.valueOf(dataMap));
            HttpProcessNew _httpProcess = new HttpProcessNew(_url, HttpProcessNew.RequestMethod.POST, dataMap, _ohttprSRresponse22);
            _httpProcess.execute();
        } catch (Exception e) {
            VenturaException.Print(e);
        }
    }

    private void GetPledgeOrderDetails(String clientID, String strBuffer, String OrderType) {
        try {
            String _url = "https://mobileapi.ventura1.com/Service.asmx/PledgeOrderDetails";
            HashMap<String, String> dataMap = new HashMap<>();
            dataMap.put("userid",UserSession.getLoginDetailsModel().getUserID()); //18J018
            dataMap.put("authenid", UserSession.getClientResponse().getAuthenticationId());
            dataMap.put("OrderType", OrderType);
            dataMap.put("ClientID", clientID);
            dataMap.put("PledgedData", strBuffer);
            dataMap.put("DeviceCode", MobileInfo.getDeviceID(getActivity()));
            GlobalClass.log("dataMap", String.valueOf(dataMap));
            HttpProcessNew _httpProcess = new HttpProcessNew(_url, HttpProcessNew.RequestMethod.POST, dataMap, _ohttprPlaceOrder);
            _httpProcess.execute();
        } catch (Exception e) {
            VenturaException.Print(e);
        }
    }

    private void GetActiveSectionList() {
        try {
            String _url = "https://mobileapi.ventura1.com/Service.asmx/GetActiveSectionList ";
            HashMap<String, String> dataMap = new HashMap<>();
            dataMap.put("userid",UserSession.getLoginDetailsModel().getUserID()); //18J018
            dataMap.put("authenid", UserSession.getClientResponse().getAuthenticationId());
            HttpProcessNew _httpProcess = new HttpProcessNew(_url, HttpProcessNew.RequestMethod.POST, dataMap, _ohttActiveSessionBresponse);
            _httpProcess.execute();
        } catch (Exception e) {
            VenturaException.Print(e);
        }
    }


    @Override
    public void onDetach() {
        super.onDetach();

    }

    @Override
    public void onClick(View v) {
        hideKeyboard(v);
        switch (v.getId()) {
            //92282264
            case R.id.RbtsharesinDp:
                try {
                    recyclerView.setVisibility(View.GONE);
                    nodata_txtview.setVisibility(View.GONE);

                    getActivity().runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            new Handler(Looper.getMainLooper()).postDelayed(new Runnable() {
                                @Override
                                public void run() {
                                    callMethodCheckedResetVb();
                                    GlobalClass.showProgressDialog("Requesting...");
                                    SharesInDpMethod(0);
                                    PledgedCountMethod(0, total_PledgedCountDp, total_PledgedCountVb, total_PledgedCountMargin, total_PledgedCountFunding);
                                }
                            }, 100);
                        }
                    });
                } catch (Exception e) {
                    VenturaException.Print(e);
                }

                break;
            case R.id.RbtsharesinVb:
                try {
                    recyclerView.setVisibility(View.GONE);
                    nodata_txtview.setVisibility(View.GONE);

                    getActivity().runOnUiThread(new Runnable() {
                        @Override
                        public void run() {

                            new Handler(Looper.getMainLooper()).postDelayed(new Runnable() {
                                @Override
                                public void run() {
                                    callMethodCheckedResetDp();
                                    GlobalClass.showProgressDialog("Requesting...");
                                    SharesInVbMethod(0);
                                    PledgedCountMethod(0, total_PledgedCountDp, total_PledgedCountVb, total_PledgedCountMargin, total_PledgedCountFunding);
                                }
                            }, 100);

                        }
                    });

                } catch (Exception e) {
                    VenturaException.Print(e);
                }

                break;
            case R.id.RbtnPledgeformargin:
                try {
                    recyclerView.setVisibility(View.GONE);
                    nodata_txtview.setVisibility(View.GONE);

                    getActivity().runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            new Handler(Looper.getMainLooper()).postDelayed(new Runnable() {
                                @Override
                                public void run() {
                                    callMethodCheckedResetSr();
                                    GlobalClass.showProgressDialog("Requesting...");
                                    SharesInMarginMethod(0);
                                    PledgedCountMethod(0, total_PledgedCountDp, total_PledgedCountVb, total_PledgedCountMargin, total_PledgedCountFunding);

                                }
                            }, 100);

                        }
                    });

                } catch (Exception e) {
                    VenturaException.Print(e);
                }

                break;
            case R.id.RbtnpledgefrFunding:
                try {
                    recyclerView.setVisibility(View.GONE);
                    nodata_txtview.setVisibility(View.GONE);

                    getActivity().runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            new Handler(Looper.getMainLooper()).postDelayed(new Runnable() {
                                @Override
                                public void run() {
                                    callMethodCheckedResetSr();
                                    GlobalClass.showProgressDialog("Requesting...");
                                    SharesInFundingMethod(0);
                                    PledgedCountMethod(1, total_PledgedCountDp, total_PledgedCountVb, total_PledgedCountMargin, total_PledgedCountFunding);
                                }
                            }, 100);
                        }
                    });
                } catch (Exception e) {
                    VenturaException.Print(e);
                }

                break;
            case R.id.btnPledgeMargin:
                try {
                    String orderType = "";
                    boolean checkQtyAvail = false;
                    //OrderType - DP or VB or FA or SR
                    stringBuffer.setLength(0);
                    if (btnPledgeMargin.getTag().equals("PLEDGE FOR VB MARGIN")) {
                        orderType = "VB";
                        for (GetDPPledgeFormDetailsModel model : arrayListVbb) {
                            if (model.isSelected()) {
                                if (model.getSetPledgeQty() != null && !model.getSetPledgeQty().equalsIgnoreCase("") && !model.getSetPledgeQty().equalsIgnoreCase("0")) {
                                    stringBuffer.append(model.getISIN() + "~" + model.getSetPledgeQty() + "|");
                                    checkQtyAvail = true;
                                }
                            }
                        }
                    } else if (btnPledgeMargin.getTag().equals("PLEDGE FOR DP MARGIN")) {
                        orderType = "DP";
                        for (GetDPPledgeFormDetailsModel model : arrayListDpp) {
                            if (model.isSelected()) {
                                if (model.getSetPledgeQty() != null && !model.getSetPledgeQty().equalsIgnoreCase("") && !model.getSetPledgeQty().equalsIgnoreCase("0")) {
                                    stringBuffer.append(model.getISIN() + "~" + model.getSetPledgeQty() + "|");
                                    checkQtyAvail = true;
                                }
                            }
                        }
                    } else if (btnPledgeMargin.getTag().equals("M")) {
                        orderType = "SR";
                        for (GetSRPledgeFormDetailsModel model : arrayListSRMargin) {
                            if (model.isSelected()) {
                                if (model.getSetPledgeQty() != null && !model.getSetPledgeQty().equalsIgnoreCase("") && !model.getSetPledgeQty().equalsIgnoreCase("0")) {
                                    stringBuffer.append(model.getISIN() + "~" + model.getSetPledgeQty() + "|");
                                    checkQtyAvail = true;
                                }
                            }
                        }
                    } else if (btnPledgeMargin.getTag().equals("F")) {
                        orderType = "FA";
                        for (GetSRPledgeFormDetailsModel model : arrayListSR) {
                            if (model.isSelected()) {
                                if (model.getSetPledgeQty() != null && !model.getSetPledgeQty().equalsIgnoreCase("") && !model.getSetPledgeQty().equalsIgnoreCase("0")) {
                                    stringBuffer.append(model.getISIN() + "~" + model.getSetPledgeQty() + "|");
                                    checkQtyAvail = true;
                                }
                            }
                        }
                    }

                    if (checkQtyAvail) {
                        stringBuffer = stringBuffer.deleteCharAt(stringBuffer.length() - 1);
                        GetPledgeOrderDetails(ClientId, stringBuffer.toString(), orderType);

                    } else {
                        GlobalClass.showAlertDialog("Please Enter Pledged Qty.");
                    }
                } catch (Exception e) {
                    VenturaException.Print(e);

                }
                break;
        }
    }

    private void hideKeyboard(View v) {
        try {
            GlobalClass.hideKeyboardPledge(v, getActivity(), keyBoadInterface);
        } catch (Exception e) {
            VenturaException.Print(e);
        }
    }

    private void SharesInFundingMethod(int i) {
        RbtsharesinDp.setTypeface(null, Typeface.NORMAL);
        RbtsharesinVb.setTypeface(null, Typeface.NORMAL);
        RbtnPledgeformargin.setTypeface(null, Typeface.NORMAL);
        RbtnpledgefrFunding.setTypeface(null, Typeface.BOLD);

        sharesinDpView.setVisibility(View.GONE);
        sharesinDpView1.setVisibility(View.VISIBLE);
        sharesinVbView.setVisibility(View.GONE);
        sharesinVbView1.setVisibility(View.VISIBLE);
        PledgeformarginView.setVisibility(View.GONE);
        PledgeformarginView1.setVisibility(View.VISIBLE);
        pledgefrFundingView.setVisibility(View.VISIBLE);

        btnPledgeMargin.setText("PLEDGE FOR FUNDING");
        btnPledgeMargin.setTag("F");
        RbtsharesinDp.setChecked(false);
        RbtsharesinVb.setChecked(false);
        RbtnPledgeformargin.setChecked(false);
        RbtnpledgefrFunding.setChecked(true);
        RbtnpledgefrFunding.setTextColor(ContextCompat.getColor(getActivity(), R.color.white));
        RbtnPledgeformargin.setBackground(null);
        RbtsharesinVb.setBackground(null);
        RbtsharesinDp.setBackground(null);

        methodTextViewMarginzero();


        // max funding tag
        linear_maxFund1.setVisibility(View.VISIBLE);
        txt_maxfundLabel.setVisibility(View.VISIBLE);

        //available MTF margin ..

        relativeMTFmargin.setVisibility(View.VISIBLE);
//        total_MTF_utilisedmargin=mView.findViewById(R.id.total_MTF_utilisedmargin);
//        lineartotal_pledged_value2=mView.findViewById(R.id.lineartotal_pledged_value2);
//        total_MTF_utilisedmargin1=mView.findViewById(R.id.total_MTF_utilisedmargin1);


        //utilised margin ..


        getActivity().runOnUiThread(new Runnable() {
            @Override
            public void run() {
                new Handler(Looper.getMainLooper()).postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        GlobalClass.dismissdialog();


                        if (arrayListSR.size() > 0) {

                          /*  if (arrayListSR.get(0).getPledgeAllow().equalsIgnoreCase("0")) {
                                //nodata_txtview.setVisibility(View.VISIBLE);
//                                nodata_txtview.setText("Pledge Services are available from Monday to Friday between 9:00 am to 4:00 pm");
                                Alert("Pledge Services are available from Monday to Friday between 9:00 am to 4:00 pm",GlobalClass.latestContext);

                            } else {
                                mLayoutManager = new LinearLayoutManager(getActivity());
                                recyclerView.setLayoutManager(mLayoutManager);
                                recyclerView.setItemAnimator(new DefaultItemAnimator());
                                pledgeAdapterSr = new PledgeAdapterSr(pledgeMarginTotalCount, keyBoadInterface, arrayListSR, getActivity(), recyclerViewSmooth, "F", modelTotalMargin);
                                recyclerView.setAdapter(pledgeAdapterSr);
                                pledgeAdapterSr.notifyDataSetChanged();
                                recyclerView.setVisibility(View.VISIBLE);
                                nodata_txtview.setVisibility(View.GONE);
                            }*/

                            mLayoutManager = new LinearLayoutManager(getActivity());
                            recyclerView.setLayoutManager(mLayoutManager);
                            recyclerView.setItemAnimator(new DefaultItemAnimator());
                            pledgeAdapterSr = new PledgeAdapterSr(pledgeMarginTotalCount, keyBoadInterface, arrayListSR, getActivity(), recyclerViewSmooth, "F", modelTotalMargin);
                            recyclerView.setAdapter(pledgeAdapterSr);
                            pledgeAdapterSr.notifyDataSetChanged();
                            recyclerView.setVisibility(View.VISIBLE);
                            nodata_txtview.setVisibility(View.GONE);

                        } else {
                            recyclerView.setVisibility(View.GONE);
                            nodata_txtview.setVisibility(View.VISIBLE);
                            if (i == 1) {
                                SharesInMarginMethod(1);
                            }
                        }


                    }
                }, 100);
            }
        });
    }

    private void SharesInMarginMethod(int i) {
        RbtsharesinDp.setTypeface(null, Typeface.NORMAL);
        RbtsharesinVb.setTypeface(null, Typeface.NORMAL);
        RbtnPledgeformargin.setTypeface(null, Typeface.BOLD);
        RbtnpledgefrFunding.setTypeface(null, Typeface.NORMAL);

        sharesinDpView.setVisibility(View.GONE);
        sharesinDpView1.setVisibility(View.VISIBLE);
        sharesinVbView.setVisibility(View.GONE);
        sharesinVbView1.setVisibility(View.VISIBLE);
        PledgeformarginView.setVisibility(View.VISIBLE);
        pledgefrFundingView.setVisibility(View.GONE);
        pledgefrFundingView1.setVisibility(View.VISIBLE);

        btnPledgeMargin.setTag("M");
        btnPledgeMargin.setText("PLEDGE FOR MARGIN");
        RbtsharesinDp.setChecked(false);
        RbtsharesinVb.setChecked(false);
        RbtnpledgefrFunding.setChecked(false);
        RbtnPledgeformargin.setChecked(true);
        RbtnPledgeformargin.setTextColor(ContextCompat.getColor(getActivity(), R.color.white));
        RbtnpledgefrFunding.setBackground(null);
        RbtsharesinDp.setBackground(null);
        RbtsharesinVb.setBackground(null);

        methodTextViewMarginzero();

        linear_maxFund1.setVisibility(View.INVISIBLE);
        txt_maxfundLabel.setVisibility(View.INVISIBLE);

        // new added
        relativeMTFmargin.setVisibility(View.INVISIBLE);


        //call adapter here
        getActivity().runOnUiThread(new Runnable() {
            @Override
            public void run() {
                new Handler(Looper.getMainLooper()).postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        GlobalClass.dismissdialog();

                        if (arrayListSRMargin.size() > 0) {
                           /* if (arrayListSRMargin.get(0).getPledgeAllow().equalsIgnoreCase("0")) {
                               // nodata_txtview.setVisibility(View.VISIBLE);
//                                nodata_txtview.setText("Pledge Services are available from Monday to Friday between 9:00 am to 4:00 pm");
                                Alert("Pledge Services are available from Monday to Friday between 9:00 am to 4:00 pm",GlobalClass.latestContext);

                            } else {
                                mLayoutManager = new LinearLayoutManager(getActivity());
                                recyclerView.setLayoutManager(mLayoutManager);
                                recyclerView.setItemAnimator(new DefaultItemAnimator());
                                pledgeAdapterSr = new PledgeAdapterSr(pledgeMarginTotalCount, keyBoadInterface, arrayListSRMargin, getActivity(), recyclerViewSmooth, "M", modelTotalMargin);
                                recyclerView.setAdapter(pledgeAdapterSr);
                                pledgeAdapterSr.notifyDataSetChanged();
                                recyclerView.setVisibility(View.VISIBLE);
                                nodata_txtview.setVisibility(View.GONE);
                            }*/
                            mLayoutManager = new LinearLayoutManager(getActivity());
                            recyclerView.setLayoutManager(mLayoutManager);
                            recyclerView.setItemAnimator(new DefaultItemAnimator());
                            pledgeAdapterSr = new PledgeAdapterSr(pledgeMarginTotalCount, keyBoadInterface, arrayListSRMargin, getActivity(), recyclerViewSmooth, "M", modelTotalMargin);
                            recyclerView.setAdapter(pledgeAdapterSr);
                            pledgeAdapterSr.notifyDataSetChanged();
                            recyclerView.setVisibility(View.VISIBLE);
                            nodata_txtview.setVisibility(View.GONE);

                        } else {
                            recyclerView.setVisibility(View.GONE);
                            nodata_txtview.setVisibility(View.VISIBLE);
                            if (i == 1) {
                                SharesInDpMethod(0);
                            }

                        }

                    }
                }, 100);
            }
        });
    }

    private void SharesInVbMethod(int i) {
        RbtsharesinDp.setTypeface(null, Typeface.NORMAL);
        RbtsharesinVb.setTypeface(null, Typeface.BOLD);
        RbtnPledgeformargin.setTypeface(null, Typeface.NORMAL);
        RbtnpledgefrFunding.setTypeface(null, Typeface.NORMAL);                //button.setPaintFlags(button.getPaintFlags() | Paint.UNDERLINE_TEXT_FLAG);

        sharesinDpView.setVisibility(View.GONE);
        sharesinDpView1.setVisibility(View.VISIBLE);
        sharesinVbView.setVisibility(View.VISIBLE);
        PledgeformarginView.setVisibility(View.GONE);
        PledgeformarginView1.setVisibility(View.VISIBLE);
        pledgefrFundingView.setVisibility(View.GONE);
        pledgefrFundingView1.setVisibility(View.VISIBLE);
        btnPledgeMargin.setText("PLEDGE FOR MARGIN");
        btnPledgeMargin.setTag("PLEDGE FOR VB MARGIN");
        RbtsharesinVb.setChecked(true);
        RbtsharesinDp.setChecked(false);
        RbtnpledgefrFunding.setChecked(false);
        RbtnPledgeformargin.setChecked(false);
        RbtsharesinVb.setTextColor(ContextCompat.getColor(getActivity(), R.color.white));
        RbtsharesinDp.setBackground(null);
        RbtnpledgefrFunding.setBackground(null);
        RbtnPledgeformargin.setBackground(null);
        methodTextViewMarginzero();
        linear_maxFund1.setVisibility(View.INVISIBLE);
        txt_maxfundLabel.setVisibility(View.INVISIBLE);

        // new added
        relativeMTFmargin.setVisibility(View.INVISIBLE);


        getActivity().runOnUiThread(new Runnable() {
            @Override
            public void run() {
                new Handler(Looper.getMainLooper()).postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        GlobalClass.dismissdialog();
                        if (arrayListVbb.size() > 0) {
                            /*if (arrayListVbb.get(0).getPledgeAllow().equalsIgnoreCase("0")) {
                               // nodata_txtview.setVisibility(View.VISIBLE);
                               // nodata_txtview.setText("Pledge Services are available from Monday to Friday between 9:00 am to 4:00 pm");
                                Alert("Pledge Services are available from Monday to Friday between 9:00 am to 4:00 pm",GlobalClass.latestContext);


                            } else {
                                mLayoutManager = new LinearLayoutManager(getActivity());
                                recyclerView.setLayoutManager(mLayoutManager);
                                recyclerView.setItemAnimator(new DefaultItemAnimator());
                                pledgeAdapter = new PledgeAdapter(recyclerView, keyBoadInterface, pledgeMarginTotalCount, arrayListVbb, getActivity(), recyclerViewSmooth, "VB", modelTotalMargin);
                                recyclerView.setAdapter(pledgeAdapter);
                                pledgeAdapter.notifyDataSetChanged();
                                recyclerView.setVisibility(View.VISIBLE);
                                nodata_txtview.setVisibility(View.GONE);

                            }*/
                            mLayoutManager = new LinearLayoutManager(getActivity());
                            recyclerView.setLayoutManager(mLayoutManager);
                            recyclerView.setItemAnimator(new DefaultItemAnimator());
                            pledgeAdapter = new PledgeAdapter(recyclerView, keyBoadInterface, pledgeMarginTotalCount, arrayListVbb, getActivity(), recyclerViewSmooth, "VB", modelTotalMargin);
                            recyclerView.setAdapter(pledgeAdapter);
                            pledgeAdapter.notifyDataSetChanged();
                            recyclerView.setVisibility(View.VISIBLE);
                            nodata_txtview.setVisibility(View.GONE);

                        } else {
                            recyclerView.setVisibility(View.GONE);
                            nodata_txtview.setVisibility(View.VISIBLE);
                            if (i == 1) {
                                SharesInFundingMethod(1);
                            }

                        }


                    }
                }, 100);
            }
        });
    }

    private void SharesInDpMethod(int i) {
        RbtsharesinDp.setTypeface(null, Typeface.BOLD);
        RbtsharesinVb.setTypeface(null, Typeface.NORMAL);
        RbtnPledgeformargin.setTypeface(null, Typeface.NORMAL);
        RbtnpledgefrFunding.setTypeface(null, Typeface.NORMAL);
        sharesinDpView.setVisibility(View.VISIBLE);

        sharesinVbView.setVisibility(View.GONE);
        sharesinVbView1.setVisibility(View.VISIBLE);
        PledgeformarginView.setVisibility(View.GONE);
        PledgeformarginView1.setVisibility(View.VISIBLE);
        pledgefrFundingView.setVisibility(View.GONE);
        pledgefrFundingView1.setVisibility(View.VISIBLE);

        btnPledgeMargin.setText("PLEDGE FOR MARGIN");
        btnPledgeMargin.setTag("PLEDGE FOR DP MARGIN");
        RbtsharesinDp.setChecked(true);
        RbtsharesinVb.setChecked(false);
        RbtnpledgefrFunding.setChecked(false);
        RbtnPledgeformargin.setChecked(false);
        RbtsharesinDp.setTextColor(ContextCompat.getColor(getActivity(), R.color.white));
        RbtsharesinVb.setBackground(null);
        RbtnPledgeformargin.setBackground(null);
        RbtnpledgefrFunding.setBackground(null);
        methodTextViewMarginzero();
        linear_maxFund1.setVisibility(View.INVISIBLE);
        txt_maxfundLabel.setVisibility(View.INVISIBLE);

        // new added
        relativeMTFmargin.setVisibility(View.INVISIBLE);


        getActivity().runOnUiThread(new Runnable() {
            @Override
            public void run() {
                new Handler(Looper.getMainLooper()).postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        GlobalClass.dismissdialog();

                        if (arrayListDpp.size() > 0) {
/*
                            if (arrayListDpp.get(0).getPledgeAllow().equalsIgnoreCase("0")) {
                               // nodata_txtview.setVisibility(View.VISIBLE);
                               // nodata_txtview.setText("Pledge Services are available from Monday to Friday between 9:00 am to 4:00 pm");
                                Alert("Pledge Services are available from Monday to Friday between 9:00 am to 4:00 pm",GlobalClass.latestContext);

                            } else {
                                mLayoutManager = new LinearLayoutManager(getActivity());
                                recyclerView.setLayoutManager(mLayoutManager);
                                recyclerView.setItemAnimator(new DefaultItemAnimator());
                                pledgeAdapter = new PledgeAdapter(recyclerView, keyBoadInterface, pledgeMarginTotalCount, arrayListDpp, getActivity(), recyclerViewSmooth, "DP", modelTotalMargin);
                                recyclerView.setAdapter(pledgeAdapter);
                                recyclerView.setVisibility(View.VISIBLE);
                                nodata_txtview.setVisibility(View.GONE);
                                pledgeAdapter.notifyDataSetChanged();
                            }*/

                            mLayoutManager = new LinearLayoutManager(getActivity());
                            recyclerView.setLayoutManager(mLayoutManager);
                            recyclerView.setItemAnimator(new DefaultItemAnimator());
                            pledgeAdapter = new PledgeAdapter(recyclerView, keyBoadInterface, pledgeMarginTotalCount, arrayListDpp, getActivity(), recyclerViewSmooth, "DP", modelTotalMargin);
                            recyclerView.setAdapter(pledgeAdapter);
                            recyclerView.setVisibility(View.VISIBLE);
                            nodata_txtview.setVisibility(View.GONE);
                            pledgeAdapter.notifyDataSetChanged();


                        } else {
                            recyclerView.setVisibility(View.GONE);
                            nodata_txtview.setVisibility(View.VISIBLE);
                            if (i == 1) {
                                SharesInVbMethod(1);

                            }
                        }


                    }
                }, 100);

            }
        });
    }

    public void Alert(String msg, Context context) {
        malertDialog = new AlertDialog.Builder(context);
        malertDialog.setTitle("Ventura Wealth");
        malertDialog.setMessage(msg);
        malertDialog.setIcon(R.drawable.ventura_icon);
        malertDialog.setPositiveButton("OK", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int whichButton) {
                //do something
                alertCounter--;
                dialog.dismiss();
                showScreen();
            }
        });
        malertDialog.setCancelable(false);
        alertDialog = malertDialog.create();
        showDialog();
    }

    public static void showDialog() {
        if (alertCounter < 1) {
            alertDialog.show();
            alertCounter++;
        }
    }


    Fragment m_fragment = null;

    public void showScreen() {
        try {

            Intent intent = new Intent(getActivity(), HomeActivity.class);
            startActivity(intent);

        } catch (Exception ex) {
            GlobalClass.onError("showScreen from dialog", ex);
        }
    }

    @Override
    public void PledgeCountVb(int pos) {
        try {
            recyclerView.smoothScrollToPosition(pos);
            int iVb = 0;
            arrayListVbbString.clear();
            for (int i = 0; i < arrayListVbb.size(); i++) {
                if (arrayListVbb.get(i).isSelected()) {
                    if ((arrayListVbb.get(i).getSetPledgeQty() != null) && (!arrayListVbb.get(i).getSetPledgeQty().equalsIgnoreCase("0")) && (!arrayListVbb.get(i).getSetPledgeQty().equalsIgnoreCase(""))) {
                        arrayListVbbString.add("true");
                    }
                } else {
                    arrayListVbbString.add("false");
                }
            }
            if (arrayListVbbString.contains("true")) {
                for (GetDPPledgeFormDetailsModel model : arrayListVbb) {
                    if ((model.getSetPledgeQty() != null) && (!model.getSetPledgeQty().equalsIgnoreCase("0")) && (!model.getSetPledgeQty().equalsIgnoreCase(""))) {
                        if (model.isSelected()) {
                            if (Integer.parseInt(model.getSetPledgeQty()) > 0) {
                                iVb = iVb + Integer.parseInt(model.getSetDpMargin());
                                txt_totalBuyingPowerPershareInVb.setText(Formatter.convertDoubleToValue(iVb).replace(".00", "") + "");
                            } else {
                                iVb = iVb - Integer.parseInt(model.getSetDpMargin());
                                txt_totalBuyingPowerPershareInVb.setText(Formatter.convertDoubleToValue(iVb).replace(".00", "") + "");
                            }
                        }
                    }
                }
            } else {
                txt_totalBuyingPowerPershareInVb.setText("0");
            }
        } catch (Exception e) {
            VenturaException.Print(e);
        }
    }

    @Override
    public void PledgeCountVb1(int i) {
        txt_totalBuyingPowerPershareInVb1.setText(Formatter.convertIntToValue(i) + "");
    }

    @Override
    public void PledgeCountDp(int pos) {
        try {
            recyclerView.smoothScrollToPosition(pos);
            int iDp = 0;
            arrayListDppString.clear();
            for (int i = 0; i < arrayListDpp.size(); i++) {
                if (arrayListDpp.get(i).isSelected()) {
                    if ((arrayListDpp.get(i).getSetPledgeQty() != null) && (!arrayListDpp.get(i).getSetPledgeQty().equalsIgnoreCase("0") && (!arrayListDpp.get(i).getSetPledgeQty().equalsIgnoreCase("")))) {
                        arrayListDppString.add("true");
                    }
                } else {
                    arrayListDppString.add("false");
                }
            }

            if (arrayListDppString.contains("true")) {
                for (GetDPPledgeFormDetailsModel model : arrayListDpp) {
                    if (model.isSelected()) {
                        if ((model.getSetPledgeQty() != null) && (!model.getSetPledgeQty().equalsIgnoreCase("0")) && (!model.getSetPledgeQty().equalsIgnoreCase(""))) {
                            if (Integer.parseInt(model.getSetPledgeQty()) > 0) {
                                iDp = iDp + Integer.parseInt(model.getSetDpMargin());
                                txt_totalBuyingPowerPershareInDp.setText(Formatter.convertDoubleToValue(iDp).replace(".00", "") + "");
                            } else {
                                iDp = iDp - Integer.parseInt(model.getSetDpMargin());
                                txt_totalBuyingPowerPershareInDp.setText(Formatter.convertDoubleToValue(iDp).replace(".00", "") + "");
                            }
                        }
                    }
                }
            } else {
                txt_totalBuyingPowerPershareInDp.setText("0");
            }
        } catch (Exception e) {
            VenturaException.Print(e);
        }
    }

    @Override
    public void PledgeCountDp1(int i, LinearLayout linearLayout) {
    }

    @Override
    public void PledgeCountMargin(int pos) {
        try {
            recyclerView.smoothScrollToPosition(pos);
            double iM = 0;
            arrayListMarginString.clear();
            for (int i = 0; i < arrayListSRMargin.size(); i++) {
                if (arrayListSRMargin.get(i).isSelected()) {
                    if ((arrayListSRMargin.get(i).getSetPledgeQty() != null) && (!arrayListSRMargin.get(i).getSetPledgeQty().equalsIgnoreCase("0") && (!arrayListSRMargin.get(i).getSetPledgeQty().equalsIgnoreCase("")))) {
                        arrayListMarginString.add("true");
                    }
                } else {
                    arrayListMarginString.add("false");
                }
            }
            if (arrayListMarginString.contains("true")) {
                for (GetSRPledgeFormDetailsModel model : arrayListSRMargin) {
                    if (model.isSelected()) {
                        if ((model.getSetPledgeQty() != null) && (!model.getSetPledgeQty().equalsIgnoreCase("0") && (!model.getSetPledgeQty().equalsIgnoreCase("")))) {
                            if (Integer.parseInt(model.getSetPledgeQty()) > 0) {
                                iM = iM + Double.parseDouble(model.getSetDpMargin());
                                txt_totalBuyingPowerPershareInM.setText(Formatter.convertDoubleToValue(iM).replace(".00", "") + "");
                            } else {
                                iM = iM - Double.parseDouble(model.getSetDpMargin());
                                txt_totalBuyingPowerPershareInM.setText(Formatter.convertDoubleToValue(iM).replace(".00", "") + "");
                            }
                        }
                    }
                }
            } else {
                txt_totalBuyingPowerPershareInM.setText("0");
            }
        } catch (Exception e) {
            VenturaException.Print(e);
            Log.e("PledgeCountMargin: ", e.getMessage());
        }
    }

    @Override
    public void PledgeCountMargin1() {
    }

    @Override
    public void PledgeCountFunding(int pos, EditText edtEditText, Context ctx, List<GetSRPledgeFormDetailsModel> arrayList, TextView txtDpmargin, TextView txtTotalShares, String s_Type) {
        try {
            recyclerView.smoothScrollToPosition(pos);
            double totalfundinavail = 0;
            double Utilsedmargin = 0;
            arrayListFundingString.clear();
            for (int i = 0; i < arrayListSR.size(); i++) {
                if (arrayListSR.get(i).isSelected()) {
                    if ((arrayListSR.get(i).getSetPledgeQty() != null) && (!arrayListSR.get(i).getSetPledgeQty().equalsIgnoreCase("0")) && (!arrayListSR.get(i).getSetPledgeQty().equalsIgnoreCase(""))) {
                        arrayListFundingString.add("true");
                    }
                } else {
                    arrayListFundingString.add("false");
                }
            }

            //
            if (arrayListFundingString.contains("true")) {
                for (GetSRPledgeFormDetailsModel model : arrayListSR) {
                    if (model.isSelected()) {
                        if ((model.getSetPledgeQty() != null) && (!model.getSetPledgeQty().equalsIgnoreCase("0")) && (!model.getSetPledgeQty().equalsIgnoreCase(""))) {
                            if (Integer.parseInt(model.getSetPledgeQty()) > 0) {
                                totalfundinavail = totalfundinavail + Double.parseDouble(model.getSetDpMargin()); // fundavail..

                                Log.e("testiF", String.valueOf(totalfundinavail));
                                Log.e("testMaxFunding", MaxFunding);
                                Log.e("testTotalAmountFunded", TotalAmountFunded);

                                Double aDouble1 = new Double(MaxFunding);
                                Double aDouble2 = new Double(TotalAmountFunded);

                                double d1 = aDouble1.doubleValue();
                                double d2 = aDouble2.doubleValue();

//                                String MaxFunding_ = Formatter.formatter.format(MaxFunding);
//                                String TotalAmountFunded_ = Formatter.formatter.format(TotalAmountFunded);

                                double xyz = d1 - d2;

                                Log.e("PledgeCountFunding: ", +d1 + " - " + d2 + "=" + xyz);
                                double j = xyz;

//                                String iFiF= String.valueOf(totalfundinavail);
//                                String jj=TwoDecimalIncludingComma(String.valueOf(j));
//
//                                Log.e("totalfun_00", String.valueOf(iFiF));
//                                Log.e("totalfun_11", String.valueOf(jj));

                                if (totalfundinavail > xyz) {
                                    //Log.e("test: ","Funding availed is exceeded" );
//                                    GlobalClass.Alert( "Funding availed is exceeded", getActivity());
                                    GlobalClass.Alert("You are exceeding the Maximum Funding Limit in your account", getActivity());
                                    // greater funding avail

                                    GlobalClass.hideKeyboardPledge(edtEditText, ctx, keyBoadInterface);

                                    edtEditText.setText("");
                                    edtEditText.setSelection(0);

                                    arrayList.get(pos).setSetPledgeQty("0");
                                    arrayList.get(pos).setSetDpMargin("0");
                                    arrayList.get(pos).setUtilisedMargin("0");
                                    txtDpmargin.setText("0");

                                    if (s_Type.equals("F")) {
                                        txtTotalShares.setText(model.getAvailableFAQuantity());
                                        pledgeMarginTotalCount.PledgeCountFunding(pos, edtEditText, ctx, arrayList, txtDpmargin, txtTotalShares, s_Type);
                                    } else {
                                        txtTotalShares.setText(model.getAvailableQuantity());
                                        pledgeMarginTotalCount.PledgeCountMargin(pos);
                                    }


                                } else {
                                    Utilsedmargin = Utilsedmargin + Double.parseDouble(model.getUtilisedMargin());
                                    txt_totalBuyingPowerPershareInF.setText(Formatter.convertDoubleToValue(Double.valueOf(totalfundinavail)) + "");

                                    // new added. this need to be commented when pledge get started
                                    total_pledged_value.setText(Formatter.convertDoubleToValue(Double.valueOf(Utilsedmargin)) + "");

                                    if (Utilsedmargin != 0 && Utilsedmargin > 999) {
                                        total_pledged_value1.setText((TwoDecimalIncludingComma(String.valueOf(Utilsedmargin))) + "");
                                        lineartotal_pledged_value1.setVisibility(View.VISIBLE);
                                    } else {
                                        lineartotal_pledged_value1.setVisibility(View.GONE);
                                    }

                                    try {
                                        Log.e("here", "minus");

                                        //avail margin minus utlised margin her
                                        double availableMTFmargintemp = Double.parseDouble(availableMTFmargin) - Utilsedmargin;

                                        Log.e("availableMTFmargintemp ", String.valueOf(availableMTFmargintemp));
                                        AvailableMTFmargin(String.valueOf(availableMTFmargintemp));
                                    } catch (Exception e) {

                                        Log.e("kkkkkk", e.getMessage());
                                    }
                                }


                            } else {
                                totalfundinavail = totalfundinavail - Double.parseDouble(model.getSetDpMargin());

//                                AvailableMTFmargin(Utilsedmargin,1);


//                                int j=Integer.parseInt(MaxFunding)-Integer.parseInt(TotalAmountFunded);

//                                if (iF>j){
//
//                                    GlobalClass.Alert("Funding availed is exceeded.", getActivity());
//
//                                    // greater funding avail
//                                }else {

                                Log.e("11111", model.getUtilisedMargin());
                                Log.e("111112222", String.valueOf(Utilsedmargin));

                                Utilsedmargin = Utilsedmargin - Double.parseDouble(model.getUtilisedMargin());
                                Log.e("Utilsedmargin", String.valueOf(Utilsedmargin));
                                txt_totalBuyingPowerPershareInF.setText(Formatter.convertDoubleToValue(Double.valueOf(totalfundinavail)) + "");
                                //new added
                                total_pledged_value.setText(Formatter.convertDoubleToValue(Double.valueOf(Utilsedmargin)) + "");
                                if (Utilsedmargin != 0 && Utilsedmargin > 999) {
                                    total_pledged_value1.setText((TwoDecimalIncludingComma(String.valueOf(Utilsedmargin))) + "");
                                    lineartotal_pledged_value1.setVisibility(View.VISIBLE);
                                } else {
                                    lineartotal_pledged_value1.setVisibility(View.GONE);
                                }
//                                }
                            }
                        }
                    }
                }
            } else {
                txt_totalBuyingPowerPershareInF.setText("0");
                total_pledged_value.setText("0");
                total_pledged_value1.setText("0");
            }
        } catch (Exception e) {
            VenturaException.Print(e);

            Log.e("yyyyy", e.getMessage());
            Log.e("yyyyy", String.valueOf(e.getCause()));
            Log.e("yyyyy", String.valueOf(e.getLocalizedMessage()));
        }
    }

    @Override
    public void PledgeCountFunding1() {
    }

    @Override
    public void ScrollPosition(int pos) {
        recyclerView.scrollToPosition(pos);
    }


    @Override
    public void CallDlg(String msg, Context context) {
        getActivity().runOnUiThread(new Runnable() {
            @Override
            public void run() {
                GlobalClass.Alert(msg, context);
            }
        });
    }

    @Override
    public void ApiCall(String ISIN, Context context) {

        try {
            Log.e("ApiCallISIN: ", ISIN);
            GetFundingPerScripOverall(ISIN);
        } catch (Exception e) {

            Log.e("ApiCall: ", e.getMessage());
        }
    }

    @Override
    public void HideButton() {
        try {
            getActivity().runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    new Handler(Looper.getMainLooper()).postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            linearLayoutbottm.setVisibility(View.GONE);
                        }
                    }, 100);
                }
            });

        } catch (Exception e) {
            VenturaException.Print(e);
        }
    }

    @Override
    public void ShowButton() {
        try {
            getActivity().runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    new Handler(Looper.getMainLooper()).postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            linearLayoutbottm.setVisibility(View.VISIBLE);
                        }
                    }, 300);
                }
            });

        } catch (Exception e) {
            VenturaException.Print(e);
        }
    }

    class GetTaskFirst extends AsyncTask<Object, Void, String> {
        private ProgressDialog mDialog;

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            try {
                mDialog = Dialogs.getProgressDialog(getActivity());
                mDialog.setMessage("Please wait...");
                mDialog.show();
            } catch (Exception e) {
                VenturaException.Print(e);
            }
        }

        @Override
        protected String doInBackground(Object... params) {
            try {
                if (UserSession.getClientResponse().isNeedAccordLogin()) {
                    ClientLoginResponse clientLoginResponse = VenturaServerConnect.callAccordLogin();
                    return clientLoginResponse.charResMsg.getValue();
                }
            } catch (Exception ie) {
                VenturaException.Print(ie);
            }
            return "";
        }

        @Override
        protected void onPostExecute(String result) {
            super.onPostExecute(result);
            try {
                mDialog.dismiss();
                if (result.equalsIgnoreCase("")) {
                    GetActiveSectionList();
                } else {
                    if (result.toLowerCase().contains(Constants.WEALTH_ERR)) {
                        GlobalClass.homeActivity.logoutAlert("Logout", Constants.LOGOUT_FOR_WEALTH, false);
                    } else {
                        GlobalClass.showAlertDialog(result);
                    }
                }
            } catch (Exception e) {
                VenturaException.Print(e);
            }
        }
    }

    private void displayInWebView(String Url) {
        Intent intent = new Intent(getActivity(), WebViewForPledgeFragment.class);
        intent.putExtra("Url", Url);
        startActivity(intent);
    }

    void methodTextViewMarginzero() {
        txt_totalBuyingPowerPershareInDp.setText("0");
        txt_totalBuyingPowerPershareInVb.setText("0");
        txt_totalBuyingPowerPershareInF.setText("0");
        txt_totalBuyingPowerPershareInM.setText("0");
    }

    public class XmlPullParserHandler {
        String text;
        StringBuilder builder = new StringBuilder();

        public String parse(InputStream is) {
            try {
                XmlPullParserFactory factory = XmlPullParserFactory.newInstance();
                factory.setNamespaceAware(true);
                XmlPullParser parser = factory.newPullParser();
                parser.setInput(is, null);
                int eventType = parser.getEventType();
                while (eventType != XmlPullParser.END_DOCUMENT) {
                    String tagname = parser.getName();
                    switch (eventType) {
                        case XmlPullParser.TEXT:
                            text = parser.getText();
                            break;
                        case XmlPullParser.END_TAG:
//                            if (tagname.equalsIgnoreCase("FundingAmount")) {

                            //  Log.e("tagnametagname33", tagname);
                            if (tagname.equalsIgnoreCase("MaxTotalFunding")) {
                                builder.append(text);
                            }
                            break;
                        default:
                            break;
                    }
                    eventType = parser.next();
                }
            } catch (IOException e) {
                e.printStackTrace();
            } catch (XmlPullParserException e) {
                e.printStackTrace();
            }
            return builder.toString();
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        if (shouldRefreshOnResume) {
            shouldRefreshOnResume = false;
            PledgeFragment f2 = new PledgeFragment();
            FragmentTransaction transaction = getFragmentManager().beginTransaction();
            transaction.replace(R.id.container_body, f2);
            transaction.commit();
        }
    }

    @Override
    public void onStop() {
        super.onStop();
    }

    private void init_() {
        getActivity().getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_RESIZE);
        keyBoadInterface = (KeyBoadInterface) this;
        pledgeMarginTotalCount = (PledgeMarginTotalCount) this;
        stringBuffer = new StringBuffer();
        arrayListDpp = new ArrayList<>();
        arrayListVbb = new ArrayList<>();
        arrayListDppCheck = new ArrayList<>();
        arrayListSR = new ArrayList<>();
        arrayListSRMargin = new ArrayList<>();
        arrayListSRCheck = new ArrayList<>();
        recyclerViewSmooth = (RecyclerViewSmooth) this;
        spinnerValues = new ArrayList<>();
        modelTotalMargin = (ModelTotalMargin) this;

        arrayListDppString = new ArrayList<>();
        arrayListVbbString = new ArrayList<>();
        arrayListMarginString = new ArrayList<>();
        arrayListFundingString = new ArrayList<>();

        //  main_Layout = mView.findViewById(R.id.main_Layout);
        imgNote = mView.findViewById(R.id.imgNote);
        total_pledged_value = mView.findViewById(R.id.total_pledged_value);
        total_pledged_value1 = mView.findViewById(R.id.total_pledged_value1);
        lineartotal_pledged_value1 = mView.findViewById(R.id.lineartotal_pledged_value1);
        txttotalpledgedValue = mView.findViewById(R.id.txttotalpledgedValue);
        spinner = mView.findViewById(R.id.spinner);
        txtSpinnerData = mView.findViewById(R.id.txtSpinnerData);
        btnPledgeMargin = mView.findViewById(R.id.btnPledgeMargin);
        // TimerTask();
        RbtsharesinVb = mView.findViewById(R.id.RbtsharesinVb);
        RbtsharesinDp = mView.findViewById(R.id.RbtsharesinDp);
        RbtnpledgefrFunding = mView.findViewById(R.id.RbtnpledgefrFunding);
        RbtnPledgeformargin = mView.findViewById(R.id.RbtnPledgeformargin);

        sharesinVbView = mView.findViewById(R.id.sharesinVbView);
        sharesinVbView1 = mView.findViewById(R.id.sharesinVbView1);
        sharesinDpView = mView.findViewById(R.id.sharesinDpView);
        sharesinDpView1 = mView.findViewById(R.id.sharesinDpView1);
        pledgefrFundingView = mView.findViewById(R.id.pledgefrFundingView);
        pledgefrFundingView1 = mView.findViewById(R.id.pledgefrFundingView1);
        PledgeformarginView = mView.findViewById(R.id.PledgeformarginView);
        PledgeformarginView1 = mView.findViewById(R.id.PledgeformarginView1);


        // new added bu shiva
        // available mtf margin.

        relativeMTFmargin = mView.findViewById(R.id.relativeMTFmargin);
        total_MTF_utilisedmargin1 = mView.findViewById(R.id.total_MTF_utilisedmargin1);
        total_MTF_utilisedmargin2 = mView.findViewById(R.id.total_MTF_utilisedmargin2);
        lineartotal_pledged_value2 = mView.findViewById(R.id.lineartotal_pledged_value2);


        RbtsharesinDp.setOnClickListener(this);
        RbtsharesinVb.setOnClickListener(this);
        RbtnpledgefrFunding.setOnClickListener(this);
        RbtnPledgeformargin.setOnClickListener(this);
        btnPledgeMargin.setOnClickListener(this);
        indexationRd = mView.findViewById(R.id.indexationRd);
        indexationRd2 = mView.findViewById(R.id.indexationRd2);
        clNameProfile = mView.findViewById(R.id.clNameProfile);

        String name = UserSession.getLoginDetailsModel().getClientName();
        if (name.contains("Mrs")) {
            clNameProfile.setText(name.replace("Mrs", ""));
        } else if (name.contains("Mr")) {
            clNameProfile.setText(name.replace("Mr", ""));
        } else {
            clNameProfile.setText(name);
        }
        clCodeProfile = mView.findViewById(R.id.clCodeProfile);
        nodata_txtview = mView.findViewById(R.id.nodata_txtview);
        txtsharedinStock = mView.findViewById(R.id.txtsharedinStock);
        txt_totalBuyingPowerPershareInDp = mView.findViewById(R.id.txt_totalBuyingPowerPershareInDp);
        txt_totalBuyingPowerPershareInDp1 = mView.findViewById(R.id.txt_totalBuyingPowerPershareInDp1);
        txt_totalBuyingPowerPershareInVb = mView.findViewById(R.id.txt_totalBuyingPowerPershareInVb);
        txt_totalBuyingPowerPershareInVb1 = mView.findViewById(R.id.txt_totalBuyingPowerPershareInVb1);
        txt_totalBuyingPowerPershareInF = mView.findViewById(R.id.txt_totalBuyingPowerPershareInF);
        txt_totalBuyingPowerPershareInF1 = mView.findViewById(R.id.txt_totalBuyingPowerPershareInF1);
        txt_totalBuyingPowerPershareInM = mView.findViewById(R.id.txt_totalBuyingPowerPershareInM);
        txt_totalBuyingPowerPershareInM1 = mView.findViewById(R.id.txt_totalBuyingPowerPershareInM1);
        txt_maxfundLabel = mView.findViewById(R.id.txt_maxfundLabel);
        linear_maxFund1 = mView.findViewById(R.id.linear_maxFund1);
        linearLayoutbottm = mView.findViewById(R.id.linearLayoutbottm);
        txt_maxfund0 = mView.findViewById(R.id.txt_maxfund0);
        txt_maxfund1 = mView.findViewById(R.id.txt_maxfund1);
        txt_maxfund2 = mView.findViewById(R.id.txt_maxfund2);
        recyclerView = mView.findViewById(R.id.recycler_view);
        clCodeProfile.setText(UserSession.getLoginDetailsModel().getUserID());

        try {
            setKeyboardVisibilityListener();
        } catch (Exception e) {
            VenturaException.Print(e);
        }

        txtsharedinStock.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                SharesInFundingMethod(0);
            }
        });
        imgNote.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AlertBox.showCustomDialog(mView, getActivity());
            }
        });
    }

    @SuppressLint("LongLogTag")
    private void PledgedCountMethod(int jj, int total_PledgedCountDp,
                                    int total_PledgedCountVb,
                                    int total_PledgedCountMargin, double total_PledgedCountFunding) {
        // jj==0  means margin
        // jj==1  means Funding

        if (jj == 0) {

            txttotalpledgedValue.setText("Total Buying Power");
            int i = total_PledgedCountDp + total_PledgedCountVb + total_PledgedCountMargin;
            total_pledged_value.setText(Formatter.convertDoubleToValue(i).replace(".00", "") + "");
            if (i != 0 && (i) > 999) {
                total_pledged_value1.setText((DecimalLessIncludingComma(String.valueOf(i))) + "");
                lineartotal_pledged_value1.setVisibility(View.VISIBLE);
            } else {
                lineartotal_pledged_value1.setVisibility(View.GONE);
            }

        } else {

            txttotalpledgedValue.setText("Utilised Margin");

            total_pledged_value.setText("0");
            total_pledged_value1.setText("0");
            lineartotal_pledged_value1.setVisibility(View.GONE);

            // this need to uncomment when pledge get started .  shiva tumma    commented

//             total_pledged_value --- Total Buying Power
            /*txttotalpledgedValue.setText("Funding Pledge");
            double i = total_PledgedCountFunding;
            total_pledged_value.setText(Formatter.convertDoubleToValue(i).replace(".00", "") + "");
            if (total_PledgedCountFunding != 0 && total_PledgedCountFunding > 999) {
                total_pledged_value1.setText((TwoDecimalIncludingComma(String.valueOf(i))) + "");
                lineartotal_pledged_value1.setVisibility(View.VISIBLE);
            } else {
                lineartotal_pledged_value1.setVisibility(View.GONE);
            }*/
        }
    }

    void callMethodCheckedResetDp() {
        for (int i = 0; i < arrayListDpp.size(); i++) {
            arrayListDpp.get(i).setSelected(false);
        }
    }

    void callMethodCheckedResetVb() {
        for (int i = 0; i < arrayListVbb.size(); i++) {
            arrayListVbb.get(i).setSelected(false);
        }
    }

    void callMethodCheckedResetSr() {
        for (int i = 0; i < arrayListSR.size(); i++) {
            arrayListSR.get(i).setSelected(false);
        }
        for (int i = 0; i < arrayListSRMargin.size(); i++) {
            arrayListSRMargin.get(i).setSelected(false);
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
    }

    private void setKeyboardVisibilityListener() {
        final View parentView = ((ViewGroup) getActivity().findViewById(android.R.id.content)).getChildAt(0);
        parentView.getViewTreeObserver().addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {

            private boolean alreadyOpen;
            private final int defaultKeyboardHeightDP = 100;
            private final int EstimatedKeyboardDP = defaultKeyboardHeightDP + (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP ? 48 : 0);
            private final Rect rect = new Rect();

            @Override
            public void onGlobalLayout() {
                int estimatedKeyboardHeight = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, EstimatedKeyboardDP, parentView.getResources().getDisplayMetrics());
                parentView.getWindowVisibleDisplayFrame(rect);
                int heightDiff = parentView.getRootView().getHeight() - (rect.bottom - rect.top);
                boolean isShown = heightDiff >= estimatedKeyboardHeight;

                if (isShown == alreadyOpen) {
                    return;
                }

                alreadyOpen = isShown;

                if (alreadyOpen == false) {
                    new Handler(Looper.getMainLooper()).postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            linearLayoutbottm.setVisibility(View.VISIBLE);
                        }
                    }, 200);

                } else {
                    linearLayoutbottm.setVisibility(View.GONE);
                }
            }
        });
    }
}