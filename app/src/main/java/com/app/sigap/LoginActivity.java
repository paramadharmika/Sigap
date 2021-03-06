package com.app.sigap;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Typeface;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.app.master.MainMenuActivity;
import com.app.sources.SQLConnection;

import java.util.HashMap;
import java.util.Map;

/**
 * A login screen that offers login via email/password.
 */
public class LoginActivity extends AppCompatActivity {

    /**
     * Variable boolean for checking user login or not.
     * Set this var as false.
     * */
    private boolean login = false;

    /**
     * UI Reference
     * */
    private Button button_login;
    private EditText text_password;
    private EditText text_username;
    private TextView label_forget_password;
    private TextView label_signup;
    private TextView label_password;
    private TextView label_username;
    /**
     * End of UI Reference
     * */

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        this.requestWindowFeature(Window.FEATURE_NO_TITLE);

        getWindow().setFlags(
            WindowManager.LayoutParams.FLAG_FULLSCREEN,
            WindowManager.LayoutParams.FLAG_FULLSCREEN
        );

        setContentView(R.layout.activity_login);

        setFont();

        onLoadDevice();

        /**
         * Set method of login activity
         * */
        text_username = (EditText) findViewById(R.id.text_username);
        text_password = (EditText) findViewById(R.id.text_password);

        button_login = (Button) findViewById(R.id.button_login);
        button_login.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                ValidateLogin();
            }
        });

        TextView label_forget_password = (TextView) findViewById(R.id.label_forget_password);
        label_forget_password.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                /**
                 * End of login activity
                 * */
                finishAffinity();

                /**
                 * Go to forget password activity
                 * */
                Intent intent = new Intent(LoginActivity.this, ForgetPasswordActivity.class);
                startActivity(intent);
            }
        });

        TextView label_signup = (TextView) findViewById(R.id.label_signup);
        label_signup.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                /**
                 * End of login activity
                 * */
                finishAffinity();

                /**
                 * Go to signup activity
                 * */
                Intent intent = new Intent(LoginActivity.this, SignupActivity.class);
                startActivity(intent);
            }
        });
    }

    @Override
    protected void onResume() {
        super.onResume();

        onLoadDevice();
    }

    private void onLoadDevice () {
        SharedPreferences sharedPreferences;
        sharedPreferences = getSharedPreferences
                            (
                                SQLConnection.SHARED_PREFERENCE_ID_LOGIN,
                                Context.MODE_PRIVATE
                            );
        login = sharedPreferences.getBoolean
                (
                    SQLConnection.SHARED_PREFERENCE_LOGIN, false
                );

        if (login) {
            /**
             * Show main menu activity
             * */
            Intent intent = new Intent(LoginActivity.this, MainMenuActivity.class);
            startActivity(intent);

            /**
             * End of splash screen
             * */
            finishAffinity();
        }
        else
        {
            /**
             * No activity
             * */
        }
    }

    private void Login(final String username, final String password)
    {
        /**
         * Buatkan Request Dalam bentuk String
         * */
        StringRequest stringRequest = new StringRequest(Request.Method.POST, SQLConnection.URL_LOGIN,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        boolean cancel = false;
                        View focusView = null;

                        /**
                         * Jika Respon server gagal
                         * */
                        if(!response.equalsIgnoreCase(SQLConnection.LOGIN_SUCCESS))
                        {
                            text_username.setError("* username dan password salah");
                            focusView = text_username;
                            cancel = true;
                        }

                        if (cancel)
                        {
                            focusView.requestFocus();
                        }
                        else
                        {
                            /**
                             * Buatkan sebuah shared preference
                             * */
                            SharedPreferences sharedPreferences = LoginActivity.this.getSharedPreferences(
                                SQLConnection.SHARED_PREFERENCE_ID_LOGIN, Context.MODE_PRIVATE
                            );

                            /**
                             * Buatkan Sebuah variabel Editor Untuk penyimpanan Nilai shared preferences
                             * */
                            SharedPreferences.Editor editor = sharedPreferences.edit();

                            /**
                             * Tambahkan Nilai ke Editor
                             * */
                            editor.putBoolean(SQLConnection.SHARED_PREFERENCE_LOGIN, true);
                            editor.putString(SQLConnection.SHARED_PREFERENCE_USERNAME, username);

                            /**
                             * Simpan Nilai ke Variabel editor
                             * */
                            editor.commit();

                            /**
                             * Start main menu activity
                             * */
                            Intent intent = new Intent(
                                LoginActivity.this, MainMenuActivity.class
                            );
                            startActivity(intent);

                            /**
                             * End of login activity
                             * */
                            finishAffinity();
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        /**
                         * Tambahkan apa yang terjadi setelah Pesan Error muncul, alternatif
                         * */
                        //error.printStackTrace();
                    }
                }){
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String,String> params = new HashMap<>();
                /**
                 * Tambahkan Parameter username dan password untuk Request
                 * */
                params.put(SQLConnection.KEY_USERNAME, username);
                params.put(SQLConnection.KEY_PASSWORD, password);

                /**
                 * Kembalikan Nilai parameter
                 * */
                return params;
            }
        };

        /**
         * Tambahkan Request String ke dalam Queue
         * */
        RequestQueue requestQueue = Volley.newRequestQueue(this);
        requestQueue.add(stringRequest);
    }

    private void ValidateLogin ()
    {
        /**
         * Variables
         * */
        String username = text_username.getText().toString();
        String password = text_password.getText().toString();

        boolean cancel = false;
        View focusView = null;

        if (TextUtils.isEmpty(username))
        {
            text_username.setError("* harus diisi");
            focusView = text_username;
            cancel = true;
        }
        else if (TextUtils.isEmpty(password))
        {
            text_password.setError("* harus diisi");
            focusView = text_password;
            cancel = true;
        }
        else if (password.length() < 6)
        {
            text_password.setError("* min password 6 karakter");
            focusView = text_password;
            cancel = true;
        }

        if (cancel)
        {
            focusView.requestFocus();
        }
        else
        {
            /**
             * Go to main user interface
             * */
            Login(username, password);
        }
    }

    @SuppressWarnings("")
    private void setFont()
    {
        button_login = (Button) findViewById(R.id.button_login);
        label_forget_password = (TextView) findViewById(R.id.label_forget_password);
        label_signup = (TextView) findViewById(R.id.label_signup);
        label_password = (TextView) findViewById(R.id.label_password);
        label_username = (TextView) findViewById(R.id.label_username);
        text_password = (EditText) findViewById(R.id.text_password);
        text_username = (EditText) findViewById(R.id.text_username);

        /**
         * Set typeface
         * */
        Typeface typeface = Typeface.createFromAsset(
            getApplicationContext().getAssets(),
            "fonts/titillium_regular_webfont.ttf"
        );

        /**
         * Set custom fonts
         * */
        button_login.setTypeface(typeface);
        label_forget_password.setTypeface(typeface);
        label_signup.setTypeface(typeface);
        label_password.setTypeface(typeface);
        label_username.setTypeface(typeface);
        text_password.setTypeface(typeface);
        text_username.setTypeface(typeface);
    }

}

