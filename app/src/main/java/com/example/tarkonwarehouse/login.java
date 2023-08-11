package com.example.tarkonwarehouse;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.os.StrictMode;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;


import androidx.appcompat.app.AppCompatActivity;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

import at.favre.lib.crypto.bcrypt.BCrypt;


public class login extends AppCompatActivity {

    private EditText user;
    private EditText password;
    public static boolean comparePasswords(String plainPassword, String hashedPassword) {
        BCrypt.Verifyer verifyer = BCrypt.verifyer();
        BCrypt.Result result = verifyer.verify(plainPassword.toCharArray(), hashedPassword);
        if (result.verified) {
            return true;
        } else {
            return false;
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.login);
        user = (EditText) findViewById(R.id.usernameEditText);
        password = (EditText) findViewById(R.id.passwordEditText);
        Button button = (Button) findViewById(R.id.loginButton);

        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Connection connection = connectionclass();
                try {
                    if (connection != null) {
                        String query = "SELECT * FROM [PartCheck].[dbo].[Persons] WHERE [user] = '" + user.getText().toString() + "';";
                        Statement st = connection.createStatement();
                        ResultSet rs = st.executeQuery(query);

                        if (rs.next()) {
                            String hashedPasswordFromDatabase = rs.getString("password");
                            try {
                                if (comparePasswords(password.getText().toString(), hashedPasswordFromDatabase)) {
                                    // Hasło jest poprawne - przejdź do kolejnej aktywności
                                    String username = user.getText().toString();
                                    Toast.makeText(login.this, "Uwierzytelnienie powiodło się!", Toast.LENGTH_SHORT).show();
                                    Intent intent = new Intent(login.this, main.class);
                                    intent.putExtra("user", username);
                                    startActivity(intent);
                                    finish();
                                }else{
                                    Toast.makeText(login.this, "Niepoprawne dane logowania! ", Toast.LENGTH_SHORT).show();
                                }
                            }catch(Exception exception){
                                Toast.makeText(login.this, "Wystąpił błąd! ", Toast.LENGTH_SHORT).show();
                                exception.printStackTrace();
                            }
                        }else {
                                // Hasło jest niepoprawne
                                Toast.makeText(login.this, "Brak użytkownika! ", Toast.LENGTH_SHORT).show();
                            }
                        }

                } catch (SQLException e) {
                    throw new RuntimeException(e);
                }
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
