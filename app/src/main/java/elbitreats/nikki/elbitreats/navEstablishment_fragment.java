package elbitreats.nikki.elbitreats;

import android.app.AlertDialog;
import android.app.Fragment;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.view.MenuItemCompat;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.SearchView;
import android.widget.Spinner;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by Erika on 9/29/2017.
 */

public class navEstablishment_fragment extends Fragment {
    View myView;
    Spinner spinner;
    ListView establishmentsList;
    ArrayList<Object> listData;
    ListAdapter adapter;
    RequestQueue queue;
    Bundle bundle;

    String url = "http://indolent-secretarie.000webhostapp.com/GetAllSortedEstablishments.php";
    String chosen;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
        myView = inflater.inflate(R.layout.nav_establishment_layout, container, false);
        spinner = (Spinner) myView.findViewById(R.id.establishmentSpinner);
        establishmentsList = (ListView) myView.findViewById(R.id.establishmentList);
        queue = Volley.newRequestQueue(getActivity());
        bundle = getArguments();

        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(getActivity(), R.array.establishmentsOption, android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner.setAdapter(adapter);
        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                String item = parent.getItemAtPosition(position).toString();
                getEstablishments(item);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        return myView;
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.main, menu);
        MenuItem item = menu.findItem(R.id.menuSearch);
        SearchView sv = new SearchView(( (MainActivity) getActivity()).getSupportActionBar().getThemedContext());
        sv.setQueryHint("Search for food establishment...");
        MenuItemCompat.setShowAsAction(item, MenuItemCompat.SHOW_AS_ACTION_IF_ROOM);
        MenuItemCompat.setActionView(item, sv);
        sv.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                if (listData != null) {
                    ArrayList<Object> filteredValues = new ArrayList<Object>(listData);

                    if (newText == null || newText.trim().isEmpty()) {
                        resetSearch();
                        return false;
                    }

                    for(int i=0; i < listData.size(); i++) {
                        if (listData.get(i) instanceof String) {
                            filteredValues.remove(listData.get(i));
                        } else {
                            JSONObject temp = (JSONObject) listData.get(i);
                            try {
                                String tempName = (temp.getString("name")).toLowerCase();
                                if (!tempName.contains(newText.toLowerCase())) {
                                    filteredValues.remove(listData.get(i));
                                }
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                        }
                    }

                    adapter = new ListAdapter(getActivity(), filteredValues, "DEFAULT");
                    establishmentsList.setAdapter(adapter);
                }
                return false;
            }
        });

    }

    public void resetSearch() {
        adapter = new ListAdapter(getActivity(), listData, "DEFAULT");
        establishmentsList.setAdapter(adapter);
    }

    public void getEstablishments(String selected) {
        chosen = selected;

        StringRequest request = new StringRequest(Request.Method.POST, url, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                try {
                    JSONObject jsonObject = new JSONObject(response);
                    String status = jsonObject.getString("status");
                    if (status.equals("SUCCESS")) {
                        JSONArray establishments = jsonObject.getJSONArray("establishments");
                        listData = new ArrayList<Object>();

                        if (chosen.equals("Alphabetical")) {
                            for(int i=0; i < establishments.length(); i++) {
                                JSONObject temp = establishments.getJSONObject(i);
                                String header = temp.getString("name").charAt(0) + "";

                                if (i > 0) {
                                    JSONObject prevTemp = establishments.getJSONObject(i-1);
                                    String prevHeader = prevTemp.getString("name").charAt(0) + "";
                                    if (header.equals(prevHeader) == false) {
                                        listData.add(header.toUpperCase());
                                    }
                                } else {
                                    if (Character.isLetter(Integer.parseInt(header))) {
                                        listData.add(header.toUpperCase());
                                    } else {
                                        listData.add("0-9");
                                    }
                                }
                                listData.add(temp);
                            }
                        } else if (chosen.equals("Price Range")) {
                            for(int i=0; i < establishments.length(); i++) {
                                JSONObject temp = establishments.getJSONObject(i);
                                String[] price = (temp.getString("price_range")).split("-");
                                int minPrice = Integer.parseInt(price[0]);
                                String header = getPriceHeader(minPrice);

                                if (i > 0) {
                                    JSONObject prevTemp = establishments.getJSONObject(i-1);
                                    String[] prevPrice = (prevTemp.getString("price_range")).split("-");
                                    int prevMin = Integer.parseInt(prevPrice[0]);
                                    String prevHeader = getPriceHeader(prevMin);
                                    if (header.equals(prevHeader) == false) {
                                        listData.add(header.toUpperCase());
                                    }
                                } else {
                                    listData.add(header.toUpperCase());
                                }
                                listData.add(temp);
                            }
                        } else if (chosen.equals("Rating")) {
                            for(int i=0; i < establishments.length(); i++) {
                                JSONObject temp = establishments.getJSONObject(i);
                                String header = temp.getString("rating").charAt(0) + "";

                                if (i > 0) {
                                    JSONObject prevTemp = establishments.getJSONObject(i-1);
                                    String prevHeader = prevTemp.getString("rating").charAt(0) + "";
                                    if (header.equals(prevHeader) == false) {
                                        listData.add(header.toUpperCase());
                                    }
                                } else {
                                    listData.add(header.toUpperCase());
                                }
                                listData.add(temp);
                            }
                        } else if (chosen.equals("Frequently Visited")) {
                            for(int i=0; i < establishments.length(); i++) {
                                JSONObject temp = establishments.getJSONObject(i);
                                listData.add(temp);
                            }
                        } else if (chosen.equals("Food Establishment Type")) {
                            for(int i=0; i < establishments.length(); i++) {
                                JSONObject temp = establishments.getJSONObject(i);
                                String header = temp.getString("type");

                                if (i > 0) {
                                    JSONObject prevTemp = establishments.getJSONObject(i-1);
                                    String prevHeader = prevTemp.getString("type");
                                    if (header.equals(prevHeader) == false) {
                                        listData.add(header.toUpperCase());
                                    }
                                } else {
                                    listData.add(header.toUpperCase());
                                }
                                listData.add(temp);
                            }
                        } else if (chosen.equals("Food Ethnicity")) {
                            for(int i=0; i < establishments.length(); i++) {
                                JSONObject temp = establishments.getJSONObject(i);
                                String header = temp.getString("ethnic");

                                if (i > 0) {
                                    JSONObject prevTemp = establishments.getJSONObject(i-1);
                                    String prevHeader = prevTemp.getString("ethnic");
                                    if (header.equals(prevHeader) == false) {
                                        listData.add(header.toUpperCase());
                                    }
                                } else {
                                    listData.add(header.toUpperCase());
                                }
                                listData.add(temp);
                            }
                        } else if (chosen.equals("Location")) {
                            for(int i=0; i < establishments.length(); i++) {
                                JSONObject temp = establishments.getJSONObject(i);
                                String header = temp.getString("location");

                                if (i > 0) {
                                    JSONObject prevTemp = establishments.getJSONObject(i-1);
                                    String prevHeader = prevTemp.getString("location");
                                    if (header.equals(prevHeader) == false) {
                                        listData.add(header.toUpperCase());
                                    }
                                } else {
                                    listData.add(header.toUpperCase());
                                }
                                listData.add(temp);
                            }
                        }
                        fillList(listData);
                    } else {
                        AlertDialog.Builder prompt = new AlertDialog.Builder(getActivity());
                        prompt.setMessage("You haven't visited any food establishment pages yet.")
                                .setNegativeButton("Okay", null)
                                .create()
                                .show();
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Toast.makeText(getActivity(), error.toString(), Toast.LENGTH_LONG).show();
            }
        }) {
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String, String> parameters = new HashMap<String, String>();

                if (chosen.equals("Alphabetical")) {
                    parameters.put("option", "ALPHABET");
                } else if (chosen.equals("Price Range")) {
                    parameters.put("option", "PRICE");
                } else if (chosen.equals("Rating")) {
                    parameters.put("option", "RATING");
                } else if (chosen.equals("Frequently Visited")) {
                    parameters.put("option", "VISITED");
                } else if (chosen.equals("Food Establishment Type")) {
                    parameters.put("option", "TYPE");
                } else if (chosen.equals("Food Ethnicity")) {
                    parameters.put("option", "ETHNIC");
                } else if (chosen.equals("Location")) {
                    parameters.put("option", "LOCATION");
                }

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
        } else if (minPrice >= 100) {
            header = "PRICES STARTS AT 100";
        }
        return header;
    }

    public void fillList(ArrayList<Object> listData) {
        adapter = new ListAdapter(getActivity(), listData, "DEFAULT");
        establishmentsList.setAdapter(adapter);
        establishmentsList.isClickable();

        establishmentsList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                if (adapter.getItemViewType(position) == adapter.ITEM) {
                    JSONObject resto = (JSONObject) establishmentsList.getItemAtPosition(position);
                    try {
                        Intent intent = new Intent(getActivity(), RestaurantActivity.class);

                        if (chosen.equals("Frequently Visited")) {
                            intent.putExtra("restaurantid", resto.getString("restaurant_id"));
                        } else {
                            intent.putExtra("restaurantid", resto.getString("restaurantid"));
                        }
                        intent.putExtra("userid", bundle.getInt("userid") + "");
                        intent.putExtra("restoName", resto.getString("name"));
                        intent.putExtra("name", bundle.getString("name"));
                        getActivity().startActivity(intent);
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
            }
        });
    }
}
