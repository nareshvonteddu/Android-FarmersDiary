package vnr.farmersdiary;

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
import android.widget.GridLayout;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

public class CropDetail extends ActionBarActivity {

    public String farmerCropId;
    public TextView TotalAmountTextView;
    public ProgressBar CropDetailProgressBar;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_crop_detail);

        TotalAmountTextView = (TextView)findViewById(R.id.investmentTotalAmountText);
        CropDetailProgressBar = (ProgressBar)findViewById(R.id.cropDetailProgressBar);

        Bundle params = getIntent().getExtras();
        if(params != null)
        {
            farmerCropId = params.getString("farmerCropId");
        }

        CropDetailProgressBar.setVisibility(View.GONE);

        MobileServiceDataLayer.GetCropInvestments(this);


//        GridLayout ll = (GridLayout) findViewById(R.id.investmentRect);
//
//        Paint paint = new Paint();
//        paint.setColor(Color.parseColor("#CD5C5C"));
//        Bitmap bg = Bitmap.createBitmap(580, 800, Bitmap.Config.ARGB_8888);
//        Canvas canvas = new Canvas(bg);
//        canvas.drawRect(5, 50, ll.getWidth(), 550, paint);
//        ll.setBackgroundDrawable(new BitmapDrawable(bg));
//
//        GridLayout li = (GridLayout) findViewById(R.id.incomeRect);
//
//        Paint paintIncome = new Paint();
//        paintIncome.setColor(Color.parseColor("#CD5C5C"));
//        Bitmap bgIncome = Bitmap.createBitmap(580, 800, Bitmap.Config.ARGB_8888);
//        Canvas canvasIncome = new Canvas(bgIncome);
//        canvasIncome.drawRect(5, 50, 350, 550, paint);
//        li.setBackgroundDrawable(new BitmapDrawable(bgIncome));
    }

    @Override
    public void onWindowFocusChanged(boolean b)
    {
        GridLayout ll = (GridLayout) findViewById(R.id.investmentRect);

        Paint paint = new Paint();
        paint.setColor(Color.parseColor("#CFFF0F05"));
        Bitmap bg = Bitmap.createBitmap(580, 800, Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(bg);
        canvas.drawRect(0, 0, ll.getWidth(), 550, paint);
        ll.setBackgroundDrawable(new BitmapDrawable(bg));

        GridLayout li = (GridLayout) findViewById(R.id.incomeRect);

        Paint paintIncome = new Paint();
        paintIncome.setColor(Color.parseColor("#C80EBB48"));
        Bitmap bgIncome = Bitmap.createBitmap(580, 800, Bitmap.Config.ARGB_8888);
        Canvas canvasIncome = new Canvas(bgIncome);
        canvasIncome.drawRect(0, 0, 350, 550, paintIncome);
        li.setBackgroundDrawable(new BitmapDrawable(bgIncome));
    }


    @Override
    protected void onResume()
    {
        super.onResume();
        double totalAmount = 0;
        for (int i = 0; i < Cache.InvestmentsCache.toArray().length; i++) {
            totalAmount += ((Investment) Cache.InvestmentsCache.toArray()[i]).Amount;
        }
        TotalAmountTextView.setText((String.valueOf(totalAmount)));
    }


    public void OnInvestmentsButtonClick(View view)
    {
        Intent I = new Intent(this, InvestmentsDetail.class);
        I.putExtra("farmerCropId", farmerCropId);
        I.putExtra("totalInvestment", TotalAmountTextView.getText());
        startActivity(I);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_crop_detail, menu);
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
