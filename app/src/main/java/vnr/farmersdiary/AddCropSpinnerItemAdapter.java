package vnr.farmersdiary;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import java.util.ArrayList;

/**
 * Created by nvonteddu on 8/13/15.
 */
public class AddCropSpinnerItemAdapter extends ArrayAdapter<CropRegional>
{
    private ArrayList<CropRegional> farmerCrops;
    public AddCropSpinnerItemAdapter(Context context, int resource, ArrayList<CropRegional> farmerCrops) {
        super(context, resource, farmerCrops);
        this.farmerCrops = farmerCrops;

    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent)
    {
        return getCustomView(position, convertView, parent);
    }

    @Override
    public View getDropDownView(int position, View convertView, ViewGroup parent)
    {
        return getCustomView(position, convertView, parent);
    }



    private View getCustomView(int position, View convertView, ViewGroup parent) {
        View v = convertView;
        if(v == null)
        {
            LayoutInflater vi = (LayoutInflater)this.getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            v = vi.inflate(R.layout.add_crop_spinner_item,parent,false);
        }
        CropRegional farmerCrop = farmerCrops.get(position);
        if(farmerCrop != null)
        {
            TextView nameTextView = (TextView)v.findViewById(R.id.spTvCropName);

            if(nameTextView != null) nameTextView.setText(farmerCrop.Value);
        }

        return v;
    }
}
