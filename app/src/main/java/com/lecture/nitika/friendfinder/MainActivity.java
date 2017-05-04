package com.lecture.nitika.friendfinder;

import android.content.Intent;
import android.os.AsyncTask;
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

public class MainActivity extends AppCompatActivity implements View.OnClickListener {
    EditText username, password;
    String response="";
    Button login, register;
    String[] input=new String[2];
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

    }

    @Override
    protected void onResume() {
        super.onResume();



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

                        /**
                         * Authenticate if its a valid user, redirect to maps activity
                         */
                        input[0]=username.getText().toString();
                        input[1]=password.getText().toString();
                        new InvokeWebService().execute(input);
                        Intent actual=new Intent(getApplicationContext(),FinderActivity.class);
                        startActivity(actual);
                }
                break;
            case R.id.register:
                /**
                 * Go to register activity on Register button click
                 */
                Intent intent=new Intent(this,RegisterActivity.class);
                intent.putExtra("user","username");
                startActivity(intent);
                break;
        }
    }



    private class InvokeWebService extends AsyncTask<String, Integer,String> {

        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);
            loginService.cancel(true);
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
            String requestURL="http://10.0.0.34/databases/index.php?";
            HttpURLConnection httpURLConnection=null;

            try{
                url=new URL(requestURL);
                httpURLConnection=(HttpURLConnection)url.openConnection();
                httpURLConnection.setDoInput(true);
                httpURLConnection.setDoOutput(true);

                OutputStream outputStream=httpURLConnection.getOutputStream();
                BufferedWriter bufferedWriter= new BufferedWriter(new OutputStreamWriter(outputStream));
                StringBuilder builder=new StringBuilder();
                builder.append("username="+params[0]+"&").append("password="+params[1]);
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
