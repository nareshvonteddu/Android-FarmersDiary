package vnr.farmersdiary;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by nvonteddu on 8/13/15.
 */
public class FarmerCropItemAdapter extends ArrayAdapter<CropRegional>
{
    private ArrayList<CropRegional> farmerCrops;
    public FarmerCropItemAdapter(Context context, int resource, ArrayList<CropRegional> farmerCrops) {
        super(context, resource, farmerCrops);
        this.farmerCrops = farmerCrops;

    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent)
    {
        View v = convertView;
        if(v == null)
        {
            LayoutInflater vi = (LayoutInflater)this.getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            v = vi.inflate(R.layout.farmercroplistitem,null);
        }
        CropRegional farmerCrop = farmerCrops.get(position);
        if(farmerCrop != null)
        {
            TextView nameTextView = (TextView)v.findViewById(R.id.tvCropName);
            TextView idTextView = (TextView)v.findViewById(R.id.tvCropId);

            if(nameTextView != null) nameTextView.setText(farmerCrop.Value);
            if(idTextView != null) idTextView.setText(Integer.toString(farmerCrop.Crop_Id));
        }

        return v;
    }

}
