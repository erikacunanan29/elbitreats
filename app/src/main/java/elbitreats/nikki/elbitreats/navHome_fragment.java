package elbitreats.nikki.elbitreats;

import android.app.Dialog;
import android.app.Fragment;
import android.app.FragmentManager;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AlertDialog;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.JavascriptInterface;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.loopj.android.http.RequestHandle;
import com.loopj.android.http.RequestParams;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;

import cz.msebera.android.httpclient.Header;

/**
 * Created by Erika on 9/29/2017.
 */

public class navHome_fragment extends Fragment {
    View myView;
    Button twitter;
    Button random;
    Button submit;
    ProgressBar progress;
    Bundle bundle;

    List<String> food_terms = new ArrayList<String>();
    String[] specialWords = {"ground", "2nd", "jar", "brand", "the", "ave", "and",
            "garden", "mommy", "budget", "ate", "all", "day",
            "minute", "mix", "jungle", "sure", "box", "shop",
            "universe", "corner", "combo", "extra", "extras", "half",
            "layers", "for", "feat", "christmas", "regular", "abc",
            "acc", "liver", "aide", "with", "cup", "cups", "king",
            "mode", "amazing", "irish", "player", "andy", "mild",
            "anti", "aging", "smooth", "asian", "fresh", "tray",
            "delight", "baby", "back", "basket", "large", "jack",
            "express", "special", "split", "foster", "fan", "single",
            "soft", "big", "tagalog", "forest", "twister", "angry",
            "epic", "good", "persons", "overload", "bundle", "house",
            "free", "premium", "bye", "snore", "original", "sticks",
            "dog", "choice", "rack", "skin", "time", "finger",
            "style", "kisses", "classic", "plain", "cowboy", "fat",
            "blue", "samurai", "curiousity", "junior", "double",
            "dragon", "hand", "lights", "five", "four", "small",
            "medium", "frozen", "goodluck", "hero", "hot", "cold",
            "holy", "green", "queen", "size", "whole", "long",
            "island", "love", "spell", "ref", "square", "family",
            "red", "gold", "blast", "black", "monster", "lava",
            "naked", "new", "pale", "party", "pack", "pearl", "pink",
            "poke", "quarter", "horse", "light", "san", "super",
            "save", "side", "signature", "care", "off", "week",
            "feet", "rings", "ring", "old", "street", "supreme",
            "per", "piece", "silver", "sunrise", "sunset", "club",
            "great", "white", "classics", "surprise", "triple",
            "itch", "two", "lovers", "point", "utimate", "unli",
            "dark", "real", "first", "season", "can", "ala", "purple",
            "sky", "zero", "mixed", "extreme", "hustle", "maid",
            "one", "three", "happy", "hotel", "pako", "gising", "leche", "high" };

    LinkedHashMap<String, Integer> wordBag = new LinkedHashMap<String, Integer>();
    LinkedHashMap<String, Integer> sortedWords = new LinkedHashMap<String, Integer>();
    JSONArray restaurants = new JSONArray();
    JSONArray menu = new JSONArray();
    int topWords = 3;
    int numberofTweets = 1000;

    JSONArray suggestionRestaurant = new JSONArray();
    JSONArray suggestionMenu = new JSONArray();
    JSONArray suggestionRating = new JSONArray();

    List<Integer> restaurantid = new ArrayList<Integer>();

    final String URL_TWITTER_LOGIN = "http://indolent-secretarie.000webhostapp.com/Twitter/sign_in.php";
    final String URL_TWITTER_TIMELINE = "http://indolent-secretarie.000webhostapp.com/Twitter/get_user_timeline.php";

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
        myView = inflater.inflate(R.layout.nav_home_layout, container, false);

        twitter = (Button) myView.findViewById(R.id.bTwitter);
        random = (Button) myView.findViewById(R.id.bRandom);
        submit = (Button) myView.findViewById(R.id.bSubmitNew);
        progress = (ProgressBar) myView.findViewById(R.id.suggestionBar);

        new GetAllRestaurantTask().execute(new DatabaseConnector());
        new GetAllMenuTask().execute(new DatabaseConnector());

        bundle = getArguments();

        twitter.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //openDialog with webView for login in twitter
                suggestionRestaurant = new JSONArray();
                suggestionMenu = new JSONArray();
                suggestionRating = new JSONArray();
                login();
            }
        });

        random.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Random ran = new Random();

                if (restaurantid.size() > 0) {
                    int randomValue = restaurantid.get(ran.nextInt(restaurantid.size()));
                    String tempName = "";
                    try {
                        for(int i=0; i < restaurants.length(); i++) {
                            JSONObject temp = restaurants.getJSONObject(i);
                            if (temp.getInt("restaurantid") == randomValue) {
                                tempName = temp.getString("name");
                                break;
                            }
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                    Intent intent = new Intent(getActivity(), RestaurantActivity.class);
                    intent.putExtra("restaurantid", randomValue + "");
                    intent.putExtra("userid", bundle.getInt("userid") + "");
                    intent.putExtra("name", bundle.getString("name"));
                    intent.putExtra("restoName", tempName);

                    getActivity().startActivity(intent);
                } else {
                    Toast.makeText(getActivity(), "Data is still loading. Try again.", Toast.LENGTH_SHORT).show();
                }
            }
        });

        submit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Fragment fragment = new navSubmit_fragment();
                FragmentManager fm = getFragmentManager();
                fm.beginTransaction().replace(R.id.content_frame, fragment).commit();
            }
        });

        return myView;
    }

    private void login() {
        final WebView webview = (WebView) myView.findViewById(R.id.twitterWeb);
        webview.getSettings().setJavaScriptEnabled(true);
        webview.loadUrl(URL_TWITTER_LOGIN);
        random.setVisibility(View.GONE);
        submit.setVisibility(View.GONE);
        twitter.setVisibility(View.GONE);

        webview.setWebViewClient(new WebViewClient() {
            @Override
            public void onPageFinished(WebView view, String url) {
                super.onPageFinished(view, url);
                if (url.contains("callback.php")) {
                    view.loadUrl("javascript:JsonViewer.onJsonReceived(document.getElementsByTagName('body')[0].innerHTML)");
                    webview.setVisibility(View.GONE);
                }

            }
        });

        webview.addJavascriptInterface(new UserJavascriptInterface(getActivity()), "JsonViewer");
        webview.getSettings().setDomStorageEnabled(true);
        webview.setVisibility(View.VISIBLE);
    }

    class UserJavascriptInterface {
        private Context ctx;

        UserJavascriptInterface(Context ctx) {
            this.ctx = ctx;
        }

        @JavascriptInterface
        public void onJsonReceived(String json) {
            Gson gson = new GsonBuilder().create();
            final OAuthResult oauthResult = gson.fromJson(json, OAuthResult.class);
            if (oauthResult != null && oauthResult.getOauthToken() != null && oauthResult.getOauthTokenSecret() != null) {
                getActivity().runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        getTweets(oauthResult.getOauthToken(), oauthResult.getOauthTokenSecret(), oauthResult.getScreenName(), 5);
                    }
                });
            }
        }
    }

    private void getTweets(String oAuthToken, String oAuthTokenSecret, String screenName, int p) {
        RequestParams params = new RequestParams();
        params.add("oauth_token", oAuthToken);
        params.add("oauth_token_secret", oAuthTokenSecret);
        params.add("screen_name", screenName);
        params.add("page", p + "");

        progress.setVisibility(View.VISIBLE);
        progress.bringToFront();

        AsyncHttpClient client = new AsyncHttpClient();
        RequestHandle text = client.get(getActivity(), URL_TWITTER_TIMELINE, params, new AsyncHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, byte[] responseBody) {
                JSONArray json = null;
                try {
                    json = new JSONArray(new String(responseBody));
                    for(int i=0; i < json.length(); i++) {
                        String word = ((json.getString(i)).toLowerCase()).replaceAll("[^A-za-z0-9 @]", "");
                        String[] words = word.split(" ");
                        for (int k = 0; k < words.length; k++) {
                            if (words[k].startsWith("@") || (words[k].length() <= 2) || Arrays.asList(specialWords).contains(words[k]) ) {
                                continue;
                            } else {
                                for(int l = 0; l < food_terms.size(); l++) {
                                    String[] food = (food_terms.get(l)).split(" ");

                                    if (Arrays.asList(food).contains(words[k])) {
                                        if(wordBag.containsKey(words[k])) {
                                            wordBag.put(words[k], wordBag.get(words[k]) + 1);
                                        } else {
                                            wordBag.put(words[k], 1);
                                        }
                                        break;
                                    }
                                }
                            }
                        }
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }

                while(sortedWords.size() != topWords && wordBag.size() > 0) {
                    String key = "";
                    int max = 0;
                    for(Map.Entry<String, Integer> val: wordBag.entrySet()) {
                        if (val.getValue() > max) {
                            max = val.getValue();
                            key = val.getKey();
                        }
                    }
                    sortedWords.put(key, max);
                    wordBag.remove(key);
                }

                getRestaurantSuggestion();
                getMenuSuggestion();

                Intent intent = new Intent(getActivity(), SuggestionActivity.class);
                intent.putExtra("restaurant", suggestionRestaurant.toString());
                intent.putExtra("menu", suggestionMenu.toString());
                intent.putExtra("rating", suggestionRating.toString());
                intent.putExtra("userid", bundle.getInt("userid") + "");
                intent.putExtra("name", bundle.getString("name"));
                getActivity().startActivity(intent);
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, byte[] responseBody, Throwable error) {
                Toast.makeText(getActivity(), "Something went wrong. Try again.", Toast.LENGTH_LONG).show();
                progress.setVisibility(View.GONE);
                random.setVisibility(View.VISIBLE);
                submit.setVisibility(View.VISIBLE);
                twitter.setVisibility(View.VISIBLE);
            }

            @Override
            public void onFinish() {
                progress.setVisibility(View.GONE);
                random.setVisibility(View.VISIBLE);
                submit.setVisibility(View.VISIBLE);
                twitter.setVisibility(View.VISIBLE);
            }
        });
    }

    public void getRatingSuggestion(String foodterm, List<Integer> available) {
        JSONArray tempSuggestions = new JSONArray();

        while (tempSuggestions.length() != topWords && available.size() > 0) {
            String name = "";
            float max = 0;
            int index = 0;
            float rating = 0;
            int id = 0;
            JSONObject tempData = new JSONObject();

            for(int i=0; i < restaurants.length(); i++) {
                JSONObject resto = null;
                try {
                    resto = restaurants.getJSONObject(i);
                    id = resto.getInt("restaurantid");
                    rating = Float.parseFloat(resto.getString("rating"));
                    for(int j=0; j < available.size(); j++) {
                        if (available.get(j) == id) {
                            if (rating >= max) {
                                max = rating;
                                index = j;
                                name = resto.getString("name");
                            }
                        } else {
                            continue;
                        }
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }

            try {
                tempData.put("name", name + " (" + max + ")");
                tempData.put("id", available.get(index));
                tempSuggestions.put(tempData);
                available.remove(index);
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }

        JSONObject tempObj = new JSONObject();
        try {
            tempObj.put("foodterm", foodterm);
            tempObj.put("suggestions", tempSuggestions);
            suggestionRating.put(tempObj);
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    public void getMenuSuggestion() {
        for(Map.Entry<String, Integer> val: sortedWords.entrySet()) {
            LinkedHashMap<Integer, Integer> menuCounter = new LinkedHashMap<Integer, Integer>();
            String key = val.getKey();
            for(int i=0; i < menu.length(); i++) {
                JSONObject menuItem = null;
                try {
                    menuItem = menu.getJSONObject(i);
                    String name = convertWord(menuItem.getString("food_name"));
                    String type = convertWord(menuItem.getString("food_type"));
                    int id = menuItem.getInt("restaurant_id");

                    if (name.contains(key) || type.contains(key)) {
                        if (menuCounter.containsKey(id)) {
                            menuCounter.put(id, menuCounter.get(id) + 1);
                        } else {
                            menuCounter.put(id, 1);
                        }
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }

            List<Integer> occurred = new ArrayList<>(menuCounter.keySet());
            if (occurred.size() > 0) {
                getRatingSuggestion(key.toUpperCase(),occurred);
            }

            if (menuCounter.size() == 0) {
                continue;
            } else {
                List<Integer> suggested = new ArrayList<Integer>();
                while (suggested.size() != topWords && menuCounter.size() > 0) {
                    int id = 0;
                    int max = 0;
                    for(Map.Entry<Integer, Integer> item: menuCounter.entrySet()) {
                        if (item.getValue() > max) {
                            max = item.getValue();
                            id = item.getKey();
                        }
                    }
                    suggested.add(id);
                    menuCounter.remove(id);
                }

                suggestionMenu.put(getRestaurantDetails(key, suggested));
            }
        }
    }

    public JSONObject getRestaurantDetails(String foodterm, List<Integer> available) {
        JSONArray tempSuggestions = new JSONArray();

        for(int i=0; i < available.size(); i++) {
            for(int j=0; j < restaurants.length(); j++) {
                JSONObject resto = null;
                JSONObject tempData = new JSONObject();
                try {
                    resto = restaurants.getJSONObject(j);
                    int temp = resto.getInt("restaurantid");
                    if (temp == available.get(i)) {
                        tempData.put("name", resto.getString("name"));
                        tempData.put("id", available.get(i));
                        tempSuggestions.put(tempData);
                        break;
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }

        JSONObject tempObj = new JSONObject();
        try {
            tempObj.put("foodterm", foodterm.toUpperCase());
            tempObj.put("suggestions", tempSuggestions);
        } catch (JSONException e) {
            e.printStackTrace();
        }

        return tempObj;
    }

    public void getRestaurantSuggestion() {
        for(Map.Entry<String, Integer> val: sortedWords.entrySet()) {
            String key = val.getKey();
            JSONArray tempSuggestions = new JSONArray();

            for (int j = 0; j < restaurants.length(); j++) {
                JSONObject resto = null;
                JSONObject tempData = new JSONObject();
                try {
                    resto = restaurants.getJSONObject(j);
                    String name = convertWord(resto.getString("name"));
                    if (name == key) {
                        tempData.put("name", resto.getString("name"));
                        tempData.put("id", resto.getInt("restaurantid"));
                        tempSuggestions.put(tempData);
                        break;
                    } else if (name.contains(key)){
                        tempData.put("name", resto.getString("name"));
                        tempData.put("id", resto.getInt("restaurantid"));
                        tempSuggestions.put(tempData);
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }

            if (tempSuggestions.length() != 0) {
                JSONObject tempObj = new JSONObject();
                try {
                    tempObj.put("foodterm", key.toUpperCase());
                    tempObj.put("suggestions", tempSuggestions);
                    suggestionRestaurant.put(tempObj);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    public String convertWord(String word) {
        word = word.toLowerCase().replaceAll("[^A-za-z0-9 ]", "");
       return word;
    }

    private class GetAllRestaurantTask extends AsyncTask<DatabaseConnector, Long, JSONArray> {
        @Override
        protected JSONArray doInBackground(DatabaseConnector... params) {
            return params[0].GetAllRestaurants();
        }

        @Override
        protected void onPostExecute(JSONArray jsonArray) {
            for(int i=0; i < jsonArray.length(); i++) {
                JSONObject json = null;
                try {
                    json = jsonArray.getJSONObject(i);
                    restaurants.put(json);
                    food_terms.add(convertWord(json.getString("name")));
                    restaurantid.add(json.getInt("restaurantid"));
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    private class GetAllMenuTask extends AsyncTask<DatabaseConnector, Long, JSONArray> {
        @Override
        protected JSONArray doInBackground(DatabaseConnector... params) {
            return params[0].GetAllMenu();
        }

        @Override
        protected void onPostExecute(JSONArray jsonArray) {
            for(int i=0; i < jsonArray.length(); i++) {
                JSONObject json = null;
                try {
                    json = jsonArray.getJSONObject(i);
                    menu.put(json);
                    food_terms.add(convertWord(json.getString("food_type")));
                    food_terms.add(convertWord(json.getString("food_name")));
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }
    }
}
