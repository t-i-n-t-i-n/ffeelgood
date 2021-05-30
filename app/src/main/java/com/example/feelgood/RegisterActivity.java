package com.example.feelgood;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.HashMap;

public class RegisterActivity extends AppCompatActivity {

    EditText email, nickname, password;
    Button register;
    TextView txt_login;

    FirebaseAuth auth;
    DatabaseReference reference;
    ProgressDialog pd;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        email=findViewById(R.id.email);
        nickname=findViewById(R.id.nickname);
        password=findViewById(R.id.password);
        register=findViewById(R.id.register);
        txt_login=findViewById(R.id.txt_login);

        auth=FirebaseAuth.getInstance();
        txt_login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(RegisterActivity.this, LoginActivity.class));

            }
        });

        register.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                pd= new ProgressDialog(RegisterActivity.this);
                pd.setMessage("Please Wait...");
                pd.show();

                String str_nickname=nickname.getText().toString();
                String str_email=email.getText().toString();
                String str_password=password.getText().toString();
                if(TextUtils.isEmpty(str_nickname)||TextUtils.isEmpty(str_email)||TextUtils.isEmpty(str_password))
                    Toast.makeText(RegisterActivity.this,"Fill all fields", Toast.LENGTH_SHORT).show();
            }
        });
        private void register(String nickname,email,password){
            Task<AuthResult> authResultTask = auth.createUserWithEmailAndPassword(email, password)
                    .addOnCompleteListener(RegisterActivity.this, new OnCompleteListener<AuthResult>() {
                        @Override
                        public void onComplete(@NonNull @org.jetbrains.annotations.NotNull Task<AuthResult> task) {
                            if (task.isSuccessful()) {
                                FirebaseUser firebaseUser = auth.getCurrentUser();
                                String userid = firebaseUser.getUid();

                                reference = FirebaseDatabase.getInstance().getReference().child("Users").child(userid);

                                HashMap<String, Object> hashMap = new hashMap<>();
                                hashMap.put("id", userid);
                                hashMap.put("nickname", nickname);

                                reference.setValue(hashMap).addOnCompleteListener(@NonNull Task < Void > task)
                                {
                                    if (task.isSuccessful()) {
                                        pd.dismiss();
                                        Intent intent = new Intent(RegisterActivity.this, MainActivity.class);
                                        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
                                        startActivity(intent);
                                    } else {
                                        pd.dismiss();
                                        Toast.makeText(RegisterActivity.this, "use another email/password", Toast.LENGTH_SHORT).show();
                                    }
                                }


                            }
                        })

                    };
        }

    }
}