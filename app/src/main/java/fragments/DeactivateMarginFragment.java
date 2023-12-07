package fragments;


import android.content.Context;
import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ScrollView;
import android.widget.TextView;

import com.ventura.venturawealth.R;
import com.ventura.venturawealth.activities.homescreen.HomeActivity;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.InputSource;

import java.io.StringReader;
import java.util.HashMap;
import java.util.Vector;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import connection.HttpProcess;
import enums.eLogType;
import utils.GlobalClass;
import utils.UserSession;
import utils.VenturaException;

/**
 * A simple {@link Fragment} subclass.
 */
public class DeactivateMarginFragment extends Fragment {

    private HomeActivity _homeActivity;

    public static DeactivateMarginFragment newInstance(){
        return new DeactivateMarginFragment();
    }
    public DeactivateMarginFragment() {
        // Required empty public constructor
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        _homeActivity = (HomeActivity) context;
    }
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        _homeActivity.findViewById(R.id.homeRDgroup).setVisibility(View.GONE);
        return inflater.inflate(R.layout.fragment_deactivate_margin, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        ScrollView scrollView = view.findViewById(R.id.scrollView);
        TextView cancelBtn = view.findViewById(R.id.cancel);
        TextView deactivateBtn = view.findViewById(R.id.deactivate);
        TextView deactivateTxt = view.findViewById(R.id.deactivetext);

        String txt = "Click on deactivate option to deactivate your Client Code "+UserSession.getLoginDetailsModel().getUserID()+" for Margin Trading Facility.";
        deactivateTxt.setText(txt);
        cancelBtn.setOnClickListener(view13 -> {
            _homeActivity.getSupportFragmentManager().popBackStackImmediate();
        });
        deactivateBtn.setOnClickListener(view13 -> {
            sendDeActivateMarginReq();
        });
        GlobalClass.addAndroidLogForFragment(eLogType.SCREENLOG.name, getClass().getSimpleName(),UserSession.getLoginDetailsModel().getUserID());

    }

    private void sendDeActivateMarginReq() {
        try {
            String _url = "https://mobileapi.ventura1.com/Service.asmx/MargintradingDeactivation";
            HashMap<String,String> dataMap = new HashMap<>();
            dataMap.put("userid", UserSession.getLoginDetailsModel().getUserID());
            dataMap.put("authenid",UserSession.getClientResponse().getAuthenticationId());
            HttpProcess _httpProcess = new HttpProcess(_url, HttpProcess.RequestMethod.POST,dataMap,_ohttpr);
            _httpProcess.execute();
        }catch (Exception e){
            VenturaException.Print(e);
        }
    }

    private HttpProcess.OnHttpResponse _ohttpr = new HttpProcess.OnHttpResponse() {
        @Override
        public void onHttpResponse(String response) {

            boolean isSuccess = xmlData(response);
            if(isSuccess){
                UserSession.getClientResponse().setDeactivateMargin();
            }
            _homeActivity.runOnUiThread(() -> {
                _homeActivity.RefreshNavmenus();
                _homeActivity.getSupportFragmentManager().popBackStackImmediate();
            });
        }
    };

    public boolean xmlData(String str) {

        boolean isSuccess = false;
        Vector rowData = null;

        try {
            if (!str.equalsIgnoreCase("")) {
                if(str.contains("&#x0;")){
                    str = str.replaceAll("&#x0;", "");
                }
                DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
                DocumentBuilder dBuilder = dbFactory.newDocumentBuilder();
                InputSource s = new InputSource(new StringReader(str));
                Document doc = dBuilder.parse(s);
                doc.getDocumentElement().normalize();

                rowData = new Vector();
                NodeList nList = doc.getElementsByTagName("NewDataSet");

                //GlobalClass.log("----------------------------");
                //GlobalClass.log(nList.getLength());
                for (int temp = 0; temp < nList.getLength(); temp++) {

                    Node nNode = nList.item(temp);

                    if (nNode.getNodeType() == Node.ELEMENT_NODE && nNode.hasChildNodes()) {

                        Element eElement = (Element) nNode;
                        //GlobalClass.log(eElement.getFirstChild().getNodeName());
                        NodeList nodeTable = eElement.getChildNodes();
                        for (int i = 0; i < nodeTable.getLength(); i++) {
                            Vector data = new Vector();
                            if(!nodeTable.item(i).getNodeName().equalsIgnoreCase("#text")){
                                NodeList childNode = nodeTable.item(i).getChildNodes();
                                for (int j = 0; j < childNode.getLength(); j++) {
                                    String nodeName = childNode.item(j).getNodeName();
                                    if(!nodeName.equalsIgnoreCase("#text")){
                                        GlobalClass.log(nodeName + " ::  " + childNode.item(j).getTextContent());
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
                        if(!folioRow.isEmpty()){
                            for(int j=0;j<folioRow.size();j++){
                                if(folioRow.get(j).toString().equalsIgnoreCase("Message")){
                                    msg = folioRow.get(j+1).toString();
                                    break;
                                }
                                j++;
                            }
                        }
                    }
                    if(!msg.equalsIgnoreCase("")){
                        isSuccess = true;
                        if(msg.toLowerCase().contains("fail")){
                            isSuccess = false;
                        }
                        GlobalClass.showAlertDialog(msg);
                    }
                }
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
        return isSuccess;
    }

}
