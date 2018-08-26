package sheldon.com.android.gohomev2.activities;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.SharedPreferences;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import org.json.JSONException;
import org.json.JSONObject;

import java.math.BigInteger;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

import sheldon.com.android.gohomev2.asynctask.LoopJ;
import sheldon.com.android.gohomev2.R;
import sheldon.com.android.gohomev2.asynctask.LoopJListener;

public class LoginActivity extends AppCompatActivity implements LoopJListener {
    public static final String PREFS_NAME = "LoginPrefs";

    private EditText mUsername, mPassword;
    private String username, password;
    private Button mButtonLogin;
    private LoopJ client;
    private SharedPreferences sharedPref;
    private boolean logStat;

    @SuppressLint("CommitPrefEdits")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        getSupportActionBar().hide();

        mUsername = (EditText) findViewById(R.id.input_username);
        mPassword = (EditText) findViewById(R.id.input_password);
        mButtonLogin = (Button) findViewById(R.id.btn_login);

        client = new LoopJ(this, this);

        sharedPref = getSharedPreferences(PREFS_NAME, 0);
        logStat = sharedPref.getBoolean(getString(R.string.saved_log_stat), false);

        Log.d("LOG_STAT", "onCreate: LoginActvt " + logStat);
        if (logStat) {
            goToMainActivity();
            onLoginSuccess();
        }
    }

    @Override
    public void authenticate(JSONObject response) {
        Log.d("AUTHSTATUS", "authenticate: " + response);
        try {
            String logStat = response.getString("logStat");
            if (logStat.equals("SUCCESS")) {
                Toast.makeText(LoginActivity.this, "Logged in", Toast.LENGTH_SHORT).show();

                //make SharedPreferences object
                sharedPref.edit().putBoolean(getString(R.string.saved_log_stat), true).apply();
                sharedPref.edit().putString(getString(R.string.saved_user_name), response.getString("username")).apply();
                sharedPref.edit().putString(getString(R.string.saved_full_name), response.getString("full_name")).apply();
                sharedPref.edit().putString(getString(R.string.saved_email), response.getString("email")).apply();
                sharedPref.edit().putString(getString(R.string.saved_role), response.getString("role")).apply();
                sharedPref.edit().putString(getString(R.string.saved_token), response.getString("token")).apply();

                goToMainActivity();
                onLoginSuccess();
            } else {
                Toast.makeText(LoginActivity.this, logStat, Toast.LENGTH_SHORT).show();
                onLoginFailed();
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    public void login(View view) {
        mButtonLogin.setEnabled(false);

        username = mUsername.getText().toString();
        password = mPassword.getText().toString();

        if (!validate(username, password)) return;

        client.sendLoginRequest(username, convertPassMd5(password));
    }

    private boolean validate(String username, String password) {
        boolean valid = true;

        if (username.isEmpty() || username.length() < 3) {
            mUsername.setError("Enter a valid username");
            valid = false;
        } else {
            mUsername.setError(null);
        }

        if (password.isEmpty() || password.length() < 4) {
            mPassword.setError("Must be filled");
            valid = false;
        } else {
            mPassword.setError(null);
        }

        return valid;
    }

    private void onLoginSuccess() {
        mButtonLogin.setEnabled(true);
        finish();
    }

    private void onLoginFailed() {
        mButtonLogin.setEnabled(true);
    }

    private static String convertPassMd5(String pass) {
        String password = null;
        MessageDigest mdEnc;
        try {
            mdEnc = MessageDigest.getInstance("MD5");
            mdEnc.update(pass.getBytes(), 0, pass.length());
            pass = new BigInteger(1, mdEnc.digest()).toString(16);
            while (pass.length() < 32) {
                pass = "0" + pass;
            }
            password = pass;
        } catch (NoSuchAlgorithmException e1) {
            e1.printStackTrace();
        }
        return password;
    }

    private void goToMainActivity() {
        Intent intent = new Intent(LoginActivity.this, MainActivity.class);
        startActivity(intent);
    }
}