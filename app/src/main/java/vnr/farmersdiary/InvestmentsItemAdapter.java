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
import android.widget.TextView;

import java.util.ArrayList;

/**
 * Created by nvonteddu on 9/21/15.
 */
public class InvestmentsItemAdapter extends ArrayAdapter<Investment>
{
    private ArrayList<Investment> investments;
    public InvestmentsItemAdapter(Context context, int resource, ArrayList<Investment> investments) {
        super(context, resource, investments);
        this.investments = investments;

    }

    @TargetApi(Build.VERSION_CODES.JELLY_BEAN)
    @Override
    public View getView(int position, View convertView, ViewGroup parent)
    {
        View v = convertView;
        if(v == null)
        {
            LayoutInflater vi = (LayoutInflater)this.getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            v = vi.inflate(R.layout.investmentlistitem,null);
        }
        Investment investment = investments.get(position);
        if(investment != null)
        {
            TextView investmentTypeTextView = (TextView)v.findViewById(R.id.investmentTypeText);
            TextView investmentAmountTextView = (TextView) v.findViewById(R.id.investmentAmountText);

            if(investmentTypeTextView != null) investmentTypeTextView.setText(investment.InvestmentType);
            if(investmentAmountTextView != null) investmentAmountTextView.setText(MainActivity.formatter.format(investment.Amount));
        }

        return v;
    }
}
