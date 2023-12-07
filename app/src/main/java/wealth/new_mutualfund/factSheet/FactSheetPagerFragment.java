package wealth.new_mutualfund.factSheet;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;
import androidx.viewpager.widget.ViewPager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;


import com.ventura.venturawealth.R;
import com.ventura.venturawealth.activities.homescreen.HomeActivity;

import org.json.JSONArray;

import butterknife.BindView;
import butterknife.ButterKnife;
import enums.MF_ASSET;


public class FactSheetPagerFragment extends Fragment implements View.OnClickListener {
    private HomeActivity homeActivity;
    private String selectedSchemeCode;
    private static final String ARG_PARAM1 = "param1";
    public static JSONArray sipReturnJar, lumpsumReturnJar;
    private int whiteColor, venturaColor;
    private Drawable whiteDrawable, VenturaDrawable;
    public static MF_ASSET selectedAsset;
    public static int schemeGrDivValue;
    public static String schemeName;
    public static String navDate;
    public static String aumDate;

    @BindView(R.id.viewpager) ViewPager viewPager;
    @BindView(R.id.summary_btn) TextView summary_btn;
    @BindView(R.id.performance_btn) TextView performance_btn;
    @BindView(R.id.holding_btn) TextView holding_btn;
    @BindView(R.id.allocation_btn) TextView allocation_btn;

    public FactSheetPagerFragment() {
        // Required empty public constructor
    }

    public static FactSheetPagerFragment newInstance(String schemeCode) {
        FactSheetPagerFragment fragment = new FactSheetPagerFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, schemeCode);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        homeActivity = (HomeActivity) context;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            selectedSchemeCode = getArguments().getString(ARG_PARAM1);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_fact_sheet_pager, container, false);
        ButterKnife.bind(this, view);

        sipReturnJar = new JSONArray();
        lumpsumReturnJar = new JSONArray();
        whiteColor = homeActivity.getResources().getColor(R.color.white);
        venturaColor = homeActivity.getResources().getColor(R.color.ventura_color);
        whiteDrawable = homeActivity.getResources().getDrawable(R.drawable.fact_sheet_toggle_white);
        VenturaDrawable = homeActivity.getResources().getDrawable(R.drawable.pager_tab_background);

        summary_btn.setOnClickListener(this);
        performance_btn.setOnClickListener(this);
        holding_btn.setOnClickListener(this);
        allocation_btn.setOnClickListener(this);
        viewPager.setAdapter(new MyPagerAdapter(getChildFragmentManager()));

        setButtons(0);
        viewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {

            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

            }

            @Override
            public void onPageSelected(int position) {
                setButtons(position);
            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });

        return view;
    }

    private void setButtons(int position){
        summary_btn.setBackground(whiteDrawable);
        performance_btn.setBackground(whiteDrawable);
        holding_btn.setBackground(whiteDrawable);
        allocation_btn.setBackground(whiteDrawable);

        summary_btn.setTextColor(venturaColor);
        performance_btn.setTextColor(venturaColor);
        holding_btn.setTextColor(venturaColor);
        allocation_btn.setTextColor(venturaColor);

        switch(position) {
            case 1:
                performance_btn.setBackground(VenturaDrawable);
                performance_btn.setTextColor(whiteColor);
                break;
            case 2:
                holding_btn.setBackground(VenturaDrawable);
                holding_btn.setTextColor(whiteColor);
                break;
            case 3:
                allocation_btn.setBackground(VenturaDrawable);
                allocation_btn.setTextColor(whiteColor);
                break;
            default:
                summary_btn.setBackground(VenturaDrawable);
                summary_btn.setTextColor(whiteColor);
                break;
        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.summary_btn:
                viewPager.setCurrentItem(0);
                break;
            case R.id.performance_btn:
                viewPager.setCurrentItem(1);
                break;
            case R.id.holding_btn:
                viewPager.setCurrentItem(2);
                break;
            case R.id.allocation_btn:
                viewPager.setCurrentItem(4);
                break;
        }
    }

    class MyPagerAdapter extends FragmentPagerAdapter {
        public MyPagerAdapter(FragmentManager fm) {
            super(fm);
        }

        @Override
        public Fragment getItem(int pos) {
            switch(pos) {
                case 1:
                    return FactSheetPerformance.newInstance(selectedSchemeCode);
                case 2:
                    return FactSheetHoldingFragment.newInstance(selectedSchemeCode);
                case 3:
                    return FactSheetAllocationFragment.newInstance(selectedSchemeCode);
                default:
                    return FactSheetSummaryFragment.newInstance(selectedSchemeCode);
            }
        }

        @Override
        public int getCount() {
            return 4;
        }
    }
}