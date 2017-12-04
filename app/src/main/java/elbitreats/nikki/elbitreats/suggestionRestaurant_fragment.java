package elbitreats.nikki.elbitreats;

import android.content.Intent;
import android.support.v4.app.Fragment;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AlertDialog;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

/**
 * Created by ATHCMCC on 10/22/2017.
 */

public class suggestionRestaurant_fragment extends Fragment {
    Bundle bundle;
    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
        View suggestedRestaurant = inflater.inflate(R.layout.suggestion_restaurant, container, false);
        final ListView restaurantList = (ListView) suggestedRestaurant.findViewById(R.id.restaurantList);

        bundle = getArguments();
        ArrayList<Object> listData = new ArrayList<Object>();
        if (bundle != null) {
            try {
                JSONArray temp = new JSONArray(bundle.getString("restaurant"));
                for(int i=0; i < temp.length(); i++) {
                    JSONObject json = temp.getJSONObject(i);
                    listData.add(json.getString("foodterm"));

                    JSONArray tempSuggestions = json.getJSONArray("suggestions");
                    for(int count = 0; count < tempSuggestions.length(); count++) {
                        listData.add(tempSuggestions.getJSONObject(count));
                    }
                }

            } catch (JSONException e) {
                e.printStackTrace();
            }
        }


        final ListAdapter adapter = new ListAdapter(getActivity(), listData, "DEFAULT");
        restaurantList.setAdapter(adapter);
        restaurantList.isClickable();

        restaurantList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                if (adapter.getItemViewType(position) == adapter.ITEM) {
                    JSONObject resto = (JSONObject) restaurantList.getItemAtPosition(position);
                    try {
                        Intent intent = new Intent(getActivity(), RestaurantActivity.class);
                        intent.putExtra("restaurantid", resto.getString("id"));
                        intent.putExtra("userid", bundle.getString("userid"));
                        intent.putExtra("name", bundle.getString("name"));
                        intent.putExtra("restoName", resto.getString("name"));
                        getActivity().startActivity(intent);
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
            }
        });

        return suggestedRestaurant;
    }

}
