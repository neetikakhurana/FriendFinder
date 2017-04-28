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
import java.io.BufferedWriter;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.URL;

public class RegisterActivity extends AppCompatActivity implements View.OnClickListener, LocationListener {
    EditText username, password, confirm, email;
    Button register;
    LocationManager locationManager;

    InvokeWebService registerService=new InvokeWebService();

    double latitude,longitude;
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
        switch(v.getId()){
            case R.id.register:
                if((password.getText().toString().equals(confirm.getText().toString())) && (!email.getText().toString().equals("")) && (!password.getText().toString().equals("")) && (!username.getText().toString().equals("")) && (email.getText().toString().contains("@"))){
                    if(longitude!=0 && latitude!=0) {
                        Log.i("Register","lat"+latitude+"long"+longitude);
                        String[] input=new String[5];
                        input[0]=username.getText().toString();
                        input[1]=password.getText().toString();
                        input[2]=email.getText().toString();
                        input[3]=(new Double(latitude)).toString();
                        input[4]=(new Double(longitude)).toString();
                        registerService.execute(input);
                        Toast.makeText(getApplicationContext(), "Registration Successful", Toast.LENGTH_LONG).show();
                        Intent intent = new Intent(this, MainActivity.class);
                        startActivity(intent);
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
                else if(!password.getText().toString().equals(confirm.getText().toString())){
                    confirm.setError("Passwords don't match");
                }
                else if(email.getText().toString().equals("")){
                    email.setError("Enter email");
                }
                else if(username.getText().toString().equals("")){
                    username.setError("Enter username");
                }
                else if(!email.getText().toString().contains("@")){
                    email.setError("Email should contain @");
                }
                break;
        }
    }

    @Override
    public void onLocationChanged(Location location) {
        latitude=location.getLatitude();
        longitude=location.getLongitude();
        Log.i("Register","Got lat and long"+latitude+"long"+longitude);
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
            String requestURL="http://localhost/databases/index.php?";
            try{
                url=new URL(requestURL);
                HttpURLConnection httpURLConnection=(HttpURLConnection)url.openConnection();
                httpURLConnection.setRequestMethod("POST");
                 httpURLConnection.setDoInput(true);
                httpURLConnection.setDoOutput(true);

                OutputStream outputStream=httpURLConnection.getOutputStream();
                BufferedWriter bufferedWriter= new BufferedWriter(new OutputStreamWriter(outputStream));
                StringBuilder builder=new StringBuilder();
                builder.append("username="+params[0]+"&").append("password="+params[1]+"&").append("email="+params[2]+"&").append("latitude="+params[3]+"&").append("longitude="+params[4]);
                String str=builder.toString();
                Log.i("Register","string"+str);
                bufferedWriter.write(str);
                bufferedWriter.flush();
                bufferedWriter.close();

                int responseCode=httpURLConnection.getResponseCode();
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
            }catch (Exception e){
                e.printStackTrace();
            }
            return null;
        }
    }
}

