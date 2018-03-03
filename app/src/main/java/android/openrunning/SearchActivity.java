package android.openrunning;

import android.content.Context;
import android.content.Intent;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.os.Parcelable;
import android.preference.PreferenceManager;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.view.View;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RatingBar;
import android.widget.Toast;

import org.osmdroid.bonuspack.routing.OSRMRoadManager;
import org.osmdroid.bonuspack.routing.Road;
import org.osmdroid.bonuspack.routing.RoadManager;
import org.osmdroid.config.Configuration;
import org.osmdroid.util.GeoPoint;

import java.util.ArrayList;

import core.DBHandler;
import core.Route;

public class SearchActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search);

        // for osmdroid
        final Context ctx = getApplicationContext();
        Configuration.getInstance().load(ctx, PreferenceManager.getDefaultSharedPreferences(ctx));

        // toolbar
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        toolbar.setTitle("Strecke suchen");
        setSupportActionBar(toolbar);

        // navigation drawer
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

        // search button
        Button searchButton = (Button) findViewById(R.id.buttonSearch);
        searchButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {


                final String distance = ((EditText) findViewById(R.id.editTextDistance)).getText().toString();
                final String length = ((EditText) findViewById(R.id.editTextRouteLength)).getText().toString();
                final float rating = ((RatingBar) findViewById(R.id.ratingBar)).getRating();

                if (!distance.isEmpty() || !length.isEmpty()){
                    new Thread(new Runnable() {
                        @Override
                        public void run() {
                            Intent myIntent = new Intent(SearchActivity.this, SearchResultActivity.class);
                            Bundle b = new Bundle();

                            // ArrayList which matches all parameters
                            ArrayList<String> result = new ArrayList<>();

                            // routes which matches the rating and length (with tolerance) only ID
                            // @see DataHandler.getRoutes()
                            String resultRoutes = DBHandler.getRoutes(length, rating);

                            // calculate distance
                            String[] routes = resultRoutes.split("_");
                            for (String route : routes) {
                                // get data from route
                                Route routeInfo = DBHandler.getRoute(Integer.parseInt(route));

                                String waypoints = routeInfo.getWaypoints();
                                String[] waypointsAsArray = waypoints.split(";");
                                for (String waypoint : waypointsAsArray ){
                                    String[] waypointLatLong = waypoint.split("_");
                                    GeoPoint geoPoint = new GeoPoint(Double.parseDouble(waypointLatLong[0]), Double.parseDouble(waypointLatLong[1]));

                                    // get current position
                                    LocationListener locationListener = new LocationListener() {
                                        @Override
                                        public void onLocationChanged(Location location) {}

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

                                    // calculate
                                    ArrayList<GeoPoint> geoPoints = new ArrayList<>();
                                    geoPoints.add(new GeoPoint(location.getLatitude(), location.getLongitude()));
                                    geoPoints.add(geoPoint);
                                    RoadManager roadManager = new OSRMRoadManager(getApplicationContext());
                                    Road road = roadManager.getRoad(geoPoints);
                                    double mLength = road.mLength;

                                    System.out.println("=====================");
                                    System.out.println("distance: "+distance);
                                    System.out.println("current: "+mLength);
                                    System.out.println("=====================");

                                    if (Double.parseDouble(distance) >= mLength){
                                        result.add(route);
                                        System.out.println("match");
                                        break;
                                    } else {
                                        System.out.println("no match");
                                    }
                                }

                            }

                            // add routes to next activity
                            int index = 1;
                            for (String currentResult : result){
                                b.putInt("1", Integer.parseInt(resultRoutes.substring(0, index)));
                                index++;
                            }
                            myIntent.putExtras(b);
                            startActivity(myIntent);
                            finish();
                            SearchActivity.this.startActivity(myIntent);
                        }
                    }).start();
                } else {

                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            Toast.makeText(ctx, "Suche ungültig", Toast.LENGTH_LONG).show();
                        }
                    });
                }
            }
        });
    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        if (id == R.id.nav_home) {

            Intent myIntent = new Intent(SearchActivity.this, StartActivity.class);
            SearchActivity.this.startActivity(myIntent);

        } else if (id == R.id.nav_search) {

        } else if (id == R.id.nav_favorites) {

        } else if (id == R.id.nav_add) {

            Intent myIntent = new Intent(SearchActivity.this, CreateRouteActivity.class);
            SearchActivity.this.startActivity(myIntent);

        } else if (id == R.id.nav_release) {

        } else if (id == R.id.nav_delete_user) {

            Intent myIntent = new Intent(SearchActivity.this, DeleteUserActivity.class);
            SearchActivity.this.startActivity(myIntent);

        } else if (id == R.id.nav_delete_route) {

        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }
}
