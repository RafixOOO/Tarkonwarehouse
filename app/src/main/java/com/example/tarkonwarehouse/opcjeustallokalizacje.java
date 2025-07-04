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
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;
import android.widget.Toast;

import org.w3c.dom.Text;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Calendar;
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
        TableLayout tableLayout = findViewById(R.id.tableLayout);

        if (jwtToken != null && !jwtToken.isEmpty()) {
            Toast.makeText(this, "Witaj " + jwtToken, Toast.LENGTH_SHORT).show();
        } else {
            Intent intent = new Intent(this, login.class);
            startActivity(intent);
            finish(); // Dodaj finish, aby zakończyć bieżącą aktywność, jeśli nie ma tokenu
        }

        EditText editdodaj = findViewById(R.id.editdodaj);
        Button dodaj = findViewById(R.id.dodaj);
        EditText editusun = findViewById(R.id.editusun);
        Button usun = findViewById(R.id.usun);
        Button przen = findViewById(R.id.przenies);
        Spinner spinner = findViewById(R.id.spinner);
        TextView text = findViewById(R.id.text);
        TextView local = findViewById(R.id.local);
        local.setText("Do Lokalizacji: "+number);

        Button opcja1 = findViewById(R.id.opcja1);
        Button opcja2 = findViewById(R.id.opcja2);
        Button opcja3 = findViewById(R.id.opcja3);

        try{
        Connection connection = connectionclass();

        if (connection != null) {



                String query = "SELECT\n" +
                        "    m.PartID,\n" +
                        "    MAX(m.[Date]) AS data,\n" +
                        "    m.Localization,\n" +
                        "    (SELECT COUNT(l.PartID) from PartCheck.dbo.MagazynExtra l where l.PartID=m.PartID and l.Localization=m.Localization and l.Deleted=0) AS Ilosc,\n" +
                        "    (SELECT COUNT(h.SheetName) from SNDBASE_PROD.dbo.StockArchive h where h.SheetName=sh1.SheetName) as zuzyte,\n" +
                        "    s.Material,\n" +
                        "    s.Thickness,\n" +
                        "    s.[Length],\n" +
                        "    s.Width\n" +
                        "FROM\n" +
                        "    PartCheck.dbo.MagazynExtra m\n" +
                        "LEFT JOIN\n" +
                        "    SNDBASE_PROD.dbo.Stock s ON m.PartID = s.SheetName COLLATE SQL_Latin1_General_CP1_CI_AS\n" +
                        "LEFT JOIN\n" +
                        "    SNDBASE_PROD.dbo.StockArchive sh1 on m.PartID=sh1.SheetName COLLATE SQL_Latin1_General_CP1_CI_AS\n" +
                        "WHERE NOT EXISTS (\n" +
                        "        SELECT 1\n" +
                        "        FROM\n" +
                        "            SNDBASE_PROD.dbo.StockArchive sh\n" +
                        "        WHERE\n" +
                        "            sh.SheetName = m.PartID COLLATE SQL_Latin1_General_CP1_CI_AS\n" +
                        "            and sh.Qty=0\n" +
                        "    ) and m.PartID='"+name+"' and Deleted=0\n" +
                        "GROUP BY\n" +
                        "    m.PartID, m.Localization, s.Material, s.Thickness, s.[Length], s.Width, sh1.SheetName\n" +
                        "ORDER BY\n" +
                        "    MAX(m.[Date]) DESC;";

                PreparedStatement st = connection.prepareStatement(query);
                ResultSet rs = st.executeQuery();

                tableLayout.removeAllViews();

                TableRow headerRow = new TableRow(opcjeustallokalizacje.this);

                // Dodaj nagłówki
                for (int j = 0; j < 4; j++) {
                    TextView headerTextView = new TextView(opcjeustallokalizacje.this);
                    if (j == 0) {
                        headerTextView.setText("Arkusz");
                    } else if (j == 1) {
                        headerTextView.setText("Lok");
                    } else if (j == 2) {
                        headerTextView.setText("Ilość");
                    } else if (j == 3) {
                        headerTextView.setText("Zużyte");
                    }
                    headerRow.addView(headerTextView);
                }

                // Dodaj nagłówki do tabeli
                tableLayout.addView(headerRow);

                // Dodaj dane
                while (rs.next()) {
                    TableRow dataRow = new TableRow(opcjeustallokalizacje.this);
                    for (int j = 0; j < 4; j++) {
                        TextView textView = new TextView(opcjeustallokalizacje.this);
                        if (j == 0) {
                            textView.setText(rs.getString("PartID"));
                        } else if (j == 1) {
                            String localization = rs.getString("Localization");
                            String textToShow;

                            if ("16".equals(localization)) {
                                textToShow = "kooperacja";
                            } else if ("17".equals(localization)) {
                                textToShow = "zewnetrznie";
                            } else {
                                textToShow = localization;
                            }

                            textView.setText(textToShow);
                        } else if (j == 2) {
                            textView.setText(rs.getString("Ilosc"));
                        } else if (j == 3) {
                            textView.setText(rs.getString("zuzyte"));
                        }

                        dataRow.addView(textView);
                    }


                    // Dodaj dane do tabeli
                    tableLayout.addView(dataRow);

                }

        }else {
            Toast.makeText(opcjeustallokalizacje.this, "Wystąpił błąd podczas połączenia z bazą danych", Toast.LENGTH_LONG).show();
        }
        }catch(SQLException e) {
            e.printStackTrace();
            Toast.makeText(opcjeustallokalizacje.this, "Wystąpił błąd podczas pobierania danych z bazy", Toast.LENGTH_SHORT).show();

        }

        opcja1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                editdodaj.setVisibility(View.VISIBLE);
                dodaj.setVisibility(View.VISIBLE);
                editusun.setVisibility(View.GONE);
                usun.setVisibility(View.GONE);
                przen.setVisibility(View.GONE);
                spinner.setVisibility(View.GONE);
                text.setVisibility(View.GONE);

            }
        });

        opcja2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                editdodaj.setVisibility(View.GONE);
                dodaj.setVisibility(View.GONE);
                editusun.setVisibility(View.GONE);
                usun.setVisibility(View.GONE);
                przen.setVisibility(View.VISIBLE);
                spinner.setVisibility(View.VISIBLE);
                text.setVisibility(View.VISIBLE);

            }
        });

        opcja3.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                editdodaj.setVisibility(View.GONE);
                dodaj.setVisibility(View.GONE);
                editusun.setVisibility(View.VISIBLE);
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
                int ilosc1 = 0;

                if(editdodaj.getText().toString().isEmpty()){
                    ilosc1=1;
                }else{
                    ilosc1 = Integer.parseInt(String.valueOf(editdodaj.getText()));
                }




                String insertQuery = "INSERT INTO PartCheck.dbo.MagazynExtra (PartID, Person, Localization) VALUES (?, ?, ?)";
                
                    
                
                try (PreparedStatement insertStatement = connection.prepareStatement(insertQuery)) {
                    for(int i=1;i<=ilosc1;i++){
                    insertStatement.setString(1, name);
                    insertStatement.setString(2, jwtToken);
                    insertStatement.setInt(3, number);

                    insertStatement.executeUpdate();
                }
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
                Calendar calendar = Calendar.getInstance();
                int today = calendar.get(Calendar.DAY_OF_WEEK);

                if (today != Calendar.MONDAY) {
                    // Ustal na ostatni poniedziałek
                    calendar.set(Calendar.DAY_OF_WEEK, Calendar.MONDAY);
                }

// Pobierz dzień i miesiąc
                int day = calendar.get(Calendar.DAY_OF_MONTH);
                int month = calendar.get(Calendar.MONTH) + 1; // Dodajemy 1, bo miesiące są liczone od 0
                int dzienMiesiac = (day * 100) + month; // Tworzymy liczbę w formacie ddMM

                int haslo = dzienMiesiac; // 3009, gdy jest 30 września
                String inputText = editusun.getText().toString();
                int haslocheck = 0; // Inicjalizuj zmienną

                if (!inputText.isEmpty()) {
                    haslocheck = Integer.parseInt(inputText);
                }

// Wypisz wartości do logów
                Log.d("MyApp", "Wartość hasło: " + haslo);
                Log.d("MyApp", "Wartość haslocheck: " + haslocheck);

// Porównanie hasła
                if (haslocheck == haslo) {
                    String deleteQuery = "DELETE FROM PartCheck.dbo.MagazynExtra WHERE PartID = ? AND Localization = ? and Deleted = 0 AND Date = (SELECT MIN(Date) FROM PartCheck.dbo.MagazynExtra WHERE PartID = ? AND Localization = ? and Deleted = 0)";

                    try (PreparedStatement deleteStatement = connection.prepareStatement(deleteQuery)) {
                        deleteStatement.setString(1, name);
                        deleteStatement.setInt(2, number);
                        deleteStatement.setString(3, name);
                        deleteStatement.setInt(4, number);

                        int rowsDeleted = deleteStatement.executeUpdate();
                        if (rowsDeleted > 0) {
                            Toast.makeText(opcjeustallokalizacje.this, "Arkusz został usunięty", Toast.LENGTH_LONG).show();
                            finish();
                        } else {
                            Toast.makeText(opcjeustallokalizacje.this, "Błąd", Toast.LENGTH_LONG).show();
                        }
                    }catch (SQLException e) {
                        e.printStackTrace();
                        Toast.makeText(opcjeustallokalizacje.this, "Wystąpił błąd podczas dostępu do bazy danych", Toast.LENGTH_LONG).show();
                    }
                }else{
                    Toast.makeText(opcjeustallokalizacje.this, "Złe hasło", Toast.LENGTH_LONG).show();
                }

            }
        });

        String spinnerselect = "SELECT DISTINCT Localization FROM PartCheck.dbo.MagazynExtra WHERE PartID = ? and Deleted = 0";
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

                String deleteQuery = "DELETE FROM PartCheck.dbo.MagazynExtra WHERE PartID = ? AND Localization = ? and Deleted = 0 AND Date = (SELECT MIN(Date) FROM PartCheck.dbo.MagazynExtra WHERE PartID = ? AND Localization = ? and Deleted = 0)";

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
        if(item.getItemId() == R.id.magazyn){
            Intent intent = new Intent(this, magazyn.class);
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