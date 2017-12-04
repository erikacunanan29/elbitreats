package elbitreats.nikki.elbitreats;

import android.app.Fragment;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.view.MenuItemCompat;
import android.support.v4.view.PagerAdapter;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.SearchView;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
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

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by Erika on 9/29/2017.
 */

public class navFavorite_fragment extends Fragment  {
    View myView;
    ListView favoritesList;
    ArrayList<Object> listData;
    ListAdapter adapter;
    String url = "http://indolent-secretarie.000webhostapp.com/GetAllFavorites.php";
    RequestQueue queue;
    Bundle bundle;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
        myView = inflater.inflate(R.layout.nav_favorite_layout, container, false);
        favoritesList = (ListView) myView.findViewById(R.id.lvFavorites);
        queue = Volley.newRequestQueue(getActivity());
        bundle = getArguments();

        StringRequest request = new StringRequest(Request.Method.POST, url, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                try {
                    JSONObject jsonObject = new JSONObject(response);
                    String status = jsonObject.getString("status");

                    if (status.equals("SUCCESS")) {
                        JSONArray favorites = jsonObject.getJSONArray("favorites");
                        listData = new ArrayList<Object>();

                        for(int i=0; i < favorites.length(); i++) {
                            listData.add(favorites.getJSONObject(i));
                        }

                        adapter = new ListAdapter(getActivity(), listData, "DEFAULT");
                        favoritesList.setAdapter(adapter);
                        favoritesList.isClickable();

                        favoritesList.setOnItemClickListener(new AdapterView.OnItemClickListener() {

                            @Override
                            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                                if (adapter.getItemViewType(position) == adapter.ITEM) {
                                    JSONObject resto = (JSONObject) favoritesList.getItemAtPosition(position);
                                    try {
                                        Intent intent = new Intent(getActivity(), RestaurantActivity.class);
                                        intent.putExtra("restaurantid", resto.getString("restaurant_id"));
                                        intent.putExtra("userid", bundle.getInt("userid") + "");
                                        intent.putExtra("name", bundle.getString("name"));
                                        intent.putExtra("restoName", resto.getString("name"));
                                        getActivity().startActivity(intent);
                                    } catch (JSONException e) {
                                        e.printStackTrace();
                                    }
                                }
                            }
                        });

                    } else if (status.equals("EMPTY")) {
                        AlertDialog.Builder prompt = new AlertDialog.Builder(getActivity());
                        prompt.setMessage("You have no favorites available at this moment. Add some.")
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
                parameters.put("userid", bundle.getInt("userid") + "");
                return parameters;
            }
        };
        queue.add(request);

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

                    adapter = new ListAdapter(getActivity(), filteredValues, "DEFAULT");
                    favoritesList.setAdapter(adapter);
                }
                return false;
            }
        });

    }

    public void resetSearch() {
        adapter = new ListAdapter(getActivity(), listData, "DEFAULT");
        favoritesList.setAdapter(adapter);
    }
}
