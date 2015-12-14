package vnr.farmersdiary;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.util.DisplayMetrics;
import java.text.DecimalFormat;
import java.util.Locale;

public class MainActivity extends ActionBarActivity {

    public static  final DecimalFormat currencyFormatter = new DecimalFormat("0.00");
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        MobileServiceDataLayer.InstantiateService(this);

        SharedPreferences loginPreferences = getSharedPreferences(SPFStrings.SPFNAME.getValue(),
                Context.MODE_PRIVATE);

        /*HttpParams httpParams = new BasicHttpParams();
        ThreadSafeClientConnManager connMgr = new ThreadSafeClientConnManager(httpParams, new SchemeRegistry());
        HttpClient client = new DefaultHttpClient(connMgr, httpParams);*/


       /* List<NameValuePair> params = new LinkedList<NameValuePair>();
        params.add(new BasicNameValuePair("key", "1"));
        NameValuePair[] array = new NameValuePair[params.size()];
        params.toArray(array);
        new GetUser().execute(array);*/

        if (getIntent().getBooleanExtra("EXIT", false)) {
            finish();
        }
        else {

            String phoneNbr = loginPreferences.getString(SPFStrings.PHONENUMBER.getValue(), "");

            if (phoneNbr == "") {
                Intent I = new Intent(MainActivity.this, LoginActivity.class);
                startActivity(I);
            } else
            {
                String languageCode = loginPreferences.getString(SPFStrings.LANGUAGE.getValue(), "");
                if(!languageCode.equals("")) setLocale(languageCode);
                Intent I = new Intent(MainActivity.this, farmerCrops.class);
                startActivity(I);
            }
        }

    }

    public void setLocale(String lang)
    {
        Locale myLocale = new Locale(lang);
        Resources res = getResources();
        DisplayMetrics dm = res.getDisplayMetrics();
        Configuration conf = res.getConfiguration();
        conf.locale = myLocale;
        res.updateConfiguration(conf, dm);
        //Intent refresh = new Intent(this, LoginActivity.class);
        //startActivity(refresh);
        //finish();
    }

//    @Override
//    public boolean onCreateOptionsMenu(Menu menu) {
//        // Inflate the menu; this adds items to the action bar if it is present.
//        getMenuInflater().inflate(R.menu.menu_main, menu);
//        return true;
//    }

//    @Override
//    public boolean onOptionsItemSelected(MenuItem item) {
//        // Handle action bar item clicks here. The action bar will
//        // automatically handle clicks on the Home/Up button, so long
//        // as you specify a parent activity in AndroidManifest.xml.
//        int id = item.getItemId();
//
//        //noinspection SimplifiableIfStatement
//        if (id == R.id.action_settings) {
//            return true;
//        }
//
//        return super.onOptionsItemSelected(item);
//    }
}
