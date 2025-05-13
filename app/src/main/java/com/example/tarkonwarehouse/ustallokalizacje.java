package com.example.tarkonwarehouse;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.content.ContextCompat;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.os.StrictMode;
import android.text.InputType;
import android.util.Log;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ToggleButton;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class ustallokalizacje extends AppCompatActivity {

    private String jwtToken;
    private int number = 0 ;
    private String name;

    ToggleButton check1, check2, check3, check4, check5, check6, check7,check8,check9,check10,check11,check12,check13,check14, check15, recznie, koop, zew;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_ustallokalizacje);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        jwtToken = getIntent().getStringExtra("user");
        EditText edittext = findViewById(R.id.edittext2);
        edittext.setInputType(InputType.TYPE_TEXT_FLAG_NO_SUGGESTIONS);
        edittext.setImeOptions(EditorInfo.IME_FLAG_NO_FULLSCREEN);
        EditText edittext1 = findViewById(R.id.edittext1);
        edittext.requestFocus();
        Button button = findViewById(R.id.button1);
        Button button1 = findViewById(R.id.button);
        edittext.setOnKeyListener(new View.OnKeyListener() {
            @Override
            public boolean onKey(View v, int keyCode, KeyEvent event) {
                if ((event.getAction() == KeyEvent.ACTION_DOWN) && (keyCode == KeyEvent.KEYCODE_ENTER)) {
                    // Wywołaj akcję przycisku, np. kliknięcie
                    button.performClick();
                    return true;
                }
                return false;
            }
        });

        edittext.setOnKeyListener(new View.OnKeyListener() {
            @Override
            public boolean onKey(View v, int keyCode, KeyEvent event) {
                if ((event.getAction() == KeyEvent.ACTION_DOWN) && (keyCode == KeyEvent.KEYCODE_ENTER)) {
                    // Wywołaj akcję przycisku, np. kliknięcie
                    button.performClick();
                    return true;
                }
                return false;
            }
        });

        edittext1.setOnKeyListener(new View.OnKeyListener() {
            @Override
            public boolean onKey(View v, int keyCode, KeyEvent event) {
                if ((event.getAction() == KeyEvent.ACTION_DOWN) && (keyCode == KeyEvent.KEYCODE_ENTER)) {
                    // Wywołaj akcję przycisku, np. kliknięcie
                    button1.performClick();
                    return true;
                }
                return false;
            }
        });

        Connection connection = connectionclass();

        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                name = edittext.getText().toString();
                edittext.setText("");

                if (connection != null) {
                    if (number != 0) {
                        String checkQuery = "SELECT PartID FROM PartCheck.dbo.MagazynExtra WHERE PartID = ? and Deleted = 0";
                        PreparedStatement checkStatement = null;
                        ResultSet resultSet = null;
                        try {
                            checkStatement = connection.prepareStatement(checkQuery);
                            checkStatement.setString(1, name);

                            resultSet = checkStatement.executeQuery();

                            if (resultSet.next()) {
                                Intent intent = new Intent(ustallokalizacje.this, opcjeustallokalizacje.class);
                                intent.putExtra("user", jwtToken);
                                intent.putExtra("name", name);
                                intent.putExtra("number", number);
                                startActivity(intent);
                        }
                            else {
                                String insertQuery = "INSERT INTO PartCheck.dbo.MagazynExtra (PartID, Person, Localization) VALUES (?, ?, ?)";

                                try (PreparedStatement insertStatement = connection.prepareStatement(insertQuery)) {
                                    insertStatement.setString(1, name);
                                    insertStatement.setString(2, jwtToken);
                                    insertStatement.setInt(3, number);

                                    insertStatement.executeUpdate();
                                }
                            }
                        }catch (SQLException e) {
                            e.printStackTrace();
                            Toast.makeText(ustallokalizacje.this, "Wystąpił błąd podczas dostępu do bazy danych", Toast.LENGTH_LONG).show();
                        }

                        Toast.makeText(ustallokalizacje.this, "Arkusz "+name+" został dodany do Lok "+number+" przez "+jwtToken, Toast.LENGTH_LONG).show();

                    } else {
                        Toast.makeText(ustallokalizacje.this, "Nie wybrano lokalizacji", Toast.LENGTH_LONG).show();
                    }
                }else{
                    Toast.makeText(ustallokalizacje.this, "Wystąpił błąd podczas dodawania danych do bazy", Toast.LENGTH_LONG).show();
                }

            }
        });

        button1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                name = edittext1.getText().toString().trim().toUpperCase();
                edittext1.setText("");

                if (connection != null) {
                    if (number != 0) {
                        String checkQuery = "SELECT PartID FROM PartCheck.dbo.MagazynExtra WHERE PartID = ? and Deleted = 0";
                        PreparedStatement checkStatement = null;
                        ResultSet resultSet = null;
                        try {
                            checkStatement = connection.prepareStatement(checkQuery);
                            checkStatement.setString(1, name);

                            resultSet = checkStatement.executeQuery();

                            if (resultSet.next()) {
                                Intent intent = new Intent(ustallokalizacje.this, opcjeustallokalizacje.class);
                                intent.putExtra("user", jwtToken);
                                intent.putExtra("name", name);
                                intent.putExtra("number", number);
                                startActivity(intent);
                            }
                            else {
                                String insertQuery = "INSERT INTO PartCheck.dbo.MagazynExtra (PartID, Person, Localization) VALUES (?, ?, ?)";

                                try (PreparedStatement insertStatement = connection.prepareStatement(insertQuery)) {
                                    insertStatement.setString(1, name);
                                    insertStatement.setString(2, jwtToken);
                                    insertStatement.setInt(3, number);

                                    insertStatement.executeUpdate();
                                }
                            }
                        }catch (SQLException e) {
                            e.printStackTrace();
                            Toast.makeText(ustallokalizacje.this, "Wystąpił błąd podczas dostępu do bazy danych", Toast.LENGTH_LONG).show();
                        }

                        Toast.makeText(ustallokalizacje.this, "Arkusz "+name+" został dodany do Lok "+number+" przez "+jwtToken, Toast.LENGTH_LONG).show();

                    } else {
                        Toast.makeText(ustallokalizacje.this, "Nie wybrano lokalizacji", Toast.LENGTH_LONG).show();
                    }
                }else{
                    Toast.makeText(ustallokalizacje.this, "Wystąpił błąd podczas dodawania danych do bazy", Toast.LENGTH_LONG).show();
                }

            }
        });

        check1 = findViewById(R.id.check1);
        check2 = findViewById(R.id.check2);
        check3 = findViewById(R.id.check3);
        check4 = findViewById(R.id.check4);
        check5 = findViewById(R.id.check5);
        check6 = findViewById(R.id.check6);
        check7 = findViewById(R.id.check7);
        check8 = findViewById(R.id.check8);
        check9 = findViewById(R.id.check9);
        check10 = findViewById(R.id.check10);
        check11 = findViewById(R.id.check11);
        check12 = findViewById(R.id.check12);
        check13 = findViewById(R.id.check13);
        check14 = findViewById(R.id.check14);
        check15 = findViewById(R.id.check15);
        koop = findViewById(R.id.koop);
        recznie = findViewById(R.id.recznie);
        zew = findViewById(R.id.zew);

        check1.setBackgroundTintList(null);
        check2.setBackgroundTintList(null);
        check3.setBackgroundTintList(null);
        check4.setBackgroundTintList(null);
        check5.setBackgroundTintList(null);
        check6.setBackgroundTintList(null);
        check7.setBackgroundTintList(null);
        check8.setBackgroundTintList(null);
        check9.setBackgroundTintList(null);
        check10.setBackgroundTintList(null);
        check11.setBackgroundTintList(null);
        check12.setBackgroundTintList(null);
        check13.setBackgroundTintList(null);
        check14.setBackgroundTintList(null);
        check15.setBackgroundTintList(null);
        koop.setBackgroundTintList(null);
        recznie.setBackgroundTintList(null);
        zew.setBackgroundTintList(null);

        // Przypisanie obsługi zdarzenia kliknięcia do każdego przycisku
        check1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                uncheckOtherButtons(check1);
                number =1;
            }
        });

        check2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                uncheckOtherButtons(check2);
                number =2;
            }
        });

        check3.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                uncheckOtherButtons(check3);
                number =3;
            }
        });

        check4.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                uncheckOtherButtons(check4);
                number =4;
            }
        });

        check5.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                uncheckOtherButtons(check5);
                number =5;
            }
        });

        check6.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                uncheckOtherButtons(check6);
                number =6;
            }
        });

        check7.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                uncheckOtherButtons(check7);
                number =7;
            }
        });

        check8.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                uncheckOtherButtons(check8);
                number =8;
            }
        });

        check9.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                uncheckOtherButtons(check9);
                number =9;
            }
        });

        check10.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                uncheckOtherButtons(check10);
                number =10;
            }
        });

        check11.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                uncheckOtherButtons(check11);
                number =11;
            }
        });

        check12.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                uncheckOtherButtons(check12);
                number =12;
            }
        });

        check13.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                uncheckOtherButtons(check13);
                number =13;
            }
        });

        check14.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                uncheckOtherButtons(check14);
                number =14;
            }
        });

        check15.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                uncheckOtherButtons(check15);
                number =15;
            }
        });

        koop.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                uncheckOtherButtons(koop);
                number = 16;
            }
        });

        zew.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                uncheckOtherButtons(zew);
                number = 17;
            }
        });

        recznie.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                button1.setVisibility(recznie.isChecked() ? View.VISIBLE : View.GONE);

                if (recznie.isChecked()) {
                    // Po kliknięciu
                    setEditTextLayoutParams(edittext1, ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
                    recznie.setBackgroundTintList(ContextCompat.getColorStateList(view.getContext(), R.color.green));

                } else {
                    // Po odkliknięciu
                    setEditTextLayoutParams(edittext1, 1, 1);
                    recznie.setBackgroundTintList(null);
                    edittext.requestFocus();
                }
            }
        });


        // Weryfikacja tokenu
        if (jwtToken != null && !jwtToken.isEmpty()) {
            Toast.makeText(this, "Lokalizacja", Toast.LENGTH_SHORT).show();
        } else {
            Intent intent = new Intent(this, login.class);
            startActivity(intent);
            finish(); // Dodaj finish, aby zakończyć bieżącą aktywność, jeśli nie ma tokenu
        }
    }

    private void uncheckOtherButtons(ToggleButton currentButton) {
        // Wyłącz wszystkie inne przyciski oprócz tego, który został kliknięty
        if (currentButton != null) {
            ToggleButton[] buttons = {check1, check2, check3, check4, check5, check6, check7, check8, check9, check10, check11, check12, check13, check14, check15, koop, zew};
            for (ToggleButton button : buttons) {
                if (button == currentButton) {
                    // Ustaw tło na zielony dla aktualnie klikniętego przycisku
                    button.setBackgroundTintList(ContextCompat.getColorStateList(this, R.color.green));
                } else {
                    // Ustaw tło na domyślne dla pozostałych przycisków
                    button.setBackgroundTintList(null);
                    button.setChecked(false);
                }
            }
        }
    }

    private void setEditTextLayoutParams(EditText editText, int width, int height) {
        ViewGroup.LayoutParams params = editText.getLayoutParams();
        params.width = width;
        params.height = height;
        editText.setLayoutParams(params);
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