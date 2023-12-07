package fragments;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.Dialog;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import com.google.android.material.tabs.TabLayout;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;
import androidx.viewpager.widget.ViewPager;

import android.util.SparseArray;
import android.view.View;
import android.view.ViewGroup;

import com.ventura.venturawealth.R;

import java.util.ArrayList;
import java.util.List;

import enums.eLogType;
import enums.ePrefTAG;
import fragments.settingstab.AboutFragment;
import fragments.settingstab.AlertSetting;
import fragments.settingstab.Columnfragment;
import fragments.settingstab.DisplayFragment;
import fragments.settingstab.LinkFragment;
import fragments.settingstab.WatchListsFragment;

import com.ventura.venturawealth.VenturaApplication;
import fragments.settingstab.Quickorderfragment;
import fragments.settingstab.ReaderFragment;
import fragments.settingstab.Securityfragment;
import interfaces.OnFontChange;
import utils.GlobalClass;
import utils.UserSession;
import view.help.ColumnSettingHelp;
import view.help.QuickOrderHelp;

/**
 * Created by XTREMSOFT on 10/19/2016.
 */
@SuppressLint("ValidFragment")
public class SettingsFragment extends BaseFragment  {

    private TabLayout home_tabs;
    private ViewPager m_viewPager;
    private OnFontChange OnFontChange;

    public int selectedSettingTab = 0;

    public SettingsFragment(OnFontChange OnFontChange){
        this.OnFontChange = OnFontChange;
    }

    @Override
    protected int getLayoutResource() {
        return R.layout.settings_screen;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        home_tabs = view.findViewById(R.id.home_tabs);
        m_viewPager = view.findViewById(R.id.fragment_viewpager);
        ((Activity)getActivity()).findViewById(R.id.homeRDgroup).setVisibility(View.GONE);
        initialization();
        GlobalClass.addAndroidLogForFragment(eLogType.SCREENLOG.name, getClass().getSimpleName(), UserSession.getLoginDetailsModel().getUserID());

    }

    private void initialization() {
        setupViewPager(m_viewPager);
        home_tabs.setupWithViewPager(m_viewPager);
        home_tabs.setTabMode(TabLayout.MODE_SCROLLABLE);
        m_viewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

            }

            @Override
            public void onPageSelected(int position) {
                selectedSettingTab = position;
                showHelp(position);
            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });
    }

    private void showHelp(int position) {
        switch (position){
            case 1:
                if (VenturaApplication.getPreference().getSharedPrefFromTag(ePrefTAG.QUICK_ORDER.name,true)){
                    Dialog quickOrderHelp = new QuickOrderHelp(GlobalClass.latestContext,false);
                    quickOrderHelp.show();
                }
                break;
            case 2:
                if (VenturaApplication.getPreference().getSharedPrefFromTag(ePrefTAG.COLUMN_HELP.name,true)){
                    Dialog columnSettingHelp = new ColumnSettingHelp(GlobalClass.latestContext,false);
                    columnSettingHelp.show();
                }
                break;
            default:
                break;
        }
    }

    private void setupViewPager(ViewPager viewPager) {
        ViewPagerAdapter adapter = new ViewPagerAdapter(getChildFragmentManager());
        adapter.addFragment(new DisplayFragment(OnFontChange),"DISPLAY");
        adapter.addFragment( new Quickorderfragment(),"QUICK ORDER");
        adapter.addFragment(new Columnfragment(), "COLUMNS");
        adapter.addFragment(new Securityfragment(), "SECURITY");
        adapter.addFragment(new AlertSetting(), "ALERT");
        adapter.addFragment(new WatchListsFragment(), "WATCH LISTS");
        adapter.addFragment(new LinkFragment(), "LINKS");
        adapter.addFragment(new ReaderFragment(), "MARKET READER");
        adapter.addFragment(AboutFragment.newInstance(), "ABOUT");
        viewPager.setAdapter(adapter);
    }

    public class ViewPagerAdapter extends FragmentPagerAdapter {
        private final List<Fragment> mFragmentList = new ArrayList<>();
        private final List<String> mFragmentTitleList = new ArrayList<>();
        private SparseArray<Fragment> registeredFragments = new SparseArray<>();
        public ViewPagerAdapter(FragmentManager fm) {
            super(fm);
        }

        public List<Fragment> getmFragmentList() {
            return mFragmentList;
        }

        public void clearData() {
            mFragmentList.clear();
            mFragmentTitleList.clear();
        }

        @Override
        public Fragment getItem(int position) {
            return mFragmentList.get(position);
        }

        @Override
        public int getCount() {
            return mFragmentList.size();
        }

        @Override
        public CharSequence getPageTitle(int position) {
            return mFragmentTitleList.get(position);
        }

        @Override
        public Object instantiateItem(ViewGroup container, int position) {
            /*try {
                if(mListViews.get(arg1).getParent()==null)
                    ((ViewPager) arg0).addView(mListViews.get(arg1), 0);
                else{
                    // I am new to android, it is strange that the view to be added is already bound to a parent
                    // Through trials and error I solve this problem with the following codes
                    // Add that the element of mlistviews is listview in pagerview;
                    ((ViewGroup)mListViews.get(arg1).getParent()).removeView(mListViews.get(arg1));

                    ((ViewPager) arg0).addView(mListViews.get(arg1), 0);
                }
            } catch (Exception e) {
                GlobalClass.log("parent=", ""+mListViews.get(arg1).getParent());
                e.printStackTrace();
            }*/

            Fragment fragment = (Fragment) super.instantiateItem(container, position);
            registeredFragments.put(position, fragment);
            return fragment;
        }
        @Override
        public void destroyItem(ViewGroup container, int position, Object object) {
            registeredFragments.remove(position);
            super.destroyItem(container, position, object);
        }

        public Fragment getRegisteredFragment(int position) {
            return registeredFragments.get(position);
        }

        public void addFragment(Fragment fragment, String title) {
            mFragmentList.add(fragment);
            mFragmentTitleList.add(title);
        }
    }
}
