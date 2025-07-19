package com.example.funtravelapp;

import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.fragment.app.Fragment;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class SignUpTabFragment extends Fragment {
    private EditText emailField, passwordField, confirmField;
    private Button signupBtn;
    private FirebaseAuth mAuth;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_sign_up_tab, container, false);

        mAuth = FirebaseAuth.getInstance();

        emailField = view.findViewById(R.id.signup_email);
        passwordField = view.findViewById(R.id.signup_password);
        confirmField = view.findViewById(R.id.signup_confirm);
        signupBtn = view.findViewById(R.id.signup_button);

        signupBtn.setOnClickListener(v -> registerUser());

        return view;
    }

    private void registerUser() {
        String email = emailField.getText().toString().trim();
        String password = passwordField.getText().toString().trim();
        String confirm = confirmField.getText().toString().trim();

        if (TextUtils.isEmpty(email) || TextUtils.isEmpty(password)) {
            Toast.makeText(getActivity(), "Email and password required", Toast.LENGTH_SHORT).show();
            return;
        }
        if (!password.equals(confirm)) {
            Toast.makeText(getActivity(), "Passwords do not match", Toast.LENGTH_SHORT).show();
            return;
        }

        mAuth.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        FirebaseUser user = mAuth.getCurrentUser();
                        Toast.makeText(getActivity(), "Registration successful", Toast.LENGTH_SHORT).show();

                        // 🔁 Move to LoginTabFragment (tab index 0)
                        if (getActivity() instanceof MainActivity) {
                            MainActivity mainActivity = (MainActivity) getActivity();
                            mainActivity.switchToLoginTab();
                        }
                    } else {
                        Toast.makeText(getActivity(), "Failed: " + task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                    }
                });
    }
}
