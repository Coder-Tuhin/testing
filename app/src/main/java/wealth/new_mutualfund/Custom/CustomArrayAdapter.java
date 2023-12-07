package wealth.new_mutualfund.Custom;

import android.content.Context;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.ventura.venturawealth.R;

import java.util.List;

public class CustomArrayAdapter extends ArrayAdapter<String> {

    private final LayoutInflater mInflater;
    private final Context mContext;
    private final List<String> items;

    public CustomArrayAdapter(@NonNull Context context, @NonNull List objects) {
        super(context,0, objects);

        mContext = context;
        mInflater = LayoutInflater.from(context);
        items = objects;
    }
    @Override
    public View getDropDownView(int position, @Nullable View convertView,
                                @NonNull ViewGroup parent) {
        return createDropdownView(position, convertView, parent);
    }

    @Override
    public @NonNull
    View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        return createItemView(position, convertView, parent);
    }

    private View createDropdownView(int position, View convertView, ViewGroup parent){
        final View view = mInflater.inflate(R.layout.custom_spinner_drop_down_multiline, parent, false);
        TextView nameTv = view.findViewById(R.id.name);
        String string = items.get(position);
        nameTv.setText(string);
        return view;
    }

    private View createItemView(int position, View convertView, ViewGroup parent){
        final View view = mInflater.inflate(R.layout.mf_spinner_item_orange, parent, false);
        TextView nameTv = view.findViewById(R.id.name);
        String string = items.get(position);
        nameTv.setText(string);
        return view;
    }
}