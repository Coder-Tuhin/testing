package models;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by XTREMSOFT on 10/21/2016.
 */
public class ColumnsettingModel {
    private ArrayList<GrabberModel> columnpages;

    public ArrayList<GrabberModel> getColumnpages() {
        if (columnpages == null) return new ArrayList<>();
        return columnpages;
    }
    public void setColumnpages(ArrayList<GrabberModel> columnpages) {
        this.columnpages = columnpages;
    }
}
