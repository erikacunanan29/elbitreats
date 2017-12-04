package elbitreats.nikki.elbitreats;

import android.app.Fragment;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AlertDialog;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.TextView;
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
import org.w3c.dom.Text;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by Erika on 9/29/2017.
 */

public class navProfile_fragment extends Fragment {
    View myView;
    Bundle bundle;
    RequestQueue queue;

    ListView visitedList;
    TextView fullname;
    TextView nickgenAge;
    TextView birthday;
    TextView emailAddress;
    TextView contactNumber;
    TextView username;
    TextView password;

    String visitedUrl = "http://indolent-secretarie.000webhostapp.com/GetAllVisited.php";
    String profileUrl = "http://indolent-secretarie.000webhostapp.com/GetUser.php";

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
        myView = inflater.inflate(R.layout.nav_profile_layout, container, false);

        fullname = (TextView) myView.findViewById(R.id.nameHeader);
        nickgenAge = (TextView) myView.findViewById(R.id.nicknameGender);
        birthday = (TextView) myView.findViewById(R.id.birthday);
        emailAddress = (TextView) myView.findViewById(R.id.emailAddress);
        contactNumber = (TextView) myView.findViewById(R.id.contactNumber);
        username = (TextView) myView.findViewById(R.id.username);
        password = (TextView) myView.findViewById(R.id.password);
        visitedList = (ListView) myView.findViewById(R.id.visitedList);

        queue = Volley.newRequestQueue(getActivity());
        bundle = getArguments();

        getUser();
        getVisited();

        return myView;
    }

    public void getUser() {
        StringRequest request = new StringRequest(Request.Method.POST, profileUrl, new Response.Listener<String>() {

            @Override
            public void onResponse(String response) {
                JSONObject jsonObject = null;
                try {
                    jsonObject = new JSONObject(response);
                    String status = jsonObject.getString("status");
                    if (status.equals("SUCCESS")) {
                        JSONObject profile = jsonObject.getJSONObject("user");

                        String gender = profile.getString("gender");
                        String temp = profile.getString("nickname");
                        if (gender.equals("F")) {
                            temp = temp + ", Female, ";
                        } else {
                            temp = temp + ", Male, ";
                        }
                        temp = temp + profile.getInt("age");

                        fullname.setText(profile.getString("name"));
                        nickgenAge.setText(temp);
                        birthday.setText(profile.getString("birthday"));
                        emailAddress.setText(profile.getString("email_address"));
                        contactNumber.setText(profile.getString("contact_no"));
                        username.setText(profile.getString("username"));
                        password.setText(profile.getString("password"));
                    } else if (status.equals("EMPTY")) {
                        AlertDialog.Builder prompt = new AlertDialog.Builder(getActivity());
                        prompt.setMessage("User profile not found.")
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
    }

    public void getVisited() {
        StringRequest request = new StringRequest(Request.Method.POST, visitedUrl, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                try {
                    JSONObject jsonObject = new JSONObject(response);
                    String status = jsonObject.getString("status");

                    if (status.equals("SUCCESS")) {
                        ArrayList<Object> listData = new ArrayList<Object>();
                        JSONArray visited = jsonObject.getJSONArray("visited");

                        for(int i=0; i < visited.length(); i++) {
                            if (listData.size() == 20) {
                                break;
                            } else if (i > 0) {
                                JSONObject temp = visited.getJSONObject(i);
                                String tempName = temp.getString("name");
                                Boolean isRepeat = false;
                                for(int j=0; j < listData.size(); j++) {
                                    JSONObject temp1 = (JSONObject) listData.get(j);
                                    String tempName1 = temp1.getString("name");
                                    if (tempName1.equals(tempName)) {
                                        isRepeat = true;
                                        break;
                                    }
                                }

                                if (isRepeat == false) {
                                    listData.add(visited.getJSONObject(i));
                                }
                            } else {
                                listData.add(visited.getJSONObject(i));
                            }
                        }

                        final ListAdapter adapter = new ListAdapter(getActivity(), listData, "DEFAULT");
                        visitedList.setAdapter(adapter);
                        visitedList.isClickable();

                        visitedList.setOnItemClickListener(new AdapterView.OnItemClickListener() {

                            @Override
                            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                                if (adapter.getItemViewType(position) == adapter.ITEM) {
                                    JSONObject resto = (JSONObject) visitedList.getItemAtPosition(position);
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
                parameters.put("userid", bundle.getInt("userid") + "");
                return parameters;
            }
        };

        queue.add(request);

    }
}
