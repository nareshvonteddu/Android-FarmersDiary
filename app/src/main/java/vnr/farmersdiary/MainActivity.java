package vnr.farmersdiary;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.util.DisplayMetrics;
import android.view.Menu;
import android.view.MenuItem;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;

import java.util.LinkedList;
import java.util.List;
import java.util.Locale;

public class MainActivity extends ActionBarActivity {
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


        String phoneNbr = loginPreferences.getString(SPFStrings.PHONENUMBER.getValue(),"");

        if(phoneNbr == "") {
            Intent I = new Intent(MainActivity.this, LoginActivity.class);
            startActivity(I);
        }
        else {
            Intent I = new Intent(MainActivity.this, farmerCrops.class);
            startActivity(I);
        }
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
