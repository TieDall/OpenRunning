package android.openrunning;

import android.Manifest;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.DialogFragment;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.NavigationView;
import android.support.design.widget.Snackbar;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import org.osmdroid.api.IMapController;
import org.osmdroid.bonuspack.routing.MapQuestRoadManager;
import org.osmdroid.bonuspack.routing.OSRMRoadManager;
import org.osmdroid.bonuspack.routing.Road;
import org.osmdroid.bonuspack.routing.RoadManager;
import org.osmdroid.config.Configuration;
import org.osmdroid.events.MapEventsReceiver;
import org.osmdroid.tileprovider.tilesource.TileSourceFactory;
import org.osmdroid.util.GeoPoint;
import org.osmdroid.views.MapView;
import org.osmdroid.views.overlay.MapEventsOverlay;
import org.osmdroid.views.overlay.Marker;
import org.osmdroid.views.overlay.Polyline;
import org.osmdroid.views.overlay.mylocation.GpsMyLocationProvider;
import org.osmdroid.views.overlay.mylocation.MyLocationNewOverlay;

import java.util.ArrayList;

import core.DBHandler;

public class CreateRouteActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener, MapEventsReceiver {

    NavigationView navigationView;

    private String apiKey = "wpXplEIDvQLPHri8h8bftUopL7yvVgmW";

    private ArrayList<GeoPoint> waypoints;

    //Seperator used when route is written to database
    private String geopointSeperator = ";";
    private String coordinateSeperator = "_";

    private MapView map;
    private Polyline roadOverlay;
    private boolean gpsFound;
    private double length;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_route);
        waypoints = new ArrayList<GeoPoint>();
        gpsFound = false;

        // for osmdroid
        final Context ctx = getApplicationContext();
        Configuration.getInstance().load(ctx, PreferenceManager.getDefaultSharedPreferences(ctx));

        // toolbar
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        toolbar.setTitle("Strecke erstellen");
        setSupportActionBar(toolbar);


        // add route button
        FloatingActionButton fab_send = (FloatingActionButton) findViewById(R.id.fab_send);
        fab_send.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        String waypointsAsString = "";
                        for (GeoPoint waypoint : waypoints){
                            //creating string with routes, which is written to database
                            waypointsAsString += String.valueOf(waypoint.getLatitude()) + coordinateSeperator + String.valueOf(waypoint.getLongitude()) + geopointSeperator;
                        }

                        SharedPreferences prefs = getSharedPreferences("openrunning", MODE_PRIVATE);
                        String bid = prefs.getString("bid", null);

                        //checking wether writing to database is successfull or not
                        boolean successfull = DBHandler.addRoute(bid, "", ""+length, waypointsAsString);
                        if (successfull){

                            // Alert Dialog for adding new route successfully
                            runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    //alert message to notify the user, that route was created successful
                                    AlertDialog.Builder builder = new AlertDialog.Builder(CreateRouteActivity.this);
                                    builder.setMessage("Danke, dass du deine Strecke mit der Community teilst. ")
                                            .setCancelable(false)
                                            .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                                                @Override
                                                public void onClick(DialogInterface dialogInterface, int i) {
                                                    finish();
                                                }
                                            });
                                    AlertDialog alert = builder.create();
                                    alert.setTitle("Erfolgreich erstellt!");
                                    alert.show();
                                }
                            });

                        } else {

                            // Alert Dialog for failure adding new route
                            runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    AlertDialog.Builder builder = new AlertDialog.Builder(CreateRouteActivity.this);
                                    builder.setMessage("Bitte versuche es später noch einmal.")
                                            .setCancelable(false)
                                            .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                                                @Override
                                                public void onClick(DialogInterface dialogInterface, int i) {
                                                    finish();
                                                }
                                            });
                                    AlertDialog alert = builder.create();
                                    alert.setTitle("Es gab ein Fehler!");
                                    alert.show();
                                }
                            });

                        }
                    }
                }).start();


            }
        });

        // floating action button to delete last created marker
        // deletes marker in order they were added
        FloatingActionButton fab_undo = (FloatingActionButton) findViewById(R.id.fab_undo);
        fab_undo.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                if (waypoints.size() >= 1) {
                    waypoints.remove((waypoints.size() - 1));
                    map.invalidate();

                    // deleting road overlay if existing
                    if (map.getOverlays().contains(roadOverlay)) {

                        map.getOverlays().remove(roadOverlay);
                        map.invalidate();

                    }
                    map.getOverlays().remove(map.getOverlays().size() - 1);
                    map.invalidate();

                    // only drawing route if there are two or more waypoints
                    // --> prevents crashes while route creation
                    if (waypoints.size() >= 2) {

                        roadCalc();

                    }

                }
            }
        });

        // navigation drawer
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout_create_route);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();

        // navigation
        navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);
        hideItem();

        // checking for gps permission control
        if (ContextCompat.checkSelfPermission(this,
                Manifest.permission.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED) {

            final Activity thisActivity = this;

            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    ActivityCompat.requestPermissions(thisActivity,
                            new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                            StartActivity.accessFineLocCode);
                }
            });

        } else {
            this.displayMap();
        }
    }

    @Override
    public boolean singleTapConfirmedHelper(GeoPoint p) {
        //Toast.makeText(this, "Tapped", Toast.LENGTH_SHORT).show();
        return false;
    }

    // to add markers if long pressed on map
    @Override
    public boolean longPressHelper(GeoPoint p) {
        //getting coordinates and adding the point to waypoint array
        GeoPoint point = new GeoPoint(p.getLatitude(), p.getLongitude());
        waypoints.add(point);

        //showing marker on map
        Marker mapMarker = new Marker(map);
        mapMarker.setPosition(point);
        map.getOverlays().add(mapMarker);
        map.invalidate();

        //onClickListener to delete single markers
        mapMarker.setOnMarkerClickListener(new Marker.OnMarkerClickListener() {
            @Override
            public boolean onMarkerClick(Marker marker, MapView map) {
                map.getOverlayManager().remove(marker);
                waypoints.remove(marker.getPosition());
                //checking for waypointsArray size to prevent crashing of app when deleted marker is last remaining marker on map
                // --> because roadCalc will try to calculate a route with only one waypoint
                if (waypoints.size() > 1) {
                    roadCalc();
                } else {
                    map.getOverlayManager().remove(roadOverlay);
                    map.invalidate();
                }
                return false;
            }
        });

        //preventing crash
        if (waypoints.size() >= 2) {

            roadCalc();

        }

        return true;
    }

    /**
     * Display map with position, zoomed and position overlay.
     */
    private void displayMap() {
        // display map
        map = (MapView) findViewById(R.id.map_create_route);
        map.setTileSource(TileSourceFactory.MAPNIK);

        // displaying clickable zoom buttons on map
        map.setBuiltInZoomControls(true);
        map.setMultiTouchControls(true);

        // font size
        map.setTilesScaledToDpi(true);

        MapEventsOverlay mapEventsOverlay = new MapEventsOverlay(this);
        map.getOverlays().add(0, mapEventsOverlay);

        // specify map presentation
        final IMapController mapController = map.getController();
        mapController.setZoom(12);
        LocationListener locationListener = new LocationListener() {
            @Override
            public void onLocationChanged(Location location) {
                if (gpsFound != true) {
                    GeoPoint startPoint = new GeoPoint(location.getLatitude(), location.getLongitude());
                    mapController.setCenter(startPoint);
                    gpsFound = true;
                }
            }

            @Override
            public void onStatusChanged(String s, int i, Bundle bundle) {
            }

            @Override
            public void onProviderEnabled(String s) {
            }

            @Override
            public void onProviderDisabled(String s) {
            }
        };
        LocationManager locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            return;
        }
        locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0, 0, locationListener);
        Location location = locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);
        if( location != null ) {
            GeoPoint startPoint = new GeoPoint(location.getLatitude(), location.getLongitude());
            mapController.setCenter(startPoint);
        }
        MyLocationNewOverlay mLocationOverlay = new MyLocationNewOverlay(new GpsMyLocationProvider(getApplicationContext()),map);
        mLocationOverlay.enableMyLocation();
        map.getOverlays().add(mLocationOverlay);
    }

    /**
     * Called when permission requested is answered.
     *
     * @param requestCode Request code definded as static final in class.
     * @param permissions Array filled with permissions.
     * @param grantResults -
     */
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        if (requestCode == StartActivity.accessFineLocCode){
            this.displayMap();
        }
    }

    /**
     * Close navigation drawer when opened and do nothing when navigation drawer is closed.
     */
    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout_create_route);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    /**
     * Actions when any item is selected from navigation.
     *
     * @param item Selected item.
     * @return
     */
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        if (id == R.id.nav_home) {

            Intent myIntent = new Intent(CreateRouteActivity.this, StartActivity.class);
            CreateRouteActivity.this.startActivity(myIntent);

        } else if (id == R.id.nav_search) {

            Intent myIntent = new Intent(CreateRouteActivity.this, SearchActivity.class);
            CreateRouteActivity.this.startActivity(myIntent);

        } else if (id == R.id.nav_favorites) {

        } else if (id == R.id.nav_add) {

        } else if (id == R.id.nav_release) {

        } else if (id == R.id.nav_delete_user) {

            Intent myIntent = new Intent(CreateRouteActivity.this, DeleteUserActivity.class);
            CreateRouteActivity.this.startActivity(myIntent);

        } else if (id == R.id.nav_delete_route) {

            Intent myIntent = new Intent(CreateRouteActivity.this, DeleteRouteActivity.class);
            CreateRouteActivity.this.startActivity(myIntent);

        } else if (id == R.id.nav_logout_user) {

            // set the type und bid to empty
            SharedPreferences.Editor editor = getSharedPreferences("openrunning", MODE_PRIVATE).edit();
            editor.putString("bid", "");
            editor.putString("type", "");
            editor.commit();

            Intent myIntent = new Intent(CreateRouteActivity.this, LoginActivity.class);
            CreateRouteActivity.this.startActivity(myIntent);

        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout_create_route);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    /**
     * sets Visibility of Menu-Items true
     */
    private void hideItem(){
        navigationView = (NavigationView) findViewById(R.id.nav_view);
        Menu nav_Menu = navigationView.getMenu();

        // Menu-Items depends on which userType the current user has.
        SharedPreferences prefs = getSharedPreferences("openrunning", MODE_PRIVATE);
        String type = prefs.getString("type", "");

        // shows advanced settings if user has usertype 2
        if (type.equals("2")) {
            nav_Menu.findItem(R.id.nav_release).setVisible(true);
        }else if (type.equals("3")) {
            // shows administration settings if user has usertype 3
            nav_Menu.findItem(R.id.nav_release).setVisible(true);
            nav_Menu.findItem(R.id.nav_delete_route).setVisible(true);
            nav_Menu.findItem(R.id.nav_delete_user).setVisible(true);
        }
    }

    // function to calculate route
    private void roadCalc() {

        // removing all existing road overlays
        if (map.getOverlays().contains(roadOverlay)){

            map.getOverlays().remove(roadOverlay);
            map.invalidate();

        }

        // Make RouteManager calculating for walking
        final RoadManager roadManager = new MapQuestRoadManager(apiKey);
        roadManager.addRequestOption("routeType=pedestrian");

        new Thread(new Runnable() {
            public void run() {

                // creating new array by adding first waypoint again as last waypoint
                // needed to display the route as a circuit
                ArrayList<GeoPoint> bufferwaypoints = (ArrayList<GeoPoint>) waypoints.clone();
                bufferwaypoints.add(waypoints.get(0));

                // calculating the route and their lenght
                Road road = roadManager.getRoad(bufferwaypoints);
                length = road.mLength;

                // adding the route to map
                roadOverlay = RoadManager.buildRoadOverlay(road);
                map.getOverlays().add(roadOverlay);

                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {

                        map.invalidate();

                    }
                });
            }
        }).start();
    }
}
