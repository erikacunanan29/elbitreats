package elbitreats.nikki.elbitreats;

import android.app.Dialog;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.graphics.drawable.Drawable;
import android.support.annotation.StringRes;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.SearchView;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.RatingBar;
import android.widget.RelativeLayout;
import android.widget.Spinner;
import android.widget.Switch;
import android.widget.TabHost;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GoogleApiAvailability;
import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapFragment;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.BitmapDescriptor;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.w3c.dom.Text;

import java.lang.reflect.Array;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.regex.Pattern;

public class RestaurantActivity extends AppCompatActivity implements OnMapReadyCallback {
    String getRestaurant = "http://indolent-secretarie.000webhostapp.com/GetRestaurant.php";
    String getMenu = "http://indolent-secretarie.000webhostapp.com/GetSortedMenu.php";
    String getFavorite = "http://indolent-secretarie.000webhostapp.com/GetFavorite.php";
    String getFeedback = "http://indolent-secretarie.000webhostapp.com/GetAllFeedback.php";
    String deleteFavorite = "http://indolent-secretarie.000webhostapp.com/DeleteFavorite.php";
    String deleteRating = "http://indolent-secretarie.000webhostapp.com/DeleteRating.php";
    String updateRating = "http://indolent-secretarie.000webhostapp.com/UpdateRating.php";
    String insertVisited = "http://indolent-secretarie.000webhostapp.com/InsertVisited.php";
    String insertFavorite = "http://indolent-secretarie.000webhostapp.com/InsertFavorite.php";
    String insertFeedback = "http://indolent-secretarie.000webhostapp.com/InsertFeedback.php";

    ArrayList<Object> menuArray;
    ListAdapter menuAdapter;
    ListView menuList;

    Intent intent;
    RequestQueue queue;
    TextView foodestablishment;
    RatingBar usersRating;
    RatingBar userRating;
    TextView ratingDetails;
    Button favorite;
    TabHost restoTabs;
    Dialog dialog;
    GoogleMap map;

    String currentRating = "";
    String menuSelected = "";
    String coordinates = "";
    String restoAddress = "";
    String restoType = "";
    int anonymous = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_restaurant);

        intent = getIntent();
        queue = Volley.newRequestQueue(getApplicationContext());
        foodestablishment = (TextView) findViewById(R.id.foodestablishmentName);
        usersRating = (RatingBar) findViewById(R.id.usersRating);
        userRating = (RatingBar) findViewById(R.id.userRating);
        ratingDetails = (TextView) findViewById(R.id.ratingDetails);
        favorite = (Button) findViewById(R.id.favoriteButton);
        Button feedback = (Button) findViewById(R.id.feedbackButton);
        TextView details = (TextView) findViewById(R.id.userratingText);

        restoTabs = (TabHost) findViewById(R.id.restoTabs);
        restoTabs.setup();

        if ((intent.getStringExtra("userid")).equals("0")) {
            feedback.setVisibility(View.GONE);

            RelativeLayout.LayoutParams params = (RelativeLayout.LayoutParams) favorite.getLayoutParams();
            params.height = 0;
            params.setMargins(0,0,0,0);
            favorite.setLayoutParams(params);

            RelativeLayout.LayoutParams params1 = (RelativeLayout.LayoutParams) details.getLayoutParams();
            params1.height = 0;
            params1.setMargins(0,0,0,0);
            details.setLayoutParams(params1);

            RelativeLayout.LayoutParams params2 = (RelativeLayout.LayoutParams) userRating.getLayoutParams();
            params2.height = 0;
            params2.setMargins(0,0,0,0);
            userRating.setLayoutParams(params2);
        }



        TabHost.TabSpec spec0 = restoTabs.newTabSpec("MAP");
        spec0.setContent(R.id.layout0);
        spec0.setIndicator("MAP");
        restoTabs.addTab(spec0);

        TabHost.TabSpec spec1 = restoTabs.newTabSpec("ABOUT");
        spec1.setContent(R.id.layout1);
        spec1.setIndicator("ABOUT");
        restoTabs.addTab(spec1);

        TabHost.TabSpec spec2 = restoTabs.newTabSpec("MENU");
        spec2.setContent(R.id.layout2);
        spec2.setIndicator("MENU");
        restoTabs.addTab(spec2);

        TabHost.TabSpec spec3 = restoTabs.newTabSpec("REVIEW");
        spec3.setContent(R.id.layout3);
        spec3.setIndicator("REVIEW");
        restoTabs.addTab(spec3);

        updateVisited();
        getRestaurant();
        setMenuComponents();
        getFav();
        getFeedback();

        feedback.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog = new Dialog(RestaurantActivity.this, R.style.ThemeOverlay_AppCompat_Dialog_Alert);
                dialog.setContentView(R.layout.dialog_feedback);
                dialog.setCancelable(true);

                Button close = (Button) dialog.findViewById(R.id.closeButton);
                Button submit = (Button) dialog.findViewById(R.id.submitButton);

                dialog.show();

                close.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        dialog.dismiss();
                    }
                });

                submit.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        insertFeedback();
                    }
                });
            }
        });

        favorite.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String fav = (String) favorite.getText();
                if (fav.equals("Add to Favorites")) {
                    insertFav();
                } else if (fav.equals("Remove from Favorites")) {
                    deleteFav();
                }
            }
        });

        userRating.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                dialog = new Dialog(RestaurantActivity.this, R.style.ThemeOverlay_AppCompat_Dialog_Alert);
                dialog.setContentView(R.layout.dialog_rating);
                dialog.setCancelable(true);

                final RatingBar uRating = (RatingBar) dialog.findViewById(R.id.ratingBar);
                Button save = (Button) dialog.findViewById(R.id.saveButton);
                Button remove = (Button) dialog.findViewById(R.id.removeButton);
                final TextView header = (TextView) dialog.findViewById(R.id.ratingHeader);

                final String headerText = (header.getText()).toString();
                header.setText(headerText + currentRating);

                uRating.setRating(Float.parseFloat(currentRating));
                dialog.show();

                uRating.setOnTouchListener(new View.OnTouchListener() {
                    @Override
                    public boolean onTouch(View v, MotionEvent event) {
                        header.setText(headerText + uRating.getRating());
                        return false;
                    }
                });

                remove.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        deleteRating();
                    }
                });

                save.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        currentRating = uRating.getRating() + "";
                        updateRating();
                    }
                });

                return false;
            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.main, menu);
        MenuItem item= menu.findItem(R.id.menuSearch);
        SearchView searchView = (SearchView) item.getActionView();
        searchView.setQueryHint("Search for menu...");
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                if (menuArray != null) {
                    ArrayList<Object> filteredValues = new ArrayList<Object>(menuArray);

                    if (newText == null || newText.trim().isEmpty()) {
                        resetSearch();
                        return false;
                    }

                    for(int i=0; i < menuArray.size(); i++) {
                        if (menuArray.get(i) instanceof String) {
                            filteredValues.remove(menuArray.get(i));
                        } else {
                            JSONObject temp = (JSONObject) menuArray.get(i);
                            try {
                                String tempName = (temp.getString("food_name")).toLowerCase();
                                if (!tempName.contains(newText.toLowerCase())) {
                                    filteredValues.remove(menuArray.get(i));
                                }
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                        }
                    }

                    menuAdapter = new ListAdapter(RestaurantActivity.this, filteredValues, "MENU");
                    menuList.setAdapter(menuAdapter);
                }
                return false;
            }
        });
        return super.onCreateOptionsMenu(menu);
    }

    public void resetSearch() {
        menuAdapter = new ListAdapter(RestaurantActivity.this, menuArray, "MENU");
        menuList.setAdapter(menuAdapter);
    }

    public void getRestaurant() {
        StringRequest request = new StringRequest(Request.Method.POST, getRestaurant, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                try {
                    JSONObject obj = new JSONObject(response);
                    String status = obj.getString("status");

                    if (status.equals("SUCCESS")) {
                        JSONObject restaurant = obj.getJSONObject("resto");
                        String restoName = restaurant.getString("name");
                        coordinates = restaurant.getString("coordinates");
                        restoAddress = restaurant.getString("address");

                        foodestablishment.setText(restoName.toUpperCase());
                        usersRating.setRating(Float.parseFloat(restaurant.getString("rating")));
                        ratingDetails.setText(restaurant.getString("rating") + " (" + restaurant.getString("userCount") + " ratings)");

                        Float uRating = Float.parseFloat(restaurant.getString("userRating"));
                        currentRating = uRating + "";
                        if (uRating > 0) {
                            userRating.setRating(uRating);
                        }

                        TextView desc = (TextView) findViewById(R.id.descriptionText);
                        if (restaurant.getString("description").equals("")) {
                            desc.setText("No information to display.");
                        } else {
                            desc.setText(restaurant.getString("description"));
                        }

                        String[] days = {"Monday", "Tuesday", "Wednesday", "Thursday", "Friday", "Saturday", "Sunday"};
                        ArrayList<String> hours = new ArrayList<String>();
                        String closed = restaurant.getString("days_closed");
                        String sched = restaurant.getString("hours");
                        String[] temp = sched.split("; ");

                        int dayPosition = 0;
                        String dayName = new SimpleDateFormat("EEEE", Locale.ENGLISH).format(System.currentTimeMillis());
                        for(int i=0; i < days.length; i++) {
                            if (closed.contains(days[i])) {
                                hours.add(days[i] + ": " + "CLOSED");
                            } else if (sched.contains(days[i])) {
                                for(int j=0; j < temp.length; j++) {
                                    if (temp[j].contains(days[i])) {
                                        hours.add(temp[j]);
                                        break;
                                    }
                                }
                            } else {
                                hours.add(days[i] + ": " + temp[0]);
                            }

                            if(dayName.equals(days[i])) {
                                dayPosition = i;
                            }
                        }

                        Spinner hoursSpinner = (Spinner) findViewById(R.id.hoursSpinner);
                        ArrayAdapter<String> adapter = new ArrayAdapter<String>(RestaurantActivity.this, android.R.layout.simple_spinner_item, hours);
                        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                        hoursSpinner.setAdapter(adapter);
                        hoursSpinner.setSelection(dayPosition);

                        TextView type = (TextView) findViewById(R.id.typeText);
                        assignDetails("TYPE", restaurant.getString("type"), type);
                        restoType = restaurant.getString("type");

                        TextView cuisine = (TextView) findViewById(R.id.ethnicText);
                        assignDetails("CUISINE", restaurant.getString("ethnic"), cuisine);

                        TextView price = (TextView) findViewById(R.id.priceText);
                        assignDetails("PRICE RANGE", restaurant.getString("price_range"), price);

                        TextView payment = (TextView) findViewById(R.id.paymentText);
                        assignDetails("PAYMENT TYPE", restaurant.getString("payment_type"), payment);

                        TextView services = (TextView) findViewById(R.id.servicesText);
                        assignDetails("OTHER SERVICES", restaurant.getString("other_services"), services);

                        TextView contact = (TextView) findViewById(R.id.contactText);
                        assignDetails("CONTACT NUMBERS", restaurant.getString("contact_no"), contact);

                        TextView email = (TextView) findViewById(R.id.emailText);
                        assignDetails("EMAIL ADDRESS", restaurant.getString("email_add"), email);

                        TextView fb = (TextView) findViewById(R.id.facebookText);
                        assignDetails("FACEBOOK PAGE", restaurant.getString("facebook"), fb);

                        TextView twitter = (TextView) findViewById(R.id.twitterText);
                        assignDetails("TWITTER PAGE", restaurant.getString("twitter"), twitter);

                        TextView website = (TextView) findViewById(R.id.websiteText);
                        assignDetails("WEBSITE", restaurant.getString("website"), website);

                        googleServicesAvailable();

                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Toast.makeText(getApplicationContext(), error.toString(), Toast.LENGTH_LONG).show();
            }
        }) {
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String, String> parameters = new HashMap<String, String>();
                parameters.put("restaurantid", intent.getStringExtra("restaurantid"));
                parameters.put("userid", intent.getStringExtra("userid"));
                return parameters;
            }
        };
        queue.add(request);
    }

    public void assignDetails(String header, String value, TextView text) {
        if(!value.equals("")) {
            text.setText(header + ": " + value);
        } else {
            text.setVisibility(View.GONE);
        }
    }

    public void deleteRating() {
        StringRequest request = new StringRequest(Request.Method.POST, deleteRating, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                try {
                    JSONObject obj = new JSONObject(response);
                    String status = obj.getString("status");

                    if (status.equals("SUCCESS")) {
                        Toast.makeText(getApplicationContext(), "Rating removed.", Toast.LENGTH_SHORT).show();
                        userRating.setRating((float) 0.00);
                        currentRating = "0.00";
                        usersRating.setRating(Float.parseFloat(obj.getString("usersRating")));
                        ratingDetails.setText(obj.getString("usersRating") + " (" + obj.getString("usersCount") + " ratings)");
                        dialog.dismiss();
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Toast.makeText(getApplicationContext(), error.toString(), Toast.LENGTH_LONG).show();
            }
        }) {
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String, String> parameters = new HashMap<String, String>();
                parameters.put("restaurantid", intent.getStringExtra("restaurantid"));
                parameters.put("userid", intent.getStringExtra("userid"));
                return parameters;
            }
        };
        queue.add(request);
    }

    public void updateRating() {
        StringRequest request = new StringRequest(Request.Method.POST, updateRating, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                try {
                    JSONObject obj = new JSONObject(response);
                    String status = obj.getString("status");

                    if (status.equals("SUCCESS")) {
                        Toast.makeText(getApplicationContext(), "Rating updated.", Toast.LENGTH_SHORT).show();
                        userRating.setRating(Float.parseFloat(currentRating));
                        usersRating.setRating(Float.parseFloat(obj.getString("usersRating")));
                        ratingDetails.setText(obj.getString("usersRating") + " (" + obj.getString("usersCount") + " ratings)");
                        dialog.dismiss();
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Toast.makeText(getApplicationContext(), error.toString(), Toast.LENGTH_LONG).show();
            }
        }) {
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String, String> parameters = new HashMap<String, String>();
                parameters.put("restaurantid", intent.getStringExtra("restaurantid"));
                parameters.put("userid", intent.getStringExtra("userid"));
                parameters.put("rating", currentRating);
                return parameters;
            }
        };
        queue.add(request);
    }

    public void setMenuComponents() {
        Spinner spinner = (Spinner) findViewById(R.id.menuSpinner);

        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(RestaurantActivity.this, R.array.menuOption, android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner.setAdapter(adapter);
        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                String item = parent.getItemAtPosition(position).toString();
                getMenu(item);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
    }

    public void getMenu(String item) {
        menuSelected = item;

        StringRequest request = new StringRequest(Request.Method.POST, getMenu, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                try {
                    JSONObject jsonObject = new JSONObject(response);
                    String status = jsonObject.getString("status");

                    if (restoType.equals("Carinderia")) {
                        Toast.makeText(getApplicationContext(), "Menu varies per day.", Toast.LENGTH_LONG).show();
                    }
                    if (status.equals("SUCCESS")) {
                        JSONArray menu = jsonObject.getJSONArray("menu");
                        menuArray = new ArrayList<Object>();

                        if (menuSelected.equals("Alphabetical")) {
                            for(int i=0; i < menu.length(); i++) {
                                JSONObject temp = menu.getJSONObject(i);
                                char header = (temp.getString("food_name")).charAt(0);

                                if (i > 0) {
                                    JSONObject prevTemp = menu.getJSONObject(i-1);
                                    char prevHeader = (prevTemp.getString("food_name")).charAt(0);
                                    if (header != prevHeader) {
                                        menuArray.add(header + "");
                                    }
                                } else {
                                    if (Character.isLetter(header)) {
                                        menuArray.add(header + "");
                                    } else {
                                        menuArray.add("0-9");
                                    }
                                }
                                menuArray.add(temp);
                            }
                        } else if (menuSelected.equals("Best Seller")) {
                            for(int i=0; i < menu.length(); i++) {
                                JSONObject temp = menu.getJSONObject(i);
                                menuArray.add(temp);
                            }
                        } else if (menuSelected.equals("Main Ingredient")) {
                            for(int i=0; i < menu.length(); i++) {
                                JSONObject temp = menu.getJSONObject(i);
                                String header = temp.getString("main_ingredient");

                                if (i > 0) {
                                    JSONObject prevTemp = menu.getJSONObject(i-1);
                                    String prevHeader = prevTemp.getString("main_ingredient");
                                    if (header.equals(prevHeader) == false) {
                                        menuArray.add(header.toUpperCase());
                                    }
                                } else {
                                    menuArray.add(header.toUpperCase());
                                }
                                menuArray.add(temp);
                            }
                        } else if (menuSelected.equals("Menu Category")) {
                            for(int i=0; i < menu.length(); i++) {
                                JSONObject temp = menu.getJSONObject(i);
                                String header = temp.getString("menu_category");

                                if (i > 0) {
                                    JSONObject prevTemp = menu.getJSONObject(i-1);
                                    String prevHeader = prevTemp.getString("menu_category");
                                    if (header.equals(prevHeader) == false) {
                                        menuArray.add(header.toUpperCase());
                                    }
                                } else {
                                    menuArray.add(header.toUpperCase());
                                }
                                menuArray.add(temp);
                            }
                        } else if (menuSelected.equals("Price")) {
                            for(int i=0; i < menu.length(); i++) {
                                JSONObject temp = menu.getJSONObject(i);
                                int minPrice = Integer.parseInt(temp.getString("price"));
                                String header = getPriceHeader(minPrice) + "";

                                if (i > 0) {
                                    JSONObject prevTemp = menu.getJSONObject(i-1);
                                    int prevMin = Integer.parseInt(prevTemp.getString("price"));
                                    String prevHeader = getPriceHeader(prevMin);
                                    if (header.equals(prevHeader) == false) {
                                        menuArray.add(header.toUpperCase());
                                    }
                                } else {
                                    menuArray.add(header.toUpperCase());
                                }
                                menuArray.add(temp);
                            }
                        }

                        menuAdapter = new ListAdapter(RestaurantActivity.this, menuArray, "MENU");
                        menuList = (ListView) findViewById(R.id.menuList);
                        menuList.setAdapter(menuAdapter);
                    } else if (status.equals("EMPTY")) {
                        Toast.makeText(getApplicationContext(), "No menu available.", Toast.LENGTH_SHORT).show();
                    }
                } catch (JSONException e) {
                    Toast.makeText(getApplicationContext(), e.toString(), Toast.LENGTH_LONG).show();
                    e.printStackTrace();
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Toast.makeText(getApplicationContext(), error.toString(), Toast.LENGTH_LONG).show();
            }
        }) {
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String, String> parameters = new HashMap<String, String>();
                parameters.put("option", menuSelected);
                parameters.put("restaurantid", intent.getStringExtra("restaurantid"));
                return parameters;
            }
        };

        queue.add(request);
    }

    public String getPriceHeader(int minPrice) {
        String header = "";
        if (minPrice < 50) {
            header = "PRICES STARTS AT 20";
        } else if (minPrice >= 50 && minPrice < 100) {
            header = "PRICES STARTS AT 50";
        } else if (minPrice >= 100 && minPrice < 500) {
            header = "PRICES STARTS AT 100";
        } else if (minPrice >= 500) {
            header = "PRICES STARTS AT 500";
        }
        return header;
    }

    public void getFav() {
        StringRequest request = new StringRequest(Request.Method.POST, getFavorite, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                try {
                    JSONObject jsonObject = new JSONObject(response);
                    String status = jsonObject.getString("status");
                    if (status.equals("SUCCESS")) {
                        favorite.setCompoundDrawablesWithIntrinsicBounds(R.drawable.ic_resto_removefav, 0, 0, 0);
                        favorite.setText("Remove from Favorites");
                    } else if (status.equals("EMPTY")){
                        favorite.setCompoundDrawablesWithIntrinsicBounds(R.drawable.ic_resto_addfav, 0, 0, 0);
                        favorite.setText("Add to Favorites");
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Toast.makeText(getApplicationContext(), error.toString(), Toast.LENGTH_LONG).show();
            }
        }) {
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String, String> parameters = new HashMap<String, String>();
                parameters.put("restaurantid", intent.getStringExtra("restaurantid"));
                parameters.put("userid", intent.getStringExtra("userid"));
                return parameters;
            }
        };
        queue.add(request);
    }

    public void insertFav() {
        StringRequest request = new StringRequest(Request.Method.POST, insertFavorite, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                try {
                    JSONObject jsonObject = new JSONObject(response);
                    String status = jsonObject.getString("status");
                    if (status.equals("SUCCESS")) {
                        favorite.setCompoundDrawablesWithIntrinsicBounds(R.drawable.ic_resto_removefav, 0, 0, 0);
                        favorite.setText("Remove from Favorites");
                        Toast.makeText(getApplicationContext(), "Added to Favorites", Toast.LENGTH_SHORT).show();
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Toast.makeText(getApplicationContext(), error.toString(), Toast.LENGTH_LONG).show();
            }
        }) {
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String, String> parameters = new HashMap<String, String>();
                parameters.put("restaurantid", intent.getStringExtra("restaurantid"));
                parameters.put("userid", intent.getStringExtra("userid"));
                parameters.put("name", intent.getStringExtra("restoName"));
                return parameters;
            }
        };
        queue.add(request);
    }

    public void deleteFav() {
        StringRequest request = new StringRequest(Request.Method.POST, deleteFavorite, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                try {
                    JSONObject jsonObject = new JSONObject(response);
                    String status = jsonObject.getString("status");
                    if (status.equals("SUCCESS")) {
                        favorite.setCompoundDrawablesWithIntrinsicBounds(R.drawable.ic_resto_addfav, 0, 0, 0);
                        favorite.setText("Add to Favorites");
                        Toast.makeText(getApplicationContext(), "Removed from Favorites", Toast.LENGTH_SHORT).show();
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Toast.makeText(getApplicationContext(), error.toString(), Toast.LENGTH_LONG).show();
            }
        }) {
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String, String> parameters = new HashMap<String, String>();
                parameters.put("restaurantid", intent.getStringExtra("restaurantid"));
                parameters.put("userid", intent.getStringExtra("userid"));
                return parameters;
            }
        };
        queue.add(request);
    }

    public void getFeedback() {
        StringRequest request = new StringRequest(Request.Method.POST, getFeedback, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                try {
                    JSONObject jsonObject = new JSONObject(response);
                    String status = jsonObject.getString("status");
                    if (status.equals("SUCCESS")) {
                        JSONArray feedbacks = jsonObject.getJSONArray("feedbacks");
                        ArrayList<Object> listData = new ArrayList<Object>();

                        for(int i=0; i < feedbacks.length(); i++) {
                            listData.add(feedbacks.getJSONObject(i));
                        }

                        final ListAdapter adapter = new ListAdapter(RestaurantActivity.this, listData, "FEEDBACK");
                        ListView feedback = (ListView) findViewById(R.id.feedbackList);
                        feedback.setAdapter(adapter);
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }

            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Toast.makeText(getApplicationContext(), error.toString(), Toast.LENGTH_LONG).show();
            }
        }) {
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String, String> parameters = new HashMap<String, String>();
                parameters.put("restaurantid", intent.getStringExtra("restaurantid"));
                return parameters;
            }
        };
        queue.add(request);
    }

    public void insertFeedback() {
        final EditText reviewText = (EditText) dialog.findViewById(R.id.etReview);
        Switch anon = (Switch) dialog.findViewById(R.id.anonSwitch);

        if (anon.isChecked()) {
            anonymous = 1;
        } else {
            anonymous = 0;
        }

        StringRequest request = new StringRequest(Request.Method.POST, insertFeedback, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                try {
                    JSONObject jsonObject = new JSONObject(response);
                    String status = jsonObject.getString("status");
                    if (status.equals("SUCCESS")) {
                        Toast.makeText(getApplicationContext(), "Feedback successfully added.", Toast.LENGTH_SHORT).show();
                        getFeedback();
                        dialog.dismiss();
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }

            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Toast.makeText(getApplicationContext(), error.toString(), Toast.LENGTH_LONG).show();
            }
        }) {
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String, String> parameters = new HashMap<String, String>();
                parameters.put("restaurantid", intent.getStringExtra("restaurantid"));
                parameters.put("userid", intent.getStringExtra("userid"));
                parameters.put("name", intent.getStringExtra("name"));
                parameters.put("anon", anonymous + "");
                parameters.put("review", String.valueOf(reviewText.getText()));
                return parameters;
            }
        };
        queue.add(request);
    }

    public void googleServicesAvailable() {
        GoogleApiAvailability api = GoogleApiAvailability.getInstance();
        int isAvailable = api.isGooglePlayServicesAvailable(this);
        if (isAvailable == ConnectionResult.SUCCESS) {
            MapFragment restoMap = (MapFragment) getFragmentManager().findFragmentById(R.id.restoMaps);
            restoMap.getMapAsync(this);
        } else if (api.isUserResolvableError(isAvailable)) {
            Dialog dialog = api.getErrorDialog(this, isAvailable, 0);
            dialog.show();
        } else {
            Toast.makeText(this, "Can't connect to play services", Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        map = googleMap;
        goToLocation();
    }

    private void goToLocation() {
        String[] coordinate = coordinates.split(",");
        double lat = Double.parseDouble(coordinate[0]);
        double longi = Double.parseDouble(coordinate[1]);

        LatLng location = new LatLng(lat, longi);
        CameraUpdate update = CameraUpdateFactory.newLatLngZoom(location, 21);
        map.moveCamera(update);

        MarkerOptions options = new MarkerOptions()
                .title(restoAddress)
                .position(location);
        Marker marker = map.addMarker(options);
        marker.showInfoWindow();
    }

    public void updateVisited() {
        StringRequest request = new StringRequest(Request.Method.POST, insertVisited, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Toast.makeText(getApplicationContext(), error.toString(), Toast.LENGTH_LONG).show();
            }
        }) {
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String, String> parameters = new HashMap<String, String>();
                parameters.put("restaurantid", intent.getStringExtra("restaurantid"));
                parameters.put("userid", intent.getStringExtra("userid"));

                String temp = intent.getStringExtra("restoName");
                String edited[] = temp.split(" \\(");
                parameters.put("name", edited[0]);
                return parameters;
            }
        };
        queue.add(request);
    }
}
