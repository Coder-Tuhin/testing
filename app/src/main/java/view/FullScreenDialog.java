package view;

import android.app.Dialog;
import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;
import com.ventura.venturawealth.R;
import utils.StaticVariables;
import utils.VenturaException;

public class FullScreenDialog extends DialogFragment {

    private String _title = "";
    private String _description = "";
    private int _resource = 0;

    public static FullScreenDialog newInstance(int resource){
        FullScreenDialog fsd = new FullScreenDialog();
        try {
            Bundle args = new Bundle();
            args.putInt(StaticVariables.ARG_3, resource);
            fsd.setArguments(args);
        } catch (Exception e) {
            VenturaException.Print(e);
        }
        return fsd;
    }

    public static FullScreenDialog newInstance(String title,String description){
        FullScreenDialog fsd = new FullScreenDialog();
        try {
            Bundle args = new Bundle();
            args.putString(StaticVariables.ARG_1, title);
            args.putString(StaticVariables.ARG_2, description);
            args.putInt(StaticVariables.ARG_3, 0);
            fsd.setArguments(args);
        } catch (Exception e) {
            VenturaException.Print(e);
        }
        return fsd;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setStyle(DialogFragment.STYLE_NORMAL, R.style.FullScreenDialogStyle);
        Bundle args  = getArguments();
        try {
            assert args!=null;
            _resource = args.getInt(StaticVariables.ARG_3,0);
            if (_resource == 0){
                _title = args.getString(StaticVariables.ARG_1,"");
                _description = args.getString(StaticVariables.ARG_2,"");
            }
        }catch (Exception e){
            VenturaException.Print(e);
        }
    }

    @Override
    public void onStart() {
        super.onStart();
        Dialog dialog = getDialog();
        if (dialog != null)
        {
            int width = ViewGroup.LayoutParams.MATCH_PARENT;
            int height = ViewGroup.LayoutParams.MATCH_PARENT;
            dialog.getWindow().setLayout(width, height);
        }
    }


    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        if (_resource == 0)
            return inflater.inflate(R.layout.fullscreen_dialog,container,false);
        else
            return inflater.inflate(_resource,container,false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        if (_resource == 0){
            TextView titleTv = view.findViewById(R.id.titleTv);
            TextView descriptionTv = view.findViewById(R.id.descriptionTv);
            titleTv.setText(_title);
            descriptionTv.setText(_description);
        }
        LinearLayout close = view.findViewById(R.id.close);
        close.setOnClickListener(view1 -> dismiss());
    }
}
