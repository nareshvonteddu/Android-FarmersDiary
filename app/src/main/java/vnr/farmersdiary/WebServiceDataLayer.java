package vnr.farmersdiary;

import android.content.res.Resources;
import android.os.AsyncTask;

import com.google.gson.FieldNamingPolicy;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.microsoft.windowsazure.mobileservices.table.serialization.DateSerializer;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.utils.URLEncodedUtils;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.sql.Date;
import java.text.DateFormat;
import java.util.Arrays;


/**
 * Created by nvonteddu on 8/7/15.
 */
class GetUser extends AsyncTask<NameValuePair, Integer, HttpEntity> {

    Gson gson;
    DefaultHttpClient client;

    GetUser() {
        client = new DefaultHttpClient();

        gson = new GsonBuilder()
                .registerTypeAdapter(Date.class, new DateSerializer())
                .serializeNulls()
                .setDateFormat(DateFormat.LONG)
                .setFieldNamingPolicy(FieldNamingPolicy.UPPER_CAMEL_CASE)
                .setPrettyPrinting()
                .create();
    }


    @Override
    protected HttpEntity doInBackground(NameValuePair... params) {
        HttpGet get = new HttpGet(addMethodAndParametersToUrl("GetUser", params));
        get.setHeader("Accept", "application/json");
        get.setHeader("Content-type", "application/json");

        HttpResponse response = null;
        try {
            response = client.execute(get);
        } catch (Exception e) {
            e.printStackTrace();
        }
        HttpEntity entity = response.getEntity();
        return entity;
    }

    protected void onPostExecute(HttpEntity entity) {
        try {
            String s = convertStreamToString(entity.getContent());
            User sr = gson.fromJson(s, User.class);
            String sp = sr.Name;
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private String convertStreamToString(InputStream is) {
        BufferedReader reader = new BufferedReader(new InputStreamReader(is));
        StringBuilder sb = new StringBuilder();

        String line = null;
        try {
            while ((line = reader.readLine()) != null) {
                sb.append(line + "\n");
            }
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {
                is.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return sb.toString().replace("\n", "");
        //return sb.toString();
    }
    /*@Override
    protected Object doInBackground(Object[] params) {
        return null;
    }*/

    private String addMethodAndParametersToUrl(String method, NameValuePair... params)
    {
        String url = "http://farmersdiaryservice.cloudapp.net/FarmersDiaryService.svc/" + method;
            url += "/?";
        String paramString = URLEncodedUtils.format(Arrays.asList(params), "utf-8");
        url += paramString;
        return url;
    }
}
