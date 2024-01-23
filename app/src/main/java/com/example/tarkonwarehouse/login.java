package com.example.tarkonwarehouse;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.os.StrictMode;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.Spinner;
import android.widget.Toast;
import android.widget.ArrayAdapter;

import androidx.appcompat.app.AppCompatActivity;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

import at.favre.lib.crypto.bcrypt.BCrypt;


public class login extends AppCompatActivity {

    private Spinner usernameSpinner;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.login);

        Button button = (Button) findViewById(R.id.loginButton);
        usernameSpinner = findViewById(R.id.usernameSpinner);


        Connection connection = connectionclass();
        try {
            if (connection != null) {
                String query = "SELECT imie_nazwisko FROM PartCheck.dbo.Persons where [user] = '' ;";
                Statement st = connection.createStatement();
                ResultSet rs = st.executeQuery(query);

                List<String> usernamesList = new ArrayList<>();

                while (rs.next()) {
                    String username = rs.getString("imie_nazwisko");
                    usernamesList.add(username);
                }

                // Konwertuj List na tablicę String[]
                String[] usernames = usernamesList.toArray(new String[0]);

                ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, usernames);
                adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

                usernameSpinner.setAdapter(adapter);
            }

        } catch (SQLException e) {
            e.printStackTrace();
            Toast.makeText(login.this, "Wystąpił błąd podczas pobierania danych z bazy", Toast.LENGTH_SHORT).show();
        }

        // Obsłuż wybór z listy
        usernameSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parentView, View selectedItemView, int position, long id) {
                String selectedUsername = (String) parentView.getItemAtPosition(position);
                Toast.makeText(login.this, "Wybrano: " + selectedUsername, Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parentView) {
                // Nie trzeba implementować, ale musi być obecna ze względu na interfejs AdapterView.OnItemSelectedListener
            }
        });

        // Obsługa przycisku logowania
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String selectedUsername = (String) usernameSpinner.getSelectedItem();
                Intent intent = new Intent(login.this, main.class);
                intent.putExtra("user", selectedUsername);
                startActivity(intent);
                finish();
            }
        });
    }

    @SuppressLint("NewApi")
    public Connection connectionclass(){
        Connection con=null;
        String ip="10.100.100.48", port="49827",username="Sa",password="Shark1445NE$T", databasename="PartCheck";
        StrictMode.ThreadPolicy tp= new StrictMode.ThreadPolicy.Builder().permitAll().build();
        StrictMode.setThreadPolicy(tp);
        try{
            Class.forName("net.sourceforge.jtds.jdbc.Driver");
            String connectionUrl="jdbc:jtds:sqlserver://"+ip+":"+port+";databasename="+databasename+";User="+username+";password="+password+";";
            con= DriverManager.getConnection(connectionUrl);
        }catch(Exception exception){
            Log.e("Error",exception.getMessage());
        }
        return con;
    }



}
