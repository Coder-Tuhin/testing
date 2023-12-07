package wealth;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.app.FragmentManager;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Message;
import android.view.ContextThemeWrapper;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.ventura.venturawealth.R;
import utils.ScreenColor;

/**
 * Created by Admin on 03/04/2015.
 */
@SuppressWarnings("ALL")
public class Dialogs {

    static int index = 0;

    public static void showDialog(String msg, Context context) {
        try {
            AlertDialog.Builder builder = getAlertBuilder(context);
            builder.setMessage(msg)
                    .setCancelable(false)
                    .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int id) {
                            dialog.dismiss();
                        }
                    });
            AlertDialog alert = builder.create();
            alert.show();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void showToast(Context context, String msg, int duration) {
        try {
            Toast.makeText(context, msg, duration).show();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static AlertDialog.Builder getAlertBuilder(Context context) {
        try {
            return (new AlertDialog.Builder(new ContextThemeWrapper(context, android.R.style.Theme_Black)));
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    public static ProgressDialog getProgressDialog(Context context) {
        try {
            return (new ProgressDialog(new ContextThemeWrapper(context, android.R.style.Theme_Black)));
        } catch (Exception e) {
            e.printStackTrace();
        }

        return null;
    }
    public static void showToast(Activity mActivity , String msg) {
        try {
            Toast toast = new Toast(mActivity.getApplicationContext());
            View root =mActivity.getLayoutInflater().inflate(R.layout.toast_layout, null);
            toast.setView(root);
            ((TextView)root.findViewById(R.id.toast_txt)).setText(msg+"");
            toast.setGravity(Gravity.BOTTOM, 0, -50);
            toast.show();
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    public static void showSessionDialog(String msg, final Context context) {
        try {
            AlertDialog.Builder builder = getAlertBuilder(context);
            builder.setMessage(msg)
                    .setCancelable(false)
                    .setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int id) {
                          //  RCConnection.kill(context);
                        }
                    });
            AlertDialog alert = builder.create();
            alert.show();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void showLongTermShortTermDialog(Activity mActivity, Context context, long[] total, int _index, String comp_name, View view) {
        try {
            index = _index;
            if (total[0] == 0 && total[1] == 0) {
                Toast.makeText(context, "No data available", Toast.LENGTH_LONG).show();
            } else {
                FragmentManager fm = mActivity.getFragmentManager();
                showLongTermShortTerm editNameDialog = new showLongTermShortTerm(total, context, comp_name, view);
                editNameDialog.setStyle(DialogFragment.STYLE_NORMAL, android.R.style.Theme_Black);
                editNameDialog.show(fm, "fragment_edit_name");
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static class showLongTermShortTerm extends DialogFragment implements View.OnClickListener {

        long[] total;
        Context context;
        View root;
        String company_name;
        View parent;

        public showLongTermShortTerm() {
            // Empty constructor required for DialogFragment
        }

        @SuppressLint("ValidFragment")
        public showLongTermShortTerm(long[] _total, Context _context, String _comp_name, View v) {
            parent = v;
            total = _total;
            context = _context;
            company_name = _comp_name;
        }

        @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup container,
                                 Bundle savedInstanceState) {

            root = inflater.inflate(R.layout.longshort_term_popup, container);
            getDialog().getWindow().requestFeature(Window.FEATURE_NO_TITLE);

            if (total[0] == 0) {
                root.findViewById(R.id.layout_short_term).setVisibility(View.GONE);
            } else {
                root.findViewById(R.id.layout_short_term).setOnClickListener(this);
            }

            if (VenturaServerConnect.valueToString(total[0]).startsWith("-")) {
                ((TextView) root.findViewById(R.id.txt_short_term)).setTextColor(Color.RED);
            } else {
                ((TextView) root.findViewById(R.id.txt_short_term)).setTextColor(Color.GREEN);
            }

            if (total[1] == 0) {
                root.findViewById(R.id.layout_long_term).setVisibility(View.GONE);
                root.findViewById(R.id.seperator).setVisibility(View.GONE);

            } else {
                root.findViewById(R.id.layout_long_term).setOnClickListener(this);
            }

            if (VenturaServerConnect.valueToString(total[1]).startsWith("-")) {
                ((TextView) root.findViewById(R.id.txt_long_term)).setTextColor(Color.RED);
            } else {
                ((TextView) root.findViewById(R.id.txt_long_term)).setTextColor(Color.GREEN);
            }

            ((TextView) root.findViewById(R.id.txt_comp_name)).setText(company_name);

            ((ImageView) root.findViewById(R.id.img_close)).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Dialog dialog = getDialog();
                    if (dialog != null) {
                        dialog.dismiss();
                        parent.setAlpha(1);
                    }
                }
            });
            ((TextView) root.findViewById(R.id.txt_short_term)).setText(VenturaServerConnect.valueToString(total[0]).replace("-", ""));
            ((TextView) root.findViewById(R.id.txt_long_term)).setText(VenturaServerConnect.valueToString(total[1]).replace("-", ""));

            return root;
        }

        @Override
        public void onStart() {
            super.onStart();
            Dialog dialog = getDialog();
            if (dialog != null) {
                dialog.getWindow().setLayout(ScreenColor.getScreenWidth(context) - 30, ViewGroup.LayoutParams.WRAP_CONTENT);
                dialog.getWindow().addFlags(WindowManager.LayoutParams.FLAG_BLUR_BEHIND);
            }
        }

        @Override
        public void onClick(View view) {
            if (view instanceof LinearLayout) {
                Dialog dialog = getDialog();
                if (dialog != null) {
                    dialog.dismiss();
                    parent.setAlpha(1);
                }
            }
        }
    }
}
