package com.smazee.product.pedaleze;

import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.constraint.ConstraintLayout;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.FirebaseException;
import com.google.firebase.FirebaseTooManyRequestsException;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.PhoneAuthCredential;
import com.google.firebase.auth.PhoneAuthProvider;
import com.msg91.sendotp.library.SendOtpVerification;
import com.msg91.sendotp.library.Verification;
import com.msg91.sendotp.library.VerificationListener;

import java.util.concurrent.TimeUnit;

public class LoginActivity extends AppCompatActivity implements VerificationListener {

    private PrefManager prefManager;
    private boolean mVerificationInProgress = false;
    private String mVerificationId;
    private PhoneAuthProvider.ForceResendingToken mResendToken;
    private PhoneAuthProvider.OnVerificationStateChangedCallbacks mCallbacks;
    private FirebaseAuth mAuth;
    ConstraintLayout otpLayout;
    Verification mVerification;
    String TAG="LoginActivity--->";
    EditText otpTxt,phoneTxt;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mAuth = FirebaseAuth.getInstance();
        setContentView(R.layout.activity_login);

        otpLayout = findViewById(R.id.login_otp);
        otpLayout.setVisibility(View.INVISIBLE);
        otpTxt = findViewById(R.id.otpText);
        phoneTxt = findViewById(R.id.phoneText);

        prefManager = new PrefManager(this);

        mCallbacks = new PhoneAuthProvider.OnVerificationStateChangedCallbacks() {

            @Override
            public void onVerificationCompleted(PhoneAuthCredential credential) {
                // This callback will be invoked in two situations:
                // 1 - Instant verification. In some cases the phone number can be instantly
                //     verified without needing to send or enter a verification code.
                // 2 - Auto-retrieval. On some devices Google Play services can automatically
                //     detect the incoming verification SMS and perform verification without
                //     user action.
                Log.d(TAG, "onVerificationCompleted:" + credential);
                mVerificationInProgress=false;

                signInWithPhoneAuthCredential(credential);
            }

            @Override
            public void onVerificationFailed(FirebaseException e) {
                // This callback is invoked in an invalid request for verification is made,
                // for instance if the the phone number format is not valid.
                Log.w(TAG, "onVerificationFailed", e);
                mVerificationInProgress=false;

                if (e instanceof FirebaseAuthInvalidCredentialsException) {
                    // Invalid request
                    // ...
                    Log.d(TAG,"Invalid Credential");
                } else if (e instanceof FirebaseTooManyRequestsException) {
                    // The SMS quota for the project has been exceeded
                    // ...
                    Log.d(TAG,"SMS quota exceeded");
                }

                // Show a message and update the UI
                // ...
            }

            @Override
            public void onCodeSent(String verificationId,
                                   PhoneAuthProvider.ForceResendingToken token) {
                // The SMS verification code has been sent to the provided phone number, we
                // now need to ask the user to enter the code and then construct a credential
                // by combining the code with a verification ID.
                Log.d(TAG, "onCodeSent:" + verificationId);

                // Save verification ID and resending token so we can use them later
                mVerificationId = verificationId;
                mResendToken = token;

                // ...
            }
        };
    }

    @Override
    protected void onStart() {
        super.onStart();

        if (mVerificationInProgress && validatePhoneNumber()) {
            startPhoneNumberVerification(phoneTxt.getText().toString());
        }
    }

    private void startPhoneNumberVerification(String phoneNumber) {
        // [START start_phone_auth]
        PhoneAuthProvider.getInstance().verifyPhoneNumber(
                phoneNumber,        // Phone number to verify
                60,                 // Timeout duration
                TimeUnit.SECONDS,   // Unit of timeout
                this,               // Activity (for callback binding)
                mCallbacks);        // OnVerificationStateChangedCallbacks
        // [END start_phone_auth]

        mVerificationInProgress = true;
    }

    private void verifyPhoneNumberWithCode(String verificationId, String code) {
        // [START verify_with_code]
        PhoneAuthCredential credential = PhoneAuthProvider.getCredential(verificationId, code);
        // [END verify_with_code]
        signInWithPhoneAuthCredential(credential);
    }

    private void signInWithPhoneAuthCredential(PhoneAuthCredential credential) {
        mAuth.signInWithCredential(credential)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            // Sign in success, update UI with the signed-in user's information
                            Log.d(TAG, "signInWithCredential:success");

                            FirebaseUser user = task.getResult().getUser();
                            Toast.makeText(LoginActivity.this,user.getPhoneNumber()+user.getDisplayName(),Toast.LENGTH_SHORT).show();
                            // [START_EXCLUDE]
                            //updateUI(STATE_SIGNIN_SUCCESS, user);
                            prefManager.setFirstTimeLogin(false);
                            Intent toDetails = new Intent(LoginActivity.this,DetailsActivity.class);
                            startActivity(toDetails);

                            // [END_EXCLUDE]
                        } else {
                            // Sign in failed, display a message and update the UI
                            Log.w(TAG, "signInWithCredential:failure", task.getException());
                            if (task.getException() instanceof FirebaseAuthInvalidCredentialsException) {
                                // The verification code entered was invalid
                                // [START_EXCLUDE silent]
                                otpTxt.setError("Invalid code.");
                                // [END_EXCLUDE]
                            }
                            // [START_EXCLUDE silent]
                            // Update UI
                            //updateUI(STATE_SIGNIN_FAILED);
                            // [END_EXCLUDE]
                        }
                    }
                });
    }

    private boolean validatePhoneNumber() {
        String phoneNumber = phoneTxt.getText().toString();
        if (TextUtils.isEmpty(phoneNumber)) {
            phoneTxt.setError("Invalid phone number.");
            return false;
        }

        return true;
    }

    public void checkOtp(View view){
        //String otp = otpTxt.getText().toString();
        //Toast.makeText(this,otp,Toast.LENGTH_SHORT).show();

        String code = otpTxt.getText().toString();
        if (TextUtils.isEmpty(code)) {
            otpTxt.setError("Cannot be empty.");
            return;
        }
if(mVerification != null){
    mVerification.verify(code);
//    prefManager.setFirstTimeLogin(false);
//    Intent toDetails = new Intent(LoginActivity.this,DetailsActivity.class);
//    startActivity(toDetails);
}
//        verifyPhoneNumberWithCode(mVerificationId, code); Old Code Firebase
    }

    public void verifyNumber(View view){
        if (!validatePhoneNumber()) {
            return;
        }
//        startPhoneNumberVerification("+91"+phoneTxt.getText().toString()); Old Colde Firebase

        String phoneNumber = phoneTxt.getText().toString();
        mVerification = SendOtpVerification.createSmsVerification
                (SendOtpVerification
                        .config("91" + phoneNumber)
                        .context(this)
                        .senderId("PEDEZE")
                        .autoVerification(true)
                        .build(), this);
        mVerification.initiate();
        otpLayout.setVisibility(View.VISIBLE);

    }

    @Override
    public void onInitiated(String response) {
        Log.d(TAG, "Initialized!" + response);
        //OTP successfully resent/sent.
    }

    @Override
    public void onInitiationFailed(Exception paramException) {
        Log.e(TAG, "Verification initialization failed: " + paramException.getMessage());
        //sending otp failed.
    }

    @Override
    public void onVerified(String response) {
        Log.d(TAG, "Verified!\n" + response);
        //OTP verified successfully.
        prefManager.setFirstTimeLogin(false);
        Intent toDetails = new Intent(LoginActivity.this,DetailsActivity.class);
        startActivity(toDetails);
    }

    @Override
    public void onVerificationFailed(Exception paramException) {
        Log.e(TAG, "Verification failed: " + paramException.getMessage());
        //OTP  verification failed.
    }
}
