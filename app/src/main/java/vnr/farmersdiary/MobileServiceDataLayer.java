package vnr.farmersdiary;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.view.View;

import com.microsoft.windowsazure.mobileservices.MobileServiceClient;
import com.microsoft.windowsazure.mobileservices.MobileServiceList;
import com.microsoft.windowsazure.mobileservices.table.MobileServiceTable;
import com.microsoft.windowsazure.mobileservices.table.query.Query;
import com.microsoft.windowsazure.mobileservices.table.query.QueryOrder;
import com.microsoft.windowsazure.mobileservices.table.sync.MobileServiceSyncContext;
import com.microsoft.windowsazure.mobileservices.table.sync.MobileServiceSyncTable;
import com.microsoft.windowsazure.mobileservices.table.sync.localstore.ColumnDataType;
import com.microsoft.windowsazure.mobileservices.table.sync.localstore.MobileServiceLocalStoreException;
import com.microsoft.windowsazure.mobileservices.table.sync.localstore.SQLiteLocalStore;
import com.microsoft.windowsazure.mobileservices.table.sync.synchandler.SimpleSyncHandler;

import java.net.MalformedURLException;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ExecutionException;

/**
 * Created by nvonteddu on 8/4/15.
 */
public final class MobileServiceDataLayer
{

    private static MobileServiceClient mClient;
    private static MobileServiceSyncTable<Crop> cropsTable;
    private static MobileServiceSyncTable<CropRegional> cropsRegionalTable;
    private static Query mPullCropsQuery;
    private static SQLiteLocalStore localStore;
    private static SimpleSyncHandler handler;
    private static MobileServiceSyncContext syncContext;
    private static MobileServiceSyncTable<FarmerCrop> tableFarmerCrop;
    private static Query mPullFarmerCropsQuery;
    private static MobileServiceSyncTable<Investment> investmentTable;
    private static Query mPullAllInvestmentsQuery;

    private MobileServiceDataLayer()
    {

    }

    public static void InstantiateService(Context context)
    {
        try
        {
            mClient = new MobileServiceClient("https://farmersdiary.azure-mobile.net/", "xVgekpbBEZuyzELjnfTKbXruSpJQNx96", context);

            localStore = new SQLiteLocalStore(mClient.getContext(), "LocalFarmersDiaryDB",null,1);
            handler = new SimpleSyncHandler();
            syncContext = mClient.getSyncContext();

            ConstructGetCropsQuery(context);
            ConstructGetFarmerCropsQuery(context);
            ConstructGetAllInvestmentsQuery(context);

            DefineCropsTable();
            DefineCropsRegionalTable();
            DefineFarmerCropsTable();
            DefineInvestmentTable();

            syncContext.initialize(localStore,handler).get();
            cropsTable = mClient.getSyncTable(Crop.class);
            cropsRegionalTable = mClient.getSyncTable(CropRegional.class);
            tableFarmerCrop = mClient.getSyncTable(FarmerCrop.class);
            investmentTable = mClient.getSyncTable(Investment.class);

            SharedPreferences loginPreferences = context.getSharedPreferences(SPFStrings.SPFNAME.getValue(),
                    Context.MODE_PRIVATE);
            String syncOnStart = loginPreferences.getString(SPFStrings.SYNCONSTART.getValue(), "");
            if(!syncOnStart.equals(String.valueOf(false))
                    && !loginPreferences.getString(SPFStrings.PHONENUMBER.getValue(), "").equals(""))
            {
                syncDBChanges(context);
            }
        }
        catch (MalformedURLException e)
        {
            e.printStackTrace();
        } catch (Exception e) {
            Throwable t = e;
            while (t.getCause() != null) {
                t = t.getCause();
            }
            //createAndShowDialog(new Exception("Unknown error: " + t.getMessage()), "Error");
        }
    }

    private static void ConstructGetFarmerCropsQuery(Context context) {
        SharedPreferences loginPreferences = context.getSharedPreferences(SPFStrings.SPFNAME.getValue(),
                Context.MODE_PRIVATE);
        String phoneNbr = loginPreferences.getString(SPFStrings.PHONENUMBER.getValue(), "");
        mPullFarmerCropsQuery = mClient.getTable(FarmerCrop.class).where().field("FarmerPhoneNbr").eq(phoneNbr);
    }

    private static void ConstructGetAllInvestmentsQuery(Context context)
    {
        SharedPreferences loginPreferences = context.getSharedPreferences(SPFStrings.SPFNAME.getValue(),
                Context.MODE_PRIVATE);
        String phoneNbr = loginPreferences.getString(SPFStrings.PHONENUMBER.getValue(), "");
        mPullAllInvestmentsQuery = mClient.getTable(Investment.class).where().field("FarmerPhoneNbr").eq(phoneNbr)
                .select("id", "Amount", "InvestmentType", "InvestmentDate", "FarmerCropId", "FarmerPhoneNbr","investmentid");
    }

    private static void DefineCropsTable() throws MobileServiceLocalStoreException
    {
        Map<String, ColumnDataType> tableDefinition = new HashMap<String, ColumnDataType>();
        tableDefinition.put("id", ColumnDataType.String);
        tableDefinition.put("Crop_Id", ColumnDataType.Integer);
        tableDefinition.put("Value", ColumnDataType.String);

        localStore.defineTable("Crop", tableDefinition);
    }

    private static void DefineCropsRegionalTable() throws MobileServiceLocalStoreException
    {
        Map<String, ColumnDataType> tableDefinition = new HashMap<String, ColumnDataType>();
        tableDefinition.put("id", ColumnDataType.String);
        tableDefinition.put("Crop_Id", ColumnDataType.Integer);
        tableDefinition.put("Value", ColumnDataType.String);
        tableDefinition.put("Language_Code", ColumnDataType.String);

        localStore.defineTable("CropRegional", tableDefinition);
    }

    private static void DefineFarmerCropsTable() throws MobileServiceLocalStoreException
    {
        Map<String, ColumnDataType> tableDefinition = new HashMap<String, ColumnDataType>();
        tableDefinition.put("id", ColumnDataType.String);
        tableDefinition.put("Crop_Id", ColumnDataType.Integer);
        tableDefinition.put("Acres", ColumnDataType.Other);
        tableDefinition.put("CropDate", ColumnDataType.Date);
        tableDefinition.put("FarmerPhoneNbr", ColumnDataType.String);
        tableDefinition.put("EstimateYieldAmount",ColumnDataType.Other);
        tableDefinition.put("EstimateYieldDate",ColumnDataType.Date);
        tableDefinition.put("EstimateInvestment", ColumnDataType.Other);
        tableDefinition.put("IsYieldDone", ColumnDataType.Boolean);
        tableDefinition.put("EstimateYieldUnitIndex", ColumnDataType.Integer);
        tableDefinition.put("EstimatePrice", ColumnDataType.Other);
        tableDefinition.put("ActualYieldAmount", ColumnDataType.Other);
        tableDefinition.put("ActualYieldDate", ColumnDataType.Date);
        tableDefinition.put("ActualInvestment", ColumnDataType.Other);
        tableDefinition.put("ActualPrice", ColumnDataType.Other);
        tableDefinition.put("EstimateIncome", ColumnDataType.Other);
        tableDefinition.put("ActualYieldUnitIndex", ColumnDataType.Integer);
        tableDefinition.put("ActualIncome", ColumnDataType.Other);

        localStore.defineTable("FarmerCrop", tableDefinition);
    }

    private static void DefineInvestmentTable() throws MobileServiceLocalStoreException
    {
        Map<String, ColumnDataType> tableDefinition = new HashMap<String, ColumnDataType>();
        tableDefinition.put("id", ColumnDataType.String);
        tableDefinition.put("Amount", ColumnDataType.Other);
        tableDefinition.put("InvestmentType", ColumnDataType.String);
        tableDefinition.put("InvestmentDate", ColumnDataType.Date);
        tableDefinition.put("FarmerCropId", ColumnDataType.String);
        tableDefinition.put("FarmerPhoneNbr",ColumnDataType.String);
        tableDefinition.put("investmentid", ColumnDataType.Integer);

        localStore.defineTable("Investment", tableDefinition);
    }

    private static void ConstructGetCropsQuery(Context context)
    {
        SharedPreferences loginPreferences = context.getSharedPreferences(SPFStrings.SPFNAME.getValue(),
                Context.MODE_PRIVATE);
        String languageCode = loginPreferences.getString(SPFStrings.LANGUAGE.getValue(), "");
        if(languageCode.equals(""))
        {
            mPullCropsQuery = mClient.getTable(Crop.class).where().select("Crop_Id", "Value").orderBy("Crop_Id", QueryOrder.Ascending);
        }
        else
        {
            mPullCropsQuery = mClient.getTable(CropRegional.class).where().field("Language_Code").eq(languageCode)
                    .select("Crop_Id", "Value").orderBy("Crop_Id", QueryOrder.Ascending);
        }
    }

    private static boolean isNetworkAvailable(Context context) {
        ConnectivityManager connectivityManager
                = (ConnectivityManager)context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
        return activeNetworkInfo != null && activeNetworkInfo.isConnected();
    }

    public static void syncDBChanges(final Context context)
    {
        if (isNetworkAvailable(context))
        {
            SharedPreferences loginPreferences = context.getSharedPreferences(SPFStrings.SPFNAME.getValue(),
                    Context.MODE_PRIVATE);
            SharedPreferences.Editor editor = loginPreferences.edit();
            editor.putString(SPFStrings.SYNCONSTART.getValue(), String.valueOf(false));
            editor.commit();
            new AsyncTask<Void, Void, Void>() {

                @Override
                protected Void doInBackground(Void... params) {
                    try
                    {

                        mClient.getSyncContext().push().get();
                        SharedPreferences loginPreferences = context.getSharedPreferences(SPFStrings.SPFNAME.getValue(),
                                Context.MODE_PRIVATE);
                        String languageCode = loginPreferences.getString(SPFStrings.LANGUAGE.getValue(), "");
                        ConstructGetCropsQuery(context);
                        if(languageCode.equals(""))
                        {
                            cropsTable.pull(mPullCropsQuery).get();
                        }
                        else
                        {
                            cropsRegionalTable.pull(mPullCropsQuery).get();
                        }

                        ConstructGetFarmerCropsQuery(context);
                        tableFarmerCrop.pull(mPullFarmerCropsQuery).get();

                        ConstructGetAllInvestmentsQuery(context);
                        investmentTable.pull(mPullAllInvestmentsQuery).get();

                    } catch (Exception exception)
                    {
//                        createAndShowDialog(exception, "Error");
                        exception.printStackTrace();
                    }
                    return null;
                }
            }.execute();
        }
        else
        {
            SharedPreferences loginPreferences = context.getSharedPreferences(SPFStrings.SPFNAME.getValue(),
                    Context.MODE_PRIVATE);
            SharedPreferences.Editor editor = loginPreferences.edit();
            editor.putString(SPFStrings.SYNCONSTART.getValue(), String.valueOf(true));
            editor.commit();
//            Toast.makeText(this, "You are not online, re-sync later!" +
//                    "", Toast.LENGTH_LONG).show();
        }
    }

    public static void CreateUser(final User user, final Context context) throws InterruptedException
    {
        ((LoginActivity)context).loginProgressBar.setVisibility(View.VISIBLE);
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

                                ((LoginActivity)context).loginProgressBar.setVisibility(View.GONE);

                                syncDBChanges(context);

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
        ((farmerCrops)context).farmerCropListView.setVisibility(View.GONE);
        new AsyncTask<Void, Void, Void>()
        {

            @Override
            protected Void doInBackground(Void... params)
            {
                try
                {
                    syncDBChanges(context);
                    SharedPreferences loginPreferences = context.getSharedPreferences(SPFStrings.SPFNAME.getValue(),
                            Context.MODE_PRIVATE);
                    String languageCode = loginPreferences.getString(SPFStrings.LANGUAGE.getValue(), "");
                     MobileServiceList<CropRegional> resultCropRegional = null;
                     MobileServiceList<Crop> resultCrop = null;
                    ConstructGetCropsQuery(context);
                    if(languageCode.equals(""))
                    {
                       // MobileServiceTable<Crop> table =  mClient.getTable(Crop.class);
                       // cropsTable.pull(mPullCropsQuery).get();
                        resultCrop = cropsTable.read(mPullCropsQuery).get();
                    }
                    else
                    {
                       // MobileServiceTable<CropRegional> table = mClient.getTable(CropRegional.class);
                       // cropsRegionalTable.pull(mPullCropsQuery).get();
                        resultCropRegional = cropsRegionalTable.read(mPullCropsQuery).get();
                    }


                    //MobileServiceTable<FarmerCrop> tableFarmerCrop = mClient.getTable(FarmerCrop.class);
                    ConstructGetFarmerCropsQuery(context);
                    final MobileServiceList<FarmerCrop> resultFarmerCrop = tableFarmerCrop.read(mPullFarmerCropsQuery).get();

                    final MobileServiceList<Crop> finalResultCrop = resultCrop;
                    final MobileServiceList<CropRegional> finalResultCropRegional = resultCropRegional;



                    ((farmerCrops) context).runOnUiThread(new Runnable() {


                        @Override
                        public void run() {

                            if (finalResultCrop != null)
                            {
                                Cache.CropRegionalCache.clear();
                                for (int i = 0; i < finalResultCrop.toArray().length; i++)
                                {
                                    CropRegional cropRegional = new CropRegional();
                                    cropRegional.Id = ((Crop) finalResultCrop.toArray()[i]).Id;
                                    cropRegional.Crop_Id = ((Crop) finalResultCrop.toArray()[i]).Crop_Id;
                                    cropRegional.Value = ((Crop) finalResultCrop.toArray()[i]).Value;
                                    Cache.CropRegionalCache.add(cropRegional);
                                }
                            }
                            else if (finalResultCropRegional != null)
                            {
                                Cache.CropRegionalCache.clear();
                                for (int i = 0; i < finalResultCropRegional.toArray().length; i++)
                                {
                                    Cache.CropRegionalCache.add((CropRegional) finalResultCropRegional.toArray()[i]);
                                }
                            }

                            Cache.FarmerCropsCache.clear();
                            for (int i = 0; i < resultFarmerCrop.toArray().length; i++) {
                                Cache.FarmerCropsCache.add((FarmerCrop) resultFarmerCrop.toArray()[i]);
                            }
                            Cache.FarmerCropUIArrayListCache.clear();
                            for (FarmerCrop farmerCrop : Cache.FarmerCropsCache) {
                                FarmerCropUI farmerCropUI = new FarmerCropUI();
                                farmerCropUI.id = farmerCrop.id;
                                farmerCropUI.Crop_Id = farmerCrop.Crop_Id;
                                for (CropRegional cropRegional : Cache.CropRegionalCache) {
                                    if (cropRegional.Crop_Id == farmerCropUI.Crop_Id) {
                                        farmerCropUI.CropName = cropRegional.Value;
                                    }
                                }
                                farmerCropUI.Acres = farmerCrop.Acres;
                                farmerCropUI.CropDate = farmerCrop.CropDate;
                                farmerCropUI.FarmerPhoneNbr = farmerCrop.FarmerPhoneNbr;
                                farmerCropUI.EstimateExpense = farmerCrop.EstimateInvestment;
                                farmerCropUI.EstimateYieldAmount = farmerCrop.EstimateYieldAmount;
                                farmerCropUI.EstimateYieldDate = farmerCrop.EstimateYieldDate;
                                farmerCropUI.IsYieldDone = farmerCrop.IsYieldDone;
                                Cache.FarmerCropUIArrayListCache.add(farmerCropUI);
                                Collections.sort(Cache.FarmerCropUIArrayListCache, new Comparator<FarmerCropUI>() {
                                    @Override
                                    public int compare(FarmerCropUI lhs, FarmerCropUI rhs) {

                                        return lhs.CropDate.compareTo(rhs.CropDate);
                                    }
                                });

                            }
                            //((AddCrop)context).cropSpinner.setAdapter(new AddCropSpinnerItemAdapter(((AddCrop) context), android.R.layout.simple_dropdown_item_1line, Cache.CropRegionalCache));
                            ((farmerCrops) context).farmerCropListView.setAdapter(new FarmerCropItemAdapter(context, android.R.layout.simple_list_item_1, Cache.FarmerCropUIArrayListCache));
                            ((farmerCrops) context).progressBar.setVisibility(View.GONE);
                            ((farmerCrops) context).farmerCropListView.setVisibility(View.VISIBLE);
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
                try {

                    tableFarmerCrop.insert(farmerCrop).get();

                    syncDBChanges(addCrop);

                    Query query = mClient.getTable(FarmerCrop.class).where().field("FarmerPhoneNbr").eq(farmerCrop.FarmerPhoneNbr);
                    final MobileServiceList<FarmerCrop> resultFarmerCrop = tableFarmerCrop.read(query).get();

                            //final MobileServiceList<FarmerCrop> resultFarmerCrop = tableFarmerCrop.where().field("FarmerPhoneNbr").eq(farmerCrop.FarmerPhoneNbr).execute().get();

                    addCrop.runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            addCrop.progressBar.setVisibility(View.GONE);
                            Cache.FarmerCropsCache.clear();
                            for (int i = 0; i < resultFarmerCrop.toArray().length; i++) {
                                Cache.FarmerCropsCache.add((FarmerCrop) resultFarmerCrop.toArray()[i]);
                            }
                            Cache.FarmerCropUIArrayListCache.clear();
                            for (FarmerCrop farmerCrop : Cache.FarmerCropsCache) {
                                FarmerCropUI farmerCropUI = new FarmerCropUI();
                                farmerCropUI.id = farmerCrop.id;
                                farmerCropUI.Crop_Id = farmerCrop.Crop_Id;
                                for (CropRegional cropRegional : Cache.CropRegionalCache) {
                                    if (cropRegional.Crop_Id == farmerCropUI.Crop_Id) {
                                        farmerCropUI.CropName = cropRegional.Value;
                                    }
                                }
                                farmerCropUI.Acres = farmerCrop.Acres;
                                farmerCropUI.CropDate = farmerCrop.CropDate;
                                farmerCropUI.FarmerPhoneNbr = farmerCrop.FarmerPhoneNbr;
                                farmerCropUI.EstimateExpense = farmerCrop.EstimateInvestment;
                                farmerCropUI.EstimateYieldAmount = farmerCrop.EstimateYieldAmount;
                                farmerCropUI.EstimateYieldDate = farmerCrop.EstimateYieldDate;
                                farmerCropUI.IsYieldDone = farmerCrop.IsYieldDone;
                                Cache.FarmerCropUIArrayListCache.add(farmerCropUI);
                                Collections.sort(Cache.FarmerCropUIArrayListCache, new Comparator<FarmerCropUI>() {
                                    @Override
                                    public int compare(FarmerCropUI lhs, FarmerCropUI rhs) {

                                        return lhs.CropDate.compareTo(rhs.CropDate);
                                    }
                                });
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

    public static void SaveActuals(final FarmerCrop farmerCrop, final Actuals actuals) {

        //addCrop.progressBar.setVisibility(View.VISIBLE);

        new AsyncTask<Void, Void, Void>()
        {

            @Override
            protected Void doInBackground(Void... params) {
                try {

                    tableFarmerCrop.update(farmerCrop).get();

                    syncDBChanges(actuals);

                    Query query = mClient.getTable(FarmerCrop.class).where().field("FarmerPhoneNbr").eq(farmerCrop.FarmerPhoneNbr);
                    final MobileServiceList<FarmerCrop> resultFarmerCrop = tableFarmerCrop.read(query).get();

                    //final MobileServiceList<FarmerCrop> resultFarmerCrop = tableFarmerCrop.where().field("FarmerPhoneNbr").eq(farmerCrop.FarmerPhoneNbr).execute().get();

                    actuals.runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            //addCrop.progressBar.setVisibility(View.GONE);
                            Cache.FarmerCropsCache.clear();
                            for (int i = 0; i < resultFarmerCrop.toArray().length; i++) {
                                Cache.FarmerCropsCache.add((FarmerCrop) resultFarmerCrop.toArray()[i]);
                            }
                            Cache.FarmerCropUIArrayListCache.clear();
                            for (FarmerCrop farmerCrop : Cache.FarmerCropsCache) {
                                FarmerCropUI farmerCropUI = new FarmerCropUI();
                                farmerCropUI.id = farmerCrop.id;
                                farmerCropUI.Crop_Id = farmerCrop.Crop_Id;
                                for (CropRegional cropRegional : Cache.CropRegionalCache) {
                                    if (cropRegional.Crop_Id == farmerCropUI.Crop_Id) {
                                        farmerCropUI.CropName = cropRegional.Value;
                                    }
                                }
                                farmerCropUI.Acres = farmerCrop.Acres;
                                farmerCropUI.CropDate = farmerCrop.CropDate;
                                farmerCropUI.FarmerPhoneNbr = farmerCrop.FarmerPhoneNbr;
                                farmerCropUI.EstimateExpense = farmerCrop.EstimateInvestment;
                                farmerCropUI.EstimateYieldAmount = farmerCrop.EstimateYieldAmount;
                                farmerCropUI.EstimateYieldDate = farmerCrop.EstimateYieldDate;
                                farmerCropUI.IsYieldDone = farmerCrop.IsYieldDone;
                                Cache.FarmerCropUIArrayListCache.add(farmerCropUI);
                                Collections.sort(Cache.FarmerCropUIArrayListCache, new Comparator<FarmerCropUI>() {
                                    @Override
                                    public int compare(FarmerCropUI lhs, FarmerCropUI rhs) {

                                        return lhs.CropDate.compareTo(rhs.CropDate);
                                    }
                                });
                            }

                            actuals.finish();
                        }
                    });
                }
                catch (Exception ex)
                {
                    ex.printStackTrace();
                    actuals.runOnUiThread(new Runnable() {
                        @Override
                        public void run() {

                           // addCrop.progressBar.setVisibility(View.GONE);
                        }
                    });
                }
                return null;
            }
        }.execute();

    }

    public static void UpdateFarmerCrop(final FarmerCrop farmerCrop, final AddCrop addCrop) {

        addCrop.progressBar.setVisibility(View.VISIBLE);

        new AsyncTask<Void, Void, Void>()
        {

            @Override
            protected Void doInBackground(Void... params) {
                try {

                    tableFarmerCrop.update(farmerCrop).get();

                    syncDBChanges(addCrop);

                    Query query = mClient.getTable(FarmerCrop.class).where().field("FarmerPhoneNbr").eq(farmerCrop.FarmerPhoneNbr);
                    final MobileServiceList<FarmerCrop> resultFarmerCrop = tableFarmerCrop.read(query).get();

                    //final MobileServiceList<FarmerCrop> resultFarmerCrop = tableFarmerCrop.where().field("FarmerPhoneNbr").eq(farmerCrop.FarmerPhoneNbr).execute().get();

                    addCrop.runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            addCrop.progressBar.setVisibility(View.GONE);
                            Cache.FarmerCropsCache.clear();
                            for (int i = 0; i < resultFarmerCrop.toArray().length; i++) {
                                Cache.FarmerCropsCache.add((FarmerCrop) resultFarmerCrop.toArray()[i]);
                            }
                            Cache.FarmerCropUIArrayListCache.clear();
                            for (FarmerCrop farmerCrop : Cache.FarmerCropsCache) {
                                FarmerCropUI farmerCropUI = new FarmerCropUI();
                                farmerCropUI.id = farmerCrop.id;
                                farmerCropUI.Crop_Id = farmerCrop.Crop_Id;
                                for (CropRegional cropRegional : Cache.CropRegionalCache) {
                                    if (cropRegional.Crop_Id == farmerCropUI.Crop_Id) {
                                        farmerCropUI.CropName = cropRegional.Value;
                                    }
                                }
                                farmerCropUI.Acres = farmerCrop.Acres;
                                farmerCropUI.CropDate = farmerCrop.CropDate;
                                farmerCropUI.FarmerPhoneNbr = farmerCrop.FarmerPhoneNbr;
                                farmerCropUI.EstimateExpense = farmerCrop.EstimateInvestment;
                                farmerCropUI.EstimateYieldAmount = farmerCrop.EstimateYieldAmount;
                                farmerCropUI.EstimateYieldDate = farmerCrop.EstimateYieldDate;
                                farmerCropUI.IsYieldDone = farmerCrop.IsYieldDone;
                                Cache.FarmerCropUIArrayListCache.add(farmerCropUI);
                                Collections.sort(Cache.FarmerCropUIArrayListCache, new Comparator<FarmerCropUI>() {
                                    @Override
                                    public int compare(FarmerCropUI lhs, FarmerCropUI rhs) {

                                        return lhs.CropDate.compareTo(rhs.CropDate);
                                    }
                                });
                            }

                            addCrop.finish();
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

    public static void GetCropInvestments(final Context context)
    {
        final String farmerCropId = ((CropDetail)context).farmerCropId;
        ((CropDetail)context).CropDetailProgressBar.setVisibility(View.VISIBLE);
        new AsyncTask<Void, Void, Void>()
        {

            @Override
            protected Void doInBackground(Void... params)
            {
                try
                {
                    //MobileServiceTable<Investment> table =  mClient.getTable(Investment.class);
                    final MobileServiceList<Investment> result = investmentTable.read(mPullAllInvestmentsQuery).get();

                    ((CropDetail) context).runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            Cache.InvestmentsCache.clear();
                            double totalAmount = 0;

                            for (int i = 0; i < result.toArray().length; i++)
                            {
                                if(((Investment) result.toArray()[i]).FarmerCropId.equals(farmerCropId))
                                {
                                    Cache.InvestmentsCache.add((Investment) result.toArray()[i]);
                                    totalAmount += ((Investment) result.toArray()[i]).Amount;
                                }
                            }
                            for (int i = 0; i < Cache.FarmerCropsCache.toArray().length; i++)
                            {
                                if(((FarmerCrop)Cache.FarmerCropsCache.toArray()[i]).id.equals(farmerCropId))
                                {
                                   ((FarmerCrop)Cache.FarmerCropsCache.toArray()[i]).ActualInvestment = totalAmount;
                                    break;
                                }
                            }
                            ((CropDetail) context). TotalAmountTextView.setText(MainActivity.currencyFormatter.format(totalAmount));
                            ((CropDetail) context).DrawNetIncomeGraph();
                            ((CropDetail) context).CropDetailProgressBar.setVisibility(View.GONE);
                        }
                    });
                }
                catch (Exception e)
                {
                    e.printStackTrace();
                    ((CropDetail) context).runOnUiThread(new Runnable() {
                        @Override
                        public void run() {

                            ((CropDetail) context).CropDetailProgressBar.setVisibility(View.GONE);
                        }
                    });
                }
                return null;
            }
        }.execute();


    }

    public static void InsertInvestment(final Investment investment, final Context context)
    {
        final String farmerCropId = ((InvestmentsDetail)context).farmerCropId;
        ((InvestmentsDetail)context).investmentsProgressBar.setVisibility(View.VISIBLE);
        new AsyncTask<Void, Void, Void>()
        {

            @Override
            protected Void doInBackground(Void... params) {
                try
                {

                    investmentTable.insert(investment).get();

                    syncDBChanges(context);

                    final MobileServiceList<Investment> result = investmentTable.read(mPullAllInvestmentsQuery).get();

                    ((InvestmentsDetail) context).runOnUiThread(new Runnable() {


                        @Override
                        public void run() {
                            Cache.InvestmentsCache.clear();
                            double totalAmount = 0;

                            for (int i = 0; i < result.toArray().length; i++)
                            {
                                if(((Investment) result.toArray()[i]).FarmerCropId.equals(farmerCropId))
                                {
                                    Cache.InvestmentsCache.add((Investment) result.toArray()[i]);
                                    totalAmount += ((Investment) result.toArray()[i]).Amount;
                                }
                            }

                            ((InvestmentsDetail) context).totalAmounttextView.setText(MainActivity.currencyFormatter.format(totalAmount));
                            for (int i = 0; i < Cache.FarmerCropsCache.toArray().length; i++)
                            {
                                if(((FarmerCrop)Cache.FarmerCropsCache.toArray()[i]).id.equals(farmerCropId))
                                {
                                    ((FarmerCrop)Cache.FarmerCropsCache.toArray()[i]).ActualInvestment = totalAmount;
                                    break;
                                }
                            }
                            if(!Cache.InvestmentsCache.isEmpty())
                            {
                                ((InvestmentsDetail) context).investmentListView.setAdapter(new InvestmentsItemAdapter(context, android.R.layout.simple_list_item_1, Cache.InvestmentsCache));
                            }
                            ((InvestmentsDetail) context).investmentsProgressBar.setVisibility(View.GONE);
                        }
                    });
                }
                catch (Exception ex)
                {
                    ex.printStackTrace();
                    ((InvestmentsDetail)context).runOnUiThread(new Runnable() {
                        @Override
                        public void run() {

                            ((InvestmentsDetail)context).investmentsProgressBar.setVisibility(View.GONE);
                        }
                    });
                }
                return null;
            }
        }.execute();

    }

    public static void UpdateInvestment(final Investment investment, final Context context)
    {
        final String farmerCropId = ((InvestmentsDetail)context).farmerCropId;
        ((InvestmentsDetail) context).investmentsProgressBar.setVisibility(View.VISIBLE);
        new AsyncTask<Void, Void, Void>()
        {

            @Override
            protected Void doInBackground(Void... params)
            {
                try {

                    investmentTable.update(investment).get();

                    syncDBChanges(context);

                    final MobileServiceList<Investment> result = investmentTable.read(mPullAllInvestmentsQuery).get();


                    ((InvestmentsDetail) context).runOnUiThread(new Runnable() {


                        @Override
                        public void run() {
                            double totalAmount = 0;

                            for (int i = 0; i < result.toArray().length; i++)
                            {
                                if(((Investment) result.toArray()[i]).FarmerCropId.equals(farmerCropId))
                                {
                                    totalAmount += ((Investment)result.toArray()[i]).Amount;
                                }
                            }
                            for (int i = 0; i < Cache.FarmerCropsCache.toArray().length; i++)
                            {
                                if(((FarmerCrop)Cache.FarmerCropsCache.toArray()[i]).id.equals(((InvestmentsDetail) context).farmerCropId))
                                {
                                    ((FarmerCrop)Cache.FarmerCropsCache.toArray()[i]).ActualInvestment = totalAmount;
                                    break;
                                }
                            }
                            if(!Cache.InvestmentsCache.isEmpty())
                            {
                                ((InvestmentsDetail) context).investmentListView.setAdapter(new InvestmentsItemAdapter(context, android.R.layout.simple_list_item_1, Cache.InvestmentsCache));
                            }

                            ((InvestmentsDetail) context).totalAmounttextView.setText(MainActivity.currencyFormatter.format(totalAmount));
                            ((InvestmentsDetail) context).investmentsProgressBar.setVisibility(View.GONE);
                        }
                    });
                }
                catch (Exception ex)
                {
                    ex.printStackTrace();
                    ((InvestmentsDetail) context).runOnUiThread(new Runnable() {
                        @Override
                        public void run() {

                            ((InvestmentsDetail) context).investmentsProgressBar.setVisibility(View.GONE);
                        }
                    });
                }
                return null;
            }
        }.execute();

    }

    public static void DeleteInvestment(final Investment investment, final Context context)
    {
        final String farmerCropId = ((InvestmentsDetail)context).farmerCropId;
        ((InvestmentsDetail)context).investmentsProgressBar.setVisibility(View.VISIBLE);
        new AsyncTask<Void, Void, Void>()
        {

            @Override
            protected Void doInBackground(Void... params) {
                try
                {

                    investmentTable.delete(investment).get();

                    syncDBChanges(context);

                    final MobileServiceList<Investment> result = investmentTable.read(mPullAllInvestmentsQuery).get();


                    ((InvestmentsDetail) context).runOnUiThread(new Runnable() {


                        @Override
                        public void run() {
                            Cache.InvestmentsCache.clear();
                            double totalAmount = 0;

                            for (int i = 0; i < result.toArray().length; i++)
                            {
                                if(((Investment) result.toArray()[i]).FarmerCropId.equals(farmerCropId))
                                {
                                    Cache.InvestmentsCache.add((Investment) result.toArray()[i]);
                                    totalAmount += ((Investment) result.toArray()[i]).Amount;
                                }
                            }
                            for (int i = 0; i < Cache.FarmerCropsCache.toArray().length; i++)
                            {
                                if(((FarmerCrop)Cache.FarmerCropsCache.toArray()[i]).id.equals(((InvestmentsDetail) context).farmerCropId))
                                {
                                    ((FarmerCrop)Cache.FarmerCropsCache.toArray()[i]).ActualInvestment = totalAmount;
                                    break;
                                }
                            }

                            ((InvestmentsDetail) context).totalAmounttextView.setText(MainActivity.currencyFormatter.format(totalAmount));
                            if(!Cache.InvestmentsCache.isEmpty())
                            {
                                ((InvestmentsDetail) context).investmentListView.setAdapter(new InvestmentsItemAdapter(context, android.R.layout.simple_list_item_1, Cache.InvestmentsCache));
                            }
                            ((InvestmentsDetail) context).investmentsProgressBar.setVisibility(View.GONE);
                        }
                    });
                }
                catch (Exception ex)
                {
                    ex.printStackTrace();
                    ((InvestmentsDetail)context).runOnUiThread(new Runnable() {
                        @Override
                        public void run() {

                            ((InvestmentsDetail)context).investmentsProgressBar.setVisibility(View.GONE);
                        }
                    });
                }
                return null;
            }
        }.execute();

    }


}
