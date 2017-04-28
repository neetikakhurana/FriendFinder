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

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.URL;

public class MainActivity extends AppCompatActivity implements View.OnClickListener, LocationListener {
    EditText username, password;
    Button login, register;
    LocationManager locationManager;
    double latitude,longitude;
    String[] input=new String[4];
    InvokeWebService loginService=new InvokeWebService();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        username = (EditText) findViewById(R.id.user);
        password = (EditText) findViewById(R.id.pass);
        login = (Button) findViewById(R.id.login);
        register = (Button) findViewById(R.id.register);
        login.setOnClickListener(this);
        register.setOnClickListener(this);
        locationManager = (LocationManager) getSystemService(LOCATION_SERVICE);
        input[0]=username.getText().toString();
        input[1]=password.getText().toString();
        input[2]=(new Double(latitude)).toString();
        input[3]=(new Double(latitude)).toString();
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
        switch(v.getId()){
            case R.id.username:
                username.getText().clear();
                break;
            case R.id.login:
                if(username.getText().toString().equals("") && password.getText().toString().equals("")){
                    username.setError("Enter Username");
                    password.setError("Enter password");
                }
                else if(username.getText().toString().equals("")){
                    username.setError("Enter username");
                }
                else if(password.getText().toString().equals("")){
                    password.setError("Enter password");
                }
                else{
                    if(latitude!=0 && longitude!=0){

                        /**
                         * Authenticate if its a valid user
                         */
                        Log.i("Login","lat"+latitude+"long"+longitude);
                        Intent actual=new Intent(this,FinderActivity.class);
                        actual.putExtra("latitude",latitude);
                        actual.putExtra("longitude",longitude);
                        startActivity(actual);
                    }
                    else{
                        do{
                            try {
                                wait(30);
                            }catch (Exception e){
                                e.printStackTrace();
                            }
                        }while(latitude!=0 && longitude!=0);
                    }
                }
                break;
            case R.id.register:
                Intent intent=new Intent(this,RegisterActivity.class);
                intent.putExtra("user","username");
                startActivity(intent);
                break;
        }
    }

    @Override
    public void onLocationChanged(Location location) {
        latitude=location.getLatitude();
        longitude=location.getLongitude();
        //loginService.execute(input);

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

    private class InvokeWebService extends AsyncTask<String, Integer,String> {

        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);
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
            URL url;
            String response="";
            String requestURL="http://127.0.0.1:80/databases/index.php?";
            HttpURLConnection httpURLConnection=null;

            try{
                url=new URL(requestURL);
                httpURLConnection=(HttpURLConnection)url.openConnection();
                httpURLConnection.setRequestMethod("POST");
                httpURLConnection.setDoInput(true);
                httpURLConnection.setDoOutput(true);

                OutputStream outputStream=httpURLConnection.getOutputStream();
                BufferedWriter bufferedWriter= new BufferedWriter(new OutputStreamWriter(outputStream));
                StringBuilder builder=new StringBuilder();
                builder.append("username="+params[0]+"&").append("password="+params[1]+"&").append("latitude="+params[2]+"&").append("longitude="+params[3]);
                String str=builder.toString();
                bufferedWriter.write(str);
                bufferedWriter.flush();
                bufferedWriter.close();

                int responseCode=httpURLConnection.getResponseCode();
                Log.i("Login","respcode"+responseCode);
                if(responseCode==HttpURLConnection.HTTP_OK){
                    String line;
                    BufferedReader bufferedReader=new BufferedReader(new InputStreamReader(httpURLConnection.getInputStream()));
                    line=bufferedReader.readLine();
                    while (line!=null){
                        response+=line;
                        line=bufferedReader.readLine();
                    }
                    bufferedReader.close();
                }
                Log.i("Login","resp"+response);
            }catch (Exception e){
                e.printStackTrace();
            }
            finally {
                if(httpURLConnection!=null){
                    httpURLConnection.disconnect();
                }
            }
            return response;
        }
    }
}
