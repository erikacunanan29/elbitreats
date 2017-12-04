package elbitreats.nikki.elbitreats;

import android.app.Fragment;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AlertDialog;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.google.gson.JsonObject;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by Erika on 9/29/2017.
 */

public class navSubmit_fragment extends Fragment {
    View myView;
    String url = "http://indolent-secretarie.000webhostapp.com/InsertEstablishment.php";
    EditText etName;
    EditText etAddress;
    RequestQueue queue;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
        myView = inflater.inflate(R.layout.nav_submit_layout, container, false);
        etName =(EditText) myView.findViewById(R.id.etRestoName);
        etAddress = (EditText) myView.findViewById(R.id.etRestoLocation);
        Button bSubmit = (Button) myView.findViewById(R.id.bSubmitResto);


        queue = Volley.newRequestQueue(getActivity());
        bSubmit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                StringRequest request = new StringRequest(Request.Method.POST, url, new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        try {
                            JSONObject json = new JSONObject(response);
                            AlertDialog.Builder prompt = new AlertDialog.Builder(getActivity());
                            String status = json.getString("status");

                            if (status.equals("SUCCESS")) {
                                prompt.setMessage("Successfully submitted new establishment.")
                                        .setNegativeButton("Okay", null)
                                        .create()
                                        .show();
                                etName.setText("");
                                etAddress.setText("");
                            } else if (status.equals("DUPLICATE")) {
                                prompt.setMessage("Establishment has already been submitted for confirmation.")
                                        .setNegativeButton("Okay", null)
                                        .create()
                                        .show();
                                etName.setText("");
                                etAddress.setText("");
                            } else {
                                prompt.setMessage("Something went wrong. Try again.")
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
                        parameters.put("name", etName.getText().toString());
                        parameters.put("location", etAddress.getText().toString());
                        return parameters;
                    }
                };
                queue.add(request);
            }
        });

        return myView;
    }
}
