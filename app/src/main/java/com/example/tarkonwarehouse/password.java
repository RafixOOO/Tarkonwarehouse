package com.example.tarkonwarehouse;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.os.StrictMode;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

import at.favre.lib.crypto.bcrypt.BCrypt;

public class password extends AppCompatActivity {

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
        setContentView(R.layout.activity_password);


        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        String jwtToken = getIntent().getStringExtra("user");


        // Weryfikacja tokenu
        if (jwtToken != null) {

        } else {
            Intent intent = new Intent(this, login.class);
            startActivity(intent);

        }

        EditText old = (EditText) findViewById(R.id.oldpasswordEditText);
        EditText newpassword = (EditText) findViewById(R.id.newpasswordEditText);
        Button button = (Button) findViewById(R.id.passwordButton);

        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Connection connection = connectionclass();
                try {
                    if (connection != null) {
                        String query = "SELECT * FROM [PartCheck].[dbo].[Persons] WHERE [user] = '" + jwtToken + "';";
                        Statement st = connection.createStatement();
                        ResultSet rs = st.executeQuery(query);

                        if (rs.next()) {
                            String hashedPasswordFromDatabase = rs.getString("password");
                            try {
                                if (comparePasswords(old.getText().toString(), hashedPasswordFromDatabase)) {
                                    // Hasło jest poprawne - przejdź do kolejnej aktywności
                                    Toast.makeText(password.this, "Hasło zostało zmeinione!", Toast.LENGTH_SHORT).show();
                                    String query1 = "UPDATE [dbo].[Persons] SET  [password] ='"+BCrypt.withDefaults().hashToString(10,newpassword.getText().toString().toCharArray())+"' WHERE [user]='" + jwtToken + "';";
                                    Statement st1 = connection.createStatement();
                                    st1.executeUpdate(query1);

                                    Intent intent = new Intent(password.this, main.class);
                                    intent.putExtra("user", jwtToken);
                                    startActivity(intent);
                                }else{
                                    Toast.makeText(password.this, "Hasła nie są takie same!", Toast.LENGTH_SHORT).show();
                                }
                            }catch(Exception exception){
                                Toast.makeText(password.this, "Wystąpił błąd!", Toast.LENGTH_SHORT).show();
                                exception.printStackTrace();
                            }
                        }else {
                            // Hasło jest niepoprawne
                            Toast.makeText(password.this, "Wystapił bład! ", Toast.LENGTH_SHORT).show();
                        }
                    }

                } catch (SQLException e) {
                    throw new RuntimeException(e);
                }
            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.toolbar_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if (item.getItemId() == R.id.change) {
            // Obsługa akcji zmiany
            return true;
        } else if (item.getItemId() == R.id.logout) {
            performLogout();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    private void performLogout() {
        getIntent().removeExtra("jwtToken");
        Intent intent = new Intent(this, login.class);
        startActivity(intent);
        finish(); // Opcjonalnie zamyka aktualną aktywność, aby użytkownik nie mógł wrócić przyciskiem "Wstecz"
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