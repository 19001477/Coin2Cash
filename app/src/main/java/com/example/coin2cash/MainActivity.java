package com.example.coin2cash;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;

import com.google.android.gms.maps.GoogleMap;
import com.google.android.material.navigation.NavigationView;

public class MainActivity extends AppCompatActivity {

    // UI ELEMENTS:
    // =============================================================================================
    //REGISTER:
    EditText txt_name_reg;
    EditText txt_surname_reg;
    EditText txt_email_reg;
    EditText txt_password_reg;
    Button btn_register_reg;
    Button btn_clear_reg;
    Button btn_back_reg;

    // LOGIN
    EditText txt_email_login;
    EditText txt_password_login;
    Button btn_login;
    Button btn_clear_login;
    Button btn_register_login;

    // MAIN:
    View map;
    NavigationView side_menu;
    Button btn_directions;
    ImageView btn_fav;

    final int MENU_ITEM_METRIC = R.id.menu_item_metric;
    final int MENU_ITEM_IMPERIAL = R.id.menu_item_imperial;
    final int MENU_ITEM_DEFAULT = R.id.menu_item_default;
    final int MENU_ITEM_TRADITIONAL = R.id.menu_item_traditional;
    final int MENU_ITEM_FILTER1 = R.id.menu_item_filter1;
    final int MENU_ITEM_FILTER2 = R.id.menu_item_filter2;
    final int MENU_ITEM_FILTER3 = R.id.menu_item_filter3;
    final int MENU_ITEM_FILTER4 = R.id.menu_item_filter4;
    final int MENU_ITEM_SIGN_OUT = R.id.menu_item_sign_out;
    final int MENU_ITEM_EXIT = R.id.menu_item_exit;
    // =============================================================================================

    // ACTIVITY START & CREATE
    // =============================================================================================
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        initialize_login_components();
    }
    // =============================================================================================

    // UI & NAVIGATION:
    // =============================================================================================
    private void initialize_reg_components() {
        setContentView(R.layout.register);

        txt_name_reg = findViewById(R.id.txtName);
        txt_surname_reg = findViewById(R.id.txtSurame);
        txt_email_reg = findViewById(R.id.txtEmail);
        txt_password_reg = findViewById(R.id.txtPassword);
        btn_register_reg = findViewById(R.id.btnRegister);
        btn_clear_reg = findViewById(R.id.btnClear);
        btn_back_reg = findViewById(R.id.btnBack);

        ui_nav();
    }

    private void initialize_login_components() {
        setContentView(R.layout.login);

        txt_email_login = findViewById(R.id.txtEmail);
        txt_password_login = findViewById(R.id.txtPassword);
        btn_login = findViewById(R.id.btnLogin);
        btn_clear_login = findViewById(R.id.btnClear);
        btn_register_login = findViewById(R.id.btnRegister);

        ui_nav();
    }

    private void initialize_main_components() {
        setContentView(R.layout.activity_main);

        map = findViewById(R.id.map);
        side_menu = findViewById(R.id.side_menu);
        btn_directions = findViewById(R.id.btnDirections);
        btn_fav = findViewById(R.id.btnFavourite);

        ui_nav();
    }

    private void ui_nav() {
        btn_register_reg.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                initialize_login_components();
            }
        });

        btn_clear_reg.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //
            }
        });

        btn_back_reg.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                initialize_login_components();
            }
        });

        btn_login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                initialize_main_components();
            }
        });

        btn_clear_login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //
            }
        });

        btn_register_login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                initialize_reg_components();
            }
        });

        btn_directions.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //
            }
        });

        btn_fav.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //
            }
        });

        side_menu.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {

                switch (item.getItemId()) {
                    case MENU_ITEM_METRIC:
                        //
                        break;
                    case MENU_ITEM_IMPERIAL:
                        //
                        break;
                    case MENU_ITEM_DEFAULT:
                        //
                        break;
                    case MENU_ITEM_TRADITIONAL:
                        //
                        break;
                    case MENU_ITEM_FILTER1:
                        //
                        break;
                    case MENU_ITEM_FILTER2:
                        //
                        break;
                    case MENU_ITEM_FILTER3:
                        //
                        break;
                    case MENU_ITEM_FILTER4:
                        //
                        break;
                    case MENU_ITEM_SIGN_OUT:
                        initialize_login_components();
                        break;
                    case MENU_ITEM_EXIT:
                        System.exit(0);
                        break;
                }

                return false;
            }
        });
    }
    // =============================================================================================

    // BACKEND
    // =============================================================================================
    //
    // =============================================================================================
}