package vnr.farmersdiary;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.drawable.BitmapDrawable;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

public class CropDetail extends Activity {

    public String farmerCropId;
    public TextView TotalAmountTextView;
    public TextView EstimateInvestmentTextView;
    public TextView ActualInvestmentRectTextView;
    public TextView EstimateIncomeRectTextView;
    public TextView DetailCropNameTextView;
    public TextView DetailAcresTextView;
    public TextView ProfitTextView;
    public TextView LossTextView;
    public TextView ProfitValueTextView;
    public TextView EstimateIncomeLabel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_crop_detail);

        TotalAmountTextView = (TextView) findViewById(R.id.investmentTotalAmountText);
        EstimateInvestmentTextView = (TextView) findViewById(R.id.estimateInvestmentTextView);
        ActualInvestmentRectTextView = (TextView) findViewById(R.id.actualInvestmentRectTextView);
        EstimateIncomeRectTextView = (TextView) findViewById(R.id.estimateIncomeRectTextView);
        DetailAcresTextView = (TextView) findViewById(R.id.tvDetailCropAcres);
        DetailCropNameTextView = (TextView) findViewById(R.id.tvDetailCropName);
        ProfitTextView = (TextView) findViewById(R.id.estimateProfitTextView);
        LossTextView = (TextView) findViewById(R.id.estimateLossTextView);
        ProfitValueTextView = (TextView) findViewById(R.id.estimateProfitValueTextView);
        EstimateIncomeLabel = (TextView) findViewById(R.id.estimateIncomeLabel);

        Bundle params = getIntent().getExtras();
        if (params != null) {
            farmerCropId = params.getString("farmerCropId");
            DetailCropNameTextView.setText(params.getString("farmerCropName"));
            DetailAcresTextView.setText(params.getString("farmerCropAcres"));
        }

        MobileServiceDataLayer.GetCropInvestments(this);
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

        double income = farmerCrop.EstimateIncome;
        if(farmerCrop.IsYieldDone)
        {
            income = farmerCrop.ActualIncome;
            EstimateIncomeLabel.setText(getText(R.string.ActualIncome));
        }

        EstimateInvestmentTextView.setText(MainActivity.currencyFormatter.format(farmerCrop.EstimateInvestment));
        EstimateIncomeRectTextView.setText(MainActivity.currencyFormatter.format(income));
        ActualInvestmentRectTextView.setText(MainActivity.currencyFormatter.format(farmerCrop.ActualInvestment));

        if(income >= farmerCrop.ActualInvestment)
        {
            LossTextView.setVisibility(View.GONE);
            ProfitTextView.setVisibility(View.VISIBLE);
            ProfitValueTextView.setText(String.valueOf(income - farmerCrop.ActualInvestment));
        }
        else
        {
            LossTextView.setVisibility(View.VISIBLE);
            ProfitTextView.setVisibility(View.GONE);
            ProfitValueTextView.setText(String.valueOf(farmerCrop.ActualInvestment - income));
        }



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
    public void onWindowFocusChanged(boolean hasFocus)
    {
        DrawNetIncomeGraph();
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


        TotalAmountTextView.setText(MainActivity.currencyFormatter.format(totalAmount));
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

    public void OnEditButtonClick(View view)
    {
        Intent I = new Intent(this, AddCrop.class);
        I.putExtra("farmerCropId", farmerCropId);
        startActivity(I);
    }

    public void onYieldDoneClick(View view)
    {
        Intent I = new Intent(this, Actuals.class);
        I.putExtra("farmerCropId", farmerCropId);
        startActivity(I);
    }

    public void onDeleteCropButtonClick(View view)
    {
        FarmerCrop editingFarmerCrop = null;
        for (int i = 0; i <= Cache.FarmerCropsCache.toArray().length; i++)
        {
            if(farmerCropId.equals(Cache.FarmerCropsCache.get(i).id))
            {
                editingFarmerCrop = Cache.FarmerCropsCache.get(i);
                break;
            }
        }
        if(editingFarmerCrop != null) {
            MobileServiceDataLayer.DeleteFarmerCrop(editingFarmerCrop, this);
        }
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
