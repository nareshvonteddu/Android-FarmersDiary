package vnr.farmersdiary;

import android.app.Activity;
import android.app.DatePickerDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.app.Fragment;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Spinner;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.logging.Level;
import java.util.logging.Logger;


public class AddCrop extends Activity implements AdapterView.OnItemSelectedListener {

    public Spinner cropSpinner;
    public ArrayList<CropRegional> CropsRegional;
    public ProgressBar progressBar;
    Spinner unitsSpiner;
    TextView converterTextView;
    EditText yieldEditText;
    TextView yieldDate;
    EditText acresEditText;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_crop);

        unitsSpiner = (Spinner) findViewById(R.id.unitsSpinner);
        progressBar = (ProgressBar) findViewById(R.id.addCropProgress);
        cropSpinner = (Spinner) findViewById(R.id.cropSpinner);
        converterTextView = (TextView) findViewById(R.id.convertionTextView);
        yieldEditText = (EditText) findViewById(R.id.yieldEditText);
        yieldDate = (TextView) findViewById(R.id.yieldDate);
        acresEditText = (EditText) findViewById(R.id.acresEditText);

        acresEditText.setText("1");
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this,
                R.array.units_array, android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        unitsSpiner.setAdapter(adapter);
        unitsSpiner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id)
            {
                String converterText = "";
                switch (position)
                {
                    case 0:
                        converterText = " = X 1kg";
                        break;
                    case 1:
                        converterText = " = X 100kg";
                        break;
                    case 2:
                        converterText = " = X 1000kg";
                        break;
                }
                converterTextView.setText(converterText);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        cropSpinner.setOnItemSelectedListener(this);
        CropsRegional = new ArrayList<CropRegional>();
        MobileServiceDataLayer.GetCrops(this);

    }

    public void onYieldDateClick(View view)
    {
        DialogFragment newFragment = new DatePickerFragment() {
            @Override
            public void onDateSet(DatePicker view, int year, int month, int day)
            {
                yieldDate.setText(day+"/"+month+"/"+year);
            }
        };
        newFragment.show(getFragmentManager(), "datePicker");
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event)
    {
        if (keyCode == KeyEvent.KEYCODE_BACK)
        {

        }

        return super.onKeyDown(keyCode, event);
    }

//    @Override
//    public boolean onCreateOptionsMenu(Menu menu) {
//        // Inflate the menu; this adds items to the action bar if it is present.
//        getMenuInflater().inflate(R.menu.menu_add_crop, menu);
//        return true;
//    }
//
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

    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id)
    {
         Object item =   parent.getSelectedItem();
    }

    @Override
    public void onNothingSelected(AdapterView<?> parent)
    {

    }


    public static class DatePickerFragment extends DialogFragment
            implements DatePickerDialog.OnDateSetListener {

        @Override
        public Dialog onCreateDialog(Bundle savedInstanceState) {
            // Use the current date as the default date in the picker
            final Calendar c = Calendar.getInstance();
            int year = c.get(Calendar.YEAR);
            int month = c.get(Calendar.MONTH);
            int day = c.get(Calendar.DAY_OF_MONTH);

            // Create a new instance of DatePickerDialog and return it
            return new DatePickerDialog(getActivity(), this, year, month, day);
        }

        public void onDateSet(DatePicker view, int year, int month, int day) {
            // Do something with the date chosen by the user

        }
    }
}