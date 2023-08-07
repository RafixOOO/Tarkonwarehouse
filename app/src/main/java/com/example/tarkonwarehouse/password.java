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
import android.widget.Toast;

import java.sql.Connection;
import java.sql.DriverManager;

public class password extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_password);


        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        String jwtToken = getIntent().getStringExtra("user");

        Log.d("JWT", "Token "+jwtToken);

        // Weryfikacja tokenu
        if (jwtToken != null) {

        } else {
            Intent intent = new Intent(this, login.class);
            startActivity(intent);

        }
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