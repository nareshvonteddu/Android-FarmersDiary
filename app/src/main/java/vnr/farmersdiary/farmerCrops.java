package vnr.farmersdiary;

import android.app.Activity;
import android.content.Intent;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.ProgressBar;

import java.util.ArrayList;


public class farmerCrops extends Activity {

    public ListView farmerCropListView;
    public ProgressBar progressBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_farmer_crops);
        progressBar = (ProgressBar) findViewById(R.id.farmerCropsProgres);
        farmerCropListView = (ListView) findViewById(R.id.farmerCropListView);

        progressBar.setVisibility(View.GONE);

        if(Cache.CropRegionalCache.isEmpty()) {
            MobileServiceDataLayer.GetCrops(this);
        }
        else if(!Cache.FarmerCropUIArrayListCache.isEmpty())
        {
            this.farmerCropListView.setAdapter(new FarmerCropItemAdapter(this,android.R.layout.simple_list_item_1,Cache.FarmerCropUIArrayListCache));
        }

        farmerCropListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id)
            {
                Intent ICropDetailActivity = new Intent(parent.getContext(),CropDetail.class);
                FarmerCropUI farmerCropUI = (FarmerCropUI) farmerCropListView.getItemAtPosition(position);
                ICropDetailActivity.putExtra("farmerCropId",farmerCropUI.id);
                startActivity(ICropDetailActivity);
            }
        });
    }

    public void onAddCropClick(View view)
    {
        //MobileServiceDataLayer.GetCrops(this);
        Intent I = new Intent(this, AddCrop.class);
        startActivity(I);
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event)
    {
        if (keyCode == KeyEvent.KEYCODE_BACK)
        {
            moveTaskToBack(true);
            return true;
        }

        return super.onKeyDown(keyCode, event);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_farmer_crops, menu);
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
