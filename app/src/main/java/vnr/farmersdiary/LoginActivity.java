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
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import java.util.Locale;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Created by nvonteddu on 8/4/15.
 */
public class LoginActivity extends ActionBarActivity
{

    EditText nameText;
    EditText phoneNbrText;
    Button doneButton;
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);



        nameText = (EditText) findViewById(R.id.NameText);
        phoneNbrText = (EditText) findViewById(R.id.PhoneNbrText);


    }

    public void onDoneClick(View view) {
        try {
            User newUser = new User();
            newUser.Name = (String) nameText.getText().toString();
            newUser.PhoneNbr = (String) phoneNbrText.getText().toString();
            MobileServiceDataLayer.CreateUser(newUser, this);
        } catch (Exception ex) {
            Logger.getAnonymousLogger().log(Level.ALL, ex.toString());
        }
        ;
    }

        @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    public boolean onOptionsItemSelected(MenuItem item)
    {
        int id = item.getItemId();
        SharedPreferences loginPreferences = this.getSharedPreferences(SPFStrings.SPFNAME.getValue(),
                Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = loginPreferences.edit();
        switch (id)
        {
            case R.id.action_telugu:
                setLocale("te");
                editor.putString(SPFStrings.LANGUAGE.getValue(), "te");
                editor.commit();
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    public void setLocale(String lang)
    {
        Locale myLocale = new Locale(lang);
        Resources res = getResources();
        DisplayMetrics dm = res.getDisplayMetrics();
        Configuration conf = res.getConfiguration();
        conf.locale = myLocale;
        res.updateConfiguration(conf, dm);
        Intent refresh = new Intent(this, LoginActivity.class);
        startActivity(refresh);
        finish();
    }

    }
