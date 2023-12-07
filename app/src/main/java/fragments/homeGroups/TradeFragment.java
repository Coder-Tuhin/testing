package fragments.homeGroups;

import android.Manifest;
import android.annotation.SuppressLint;
import android.annotation.TargetApi;
import android.app.Activity;
import android.app.KeyguardManager;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.hardware.biometrics.BiometricPrompt;
import android.hardware.fingerprint.FingerprintManager;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.CancellationSignal;
import android.os.Handler;
import android.os.Message;
import android.security.keystore.KeyGenParameterSpec;
import android.security.keystore.KeyPermanentlyInvalidatedException;
import android.security.keystore.KeyProperties;
import androidx.annotation.Nullable;
import androidx.core.app.ActivityCompat;
import androidx.fragment.app.DialogFragment;
import androidx.fragment.app.Fragment;
import androidx.core.content.ContextCompat;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.text.method.PasswordTransformationMethod;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.view.animation.Animation;
import android.view.animation.TranslateAnimation;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.ventura.venturawealth.FingerprintHandler;
import com.ventura.venturawealth.FingerprintHandlerforpopup;
import com.ventura.venturawealth.FingerprintInterfaceforpopup;
import com.ventura.venturawealth.R;
import com.ventura.venturawealth.VenturaApplication;
import com.ventura.venturawealth.activities.homescreen.HomeActivity;

import java.io.IOException;
import java.security.InvalidAlgorithmParameterException;
import java.security.InvalidKeyException;
import java.security.KeyStore;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.NoSuchProviderException;
import java.security.UnrecoverableKeyException;
import java.security.cert.CertificateException;
import java.util.Calendar;
import java.util.Date;

import javax.crypto.Cipher;
import javax.crypto.KeyGenerator;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.SecretKey;

import Structure.Request.Auth.NewServerIPRequest;
import Structure.Request.Auth.NewServerIPResp;
import Structure.Request.RC.ClientLoginReq_Pointer;
import Structure.Request.RC.StructPINGeneration;
import Structure.Response.RC.StructClientLoginIntraDelResp;
import Structure.Response.RC.StructLoginFailure;
import connection.Config;
import connection.Connect;
import connection.ConnectionProcess;
import connection.SendDataToAuthServer;
import connection.SendDataToBCServer;
import connection.SendDataToRCServer;
import enums.eConstant;
import enums.eForHandler;
import enums.eLogType;
import enums.eMessage;
import enums.eMessageCode;
import enums.eServerType;
import enums.eSocketClient;
import enums.eTagForErrorMsg;
import enums.eTradeFrom;
import fragments.DashboardFragment;
import interfaces.OnAlertListener;
import models.MPINModel;
import models.TradeLoginModel;
import utils.Biometric.BiometricCallbackV28;
import utils.Biometric.BiometricUtils;
import utils.Constants;
import utils.DatePick;
import utils.DateUtil;
import utils.GlobalClass;
import utils.MobileInfo;
import utils.ObjectHolder;
import utils.PreferenceHandler;
import utils.UserSession;
import utils.VenturaException;
import view.AlertBox;

import static android.content.Context.FINGERPRINT_SERVICE;
import static android.content.Context.KEYGUARD_SERVICE;

/**
 * Created by XTREMSOFT on 24-May-2018.
 */

public class TradeFragment extends Fragment implements View.OnClickListener, RadioGroup.OnCheckedChangeListener,
        ConnectionProcess, OnAlertListener, android.app.DatePickerDialog.OnDateSetListener, FingerprintInterfaceforpopup/*FingerprintInterface*/ {
    private HomeActivity
            homeActivity;
    private LinearLayout
            mpinLinear,
            fingerLayout,
            generalLogin;
    private EditText
            mClientID;
    private RadioGroup
            loginRd,
            mTradeRDgrp;
    private EditText mDOB,
            mPAN,
            mPassword,
            clientCodempin,
            mpin,
            pin;
    private Button
            mLogin;
    private ImageView
            mCalenderImg;
    private RadioButton
            mPanRDbtn,
            mDobRDbtn,
            loginRdBtn,
            mpinRdBtn;
    private RelativeLayout
            mDobRelative;
    private LinearLayout
            mPANLayout;

    private View
            mView;
    private CheckBox
            generateMpin;
    private TextView
            forgotMpin,
            mpinNote,
            genmpinnote,
            forgot_PasswordNew;
    private boolean
            appendAvailableDate = true;
    private boolean
            appendAvailableMonth = true;
    private eTradeFrom
            tradeFrom;
    private TradeLoginModel tlm;
    private boolean isMpinLogin = false;
    private ImageView password_toggle_open,password_toggle_close,pin_toggle_open,pin_toggle_close,pan_toggle_open,pan_toggle_close;
    int start,end;

    public TradeFragment(){super();}
    private KeyStore keyStore;
    // Variable used for storing the key in the Android Keystore container
    private static final String KEY_NAME = "androidkey";
    private Cipher cipher;
    private TextView textView;
    private ImageView fingerTouchimageView;
    private FingerprintHandler fingerHelper;

    private eSocketClient serverConnect = eSocketClient.INTERACTIVE;

    @SuppressLint("ValidFragment")
    public TradeFragment(eTradeFrom eTradeFrom) {
        this.tradeFrom = eTradeFrom;

    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof HomeActivity) {
            homeActivity = (HomeActivity) context;
        }
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        getActivity().setTheme(R.style.AppTheme);
        homeActivity = (HomeActivity) getActivity();

        ((Activity) getActivity()).findViewById(R.id.home_relative).setVisibility(View.GONE);
        ((Activity) getActivity()).findViewById(R.id.homeRDgroup).setVisibility(View.VISIBLE);
        homeActivity.CheckRadioButton(HomeActivity.RadioButtons.TRADE);

            getActivity().getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_RESIZE |
                    WindowManager.LayoutParams.SOFT_INPUT_STATE_HIDDEN);
            //mView = inflater.inflate(R.layout.trade_new, container, false);
            mView = inflater.inflate(R.layout.trade_sso, container, false);
            //initViews(mView);
            GlobalClass.showProgressDialog("Connecting to Trade Server...");
            serverConnect = eSocketClient.INTERACTIVE;
            Connect.connect(GlobalClass.latestContext, this, eSocketClient.INTERACTIVE);
            //new sendTradeLogin().execute();
            if(GlobalClass.tradeLoginHandler == null){
                GlobalClass.tradeLoginHandler = new TradeLoginHandler();
            }

        GlobalClass.addAndroidLogForFragment(eLogType.SCREENLOG.name, "TradeLogin",UserSession.getLoginDetailsModel().getUserID());

        return mView;
    }

    @Override
    public void onResume() {
        super.onResume();
        if(GlobalClass.tradeLoginHandler == null){
            GlobalClass.tradeLoginHandler = new TradeLoginHandler();
        }
    }

    @Override
    public void onPause() {
        super.onPause();
        GlobalClass.tradeLoginHandler = null;
    }

    private void initViews(View view) {

        mpinLinear =(LinearLayout) view.findViewById(R.id.mpinLinear);
        generalLogin =(LinearLayout) view.findViewById(R.id.generalLogin);

        fingerLayout = (LinearLayout)view.findViewById(R.id.fingerLayout);
        textView = (TextView)view.findViewById(R.id.errorText);
        fingerTouchimageView = (ImageView)view.findViewById(R.id.icon);

        fingerTouchimageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                initializeTouch();
            }
        });
        password_toggle_open = view.findViewById(R.id.password_toggle_open);
        password_toggle_close = view.findViewById(R.id.password_toggle_close);
        pin_toggle_close = view.findViewById(R.id.pin_toggle_close);
        pin_toggle_open = view.findViewById(R.id.pin_toggle_open);
        pan_toggle_close = view.findViewById(R.id.pan_toggle_close);
        pan_toggle_open = view.findViewById(R.id.pan_toggle_open);

        loginRd =(RadioGroup) view.findViewById(R.id.loginRd);
        mDOB = (EditText) view.findViewById(R.id.dob);
        mPAN = (EditText) view.findViewById(R.id.pan);
        pin = (EditText) view.findViewById(R.id.pin);
        mClientID = (EditText) view.findViewById(R.id.clientID);
        mPassword = (EditText) view.findViewById(R.id.password);
        mpin = (EditText) view.findViewById(R.id.mpin);
        clientCodempin = (EditText) view.findViewById(R.id.clientCodeMPIN);
        clientCodempin.setText(UserSession.getLoginDetailsModel().getUserID());
        clientCodempin.setEnabled(false);

        mLogin = (Button) view.findViewById(R.id.loginButton);
        mCalenderImg = (ImageView) view.findViewById(R.id.calenderImage);
        mTradeRDgrp = (RadioGroup) view.findViewById(R.id.tradeRDgroup);
        mPanRDbtn = (RadioButton) view.findViewById(R.id.panRDbutton);
        mDobRDbtn = (RadioButton) view.findViewById(R.id.dobRDbutton);
        mpinRdBtn = (RadioButton) view.findViewById(R.id.mpinRdBtn);
        loginRdBtn = (RadioButton) view.findViewById(R.id.loginRdBtn);
        mDobRelative = (RelativeLayout) view.findViewById(R.id.dobRelative);
        mPANLayout = (LinearLayout) view.findViewById(R.id.panlayout);

        generateMpin = (CheckBox) view.findViewById(R.id.generateMpin);
        mpinNote = (TextView)view.findViewById(R.id.mPinNote);
        forgotMpin = (TextView) view.findViewById(R.id.forgotMpin);
        forgot_PasswordNew = (TextView) view.findViewById(R.id.forgot_PasswordNew);

        genmpinnote = (TextView) view.findViewById(R.id.genmpinnote);
        mLogin.setOnClickListener(this);
        forgot_PasswordNew.setOnClickListener(this);
        mCalenderImg.setOnClickListener(this);
        mTradeRDgrp.setOnCheckedChangeListener(this);
        mClientID.setText(UserSession.getLoginDetailsModel().getUserID());
        mClientID.setEnabled(false);
        //mClientID.setOnClickListener(this);
        mDOB.addTextChangedListener(watcher);
        loginRd.setOnCheckedChangeListener(this);
        tlm = VenturaApplication.getPreference().getTradeDetails();
        generateMpin.setVisibility(View.GONE);

        if (tlm == null){
            loginRd.setVisibility(View.INVISIBLE);
            generateMpin.setText("Generate MPIN");
            genmpinnote.setVisibility(View.GONE);
            generateMpin.setChecked(true);
        } else if(tlm.isDayFirstLogin()){
            loginRd.setVisibility(View.INVISIBLE);
            //generateMpin.setVisibility(View.VISIBLE);
            generateMpin.setText("Generate New MPIN");
            Date currDate = Calendar.getInstance().getTime();
            int dateDiff = DateUtil.compareDates(currDate,tlm.getSaveDate());
            if((90 -dateDiff)>0) {
                genmpinnote.setText("Note: Your current MPIN is valid for " + (90 - dateDiff) + " days. Subsequent logins during the day can be done using your MPIN.");// To generate a new MPIN select the 'Generate New MPIN' checkbox.");
            }
            else {
                generateMpin.setChecked(true);
                genmpinnote.setText("Note: Your current MPIN has expired. New MPIN will be generated."); //To generate a new MPIN select the 'Generate New MPIN' checkbox.");
            }
            genmpinnote.setVisibility(View.VISIBLE);
            mpinNote.setVisibility(View.GONE);
        }else {
            mpinRdBtn.setChecked(true);
            generalLogin.setVisibility(View.GONE);
            mpinLinear.setVisibility(View.VISIBLE);
            forgotMpin.setVisibility(View.VISIBLE);
            generateMpin.setVisibility(View.GONE);
            genmpinnote.setVisibility(View.GONE);
            mpinNote.setVisibility(View.GONE);
            forgotMpin.setOnClickListener(TradeFragment.this);
        }
        password_toggle_close.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                start=mPassword.getSelectionStart();
                end=mPassword.getSelectionEnd();
                mPassword.setTransformationMethod(null);
                mPassword.setSelection(start,end);
                password_toggle_close.setVisibility(View.INVISIBLE);
                password_toggle_open.setVisibility(View.VISIBLE);
            }
        });
        password_toggle_open.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                start=mPassword.getSelectionStart();
                end=mPassword.getSelectionEnd();
                mPassword.setTransformationMethod(new PasswordTransformationMethod());
                mPassword.setSelection(start,end);
                password_toggle_close.setVisibility(View.VISIBLE);
                password_toggle_open.setVisibility(View.INVISIBLE);
            }
        });
        pan_toggle_close.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                start=mPAN.getSelectionStart();
                end=mPAN.getSelectionEnd();
                mPAN.setTransformationMethod(null);
                mPAN.setSelection(start,end);
                pan_toggle_close.setVisibility(View.INVISIBLE);
                pan_toggle_open.setVisibility(View.VISIBLE);
            }
        });
        pan_toggle_open.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                start=mPAN.getSelectionStart();
                end=mPAN.getSelectionEnd();
                mPAN.setTransformationMethod(new PasswordTransformationMethod());
                mPAN.setSelection(start,end);
                pan_toggle_close.setVisibility(View.VISIBLE);
                pan_toggle_open.setVisibility(View.INVISIBLE);
            }
        });
        pin_toggle_close.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                start=pin.getSelectionStart();
                end=pin.getSelectionEnd();
                pin.setTransformationMethod(null);
                pin.setSelection(start,end);
                pin_toggle_close.setVisibility(View.INVISIBLE);
                pin_toggle_open.setVisibility(View.VISIBLE);
            }
        });
        pin_toggle_open.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                start=pin.getSelectionStart();
                end=pin.getSelectionEnd();
                pin.setTransformationMethod(new PasswordTransformationMethod());
                pin.setSelection(start,end);
                pin_toggle_close.setVisibility(View.VISIBLE);
                pin_toggle_open.setVisibility(View.INVISIBLE);
            }
        });
    }

    @Override
    public void onCheckedChanged(RadioGroup group, int checkedId) {
        switch (checkedId){
            case R.id.loginRdBtn:
                slidefromLefttoRight();
                break;
            case R.id.mpinRdBtn:
                slidefromRightToLeft();
                break;
            case R.id.panRDbutton:
                mPANLayout.setVisibility(View.VISIBLE);
                mDobRelative.setVisibility(View.GONE);
                break;
            case R.id.dobRDbutton:
                mPANLayout.setVisibility(View.GONE);
                mDobRelative.setVisibility(View.VISIBLE);
                break;
            default:
                break;
        }
    }

    public void slidefromRightToLeft() {
        try {
            isMpinLogin = true;
            TranslateAnimation animate;
            animate = new TranslateAnimation(0,-generalLogin.getWidth(), 0, 0);
            animate.setDuration(300);
            animate.setFillAfter(true);
            generalLogin.startAnimation(animate);

            if (mpinLinear.getVisibility()==View.GONE){
                mpinLinear.setVisibility(View.VISIBLE);
                mpin.setVisibility(View.VISIBLE);
                forgotMpin.setVisibility(View.VISIBLE);
                clientCodempin.setVisibility(View.VISIBLE);
                forgotMpin.setVisibility(View.VISIBLE);
                fingerLayout.setVisibility(View.VISIBLE);
                fingerTouchimageView.setImageResource(R.drawable.finger);
                textView.setText("");
            }
            mpinLinear.requestFocus();
            TranslateAnimation animate1;
            animate1 = new TranslateAnimation(generalLogin.getWidth(),0, 0, 0);
            animate1.setDuration(400);
            animate1.setFillAfter(true);
            mpinLinear.startAnimation(animate1);

            animate.setAnimationListener(new Animation.AnimationListener() {
                @Override
                public void onAnimationStart(Animation animation) {

                }
                @Override
                public void onAnimationEnd(Animation animation) {
                    generalLogin.setVisibility(View.GONE);
                    mpin.requestFocus();
                    initializeTouch();
                }
                @Override
                public void onAnimationRepeat(Animation animation) {

                }
            });

        }catch (Exception e){
            e.printStackTrace();
        }
    }
    @SuppressLint("MissingPermission")
    private void initializeTouch(){
        // Initializing both Android Keyguard Manager and Fingerprint Manager
        try {
            KeyguardManager keyguardManager = (KeyguardManager) getContext().getSystemService(KEYGUARD_SERVICE);
            FingerprintManager fingerprintManager = null;
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                fingerprintManager = (FingerprintManager) getContext().getSystemService(FINGERPRINT_SERVICE);
            }
            //if (headerLayout.getVisibility()==View.VISIBLE) {
            // Check whether the device has a Fingerprint sensor.
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                if (fingerprintManager == null || !fingerprintManager.isHardwareDetected()) {
                    /**
                     * An error message will be displayed if the device does not contain the fingerprint hardware.
                     * However if you plan to implement a default authentication method,
                     * you can redirect the user to a default authentication activity from here.
                     * Example:
                     * Intent intent = new Intent(this, DefaultAuthenticationActivity.class);
                     * startActivity(intent);
                     */
                    textView.setText("Your Device does not have a Fingerprint Sensor");
                    fingerLayout.setVisibility(View.GONE);
                } else {
                    // Checks whether fingerprint permission is set on manifest
                    if (ActivityCompat.checkSelfPermission(getContext(), Manifest.permission.USE_FINGERPRINT) != PackageManager.PERMISSION_GRANTED) {
                        textView.setText("Fingerprint authentication permission not enabled");
                        fingerLayout.setVisibility(View.GONE);
                    } else {
                        // Check whether at least one fingerprint is registered
                        if (!fingerprintManager.hasEnrolledFingerprints()) {
                            textView.setText("Register at least one fingerprint in Settings");
                            fingerLayout.setVisibility(View.GONE);
                        } else {
                            // Checks whether lock screen security is enabled or not
                            if (!keyguardManager.isKeyguardSecure()) {
                                textView.setText("Lock screen security not enabled in Settings");
                                fingerLayout.setVisibility(View.GONE);
                            } else {
                                fingerLayout.setVisibility(View.VISIBLE);
                                if (BiometricUtils.isBiometricPromptEnabled()) {
                                    displayBiometricPrompt(this);
                                } else {
                                    fingerTouchimageView.setEnabled(false);
                                    generateKey();
                                    if (cipherInit()) {
                                        FingerprintManager.CryptoObject cryptoObject = new FingerprintManager.CryptoObject(cipher);
                                        FingerprintHandlerforpopup helper = new FingerprintHandlerforpopup(GlobalClass.latestContext, this);
                                        helper.startAuth(fingerprintManager, cryptoObject);
                                    }
                                }
                            }
                        }
                    }
                    //}
                }
            }
        }catch (Exception ex){
            ex.printStackTrace();
            fingerLayout.setVisibility(View.GONE);
        }
    }

    protected CancellationSignal mCancellationSignal = new CancellationSignal();


    @SuppressLint("MissingPermission")
    @TargetApi(Build.VERSION_CODES.P)
    private void displayBiometricPrompt(final FingerprintInterfaceforpopup biometricCallback) {
        fingerTouchimageView.setEnabled(false);
        new BiometricPrompt.Builder(GlobalClass.latestContext)
                .setTitle("Biometric Authentication")
                .setSubtitle("")
                .setDescription("Please touch the finger scanner.")
                .setNegativeButton("Cancel", GlobalClass.latestContext.getMainExecutor(), new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        biometricCallback.onAuthFailed();
                    }
                })
                .build()
                .authenticate(mCancellationSignal, GlobalClass.latestContext.getMainExecutor(),new BiometricCallbackV28(biometricCallback));
    }

    @TargetApi(Build.VERSION_CODES.M)
    protected void generateKey() {
        try {
            keyStore = KeyStore.getInstance("AndroidKeyStore");
        } catch (Exception e) {
            e.printStackTrace();
        }
        KeyGenerator keyGenerator;
        try {
            keyGenerator = KeyGenerator.getInstance(KeyProperties.KEY_ALGORITHM_AES, "AndroidKeyStore");
        } catch (NoSuchAlgorithmException | NoSuchProviderException e) {
            throw new RuntimeException("Failed to get KeyGenerator instance", e);
        }
        try {
            keyStore.load(null);
            keyGenerator.init(new
                    KeyGenParameterSpec.Builder(KEY_NAME,
                    KeyProperties.PURPOSE_ENCRYPT |
                            KeyProperties.PURPOSE_DECRYPT)
                    .setBlockModes(KeyProperties.BLOCK_MODE_CBC)
                    .setUserAuthenticationRequired(true)
                    .setEncryptionPaddings(
                            KeyProperties.ENCRYPTION_PADDING_PKCS7)
                    .build());
            keyGenerator.generateKey();
        } catch (NoSuchAlgorithmException |
                InvalidAlgorithmParameterException
                | CertificateException | IOException e) {
            throw new RuntimeException(e);
        }
    }


    @TargetApi(Build.VERSION_CODES.M)
    public boolean cipherInit() {
        try {
            cipher = Cipher.getInstance(KeyProperties.KEY_ALGORITHM_AES + "/" + KeyProperties.BLOCK_MODE_CBC + "/" + KeyProperties.ENCRYPTION_PADDING_PKCS7);
        } catch (NoSuchAlgorithmException | NoSuchPaddingException e) {
            throw new RuntimeException("Failed to get Cipher", e);
        }
        try {
            keyStore.load(null);
            SecretKey key = (SecretKey) keyStore.getKey(KEY_NAME, null);
            cipher.init(Cipher.ENCRYPT_MODE, key);
            return true;
        } catch (KeyPermanentlyInvalidatedException e) {
            return false;
        } catch (KeyStoreException | CertificateException | UnrecoverableKeyException | IOException | NoSuchAlgorithmException | InvalidKeyException e) {
            throw new RuntimeException("Failed to init Cipher", e);
        }
    }

    public void slidefromLefttoRight() {
        try {
            isMpinLogin = false;
            TranslateAnimation animate;
            animate = new TranslateAnimation(0,mpinLinear.getWidth(), 0, 0);
            animate.setDuration(300);
            animate.setFillAfter(true);
            mpinLinear.startAnimation(animate);

            if (generalLogin.getVisibility()==View.GONE)
                generalLogin.setVisibility(View.VISIBLE);

            TranslateAnimation animate1;
            animate1 = new TranslateAnimation(-mpinLinear.getWidth(),0, 0, 0);
            animate1.setDuration(400);
            animate1.setFillAfter(true);
            generalLogin.startAnimation(animate1);

            animate.setAnimationListener(new Animation.AnimationListener() {
                @Override
                public void onAnimationStart(Animation animation) {
                    //headerLayout.setVisibility(View.GONE);
                }

                @Override
                public void onAnimationEnd(Animation animation) {
                    mpinLinear.setVisibility(View.GONE);
                    forgotMpin.setVisibility(View.GONE);
                    mpin.setVisibility(View.GONE);
                    clientCodempin.setVisibility(View.GONE);
                    fingerLayout.setVisibility(View.GONE);
                    mClientID.requestFocus();

                    if(fingerHelper != null){
                        fingerHelper.cancelAuth();
                        fingerHelper = null;
                    }
                }

                @Override
                public void onAnimationRepeat(Animation animation) {

                }
            });


        }catch (Exception e){
            e.printStackTrace();
        }
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.loginButton:
                if (isValidate()) {
                    mLogin.setEnabled(false);
                    connectToTradeServer();
                }
                break;
            case R.id.calenderImage:
                DialogFragment newFragment = new DatePick(this);
                newFragment.show(getActivity().getSupportFragmentManager(), "datePicker");
                break;
            case R.id.forgotMpin:
                showMessageOKCancel("Are you sure you want to re-generate your MPIN?",
                        new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                clearMpin();
                            }
                        });
                break;
            default:
                break;
        }
    }


    private void clearMpin() {
        try {
            PreferenceHandler.setMpinRetryCount(1);
            VenturaApplication.getPreference().setTradeDetails(null);
            MPINModel mpinDetails = VenturaApplication.getPreference().getMPINDetails();
            mpinDetails.clearMPIN(UserSession.getLoginDetailsModel().getUserID());
            VenturaApplication.getPreference().setMPINDetails(mpinDetails);

            tlm = null;
            loginRd.setVisibility(View.INVISIBLE);
            generateMpin.setVisibility(View.GONE);
            mpinNote.setVisibility(View.VISIBLE);
            loginRdBtn.setChecked(true);
            generateMpin.setText("Generate MPIN");
            generateMpin.setChecked(true);
            genmpinnote.setVisibility(View.GONE);
        }catch (Exception ex){
            VenturaException.Print(ex);
        }
    }

    private void showMessageOKCancel(String message, DialogInterface.OnClickListener okListener) {
        new AlertDialog.Builder(GlobalClass.latestContext)
                .setMessage(message)
                .setPositiveButton("Generate", okListener)
                .setNegativeButton("Cancel", null)
                .create()
                .show();
    }

    @Override
    public void onDateSet(DatePicker datePicker, int year, int monthOfYear, int dayOfMonth) {
        String dayString = dayOfMonth < 10 ? "0" + dayOfMonth : "" + dayOfMonth;
        int month = monthOfYear + 1;
        String monthString = month < 10 ? "0" + month : "" + month;
        String date = "" + dayString + "/" + (monthString) + "/" + year;
        mDOB.setText(date);
    }

    private boolean isValidate() {
        if (mpinLinear.getVisibility()==View.VISIBLE){
            if (!clientCodempin.getText().toString().trim().equalsIgnoreCase(UserSession.getLoginDetailsModel().getUserID())) {
                GlobalClass.showAlertDialog(eMessage.INVALID_CLIENTID.name);
                return false;
            }
            Date currDate = Calendar.getInstance().getTime();
            int dateDiff = DateUtil.compareDates(currDate,tlm.getSaveDate());
            if (dateDiff>=90){
                showMessageOKCancel("Your MPIN has expired. generate a new MPIN",
                        new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                clearMpin();
                            }
                        });
                //VenturaApplication.getPreference().setTradeDetails(null);
                //PreferenceHandler.setMpinRetryCount(1);
                return false;
            }
            String mPin = mpin.getText().toString().trim();
            if (!mPin.equals(tlm.getMpin())){
                if (PreferenceHandler.mpinRetryCount()>4){
                    clearMpin();
                    GlobalClass.showAlertDialog("Your MPIN is locked due to maximum attempts. Request you to generate new MPIN.");
                }else {
                    GlobalClass.showAlertDialog("You have made "+PreferenceHandler.mpinRetryCount()+
                            " unsuccessful attempt(s). The maximum retry attempts allowed for login are 5."+
                            "\n To generate new MPIN, tap on \"Forgot MPIN\" option.");
                    PreferenceHandler.setMpinRetryCount(PreferenceHandler.mpinRetryCount()+1);
                }
                return false;
            } else{
                PreferenceHandler.setMpinRetryCount(1);
            }
        }else {
            if (!mClientID.getText().toString().trim().equalsIgnoreCase(UserSession.getLoginDetailsModel().getUserID())) {
                GlobalClass.showAlertDialog(eMessage.INVALID_CLIENTID.name);
                return false;
            } else if (TextUtils.isEmpty(mPassword.getText().toString().trim())) {
                GlobalClass.showAlertDialog(eMessage.BLANK_PASS.name);
                return false;
            } else if (TextUtils.isEmpty(pin.getText().toString())) {
                GlobalClass.showAlertDialog(eMessage.BLANK_PIN.name);
                return false;
            } else if (mDobRDbtn.isChecked() && TextUtils.isEmpty(mDOB.getText().toString())) {
                GlobalClass.showAlertDialog(eMessage.BLANK_DOB.name);
                return false;
            } else if (mPanRDbtn.isChecked() && TextUtils.isEmpty(mPAN.getText().toString())) {
                GlobalClass.showAlertDialog(eMessage.BLANK_PAN.name);
                return false;
            }
        }
        return true;
    }
    @Override
    public void serverNotAvailable() {
        GlobalClass.dismissdialog();

        if(serverConnect == eSocketClient.INTERACTIVE) {
            StructLoginFailure loginFailure = new StructLoginFailure();
            loginFailure.msg.setValue(Constants.ERR_MSG_TRADESERVER_CONNECTION);
            loginFailure.errorTag.setValue(eTagForErrorMsg.ITSCONNECTION_FAIL.value);
            enableLoginButton(loginFailure);
        }else {
            GlobalClass.showAlertDialog( Constants.ERR_MSG_TRADESERVER_CONNECTION);
        }
    }
    @Override
    public void connected() {
        if(serverConnect == eSocketClient.AUTH){
            new SendSecondIPReq().execute();
        }else {
            if (!UserSession.isTradeLogin()) {
                /*
                loginModel = new LoginModel();
                loginModel.password = mPassword.getText().toString().trim();
                loginModel.pin = pin.getText().toString().trim();
                short PDTag = 1;
                String pan_dob = mPAN.getText().toString().trim();
                if (mDobRDbtn.isChecked()) {
                    PDTag = 2;
                    pan_dob = mDOB.getText().toString().trim();
                }
                loginModel.pan_dob = pan_dob;
                loginModel.PDTag = PDTag;*/
                new sendTradeLogin().execute();
            }
        }
    }

    @Override
    public void onAuthSucceeded() {
        mLogin.setEnabled(false);
        this.update("Fingerprint Authentication succeeded.",true);
        connectToTradeServer();
    }

    @Override
    public void onAuthFailed() {
        this.update("Fingerprint Authentication failed.", false);
        fingerTouchimageView.setEnabled(true);
    }

    @Override
    public void onAuthError() {
        this.update("Fingerprint Authentication error.", false);
        fingerTouchimageView.setEnabled(true);
    }

    @Override
    public void onAuthHelp() {
        this.update("Fingerprint Authentication help.", false);
    }

    public void update(String e, Boolean success){
        try {
            if (fingerLayout.getVisibility() == View.VISIBLE) {
                textView.setText(e);
                if (success) {
                    //imageView.setBackgroundResource(R.drawable.right);
                    fingerTouchimageView.setImageResource(R.drawable.right);
                    textView.setTextColor(ContextCompat.getColor(getContext(), R.color.green1));
                } else {
                    textView.setTextColor(ContextCompat.getColor(getContext(), R.color.red));
                    //imageView.setBackgroundResource(R.drawable.wrong);
                    fingerTouchimageView.setImageResource(R.drawable.wrong);
                }
            }
        }catch (Exception ex){ex.printStackTrace();}
    }
    //int countT = 0;
    private void connectToTradeServer(){
        try {
        /*
            if(countT == 0){
                //ObjectHolder.connconfig.setTradeServerIP(serverIP.tradeServerDomainName.getValue());
                ObjectHolder.connconfig.setRcServerPort(1234);
                //UserSession.getClientResponse().setTradeServerIP(serverIP.serverIp.getValue());
                //UserSession.getClientResponse().setTradeDomainName(serverIP.tradeServerDomainName.getValue());
                UserSession.getClientResponse().setRCPort(1234);
            }
            countT++;*/
            GlobalClass.showProgressDialog("Connecting to Trade Server...");
            serverConnect = eSocketClient.INTERACTIVE;
            Connect.connect(GlobalClass.latestContext, this, eSocketClient.INTERACTIVE);
        }catch (Exception ex){
            ex.printStackTrace();
        }
    }
    private void connectToAuthServer(){
        try {
            GlobalClass.showProgressDialog("Connecting to Auth Server...");
            serverConnect = eSocketClient.AUTH;
            Connect.connect(getContext(), this, eSocketClient.AUTH);
        }catch (Exception ex){
            ex.printStackTrace();
        }
    }
    public static class LoginModel {
        public String password;
        public String pin;
        public String pan_dob;
        public short PDTag;
    }
    private LoginModel loginModel;

    private class sendTradeLogin extends AsyncTask<String, String, String> {

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            GlobalClass.showProgressDialog("Please wait...");
        }

        @Override
        protected String doInBackground(String... params) {
            try {
                ClientLoginReq_Pointer reqPointer = new ClientLoginReq_Pointer();
                reqPointer.clientCode.setValue(UserSession.getLoginDetailsModel().getUserID());
                reqPointer.iMEI.setValue(MobileInfo.getDeviceID(GlobalClass.latestContext));
                reqPointer.modelNo.setValue(Build.MODEL);
                reqPointer.versionNo.setValue(MobileInfo.getAppVersionCode());
                reqPointer.publicIP.setValue(MobileInfo.getIPAddress(true));
                String noncurrTime = "nonsso_"+DateUtil.getCurrentTimeStamp();
                reqPointer.sessionId.setValue(PreferenceHandler.getSSOSessionID().equalsIgnoreCase("")?noncurrTime:PreferenceHandler.getSSOSessionID());
                reqPointer.authToken.setValue(PreferenceHandler.getSSOAuthToken().equalsIgnoreCase("")?noncurrTime:PreferenceHandler.getSSOAuthToken());
                reqPointer.platform.setValue("pointer");

                GlobalClass.log("Trade login Req : " + reqPointer.toString());
                SendDataToRCServer sendDataToRCServer = new SendDataToRCServer();
                sendDataToRCServer.sendLoginRequest(reqPointer);
            } catch (Exception e) {
                GlobalClass.dismissdialog();
                e.printStackTrace();
            }
            return null;
        }
    }

    private class SendSecondIPReq extends AsyncTask<Void, Void, Void> {

        private SendDataToAuthServer sendDataToAuthServer = null;

        @Override
        protected Void doInBackground(Void... voids) {
            try {
                eMessageCode msgCode;
                byte[] data;

                msgCode = eMessageCode.GET_SERVER_SECONDIP;
                Config config = VenturaApplication.getPreference().getConnectionConfig();

                NewServerIPRequest loginRequest = new NewServerIPRequest();
                loginRequest.username.setValue(UserSession.getLoginDetailsModel().getUserID());
                loginRequest.versionNumber.setValue(MobileInfo.getAppVersionCode());
                loginRequest.osType.setValue(1);
                loginRequest.noOfTy.setValue(authServerConnectionCount);
                loginRequest.currentIP.setValue(UserSession.getClientResponse().getTradeServerIP());
                data = loginRequest.data.getByteArr((short) msgCode.value);
                sendDataToAuthServer = new SendDataToAuthServer(getContext(), null, msgCode, data);

            } catch (Exception e) {
                VenturaException.Print(e);
            }
            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);
            sendDataToAuthServer.execute();
        }
    }

    @Override
    public void sensexNiftyCame() {}

    private void handleLoginResponse(StructClientLoginIntraDelResp loginRes) {
        try {
            GlobalClass.log("TradeLogin : " + loginRes.toString());
            enableLoginButton(null);
            boolean isMarginTrade = false;
            if (loginRes.isMarginTrade.getValue() == 1) isMarginTrade = true;
            ObjectHolder.isMarginTrade = isMarginTrade;
            ObjectHolder.isCommodityAllow = (loginRes.isCommodityAllow.getValue()==1);
            ObjectHolder.isOCOAllow = loginRes.allowOCO.getValue();
            ObjectHolder.isMarginBF = loginRes.isMarginBF.getValue() == 1;
            ObjectHolder.isPOA = loginRes.isPOA.getValue() == 1;

            ((HomeActivity) getActivity()).addRemoveReportSpinner(isMarginTrade);
            if(loginModel != null && !loginModel.password.equalsIgnoreCase("")) {
                UserSession.getLoginDetailsModel().setPassword(loginModel.password);
            }
            UserSession.getLoginDetailsModel().setIntradayDelivery(loginRes.allowIntradayDelivery.getValue());
            UserSession.getLoginDetailsModel().setFNOIntradayDelivery(loginRes.allowFNOIntradayDelivery.getValue());

            /*
            int dateDiff = DateUtil.compareDates(loginRes.systemDateTime.getValue(), loginRes.lastModifiedTime.getValue());
            //int daysLeft = -1;//5 - dateDiff;
            int daysLeft = 89 - dateDiff; // 26022019... changes from 15 days to 90 days..
            if (loginRes.lastModifiedTime.getValue() == 0 || loginRes.isAdminReset.getValue() == 1) {
                GlobalClass.dismissdialog();
                String msg = (loginRes.isAdminReset.getValue() == 1)?"Your Password has been reset by Admin. Please Change your Password and Pin.":
                        "This is your first login. Please change your password.";
                new AlertBox(getContext(), "OK", "Cancel", msg, this, eConstant.PASS_CHNG.name);
            } else if (daysLeft <= 0) {
                GlobalClass.dismissdialog();
                String msg = "Your last password change was on " + DateUtil.dateFormatter(loginRes.lastModifiedTime.getValue(),Constants.DDMMYYHHMMSS)+"\n";
                msg = msg + "Your password has expired on " + DateUtil.dateFormatter(loginRes.lastModifiedTime.getValue() + ((60 * 60 * 24)*89),Constants.DDMMYYHHMMSS)+"\n";
                msg = msg + "You need to change your password before you can place order.";
                //new AlertBox(getContext(), "OK", "Cancel", "Your account password expired.", this, eConstant.PASS_CHNG.name);
                new AlertBox(getContext(), "OK",  msg, this, eConstant.PASS_CHNG.name);
            } else if ((daysLeft < 4)) {
                GlobalClass.dismissdialog();
                String msg = "Your last password change was on " + DateUtil.dateFormatter(loginRes.lastModifiedTime.getValue(),Constants.DDMMYYHHMMSS)+"\n";
                msg = msg + "Your password will expire at " + DateUtil.dateFormatter(loginRes.lastModifiedTime.getValue() + ((60 * 60 * 24)*89),Constants.DDMMYYHHMMSS)+"\n";
                msg = msg + "Please change your password before it expires.";
                //new AlertBox(getContext(), "OK", "Cancel", "Your account password will expire in " + daysLeft + " day(s).",this, eConstant.WARNING.name);
                new AlertBox(getContext(), "OK",  msg,this, eConstant.WARNING.name);
            } else {*/
                openNextScreenMeth();
            //}
        } catch (Exception ex) {
            VenturaException.Print(ex);
        }
    }

    private void openNextScreenMeth() {
        /*if (generateMpin.isChecked()){
            GlobalClass.showProgressDialog("Please wait...");
            StructPINGeneration spg = new StructPINGeneration();
            spg.clientCode.setValue(UserSession.getLoginDetailsModel().getUserID());
            spg.mobileNo.setValue(UserSession.getLoginDetailsModel().getMobileNo());
            spg.iMEI.setValue(MobileInfo.getDeviceID(GlobalClass.latestContext));
            SendDataToRCServer sendDataToRCServer = new SendDataToRCServer();
            sendDataToRCServer.sendMPINRequest(spg);
        }else*/
            new NextScreen(getActivity()).execute();
    }

    @Override
    public void onOk(String tag) {
        if (tag.equalsIgnoreCase(eConstant.WARNING.name)) {
            openNextScreenMeth();
        }
    }

    @Override
    public void onCancel(String tag) {

    }

    private class NextScreen extends AsyncTask<Void, Void, Void> {
        private Activity activity;

        public NextScreen(Activity activity) {
            this.activity = activity;
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            GlobalClass.showProgressDialog("Please wait...");
        }

        @Override
        protected Void doInBackground(Void... voids) {
            try {
                UserSession.setTradeLogin(true);
                //if(GlobalClass.mktDataHandler.getCurrSymbolList().size() <= 0) {
                new SendDataToBCServer().sendCurrSymbolList();
                //}
                /*
                tlm = VenturaApplication.getPreference().getTradeDetails();
                if (tlm != null){
                    tlm.setDayFirstLogin(false);
                    VenturaApplication.getPreference().setTradeDetails(tlm);

                    MPINModel mpinDetails = VenturaApplication.getPreference().getMPINDetails();
                    mpinDetails.addMPIN(UserSession.getLoginDetailsModel().getUserID(),tlm);
                    VenturaApplication.getPreference().setMPINDetails(mpinDetails);
                }*/
                //UserSession.getLoginDetailsModel().setUserID("99983103");
                GlobalClass.getClsTradeBook().sendTradeBookRequest();
                GlobalClass.getClsOrderBook().sendOrderBookRequest();
                if(UserSession.getClientResponse().isSLBMActivated() && (UserSession.getClientResponse().getServerType() == eServerType.ITS)) {
                    GlobalClass.getClsMarginHolding().sendHoldingRequest();
                    GlobalClass.getClsMarginHolding().sendSLBMHoldingRequest();
                }else if(UserSession.getClientResponse().isEDISActive()){
                    GlobalClass.getClsMarginHolding().sendHoldingRequest();
                }

                if(UserSession.getClientResponse().getServerType() == eServerType.RC){
                    GlobalClass.getClsCFDBook().sendCFDBookRequest();
                    if(ObjectHolder.isOCOAllow) {
                        GlobalClass.getClsBracketOrderBook().sendBracketOrderBookRequest();
                        GlobalClass.getClsBracketPositionBook().sendBracketPositionBookRequest();
                    }
                }
            } catch (Exception e) {
                VenturaException.Print(e);
            }
            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);
            activity.runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    GlobalClass.dismissdialog();

                    if (GlobalClass.homeActivity != null){//Always after Trade Login
                        HomeActivity homeActivity = (HomeActivity) GlobalClass.homeActivity;
                        homeActivity.RefreshNavmenus();
                    }
                    ((Activity) getActivity()).findViewById(R.id.exp_listview).setVisibility(View.VISIBLE);
                    ((RadioButton) getActivity().findViewById(R.id.tradeRDbutton)).setText("REPORTS");
                    switch (tradeFrom) {
                        case DASHBOARD:
                            GlobalClass.fragmentTransaction(new DashboardFragment(), R.id.container_body, false, "");
                            break;
                        case MKTDEPTH:
                            GlobalClass.fragmentManager.popBackStackImmediate();
                            break;
                        default:
                            String selectedTab = PreferenceHandler.getSelectedReport();
                            homeActivity.setSpinner(selectedTab,false);
                            GlobalClass.fragmentTransaction(ReportFragment.newInstance(), R.id.container_body, false, "");
                            break;
                    }
                }
            });

        }
    }

    private int _prevTextLength = 0;
    private int _currTextlength = 0;
    private TextWatcher watcher = new TextWatcher() {
        @Override
        public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            String str = mDOB.getText().toString();
            _prevTextLength = str.length();
        }

        @Override
        public void onTextChanged(CharSequence s, int start, int before, int count) {
            String str = mDOB.getText().toString();
            _currTextlength = str.length();
        }

        @Override
        public void afterTextChanged(Editable editable) {
            if (_prevTextLength < _currTextlength) {
                if (_currTextlength == 2) {
                    mDOB.append("/");
                } else if (_currTextlength == 3 && editable.charAt(2) != '/') {
                    String _tempText = editable.toString();
                    String addChar = "/" + _tempText.charAt(2);
                    String str = _tempText.substring(0, 2) + addChar;
                    mDOB.setText(str);
                    mDOB.setSelection(str.length());
                } else if (_currTextlength == 5) {
                    mDOB.append("/");
                } else if (_currTextlength == 6 && editable.charAt(5) != '/') {
                    String _tempText = editable.toString();
                    String addChar = "/" + _tempText.charAt(5);
                    String str = _tempText.substring(0, 5) + addChar;
                    mDOB.setText(str);
                    mDOB.setSelection(str.length());
                }
            }
        }
    };

    class TradeLoginHandler extends Handler {
        @Override
        public void handleMessage(Message msg) {
            try {
                Bundle refreshBundle = msg.getData();
                if (refreshBundle != null) {
                    try {
                        GlobalClass.dismissdialog();
                        int msgCode = refreshBundle.getInt(eForHandler.MSG_CODE.name);
                        eMessageCode emessagecode = eMessageCode.valueOf(msgCode);
                        switch (emessagecode) {
                            case LOGIN_RC:
                            case RESEARCH_FETCH_SSO_LOGIN:{
                                byte[] data = refreshBundle.getByteArray(eForHandler.RESDATA.name);
                                StructClientLoginIntraDelResp loginResponse = new StructClientLoginIntraDelResp(data);
                                handleLoginResponse(loginResponse);
                            }
                            break;
                            case LOGIN_RC_FAILURE:
                                StructLoginFailure structLoginFailure =  new StructLoginFailure(refreshBundle.getByteArray(eForHandler.RESDATA.name));
                                enableLoginButton(structLoginFailure);
                                break;
                            case GET_SERVER_SECONDIP:
                                NewServerIPResp serverIP =  new NewServerIPResp(refreshBundle.getByteArray(eForHandler.RESPONSE.name));
                                handleServerIP(serverIP);
                                break;
                            case MPIN_REQ:
                                byte[] data = refreshBundle.getByteArray(eForHandler.RESDATA.name);
                                StructPINGeneration spg = new StructPINGeneration(data);
                                saveLogingData(spg);
                                break;
                            default:
                                break;
                        }
                    } catch (Exception ex) {
                        ex.printStackTrace();
                    }
                }
                super.handleMessage(msg);
            } catch (Exception e) {
                VenturaException.Print(e);
            }
        }
    }
    private int authServerConnectionCount = 0;
    private void enableLoginButton(final  StructLoginFailure structLoginFailure){

        //structLoginFailure.errorTag.setValue(4);
        //structLoginFailure.msg.setValue("Too Many Attempts to login with invalid information. Account Locked");

        AppCompatActivity act = (AppCompatActivity) GlobalClass.latestContext;
        act.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                //mLogin.setEnabled(true);
                if (structLoginFailure != null) {

                    if(structLoginFailure.errorTag.getValue() == eTagForErrorMsg.ITSCONNECTION_FAIL.value){
                        //connect Auth Socket for different IP...
                        if(authServerConnectionCount < 3) {
                            authServerConnectionCount++;
                            connectToAuthServer();
                        }else{
                            new AlertBox(GlobalClass.latestContext, "", "OK", structLoginFailure.msg.getValue(), false);
                        }
                    }else {
                        if (structLoginFailure.msg.getValue().toLowerCase().contains("account locked")) {
                            String msgToShow = structLoginFailure.msg.getValue() + "\n\n" + "We request you kindly reset your password through forgot password.";
                            VenturaApplication.getPreference().setTradeDetails(null);
                            MPINModel mpinDetails = VenturaApplication.getPreference().getMPINDetails();
                            mpinDetails.clearMPIN(UserSession.getLoginDetailsModel().getUserID());
                            VenturaApplication.getPreference().setMPINDetails(mpinDetails);
                            new AlertBox(GlobalClass.latestContext, "Forgot Password", "Cancel", msgToShow, TradeFragment.this, eConstant.ACCLOCK.name);
                        } else {
                            //Toast.makeText(GlobalClass.latestContext,structLoginFailure.msg.getValue(),Toast.LENGTH_LONG);
                            new AlertBox(GlobalClass.latestContext, "", "OK", structLoginFailure.msg.getValue(), true);
                        }/*
                        if (mpinLinear.getVisibility() == View.VISIBLE && !structLoginFailure.msg.getValue().toLowerCase().contains("maintenance")) {
                            clearMpin();
                        }*/
                    }
                }
            }
        });

    }
    private void handleServerIP(NewServerIPResp serverIP){
        try{
            ObjectHolder.connconfig.setTradeServerIP(serverIP.tradeServerDomainName.getValue());
            ObjectHolder.connconfig.setRcServerPort(serverIP.portRC.getValue());
            VenturaApplication.getPreference().setConnectionConfig(ObjectHolder.connconfig);
            UserSession.getClientResponse().setTradeServerIP(serverIP.serverIp.getValue());
            UserSession.getClientResponse().setTradeDomainName(serverIP.tradeServerDomainName.getValue());
            UserSession.getClientResponse().setRCPort(serverIP.portRC.getValue());
            connectToTradeServer();
        }catch (Exception ex){
            ex.printStackTrace();
        }
    }
    private void saveLogingData(StructPINGeneration spg) {
        try{
            TradeLoginModel tradeLoginModel = new TradeLoginModel();
            tradeLoginModel.setChkType(loginModel.PDTag);
            tradeLoginModel.setDobPan(loginModel.pan_dob);
            tradeLoginModel.setMpin(spg.mPin.getValue());
            tradeLoginModel.setPassword(loginModel.password);
            tradeLoginModel.setPin(loginModel.pin);
            tradeLoginModel.setSaveDate(new Date());
            tradeLoginModel.setDayFirstLogin(false);
            VenturaApplication.getPreference().setTradeDetails(tradeLoginModel);
            MPINModel mpinDetails = VenturaApplication.getPreference().getMPINDetails();
            mpinDetails.addMPIN(UserSession.getLoginDetailsModel().getUserID(),tradeLoginModel);
            VenturaApplication.getPreference().setMPINDetails(mpinDetails);

            GlobalClass.dismissdialog();
            GlobalClass.showAlertDialog("Your 4 digits MPIN is "+spg.mPin.getValue()+" valid for 90 days.");
            new NextScreen(getActivity()).execute();
        }catch (Exception e){
            e.printStackTrace();
        }
    }
    public void forgotPasswordClicked(){
        UserSession.getLoginDetailsModel().setClient(true);
    }
}
