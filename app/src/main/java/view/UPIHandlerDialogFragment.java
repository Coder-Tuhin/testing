package view;

import android.app.Dialog;
import android.content.Context;
import android.os.AsyncTask;
import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import com.ventura.venturawealth.R;

import org.json.JSONArray;
import org.json.JSONObject;

import enums.eMFJsonTag;
import enums.eMessageCodeWealth;
import utils.GlobalClass;
import utils.UserSession;
import wealth.VenturaServerConnect;

public class UPIHandlerDialogFragment extends DialogFragment {
    private MyAdapter adapter;
    private JSONArray jar;
    private ListView listView;
    public static UPIHandlerDialogFragment newInstance(){
        UPIHandlerDialogFragment fragment = new UPIHandlerDialogFragment();
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setStyle(DialogFragment.STYLE_NORMAL, R.style.FullScreenDialogStyle);
    }

    @Override
    public void onStart() {
        super.onStart();
        Dialog dialog = getDialog();
        if (dialog != null) {
            int width = ViewGroup.LayoutParams.MATCH_PARENT;
            int height = ViewGroup.LayoutParams.MATCH_PARENT;
            dialog.getWindow().setLayout(width, height);
        }
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.upiaddhelp_scr,container,false);
        adapter = new MyAdapter(getActivity());
        listView = view.findViewById(R.id.ListView);

        LinearLayout close = view.findViewById(R.id.close);
        close.setOnClickListener(view1 -> dismiss());

        new AddUPIReq(eMessageCodeWealth.IPO_GET_IPO_HANDLER_LIST.value).execute();
        return view;
    }

    class AddUPIReq extends AsyncTask<String, Void, String> {
        int msgCode;

        AddUPIReq(int mCode){
            this.msgCode = mCode;
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            GlobalClass.showProgressDialog("Requesting...");
        }

        @Override
        protected String doInBackground(String... strings) {
            if(msgCode == eMessageCodeWealth.IPO_GET_IPO_HANDLER_LIST.value) {
                try{
                    JSONObject jdata = new JSONObject();
                    jdata.put(eMFJsonTag.CLINETCODE.name, UserSession.getLoginDetailsModel().getUserID());
                    JSONObject jsonData = VenturaServerConnect.sendReqJSONDataMFReport(eMessageCodeWealth.IPO_GET_IPO_HANDLER_LIST.value, jdata);
                    if (jsonData != null) {
                        return jsonData.toString();
                    }
                }catch (Exception e){
                    e.printStackTrace();
                    displayError(e.getMessage());
                }

            }
            return null;
        }

        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);
            GlobalClass.dismissdialog();

            if(s != null){
                try {
                    JSONObject jsonData = new JSONObject(s);
                    GlobalClass.log("MrGoutamD (IPO_GET_IPO_HANDLER_LIST): "+jsonData.toString());
                    processHandlerData(jsonData);
                }catch (Exception ex){
                    ex.printStackTrace();
                    displayError(ex.getMessage());
                }
            }
        }
    }

    private void processHandlerData(JSONObject jsonData) {
        try {
            jar = jsonData.getJSONArray("data");
            listView.setAdapter(adapter);
            adapter.notifyDataSetChanged();
        }catch (Exception e){
            e.printStackTrace();
            displayError(e.getMessage());
        }
    }

    private void displayError(String err){
        GlobalClass.showAlertDialog(err);
    }

    class MyAdapter extends BaseAdapter {
        private Context context;
        public MyAdapter(Context context) {
            this.context = context;
        }
        @Override
        public int getCount() {
            return jar.length();
        }
        @Override
        public Object getItem(int position) {
            return position;
        }
        @Override
        public long getItemId(int position) {
            return position;
        }
        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            if (convertView == null){
                convertView = LayoutInflater.from(context).inflate(R.layout.upi_handler_list_item, parent, false);
            }
            try {
                JSONObject job = jar.getJSONObject(position);
                TextView sl_no_tv = convertView.findViewById(R.id.sl_no_tv);
                TextView upi_app_name = convertView.findViewById(R.id.upi_app_name);
                TextView handle_tv = convertView.findViewById(R.id.handle_tv);

                sl_no_tv.setText(job.getString("Srno"));
                upi_app_name.setText(job.getString("AppName"));
                handle_tv.setText(job.getString("HandleName"));
            }catch (Exception e){
                e.printStackTrace();
            }
            return convertView;
        }
    }
}
