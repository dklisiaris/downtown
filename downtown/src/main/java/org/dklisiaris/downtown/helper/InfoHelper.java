package org.dklisiaris.downtown.helper;

import org.dklisiaris.downtown.R;

import java.util.ArrayList;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.Typeface;
import android.net.Uri;
import android.os.Build;
import android.telephony.TelephonyManager;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.webkit.WebView;
import android.widget.Button;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

/**
 * Class for building custom dialogs with specific types of information
 * @author meec
 */
public class InfoHelper{
    private Activity mContext;
    private Dialog mDialog;
    private LayoutInflater mInflater;
    private Typeface roboLight;
    private Typeface roboRegular;

    public InfoHelper(Activity context) {
        this.mContext = context;
        // Loading Font Face
        roboLight = Typeface.createFromAsset(mContext.getAssets(), "fonts/Roboto-Light.ttf");
        roboRegular = Typeface.createFromAsset(mContext.getAssets(), "fonts/Roboto-Regular.ttf");
    }

    /**
     * Shows a description dialog.
     * Since v1.7.4 it supports html text.
     * @param desc The text of description to show off
     */
    @SuppressLint("NewApi")
    public void show(String desc) {
        this.mInflater = LayoutInflater.from(mContext);
        final View infoView = mInflater.inflate(R.layout.info_dialog, null);

        TextView dTitle = (TextView)infoView.findViewById(R.id.dialog_title);
        TextView title = (TextView)infoView.findViewById(R.id.title);
        //TextView txt = (TextView)infoView.findViewById(R.id.info_text);
        WebView webDesc = (WebView)infoView.findViewById(R.id.webText);
        Button dialogButton = (Button) infoView.findViewById(R.id.btn_close);

        String header="<html xmlns=\"http://www.w3.org/1999/xhtml\"><head><meta http-equiv=\"Content-Type\" content=\"text/html; charset=utf-8\"><title>Lorem Ipsum</title></head><body>";
        String footer="</body></html>";
        //CharSequence descToShow = Html.fromHtml(desc);
        webDesc.setBackgroundColor(0);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB) {
            webDesc.setLayerType(WebView.LAYER_TYPE_SOFTWARE, null);
        }
        webDesc.setBackgroundColor(Color.TRANSPARENT);
        webDesc.setHorizontalScrollBarEnabled(false);

        webDesc.loadDataWithBaseURL(null, header+desc+footer, "text/html", "utf-8", null);

        Log.d("HTML TEXT",desc);
        dTitle.setText("Πληροφορίες");
        title.setText("Αναλυτικές Πληροφορίες");
        title.setTypeface(roboLight);
        //txt.setText(descToShow);
        //txt.setTypeface(roboRegular);

        AlertDialog.Builder builder;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB) {
            builder = new AlertDialog.Builder(mContext, AlertDialog.THEME_DEVICE_DEFAULT_DARK);
        } else {
            builder = new AlertDialog.Builder(mContext);
        }
        //builder.setTitle("Πληροφορίες");
        //builder.setIcon(R.drawable.logobackface);         
        builder.setView(infoView);

        mDialog = builder.create();
        mDialog.requestWindowFeature(Window.FEATURE_NO_TITLE);



        dialogButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v)
            {
                mDialog.dismiss();
            }
        });

        mDialog.show();
    }

    /**
     * Shows a telephone dialog in which the users can call the numbers
     * @param tels An arrayList with telephones strings
     * @param fax A string with fax number
     */
    @SuppressLint("NewApi")
    public void show(ArrayList<String> tels, String fax) {
        this.mInflater = LayoutInflater.from(mContext);
        final View infoView = mInflater.inflate(R.layout.tel_dialog, null);

        TextView dTitle = (TextView)infoView.findViewById(R.id.dialog_title);
        TextView titleTel = (TextView)infoView.findViewById(R.id.title_tel);
        TextView titleFax = (TextView)infoView.findViewById(R.id.title_fax);
        final TextView tel1 = (TextView)infoView.findViewById(R.id.tel1);
        final TextView tel2 = (TextView)infoView.findViewById(R.id.tel2);
        final TextView tel3 = (TextView)infoView.findViewById(R.id.tel3);
        final TextView fax1 = (TextView)infoView.findViewById(R.id.fax);
        final RelativeLayout tel1L = (RelativeLayout) infoView.findViewById(R.id.tel1_layout);
        final RelativeLayout tel2L = (RelativeLayout) infoView.findViewById(R.id.tel2_layout);
        final RelativeLayout tel3L = (RelativeLayout) infoView.findViewById(R.id.tel3_layout);
        final RelativeLayout faxL = (RelativeLayout) infoView.findViewById(R.id.fax_layout);
        Button dialogButton = (Button) infoView.findViewById(R.id.btn_close);

        dTitle.setText("Τηλέφωνα");

        titleTel.setTypeface(roboLight);
        titleTel.setText("Σταθερό και Κινητό Τηλέφωνο");

        titleFax.setText("Fax Τηλέφωνο");
        titleFax.setTypeface(roboLight);

        int size=tels.size();
        if(size>0)
            tel1.setText(tels.get(0));
        tel1.setTypeface(roboRegular);
        if(size>1)
            tel2.setText(tels.get(1));
        tel2.setTypeface(roboRegular);
        if(size>2)
            tel3.setText(tels.get(2));
        tel3.setTypeface(roboRegular);
        if(fax!=null && !fax.equals(""))
            fax1.setText(fax);
        fax1.setTypeface(roboRegular);

        AlertDialog.Builder builder;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB) {
            builder = new AlertDialog.Builder(mContext, AlertDialog.THEME_DEVICE_DEFAULT_DARK);
        } else {
            builder = new AlertDialog.Builder(mContext);
        }
        //builder.setTitle("Πληροφορίες");
        //builder.setIcon(R.drawable.logobackface);         
        builder.setView(infoView);

        mDialog = builder.create();
        mDialog.requestWindowFeature(Window.FEATURE_NO_TITLE);


        tel1L.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v)
            {
                if (tel1.getText()==null || tel1.getText().equals("-")){
                    Toast t = Toast.makeText(infoView.getContext(), "Μη Διαθέσιμο Τηλέφωνο", Toast.LENGTH_LONG);
                    t.setGravity(Gravity.TOP|Gravity.CENTER_HORIZONTAL, 0, 0);
                    t.show();
                }
                else{
                    if (!isTelephonyEnabled()){
                        Toast t = Toast.makeText(infoView.getContext(), "Η συσκευή σας δεν υποστηρίζει τηλεφωνικές κλήσεις.", Toast.LENGTH_LONG);
                        t.setGravity(Gravity.TOP|Gravity.CENTER_HORIZONTAL, 0, 0);
                        t.show();
                    }
                    else{
                        call(tel1.getText().toString());
                    }
                }
            }
        });
        tel2L.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v)
            {
                if (tel2.getText()==null || tel2.getText().equals("-")){
                    Toast t = Toast.makeText(infoView.getContext(), "Μη Διαθέσιμο Τηλέφωνο", Toast.LENGTH_LONG);
                    t.setGravity(Gravity.TOP|Gravity.CENTER_HORIZONTAL, 0, 0);
                    t.show();
                }
                else{
                    if (!isTelephonyEnabled()){
                        Toast t = Toast.makeText(infoView.getContext(), "Η συσκευή σας δεν υποστηρίζει τηλεφωνικές κλήσεις.", Toast.LENGTH_LONG);
                        t.setGravity(Gravity.TOP|Gravity.CENTER_HORIZONTAL, 0, 0);
                        t.show();
                    }
                    else{
                        call(tel2.getText().toString());
                    }
                }
            }
        });

        tel3L.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v)
            {
                if (tel3.getText()==null || tel3.getText().equals("-")){
                    Toast t = Toast.makeText(infoView.getContext(), "Μη Διαθέσιμο Τηλέφωνο", Toast.LENGTH_LONG);
                    t.setGravity(Gravity.TOP|Gravity.CENTER_HORIZONTAL, 0, 0);
                    t.show();
                }
                else{
                    if (!isTelephonyEnabled()){
                        Toast t = Toast.makeText(infoView.getContext(), "Η συσκευή σας δεν υποστηρίζει τηλεφωνικές κλήσεις.", Toast.LENGTH_LONG);
                        t.setGravity(Gravity.TOP|Gravity.CENTER_HORIZONTAL, 0, 0);
                        t.show();
                    }
                    else{
                        call(tel3.getText().toString());
                    }
                }
            }
        });

        faxL.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v)
            {
                if (fax1.getText()==null || fax1.getText().equals("-")){
                    Toast t = Toast.makeText(infoView.getContext(), "Μη Διαθέσιμο Τηλέφωνο", Toast.LENGTH_LONG);
                    t.setGravity(Gravity.TOP|Gravity.CENTER_HORIZONTAL, 0, 0);
                    t.show();
                }
                else{
                    if (!isTelephonyEnabled()){
                        Toast t = Toast.makeText(infoView.getContext(), "Η συσκευή σας δεν υποστηρίζει τηλεφωνικές κλήσεις.", Toast.LENGTH_LONG);
                        t.setGravity(Gravity.TOP|Gravity.CENTER_HORIZONTAL, 0, 0);
                        t.show();
                    }
                    else{
                        call(fax1.getText().toString());
                    }
                }
            }
        });
        dialogButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v)
            {
                mDialog.dismiss();
            }
        });

        mDialog.show();
    }

    public void call(String phone) {
        Intent callIntent = new Intent(Intent.ACTION_DIAL);
        callIntent.setData(Uri.parse("tel:"+phone.replaceAll("\\s","")));
        ((Activity) mContext).startActivity(callIntent);
    }

    private boolean isTelephonyEnabled(){
        TelephonyManager tm = (TelephonyManager)mContext.getSystemService(Activity.TELEPHONY_SERVICE);
        return tm != null && tm.getSimState()==TelephonyManager.SIM_STATE_READY;
    }
}
