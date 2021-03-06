package android.openrunning;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.NavigationView;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.GravityCompat;
import android.support.v4.view.ViewPager;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import org.osmdroid.api.IMapController;
import org.osmdroid.bonuspack.routing.MapQuestRoadManager;
import org.osmdroid.bonuspack.routing.OSRMRoadManager;
import org.osmdroid.bonuspack.routing.Road;
import org.osmdroid.bonuspack.routing.RoadManager;
import org.osmdroid.config.Configuration;
import org.osmdroid.util.GeoPoint;
import org.osmdroid.views.MapView;
import org.osmdroid.views.overlay.Polyline;

import java.text.DecimalFormat;
import java.util.ArrayList;

import core.DBHandler;
import core.Route;

public class SearchResultActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {

    Context ctx;
    NavigationView navigationView;

    private String apiKey = "wpXplEIDvQLPHri8h8bftUopL7yvVgmW";

    static ArrayList<Route> routes;
    private String[] splittedWaypoints;

    private MapView map;
    private Polyline roadOverlay;
    ArrayList<GeoPoint> waypoints = new ArrayList<>();

    private int in =1;
    int index;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search_result);
        routes = new ArrayList<>();
        map= (MapView) findViewById(R.id.map);

        // font size map
        map.setTilesScaledToDpi(true);

        // for osmdroid
        ctx = getApplicationContext();
        Configuration.getInstance().load(ctx, PreferenceManager.getDefaultSharedPreferences(ctx));

        // toolbar
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        toolbar.setTitle("Streckenergebnisse");
        setSupportActionBar(toolbar);

        // drawer
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();

        // navigation
        navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);
        hideItem();

        new Thread(new Runnable() {
            @Override
            public void run() {

                //adding given routes to array
                Bundle b = getIntent().getExtras();

                int i = 0;

                while(b.get(""+i) != null){
                    int sid = (Integer) b.get(""+i);
                    Route route = DBHandler.getRoute(sid);
                    routes.add(route);
                    i++;
                }

                //displaying the first route
                //singleWaypoints gives array with single waypoints
                // --> waypoints are in form longitude_latitude
                String[] singleWaypoints = routes.get(0).getWaypoints().toString().split(";");

                //setting text of TextViews
                TextView length = (TextView) findViewById(R.id.textViewLength);
                double l = routes.get(0).getLength();
                DecimalFormat df = new DecimalFormat("##.##");
                l = Double.parseDouble(df.format(l));
                length.setText(l+" km");

                TextView rating = (TextView) findViewById(R.id.textViewRating);
                rating.setText(String.valueOf(routes.get(0).getAverageVotes()));

                //creating GeoPoints from singleWaypoints array by splitting each waypoint
                for(String waypoint : singleWaypoints){
                    splittedWaypoints=waypoint.split("_");
                    Double latidude = Double.parseDouble(splittedWaypoints[0]);
                    Double longitude = Double.parseDouble(splittedWaypoints[1]);
                    GeoPoint p = new GeoPoint(latidude, longitude);
                    waypoints.add(p);

                }

                roadCalc();

            }
        }).start();

        //FloatingActionButton to display next route
        FloatingActionButton fab_Next = (FloatingActionButton) findViewById(R.id.fab_Next);
        fab_Next.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {

                //checking wether there are only one route as search result or more
                if(routes.size() == 1){

                    index=0;

                }else{

                    index = in;

                }

                String[] singleWaypoints = routes.get(index).getWaypoints().toString().split(";");

                TextView length = (TextView) findViewById(R.id.textViewLength);
                double l = routes.get(index).getLength();
                DecimalFormat df = new DecimalFormat("##.##");
                l = Double.parseDouble(df.format(l));
                length.setText(l+" km");

                TextView rating = (TextView) findViewById(R.id.textViewRating);
                rating.setText(String.valueOf(routes.get(index).getAverageVotes()));

                for(String waypoint : singleWaypoints){
                    splittedWaypoints=waypoint.split("_");
                    Double latidude = Double.parseDouble(splittedWaypoints[0]);
                    Double longitude = Double.parseDouble(splittedWaypoints[1]);
                    GeoPoint p = new GeoPoint(latidude, longitude);
                    waypoints.add(p);
                }

                roadCalc();

                //preventing index out of range error
                //--> if last route is shown and actionbutton is pressed again, first route will be shown
                if(in == routes.size()-1) {
                    in = 0;

                } else {in++;}
            }
        });

        FloatingActionButton fab_Back = (FloatingActionButton) findViewById(R.id.fab_Back);
        fab_Back.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {

                if(in > 0){

                    in--;
                }

                if(routes.size()==1){

                    index=0;

                }else{

                    index = in;

                }

                String[] singleWaypoints = routes.get(index).getWaypoints().toString().split(";");

                TextView length = (TextView) findViewById(R.id.textViewLength);
                double l = routes.get(index).getLength();
                DecimalFormat df = new DecimalFormat("##.##");
                l = Double.parseDouble(df.format(l));
                length.setText(l+" km");

                TextView rating = (TextView) findViewById(R.id.textViewRating);
                rating.setText(String.valueOf(routes.get(index).getAverageVotes()));

                for(String waypoint : singleWaypoints){
                    splittedWaypoints=waypoint.split("_");
                    Double latidude = Double.parseDouble(splittedWaypoints[0]);
                    Double longitude = Double.parseDouble(splittedWaypoints[1]);
                    GeoPoint p = new GeoPoint(latidude, longitude);
                    waypoints.add(p);
                }

                roadCalc();

                //preventing index out of range error
                //--> if first route is shown and actionbutton is pressed again, first route will be shown
                if(in == routes.size()-1) {
                    in = 0;

                } else {in++;}
            }
        });

    }


    /**
     * Close navigation drawer when opened and do nothing when navigation drawer is closed.
     */
    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
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

            Intent myIntent = new Intent(SearchResultActivity.this, StartActivity.class);
            SearchResultActivity.this.startActivity(myIntent);

        } else if (id == R.id.nav_search) {

            Intent myIntent = new Intent(SearchResultActivity.this, SearchActivity.class);
            SearchResultActivity.this.startActivity(myIntent);

        } else if (id == R.id.nav_favorites) {

        } else if (id == R.id.nav_add) {

            Intent myIntent = new Intent(SearchResultActivity.this, CreateRouteActivity.class);
            SearchResultActivity.this.startActivity(myIntent);

        } else if (id == R.id.nav_release) {

        } else if (id == R.id.nav_delete_user) {

            Intent myIntent = new Intent(SearchResultActivity.this, DeleteUserActivity.class);
            SearchResultActivity.this.startActivity(myIntent);

        } else if (id == R.id.nav_delete_route) {

            Intent myIntent = new Intent(SearchResultActivity.this, DeleteRouteActivity.class);
            SearchResultActivity.this.startActivity(myIntent);

        } else if (id == R.id.nav_logout_user) {

            // set the type und bid to empty
            SharedPreferences.Editor editor = getSharedPreferences("openrunning", MODE_PRIVATE).edit();
            editor.putString("bid", "");
            editor.putString("type", "");
            editor.commit();

            Intent myIntent = new Intent(SearchResultActivity.this, LoginActivity.class);
            SearchResultActivity.this.startActivity(myIntent);

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
        getMenuInflater().inflate(R.menu.actions_search_result, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        if (id == R.id.action_favorite) {


            return true;
        } else if (id == R.id.action_report) {

            new Thread(new Runnable() {
                @Override
                public void run() {

                    // get SID from surrent route
                    int sid = routes.get(index).getId();

                    // set RouteStatus to "2" on the route with this SID
                    String result = DBHandler.setRouteStatus(""+sid, ""+2);

                    if (result.equals("erfolgreich")) {
                        runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            Toast.makeText(ctx, "erlogreich gemeldet", Toast.LENGTH_LONG).show();
                        }
                    });
                }
                    }
            }).start();
            return true;
        }

        return super.onOptionsItemSelected(item);
    }


    // for detailed information see "roadCalc" function in "CreateRouteActivity"
    private void roadCalc() {

        if (map.getOverlays().contains(roadOverlay)){

            map.getOverlays().remove(roadOverlay);
            map.invalidate();

        }

        // Make RouteManager calculating for walking
        final RoadManager roadManager = new MapQuestRoadManager(apiKey);
        roadManager.addRequestOption("routeType=pedestrian");

        new Thread(new Runnable() {
            public void run() {

                ArrayList<GeoPoint> bufferwaypoints = (ArrayList<GeoPoint>) waypoints.clone();
                bufferwaypoints.add(waypoints.get(0));

                Road road = roadManager.getRoad(bufferwaypoints);

                roadOverlay = RoadManager.buildRoadOverlay(road);
                map.getOverlays().add(roadOverlay);

                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {

                        //adding zoom buttons and zoom on shown map
                        final IMapController mapController = map.getController();
                        mapController.setZoom(14);
                        mapController.setCenter(waypoints.get(0));
                        map.setBuiltInZoomControls(true);
                        map.setMultiTouchControls(true);
                        map.invalidate();
                        waypoints.clear();

                    }
                });
            }
        }).start();
    }
}
