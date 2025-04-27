package com.petplore.app;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.core.app.ActivityCompat;

import android.Manifest;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Build;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.util.Patterns;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.facebook.AccessToken;
import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.login.LoginManager;
import com.facebook.login.LoginResult;
import com.facebook.login.widget.LoginButton;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.SignInButton;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.FirebaseException;
import com.google.firebase.FirebaseTooManyRequestsException;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.EmailAuthProvider;
import com.google.firebase.auth.FacebookAuthProvider;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.GetTokenResult;
import com.google.firebase.auth.GoogleAuthProvider;
import com.google.firebase.auth.PhoneAuthCredential;
import com.google.firebase.auth.PhoneAuthProvider;

import java.util.concurrent.TimeUnit;

public class RegisterLoginActivity extends AppCompatActivity {

    FirebaseAuth firebaseAuth;
    ProgressDialog progressDialog;
    SignInButton googleSignInButton;
    GoogleSignInClient mGoogleSignInClient;

    // used for call back facebook
    CallbackManager mCallbackManager;

    String mVerificationId;
    EditText msgVerifyEditText;

    private final static int RC_SIGN_IN = 2;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login_register);

        // set firebase
        firebaseAuth = FirebaseAuth.getInstance();

        // PREPARE GOOGLE SIGN IN
        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(getString(R.string.default_web_client_id))
                .requestEmail()
                .build();

        // Build a GoogleSignInClient with the options specified by gso.
        mGoogleSignInClient = GoogleSignIn.getClient(this, gso);

        googleSignInButton = findViewById(R.id.googleButton);

        googleSignInButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                progressDialog = new ProgressDialog(RegisterLoginActivity.this);
                progressDialog.setMessage("Logging in...");
                progressDialog.show();

                googleSignIn();
            }
        });
        // END GOOGLE SIGN IN

        // START Facebook Login
        // Initialize Facebook Login button
        mCallbackManager = CallbackManager.Factory.create();
        LoginButton loginButton = findViewById(R.id.facebook_login_button);
        loginButton.setReadPermissions("email", "public_profile");
        loginButton.registerCallback(mCallbackManager, new FacebookCallback<LoginResult>() {
            @Override
            public void onSuccess(LoginResult loginResult) {
                Log.d("loginFacebook", "facebook:onSuccess:" + loginResult);
                handleFacebookAccessToken(loginResult.getAccessToken());
            }

            @Override
            public void onCancel() {
                Log.d("loginFacebook", "facebook:onCancel");
                // ...
            }

            @Override
            public void onError(FacebookException error) {
                Log.d("loginFacebook", "facebook:onError", error);
                // ...
            }
        });

        // End Facebook Login

        // Switch between login and register and phone
        final ConstraintLayout loginItemsConstraint = findViewById(R.id.loginItemsConstraint);
        final ConstraintLayout registerItemsConstraint = findViewById(R.id.registerItemsConstraint);
        final ConstraintLayout phoneNumberItemsConstraint = findViewById(R.id.phoneItemsConstraint);

        TextView getToRegisterPage = findViewById(R.id.getToRegister);
        TextView getToLoginPage = findViewById(R.id.getToLogin);
        TextView getToPhoneRegister = findViewById(R.id.phoneGetToRegister);

        final EditText loginNameInput = findViewById(R.id.loginTextInput);
        final EditText loginPassInput = findViewById(R.id.loginPassTextInput);

        final EditText registerNameInput = findViewById(R.id.registerUserTextInput);
        final EditText registerPassInput = findViewById(R.id.registerPasswordTextInput);

        final EditText loginPhoneNumberInput = findViewById(R.id.phoneLoginTextInput);

        getToRegisterPage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                // reset login inputs error
                loginNameInput.setError(null);
                loginPassInput.setError(null);

                // reset phone input errors
                loginPhoneNumberInput.setError(null);

                // get to register constraint
                loginItemsConstraint.setVisibility(View.GONE);
                phoneNumberItemsConstraint.setVisibility(View.GONE);
                registerItemsConstraint.setVisibility(View.VISIBLE);
            }
        });

        getToLoginPage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                // reset register inputs error
                registerNameInput.setError(null);
                registerPassInput.setError(null);

                // reset phone input errors
                loginPhoneNumberInput.setError(null);

                // get to login constraint
                registerItemsConstraint.setVisibility(View.GONE);
                phoneNumberItemsConstraint.setVisibility(View.GONE);
                loginItemsConstraint.setVisibility(View.VISIBLE);
            }
        });

        getToPhoneRegister.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                // reset register inputs error
                registerNameInput.setError(null);
                registerPassInput.setError(null);

                // reset login inputs error
                loginNameInput.setError(null);
                loginPassInput.setError(null);

                // get to phone constraint
                registerItemsConstraint.setVisibility(View.GONE);
                loginItemsConstraint.setVisibility(View.GONE);
                phoneNumberItemsConstraint.setVisibility(View.VISIBLE);


            }
        });

        // END Switch between login and register


        // START OF EMAIL LOGIN

        Button loginAcceptButton = findViewById(R.id.loginAcceptButton);
        loginAcceptButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (!loginNameInput.getText().toString().trim().isEmpty() && Patterns.EMAIL_ADDRESS.matcher(loginNameInput.getText().toString().trim()).matches() && !loginPassInput.getText().toString().trim().isEmpty()) {

                    progressDialog = new ProgressDialog(RegisterLoginActivity.this);
                    progressDialog.setMessage("Logging in...");
                    progressDialog.show();

                    final String userName = loginNameInput.getText().toString();

                    String password = loginPassInput.getText().toString();


                    firebaseAuth.signInWithEmailAndPassword(userName, password).addOnCompleteListener(RegisterLoginActivity.this, new OnCompleteListener<AuthResult>() {
                        @Override
                        public void onComplete(@NonNull Task<AuthResult> task) {
                            if (task.isSuccessful()) {

                                if (firebaseAuth.getCurrentUser().isEmailVerified()) {
                                    getToMainMenuAfterSignIn();
                                } else {
                                    verifyEmailByDialog().show();
                                }

                                progressDialog.dismiss();

                            } else {
                                // TODO LATER
                                //  login was not sucessful

                                String loginError;
                                loginError = task.getException().getMessage();
                                Toast.makeText(getApplicationContext(), loginError, Toast.LENGTH_LONG).show();
                                progressDialog.dismiss();
                            }
                        }
                    });


                } else if (loginNameInput.getText().toString().trim().isEmpty()) {
                    loginPassInput.setError(null);
                    loginNameInput.setError("Email required!");
                    loginNameInput.requestFocus();

                } else if (!Patterns.EMAIL_ADDRESS.matcher(loginNameInput.getText().toString().trim()).matches()) {

                    loginPassInput.setError(null);
                    loginNameInput.setError("Wrong email!");
                    loginNameInput.requestFocus();

                } else if (loginPassInput.getText().toString().trim().isEmpty()) {
                    loginNameInput.setError(null);
                    loginPassInput.setError("Enter your password!");
                    loginPassInput.requestFocus();
                }
            }
        });
        // END OF EMAIL LOGIN


        // START OF EMAIL REGISTER

        Button registerAcceptButton = findViewById(R.id.registerAcceptButton);

        registerAcceptButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if (!registerNameInput.getText().toString().trim().isEmpty() && !registerPassInput.getText().toString().trim().isEmpty() && Patterns.EMAIL_ADDRESS.matcher(registerNameInput.getText().toString().trim()).matches() && registerPassInput.getText().toString().trim().length() >= 8) {

                    progressDialog = new ProgressDialog(RegisterLoginActivity.this);
                    progressDialog.setMessage("registering");
                    progressDialog.show();

                    final String userName = registerNameInput.getText().toString();

                    final String password = registerPassInput.getText().toString();


                    firebaseAuth.createUserWithEmailAndPassword(userName, password).addOnCompleteListener(RegisterLoginActivity.this, new OnCompleteListener<AuthResult>() {
                        @Override
                        public void onComplete(@NonNull Task<AuthResult> task) {
                            if (task.isSuccessful()) {

                                firebaseAuth.getCurrentUser().sendEmailVerification()
                                        .addOnCompleteListener(new OnCompleteListener<Void>() {
                                            @Override
                                            public void onComplete(@NonNull Task<Void> task) {
                                                if (task.isSuccessful()) {
                                                    verifyEmailByDialog().show();
                                                }
                                            }
                                        });

                                registerItemsConstraint.setVisibility(View.INVISIBLE);
                                loginItemsConstraint.setVisibility(View.VISIBLE);

                                loginNameInput.setText(userName);
                                loginPassInput.setText(password);


                                progressDialog.dismiss();

                            } else {
                                // TODO LATER
                                //  Register was not sucessful and can't log in

                                String registerError;
                                registerError = task.getException().getMessage();
                                Toast.makeText(getApplicationContext(), registerError, Toast.LENGTH_LONG).show();
                                progressDialog.dismiss();
                            }
                        }
                    });


                } else if (registerNameInput.getText().toString().trim().isEmpty()) {
                    registerPassInput.setError(null);
                    registerNameInput.setError("Email required!");
                    registerNameInput.requestFocus();

                } else if (!Patterns.EMAIL_ADDRESS.matcher(registerNameInput.getText().toString().trim()).matches()) {

                    registerPassInput.setError(null);
                    registerNameInput.setError("Wrong email!");
                    registerNameInput.requestFocus();

                } else if (registerPassInput.getText().toString().trim().isEmpty() || registerPassInput.getText().toString().trim().length() < 8) {
                    registerNameInput.setError(null);
                    registerPassInput.setError("Password most be at least 8 character!");
                    registerPassInput.requestFocus();
                }

            }
        });

        // END OF EMAIL REGISTER

        // START OF PHONE LOGIN

        Button phoneLoginAcceptButton = findViewById(R.id.phoneLoginAcceptButton);

        phoneLoginAcceptButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                String phoneNumber = loginPhoneNumberInput.getText().toString().trim();

                if (Patterns.PHONE.matcher(phoneNumber).matches()) {

//                    TODO test msg dialog
                    AlertDialog.Builder msgVerifyDialogBuilder = new AlertDialog.Builder(RegisterLoginActivity.this);
                    View msgVerifyView = getLayoutInflater().inflate(R.layout.phone_login_dialog, null);

                    msgVerifyEditText = msgVerifyView.findViewById(R.id.msgVerifyEditText);
                    Button acceptVerifyButton = msgVerifyView.findViewById(R.id.msgVerificationButton);

                    acceptVerifyButton.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {

                            String code = msgVerifyEditText.getText().toString();

                            if (TextUtils.isEmpty(code)) {
                                msgVerifyEditText.setError("Cannot be empty.");
                                return;
                            } else {
                                verifyPhoneNumberWithCode(mVerificationId, code);
                            }
//                            TODO add function later
                        }
                    });

                    msgVerifyDialogBuilder.setView(msgVerifyView);

                    AlertDialog msgVerifyDialog = msgVerifyDialogBuilder.create();
                    msgVerifyDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
                    msgVerifyDialog.show();
//                    TODO test msg dialog

                    PhoneAuthProvider.getInstance().verifyPhoneNumber(
                            phoneNumber,        // Phone number to verify
                            60,                 // Timeout duration
                            TimeUnit.SECONDS,   // Unit of timeout
                            RegisterLoginActivity.this,               // Activity (for callback binding)
                            mCallbacks);        // OnVerificationStateChangedCallbacks

                    // TODO
                    //  add phone login here

                } else {

                    loginPhoneNumberInput.setError("please enter a valid phone number");

                }

            }
        });

//
//        // START Button verify code
//
//        Button phoneVerifyButton = findViewById(R.id.tempVerifyAcceptButton);
//        phoneVerifyButton.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//
//                String code = loginPhoneVerificationInput.getText().toString();
//
//                if (TextUtils.isEmpty(code)) {
//                    loginPhoneVerificationInput.setError("Cannot be empty.");
//                    return;
//                } else {
//                    verifyPhoneNumberWithCode(mVerificationId, code);
//                }
//            }
//        });
//        // END Button verify code


        // END OF PHONE LOGIN

        // get location access permissions
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (checkSelfPermission(Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && checkSelfPermission(Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {


                ActivityCompat.requestPermissions(RegisterLoginActivity.this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, 1);


                // TODO: Consider calling
                //    Activity#requestPermissions
                // here to request the missing permissions, and then overriding
                //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
                //                                          int[] grantResults)
                // to handle the case where the user grants the permission. See the documentation
                // for Activity#requestPermissions for more details.
                return;
            }
        }
    }


    // Phone login enter verification code START
    private void verifyPhoneNumberWithCode(String verificationId, String code) {
        // [START verify_with_code]
        PhoneAuthCredential credential = PhoneAuthProvider.getCredential(verificationId, code);
        // [END verify_with_code]
        signInWithPhoneAuthCredential(credential);
    }
    // Phone login enter verification code END


    // Phone Login Callbacks Start ()
    PhoneAuthProvider.OnVerificationStateChangedCallbacks mCallbacks = new PhoneAuthProvider.OnVerificationStateChangedCallbacks() {

        @Override
        public void onVerificationCompleted(PhoneAuthCredential credential) {
            // This callback will be invoked in two situations:
            // 1 - Instant verification. In some cases the phone number can be instantly
            //     verified without needing to send or enter a verification code.
            // 2 - Auto-retrieval. On some devices Google Play services can automatically
            //     detect the incoming verification SMS and perform verification without
            //     user action.
            Log.d("phoneVerify", "onVerificationCompleted:" + credential);

            signInWithPhoneAuthCredential(credential);
        }

        @Override
        public void onVerificationFailed(FirebaseException e) {
            // This callback is invoked in an invalid request for verification is made,
            // for instance if the the phone number format is not valid.
            Log.w("phoneVerify", "onVerificationFailed", e);

            if (e instanceof FirebaseAuthInvalidCredentialsException) {
                // Invalid request
                // ...
            } else if (e instanceof FirebaseTooManyRequestsException) {
                // The SMS quota for the project has been exceeded
                // ...
            }

            // Show a message and update the UI
            // ...
        }

        @Override
        public void onCodeSent(@NonNull String verificationId,
                               @NonNull PhoneAuthProvider.ForceResendingToken token) {
            // The SMS verification code has been sent to the provided phone number, we
            // now need to ask the user to enter the code and then construct a credential
            // by combining the code with a verification ID.
            Log.d("phoneVerify", "onCodeSent:" + verificationId);

            // Save verification ID and resending token so we can use them later

            mVerificationId = verificationId;
            PhoneAuthProvider.ForceResendingToken mResendToken = token;

            // ...
        }
    };

    // Phone Login Call backs End

    // [START sign_in_with_phone]
    private void signInWithPhoneAuthCredential(PhoneAuthCredential credential) {
        firebaseAuth.signInWithCredential(credential)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            // Sign in success, update UI with the signed-in user's information
                            Log.d("phoneVerify", "signInWithCredential:success");

                            FirebaseUser user = task.getResult().getUser();

                            getToMainMenuAfterSignIn();

                        } else {
                            // Sign in failed, display a message and update the UI
                            Log.w("phoneVerify", "signInWithCredential:failure", task.getException());
                            if (task.getException() instanceof FirebaseAuthInvalidCredentialsException) {
                                // The verification code entered was invalid
                                // [START_EXCLUDE silent]
                                msgVerifyEditText.setError("Invalid code.");
                                // [END_EXCLUDE]
                            }
                            // [START_EXCLUDE silent]
                            // Update UI
//                            TODO
                            //  after failed phone verification
//                            updateUI(STATE_SIGNIN_FAILED);
                            // [END_EXCLUDE]
                        }
                    }
                });
    }

    // [END sign_in_with_phone]


    // START Google Combined With Facebook Sign in
    private void googleSignIn() {
        Intent signInIntent = mGoogleSignInClient.getSignInIntent();
        startActivityForResult(signInIntent, RC_SIGN_IN);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        // Result returned from launching the Intent from GoogleSignInApi.getSignInIntent(...);
        if (requestCode == RC_SIGN_IN) {
            Task<GoogleSignInAccount> task = GoogleSignIn.getSignedInAccountFromIntent(data);
            try {
                // Google Sign In was successful, authenticate with Firebase
                GoogleSignInAccount account = task.getResult(ApiException.class);
                firebaseAuthWithGoogle(account);
            } catch (ApiException e) {
                // Google Sign In failed, update UI appropriately
                Log.w("googleSignin", "Google sign in failed", e);
                // ...
            }
        } else {
            // Facebook part
            // Pass the activity result back to the Facebook SDK
            mCallbackManager.onActivityResult(requestCode, resultCode, data);
        }
    }

    private void firebaseAuthWithGoogle(GoogleSignInAccount acct) {
        Log.d("googleSignin", "firebaseAuthWithGoogle:" + acct.getId());

        AuthCredential credential = GoogleAuthProvider.getCredential(acct.getIdToken(), null);
        firebaseAuth.signInWithCredential(credential)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            // Sign in success, update UI with the signed-in user's information
                            Log.d("googleSignin", "signInWithCredential:success");
                            FirebaseUser user = firebaseAuth.getCurrentUser();
                            Toast.makeText(RegisterLoginActivity.this, "Signed in.", Toast.LENGTH_SHORT).show();
                            getToMainMenuAfterSignIn();
                            progressDialog.dismiss();
                            verifyEmailByDialog().show();

                        } else {
                            // If sign in fails, display a message to the user.
                            Log.w("googleSignin", "signInWithCredential:failure", task.getException());
                            Toast.makeText(RegisterLoginActivity.this, "Authentication Failed.", Toast.LENGTH_SHORT).show();
                            progressDialog.dismiss();
                            // TODO
                            //  make failed instance
                        }

                        // ...
                    }
                });
    }
    // END Google Combined With Facebook Sign in

    // Begin Facebook Sign in

    private void handleFacebookAccessToken(AccessToken token) {
        Log.d("loginFacebook", "handleFacebookAccessToken:" + token);

        AuthCredential credential = FacebookAuthProvider.getCredential(token.getToken());
        firebaseAuth.signInWithCredential(credential)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            // Sign in success, update UI with the signed-in user's information
                            Log.d("loginFacebook", "signInWithCredential:success");
                            FirebaseUser user = firebaseAuth.getCurrentUser();
                            getToMainMenuAfterSignIn();
                        } else {
                            // If sign in fails, display a message to the user.

                            // logout from facebook
                            LoginManager.getInstance().logOut();
                            Log.w("loginFacebook", "signInWithCredential:failure", task.getException());
                            Toast.makeText(RegisterLoginActivity.this, "Authentication failed, email may have been used for google login before",
                                    Toast.LENGTH_SHORT).show();
                        }

                        // ...
                    }
                });
    }
    // End Facebook Sign in

    // Start Email Verify Dialog
    Dialog verifyEmailByDialog() {
        final AlertDialog.Builder emailVerifyDialog = new AlertDialog.Builder(RegisterLoginActivity.this);

        String tempEmailPlace = "verify email";
        if (firebaseAuth.getCurrentUser().getEmail() != null) {
            tempEmailPlace = firebaseAuth.getCurrentUser().getEmail();
        }

        emailVerifyDialog.setTitle(tempEmailPlace);
        emailVerifyDialog.setMessage("please check your email and verify");

        // set positive button function
        emailVerifyDialog.setPositiveButton("Done", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                if (firebaseAuth.getCurrentUser().isEmailVerified()) {
                    getToMainMenuAfterSignIn();
                }
                ;
            }
        });

        // set resend verification button
        emailVerifyDialog.setNeutralButton("resend email", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                firebaseAuth.getCurrentUser().sendEmailVerification();

                Toast.makeText(RegisterLoginActivity.this, "email have been sent, check yout inbox", Toast.LENGTH_SHORT);
                verifyEmailByDialog().show();
            }
        });

        return emailVerifyDialog.create();


    }
    // End Email Verify Dialog

    void getToMainMenuAfterSignIn() {

        Intent mainActivityIntent = new Intent(getApplicationContext(), PeopleActivity.class);

        mainActivityIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);

        startActivity(mainActivityIntent);


    }

    // TODO CHOICES for auto login
    @Override
    protected void onStart() {
        super.onStart();

        if (firebaseAuth.getCurrentUser() != null) {

            FirebaseAuth.getInstance().getCurrentUser().getIdToken(false).addOnCompleteListener(new OnCompleteListener<GetTokenResult>() {
                @Override
                public void onComplete(@NonNull Task<GetTokenResult> task) {
                    if (task.isSuccessful()) {
                        if(task.getResult().getSignInProvider().equals(EmailAuthProvider.EMAIL_PASSWORD_SIGN_IN_METHOD)) {
                            if (!firebaseAuth.getCurrentUser().isEmailVerified()) {
                                verifyEmailByDialog().show();
                            } else {
                                getToMainMenuAfterSignIn();
                            }
                        } else {
                            getToMainMenuAfterSignIn();
                        }
                    }
                }
            });

        }
    }
}
