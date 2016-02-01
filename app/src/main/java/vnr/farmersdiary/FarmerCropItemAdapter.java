package vnr.farmersdiary;

import android.annotation.TargetApi;
import android.content.Context;
import android.content.res.Resources;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.text.format.DateFormat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.GridLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by nvonteddu on 8/13/15.
 */
public class FarmerCropItemAdapter extends ArrayAdapter<FarmerCropUI>
{
    private ArrayList<FarmerCropUI> farmerCrops;
    public FarmerCropItemAdapter(Context context, int resource, ArrayList<FarmerCropUI> farmerCrops) {
        super(context, resource, farmerCrops);
        this.farmerCrops = farmerCrops;

    }

    @TargetApi(Build.VERSION_CODES.JELLY_BEAN)
    @Override
    public View getView(int position, View convertView, ViewGroup parent)
    {
        View v = convertView;
        if(v == null)
        {
            LayoutInflater vi = (LayoutInflater)this.getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            v = vi.inflate(R.layout.farmercroplistitem,null);
        }
        FarmerCrop farmerCropDataItem = null;
        FarmerCropUI farmerCrop = farmerCrops.get(position);

        if(farmerCrop != null)
        {
            TextView nameTextView = (TextView)v.findViewById(R.id.tvCropName);
            TextView acresTextView = (TextView) v.findViewById(R.id.tvCropAcres);
            GridLayout layout = (GridLayout) v.findViewById(R.id.farmerListViewItemLayout);
            TextView plantedDatetextView = (TextView) v.findViewById(R.id.tvPlantedDate);
            ImageView thumbImageView = (ImageView) v.findViewById(R.id.thumbImage);

            for (int i = 0; i <= Cache.FarmerCropsCache.toArray().length; i++)
            {
                if(farmerCrop.id.equals(Cache.FarmerCropsCache.get(i).id))
                {
                    farmerCropDataItem = Cache.FarmerCropsCache.get(i);
                    break;
                }
            }

            if(farmerCropDataItem != null && farmerCropDataItem.IsYieldDone && (farmerCropDataItem.ActualIncome - farmerCropDataItem.ActualInvestment) > 0)
            {
                thumbImageView.setImageDrawable(getContext().getResources().getDrawable(R.drawable.ic_thumb_up_black_24dp));
            }
            else if(farmerCropDataItem != null && farmerCropDataItem.IsYieldDone && (farmerCropDataItem.ActualIncome - farmerCropDataItem.ActualInvestment) <= 0)
            {
                thumbImageView.setImageDrawable(getContext().getResources().getDrawable(R.drawable.ic_thumb_down_black_24dp));
            }
            else
            {
                thumbImageView.setVisibility(View.INVISIBLE);
            }

            if(nameTextView != null) nameTextView.setText(farmerCrop.CropName);
            if(acresTextView != null) acresTextView.setText(Double.toString(farmerCrop.Acres));
            if(plantedDatetextView != null) plantedDatetextView.setText(DateFormat.format("dd/MM/yyyy", farmerCrop.CropDate).toString());
            if(!farmerCrop.IsYieldDone)
            {
                Resources res = getContext().getResources();
                Drawable background = res.getDrawable(R.drawable.farmer_crop_listitem_notdone_background);
                layout.setBackground(background);
            }
            //if(idTextView != null) idTextView.setText(Integer.toString(farmerCrop.Crop_Id));
        }

        return v;
    }

}
