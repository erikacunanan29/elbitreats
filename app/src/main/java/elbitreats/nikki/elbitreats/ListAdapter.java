package elbitreats.nikki.elbitreats;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONException;
import org.json.JSONObject;
import org.w3c.dom.Text;

import java.util.ArrayList;

/**
 * Created by Erika on 10/23/2017.
 */

public class ListAdapter extends BaseAdapter{
    public static final int ITEM = 0;
    public static final int HEADER = 1;

    private LayoutInflater inflater;
    ArrayList<Object> suggested;
    String design;

    public ListAdapter(Context context, ArrayList<Object> suggest, String design) {
        this.suggested = suggest;
        this.design = design;
        inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }

    @Override
    public int getItemViewType(int position) {
        if (suggested.get(position) instanceof String) {
            return HEADER;
        } else {
            return ITEM;
        }
    }

    @Override
    public int getViewTypeCount() {
        return 2;
    }

    @Override
    public int getCount() {
        return suggested.size();
    }

    @Override
    public Object getItem(int position) {
        return suggested.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        if (convertView == null) {
            switch(getItemViewType(position)) {
                case ITEM:
                    if (design.equals("DEFAULT")) {
                        convertView = inflater.inflate(R.layout.listview_item, null);
                    } else if (design.equals("MENU")){
                        convertView = inflater.inflate(R.layout.listview_menuitem, null);
                    } else if (design.equals("FEEDBACK")) {
                        convertView = inflater.inflate(R.layout.listview_feedbackitem, null);
                    }
                    break;
                case HEADER:
                    convertView = inflater.inflate(R.layout.listview_header, null);
                    break;
            }
        }

        switch(getItemViewType(position)) {
            case ITEM:
                if (design.equals("DEFAULT")) {
                    TextView tvItem = (TextView) convertView.findViewById(R.id.listviewItem);
                    try {
                        tvItem.setText(((JSONObject) suggested.get(position)).getString("name"));
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                } else if (design.equals("MENU")) {
                    TextView name = (TextView) convertView.findViewById(R.id.foodName);
                    TextView price = (TextView) convertView.findViewById(R.id.foodPrice);
                    TextView type = (TextView) convertView.findViewById(R.id.foodCategory);
                    TextView desc = (TextView) convertView.findViewById(R.id.foodDescription);
                    TextView add = (TextView) convertView.findViewById(R.id.foodAdditional);

                    try {
                        JSONObject temp = (JSONObject) suggested.get(position);
                        String tempName = "";
                        if ((temp.getString("bestseller")).equals("1")) {
                            name.setCompoundDrawablesWithIntrinsicBounds(R.drawable.ic_resto_star, 0, 0, 0);
                        } else {
                            name.setCompoundDrawablesWithIntrinsicBounds(0, 0, 0, 0);
                        }

                        if ((temp.getString("code")).equals("")) {
                            tempName = " " + temp.getString("food_name");
                        } else {
                            tempName = " " + temp.getString("code") + " " + temp.getString("food_name");
                        }

                        name.setText(tempName);
                        price.setText(temp.getString("price"));
                        type.setText("FOOD TYPE: " + temp.getString("food_type"));

                        String tempDesc = temp.getString("description");
                        if (!tempDesc.equals("")) {
                            desc.setVisibility(View.VISIBLE);
                            desc.setText(tempDesc);
                        } else {
                            desc.setVisibility(View.GONE);
                        }

                        String tempAdd = temp.getString("additional");
                        if (!tempAdd.equals("")) {
                            add.setVisibility(View.VISIBLE);
                            add.setText(tempAdd);
                        } else {
                            add.setVisibility(View.GONE);
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                } else if (design.equals("FEEDBACK")) {
                    TextView name = (TextView) convertView.findViewById(R.id.usernameText);
                    TextView date = (TextView) convertView.findViewById(R.id.datepostedText);
                    TextView review = (TextView) convertView.findViewById(R.id.reviewText);

                    JSONObject temp = (JSONObject) suggested.get(position);
                    try {
                        if (temp.getInt("anonymous") == 1) {
                            name.setText("Anonymous");
                        } else {
                            name.setText(temp.getString("user_name"));
                        }
                        date.setText(temp.getString("date_posted"));
                        review.setText(temp.getString("review"));
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
                break;
            case HEADER:
                TextView tvHeader = (TextView) convertView.findViewById(R.id.listviewHeader);
                tvHeader.setText((String) suggested.get(position));
                break;
        }

        return convertView;
    }
}
