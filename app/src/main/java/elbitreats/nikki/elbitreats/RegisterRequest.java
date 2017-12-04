package elbitreats.nikki.elbitreats;

import com.android.volley.Response;
import com.android.volley.toolbox.StringRequest;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by Erika on 9/14/2017.
 */

public class RegisterRequest extends StringRequest {
    private static final String REGISTER_REQUEST_URL = "https://indolent-secretarie.000webhostapp.com/Register.php";
    private Map<String, String> params;

    public RegisterRequest(String username, String password, String name, String nickname, String email_address, String birthday, String gender, String contact_no, String age, Response.Listener<String> listener) {
        super(Method.POST, REGISTER_REQUEST_URL, listener, null);
        params = new HashMap<>();
        params.put("username", username);
        params.put("password", password);
        params.put("name", name);
        params.put("nickname", nickname);
        params.put("email_address", email_address);
        params.put("birthday", birthday);
        params.put("gender", gender);
        params.put("contact_no", contact_no);
        params.put("age", age);
    }

    @Override
    public Map<String, String> getParams() {
        return params;
    }
}
