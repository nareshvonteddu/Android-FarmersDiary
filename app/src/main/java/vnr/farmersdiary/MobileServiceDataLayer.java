package vnr.farmersdiary;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.util.Pair;
import android.view.View;

import com.microsoft.windowsazure.mobileservices.MobileServiceClient;
import com.microsoft.windowsazure.mobileservices.MobileServiceList;
import com.microsoft.windowsazure.mobileservices.http.ServiceFilterResponse;
import com.microsoft.windowsazure.mobileservices.table.MobileServiceTable;
import com.microsoft.windowsazure.mobileservices.table.TableOperationCallback;

import java.net.MalformedURLException;
import java.util.ArrayList;
import java.util.List;
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
//            mClient.getTable(User.class).insert(user, new TableOperationCallback<User>()
//            {
//                public void onCompleted(User entity, Exception exception, ServiceFilterResponse response)
//                {
//                    if (exception == null || exception.getMessage().contains("Error: Could not insert the item because an item with that id already exists.")) {
//                        // Insert succeeded
//                        SharedPreferences loginPreferences = context.getSharedPreferences(SPFStrings.SPFNAME.getValue(),
//                                Context.MODE_PRIVATE);
//                        SharedPreferences.Editor editor = loginPreferences.edit();
//                        editor.putString(SPFStrings.PHONENUMBER.getValue(), user.PhoneNbr);
//                        editor.commit();
//
//                        String phoneNbr = loginPreferences.getString(SPFStrings.PHONENUMBER.getValue(), "");
//
//
//                        Intent I = new Intent(context, farmerCrops.class);
//                        context.startActivity(I);
//
//                        //Logger.getAnonymousLogger().log(Level.ALL,phoneNbr);
//
//                    } else {
//                        // Insert failed
//                            Logger.getAnonymousLogger().log(Level.ALL, exception.toString());
//                    }
//                }
//            });


        new AsyncTask<Void,Void,Void>()
        {

            @Override
            protected Void doInBackground(Void... params) {
                MobileServiceTable<User> tableUser = mClient.getTable(User.class);
                try {
                    tableUser.insert(user).get();
                }
                catch (Exception ex)
                {
                    ex.printStackTrace();
                }
                finally {
                    try {

                        final MobileServiceList<User> resultUsers = tableUser.where().field("PhoneNbr").eq(user.PhoneNbr)
                                .execute().get();
                        ((LoginActivity)context).runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                SharedPreferences loginPreferences = context.getSharedPreferences(SPFStrings.SPFNAME.getValue(),
                                Context.MODE_PRIVATE);
                                SharedPreferences.Editor editor = loginPreferences.edit();
                                editor.putString(SPFStrings.PHONENUMBER.getValue(), user.PhoneNbr);
                                editor.commit();
                                editor.putString(SPFStrings.USERID.getValue(), String.valueOf(((User) resultUsers.toArray()[0]).id));
                                editor.commit();
                               // String phoneNbr = loginPreferences.getString(SPFStrings.PHONENUMBER.getValue(), "");


                                Intent I = new Intent(context, farmerCrops.class);
                                context.startActivity(I);
                            }
                        });


                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    } catch (ExecutionException e) {
                        e.printStackTrace();
                    }
                }
                return null;
            }
        }.execute();
    }

    public static void GetCrops(final Context context)
    {

        ((farmerCrops)context).progressBar.setVisibility(View.VISIBLE);
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

                    SharedPreferences loginPreferences = context.getSharedPreferences(SPFStrings.SPFNAME.getValue(),
                            Context.MODE_PRIVATE);
                    String phoneNbr = loginPreferences.getString(SPFStrings.PHONENUMBER.getValue(), "");

                    MobileServiceTable<FarmerCrop> tableFarmerCrop = mClient.getTable(FarmerCrop.class);
                    final MobileServiceList<FarmerCrop> resultFarmerCrop = tableFarmerCrop.where().field("FarmerPhoneNbr").eq(phoneNbr).execute().get();

                        ((farmerCrops) context).runOnUiThread(new Runnable() {

                            @Override
                            public void run() {
                                for (int i = 0; i < result.toArray().length; i++) {
                                    //((AddCrop)context).CropsRegional.add((CropRegional) result.toArray()[i]);
                                    Cache.CropRegionalCache.add((CropRegional) result.toArray()[i]);
                                }
                                Cache.FarmerCropsCache.clear();
                                for (int i = 0; i < resultFarmerCrop.toArray().length; i++ )
                                {
                                    Cache.FarmerCropsCache.add((FarmerCrop) resultFarmerCrop.toArray()[i]);
                                }
                                Cache.FarmerCropUIArrayListCache.clear();
                                for (FarmerCrop farmerCrop : Cache.FarmerCropsCache)
                                {
                                    FarmerCropUI farmerCropUI = new FarmerCropUI();
                                    farmerCropUI.id = farmerCrop.id;
                                    farmerCropUI.Crop_Id = farmerCrop.Crop_Id;
                                    for(CropRegional cropRegional : Cache.CropRegionalCache)
                                    {
                                        if(cropRegional.Crop_Id == farmerCropUI.Crop_Id)
                                        {
                                            farmerCropUI.CropName = cropRegional.Value;
                                        }
                                    }
                                    farmerCropUI.Acres = farmerCrop.Acres;
                                    farmerCropUI.CropDate = farmerCrop.CropDate;
                                    farmerCropUI.FarmerPhoneNbr = farmerCrop.FarmerPhoneNbr;
                                    farmerCropUI.EstimateExpense = farmerCrop.EstimateExpense;
                                    farmerCropUI.EstimateYieldAmount = farmerCrop.EstimateYieldAmount;
                                    farmerCropUI.EstimateYieldDate = farmerCrop.EstimateYieldDate;
                                    farmerCropUI.IsYieldDone = farmerCrop.IsYieldDone;
                                    Cache.FarmerCropUIArrayListCache.add(farmerCropUI);
                                }
                                //((AddCrop)context).cropSpinner.setAdapter(new AddCropSpinnerItemAdapter(((AddCrop) context), android.R.layout.simple_dropdown_item_1line, Cache.CropRegionalCache));
                                ((farmerCrops)context).farmerCropListView.setAdapter(new FarmerCropItemAdapter(context,android.R.layout.simple_list_item_1,Cache.FarmerCropUIArrayListCache));
                                        ((farmerCrops) context).progressBar.setVisibility(View.GONE);
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

    public static void CreateFarmerCrop(final FarmerCrop farmerCrop, final AddCrop addCrop) {

        addCrop.progressBar.setVisibility(View.VISIBLE);

        new AsyncTask<Void, Void, Void>()
        {

            @Override
            protected Void doInBackground(Void... params) {
                MobileServiceTable<FarmerCrop> table = mClient.getTable(FarmerCrop.class);
                try {

                    table.insert(farmerCrop).get();

                    MobileServiceTable<FarmerCrop> tableFarmerCrop = mClient.getTable(FarmerCrop.class);
                    final MobileServiceList<FarmerCrop> resultFarmerCrop = tableFarmerCrop.where().field("FarmerPhoneNbr").eq(farmerCrop.FarmerPhoneNbr).execute().get();

                    addCrop.runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            addCrop.progressBar.setVisibility(View.GONE);
                            Cache.FarmerCropsCache.clear();
                            for (int i = 0; i < resultFarmerCrop.toArray().length; i++ )
                            {
                                Cache.FarmerCropsCache.add((FarmerCrop) resultFarmerCrop.toArray()[i]);
                            }
                            Cache.FarmerCropUIArrayListCache.clear();
                            for (FarmerCrop farmerCrop : Cache.FarmerCropsCache)
                            {
                                FarmerCropUI farmerCropUI = new FarmerCropUI();
                                farmerCropUI.id = farmerCrop.id;
                                farmerCropUI.Crop_Id = farmerCrop.Crop_Id;
                                for(CropRegional cropRegional : Cache.CropRegionalCache)
                                {
                                    if(cropRegional.Crop_Id == farmerCropUI.Crop_Id)
                                    {
                                        farmerCropUI.CropName = cropRegional.Value;
                                    }
                                }
                                farmerCropUI.Acres = farmerCrop.Acres;
                                farmerCropUI.CropDate = farmerCrop.CropDate;
                                farmerCropUI.FarmerPhoneNbr = farmerCrop.FarmerPhoneNbr;
                                farmerCropUI.EstimateExpense = farmerCrop.EstimateExpense;
                                farmerCropUI.EstimateYieldAmount = farmerCrop.EstimateYieldAmount;
                                farmerCropUI.EstimateYieldDate = farmerCrop.EstimateYieldDate;
                                farmerCropUI.IsYieldDone = farmerCrop.IsYieldDone;
                                Cache.FarmerCropUIArrayListCache.add(farmerCropUI);
                            }

                            Intent I = new Intent(addCrop, farmerCrops.class);
                            addCrop.startActivity(I);

                        }
                    });
                }
                catch (Exception ex)
                {
                    ex.printStackTrace();
                    addCrop.runOnUiThread(new Runnable() {
                        @Override
                        public void run() {

                            addCrop.progressBar.setVisibility(View.GONE);
                        }
                    });
                }
                return null;
            }
        }.execute();

    }
}
