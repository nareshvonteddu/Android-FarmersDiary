package vnr.farmersdiary;

import android.app.Activity;
import android.app.DatePickerDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Spinner;
import android.widget.TextView;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;


public class AddCrop extends Activity implements AdapterView.OnItemSelectedListener {

    public Spinner cropSpinner;
    public ArrayList<CropRegional> CropsRegional;
    Spinner unitsSpiner;
    TextView converterTextView;
    EditText yieldEditText;
    TextView yieldDate;
    EditText acresEditText;
    TextView sowDate;
    public ProgressBar progressBar;
    public FarmerCrop farmerCrop;
    public EditText investment;
    private int converterPosition;
    EditText estimatePriceEditText;
    TextView estimatePriceUnitTextView;
    EditText estimateIncomeEditText;


    private CropRegional selectedCrop;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_crop);

        unitsSpiner = (Spinner) findViewById(R.id.unitsSpinner);
        cropSpinner = (Spinner) findViewById(R.id.cropSpinner);
        converterTextView = (TextView) findViewById(R.id.convertionTextView);
        yieldEditText = (EditText) findViewById(R.id.yieldEditText);
        yieldDate = (TextView) findViewById(R.id.yieldDate);
        acresEditText = (EditText) findViewById(R.id.acresEditText);
        sowDate = (TextView) findViewById(R.id.sowDate);
        progressBar = (ProgressBar) findViewById(R.id.addCropProgres);
        investment = (EditText) findViewById(R.id.investment);
        estimatePriceEditText = (EditText) findViewById(R.id.estimatePriceEditText);
        estimatePriceUnitTextView = (TextView) findViewById(R.id.estimatePriceUnitsTextView);
        estimateIncomeEditText = (EditText) findViewById(R.id.estimateIncomeEditText);

        progressBar.setVisibility(View.GONE);
        farmerCrop = new FarmerCrop();
        acresEditText.setText("1");
        DateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy");
        try {
            Calendar c = Calendar.getInstance();
            String formattedDate = dateFormat.format(c.getTime());
            sowDate.setText(formattedDate);
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this,
                R.array.units_array, android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        unitsSpiner.setAdapter(adapter);
        unitsSpiner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                converterPosition = position;
                String converterText = "";
                switch (position) {
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
                estimatePriceUnitTextView.setText(unitsSpiner.getSelectedItem().toString());
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        estimatePriceUnitTextView.setText(unitsSpiner.getSelectedItem().toString());
        estimatePriceEditText.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if(!hasFocus)
                {
                    try
                    {
                        double yield = Double.valueOf(yieldEditText.getText().toString());
                        double price = Double.valueOf(estimatePriceEditText.getText().toString());
                        String income = String.valueOf(yield * price);
                        estimateIncomeEditText.setText(income);
                    }
                    catch (Exception e)
                    {
                        e.printStackTrace();
                        estimateIncomeEditText.setText("0.0");
                    }
                }
            }
        });

        cropSpinner.setOnItemSelectedListener(this);
        CropsRegional = new ArrayList<CropRegional>();
        //MobileServiceDataLayer.GetCrops(this);
        cropSpinner.setAdapter(new AddCropSpinnerItemAdapter(this, android.R.layout.simple_dropdown_item_1line, Cache.CropRegionalCache));

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

    public void onSowDateClick(View view)
    {
        DialogFragment newFragment = new DatePickerFragment() {
            @Override
            public void onDateSet(DatePicker view, int year, int month, int day)
            {
                sowDate.setText(day+"/"+month+"/"+year);
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

    public void onDoneClick(View view)
    {

        farmerCrop.Crop_Id = selectedCrop.Crop_Id;
        farmerCrop.Acres = Double.valueOf(acresEditText.getText().toString());
        DateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy");
        try {
            farmerCrop.CropDate = dateFormat.parse(sowDate.getText().toString());
        } catch (ParseException e) {
            e.printStackTrace();
        }
        farmerCrop.EstimateInvestment = Double.valueOf(investment.getText().toString());

        try {
            farmerCrop.EstimateYieldDate = dateFormat.parse(yieldDate.getText().toString());
        } catch (ParseException e) {
            e.printStackTrace();
        }

        SharedPreferences loginPreferences = getSharedPreferences(SPFStrings.SPFNAME.getValue(),
                Context.MODE_PRIVATE);
        String phoneNbr = loginPreferences.getString(SPFStrings.PHONENUMBER.getValue(),"");
        farmerCrop.FarmerPhoneNbr = phoneNbr;
        farmerCrop.UnitResourceIndex = converterPosition;
        switch (converterPosition)
        {
            case 0:
                farmerCrop.EstimateYieldAmount = Double.valueOf(yieldEditText.getText().toString()) * 1;
                break;
            case 1:
                farmerCrop.EstimateYieldAmount = Double.valueOf(yieldEditText.getText().toString()) * 100;
                break;
            case 2:
                farmerCrop.EstimateYieldAmount = Double.valueOf(yieldEditText.getText().toString()) * 1000;
                break;
        }
        farmerCrop.EstimatePrice = Double.valueOf(estimatePriceEditText.getText().toString());

        MobileServiceDataLayer.CreateFarmerCrop(farmerCrop, this);
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
         selectedCrop = (CropRegional) parent.getSelectedItem();
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