package elbitreats.nikki.elbitreats;

import org.json.JSONArray;
import org.json.JSONException;

import java.io.IOException;

import cz.msebera.android.httpclient.HttpEntity;
import cz.msebera.android.httpclient.HttpResponse;
import cz.msebera.android.httpclient.client.ClientProtocolException;
import cz.msebera.android.httpclient.client.HttpClient;
import cz.msebera.android.httpclient.client.methods.HttpGet;
import cz.msebera.android.httpclient.impl.client.HttpClients;
import cz.msebera.android.httpclient.util.EntityUtils;

/**
 * Created by Erika on 10/2/2017.
 */

public class DatabaseConnector {

    public JSONArray GetAllRestaurants() {
        //URL for php code for getting all Restaurants
        String url = "http://indolent-secretarie.000webhostapp.com/GetAllRestaurant.php";
        JSONArray jsonArray = httpFunction(url);
        return jsonArray;
    }

    public JSONArray GetAllMenu() {
        String url = "http://indolent-secretarie.000webhostapp.com/GetAllMenu.php";
        JSONArray jsonArray = httpFunction(url);
        return jsonArray;
    }

    public JSONArray httpFunction(String url) {
        //GET HttpResponse Object from URL
        //GET HttpEntity from HTTP Response Object

        HttpEntity httpEntity = null;

        try {
            HttpClient httpClient = HttpClients.createDefault();
            HttpGet httpGet = new HttpGet(url);
            HttpResponse httpResponse = httpClient.execute(httpGet);
            httpEntity = httpResponse.getEntity();

        } catch(ClientProtocolException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }

        //Convert HttpEntity into JSON Array
        JSONArray jsonArray = null;

        if (httpEntity != null) {
            try {
                String entityResponse = EntityUtils.toString(httpEntity);
                jsonArray = new JSONArray(entityResponse);
            } catch (IOException e) {
                e.printStackTrace();
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }

        return jsonArray;
    }
}
