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


                            String query = "SELECT " +
                                    "    m.PartID," +
                                    "    m.[Date] AS data," +
                                    "    m2.Person," +
                                    "    m3.Localization," +
                                    "    s.Material," +
                                    "    s.Thickness," +
                                    "    s.[Length]," +
                                    "    s.Qty," +
                                    "    s.Width " +
                                    "FROM " +
                                    "    (" +
                                    "        SELECT " +
                                    "            PartID, MAX([Date]) AS max_date " +
                                    "        FROM " +
                                    "            PartCheck.dbo.MagazynExtra " +
                                    "        GROUP BY " +
                                    "            PartID" +
                                    "    ) max_dates " +
                                    "INNER JOIN " +
                                    "    PartCheck.dbo.MagazynExtra m ON max_dates.PartID = m.PartID AND max_dates.max_date = m.[Date] " +
                                    "LEFT JOIN " +
                                    "    SNDBASE_PROD.dbo.Stock s ON m.PartID = s.SheetName COLLATE SQL_Latin1_General_CP1_CI_AS " +
                                    "INNER JOIN " +
                                    "    PartCheck.dbo.MagazynExtra m2 ON m.MagazynID = m2.MagazynID AND m2.[Date] = max_dates.max_date " +
                                    "INNER JOIN " +
                                    "    PartCheck.dbo.MagazynExtra m3 ON m.MagazynID = m3.MagazynID AND m3.[Date] = max_dates.max_date " +
                                    "WHERE " +
                                    "    NOT EXISTS (" +
                                    "        SELECT 1 " +
                                    "        FROM " +
                                    "            SNDBASE_PROD.dbo.StockArchive sh " +
                                    "        WHERE " +
                                    "            sh.SheetName = m.PartID COLLATE SQL_Latin1_General_CP1_CI_AS " +
                                    "    ) AND m3.Localization = ? " +
                                    "ORDER BY " +
                                    "    m.[Date] DESC";

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
                                    headerTextView.setText("Data");
                                } else if (j == 2) {
                                    headerTextView.setText("Lokalizacja");
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
                                        textView.setText(rs.getString("data"));
                                    } else if (j == 2) {
                                        textView.setText("Lok " + rs.getString("Localization"));
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
                                        "    m.[Date] AS data,\n" +
                                        "    m2.Person,\n" +
                                        "    m3.Localization,\n" +
                                        "    s.Material,\n" +
                                        "    s.Thickness,\n" +
                                        "    s.[Length],\n" +
                                        "    s.Qty,\n" +
                                        "    s.Width \n" +
                                        "FROM \n" +
                                        "    (\n" +
                                        "        SELECT \n" +
                                        "            PartID, MAX([Date]) AS max_date\n" +
                                        "        FROM \n" +
                                        "            PartCheck.dbo.MagazynExtra\n" +
                                        "        GROUP BY \n" +
                                        "            PartID\n" +
                                        "    ) max_dates\n" +
                                        "INNER JOIN \n" +
                                        "    PartCheck.dbo.MagazynExtra m ON max_dates.PartID = m.PartID AND max_dates.max_date = m.[Date]\n" +
                                        "LEFT JOIN \n" +
                                        "    SNDBASE_PROD.dbo.Stock s ON m.PartID = s.SheetName COLLATE SQL_Latin1_General_CP1_CI_AS\n" +
                                        "INNER JOIN \n" +
                                        "    PartCheck.dbo.MagazynExtra m2 ON m.MagazynID = m2.MagazynID AND m2.[Date] = max_dates.max_date\n" +
                                        "INNER JOIN \n" +
                                        "    PartCheck.dbo.MagazynExtra m3 ON m.MagazynID = m3.MagazynID AND m3.[Date] = max_dates.max_date\n" +
                                        "WHERE \n" +
                                        "    NOT EXISTS (\n" +
                                        "        SELECT 1\n" +
                                        "        FROM \n" +
                                        "            SNDBASE_PROD.dbo.StockArchive sh\n" +
                                        "        WHERE \n" +
                                        "            sh.SheetName = m.PartID COLLATE SQL_Latin1_General_CP1_CI_AS\n" +
                                        "    ) AND m.PartID LIKE '"+editTextValue+"%' order by m.[Date] desc;";
                            }else{
                                query = "SELECT \n" +
                                        "    m.PartID,\n" +
                                        "    m.[Date] AS data,\n" +
                                        "    m2.Person,\n" +
                                        "    m3.Localization,\n" +
                                        "    s.Material,\n" +
                                        "    s.Thickness,\n" +
                                        "    s.[Length],\n" +
                                        "    s.Qty,\n" +
                                        "    s.Width \n" +
                                        "FROM \n" +
                                        "    (\n" +
                                        "        SELECT \n" +
                                        "            PartID, MAX([Date]) AS max_date\n" +
                                        "        FROM \n" +
                                        "            PartCheck.dbo.MagazynExtra\n" +
                                        "        GROUP BY \n" +
                                        "            PartID\n" +
                                        "    ) max_dates\n" +
                                        "INNER JOIN \n" +
                                        "    PartCheck.dbo.MagazynExtra m ON max_dates.PartID = m.PartID AND max_dates.max_date = m.[Date]\n" +
                                        "LEFT JOIN \n" +
                                        "    SNDBASE_PROD.dbo.Stock s ON m.PartID = s.SheetName COLLATE SQL_Latin1_General_CP1_CI_AS\n" +
                                        "INNER JOIN \n" +
                                        "    PartCheck.dbo.MagazynExtra m2 ON m.MagazynID = m2.MagazynID AND m2.[Date] = max_dates.max_date\n" +
                                        "INNER JOIN \n" +
                                        "    PartCheck.dbo.MagazynExtra m3 ON m.MagazynID = m3.MagazynID AND m3.[Date] = max_dates.max_date\n" +
                                        "WHERE \n" +
                                        "    NOT EXISTS (\n" +
                                        "        SELECT 1\n" +
                                        "        FROM \n" +
                                        "            SNDBASE_PROD.dbo.StockArchive sh\n" +
                                        "        WHERE \n" +
                                        "            sh.SheetName = m.PartID COLLATE SQL_Latin1_General_CP1_CI_AS\n" +
                                        "    ) AND m.PartID LIKE '"+editTextValue+"%' and m3.Localization="+selectedValue+" order by m.[Date] desc;";
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
                                    headerTextView.setText("Data");
                                } else if (j == 2) {
                                    headerTextView.setText("Lokalizacja");
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
                                        textView.setText(rs.getString("data"));
                                    } else if (j == 2) {
                                        textView.setText("Lok " + rs.getString("Localization"));
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