package org.assignment;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
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
import com.google.gson.Gson;

import org.assignment.modals.Token;
import org.assignment.utils.URL;
import org.assignment.utils.UtilsFunction;

import java.util.HashMap;
import java.util.Map;

public class LoginActivity extends AppCompatActivity implements View.OnClickListener {

    private EditText email, password;
    private Button login_button;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        email = findViewById(R.id.email);
        password = findViewById(R.id.password);
        login_button = findViewById(R.id.login_button);

        setListeners();
    }

    private void setListeners() {
        login_button.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        validation();
    }

    private void validation() {
        String email_str = email.getText().toString();
        String password_str = password.getText().toString();
        if (!UtilsFunction.isEmailValid(email_str)) {
            email.requestFocus();
            email.setError("Please enter valid email");
        } else if (TextUtils.isEmpty(password_str)) {
            password.requestFocus();
            password.setError("Please enter valid password");
        } else {
            loginUser(email_str, password_str);
        }
    }

    private void loginUser(String email, String password) {
        final Gson gson = new Gson();
        StringRequest request = new StringRequest(Request.Method.POST, URL.LOGIN, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                Token token = gson.fromJson(response, Token.class);
                if (!TextUtils.isEmpty(token.getToken())) {
                    startActivity(new Intent(LoginActivity.this, Home.class));
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                error.printStackTrace();
                Toast.makeText(LoginActivity.this, "Something went wrong", Toast.LENGTH_SHORT).show();
            }
        }){

            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                Map<String, String> params = new HashMap<String, String>();
                params.put("Content-Type", "application/x-www-form-urlencoded");
                return params;
            }

            @Nullable
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String, String> loginCredentials = new HashMap<>();
                loginCredentials.put("email", email);
                loginCredentials.put("password", password);
                return loginCredentials;
            }
        };

        RequestQueue queue = Volley.newRequestQueue(this);
        queue.add(request);
        queue.start();
    }
}