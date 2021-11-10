package phaseone.app.coin2cash;

import android.content.Context;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.drawable.Drawable;
import android.location.Location;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
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
import java.util.List;

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
    private String USER_NAME;
    private String USER_EMAIL;

    private String travelTime;
    private String travelDistance;

    private List<Polyline> polylines;

    private String units;
    // =============================================================================================

    // ACTIVITY START & CREATE
    // =============================================================================================
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        configure_map(savedInstanceState);
        //initialize_login_components();
        initialize_main_components();
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

        View header = side_menu.getHeaderView(R.layout.side_menu_header);
        side_menu_header_name = header.findViewById(R.id.side_menu_header_name);
        side_menu_header_email = header.findViewById(R.id.side_menu_header_email);

        side_menu_header_name.setText(USER_NAME);
        side_menu_header_email.setText(USER_EMAIL);

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
                initialize_main_components();
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
                        initialize_main_components();
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

    @Override
    public void onMapReady(@NonNull GoogleMap googleMap) {
        map = googleMap;

        location_update();
        getLocationPermission();
        updateLocationUI();
        getDeviceLocation();

        readAtmList();

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

        txt_lat.setText("" + coords.latitude);
        txt_long.setText("" + coords.longitude);
        txt_distance.setText(getTravelDistance(returnDeviceLocation(), coords));
        txt_time.setText(getTravelTime(returnDeviceLocation(), coords));

        ui_nav_directions(coords);

        bs.show();
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

        LatLng location = new LatLng(lastKnownLocation.getLatitude(),
                lastKnownLocation.getLongitude());

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

                        map.addMarker(new MarkerOptions().position(coords).title("Bitcoin ATM").flat(true).icon(bitmapDescriptorFromVector(getApplicationContext(), R.drawable.ic_btc_marker))).setTag(i);
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



        String duration = getTravelTime(returnDeviceLocation(), destPosition);
        String distance = getTravelDistance(returnDeviceLocation(), destPosition);

        Toast.makeText(getApplicationContext(), "Duration = " + duration + " Distance = " + distance, Toast.LENGTH_SHORT).show();
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
            polyOptions.color(getResources().getColor(R.color.quantum_googblue600));
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

    private String getTravelTime(LatLng origin, LatLng destination) {
        // Instantiate the RequestQueue.
        RequestQueue queue = Volley.newRequestQueue(getApplicationContext());
        String url ="https://maps.googleapis.com/maps/api/directions/json?" +
                "origin=" + origin.latitude + "," + origin.longitude + "&" +
                "destination=" + destination.latitude + "," + destination.longitude + "&" +
                "units=" + units + "&" +
                "key=AIzaSyDzT26Dm2Z7e8TTvynLydJuHlZGamQGBzk";

        //Store array from URL:
        JsonObjectRequest request = new JsonObjectRequest(Request.Method.GET, url, null, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                try {
                    JSONArray route = response.getJSONArray("routes"); //Store objects from the array
                    JSONArray legs = route.getJSONObject(0).getJSONArray("legs");

                    travelTime = legs.getJSONObject(0).getJSONObject("duration").getString("text");
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

        return travelTime;
    }

    private String getTravelDistance(LatLng origin, LatLng destination) {
        // Instantiate the RequestQueue.
        RequestQueue queue = Volley.newRequestQueue(getApplicationContext());
        String url ="https://maps.googleapis.com/maps/api/directions/json?" +
                "origin=" + origin.latitude + "," + origin.longitude + "&" +
                "destination=" + destination.latitude + "," + destination.longitude + "&" +
                "units=" + units + "&" +
                "key=AIzaSyDzT26Dm2Z7e8TTvynLydJuHlZGamQGBzk";

        //Store array from URL:
        JsonObjectRequest request = new JsonObjectRequest(Request.Method.GET, url, null, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                try {
                    JSONArray route = response.getJSONArray("routes"); //Store objects from the array
                    JSONArray legs = route.getJSONObject(0).getJSONArray("legs");

                    travelDistance = legs.getJSONObject(0).getJSONObject("distance").getString("text");
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

        return travelDistance;
    }

    private void getPOIs(String type) {

    }

    public void placePOIMarkers(String type) {
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

                        map.addMarker(new MarkerOptions().position(location).title(name).flat(true)).setTag(type);
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