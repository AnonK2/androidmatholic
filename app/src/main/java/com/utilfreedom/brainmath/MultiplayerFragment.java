package com.utilfreedom.brainmath;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.facebook.CallbackManager;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class MultiplayerFragment extends Fragment {
    private static final String TAG = "GL: ";
    Boolean isLogin = false;

    private CallbackManager mCallbackManager;
    private FirebaseAuth mAuth;

//    EditText usernameET;
    EditText emailET;
    EditText passwordET;
    Button loginBtn;
    Button signUpBtn;

    public MultiplayerFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        //firebase
        mCallbackManager = CallbackManager.Factory.create();
        mAuth = FirebaseAuth.getInstance();

        // Inflate the layout for this fragment
        View v = inflater.inflate(R.layout.fragment_multiplayer, container, false);

//        usernameET = (EditText) v.findViewById(R.id.usernameET);
        emailET = (EditText) v.findViewById(R.id.emailET);
        passwordET = (EditText) v.findViewById(R.id.passwordET);
        loginBtn = (Button) v.findViewById(R.id.loginBtn);
        loginBtn.setOnClickListener(new LoginBtnPressed());

        signUpBtn = (Button) v.findViewById(R.id.signUpBtn);
        signUpBtn.setOnClickListener(new SignUpBtnPressed());

        return v;
    }

    private class SignUpBtnPressed implements View.OnClickListener {
        @Override
        public void onClick(View view) {
            FragmentManager fm = getFragmentManager();
            fm.beginTransaction()
            .addToBackStack(null)
            .replace(R.id.mainLayout, new SignUpFragment())
            .setTransition(FragmentTransaction.TRANSIT_NONE)
            .commit();
        }
    }

    private class LoginBtnPressed implements View.OnClickListener {

        @Override
        public void onClick(View view) {
            if (isLogin) {
                mAuth.signOut();
                buttonTextAndLoginStatusWithText("Login", false);
            } else {
//                String username = usernameET.getText().toString();
                String email = emailET.getText().toString();
                String password = passwordET.getText().toString();

                Task<AuthResult> task =  mAuth.signInWithEmailAndPassword(email, password);
                task.addOnSuccessListener(new handleSignInSuccess()); // <- signIn successful
                task.addOnFailureListener(new handleSignInFailed()); // <- signIn failed
//                task.addOnFailureListener(new handleSignInFailed(username, email, password)); // <- signIn failed
            }

        }

        private class handleSignInFailed implements OnFailureListener {
            String username, email, password;

            public handleSignInFailed() {}

//            public handleSignInFailed(String username, String email, String password) {
//                this.username = username;
//                this.email = email;
//                this.password = password;
//            }

            @Override
            public void onFailure(@NonNull Exception e) {
                Toast.makeText(getActivity(), "Email/Password incorrect, please try again.",
                        Toast.LENGTH_SHORT).show();



//                mAuth.createUserWithEmailAndPassword(email, password)
//                        .addOnCompleteListener(getActivity(), new OnCompleteListener<AuthResult>() {
//                            @Override
//                            public void onComplete(@NonNull Task<AuthResult> task) {
//                                if (task.isSuccessful()) {
//                                    // Sign in success, update UI with the signed-in user's information
//                                    final FirebaseUser user = mAuth.getCurrentUser();
//                                    HashMap<String, Object> map = new HashMap<>();
//                                    map.put("email", user.getEmail());
//                                    map.put("username", username);
//
//                                    Config.DB_BASE.child("users")
//                                            .child(user.getUid())
//                                            .updateChildren(map, new DatabaseReference.CompletionListener() {
//                                                @Override
//                                                public void onComplete(DatabaseError databaseError, DatabaseReference databaseReference) {
//                                                    if (databaseError != null) {
//                                                        System.out.println(databaseError);
//                                                        return;
//                                                    }
//                                                    updateUI(user);
//                                                }
//                                            });
//                                    //after register, redirect to signIn
//                                    mAuth.signInWithEmailAndPassword(email, password)
//                                            .addOnSuccessListener(new handleSignInSuccess());
//                                } else {
//                                    // If sign in fails, display a message to the user.
//                                    Toast.makeText(getActivity(), "Email/Password incorrect, please try again.",
//                                            Toast.LENGTH_SHORT).show();
//                                    updateUI(null);
//                                }
//                            }
//                        });
            }
        }

        private class handleSignInSuccess implements com.google.android.gms.tasks.OnSuccessListener<AuthResult> {

            @Override
            public void onSuccess(AuthResult authResult) {
                System.out.println("GL: handleSignInSuccess");
                updateUI(authResult.getUser());
                startMultiplayerMenuFragment();
            }
        }
    }

    @Override
    public void onStart() {
        super.onStart();
        // Check if user is signed in (non-null) and update UI accordingly.
        FirebaseUser currentUser = mAuth.getCurrentUser();
        updateUI(currentUser);
    }

    private void updateUI(FirebaseUser currentUser) {
        if (currentUser != null) {
//            System.out.println("GL: updateUI called");
//            buttonTextAndLoginStatusWithText("LogOut", true);
            startMultiplayerMenuFragment();
        }
    }

    private void startMultiplayerMenuFragment() {
        FragmentManager fm = getFragmentManager();
        fm.beginTransaction()
        .replace(R.id.mainLayout, new MultiplayerMenuFragment())
        .commit();
    }

    private void buttonTextAndLoginStatusWithText(String text, boolean isLogin) {
        this.loginBtn.setText(text);
        this.isLogin = isLogin;
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        //pass the activity result back to the facebook SDK
//        mCallbackManager.onActivityResult(requestCode, resultCode, data);
    }
}
