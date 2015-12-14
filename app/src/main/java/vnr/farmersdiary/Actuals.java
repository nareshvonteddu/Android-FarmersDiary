package vnr.farmersdiary;

import android.app.Activity;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.drawable.BitmapDrawable;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.Switch;
import android.widget.TextView;

public class Actuals extends Activity {


    public EditText ActualYieldAmountEditText;
    public Spinner ActualYieldUnitSpinner;
    public TextView ActualYieldConversionTextView;
    public EditText ActualPriceEditText;
    public TextView ActualPriceUnitsTextView;
    public TextView ActualIncomeEditText;
    public TextView ActualActualInvestmentRectTextView;
    public TextView ActualIncomeRectTextView;
    Switch yieldDoneSwitch;
    LinearLayout ActualsLayout;
    private String farmerCropId;
    private int converterPosition;
    FarmerCrop farmerCrop = null;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_actuals);


        ActualYieldAmountEditText = (EditText) findViewById(R.id.actualYieldEditText);
        ActualYieldUnitSpinner = (Spinner) findViewById(R.id.actualYieldUnitsSpinner);
        ActualYieldConversionTextView = (TextView) findViewById(R.id.actualYieldConvertionTextView);
        ActualPriceEditText = (EditText) findViewById(R.id.actualPriceEditText);
        ActualPriceUnitsTextView = (TextView) findViewById(R.id.actualPriceUnitsTextView);
        ActualIncomeEditText = (EditText) findViewById(R.id.actualIncomeEditText);
        ActualActualInvestmentRectTextView = (TextView) findViewById(R.id.actualActualInvestmentRectTextView);
        ActualIncomeRectTextView = (TextView) findViewById(R.id.actualIncomeRectTextView);
        ActualsLayout = (LinearLayout) findViewById(R.id.actualsLayout);

        Bundle params = getIntent().getExtras();
        if (params != null) {
            farmerCropId = params.getString("farmerCropId");
            for (int i = 0; i < Cache.FarmerCropsCache.toArray().length; i++) {
                if (((FarmerCrop) Cache.FarmerCropsCache.toArray()[i]).id.equals(farmerCropId)) {
                    farmerCrop = ((FarmerCrop) Cache.FarmerCropsCache.toArray()[i]);
                    break;
                }
            }
        }


        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this,
                R.array.units_array, android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        ActualYieldUnitSpinner.setAdapter(adapter);
        ActualYieldUnitSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
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
                ActualYieldConversionTextView.setText(converterText);
                ActualPriceUnitsTextView.setText(ActualYieldUnitSpinner.getSelectedItem().toString());
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        ActualYieldAmountEditText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s)
            {
                try {
                    double yield = Double.valueOf(ActualYieldAmountEditText.getText().toString());
                    double price = Double.valueOf(ActualPriceEditText.getText().toString());
                    String income = MainActivity.currencyFormatter.format(yield * price);
                    ActualIncomeEditText.setText(income);
                    ActualIncomeRectTextView.setText(String.valueOf(income));
                    DrawActualNetIncomeGraph();
                } catch (Exception e) {
                    e.printStackTrace();
                    ActualIncomeEditText.setText("0.0");
                }
            }
        });

        ActualPriceUnitsTextView.setText(ActualYieldUnitSpinner.getSelectedItem().toString());
        ActualPriceEditText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s)
            {
                try {
                    double yield = Double.valueOf(ActualYieldAmountEditText.getText().toString());
                    double price = Double.valueOf(ActualPriceEditText.getText().toString());
                    String income = MainActivity.currencyFormatter.format(yield * price);
                    ActualIncomeEditText.setText(income);
                    ActualIncomeRectTextView.setText(String.valueOf(income));
                    DrawActualNetIncomeGraph();
                } catch (Exception e) {
                    e.printStackTrace();
                    ActualIncomeEditText.setText("0.0");
                }
            }
        });

        ActualIncomeEditText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                ActualIncomeRectTextView.setText(s.toString());
                DrawActualNetIncomeGraph();
            }
        });


        //DrawActualNetIncomeGraph();
    }


    public void DrawActualNetIncomeGraph()
    {
        LinearLayout ll = (LinearLayout) findViewById(R.id.actualInvestmentRect);
        LinearLayout li = (LinearLayout) findViewById(R.id.actualIncomeRect);

        int maxinvestmentwidth = ll.getWidth();
        int maxincomewidth = li.getWidth();

        if(maxincomewidth == 0 || maxinvestmentwidth == 0)
        {
            return;
        }

        int investmentWidth = 2;
        int incomeWidth = 2;
        FarmerCrop farmerCrop = null;

        for (int i = 0; i < Cache.FarmerCropsCache.toArray().length; i++)
        {
            if(((FarmerCrop)Cache.FarmerCropsCache.toArray()[i]).id.equals(farmerCropId))
            {
                farmerCrop = ((FarmerCrop)Cache.FarmerCropsCache.toArray()[i]);
                break;
            }
        }

        if(ActualIncomeRectTextView.getText().equals(""))
        {
            ActualIncomeRectTextView.setText("00.00");
        }

        ActualActualInvestmentRectTextView.setText(MainActivity.currencyFormatter.format(farmerCrop.ActualInvestment));

        double income = Double.valueOf(ActualIncomeRectTextView.getText().toString());

        if(farmerCrop.ActualInvestment >= income
                && farmerCrop.ActualInvestment != 0)
        {
            investmentWidth = maxinvestmentwidth;

            if(income != 0)
            {
                incomeWidth = (int) ((income/farmerCrop.ActualInvestment) * investmentWidth);
            }
        }
        else if(income != 0)
        {
            incomeWidth = maxincomewidth;
            if(farmerCrop.ActualInvestment != 0)
            {
                double div = (farmerCrop.ActualInvestment/income);
                investmentWidth = (int) Math.round(div * incomeWidth);
            }
        }

        if(investmentWidth < 2) investmentWidth = 2;
        if(incomeWidth < 2) incomeWidth = 2;

        Paint paint = new Paint();
        paint.setColor(Color.parseColor("#CFFF0F05"));
        Bitmap bg = Bitmap.createBitmap(maxinvestmentwidth, 800, Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(bg);
        canvas.drawRect(0, 0, investmentWidth, 550, paint);
        ll.setBackgroundDrawable(new BitmapDrawable(bg));

        Paint paintIncome = new Paint();
        paintIncome.setColor(Color.parseColor("#C80EBB48"));
        Bitmap bgIncome = Bitmap.createBitmap(maxincomewidth, 800, Bitmap.Config.ARGB_8888);
        Canvas canvasIncome = new Canvas(bgIncome);
        canvasIncome.drawRect(0, 0, incomeWidth, 550, paintIncome);
        li.setBackgroundDrawable(new BitmapDrawable(bgIncome));
    }

    @Override
    public void onWindowFocusChanged(boolean hasFocus)
    {
        if(farmerCrop.ActualYieldAmount != 0)
        {
            ActualYieldAmountEditText.setText(String.valueOf(farmerCrop.ActualYieldAmount));
        }
        if(farmerCrop.ActualPrice != 0)
        {
            ActualPriceEditText.setText(String.valueOf(farmerCrop.ActualPrice));
        }
        if(farmerCrop.ActualIncome != 0)
        {
            ActualIncomeEditText.setText(String.valueOf(farmerCrop.ActualIncome));
        }


        DrawActualNetIncomeGraph();
    }


    @Override
    protected void onResume()
    {
        super.onResume();
        ActualYieldUnitSpinner.setSelection(farmerCrop.ActualYieldUnitIndex);
        DrawActualNetIncomeGraph();
    }

    public void onSaveActualsClick(View view)
    {

        farmerCrop.ActualYieldUnitIndex = converterPosition;
        switch (converterPosition)
        {
            case 0:
                farmerCrop.ActualYieldAmount = Double.valueOf(ActualYieldAmountEditText.getText().toString()) * 1;
                break;
            case 1:
                farmerCrop.ActualYieldAmount = Double.valueOf(ActualYieldAmountEditText.getText().toString()) * 100;
                break;
            case 2:
                farmerCrop.ActualYieldAmount = Double.valueOf(ActualYieldAmountEditText.getText().toString()) * 1000;
                break;
        }
        farmerCrop.ActualPrice = Double.valueOf(ActualPriceEditText.getText().toString());
        farmerCrop.ActualIncome = Double.valueOf(ActualIncomeEditText.getText().toString());

        MobileServiceDataLayer.SaveActuals(farmerCrop,this);
    }
}
