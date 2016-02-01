package vnr.farmersdiary;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.res.TypedArray;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.Spinner;
import android.widget.TextView;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;

public class InvestmentsDetail extends Activity {

    public ListView investmentListView;
    public TextView totalAmounttextView;
    private int investmentTypeId = 0;

    ImageButton saveInvestmentButton;
    ImageButton deleteInvestmentButton;
    Spinner investmentTypeSpinner;
    EditText editInvestmentText;
    public String farmerCropId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_investments_detail);


        totalAmounttextView = (TextView) findViewById(R.id.investmentsDetailTotalText);
        investmentListView = (ListView) findViewById(R.id.investmentsListView);

        Bundle params = getIntent().getExtras();
        if(params != null)
        {
            farmerCropId = params.getString("farmerCropId");
            totalAmounttextView.setText(params.getString("totalInvestment"));
        }

        if(!Cache.InvestmentsCache.isEmpty())
        {
            investmentListView.setAdapter(new InvestmentsItemAdapter(this, android.R.layout.simple_list_item_1, Cache.InvestmentsCache));
        }

        investmentListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> parent, final View view, int position, long id) {
                final Investment clickedInvestment = (Investment) investmentListView.getItemAtPosition(position);

                final Dialog dialog = new Dialog(parent.getContext());
                dialog.setContentView(R.layout.activity_add_edit_investment);

                investmentTypeSpinner = (Spinner) dialog.findViewById(R.id.investmentTypeSpinner);
                ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(parent.getContext(),
                        R.array.investment_array, android.R.layout.simple_spinner_item);
                adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                investmentTypeSpinner.setAdapter(adapter);

                investmentTypeSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {

                    @Override
                    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                        investmentTypeId = position;
                    }

                    @Override
                    public void onNothingSelected(AdapterView<?> parent) {

                    }
                });

                editInvestmentText = (EditText) dialog.findViewById(R.id.editInvestmentAmountText);

                saveInvestmentButton = (ImageButton) dialog.findViewById(R.id.saveInvestmentButton);
                deleteInvestmentButton = (ImageButton) dialog.findViewById(R.id.deleteInvestmentButton);

                saveInvestmentButton.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        clickedInvestment.InvestmentType =  (String)getResources().obtainTypedArray(R.array.investment_array).getText(investmentTypeId); //investmentTypeSpinner.getSelectedItem().toString();
                        clickedInvestment.Amount = Double.valueOf(editInvestmentText.getText().toString());
                        clickedInvestment.FarmerCropId = farmerCropId;
                        clickedInvestment.investmentid = investmentTypeId;
                        MobileServiceDataLayer.UpdateInvestment(clickedInvestment, view.getContext());
                        dialog.dismiss();
                    }
                });

                deleteInvestmentButton.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        MobileServiceDataLayer.DeleteInvestment(clickedInvestment,view.getContext());
                        dialog.dismiss();
                    }
                });


                if (clickedInvestment != null) {
                    TypedArray ids = getResources().obtainTypedArray(R.array.investment_array);

                    for (int i = 0; i <= ids.length(); i++) {
                        String val = (String) ids.getText(i);
                        if (val != null && val.equals(clickedInvestment.InvestmentType)) {
                            investmentTypeSpinner.setSelection(i);
                        }
                    }
                    ids.recycle();
                    editInvestmentText.setText(MainActivity.currencyFormatter.format(clickedInvestment.Amount));
                }


                dialog.show();

            }
        });
    }

    public void onAddInvestmentClick(final View view)
    {

        final Investment newInvestment = new Investment();
        final Dialog dialog = new Dialog(this);
        dialog.setContentView(R.layout.activity_add_edit_investment);

        investmentTypeSpinner = (Spinner) dialog.findViewById(R.id.investmentTypeSpinner);
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this,
                R.array.investment_array, android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        investmentTypeSpinner.setAdapter(adapter);

        investmentTypeSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {

            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                investmentTypeId = position;
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        editInvestmentText = (EditText) dialog.findViewById(R.id.editInvestmentAmountText);

        saveInvestmentButton = (ImageButton) dialog.findViewById(R.id.saveInvestmentButton);
        deleteInvestmentButton = (ImageButton) dialog.findViewById(R.id.deleteInvestmentButton);

        saveInvestmentButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                newInvestment.InvestmentType = (String)getResources().obtainTypedArray(R.array.investment_array).getText(investmentTypeId); //investmentTypeSpinner.getSelectedItem().toString();
                newInvestment.Amount = Double.valueOf(editInvestmentText.getText().toString());
                newInvestment.FarmerCropId = farmerCropId;
                newInvestment.investmentid = investmentTypeId;
                SharedPreferences loginPreferences = view.getContext().getSharedPreferences(SPFStrings.SPFNAME.getValue(),
                        Context.MODE_PRIVATE);
                String phoneNbr = loginPreferences.getString(SPFStrings.PHONENUMBER.getValue(), "");
                newInvestment.FarmerPhoneNbr = phoneNbr;
                DateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy");
                try {
                    Calendar c = Calendar.getInstance();
                    String formattedDate = dateFormat.format(c.getTime());
                    newInvestment.InvestmentDate = dateFormat.parse(formattedDate);
                }
                catch (ParseException e) {

                    e.printStackTrace();
                }
                catch (Exception e)
                {
                    e.printStackTrace();
                }
                MobileServiceDataLayer.InsertInvestment(newInvestment,view.getContext());
                dialog.dismiss();
            }
        });

        deleteInvestmentButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });
        dialog.show();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_investments_detail, menu);
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
}
