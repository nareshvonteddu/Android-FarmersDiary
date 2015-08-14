package vnr.farmersdiary;

import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ProgressBar;
import android.widget.Spinner;

import java.util.ArrayList;


public class AddCrop extends ActionBarActivity implements AdapterView.OnItemSelectedListener {

    public Spinner cropSpinner;
    public ArrayList<CropRegional> CropsRegional;
    public ProgressBar progressBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_crop);

        progressBar = (ProgressBar) findViewById(R.id.addCropProgress);
        cropSpinner = (Spinner) findViewById(R.id.cropSpinner);
        cropSpinner.setOnItemSelectedListener(this);
        CropsRegional = new ArrayList<CropRegional>();
        MobileServiceDataLayer.GetCrops(this);

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_add_crop, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id)
    {
         Object item =   parent.getSelectedItem();
    }

    @Override
    public void onNothingSelected(AdapterView<?> parent)
    {

    }
}
