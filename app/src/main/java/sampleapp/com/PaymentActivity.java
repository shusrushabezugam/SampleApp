package sampleapp.com;

import android.content.DialogInterface;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;


public class PaymentActivity extends AppCompatActivity {
private Button pay;
    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_payment);
        pay = (Button) findViewById(R.id.pay2);
        pay.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AlertDialog.Builder builder = new AlertDialog.Builder(
                        PaymentActivity.this);
                builder.setTitle("THANK YOU !!!!");
                builder.setMessage("Payment Sucessful");
                builder.setPositiveButton("OK",
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog,
                                                int which) {
                                Toast.makeText(getApplicationContext(), "Yes is clicked", Toast.LENGTH_LONG).show();
                            }
                        });
                builder.show();
            }
        });
    }
}
