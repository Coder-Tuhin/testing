package fragments.settingstab;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.leadingbyte.stockchart.Line;
import com.ventura.venturawealth.R;

import butterknife.ButterKnife;
import butterknife.BindView;
import enums.eLogType;
import utils.GlobalClass;
import utils.UserSession;

/**
 * Created by XTREMSOFT on 10/19/2016.
 */
public class LinkFragment extends Fragment implements View.OnClickListener{

    @BindView(R.id.equitylink)
    LinearLayout equitylink;
    @BindView(R.id.commoditylink)
    LinearLayout commoditylink;
    @BindView(R.id.nselink_textview)TextView nselink_textview;
    @BindView(R.id.bselink_textview)TextView bselink_textview;
    @BindView(R.id.sebilink_textview)TextView sebilink_textview;
    @BindView(R.id.vhomelink_textview)TextView vhomelink_textview;
    @BindView(R.id.ipnlink_textview)TextView ipnlink_textview;
    @BindView(R.id.ipblink_textview)TextView ipblink_textview;
    @BindView(R.id.usermanuallink_textview)TextView usermanuallink_textview;


    @BindView(R.id.venturalink_textview)TextView venturalink_textview;
    @BindView(R.id.mcxlink_textview)TextView mcxlink_textview;
    @BindView(R.id.ncdexlink_textview)TextView ncdexlink_textview;
    @BindView(R.id.sebiclink_textview)TextView sebiclink_textview;
    @BindView(R.id.mcxmarginlink_textview)TextView mcxmarginlink_textview;
    @BindView(R.id.ncdexmarginlink_textview)TextView ncdexmarginlink_textview;
    @BindView(R.id.pivotmcxlink_textview)TextView pivotmcxlink_textview;
    @BindView(R.id.pivotncdexlink_textview)TextView pivotncdexlink_textview;
    @BindView(R.id.dailypointerlink_textview)TextView dailypointerlink_textview;

    public LinkFragment(){
        super();
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View layout = inflater.inflate(R.layout.link_screen, container, false);
        ButterKnife.bind(this, layout);
        initialization();
        GlobalClass.addAndroidLogForFragment(eLogType.SCREENLOG.name, getClass().getSimpleName(), UserSession.getLoginDetailsModel().getUserID());

        return layout;
    }

    private void initialization() {
        equitylink.setVisibility(View.GONE);
        commoditylink.setVisibility(View.GONE);
        if(GlobalClass.isEquity()) {
            equitylink.setVisibility(View.VISIBLE);
            nselink_textview.setOnClickListener(this);
            bselink_textview.setOnClickListener(this);
            sebilink_textview.setOnClickListener(this);
            vhomelink_textview.setOnClickListener(this);
            ipnlink_textview.setOnClickListener(this);
            ipblink_textview.setOnClickListener(this);
            usermanuallink_textview.setOnClickListener(this);
        }else if(GlobalClass.isCommodity()){
            commoditylink.setVisibility(View.VISIBLE);

            venturalink_textview.setOnClickListener(this);
            mcxlink_textview.setOnClickListener(this);
            ncdexlink_textview.setOnClickListener(this);
            sebiclink_textview.setOnClickListener(this);
            mcxmarginlink_textview.setOnClickListener(this);
            ncdexmarginlink_textview.setOnClickListener(this);
            pivotmcxlink_textview.setOnClickListener(this);
            pivotncdexlink_textview.setOnClickListener(this);
            dailypointerlink_textview.setOnClickListener(this);
        }
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()){
            case R.id.nselink_textview:
                openUrl("http://www.nseindia.com/");
                break;
            case R.id.bselink_textview:
                openUrl("http://www.bseindia.com/");
                break;
            case R.id.sebilink_textview:
            case R.id.sebiclink_textview:
                openUrl("http://www.sebi.gov.in/sebiweb/");
                break;
            case R.id.vhomelink_textview:
                openUrl("https://www.ventura1.com/");
                break;
            case R.id.ipnlink_textview:
                openUrl("http://www.nseindia.com/");
                break;
            case R.id.ipblink_textview:
                openUrl("http://www.bseindia.com/");
                break;
            case R.id.usermanuallink_textview:
                openUrl("https://www.ventura1.com/download/vw/usermanual.pdf");
                break;

            case R.id.venturalink_textview:
                openUrl("https://www.ventura1.com/");
                break;
            case R.id.mcxlink_textview:
                openUrl("http://www.mcxindia.com");
                break;
            case R.id.ncdexlink_textview:
                openUrl("https://www.ncdex.com/index.aspx");
                break;
            case R.id.mcxmarginlink_textview:
                openUrl("https://www.ventura1.com/download/MCX_Margin.xls");
                break;
            case R.id.ncdexmarginlink_textview:
                openUrl("https://www.ventura1.com/download/NCDEX_Margin.xls");
                break;
            case R.id.pivotmcxlink_textview:
                openUrl("https://www.ventura1.com/Download/NON AGRI.PDF");
                break;
            case R.id.pivotncdexlink_textview:
                openUrl("https://www.ventura1.com/Download/AGRI.PDF");
                break;
            case R.id.dailypointerlink_textview:
                openUrl("https://dailypointer.acesphere.com/Downloads/dailypointer.aspx");
                break;
            default:
                break;
        }
    }
    private void openUrl(String url) {
        Uri uri = Uri.parse(url);
        Intent intent = new Intent(Intent.ACTION_VIEW, uri);
        startActivity(intent);
    }
    @Override
    public void onPause() {
        super.onPause();
    }
}
