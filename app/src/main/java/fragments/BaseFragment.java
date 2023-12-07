package fragments;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.ventura.venturawealth.activities.BaseActivity;

public abstract class BaseFragment  extends Fragment {

    //private BaseActivity _baseActivity;
    protected abstract int getLayoutResource();
    private View mView;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        if (mView == null) mView= inflater.inflate(getLayoutResource(),container,false);
        return mView;
    }

    @Override
    public void onResume() {
        super.onResume();

    }

    @Override
    public void onAttach(Context context) {
        //_baseActivity = (BaseActivity) BaseActivity.getLatestContext();
        super.onAttach(context);
    }

    @Override
    public void onDetach() {
        super.onDetach();
    }

    /*
    public BaseActivity getBaseActivity(){
        if (_baseActivity == null){
            _baseActivity = (BaseActivity) BaseActivity.getLatestContext();
        }
        return _baseActivity;
    }*/

    public void setVisibility(View view, int visibility){
        if (view!=null && view.getVisibility()!=visibility)
            view.setVisibility(visibility);
    }

    public void switchVisibility(View view){
        if (view==null)return;
        if (view.getVisibility()==View.VISIBLE)
            view.setVisibility(View.GONE);
        else view.setVisibility(View.VISIBLE);
    }


    @Override
    public void startActivity(Intent intent) {
        super.startActivity(intent);
       // getBaseActivity().overridePendingTransitionEnter();
    }

    @Override
    public void startActivityForResult(Intent intent, int requestCode) {
        super.startActivityForResult(intent, requestCode);
       // getBaseActivity().overridePendingTransitionEnter();
    }
}