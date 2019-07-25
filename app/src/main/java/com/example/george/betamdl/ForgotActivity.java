package com.example.george.betamdl;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.telephony.SmsManager;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

public class ForgotActivity extends AppCompatActivity {

    private EditText message;
    private Button submit;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_forgot);

        submit = findViewById(R.id.submit);
        message = findViewById(R.id.message);


        submit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                //On récupère ce qui a été entré dans les EditText
                String NewMessage = message.getText().toString();

                SmsManager.getDefault().sendTextMessage("+330660483560", null, NewMessage, null, null);

                Toast.makeText(ForgotActivity.this, "Envoyé !", Toast.LENGTH_SHORT).show();

            }
        });


    }
}
