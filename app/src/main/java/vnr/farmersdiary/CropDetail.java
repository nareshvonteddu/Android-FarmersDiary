package vnr.farmersdiary;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.drawable.BitmapDrawable;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.GridLayout;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.Spinner;
import android.widget.TextView;

public class CropDetail extends Activity {

    public String farmerCropId;
    public TextView TotalAmountTextView;
    public ProgressBar CropDetailProgressBar;
    public TextView EstimateInvestmentTextView;
    public TextView ActualInvestmentRectTextView;
    public TextView EstimateIncomeRectTextView;
    public EditText ActualYieldAmountEditText;
    public Spinner ActualYieldUnitSpinner;
    public TextView ActualYieldConversionTextView;
    public EditText ActualPriceEditText;
    public TextView ActualPriceUnitsTextView;
    public TextView ActualIncomeEditText;
    public TextView ActualActualInvestmentRectTextView;
    public TextView ActualIncomeRectTextView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_crop_detail);

        TotalAmountTextView = (TextView) findViewById(R.id.investmentTotalAmountText);
        CropDetailProgressBar = (ProgressBar) findViewById(R.id.cropDetailProgressBar);
        EstimateInvestmentTextView = (TextView) findViewById(R.id.estimateInvestmentTextView);
        ActualInvestmentRectTextView = (TextView) findViewById(R.id.actualInvestmentRectTextView);
        EstimateIncomeRectTextView = (TextView) findViewById(R.id.estimateIncomeRectTextView);
        ActualYieldAmountEditText = (EditText) findViewById(R.id.actualYieldEditText);
        ActualYieldUnitSpinner = (Spinner) findViewById(R.id.actualYieldUnitsSpinner);
        ActualYieldConversionTextView = (TextView) findViewById(R.id.actualYieldConvertionTextView);
        ActualPriceEditText = (EditText) findViewById(R.id.actualPriceEditText);
        ActualPriceUnitsTextView = (TextView) findViewById(R.id.actualPriceUnitsTextView);
        ActualIncomeEditText = (EditText) findViewById(R.id.actualIncomeEditText);
        ActualActualInvestmentRectTextView = (TextView) findViewById(R.id.actualActualInvestmentRectTextView);
        ActualIncomeRectTextView = (TextView) findViewById(R.id.actualIncomeRectTextView);

        Bundle params = getIntent().getExtras();
        if (params != null) {
            farmerCropId = params.getString("farmerCropId");
        }

        CropDetailProgressBar.setVisibility(View.GONE);

        MobileServiceDataLayer.GetCropInvestments(this);

        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this,
                R.array.units_array, android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        ActualYieldUnitSpinner.setAdapter(adapter);
        ActualYieldUnitSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
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

        ActualPriceUnitsTextView.setText(ActualYieldUnitSpinner.getSelectedItem().toString());
        ActualPriceEditText.setOnFocusChangeListener(new View.OnFocusChangeListener(){

            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if(!hasFocus)
                {
                    try
                    {
                        double yield = Double.valueOf(ActualYieldAmountEditText.getText().toString());
                        double price = Double.valueOf(ActualPriceEditText.getText().toString());
                        String income = MainActivity.formatter.format(yield * price);
                        ActualIncomeEditText.setText(income);
                        ActualIncomeRectTextView.setText(String.valueOf(income));
                        DrawActualNetIncomeGraph();
                    }
                    catch (Exception e)
                    {
                        e.printStackTrace();
                        ActualIncomeEditText.setText("0.0");
                    }
                }
            }
        });
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

        //EstimateInvestmentTextView.setText(MainActivity.formatter.format(farmerCrop.EstimateInvestment));
        //EstimateIncomeRectTextView.setText(MainActivity.formatter.format(farmerCrop.EstimateIncome));
        ActualActualInvestmentRectTextView.setText(MainActivity.formatter.format(farmerCrop.ActualInvestment));

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

    public void DrawNetIncomeGraph()
    {
        LinearLayout ll = (LinearLayout) findViewById(R.id.investmentRect);
        LinearLayout li = (LinearLayout) findViewById(R.id.incomeRect);

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

        if(farmerCrop == null) return;

        EstimateInvestmentTextView.setText(MainActivity.formatter.format(farmerCrop.EstimateInvestment));
        EstimateIncomeRectTextView.setText(MainActivity.formatter.format(farmerCrop.EstimateIncome));
        ActualInvestmentRectTextView.setText(MainActivity.formatter.format(farmerCrop.ActualInvestment));

        double income = farmerCrop.EstimateIncome;

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
    protected void onResume()
    {
        super.onResume();
        double totalAmount = 0;
//        for (int i = 0; i < Cache.InvestmentsCache.toArray().length; i++) {
//            totalAmount += ((Investment) Cache.InvestmentsCache.toArray()[i]).Amount;
//        }
        for (int i = 0; i < Cache.FarmerCropsCache.toArray().length; i++)
        {
            if(((FarmerCrop)Cache.FarmerCropsCache.toArray()[i]).id.equals(farmerCropId))
            {
               totalAmount = ((FarmerCrop)Cache.FarmerCropsCache.toArray()[i]).ActualInvestment;
                break;
            }
        }


        TotalAmountTextView.setText(MainActivity.formatter.format(totalAmount));
        //View view = findViewById(R.id.activity_crop_detail);
        DrawNetIncomeGraph();
    }


    public void OnInvestmentsButtonClick(View view)
    {
        Intent I = new Intent(this, InvestmentsDetail.class);
        I.putExtra("farmerCropId", farmerCropId);
        I.putExtra("totalInvestment", TotalAmountTextView.getText());
        startActivity(I);
    }

//    @Override
//    public boolean onCreateOptionsMenu(Menu menu) {
//        // Inflate the menu; this adds items to the action bar if it is present.
//        getMenuInflater().inflate(R.menu.menu_crop_detail, menu);
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
}
