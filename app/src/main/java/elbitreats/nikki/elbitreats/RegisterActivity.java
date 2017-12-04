package elbitreats.nikki.elbitreats;

import android.app.DatePickerDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.content.Intent;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.toolbox.Volley;

import org.json.JSONException;
import org.json.JSONObject;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;

public class RegisterActivity extends AppCompatActivity implements DatePickerDialog.OnDateSetListener {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        final EditText etusername = (EditText) findViewById(R.id.etUsername);
        final EditText etpassword = (EditText) findViewById(R.id.etPassword);
        final EditText etname = (EditText) findViewById(R.id.etName);
        final EditText etnickname = (EditText) findViewById(R.id.etNickname);
        final EditText etemail = (EditText) findViewById(R.id.etEmail);
        final EditText etnumber = (EditText) findViewById(R.id.etNumber);
        final EditText etbirthday = (EditText) findViewById(R.id.etBirthday);

        final Button register = (Button) findViewById(R.id.bRegister);

        final RadioGroup gender = (RadioGroup) findViewById(R.id.rgGender);

        register.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final String user = etusername.getText().toString();
                final String pass = etpassword.getText().toString();
                final String name = etname.getText().toString();
                final String nickname = etnickname.getText().toString();
                final String email = etemail.getText().toString();
                final String contact = etnumber.getText().toString();
                final String bday = etbirthday.getText().toString();

                final String age = calculateAge(bday) + "";

                int selected_id = gender.getCheckedRadioButtonId();
                final RadioButton fm = (RadioButton) findViewById(selected_id);
                final String sex;
                if ((fm.getText().toString()).equals("Female")) {
                    sex = 'F' + "";
                } else {
                    sex = 'M' + "";
                }

                Response.Listener<String> responseListener = new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        try {
                            JSONObject jsonResponse = new JSONObject(response);
                            boolean success = jsonResponse.getBoolean("success");

                            if (success) {
                                Intent intent = new Intent(RegisterActivity.this, LoginActivity.class);
                                RegisterActivity.this.startActivity(intent);
                                AlertDialog.Builder builder = new AlertDialog.Builder(RegisterActivity.this);
                                builder.setMessage(fm.getText().toString())
                                        .setNegativeButton("Retry", null)
                                        .create().show();
                            } else {
                                AlertDialog.Builder builder = new AlertDialog.Builder(RegisterActivity.this);
                                builder.setMessage("Register Failed")
                                        .setNegativeButton("Retry", null)
                                        .create().show();
                            }

                        } catch (JSONException e) {
                            e.printStackTrace();
                        }

                    }
                };

                RegisterRequest registerRequest = new RegisterRequest(user, pass, name, nickname, email, bday, sex, contact, age, responseListener);
                RequestQueue queue = Volley.newRequestQueue(RegisterActivity.this);
                queue.add(registerRequest);
            }
        });
    }

    public void datePicker(View view) {
        DatePickerFragment fragment = new DatePickerFragment();
        fragment.show(getFragmentManager(), "date");
    }

    private void setDate(final Calendar calendar) {
        SimpleDateFormat format = new SimpleDateFormat("dd-MM-yyyy");
        String dateString = format.format(calendar.getTime());
        ((TextView) findViewById(R.id.etBirthday)).setText(dateString);
    }

    @Override
    public void onDateSet(DatePicker view, int year, int month, int day) {
        Calendar cal = new GregorianCalendar(year, month, day);
        setDate(cal);
    }

    public static class DatePickerFragment extends DialogFragment {
        @Override
        public Dialog onCreateDialog(Bundle savedInstanceState) {
            final Calendar c = Calendar.getInstance();
            int year = c.get(Calendar.YEAR);
            int month = c.get(Calendar.MONTH);
            int day = c.get(Calendar.DAY_OF_MONTH);

            return new DatePickerDialog(getActivity(), (DatePickerDialog.OnDateSetListener) getActivity(), year, month, day);
        }
    }

    public int calculateAge(String birthday) {
        String[] bday = birthday.split("-");
        int day = Integer.parseInt(bday[0]);
        int month = Integer.parseInt(bday[1]);
        int year = Integer.parseInt(bday[2]);

        String date = new SimpleDateFormat("dd-MM-yyyy").format(new Date());
        String [] curDate = date.split("-");
        int curDay = Integer.parseInt(curDate[0]);
        int curMon = Integer.parseInt(curDate[1]);
        int curYear = Integer.parseInt(curDate[2]);

        int age = curYear - year;

        if ((curMon < month) || (curMon == month && curDay < day)) {
            age = age - 1;
        }

        return age;
    }
}
