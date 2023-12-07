package interfaces;

import android.content.Context;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.util.List;

import models.GetSRPledgeFormDetailsModel;

public interface PledgeMarginTotalCount {
    void PledgeCountVb(int pos);
    void PledgeCountVb1(int i);
    void PledgeCountDp(int pos);
    void PledgeCountDp1(int i, LinearLayout linearLayout);
    void PledgeCountMargin(int pos);
    void PledgeCountMargin1();
    void PledgeCountFunding(int pos, EditText edtEditText, Context ctx, List<GetSRPledgeFormDetailsModel> arrayList, TextView txtDpmargin, TextView txtTotalShares, String s_Type);
    void PledgeCountFunding1();
    void ScrollPosition(int position);
    void CallDlg(String msg, Context context);
    void ApiCall(String ISIN, Context context);
}
