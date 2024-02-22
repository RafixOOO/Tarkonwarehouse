package com.example.tarkonwarehouse;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.content.ContextCompat;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.os.StrictMode;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;
import android.widget.Toast;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

public class zajdzlokalizacje extends AppCompatActivity {

    private String jwtToken;

    private EditText editText;
    private Spinner usernameSpinner;

    private String selectedValue;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_zajdzlokalizacje);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        usernameSpinner = findViewById(R.id.usernameSpinner);

        jwtToken = getIntent().getStringExtra("user");

        List<String> numbersList = new ArrayList<>();
        for (int i = 0; i <= 15; i++) {
            numbersList.add(String.valueOf(i));
        }

        // Tworzenie adaptera
        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, numbersList);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

        // Ustawienie adaptera w Spinnerze
        usernameSpinner.setAdapter(adapter);

        if (jwtToken != null && !jwtToken.isEmpty()) {
            Toast.makeText(this, "Lokalizacja", Toast.LENGTH_SHORT).show();
        } else {
            Intent intent = new Intent(this, login.class);
            startActivity(intent);
            finish(); // Dodaj finish, aby zakończyć bieżącą aktywność, jeśli nie ma tokenu
        }

        editText = findViewById(R.id.edittext1);
        usernameSpinner = findViewById(R.id.usernameSpinner);
        TableLayout tableLayout = findViewById(R.id.tableLayout);
        Button button = findViewById(R.id.button);

        usernameSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int position, long id) {
                // Pobieranie wybranej wartości z Spinnera
                selectedValue = adapterView.getItemAtPosition(position).toString();}

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {
                // Obsługa, gdy nic nie jest wybrane w Spinnerze
            }
        });


        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Connection connection = connectionclass();
                String editTextValue = editText.getText().toString().toUpperCase();
                if (connection != null) {
                    if(selectedValue.equals("0") && editTextValue.isEmpty()){
                        Toast.makeText(zajdzlokalizacje.this, "Nie wybrano lokalizacji lub pole tekstowe jest puste", Toast.LENGTH_LONG).show();

                    }else if(!selectedValue.equals("0") && editTextValue.isEmpty()){
                        try{


                            String query = "SELECT \n" +
                                    "    m.PartID,\n" +
                                    "    max(m.[Date]) AS data,\n" +
                                    "    m.Person,\n" +
                                    "    m.Localization,\n" +
                                    "    COUNT(m.PartID) AS Ilosc,\n" +
                                    "    s.Material,\n" +
                                    "    s.Thickness,\n" +
                                    "    s.[Length],\n" +
                                    "    s.Width\n" +
                                    "FROM PartCheck.dbo.MagazynExtra m\n" +
                                    "Left JOIN \n" +
                                    "    SNDBASE_PROD.dbo.Stock s ON m.PartID = s.SheetName COLLATE SQL_Latin1_General_CP1_CI_AS\n" +
                                    "        where  NOT EXISTS (\n" +
                                    "        SELECT 1\n" +
                                    "        FROM \n" +
                                    "            SNDBASE_PROD.dbo.StockArchive sh\n" +
                                    "        WHERE \n" +
                                    "            sh.SheetName = m.PartID COLLATE SQL_Latin1_General_CP1_CI_AS\n" +
                                    "    ) and m.Localization = ? \n" +
                                    "group by m.PartID,m.Person,m.Localization,s.Material,s.Thickness,s.[Length],s.Width\n" +
                                    "order by MAX(m.[Date]) DESC;";

                            PreparedStatement st = connection.prepareStatement(query);
                            st.setString(1, selectedValue); // Ustawiamy wartość dla parametru zapytania
                            ResultSet rs = st.executeQuery();

                            tableLayout.removeAllViews();

                            TableRow headerRow = new TableRow(zajdzlokalizacje.this);

                            // Dodaj nagłówki
                            for (int j = 0; j < 3; j++) {
                                TextView headerTextView = new TextView(zajdzlokalizacje.this);
                                if (j == 0) {
                                    headerTextView.setText("Arkusz");
                                } else if (j == 1) {
                                    headerTextView.setText("Lokalizacja");
                                } else if (j == 2) {
                                    headerTextView.setText("Ilość");
                                }
                                headerRow.addView(headerTextView);
                            }

                            // Dodaj nagłówki do tabeli
                            tableLayout.addView(headerRow);

                            // Dodaj dane
                            while (rs.next()) {
                                TableRow dataRow = new TableRow(zajdzlokalizacje.this);
                                for (int j = 0; j < 4; j++) {
                                    TextView textView = new TextView(zajdzlokalizacje.this);
                                    if (j == 0) {
                                        textView.setText(rs.getString("PartID"));
                                    }  else if (j == 1) {
                                        textView.setText("Lok " + rs.getString("Localization"));
                                    } else if (j == 2) {
                                        textView.setText(rs.getString("Ilosc"));
                                    }

                                    dataRow.addView(textView);
                                }

                                // Dodaj dane do tabeli
                                tableLayout.addView(dataRow);
                            }

                        }catch(SQLException e) {
                            e.printStackTrace();
                            Toast.makeText(zajdzlokalizacje.this, "Wystąpił błąd podczas pobierania danych z bazy", Toast.LENGTH_SHORT).show();

                        }

                    }else{

                        try{
                            String query;
                            if(selectedValue.equals("0")){
                                query = "SELECT \n" +
                                        "    m.PartID,\n" +
                                        "    max(m.[Date]) AS data,\n" +
                                        "    m.Person,\n" +
                                        "    m.Localization,\n" +
                                        "    COUNT(m.PartID) AS Ilosc,\n" +
                                        "    s.Material,\n" +
                                        "    s.Thickness,\n" +
                                        "    s.[Length],\n" +
                                        "    s.Width\n" +
                                        "FROM PartCheck.dbo.MagazynExtra m\n" +
                                        "Left JOIN \n" +
                                        "    SNDBASE_PROD.dbo.Stock s ON m.PartID = s.SheetName COLLATE SQL_Latin1_General_CP1_CI_AS\n" +
                                        "        where  NOT EXISTS (\n" +
                                        "        SELECT 1\n" +
                                        "        FROM \n" +
                                        "            SNDBASE_PROD.dbo.StockArchive sh\n" +
                                        "        WHERE \n" +
                                        "            sh.SheetName = m.PartID COLLATE SQL_Latin1_General_CP1_CI_AS\n" +
                                        "    ) and m.PartID LIKE '"+editTextValue+"%'\n" +
                                        "group by m.PartID,m.Person,m.Localization,s.Material,s.Thickness,s.[Length],s.Width\n" +
                                        "order by MAX(m.[Date]) DESC;";
                            }else{
                                query = "SELECT \n" +
                                        "    m.PartID,\n" +
                                        "    max(m.[Date]) AS data,\n" +
                                        "    m.Person,\n" +
                                        "    m.Localization,\n" +
                                        "    COUNT(m.PartID) AS Ilosc,\n" +
                                        "    s.Material,\n" +
                                        "    s.Thickness,\n" +
                                        "    s.[Length],\n" +
                                        "    s.Width\n" +
                                        "FROM PartCheck.dbo.MagazynExtra m\n" +
                                        "Left JOIN \n" +
                                        "    SNDBASE_PROD.dbo.Stock s ON m.PartID = s.SheetName COLLATE SQL_Latin1_General_CP1_CI_AS\n" +
                                        "        where  NOT EXISTS (\n" +
                                        "        SELECT 1\n" +
                                        "        FROM \n" +
                                        "            SNDBASE_PROD.dbo.StockArchive sh\n" +
                                        "        WHERE \n" +
                                        "            sh.SheetName = m.PartID COLLATE SQL_Latin1_General_CP1_CI_AS\n" +
                                        "    ) and m.PartID LIKE '"+editTextValue+"%' and m.Localization="+selectedValue+"\n" +
                                        "group by m.PartID,m.Person,m.Localization,s.Material,s.Thickness,s.[Length],s.Width\n" +
                                        "order by MAX(m.[Date]) DESC;";
                            }


                            Statement st = connection.createStatement();
                            ResultSet rs = st.executeQuery(query);

                            tableLayout.removeAllViews();

                            TableRow headerRow = new TableRow(zajdzlokalizacje.this);

                            // Dodaj nagłówki
                            for (int j = 0; j < 3; j++) {
                                TextView headerTextView = new TextView(zajdzlokalizacje.this);
                                if (j == 0) {
                                    headerTextView.setText("Arkusz");
                                } else if (j == 1) {
                                    headerTextView.setText("Lokalizacja");
                                } else if (j == 2) {
                                    headerTextView.setText("Ilość");
                                }
                                headerRow.addView(headerTextView);
                            }

                            // Dodaj nagłówki do tabeli
                            tableLayout.addView(headerRow);

                            // Dodaj dane
                            while (rs.next()) {
                                TableRow dataRow = new TableRow(zajdzlokalizacje.this);
                                for (int j = 0; j < 3; j++) {
                                    TextView textView = new TextView(zajdzlokalizacje.this);
                                    if (j == 0) {
                                        textView.setText(rs.getString("PartID"));
                                    } else if (j == 1) {
                                        textView.setText("Lok " + rs.getString("Localization"));
                                    }
                                    else if (j == 2) {
                                        textView.setText(rs.getString("Ilosc"));
                                    }

                                    dataRow.addView(textView);
                                }

                                // Dodaj dane do tabeli
                                tableLayout.addView(dataRow);
                            }

                        }catch(SQLException e) {
                            e.printStackTrace();
                            Toast.makeText(zajdzlokalizacje.this, "Wystąpił błąd podczas pobierania danych z bazy", Toast.LENGTH_SHORT).show();

                        }

                    }

                }else{
                    Toast.makeText(zajdzlokalizacje.this, "Wystąpił błąd podczas połączenia z bazą danych", Toast.LENGTH_LONG).show();
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