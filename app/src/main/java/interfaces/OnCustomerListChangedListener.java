package interfaces;

import java.util.List;

import models.GrabberModel;

/**
 * Created by XTREMSOFT on 10/20/2016.
 */
public interface OnCustomerListChangedListener {
    void onNoteListChanged(List<GrabberModel> customers);
}
