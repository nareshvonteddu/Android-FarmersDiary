package vnr.farmersdiary;

import android.content.Intent;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
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
