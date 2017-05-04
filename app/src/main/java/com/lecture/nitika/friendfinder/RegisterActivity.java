package com.lecture.nitika.friendfinder;

import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.AsyncTask;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

public class RegisterActivity extends AppCompatActivity implements View.OnClickListener, LocationListener {
    EditText username, password, confirm, email;
    Button register;
    LocationManager locationManager;
    private boolean isLocationUpdated = false;
    String[] input = new String[5];
    RegisterService registerService = new RegisterService();

    double latitude, longitude;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);
        username = (EditText) findViewById(R.id.userText);
        password = (EditText) findViewById(R.id.passText);
        confirm = (EditText) findViewById(R.id.confirmText);
        email = (EditText) findViewById(R.id.emailText);
        register = (Button) findViewById(R.id.register);
        register.setOnClickListener(this);
        locationManager = (LocationManager) getSystemService(LOCATION_SERVICE);
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            return;
        }
        locationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, 0, 0, this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.register:
                if ((password.getText().toString().equals(confirm.getText().toString())) && (!email.getText().toString().equals("")) && (!password.getText().toString().equals("")) && (!username.getText().toString().equals("")) && (email.getText().toString().contains("@"))) {
                    if (longitude != 0 && latitude != 0) {
                        Log.i("Register", "lat" + latitude + "long" + longitude);

                        input[0] = username.getText().toString();
                        input[1] = password.getText().toString();
                        input[2] = email.getText().toString();
                        input[3] = (new Double(latitude)).toString();
                        input[4] = (new Double(longitude)).toString();
                        new RegisterService().execute(input);
                        Toast.makeText(getApplicationContext(), "Registration Successful", Toast.LENGTH_LONG).show();

                        Intent intent = new Intent(getApplicationContext(), MainActivity.class);
                        startActivity(intent);
                    }

                } else if (!password.getText().toString().equals(confirm.getText().toString())) {
                    confirm.setError("Passwords don't match");
                } else if (email.getText().toString().equals("")) {
                    email.setError("Enter email");
                } else if (username.getText().toString().equals("")) {
                    username.setError("Enter username");
                } else if (!email.getText().toString().contains("@")) {
                    email.setError("Email should contain @");
                }
                break;
        }
    }

    @Override
    public void onLocationChanged(Location location) {
        if (!isLocationUpdated) {
            latitude = location.getLatitude();
            longitude = location.getLongitude();
            Log.i("Register", "Got lat and long" + latitude + "long" + longitude);
            if (!isLocationUpdated) {
                isLocationUpdated = true;

                if (ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                    // TODO: Consider calling

                    return;
                }
                locationManager.removeUpdates(this);
            }
        }
    }

    @Override
    public void onStatusChanged(String provider, int status, Bundle extras) {

    }

    @Override
    public void onProviderEnabled(String provider) {

    }

    @Override
    public void onProviderDisabled(String provider) {

    }


    private class RegisterService extends AsyncTask<String, Integer,String> {

        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);
            registerService.cancel(true);

        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }

        @Override
        protected void onProgressUpdate(Integer... values) {
            super.onProgressUpdate(values);
        }

        @Override
        protected String doInBackground(String... params) {
            Log.d("Register","im here");
            URL url;
            String response="";
            HttpURLConnection connection=null;
            try{
                String urlstring="http://10.0.0.34/databases/index.php";
                urlstring+="?username="+params[0]+"&password="+params[1]+"&email="+params[2]+"&latitude="+params[3]+"&longitude="+params[4];
                url=new URL(urlstring);
                connection=(HttpURLConnection)url.openConnection();
                int code=connection.getResponseCode();
                Log.i("Register","respcode"+code);
                if(code==HttpURLConnection.HTTP_OK){
                    String line;
                    BufferedReader br=new BufferedReader(new InputStreamReader(connection.getInputStream()));
                    line=br.readLine();
                    while(line!=null){
                        response+=line;
                        line=br.readLine();
                    }
                }
                connection.disconnect();
            }catch (Exception e){
                e.printStackTrace();
            }
            return null;
        }
    }
}

