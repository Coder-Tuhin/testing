package adapters;

//25NOVSHIVA

import static utils.Formatter.DecimalLessIncludingComma;
import static utils.Formatter.TwoDecimalIncludingComma;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Color;
import android.os.Build;
import android.text.Editable;
import android.text.Html;
import android.text.InputType;
import android.text.TextWatcher;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.RequiresApi;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.ventura.venturawealth.R;
import com.ventura.venturawealth.VenturaApplication;

import java.security.spec.ECField;
import java.text.NumberFormat;
import java.util.List;
import java.util.Locale;

import interfaces.KeyBoadInterface;
import interfaces.ModelTotalMargin;
import interfaces.PledgeMarginTotalCount;
import interfaces.RecyclerViewSmooth;
import models.GetDPPledgeFormDetailsModel;
import models.GetSRPledgeFormDetailsModel;
import utils.Encriptions.ReusableLogics;
import utils.GlobalClass;
import utils.UserSession;
import utils.VenturaException;
import view.AlertBox;

public class PledgeAdapterSr extends RecyclerView.Adapter<PledgeAdapterSr.MyViewHolder> {

    private List<GetSRPledgeFormDetailsModel> arrayList;
    Context ctx;
    RecyclerViewSmooth recyclerViewSmooth;
    String s_Type = "";
    double z = 0;
    double z1 = 0;
    double z2 = 0;
    int currentpos = -1;
    ModelTotalMargin modelTotalMargin;
    int Temp = 0;
    int Temp1 = 0;
    View itemView;
    PledgeMarginTotalCount pledgeMarginTotalCount;

    KeyBoadInterface keyBoadInterface;
    GetSRPledgeFormDetailsModel model=null;
    public class MyViewHolder extends RecyclerView.ViewHolder {
        public TextView txtSchemeName, txtBuyingPower, txtDpmargin, txtEditText, txtDPVBSR;
        public TextView txtTotalShares;
        public TextView txtTodaysPledge;
        public TextView txt_BuyingFunding;
        EditText edtEditText;
        CheckBox checkBox;
        LinearLayout linearLayout1, linearLayout2, main_Layout;

        public MyViewHolder(View view) {
            super(view);
            main_Layout = view.findViewById(R.id.main_Layout);
            checkBox = (CheckBox) view.findViewById(R.id.checkBox);
            txt_BuyingFunding = (TextView) view.findViewById(R.id.txt_BuyingFunding);
            txtSchemeName = (TextView) view.findViewById(R.id.txtSchemeName);
            txtBuyingPower = (TextView) view.findViewById(R.id.txtBuyingPower);
            txtDpmargin = (TextView) view.findViewById(R.id.txtDpmargin);
            edtEditText = view.findViewById(R.id.edtEditText);
            linearLayout1 = view.findViewById(R.id.linear1);
            linearLayout2 = view.findViewById(R.id.linear2);
            txtEditText = view.findViewById(R.id.txtEditText);
            txtDPVBSR = view.findViewById(R.id.txtDPVBSR);
            txtTotalShares = view.findViewById(R.id.txtTotalShares);
            txtTodaysPledge = view.findViewById(R.id.txtTodaysPledge);

        }
    }

    public PledgeAdapterSr(PledgeMarginTotalCount pledgeMarginTotalCount, KeyBoadInterface keyBoadInterface, List<GetSRPledgeFormDetailsModel> arrayList, Context context, RecyclerViewSmooth recyclerViewSmooth, String s, ModelTotalMargin modelTotalMargin) {
        this.arrayList = arrayList;
        this.ctx = context;
        this.recyclerViewSmooth = recyclerViewSmooth;
        this.s_Type = s;
        this.modelTotalMargin = modelTotalMargin;
        this.pledgeMarginTotalCount = pledgeMarginTotalCount;
        this.keyBoadInterface = keyBoadInterface;

    }

    @Override
    public PledgeAdapterSr.MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.pledgesr_layout, parent, false);
        return new PledgeAdapterSr.MyViewHolder(itemView);
    }

    @RequiresApi(api = Build.VERSION_CODES.N)
    @Override
    public void onBindViewHolder(PledgeAdapterSr.MyViewHolder holder, @SuppressLint("RecyclerView") int position) {
        GetSRPledgeFormDetailsModel model= arrayList.get(position);

        try {
            holder.txtSchemeName.setText(model.getCompName());
            holder.checkBox.setTag(position);
            holder.edtEditText.setTag(position);
            itemView.setTag(holder);

            String FundingPerShare = "";


            if (s_Type.equals("F")) {
                FundingPerShare = "<font >" + "\u20B9" + "</font>" + "<font >" + "<b>" + TwoDecimalIncludingComma(model.getFundingPerShare()) + "<b></font>";
                holder.txtTotalShares.setText(model.getAvailableFAQuantity());
                holder.txtTodaysPledge.setText(model.getConfirmedFAQuantity());
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                    holder.txtBuyingPower.setText(Html.fromHtml(FundingPerShare, Html.FROM_HTML_MODE_LEGACY));
                }

                String cnfrmQtyStr = "<font >" + "<b>" + model.getConfirmedFAQuantity() + "<b></font>";
                holder.txtEditText.setText(Html.fromHtml(cnfrmQtyStr, Html.FROM_HTML_MODE_LEGACY));

                Log.e("zzzzz", ReusableLogics.getKeyPledgeActiveSession(ctx));
                ///added

                if (ReusableLogics.getKeyPledgeActiveSession(ctx).contains("FA") ){
                    holder.checkBox.setEnabled(true);
                    holder.main_Layout.setEnabled(true);
                }else {
                    holder.checkBox.setEnabled(false);
                    holder.main_Layout.setEnabled(false);
                }

            } else {

                FundingPerShare = "<font >" + "\u20B9" + "</font>" + "<font >" + "<b>" + DecimalLessIncludingComma(model.getBuyingPower()) + "<b></font>";
                holder.txtTotalShares.setText(model.getAvailableQuantity());
                holder.txtTodaysPledge.setText(model.getConfirmedSRQuantity());
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                    holder.txtBuyingPower.setText(Html.fromHtml(FundingPerShare, Html.FROM_HTML_MODE_LEGACY));
                }

                String cnfrmQtyStr = "<font >" + "<b>" + model.getConfirmedSRQuantity() + "<b></font>";
                holder.txtEditText.setText(Html.fromHtml(cnfrmQtyStr, Html.FROM_HTML_MODE_LEGACY));

                if (ReusableLogics.getKeyPledgeActiveSession(ctx).contains("SR")){
                    holder.checkBox.setEnabled(true);
                    holder.main_Layout.setEnabled(true);
                }else {
                    holder.checkBox.setEnabled(false);
                    holder.main_Layout.setEnabled(false);
                }
            }

            holder.txtDpmargin.setText("0");
            holder.edtEditText.setRawInputType(InputType.TYPE_CLASS_NUMBER);
            if (model.getConfirmedSRQuantity().equalsIgnoreCase("0")) {
                holder.txtEditText.setTextColor(Color.WHITE);
            } else {
                holder.txtEditText.setTextColor(Color.GREEN);
            }

        } catch (Exception e) {
            VenturaException.Print(e);
        }

        holder.checkBox.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                currentpos = position;
                try {
                    if (holder.checkBox.isChecked()) {
                        Checked(holder, position,arrayList);
                    } else {
                        Unchecked(holder, position, model);
                    }
                } catch (Exception e) {
                    VenturaException.Print(e);
                }

            }
        });

        holder.main_Layout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                currentpos = position;

                try {

                    if (holder.checkBox.isChecked()) {
                        holder.checkBox.setChecked(false);
                    } else {
                        holder.checkBox.setChecked(true);
                    }

                    if (holder.checkBox.isChecked()) {
                        Checked(holder, position,arrayList);

                    } else {
                        Unchecked(holder, position, model);
                    }
                } catch (Exception e) {
                    VenturaException.Print(e);
                }
            }
        });

        holder.edtEditText.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                boolean handled = false;
                GlobalClass.hideKeyboardPledge(holder.edtEditText, ctx, keyBoadInterface);
                handled = true;
                return handled;
            }
        });

        holder.edtEditText.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                keyBoadInterface.HideButton();
            }
        });


        holder.edtEditText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

                Log.e("beforeTextChanged: ", s.toString());

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                Log.e("onTextChanged: ", s.toString());

            }

            @Override
            public void afterTextChanged(Editable editable) {

                Log.e("afterTextChanged: ", editable.toString());
                int iii;
                //  double jj=236444.99999999997;

                try {

                    if (editable.length() > 0) {

                        if (s_Type.equals("F")) {
                            iii = Integer.parseInt(arrayList.get(position).getAvailableFAQuantity());
                        } else {
                            iii = Integer.parseInt(arrayList.get(position).getAvailableQuantity());
                        }

                        if (Integer.parseInt(editable.toString()) > iii) {

                            GlobalClass.Alert("Entered quantity is more than the available quantity. You can pledge only " + iii + " shares.", ctx);

                            //ResetData(holder,position);
                            GlobalClass.hideKeyboardPledge(holder.edtEditText, ctx, keyBoadInterface);

                            holder.edtEditText.setText("");
                            holder.edtEditText.setSelection(0);

                            arrayList.get(position).setSetPledgeQty("0");
                            arrayList.get(position).setSetDpMargin("0");
                            arrayList.get(position).setUtilisedMargin("0");
                            holder.txtDpmargin.setText("0");

                            if (s_Type.equals("F")) {
                                holder.txtTotalShares.setText(model.getAvailableFAQuantity());
                                pledgeMarginTotalCount.PledgeCountFunding(position, holder.edtEditText, ctx, arrayList, holder.txtDpmargin, holder.txtTotalShares, s_Type);
                            } else {
                                holder.txtTotalShares.setText(model.getAvailableQuantity());
                                pledgeMarginTotalCount.PledgeCountMargin(position);
                            }

                        } else {

                            arrayList.get(position).setSetPledgeQty(editable.toString());

                            if (!s_Type.equals("F")) { // margin
                                int dp = Integer.parseInt(arrayList.get(position).getAvailableQuantity()) - Integer.parseInt(editable.toString());
                                holder.txtTotalShares.setText("" + dp);

                                Log.e("ccc", arrayList.get(position).getBuyingPower());

                                double x = Double.parseDouble(editable.toString());
                                double y = Double.parseDouble(arrayList.get(position).getBuyingPower());

                                //sum these two numbers

                                z = x * y;

                                arrayList.get(position).setSetDpMargin(String.valueOf(z));
                                pledgeMarginTotalCount.PledgeCountMargin(position);
                                holder.txtDpmargin.setText(("\u20B9" + DecimalLessIncludingComma(arrayList.get(position).getSetDpMargin())) + "");

                            } else {

                                // funding
                                int dp = Integer.parseInt(arrayList.get(position).getAvailableFAQuantity()) - Integer.parseInt(editable.toString());
                                holder.txtTotalShares.setText("" + dp);

                                double x = Double.parseDouble(editable.toString());
                                double y = Double.parseDouble(arrayList.get(position).getFundingPerShare());
                                double zz = Double.parseDouble(arrayList.get(position).getRiskMargin());
                                //sum these two numbers
                                z1 = x * y; //MaxFundingPerScrip

                                Log.e("0000==" + arrayList.get(position).getFundingPerShare(), String.valueOf(y));


                                z2 = z1 * zz;

//                                Log.e("x1x1", arrayList.get(position).getMaxFundingPerScrip());

                                Log.e("z222222", String.valueOf(z2));

                                String getMaxFundingPerScrip = VenturaApplication.getPreference().getSharedPrefFromTag("MaxFundingPerScrip", "");
                                String OverallScripValue = VenturaApplication.getPreference().getSharedPrefFromTag("OverallScripValue", "");
                                String MaxFundingPerScripOverall = VenturaApplication.getPreference().getSharedPrefFromTag("MaxFundingPerScripOverall", "");

                                Log.e("getMaxFundingPerScrip11", String.valueOf(z1));
                                Log.e("getMaxFundingPerScrip22", String.valueOf(Double.parseDouble(getMaxFundingPerScrip)));

                                Double aDouble1=new Double(z1);
                                Double aDouble2=new Double(getMaxFundingPerScrip);
                                Double aDouble3=new Double(OverallScripValue);
                                Double aDouble4=new Double(MaxFundingPerScripOverall);

                                double d1=aDouble1.doubleValue();
                                double d2=aDouble2.doubleValue();
                                double d3=aDouble3.doubleValue(); // OverallScripValue
                                double d4=aDouble4.doubleValue(); //MaxFundingPerScripOverall

//                                String MaxFunding_ = Formatter.formatter.format(MaxFunding);
//                                String TotalAmountFunded_ = Formatter.formatter.format(TotalAmountFunded);

//                                double xyz=d1-d2;

//                                Log.e("PledgeCountFunding: ", + d1 + " - "+d2 + "=" + xyz);
//                                double j= xyz;



                                //(FPS * CPR) should be less than MaxFundingPerScrip. otherwise.. toast msge

                                if (d1 > d2) {
                                    Log.e("sambooo", "here11");

                                    //ResetData(holder,position);
                                    GlobalClass.hideKeyboardPledge(holder.edtEditText, ctx, keyBoadInterface);

                                    holder.edtEditText.setText("");
                                    holder.edtEditText.setSelection(0);

                                    arrayList.get(position).setSetPledgeQty("0");
                                    arrayList.get(position).setSetDpMargin("0");
                                    arrayList.get(position).setUtilisedMargin("0");
                                    holder.txtDpmargin.setText("0");

                                    if (s_Type.equals("F")) {
                                        holder.txtTotalShares.setText(model.getAvailableFAQuantity());
                                        pledgeMarginTotalCount.PledgeCountFunding(position, holder.edtEditText, ctx, arrayList, holder.txtDpmargin, holder.txtTotalShares, s_Type);
                                    } else {
                                        holder.txtTotalShares.setText(model.getAvailableQuantity());
                                        pledgeMarginTotalCount.PledgeCountMargin(position);
                                    }

//                                    GlobalClass.Alert( "(FPS * CPR) should be less than MaxFundingPerScrip",ctx);
                                    GlobalClass.Alert( "You are exceeding Maximum Funding Value Per Scrip",ctx);
                                }

                                //OverallScripValue


                                else  if ((z1+d3)>d4) {

                                    //ResetData(holder,position);
                                    GlobalClass.hideKeyboardPledge(holder.edtEditText, ctx, keyBoadInterface);

                                    holder.edtEditText.setText("");
                                    holder.edtEditText.setSelection(0);

                                    arrayList.get(position).setSetPledgeQty("0");
                                    arrayList.get(position).setSetDpMargin("0");
                                    arrayList.get(position).setUtilisedMargin("0");
                                    holder.txtDpmargin.setText("0");

                                    if (s_Type.equals("F")) {
                                        holder.txtTotalShares.setText(model.getAvailableFAQuantity());
                                        pledgeMarginTotalCount.PledgeCountFunding(position, holder.edtEditText, ctx, arrayList, holder.txtDpmargin, holder.txtTotalShares, s_Type);
                                    } else {
                                        holder.txtTotalShares.setText(model.getAvailableQuantity());
                                        pledgeMarginTotalCount.PledgeCountMargin(position);
                                    }

                                    Log.e("sambooo", "here22");
//                                    GlobalClass.Alert( "overallscripvalue+(FPS * CPR) should be less than MaxFundingPerScripOverall",ctx);
                                    GlobalClass.Alert( "Scrip funding amount exceeding overall limit",ctx);
                                } else {

                                    Log.e("test", String.valueOf(z1));
                                    arrayList.get(position).setSetDpMargin(String.valueOf(z1));
                                    arrayList.get(position).setUtilisedMargin(String.valueOf(z2));
                                    pledgeMarginTotalCount.PledgeCountFunding(position, holder.edtEditText, ctx, arrayList, holder.txtDpmargin, holder.txtTotalShares, s_Type);

                                    holder.txtDpmargin.setText(("\u20B9" + TwoDecimalIncludingComma(arrayList.get(position).getSetDpMargin())) + "");
                                }
                            }
                        }

                    } else {

                        Log.e("samboo","here33" );
                        arrayList.get(position).setSetPledgeQty("0");
                        arrayList.get(position).setSetDpMargin("0");
                        arrayList.get(position).setUtilisedMargin("0");
                        holder.txtDpmargin.setText("0");
                        Log.e("vvvvvvvvvvvvvv",model.getAvailableFAQuantity() );
                        if (s_Type.equals("F")) {
                            holder.txtTotalShares.setText(model.getAvailableFAQuantity());
                            pledgeMarginTotalCount.PledgeCountFunding(position, holder.edtEditText, ctx, arrayList, holder.txtDpmargin, holder.txtTotalShares, s_Type);
                        } else {
                            holder.txtTotalShares.setText(model.getAvailableQuantity());
                            pledgeMarginTotalCount.PledgeCountMargin(position);
                        }
                    }
                } catch (Exception e) {
                    VenturaException.Print(e);

                    Log.e("exception: ", e.getMessage());
                }

            }
        });


    }

//    private void ResetData(MyViewHolder holder,int position) {
//        GlobalClass.hideKeyboardPledge(holder.edtEditText, ctx, keyBoadInterface);
//
//        holder.edtEditText.setText("");
//        holder.edtEditText.setSelection(0);
//
//        arrayList.get(position).setSetPledgeQty("0");
//        arrayList.get(position).setSetDpMargin("0");
//        arrayList.get(position).setUtilisedMargin("0");
//        holder.txtDpmargin.setText("0");
//
//        if (s_Type.equals("F")) {
//            holder.txtTotalShares.setText(model.getAvailableFAQuantity());
//            pledgeMarginTotalCount.PledgeCountFunding(position, holder.edtEditText, ctx, arrayList, holder.txtDpmargin, holder.txtTotalShares, s_Type);
//        } else {
//            holder.txtTotalShares.setText(model.getAvailableQuantity());
//            pledgeMarginTotalCount.PledgeCountMargin(position);
//        }
//
//    }

    private void Unchecked(MyViewHolder holder, int position, GetSRPledgeFormDetailsModel model) {

        arrayList.get(position).setSelected(false);
        holder.linearLayout2.setVisibility(View.GONE);
        holder.edtEditText.setBackgroundDrawable(null);
        holder.edtEditText.setVisibility(View.GONE);
        holder.txtEditText.setVisibility(View.VISIBLE);
        holder.edtEditText.setPressed(false);
        holder.edtEditText.setActivated(false);
        holder.edtEditText.setCursorVisible(false);
        holder.edtEditText.requestFocus();

        GlobalClass.hideKeyboardPledge(holder.edtEditText, ctx, keyBoadInterface);

        //
        arrayList.get(position).setSetPledgeQty("0");
        arrayList.get(position).setSetDpMargin("0");
        holder.edtEditText.setText("");

        if (s_Type.equals("F")) {
            holder.txtDpmargin.setText("0");
            holder.txtTotalShares.setText(model.getAvailableFAQuantity());
            pledgeMarginTotalCount.PledgeCountFunding(position, holder.edtEditText, ctx, arrayList, holder.txtDpmargin, holder.txtTotalShares, s_Type);
        } else {
            holder.txtDpmargin.setText("0");
            holder.txtTotalShares.setText(model.getAvailableQuantity());
            pledgeMarginTotalCount.PledgeCountMargin(position);
        }

    }

    private void Checked(MyViewHolder holder, int position,  List<GetSRPledgeFormDetailsModel> arrayList) {
        //OverallScripValue

        if (s_Type.equals("F")) {
            pledgeMarginTotalCount.ApiCall(arrayList.get(position).getISIN(),ctx);
        }
        pledgeMarginTotalCount.ScrollPosition(position);
        arrayList.get(position).setSelected(true);
        holder.linearLayout2.setVisibility(View.VISIBLE);
        holder.edtEditText.setBackgroundDrawable(ContextCompat.getDrawable(ctx, R.drawable.round_border_et));
        holder.edtEditText.setVisibility(View.VISIBLE);
        holder.txtEditText.setVisibility(View.GONE);
        holder.edtEditText.setPressed(true);
        holder.edtEditText.setCursorVisible(true);
        holder.edtEditText.setActivated(true);
        holder.edtEditText.requestFocus();

        GlobalClass.showKeyboardPledge(holder.edtEditText, ctx, keyBoadInterface);

    }

    @Override
    public int getItemCount() {
        return arrayList.size();
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public int getItemViewType(int position) {
        return position;
    }
}
