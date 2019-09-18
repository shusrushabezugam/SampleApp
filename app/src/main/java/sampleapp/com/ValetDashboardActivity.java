package sampleapp.com;
import sampleapp.com.CaptureActivity;
import android.content.Intent;
import android.icu.text.SimpleDateFormat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.Toast;

import java.text.ParseException;
import java.util.Date;
import java.util.Calendar;


public class ValetDashboardActivity extends AppCompatActivity implements View.OnClickListener {

    long date = System.currentTimeMillis();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_valet_dashboard);
        findViewById(R.id.numberplatescan).setOnClickListener(this);
        findViewById(R.id.logout).setOnClickListener(this);
        findViewById(R.id.nms).setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.numberplatescan:
                startActivity(new Intent(ValetDashboardActivity.this, CaptureActivity.class));
                break;
            case R.id.logout:
                startActivity(new Intent(ValetDashboardActivity.this, LogOutActivity.class));
                break;
            case R.id.nms:
               startActivity(new Intent(ValetDashboardActivity.this, DecodeActivity.class));
                break;
        }

    }

    public static long duration() throws ParseException {
CaptureActivity c=new CaptureActivity();
DecodeActivity d=new DecodeActivity();
        SimpleDateFormat format = new SimpleDateFormat("HH:mm:ss");
        Date date1 = format.parse(c.encodeTime());
        System.out.println(date1);
        Date date2 = format.parse(d.decodeTime());
        System.out.println(date2);
        long difference = date2.getTime() - date1.getTime();
        return difference;
    }

    protected static int payment() throws ParseException {
        int hours = (int) ((duration() / (1000*60)) % 60);
        int amount;
        if (hours >= 0 && hours < 2)
           return amount = 10;
        else if (hours >= 2)
            return amount = 20;
       return 15;
    }

}