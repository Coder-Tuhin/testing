package adapters;
//06decSHIVA

import static utils.Formatter.DecimalLessIncludingComma;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Color;
import android.os.Build;
import android.text.Editable;
import android.text.Html;
import android.text.InputType;
import android.text.TextWatcher;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.RequiresApi;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.ventura.venturawealth.R;

import java.util.List;

import interfaces.KeyBoadInterface;
import interfaces.ModelTotalMargin;
import interfaces.PledgeMarginTotalCount;
import interfaces.RecyclerViewSmooth;
import models.GetDPPledgeFormDetailsModel;
import utils.Encriptions.ReusableLogics;
import utils.GlobalClass;
import utils.VenturaException;

public class PledgeAdapter extends RecyclerView.Adapter<PledgeAdapter.MyViewHolder> {
    private List<GetDPPledgeFormDetailsModel> arrayList;
    Context ctx;
    RecyclerViewSmooth recyclerViewSmooth;
    String s_Type = "";
    int z = 0;
    ModelTotalMargin modelTotalMargin;
    View itemView = null;
    PledgeMarginTotalCount pledgeMarginTotalCount;
    KeyBoadInterface keyBoadInterface;
    RecyclerView recyclerView;

    public class MyViewHolder extends RecyclerView.ViewHolder {
        public TextView txtSchemeName, txtBuyingPower, txtDpmargin, txtEditText, txtDPVBSR;
        public TextView txtTotalShares;
        public TextView txtTodaysPledge;
        EditText edtEditText;
        CheckBox checkBox;
        LinearLayout main_Layout;
        LinearLayout linearLayout1, linearLayout2;

        public MyViewHolder(View view) {
            super(view);
            checkBox = (CheckBox) view.findViewById(R.id.checkBox);
            txtSchemeName = (TextView) view.findViewById(R.id.txtSchemeName);
            main_Layout = (LinearLayout) view.findViewById(R.id.main_Layout);
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

    public PledgeAdapter(RecyclerView recyclerView, KeyBoadInterface keyBoadInterface, PledgeMarginTotalCount pledgeMarginTotalCount, List<GetDPPledgeFormDetailsModel> arrayList, Context context, RecyclerViewSmooth recyclerViewSmooth, String s, ModelTotalMargin modelTotalMargin) {
        this.arrayList = arrayList;
        this.ctx = context;
        this.recyclerViewSmooth = recyclerViewSmooth;
        this.s_Type = s;
        this.modelTotalMargin = modelTotalMargin;
        this.pledgeMarginTotalCount = pledgeMarginTotalCount;
        this.recyclerView = recyclerView;
        this.keyBoadInterface = keyBoadInterface;
    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.pledgevbdp_layout, parent, false);
        return new MyViewHolder(itemView);
    }

    @RequiresApi(api = Build.VERSION_CODES.N)
    @Override
    public void onBindViewHolder(MyViewHolder holder, @SuppressLint("RecyclerView") int position) {
        GetDPPledgeFormDetailsModel model = arrayList.get(position);

        try {
            holder.txtSchemeName.setText(model.getCompName());

            String BuyingPowerstr = "<font >" + "\u20B9" + "</font>" + "<font >" + "<b>" + DecimalLessIncludingComma(model.getBuyingPower()) + "<b></font>";

            holder.checkBox.setTag(position);
            holder.edtEditText.setTag(position);
            itemView.setTag(holder);

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                holder.txtBuyingPower.setText(Html.fromHtml(BuyingPowerstr, Html.FROM_HTML_MODE_LEGACY));
            }
            String cnfrmQtyStr = "<font >" + "<b>" + model.getConfirmedDPQuantity() + "<b></font>";
            holder.txtEditText.setText(Html.fromHtml(cnfrmQtyStr, Html.FROM_HTML_MODE_LEGACY));

            if (model.getConfirmedDPQuantity().equalsIgnoreCase("0")) {
                holder.txtEditText.setTextColor(Color.WHITE);
            } else {
                holder.txtEditText.setTextColor(Color.GREEN);
            }

            if (s_Type.equals("DP")) {
                holder.txtTotalShares.setText(model.getDPQuantity());
                holder.txtTodaysPledge.setText(model.getConfirmedDPQuantity());
                holder.txtEditText.setText(model.getConfirmedDPQuantity());

                if (ReusableLogics.getKeyPledgeActiveSession(ctx).contains("DP") && !ReusableLogics.getKeyPledgeActiveSession(ctx).contains("0")){
                    holder.checkBox.setEnabled(true);
                    holder.main_Layout.setEnabled(true);
                }else {
                    holder.checkBox.setClickable(false);
                    holder.main_Layout.setClickable(false);
                }

            } else {
                holder.txtTotalShares.setText(model.getVBQuantity());
                holder.txtTodaysPledge.setText(model.getConfirmedVBQuantity());
                holder.txtEditText.setText(model.getConfirmedVBQuantity());

                if (ReusableLogics.getKeyPledgeActiveSession(ctx).contains("VB") && !ReusableLogics.getKeyPledgeActiveSession(ctx).contains("0")){
                    holder.checkBox.setEnabled(true);
                    holder.main_Layout.setEnabled(true);
                }else {
                    holder.checkBox.setClickable(false);
                    holder.main_Layout.setClickable(false);
                }

            }
            holder.edtEditText.setRawInputType(InputType.TYPE_CLASS_NUMBER);
        } catch (Exception e) {
            VenturaException.Print(e);
        }




        holder.checkBox.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                try {
                    if (holder.checkBox.isChecked()) {
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

                    } else {
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

                        arrayList.get(position).setSetPledgeQty("0");
                        arrayList.get(position).setSetDpMargin("0");
                        holder.edtEditText.setText("");
                        holder.txtDpmargin.setText("0");

                        if (s_Type.equals("DP")) {
                            holder.txtTotalShares.setText(model.getDPQuantity());
                            pledgeMarginTotalCount.PledgeCountDp(position);
                        } else {
                            holder.txtTotalShares.setText(model.getVBQuantity());
                            pledgeMarginTotalCount.PledgeCountVb(position);
                        }
                    }

                }catch (Exception e){
                    e.getMessage();
                }


                pledgeMarginTotalCount.ScrollPosition(position);
            }
        });


        holder.main_Layout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                try {
                    if (holder.checkBox.isChecked()) {
                        holder.checkBox.setChecked(false);
                    } else {
                        holder.checkBox.setChecked(true);
                    }
                    if (holder.checkBox.isChecked()) {
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

                    } else {
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

                        arrayList.get(position).setSetPledgeQty("0");
                        arrayList.get(position).setSetDpMargin("0");
                        holder.edtEditText.setText("");
                        holder.txtDpmargin.setText("0");

                        if (s_Type.equals("DP")) {
                            holder.txtTotalShares.setText(model.getDPQuantity());
                            pledgeMarginTotalCount.PledgeCountDp(position);
                        } else {
                            holder.txtTotalShares.setText(model.getVBQuantity());
                            pledgeMarginTotalCount.PledgeCountVb(position);
                        }
                    }
                }catch (Exception e){
                    VenturaException.Print(e);
                }
            }
        });

        holder.edtEditText.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                boolean handled = false;
                GlobalClass.hideKeyboardPledge(holder.edtEditText, ctx, keyBoadInterface);
                return handled;
            }
        });

        holder.edtEditText.setOnKeyListener(new View.OnKeyListener() {
            public boolean onKey(View v, int keyCode, KeyEvent event) {
                return false;
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

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable editable) {
                int iii;

                try {
                    if (editable.length() > 0) {
                        if (s_Type.equals("DP")) {
                            iii = Integer.parseInt(arrayList.get(position).getDPQuantity());
                        } else {
                            iii = Integer.parseInt(arrayList.get(position).getVBQuantity());
                        }

                        if (Integer.parseInt(editable.toString()) > iii) {
                            GlobalClass.Alert("Entered quantity is more than the available quantity. You can pledge only " + iii + " shares.", ctx);
                            GlobalClass.hideKeyboardPledge(holder.edtEditText, ctx, keyBoadInterface);
                            arrayList.get(position).setSetPledgeQty("0");
                            arrayList.get(position).setSetDpMargin("0");
                            holder.edtEditText.setText("");
                            holder.txtDpmargin.setText("0");
                            holder.edtEditText.setSelection(0);
                            if (s_Type.equals("DP")) {
                                holder.txtTotalShares.setText(model.getDPQuantity());
                                pledgeMarginTotalCount.PledgeCountDp(position);
                            } else {
                                holder.txtTotalShares.setText(model.getVBQuantity());
                                pledgeMarginTotalCount.PledgeCountVb(position);
                            }
                        } else {
                            arrayList.get(position).setSetPledgeQty(editable.toString());
                            int x = Integer.parseInt(editable.toString());
                            int y = Integer.parseInt(arrayList.get(position).getBuyingPower());
                            //sum these two numbers
                            z = x * y;
                            if (s_Type.equals("DP")) {
                                int dp = Integer.parseInt(arrayList.get(position).getDPQuantity()) - Integer.parseInt(editable.toString());
                                holder.txtTotalShares.setText("" + dp);
                                arrayList.get(position).setSetDpMargin(String.valueOf(z));
                                pledgeMarginTotalCount.PledgeCountDp(position);

                            } else {
                                int dp = Integer.parseInt(arrayList.get(position).getVBQuantity()) - Integer.parseInt(editable.toString());
                                holder.txtTotalShares.setText("" + dp);
                                arrayList.get(position).setSetDpMargin(String.valueOf(z));
                                pledgeMarginTotalCount.PledgeCountVb(position);
                            }
                            holder.txtDpmargin.setText(("\u20B9" + DecimalLessIncludingComma(arrayList.get(position).getSetDpMargin())) + "");
                        }

                    } else {
                        arrayList.get(position).setSetPledgeQty("0");
                        arrayList.get(position).setSetDpMargin("0");
                        holder.txtDpmargin.setText("0");
                        if (s_Type.equals("DP")) {
                            holder.txtTotalShares.setText(model.getDPQuantity());
                            pledgeMarginTotalCount.PledgeCountDp(position);
                        } else {
                            holder.txtTotalShares.setText(model.getVBQuantity());
                            pledgeMarginTotalCount.PledgeCountVb(position);
                        }
                    }
                } catch (Exception e) {
                    VenturaException.Print(e);
                }


            }
        });
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