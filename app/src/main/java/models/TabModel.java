package models;

import androidx.fragment.app.Fragment;

/**
 * Created by XTREMSOFT on 11/28/2016.
 */
public class TabModel {
    private Fragment tabfragment;
    private String tabtitle;

    public TabModel(Fragment tabfragment, String tabtitle) {
        this.tabfragment = tabfragment;
        this.tabtitle = tabtitle;
    }

    public Fragment getTabfragment() {
        return tabfragment;
    }

    public void setTabfragment(Fragment tabfragment) {
        this.tabfragment = tabfragment;
    }

    public String getTabtitle() {
        return tabtitle;
    }

    public void setTabtitle(String tabtitle) {
        this.tabtitle = tabtitle;
    }
}
