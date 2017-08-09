package com.utilfreedom.brainmath;


import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.facebook.CallbackManager;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;

import java.util.HashMap;

public class SignUpFragment extends Fragment {
    private static final String TAG = "GL: ";

    private CallbackManager mCallbackManager;
    private FirebaseAuth mAuth;

    EditText usernameET;
    EditText emailET;
    EditText passwordET;
    Button signUpBtn;

    public SignUpFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        //firebase
        mCallbackManager = CallbackManager.Factory.create();
        mAuth = FirebaseAuth.getInstance();

        // Inflate the layout for this fragment
        View v = inflater.inflate(R.layout.fragment_sign_up, container, false);

        usernameET = (EditText) v.findViewById(R.id.usernameET);
        emailET = (EditText) v.findViewById(R.id.emailET);
        passwordET = (EditText) v.findViewById(R.id.passwordET);
        signUpBtn = (Button) v.findViewById(R.id.signUpBtn);
        signUpBtn.setOnClickListener(new SignUpBtnPressed());

        return v;
    }

    private class SignUpBtnPressed implements View.OnClickListener {

        @Override
        public void onClick(View view) {

            final String username = usernameET.getText().toString();
            final String email = emailET.getText().toString();
            final String password = passwordET.getText().toString();

            mAuth.createUserWithEmailAndPassword(email, password)
                    .addOnCompleteListener(getActivity(), new OnCompleteListener<AuthResult>() {
                        @Override
                        public void onComplete(@NonNull Task<AuthResult> task) {
                            if (task.isSuccessful()) {
                                // Sign in success, update UI with the signed-in user's information
                                final FirebaseUser user = mAuth.getCurrentUser();
                                HashMap<String, Object> map = new HashMap<>();
                                map.put("email", user.getEmail());
                                map.put("username", username);

                                Config.DB_BASE.child("users")
                                        .child(user.getUid())
                                        .updateChildren(map, new DatabaseReference.CompletionListener() {
                                            @Override
                                            public void onComplete(DatabaseError databaseError, DatabaseReference databaseReference) {
                                                if (databaseError != null) {
                                                    System.out.println(databaseError);
                                                    return;
                                                }
                                            }
                                        });
                                //after register, redirect to signIn
                                mAuth.signInWithEmailAndPassword(email, password)
                                        .addOnSuccessListener(new handleSignInSuccess());
                            } else {
                                // If sign in fails, display a message to the user.
                                Toast.makeText(getActivity(), "Email already used, please try again.",
                                        Toast.LENGTH_SHORT).show();
                            }
                        }
                    });
        }
    }


    private class handleSignInSuccess implements com.google.android.gms.tasks.OnSuccessListener<AuthResult> {

        @Override
        public void onSuccess(AuthResult authResult) {
            System.out.println("GL: handleSignInSuccess");
            startMultiplayerMenuFragment();
        }
    }



    private void startMultiplayerMenuFragment() {
        FragmentManager fm = getFragmentManager();
        fm.beginTransaction()
        .replace(R.id.mainLayout, new MultiplayerMenuFragment())
        .commit();
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        //pass the activity result back to the facebook SDK
//        mCallbackManager.onActivityResult(requestCode, resultCode, data);
    }
}

