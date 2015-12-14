package vnr.farmersdiary;

import android.app.Activity;
import android.app.Application;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.telephony.TelephonyManager;
import android.util.DisplayMetrics;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Spinner;

import java.util.Locale;
import java.util.Objects;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Created by nvonteddu on 8/4/15.
 */
public class LoginActivity extends Activity
{

    EditText nameText;
    EditText phoneNbrText;
    Button doneButton;
    public ProgressBar loginProgressBar;
    Spinner languagesSpinner;

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);



        phoneNbrText = (EditText) findViewById(R.id.PhoneNbrText);
        loginProgressBar = (ProgressBar) findViewById(R.id.loginProgressBar);
        languagesSpinner = (Spinner) findViewById(R.id.languagesSpinner);

        final ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this,
                R.array.languages_array, android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        languagesSpinner.setAdapter(adapter);
        languagesSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id)
            {
                String selectedItem = (String) adapter.getItem(position);
                SharedPreferences loginPreferences = getSharedPreferences(SPFStrings.SPFNAME.getValue(),
                        Context.MODE_PRIVATE);
                SharedPreferences.Editor editor = loginPreferences.edit();
                switch (selectedItem)
                {
                    case "English":
                        if(!loginPreferences.getString(SPFStrings.LANGUAGE.getValue(), "").equals(""))
                        {
                            setLocale("en");
                            String hint = getResources().getString(R.string.hintPhone);
                            phoneNbrText.setHint(hint);
                            editor.putString(SPFStrings.LANGUAGE.getValue(), "");
                            editor.commit();
                        }
                        break;

                    case "తెలుగు":
                        setLocale("te");
                        String hint = getResources().getString(R.string.hintPhone);
                        phoneNbrText.setHint(hint);
                        editor.putString(SPFStrings.LANGUAGE.getValue(), "te");
                        editor.commit();
                        break;
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
        String mPhoneNumber = "";
        try
        {

            TelephonyManager tMgr = (TelephonyManager)this.getSystemService(Context.TELEPHONY_SERVICE);
            mPhoneNumber = tMgr.getLine1Number();
        }
        catch (Exception ex)
        {
            Logger.getAnonymousLogger().log(Level.ALL, ex.toString());
        }
        if(mPhoneNumber != null && !mPhoneNumber.equals(""))
        {
            mPhoneNumber = mPhoneNumber.substring(1);
            phoneNbrText.setText(mPhoneNumber);
        }

        loginProgressBar.setVisibility(View.GONE);
    }

    public void onDoneClick(View view)
    {
        try
        {
            if(phoneNbrText.getText().length() != 10) return;
            User newUser = new User();
            newUser.PhoneNbr = (String) phoneNbrText.getText().toString();
            MobileServiceDataLayer.CreateUser(newUser, this);
        } catch (Exception ex) {
            Logger.getAnonymousLogger().log(Level.ALL, ex.toString());
        }
        ;
    }

//        @Override
//    public boolean onCreateOptionsMenu(Menu menu) {
//        // Inflate the menu; this adds items to the action bar if it is present.
//        getMenuInflater().inflate(R.menu.menu_main, menu);
//        return true;
//    }

//    public boolean onOptionsItemSelected(MenuItem item)
//    {
//        int id = item.getItemId();
//        SharedPreferences loginPreferences = this.getSharedPreferences(SPFStrings.SPFNAME.getValue(),
//                Context.MODE_PRIVATE);
//        SharedPreferences.Editor editor = loginPreferences.edit();
//        switch (id)
//        {
//            case R.id.action_telugu:
//                setLocale("te");
//                editor.putString(SPFStrings.LANGUAGE.getValue(), "te");
//                editor.commit();
//                return true;
//        }
//        return super.onOptionsItemSelected(item);
//    }

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

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK )
        {
            Intent intent = new Intent(this, MainActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            intent.putExtra("EXIT", true);
            startActivity(intent);
        }
        return super.onKeyDown(keyCode, event);
    }

    }
