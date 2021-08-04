package com.example.bookurbook.activities;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.example.bookurbook.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;

public class ForgottenPasswordActivity extends AppCompatActivity {

    private EditText email;
    private Button reset;
    private FirebaseAuth auth;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_forgotten_password);
        init();
    }
    public void init()
    {
        email = findViewById(R.id.editemail3);
        reset = findViewById(R.id.sendresetemail);
        auth = FirebaseAuth.getInstance();
        reset.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(TextUtils.isEmpty(email.getText().toString()))
                {
                    Toast noEmail = Toast.makeText(ForgottenPasswordActivity.this,"Email field is empty", Toast.LENGTH_LONG);
                    noEmail.show();
                }
                else if(!(email.getText().toString().contains("@")))
                {
                    Toast notEmail = Toast.makeText(ForgottenPasswordActivity.this, "Wrong email",  Toast.LENGTH_LONG);
                    notEmail.show();
                }
                else if(!(email.getText().toString().substring(email.getText().toString().indexOf("@"), email.getText().toString().length()).contains(".edu.tr")))
                {
                    Toast notEdu = Toast.makeText(ForgottenPasswordActivity.this, "This is not a edu.tr mail",  Toast.LENGTH_LONG);
                    notEdu.show();
                }
                else
                {
                    auth.sendPasswordResetEmail(email.getText().toString()).addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            if(task.isSuccessful())
                            {
                              Toast done = Toast.makeText(ForgottenPasswordActivity.this, "Password reset link has been sent to your email.", Toast.LENGTH_SHORT);
                                new CountDownTimer(3000, 1000)
                                {
                                    public void onTick(long millisUntilFinished){}
                                    public void onFinish()
                                    {
                                        startActivity(new Intent(ForgottenPasswordActivity.this, LoginActivity.class));
                                    }
                                } .start();
                            }
                            else
                            {
                                Toast fail = Toast.makeText(ForgottenPasswordActivity.this, "Something is wrong. Check your internet connectin.", Toast.LENGTH_LONG);
                            }
                        }
                    });
                }
            }
        });
    }
}