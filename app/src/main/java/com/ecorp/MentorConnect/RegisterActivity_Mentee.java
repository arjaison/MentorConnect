package com.ecorp.MentorConnect;

import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Typeface;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.RequiresApi;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
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
import com.google.firebase.auth.UserProfileChangeRequest;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import java.nio.charset.Charset;
import java.security.NoSuchAlgorithmException;
import java.util.HashMap;



public class RegisterActivity_Mentee extends AppCompatActivity {

    EditText email, password,mentname;
    TextView register_tv, msg_reg_tv;
    Button btn_register;
    Typeface MR, MRR;
    FirebaseAuth auth;
    DatabaseReference reference;
    ProgressDialog dialog;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register_mentee);

        MRR = Typeface.createFromAsset(getAssets(), "fonts/myriadregular.ttf");
        MR = Typeface.createFromAsset(getAssets(), "fonts/myriad.ttf");

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle("Register");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        mentname=findViewById(R.id.mentname);
        email = findViewById(R.id.email);
        password = findViewById(R.id.password);
        btn_register = findViewById(R.id.btn_register);
        register_tv = findViewById(R.id.register_tv);
        msg_reg_tv = findViewById(R.id.msg_reg_tv);


        msg_reg_tv.setTypeface(MRR);
        mentname.setTypeface(MRR);
        email.setTypeface(MRR);
        password.setTypeface(MRR);
        btn_register.setTypeface(MR);
        register_tv.setTypeface(MR);

        auth = FirebaseAuth.getInstance();

        btn_register.setOnClickListener(new View.OnClickListener() {
            @RequiresApi(api = Build.VERSION_CODES.KITKAT)
            @Override
            public void onClick(View view) {

                String txt_email = email.getText().toString();
               String txt_mentname=mentname.getText().toString();
                String txt_password = password.getText().toString();
                String txt_username = null;

                //Hash.toHexString(Hash.getSHA(txt_password))+
                try {
                    txt_username = Hash.toHexString(Hash.getSHA(new String(new byte[7], Charset.forName("UTF-8"))));
                } catch (NoSuchAlgorithmException e) {
                    e.printStackTrace();
                }

                Utils.hideKeyboard(RegisterActivity_Mentee.this);
                if (txt_email.matches("[0-9]+@sastra.ac.in"))
                {
                    if (TextUtils.isEmpty(txt_email) || TextUtils.isEmpty(txt_password)) {
                        Toast.makeText(RegisterActivity_Mentee.this, "All fields are required", Toast.LENGTH_SHORT).show();
                    } else if (txt_password.length() < 6) {
                        Toast.makeText(RegisterActivity_Mentee.this, "Password must be at least 6 characters", Toast.LENGTH_SHORT).show();
                    } else {

                        register1(txt_username, txt_email, txt_password,txt_mentname);

                    }
            }
                else
                    Toast.makeText(RegisterActivity_Mentee.this, "Enter valid Sastra Mail ID", Toast.LENGTH_SHORT).show();

            }
        });
    }




    private void register1(final String username, String email, String password, final String mentorname){

        dialog = Utils.showLoader(RegisterActivity_Mentee.this);

        auth.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()){
                            FirebaseUser firebaseUser = auth.getCurrentUser();
                            assert firebaseUser != null;
                            String userid = firebaseUser.getUid();





                            reference = FirebaseDatabase.getInstance().getReference("Users").child(userid);

                            HashMap<String, String> hashMap = new HashMap<>();
                            hashMap.put("id", userid);
                            hashMap.put("username", username);
                            hashMap.put("imageURL", "default");
                            hashMap.put("status", "offline");
                            hashMap.put("bio", mentorname);
                            hashMap.put("search", username.toLowerCase());
                            if(dialog!=null){
                                dialog.dismiss();
                            }
                            UserProfileChangeRequest profileUpdates = new UserProfileChangeRequest.Builder()
                                    .setDisplayName(mentorname).build();
                            firebaseUser.updateProfile(profileUpdates);
                            reference.setValue(hashMap).addOnCompleteListener(new OnCompleteListener<Void>() {
                                @Override
                                public void onComplete(@NonNull Task<Void> task) {
                                    if (task.isSuccessful()){
                                        Intent intent = new Intent(RegisterActivity_Mentee.this, LoginActivity.class);
                                        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
                                        startActivity(intent);
                                        finish();
                                    }
                                }
                            });
                            //edited

                        }

                        else {
                            Toast.makeText(RegisterActivity_Mentee.this, "You can't register with this Email", Toast.LENGTH_SHORT).show();
                            if(dialog!=null){
                                dialog.dismiss();
                            }
                        }
                    }
                });
    }
}
