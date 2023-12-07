package com.ventura.venturawealth;

public interface FingerprintInterface {
    void onAuthSucceeded();
    void onAuthFailed();
    void onAuthHelp();
    void onAuthError();
}
