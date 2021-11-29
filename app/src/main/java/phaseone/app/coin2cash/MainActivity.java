package phaseone.app.coin2cash;

import android.content.Context;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.drawable.Drawable;
import android.location.Location;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.directions.route.AbstractRouting;
import com.directions.route.Route;
import com.directions.route.RouteException;
import com.directions.route.Routing;
import com.directions.route.RoutingListener;
import com.example.coin2cash.R;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptor;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.Polyline;
import com.google.android.gms.maps.model.PolylineOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.libraries.places.api.Places;
import com.google.android.libraries.places.api.net.PlacesClient;
import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.google.android.material.navigation.NavigationView;
import com.google.gson.JsonArray;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class MainActivity extends AppCompatActivity implements OnMapReadyCallback, RoutingListener {

    // MAP ELEMENTS:
    // =============================================================================================
    private static final String TAG = MainActivity.class.getSimpleName();
    private GoogleMap map;
    private FusedLocationProviderClient flpc;
    private Location lastKnownLocation;
    private CameraPosition cameraPosition;
    private PlacesClient pc;
    private static final String KEY_CAMERA_POSITION = "camera_position";
    private static final String KEY_LOCATION = "location";
    private final LatLng defaultLocation = new LatLng(-33.8523341, 151.2106085);
    private static final int DEFAULT_ZOOM = 24;
    private static final int PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION = 1;
    private boolean locationPermissionGranted;
    // =============================================================================================

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
    //View map;
    NavigationView side_menu;

    // SIDE MENU HEADER:
    TextView side_menu_header_name;
    TextView side_menu_header_email;
    RadioButton opt_metric;
    RadioButton opt_imperial;
    RadioButton opt_default;
    RadioButton opt_traditional;
    CheckBox opt_atm;
    CheckBox opt_bank;
    CheckBox opt_casino;
    CheckBox opt_cafe;

    // LOCATION DETAILS:
    BottomSheetDialog bs;
    TextView txt_lat;
    TextView txt_long;
    TextView txt_distance;
    TextView txt_time;
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

    // VAR:
    // =============================================================================================
    private final int WIDTH = 128;
    private final int HEIGHT = 128;
    userData user = new userData();
    routeData routeData = new routeData();

    private BitmapDescriptor markerIcon;

    private List<Polyline> polylines;
    // =============================================================================================

    // ACTIVITY START & CREATE
    // =============================================================================================
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        configure_map(savedInstanceState);
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

        ui_nav_reg();
    }

    private void initialize_login_components() {
        setContentView(R.layout.login);

        txt_email_login = findViewById(R.id.txtEmail);
        txt_password_login = findViewById(R.id.txtPassword);
        btn_login = findViewById(R.id.btnLogin);
        btn_clear_login = findViewById(R.id.btnClear);
        btn_register_login = findViewById(R.id.btnRegister);

        ui_nav_login();
    }

    private void initialize_main_components() {
        setContentView(R.layout.activity_main);

        side_menu = findViewById(R.id.side_menu);

        View header = side_menu.getHeaderView(0);
        side_menu_header_name = header.findViewById(R.id.side_menu_header_name);
        side_menu_header_email = header.findViewById(R.id.side_menu_header_email);

        side_menu_header_name.setText(user.getFullname());
        side_menu_header_email.setText(user.getEmail());

        opt_metric = (RadioButton) side_menu.getMenu().findItem(MENU_ITEM_METRIC).getActionView();
        opt_imperial = (RadioButton) side_menu.getMenu().findItem(MENU_ITEM_IMPERIAL).getActionView();

        opt_default = (RadioButton) side_menu.getMenu().findItem(MENU_ITEM_DEFAULT).getActionView();
        opt_traditional = (RadioButton) side_menu.getMenu().findItem(MENU_ITEM_TRADITIONAL).getActionView();

        opt_atm = (CheckBox) side_menu.getMenu().findItem(MENU_ITEM_FILTER1).getActionView();
        opt_bank = (CheckBox) side_menu.getMenu().findItem(MENU_ITEM_FILTER2).getActionView();
        opt_casino = (CheckBox) side_menu.getMenu().findItem(MENU_ITEM_FILTER3).getActionView();
        opt_cafe = (CheckBox) side_menu.getMenu().findItem(MENU_ITEM_FILTER4).getActionView();

        ui_nav_main();

        loadMap();
    }

    private void ui_nav_reg() {
        btn_register_reg.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                createUser();
                initialize_login_components();
            }
        });

        btn_clear_reg.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                txt_email_reg.setText("");
                txt_name_reg.setText("");
                txt_surname_reg.setText("");
                txt_password_reg.setText("");
            }
        });

        btn_back_reg.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                initialize_login_components();
            }
        });
    }

    private void ui_nav_login() {
        btn_login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                checkUser(txt_email_login.getText().toString(), txt_password_login.getText().toString());
            }
        });

        btn_clear_login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                txt_email_login.setText("");
                txt_password_login.setText("");
            }
        });

        btn_register_login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                initialize_reg_components();
            }
        });
    }

    private void ui_nav_main() {
        side_menu.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                switch (item.getItemId()) {
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

        opt_metric.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                user.setUnits(true);
                postClick();
            }
        });

        opt_imperial.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                user.setUnits(false);
                postClick();
            }
        });

        opt_default.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                user.setMarkers(true);
                postClick();
            }
        });

        opt_traditional.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                user.setMarkers(false);
                postClick();
            }
        });

        opt_atm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (opt_atm.isChecked()) {
                    user.setOpt1Setting(true);
                }
                else {
                    user.setOpt1Setting(false);
                }
                postClick();
            }
        });

        opt_bank.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (opt_bank.isChecked()) {
                    user.setOpt2Setting(true);
                }
                else {
                    user.setOpt2Setting(false);
                }
                postClick();
            }
        });

        opt_casino.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (opt_casino.isChecked()) {
                    user.setOpt3Setting(true);
                }
                else {
                    user.setOpt3Setting(false);
                }
                postClick();
            }
        });

        opt_cafe.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (opt_cafe.isChecked()) {
                    user.setOpt4Setting(true);
                }
                else {
                    user.setOpt4Setting(false);
                }
                postClick();
            }
        });
    }

    private void postClick() {
        int opt1 = 0, opt2 = 0, opt3 = 0, opt4 = 0, units = 0, markers = 0;


        if (user.getUnits() == true) {
            units = 1;
        }
        if (user.getMarkers() == true) {
            markers = 1;
        }
        if (user.getOpt1Setting() == true) {
            opt1 = 1;
        }
        if (user.getOpt2Setting() == true) {
            opt2 = 1;
        }
        if (user.getOpt3Setting() == true) {
            opt3 = 1;
        }
        if (user.getOpt4Setting() == true) {
            opt4 = 1;
        }

        updateUserSettings(opt1, opt2, opt3, opt4, units, markers);

        getUserDetails(user.getEmail());

        updateChecks();
        getPOIs();
    }

    private void updateChecks() {
        if (user.getUnits() == true) {
            opt_metric.setChecked(true);
            opt_imperial.setChecked(false);
        } else {
            opt_metric.setChecked(false);
            opt_imperial.setChecked(true);
        }

        if (user.getMarkers() == true) {
            opt_default.setChecked(true);
            opt_traditional.setChecked(false);
        } else {
            opt_default.setChecked(false);
            opt_traditional.setChecked(true);
        }

        if (user.getOpt1Setting() == true) {
            opt_atm.setChecked(true);
        } else {
            opt_atm.setChecked(false);
        }

        if (user.getOpt2Setting() == true) {
            opt_bank.setChecked(true);
        } else {
            opt_bank.setChecked(false);
        }

        if (user.getOpt3Setting() == true) {
            opt_casino.setChecked(true);
        } else {
            opt_casino.setChecked(false);
        }

        if (user.getOpt4Setting() == true) {
            opt_cafe.setChecked(true);
        } else {
            opt_cafe.setChecked(false);
        }
    }

    private void ui_nav_directions(LatLng coords) {
        btn_directions.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getDirections(coords);
                bs.dismiss();
            }
        });

        btn_fav.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //
            }
        });
    }
    // =============================================================================================

    // BACKEND
    // =============================================================================================
    private void createUser() {
        //Populate user details with UI elements
        String email = txt_email_reg.getText().toString();
        String password = txt_password_reg.getText().toString();
        String name = txt_name_reg.getText().toString();
        String surname = txt_surname_reg.getText().toString();

        // Instantiate the RequestQueue.
        RequestQueue queue = Volley.newRequestQueue(getApplicationContext());
        String url ="https://coin2cash-p1.000webhostapp.com/createUser.php?email=" + email + "&" +
                    "password=" + password + "&" +
                    "name=" + name + "&" +
                    "surname=" + surname;

        //Store array from URL:
        JsonArrayRequest request = new JsonArrayRequest(Request.Method.GET, url, null, new Response.Listener<JSONArray>() {
            @Override
            public void onResponse(JSONArray response) {
                try {
                    JSONObject itemExists = (JSONObject) response.get(0); //Store objects from the array

                    //Store values from the array:
                    String inserted = itemExists.getString("inserted");

                    if (inserted.equalsIgnoreCase("true")) {
                        //If inserted then...
                        initialize_login_components();
                    }
                    else if (inserted.equalsIgnoreCase("false")) {
                        //If insert failed then...
                        Toast.makeText(getApplicationContext(), "Email already exists", Toast.LENGTH_SHORT).show();
                    }
                }
                catch (Exception e) {
                    //Display error:
                    Toast.makeText(getApplicationContext(), "JSON Error: " + e.toString(), Toast.LENGTH_SHORT).show();
                    Log.d("JSON Error", "" + e.toString());
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                //Display error:
                Toast.makeText(getApplicationContext(), "onErrorResponse: " + error.toString(), Toast.LENGTH_SHORT).show();
                Log.d("onErrorResponse", "" + error.toString());
            }
        });

        queue.add(request); //Add request to queue
    }

    private void checkUser(String email, String password) {
        // Instantiate the RequestQueue.
        RequestQueue queue = Volley.newRequestQueue(getApplicationContext());
        String url ="https://coin2cash-p1.000webhostapp.com/checkUser.php?email=" + email + "&" +
                "password=" + password;

        //Store array from URL:
        JsonArrayRequest request = new JsonArrayRequest(Request.Method.GET, url, null, new Response.Listener<JSONArray>() {
            @Override
            public void onResponse(JSONArray response) {
                try {
                    JSONObject loginStatus = (JSONObject) response.get(0); //Store objects from the array

                    //Store values from the array:
                    String login = loginStatus.getString("login");

                    if (login.equalsIgnoreCase("true")) {
                        //If login success then...
                        getUserDetails(email);

                        Handler mHandler = new Handler();

                        mHandler.postDelayed(new Runnable() {
                            @Override
                            public void run() {
                                initialize_main_components();
                            }
                        }, 1000);
                    }
                    else if (login.equalsIgnoreCase("false")) {
                        //If login failed then...
                        Toast.makeText(getApplicationContext(), "Incorrect login details", Toast.LENGTH_SHORT).show();
                    }
                }
                catch (Exception e) {
                    //Display error:
                    Toast.makeText(getApplicationContext(), "JSON Error: " + e.toString(), Toast.LENGTH_SHORT).show();
                    Log.d("JSON Error", "" + e.toString());
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                //Display error:
                Toast.makeText(getApplicationContext(), "onErrorResponse: " + error.toString(), Toast.LENGTH_SHORT).show();
                Log.d("onErrorResponse", "" + error.toString());
            }
        });

        queue.add(request); //Add request to queue
    }

    private void getUserDetails(String email) {
        // Instantiate the RequestQueue.
        RequestQueue queue = Volley.newRequestQueue(getApplicationContext());
        String url ="https://coin2cash-p1.000webhostapp.com/readUser.php?email=" + email;

        //Store array from URL:
        JsonArrayRequest request = new JsonArrayRequest(Request.Method.GET, url, null, new Response.Listener<JSONArray>() {
            @Override
            public void onResponse(JSONArray response) {
                try {
                    JSONObject userDetails = (JSONObject) response.get(0); //Store objects from the array

                    //Store values from the array:
                    user.setEmail(userDetails.getString("email"));
                    user.setFullname(userDetails.getString("fullname"));

                    user.setUnits(convertJSON(userDetails.getString("unitsSetting")));
                    user.setMarkers(convertJSON(userDetails.getString("markerSetting")));
                    user.setOpt1Setting(convertJSON(userDetails.getString("opt1Setting")));
                    user.setOpt2Setting(convertJSON(userDetails.getString("opt2Setting")));
                    user.setOpt3Setting(convertJSON(userDetails.getString("opt3Setting")));
                    user.setOpt4Setting(convertJSON(userDetails.getString("opt4Setting")));

                    if (user.getUnits() == true) {
                        routeData.setUnits("metric");
                    }
                    else {
                        routeData.setUnits("imperial");
                    }

                    Bitmap img;
                    Bitmap temp;

                    if (user.getMarkers() == true) {
                        temp = BitmapFactory.decodeResource(getResources(),R.drawable.default_icon);
                        img = Bitmap.createScaledBitmap(temp, WIDTH, HEIGHT, false);
                        markerIcon = BitmapDescriptorFactory.fromBitmap(img);
                    }
                    else {
                        temp = BitmapFactory.decodeResource(getResources(),R.drawable.traditional_icon);
                        img = Bitmap.createScaledBitmap(temp, WIDTH, HEIGHT, false);
                        markerIcon = BitmapDescriptorFactory.fromBitmap(img);
                    }
                }
                catch (Exception e) {
                    //Display error:
                    Toast.makeText(getApplicationContext(), "JSON Error: " + e.toString(), Toast.LENGTH_SHORT).show();
                    Log.d("JSON Error", "" + e.toString());
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                //Display error:
                Toast.makeText(getApplicationContext(), "onErrorResponse: " + error.toString(), Toast.LENGTH_SHORT).show();
                Log.d("onErrorResponse", "" + error.toString());
            }
        });

        queue.add(request); //Add request to queue
    }

    private boolean convertJSON(String input) {
        if (input.equalsIgnoreCase("1")) {
            return true;
        }
        else {
            return false;
        }
    }

    private void updateUserSettings(int opt1, int opt2, int opt3, int opt4, int units, int markers) {
        // Instantiate the RequestQueue.
        RequestQueue queue = Volley.newRequestQueue(getApplicationContext());

        String url ="https://coin2cash-p1.000webhostapp.com/updateUser.php?email=" + user.getEmail() + "&" +
                    "opt1=" + opt1 + "&" +
                    "opt2=" + opt2 + "&" +
                    "opt3=" + opt3 + "&" +
                    "opt4=" + opt4 + "&" +
                    "units=" + units + "&" +
                    "markers=" + markers;

        Log.d("", "updateUserSettings: " + url);

        //Store array from URL:
        JsonArrayRequest request = new JsonArrayRequest(Request.Method.GET, url, null, new Response.Listener<JSONArray>() {
            @Override
            public void onResponse(JSONArray response) {
                try {
                    JSONObject updated = (JSONObject) response.get(0); //Store objects from the array

                    //Store values from the array:
                    String isUpdated = updated.getString("updated");

                    if (isUpdated.equalsIgnoreCase("true")) {
                        System.out.println("Updated user settings successfully");
                        Log.d("", "Updated user settings successfully");
                    }
                    else {
                        System.out.println("Could not update user settings");
                        Log.d("", "Could not update user settings");
                    }
                }
                catch (Exception e) {
                    //Display error:
                    Toast.makeText(getApplicationContext(), "JSON Error: " + e.toString(), Toast.LENGTH_SHORT).show();
                    Log.d("JSON Error", "" + e.toString());
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                //Display error:
                Toast.makeText(getApplicationContext(), "onErrorResponse: " + error.toString(), Toast.LENGTH_SHORT).show();
                Log.d("onErrorResponse", "" + error.toString());
            }
        });

        queue.add(request); //Add request to queue
    }

    @Override
    public void onMapReady(@NonNull GoogleMap googleMap) {
        map = googleMap;

        location_update();
        getLocationPermission();
        updateLocationUI();
        getDeviceLocation();

        Handler mHandler = new Handler();

        mHandler.postDelayed(new Runnable() {
            @Override
            public void run() {
                getPOIs();
            }
        }, 1000);

        //Toast.makeText(getApplicationContext(), "" + user.getUnits(), Toast.LENGTH_SHORT).show();

        updateChecks();

        // adding on click listener to marker of google maps.
        map.setOnMarkerClickListener(new GoogleMap.OnMarkerClickListener() {
            @Override
            public boolean onMarkerClick(Marker marker) {
                //On marker click we are navigating to location
                LatLng coords = marker.getPosition();

                //getDirections(coords);
                display_bottom_sheet(coords);

                return false;
            }
        });
    }

    private void display_bottom_sheet(LatLng coords) {
        bs = new BottomSheetDialog(this);
        bs.setContentView(R.layout.location_details);

        txt_lat = bs.findViewById(R.id.location_details_lat);
        txt_long = bs.findViewById(R.id.location_details_long);
        txt_distance = bs.findViewById(R.id.location_details_distance);
        txt_time = bs.findViewById(R.id.location_details_time);
        btn_directions = bs.findViewById(R.id.btnDirections);
        btn_fav = bs.findViewById(R.id.btnFavourite);

        getTravelTime(returnDeviceLocation(), coords);
        getTravelDistance(returnDeviceLocation(), coords);

        ui_nav_directions(coords);

        Handler h = new Handler();

        h.postDelayed(new Runnable() {
            @Override
            public void run() {
                txt_lat.setText("" + coords.latitude);
                txt_long.setText("" + coords.longitude);

                txt_distance.setText(routeData.getDistance());
                txt_time.setText(routeData.getDuration());

                bs.show();
            }
        }, 1000);
    }

    private void configure_map(Bundle savedInstanceState) {
        // [START_EXCLUDE silent]
        // [START maps_current_place_on_create_save_instance_state]
        // Retrieve location and camera position from saved instance state.
        if (savedInstanceState != null) {
            lastKnownLocation = savedInstanceState.getParcelable(KEY_LOCATION);
            cameraPosition = savedInstanceState.getParcelable(KEY_CAMERA_POSITION);
        }
        // [END maps_current_place_on_create_save_instance_state]
        // [END_EXCLUDE]

        // [START_EXCLUDE silent]
        // Construct a PlacesClient
        Places.initialize(getApplicationContext().getApplicationContext(), getResources().getString(R.string.api_key));
        pc = Places.createClient(getApplicationContext());

        // Construct a FusedLocationProviderClient.
        flpc = LocationServices.getFusedLocationProviderClient(getApplicationContext());
    }

    private void location_update() {
        map.setOnMyLocationChangeListener(new GoogleMap.OnMyLocationChangeListener() {
            @Override
            public void onMyLocationChange(@NonNull Location location) {

                float zoom = map.getCameraPosition().zoom;
                //map.moveCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(location.getLatitude(), location.getLongitude()), zoom));
            }
        });
    }

    private void getLocationPermission() {
        /*
         * Request location permission, so that we can get the location of the
         * device. The result of the permission request is handled by a callback,
         * onRequestPermissionsResult.
         */
        if (ContextCompat.checkSelfPermission(getApplicationContext().getApplicationContext(),
                android.Manifest.permission.ACCESS_FINE_LOCATION)
                == PackageManager.PERMISSION_GRANTED) {
            locationPermissionGranted = true;
        } else {
            ActivityCompat.requestPermissions(this,
                    new String[]{android.Manifest.permission.ACCESS_FINE_LOCATION},
                    PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION);
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           @NonNull String[] permissions,
                                           @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        locationPermissionGranted = false;
        switch (requestCode) {
            case PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION: {
                // If request is cancelled, the result arrays are empty.
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    locationPermissionGranted = true;
                }
            }
        }
        updateLocationUI();
    }

    private LatLng returnDeviceLocation() {
        /*
         * Get the best and most recent location of the device, which may be null in rare
         * cases when a location is not available.
         */
        try {
            if (locationPermissionGranted) {
                Task<Location> locationResult = flpc.getLastLocation();
                locationResult.addOnCompleteListener(this, new OnCompleteListener<Location>() {
                    @Override
                    public void onComplete(@NonNull Task<Location> task) {
                        if (task.isSuccessful()) {
                            // Set the map's camera position to the current location of the device.
                            lastKnownLocation = task.getResult();
                        } else {
                            Log.d(TAG, "Current location is null. Using defaults.");
                            Log.e(TAG, "Exception: %s", task.getException());
                            map.moveCamera(CameraUpdateFactory
                                    .newLatLngZoom(defaultLocation, DEFAULT_ZOOM));
                            map.getUiSettings().setMyLocationButtonEnabled(false);
                        }
                    }
                });
            }
        } catch (SecurityException e) {
            Log.e("Exception: %s", e.getMessage(), e);
        }

        LatLng location = new LatLng(lastKnownLocation.getLatitude(), lastKnownLocation.getLongitude());

        return location;
    }

    private void getDeviceLocation() {
        /*
         * Get the best and most recent location of the device, which may be null in rare
         * cases when a location is not available.
         */
        try {
            if (locationPermissionGranted) {
                Task<Location> locationResult = flpc.getLastLocation();
                locationResult.addOnCompleteListener(this, new OnCompleteListener<Location>() {
                    @Override
                    public void onComplete(@NonNull Task<Location> task) {
                        if (task.isSuccessful()) {
                            // Set the map's camera position to the current location of the device.
                            lastKnownLocation = task.getResult();
                            if (lastKnownLocation != null) {
                                map.moveCamera(CameraUpdateFactory.newLatLngZoom(
                                        new LatLng(lastKnownLocation.getLatitude(),
                                                lastKnownLocation.getLongitude()), DEFAULT_ZOOM));
                            }
                        } else {
                            Log.d(TAG, "Current location is null. Using defaults.");
                            Log.e(TAG, "Exception: %s", task.getException());
                            map.moveCamera(CameraUpdateFactory
                                    .newLatLngZoom(defaultLocation, DEFAULT_ZOOM));
                            map.getUiSettings().setMyLocationButtonEnabled(false);
                        }
                    }
                });
            }
        } catch (SecurityException e) {
            Log.e("Exception: %s", e.getMessage(), e);
        }
    }

    private void updateLocationUI() {
        if (map == null) {
            return;
        }
        try {
            if (locationPermissionGranted) {
                map.setMyLocationEnabled(true);
                map.getUiSettings().setMyLocationButtonEnabled(true);
            } else {
                map.setMyLocationEnabled(false);
                map.getUiSettings().setMyLocationButtonEnabled(false);
                lastKnownLocation = null;
                getLocationPermission();
            }
        } catch (SecurityException e) {
            Log.e("Exception: %s", e.getMessage());
        }
    }

    private void loadMap() {
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);
    }

    private void readAtmList() {
        // Instantiate the RequestQueue.
        RequestQueue queue = Volley.newRequestQueue(getApplicationContext());
        String url ="https://coin2cash-p1.000webhostapp.com/readAtmList.php?";

        //Store array from URL:
        JsonArrayRequest request = new JsonArrayRequest(Request.Method.GET, url, null, new Response.Listener<JSONArray>() {
            @Override
            public void onResponse(JSONArray response) {
                try {
                    for (int i = 0; i < response.length(); i++) {
                        JSONObject atmJSON = (JSONObject) response.get(i); //Store objects from the array

                        double latitude = Double.parseDouble(atmJSON.getString("latitude"));
                        double longitude = Double.parseDouble(atmJSON.getString("longitude"));

                        LatLng coords = new LatLng(latitude, longitude);

                        map.addMarker(new MarkerOptions().position(coords).title("Bitcoin ATM").flat(true).icon(markerIcon)).setTag(i);
                    }
                }
                catch (Exception e) {
                    //Display error:
                    Toast.makeText(getApplicationContext(), "JSON Error: " + e.toString(), Toast.LENGTH_SHORT).show();
                    Log.d("JSON Error", "" + e.toString());
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                //Display error:
                Toast.makeText(getApplicationContext(), "onErrorResponse: " + error.toString(), Toast.LENGTH_SHORT).show();
                Log.d("onErrorResponse", "" + error.toString());
            }
        });

        queue.add(request); //Add request to queue
    }

    private BitmapDescriptor bitmapDescriptorFromVector(Context context, int vectorResId) {
        Drawable vectorDrawable = ContextCompat.getDrawable(context, vectorResId);
        vectorDrawable.setBounds(0, 0, vectorDrawable.getIntrinsicWidth(), vectorDrawable.getIntrinsicHeight());
        Bitmap bitmap = Bitmap.createBitmap(vectorDrawable.getIntrinsicWidth(), vectorDrawable.getIntrinsicHeight(), Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(bitmap);
        vectorDrawable.draw(canvas);
        return BitmapDescriptorFactory.fromBitmap(bitmap);
    }

    private void getDirections(LatLng destPosition) {
        removeRoute();

        Routing routing = new Routing.Builder()
                .travelMode(AbstractRouting.TravelMode.DRIVING)
                .withListener(this)
                .waypoints(returnDeviceLocation(), destPosition, destPosition)
                .key("AIzaSyDzT26Dm2Z7e8TTvynLydJuHlZGamQGBzk")
                .build();
        routing.execute();
    }

    @Override
    public void onRoutingFailure(RouteException e) {
        Log.e("check", e.getMessage());
    }

    @Override
    public void onRoutingStart() {
        Log.e("check", "onRoutingStart");
    }

    @Override
    public void onRoutingSuccess(ArrayList<Route> route, int shortestRouteIndex) {
        Log.e("check", "onRoutingSuccess");
        polylines = new ArrayList<>();

        if (polylines.size() > 0) {
            for (Polyline poly : polylines) {
                poly.remove();
            }
        }

        polylines = new ArrayList<>();
        //add route(s) to the map.
        for (int i = 0; i < route.size(); i++) {

            //In case of more than 5 alternative routes

            PolylineOptions polyOptions = new PolylineOptions();
            polyOptions.color(getResources().getColor(R.color.color_primary));
            polyOptions.width(10 + i * 3);
            polyOptions.addAll(route.get(i).getPoints());
            Polyline polyline = map.addPolyline(polyOptions);
            polylines.add(polyline);
        }
    }

    @Override
    public void onRoutingCancelled() {
        Log.e("check", "onRoutingCancelled");
    }

    private void getTravelTime(LatLng origin, LatLng destination) {
        // Instantiate the RequestQueue.
        RequestQueue queue = Volley.newRequestQueue(getApplicationContext());
        String url ="https://maps.googleapis.com/maps/api/directions/json?" +
                "origin=" + origin.latitude + "," + origin.longitude + "&" +
                "destination=" + destination.latitude + "," + destination.longitude + "&" +
                "units=" + routeData.getUnits() + "&" +
                "key=AIzaSyDzT26Dm2Z7e8TTvynLydJuHlZGamQGBzk";

        //Store array from URL:
        final String[] travelTime = new String[1];

        JsonObjectRequest request = new JsonObjectRequest(Request.Method.GET, url, null, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                try {
                    JSONArray route = response.getJSONArray("routes"); //Store objects from the array
                    JSONArray legs = route.getJSONObject(0).getJSONArray("legs");

                    routeData.setDuration(legs.getJSONObject(0).getJSONObject("duration").getString("text"));
                }
                catch (Exception e) {
                    //Display error:
                    Toast.makeText(getApplicationContext(), "JSON Error: " + e.toString(), Toast.LENGTH_SHORT).show();
                    Log.d("JSON Error", "" + e.toString());
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                //Display error:
                Toast.makeText(getApplicationContext(), "onErrorResponse: " + error.toString(), Toast.LENGTH_SHORT).show();
                Log.d("onErrorResponse", "" + error.toString());
            }
        });

        queue.add(request); //Add request to queue
    }

    private void getTravelDistance(LatLng origin, LatLng destination) {
        // Instantiate the RequestQueue.
        RequestQueue queue = Volley.newRequestQueue(getApplicationContext());
        String url ="https://maps.googleapis.com/maps/api/directions/json?" +
                "origin=" + origin.latitude + "," + origin.longitude + "&" +
                "destination=" + destination.latitude + "," + destination.longitude + "&" +
                "units=" + routeData.getUnits() + "&" +
                "key=AIzaSyDzT26Dm2Z7e8TTvynLydJuHlZGamQGBzk";

        //Store array from URL:
        JsonObjectRequest request = new JsonObjectRequest(Request.Method.GET, url, null, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                try {
                    JSONArray route = response.getJSONArray("routes"); //Store objects from the array
                    JSONArray legs = route.getJSONObject(0).getJSONArray("legs");

                    routeData.setDistance(legs.getJSONObject(0).getJSONObject("distance").getString("text"));
                }
                catch (Exception e) {
                    //Display error:
                    Toast.makeText(getApplicationContext(), "JSON Error: " + e.toString(), Toast.LENGTH_SHORT).show();
                    Log.d("JSON Error", "" + e.toString());
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                //Display error:
                Toast.makeText(getApplicationContext(), "onErrorResponse: " + error.toString(), Toast.LENGTH_SHORT).show();
                Log.d("onErrorResponse", "" + error.toString());
            }
        });

        queue.add(request); //Add request to queue
    }

    private void getPOIs() {
        map.clear();
        readAtmList();

        Bitmap img;
        Bitmap temp;
        BitmapDescriptor icon;

        if (user.getOpt1Setting() == true) {
            temp = BitmapFactory.decodeResource(getResources(),R.drawable.atm_icon);
            img = Bitmap.createScaledBitmap(temp, WIDTH, HEIGHT, false);
            icon = BitmapDescriptorFactory.fromBitmap(img);
            placePOIMarkers("atm", icon);
        }
        if (user.getOpt2Setting() == true) {
            temp = BitmapFactory.decodeResource(getResources(),R.drawable.bank_icon);
            img = Bitmap.createScaledBitmap(temp, WIDTH, HEIGHT, false);
            icon = BitmapDescriptorFactory.fromBitmap(img);
            placePOIMarkers("bank", icon);
        }
        if (user.getOpt3Setting() == true) {
            temp = BitmapFactory.decodeResource(getResources(),R.drawable.casino_icon);
            img = Bitmap.createScaledBitmap(temp, WIDTH, HEIGHT, false);
            icon = BitmapDescriptorFactory.fromBitmap(img);
            placePOIMarkers("casino", icon);
        }
        if (user.getOpt4Setting() == true) {
            temp = BitmapFactory.decodeResource(getResources(),R.drawable.cafe_icon);
            img = Bitmap.createScaledBitmap(temp, WIDTH, HEIGHT, false);
            icon = BitmapDescriptorFactory.fromBitmap(img);
            placePOIMarkers("cafe", icon);
        }
    }

    public void placePOIMarkers(String type, BitmapDescriptor icon) {
        // Instantiate the RequestQueue.
        RequestQueue queue = Volley.newRequestQueue(getApplicationContext());

        String url ="https://maps.googleapis.com/maps/api/place/nearbysearch/json?location=" + returnDeviceLocation().latitude + "," + returnDeviceLocation().longitude + "&radius=1000&types=" + type + "&key=AIzaSyDzT26Dm2Z7e8TTvynLydJuHlZGamQGBzk";

        //Store array from URL:
        JsonObjectRequest request = new JsonObjectRequest(Request.Method.GET, url, null, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                try {
                    JSONArray results = response.getJSONArray("results"); //Store objects from the array

                    for (int i = 0; i < results.length(); i++) {
                        String name = results.getJSONObject(i).getString("name");
                        LatLng location = new LatLng(Double.parseDouble(results.getJSONObject(i).getJSONObject("geometry").getJSONObject("location").getString("lat")),
                                Double.parseDouble(results.getJSONObject(i).getJSONObject("geometry").getJSONObject("location").getString("lng")));

                        map.addMarker(new MarkerOptions().icon(icon).position(location).title(name).flat(true)).setTag(type);
                    }
                }
                catch (Exception e) {
                    //Display error:
                    Toast.makeText(getApplicationContext(), "JSON Error: " + e.toString(), Toast.LENGTH_SHORT).show();
                    Log.d("JSON Error", "" + e.toString());
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                //Display error:
                Toast.makeText(getApplicationContext(), "onErrorResponse: " + error.toString(), Toast.LENGTH_SHORT).show();
                Log.d("onErrorResponse", "" + error.toString());
            }
        });

        queue.add(request); //Add request to queue
    }

    public void removeRoute() {
        try {
            if (!polylines.isEmpty()) {
                for (Polyline poly : polylines) {
                    poly.remove();
                }
            }
        }
        catch (Exception e) {
            System.out.println(e.toString());
        }
    }
    // =============================================================================================
}

// Fixed Version