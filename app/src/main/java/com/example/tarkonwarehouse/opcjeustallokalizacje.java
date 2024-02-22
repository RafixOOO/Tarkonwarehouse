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
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import org.w3c.dom.Text;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class opcjeustallokalizacje extends AppCompatActivity {

    private String jwtToken;
    private String name;
    private int number;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_opcjeustallokalizacje);

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        jwtToken = getIntent().getStringExtra("user");
        name = getIntent().getStringExtra("name");
        number = getIntent().getIntExtra("number", 0);

        if (jwtToken != null && !jwtToken.isEmpty()) {
            Toast.makeText(this, "Witaj " + jwtToken, Toast.LENGTH_SHORT).show();
        } else {
            Intent intent = new Intent(this, login.class);
            startActivity(intent);
            finish(); // Dodaj finish, aby zakończyć bieżącą aktywność, jeśli nie ma tokenu
        }

        Button dodaj = findViewById(R.id.dodaj);
        Button usun = findViewById(R.id.usun);
        Button przen = findViewById(R.id.przenies);
        Spinner spinner = findViewById(R.id.spinner);
        TextView text = findViewById(R.id.text);

        Button opcja1 = findViewById(R.id.opcja1);
        Button opcja2 = findViewById(R.id.opcja2);
        Button opcja3 = findViewById(R.id.opcja3);

        opcja1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                dodaj.setVisibility(View.VISIBLE);
                usun.setVisibility(View.GONE);
                przen.setVisibility(View.GONE);
                spinner.setVisibility(View.GONE);
                text.setVisibility(View.GONE);

            }
        });

        opcja2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                dodaj.setVisibility(View.GONE);
                usun.setVisibility(View.GONE);
                przen.setVisibility(View.VISIBLE);
                spinner.setVisibility(View.VISIBLE);
                text.setVisibility(View.VISIBLE);

            }
        });

        opcja3.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                dodaj.setVisibility(View.GONE);
                usun.setVisibility(View.VISIBLE);
                przen.setVisibility(View.GONE);
                spinner.setVisibility(View.GONE);
                text.setVisibility(View.GONE);

            }
        });


        Connection connection = connectionclass();

        dodaj.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                String insertQuery = "INSERT INTO PartCheck.dbo.MagazynExtra (PartID, Person, Localization) VALUES (?, ?, ?)";

                try (PreparedStatement insertStatement = connection.prepareStatement(insertQuery)) {
                    insertStatement.setString(1, name);
                    insertStatement.setString(2, jwtToken);
                    insertStatement.setInt(3, number);

                    insertStatement.executeUpdate();

                    Toast.makeText(opcjeustallokalizacje.this, "Arkusz został dodany", Toast.LENGTH_LONG).show();

                    finish();
                }catch (SQLException e) {
                    e.printStackTrace();
                    Toast.makeText(opcjeustallokalizacje.this, "Wystąpił błąd podczas dostępu do bazy danych", Toast.LENGTH_LONG).show();
                }
            }
        });

        usun.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                String insertQuery = "DELETE FROM PartCheck.dbo.MagazynExtra WHERE PartID = ? AND Localization = ? AND Date = (SELECT MIN(Date) FROM PartCheck.dbo.MagazynExtra WHERE PartID = ? AND Localization = ?)";

                try (PreparedStatement deleteStatement = connection.prepareStatement(insertQuery)) {
                    deleteStatement.setString(1, name);
                    deleteStatement.setInt(2, number);
                    deleteStatement.setString(3, name);
                    deleteStatement.setInt(4, number);

                    int rowsDeleted = deleteStatement.executeUpdate();
                    if (rowsDeleted > 0) {
                        Toast.makeText(opcjeustallokalizacje.this, "Arkusz został usunięty", Toast.LENGTH_LONG).show();
                    } else {
                        Toast.makeText(opcjeustallokalizacje.this, "Błąd", Toast.LENGTH_LONG).show();
                    }

                    finish();
                }catch (SQLException e) {
                    e.printStackTrace();
                    Toast.makeText(opcjeustallokalizacje.this, "Wystąpił błąd podczas dostępu do bazy danych", Toast.LENGTH_LONG).show();
                }
            }
        });

        String spinnerselect = "SELECT DISTINCT Localization FROM PartCheck.dbo.MagazynExtra WHERE PartID = ?";
        List<String> localizations = new ArrayList<>();

        try (PreparedStatement spinnerStatement = connection.prepareStatement(spinnerselect)) {
            spinnerStatement.setString(1, name);

            try (ResultSet resultSet = spinnerStatement.executeQuery()) {
                // Dodawanie lokalizacji do listy
                while (resultSet.next()) {
                    String localization = resultSet.getString("Localization");
                    localizations.add(localization);
                }
            }
        }catch (SQLException e) {
            e.printStackTrace();
            Toast.makeText(opcjeustallokalizacje.this, "Wystąpił błąd podczas dostępu do bazy danych", Toast.LENGTH_LONG).show();
        }

        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, localizations);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner.setAdapter(adapter);


        int selectedLocalization = Integer.parseInt(spinner.getSelectedItem().toString());


        przen.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                String deleteQuery = "DELETE FROM PartCheck.dbo.MagazynExtra WHERE PartID = ? AND Localization = ? AND Date = (SELECT MIN(Date) FROM PartCheck.dbo.MagazynExtra WHERE PartID = ? AND Localization = ?)";

                try (PreparedStatement deleteStatement = connection.prepareStatement(deleteQuery)) {
                    deleteStatement.setString(1, name);
                    deleteStatement.setInt(2, selectedLocalization);
                    deleteStatement.setString(3, name);
                    deleteStatement.setInt(4, selectedLocalization);

                    int rowsDeleted = deleteStatement.executeUpdate();
                    if (rowsDeleted > 0) {
                        Toast.makeText(opcjeustallokalizacje.this, "", Toast.LENGTH_LONG).show();
                    } else {
                        Toast.makeText(opcjeustallokalizacje.this, "Błąd", Toast.LENGTH_LONG).show();
                    }
                }catch (SQLException e) {
                    e.printStackTrace();
                    Toast.makeText(opcjeustallokalizacje.this, "Wystąpił błąd podczas dostępu do bazy danych", Toast.LENGTH_LONG).show();
                }

                String insertQuery = "INSERT INTO PartCheck.dbo.MagazynExtra (PartID, Person, Localization) VALUES (?, ?, ?)";

                try (PreparedStatement insertStatement = connection.prepareStatement(insertQuery)) {
                    insertStatement.setString(1, name);
                    insertStatement.setString(2, jwtToken);
                    insertStatement.setInt(3, number);

                    insertStatement.executeUpdate();

                    Toast.makeText(opcjeustallokalizacje.this, "Arkusz został przeniesiony", Toast.LENGTH_LONG).show();
                    finish();
                }catch (SQLException e) {
                    e.printStackTrace();
                    Toast.makeText(opcjeustallokalizacje.this, "Wystąpił błąd podczas dostępu do bazy danych", Toast.LENGTH_LONG).show();
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
        if(item.getItemId() == R.id.main){
            Intent intent = new Intent(this, main.class);
            intent.putExtra("user", jwtToken);
            startActivity(intent);
            return true;
        }
        else if (item.getItemId() == R.id.logout) {
            performLogout();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    private void performLogout() {
        // Tutaj umieść kod do wykonania wylogowania
        // Na przykład, usuń token uwierzytelniający, czyśc pamięć podręczną itp.

        // Przykład: Powrót do ekranu logowania
        getIntent().removeExtra("user");
        jwtToken="";
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