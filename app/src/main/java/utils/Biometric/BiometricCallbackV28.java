package utils.Biometric;

import android.hardware.biometrics.BiometricPrompt;
import android.os.Build;
import androidx.annotation.RequiresApi;
import com.ventura.venturawealth.FingerprintInterfaceforpopup;


@RequiresApi(api = Build.VERSION_CODES.P)
public class BiometricCallbackV28 extends BiometricPrompt.AuthenticationCallback {

    private FingerprintInterfaceforpopup biometricCallback;
    public BiometricCallbackV28(FingerprintInterfaceforpopup biometricCallback) {
        this.biometricCallback = biometricCallback;
    }

    @Override
    public void onAuthenticationSucceeded(BiometricPrompt.AuthenticationResult result) {
        super.onAuthenticationSucceeded(result);
        biometricCallback.onAuthSucceeded();
    }

    @Override
    public void onAuthenticationHelp(int helpCode, CharSequence helpString) {
        super.onAuthenticationHelp(helpCode, helpString);
        biometricCallback.onAuthHelp();
    }

    @Override
    public void onAuthenticationError(int errorCode, CharSequence errString) {
        super.onAuthenticationError(errorCode, errString);
        biometricCallback.onAuthError();
    }


    @Override
    public void onAuthenticationFailed() {
        super.onAuthenticationFailed();
        biometricCallback.onAuthFailed();
    }
}
