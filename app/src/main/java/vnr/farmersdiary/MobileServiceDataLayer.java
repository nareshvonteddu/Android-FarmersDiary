package vnr.farmersdiary;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.view.View;

import com.microsoft.windowsazure.mobileservices.MobileServiceClient;
import com.microsoft.windowsazure.mobileservices.MobileServiceList;
import com.microsoft.windowsazure.mobileservices.http.ServiceFilterResponse;
import com.microsoft.windowsazure.mobileservices.table.MobileServiceTable;
import com.microsoft.windowsazure.mobileservices.table.TableOperationCallback;

import java.net.MalformedURLException;
import java.util.concurrent.ExecutionException;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Created by nvonteddu on 8/4/15.
 */
public final class MobileServiceDataLayer
{

    private static MobileServiceClient mClient;
    private MobileServiceDataLayer()
    {

    }

    public static void InstantiateService(Context context)
    {
        try
        {
            mClient = new MobileServiceClient("https://farmersdiary.azure-mobile.net/", "xVgekpbBEZuyzELjnfTKbXruSpJQNx96", context);
        }
        catch (MalformedURLException e)
        {
            e.printStackTrace();
        }
    }

    public static void CreateUser(final User user, final Context context) throws InterruptedException
    {
            mClient.getTable(User.class).insert(user, new TableOperationCallback<User>()
            {
                public void onCompleted(User entity, Exception exception, ServiceFilterResponse response)
                {
                    if (exception == null || exception.getMessage().contains("Error: Could not insert the item because an item with that id already exists.")) {
                        // Insert succeeded
                        SharedPreferences loginPreferences = context.getSharedPreferences(SPFStrings.SPFNAME.getValue(),
                                Context.MODE_PRIVATE);
                        SharedPreferences.Editor editor = loginPreferences.edit();
                        editor.putString(SPFStrings.PHONENUMBER.getValue(), user.PhoneNbr);
                        editor.commit();

                        String phoneNbr = loginPreferences.getString(SPFStrings.PHONENUMBER.getValue(), "");


                        Intent I = new Intent(context, farmerCrops.class);
                        context.startActivity(I);

                        //Logger.getAnonymousLogger().log(Level.ALL,phoneNbr);

                    } else {
                        // Insert failed
                            Logger.getAnonymousLogger().log(Level.ALL, exception.toString());
                    }
                }
            });
    }

    public static void GetCrops(final Context context)
    {

        ((AddCrop)context).progressBar.setVisibility(View.VISIBLE);
        new AsyncTask<Void, Void, Void>()
        {

            @Override
            protected Void doInBackground(Void... params)
            {
                try
                {
                    MobileServiceTable<CropRegional> table =  mClient.getTable(CropRegional.class);
                    final MobileServiceList<CropRegional> result = table.where().field("Language_Id").eq(1)
                            .select("Crop_Id", "Language_Id", "Value").execute().get();

                    ((AddCrop)context).runOnUiThread(new Runnable() {

                        @Override
                        public void run()
                        {
                            for (int i =0; i < result.toArray().length; i++)
                            {
                                ((AddCrop)context).CropsRegional.add((CropRegional) result.toArray()[i]);
                            }
                            ((AddCrop)context).cropSpinner.setAdapter(new AddCropSpinnerItemAdapter(((AddCrop) context), android.R.layout.simple_dropdown_item_1line, ((AddCrop) context).CropsRegional));
                            ((AddCrop)context).progressBar.setVisibility(View.GONE);
                        }
                    });
                }
                catch (InterruptedException e)
                {
                    e.printStackTrace();
                }
                catch (ExecutionException e)
                {
                    e.printStackTrace();
                }
                catch (Exception e)
                {
                    e.printStackTrace();
                }
                return null;
            }
        }.execute();




    }

}
