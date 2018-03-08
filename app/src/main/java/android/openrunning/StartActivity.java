package android.openrunning;

import android.Manifest;
import android.app.Activity;
import android.app.AlertDialog;
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
import android.support.design.widget.Snackbar;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.view.MenuInflater;
import android.view.View;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;

import org.osmdroid.api.IMapController;
import org.osmdroid.config.Configuration;
import org.osmdroid.tileprovider.tilesource.TileSourceFactory;
import org.osmdroid.util.GeoPoint;
import org.osmdroid.views.MapView;
import org.osmdroid.views.overlay.mylocation.GpsMyLocationProvider;
import org.osmdroid.views.overlay.mylocation.MyLocationNewOverlay;

public class StartActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {

    static final int accessFineLocCode = 2;
    NavigationView navigationView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_start);

        // for osmdroid
        Context ctx = getApplicationContext();
        Configuration.getInstance().load(ctx, PreferenceManager.getDefaultSharedPreferences(ctx));

        // toolbar
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        toolbar.setTitle("Start");
        setSupportActionBar(toolbar);

        // update position button
        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab_position);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        // display map
                        final MapView map = (MapView) findViewById(R.id.map_start);
                        map.setTileSource(TileSourceFactory.MAPNIK);

                        // specify map presentation
                        final IMapController mapController = map.getController();
                        mapController.setZoom(12);
                        LocationListener locationListener = new LocationListener() {
                            @Override
                            public void onLocationChanged(Location location) {
                                GeoPoint startPoint = new GeoPoint(location.getLatitude(), location.getLongitude());
                                mapController.setCenter(startPoint);
                            }
                            @Override
                            public void onStatusChanged(String s, int i, Bundle bundle) {}
                            @Override
                            public void onProviderEnabled(String s) {}
                            @Override
                            public void onProviderDisabled(String s) {}
                        };
                        LocationManager locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
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
                });

            }
        });

        // navigation drawer
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
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

    /**
     * Display map with position, zoomed and position overlay.
     */
    private void displayMap(){
        // display map
        final MapView map = (MapView) findViewById(R.id.map_start);
        map.setTileSource(TileSourceFactory.MAPNIK);

        // specify map presentation
        final IMapController mapController = map.getController();
        mapController.setZoom(12);
        LocationListener locationListener = new LocationListener() {
            @Override
            public void onLocationChanged(Location location) {
                GeoPoint startPoint = new GeoPoint(location.getLatitude(), location.getLongitude());
                mapController.setCenter(startPoint);
            }
            @Override
            public void onStatusChanged(String s, int i, Bundle bundle) {}
            @Override
            public void onProviderEnabled(String s) {}
            @Override
            public void onProviderDisabled(String s) {}
        };
        LocationManager locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
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

        if (grantResults[0] == PackageManager.PERMISSION_GRANTED){
            this.displayMap();
        } else {
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    AlertDialog.Builder builder = new AlertDialog.Builder(StartActivity.this);
                    builder.setMessage("Bitte erlaube der App GPS zu nutzen. Die App kann sonst nicht verwendet werden.\nDies kann über die Einstellungen von Android vorgenommen werden. \nDie App startet immer wieder neu, bis die Berechtigung erteilt wurde.")
                            .setCancelable(false)
                            .setPositiveButton("Schließen", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialogInterface, int i) {
                                    System.exit(0);
                                }
                            });
                    AlertDialog alert = builder.create();
                    alert.setTitle("GPS Berechtigung fehlt!");
                    alert.show();
                }
            });

        }
    }

    /**
     * Close navigation drawer when opened and do nothing when navigation drawer is closed.
     */
    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {}
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

        } else if (id == R.id.nav_search) {

            Intent myIntent = new Intent(StartActivity.this, SearchActivity.class);
            StartActivity.this.startActivity(myIntent);

        } else if (id == R.id.nav_favorites) {

        } else if (id == R.id.nav_add) {

            Intent myIntent = new Intent(StartActivity.this, CreateRouteActivity.class);
            StartActivity.this.startActivity(myIntent);

        } else if (id == R.id.nav_release) {

        } else if (id == R.id.nav_delete_user) {

            Intent myIntent = new Intent(StartActivity.this, DeleteUserActivity.class);
            StartActivity.this.startActivity(myIntent);

        } else if (id == R.id.nav_delete_route) {

            Intent myIntent = new Intent(StartActivity.this, DeleteRouteActivity.class);
            StartActivity.this.startActivity(myIntent);

        } else if (id == R.id.nav_logout_user) {

            // set the type und bid to empty
            SharedPreferences.Editor editor = getSharedPreferences("openrunning", MODE_PRIVATE).edit();
            editor.putString("bid", "");
            editor.putString("type", "");
            editor.commit();

            Intent myIntent = new Intent(StartActivity.this, LoginActivity.class);
            StartActivity.this.startActivity(myIntent);

        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
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

    /*
        Actions on toolbar.
     */

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.actions_start, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        if (id == R.id.action_search) {
            Intent myIntent = new Intent(StartActivity.this, SearchActivity.class);
            StartActivity.this.startActivity(myIntent);
            return true;
        } else if (id == R.id.action_add) {
            Intent myIntent = new Intent(StartActivity.this, CreateRouteActivity.class);
            StartActivity.this.startActivity(myIntent);
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

}
