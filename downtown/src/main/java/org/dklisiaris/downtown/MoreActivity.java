package org.dklisiaris.downtown;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.view.MenuItem;
import android.widget.TextView;
import org.dklisiaris.downtown.R;

public class MoreActivity extends ActionBarActivity {

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //supportRequestWindowFeature(Window.FEATURE_ACTION_BAR_OVERLAY);
        setContentView(R.layout.more);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        Intent i = getIntent();
        String form = i.getStringExtra("title");

        TextView title = (TextView)findViewById(R.id.title);
        TextView txt = (TextView)findViewById(R.id.info_text);

        title.setText(makeTitle(form));
        txt.setText(makeText(form));

    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                onBackPressed();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }



    public String makeText(String form){
        String text="text";
        if(form.equals("entry")){
            text="Τώρα μπορείτε να καταχωρήσετε την επιχείρηση σας δωρεάν στον Ελληνικό Πολυοδηγό. " +
                    "Για να γίνει επιτυχής η καταχώρηση σας θα χρειαστείτε ένα τηλέφωνο και πρόσβαση στα στοιχεία " +
                    "της επιχείρησης. Καλέστε μας δωρεάν στο 2105310556 καθημερινώς από 9πμ έως 5μμ και άμεσα ένας " +
                    "εκπροσωπός μας θα σας καθοδηγήσει στην ολοκλήρωση της καταχώρησης.Θυμηθείτε, πως ο Ελληνικός " +
                    "Πολυοδηγός σας προσφέρει μια μοναδική θέση διαφήμισης σε όλα τα κινητά τηλέφωνα και ταμπλέτες." +
                    "Συνεκδοχικά ξεκινήστε τώρα την Δωρεάν Καταχώρησή σας και κερδίστε νέους πελάτες.";
        }
        else if(form.equals("info")){
            text="Ο Ελληνικός Πολυοδηγός ειδικεύεται στην διαφήμιση επιχειρήσεων " +
                    "και επαγγελματιών για όλα τα κινητά τηλέφωνα και ταμπλέτες.Σκοπός μας " +
                    "είναι να μεταφέρουμε την επιχείρηση σας στην παλάμη του ενδιαφερόμενου είτε " +
                    "από το κινητό η ταμπλέτα του σε μορφή πληροφοριών και εικόνων, άμεσα και εύκολα." +
                    "Η κάθε καταχώρηση στην εφαρμογή περιέχει όλα τα στοιχεία μιας επιχείρησης και καλύπτει " +
                    "την πλήρη πληροφόρηση του ενδιαφερόμενου.Αποτελεί λοιπόν μια ξεκάθαρη και αποτελεσματική διαφήμιση.";
        }
        else if(form.equals("contact")){
            text="Μπορείτε να επικοινωνείτε μαζί μας για όποια απορία έχετε σχετικά " +
                    "με τις υπηρεσίες που παρέχουμε στο τηλέφωνο 210 53 10 556 καθημερινά από 9μμ-5πμ. " +
                    "Εναλλακτικά μπορείτε να μας στείλετε email με όλες σας τις ερωτήσεις στο info@futurearts.gr "+
                    "FutureArts. \n"+
                    "286 Ιερά οδός (εμπορικό κέντρο), 1ος Όροφος, "+
                    "Αιγάλεω, Τ.Κ 12244, \n"+
                    "Website: www.futurearts.gr";
        }
        return text;
    }
    public String makeTitle(String form){
        String title="title";
        if(form.equals("entry")){
            title="Δωρεάν Καταχώρηση";
        }
        else if(form.equals("info")){
            title="Πληροφορίες";
        }
        else if(form.equals("contact")){
            title="Επικοινωνία";
        }
        return title;
    }


}
