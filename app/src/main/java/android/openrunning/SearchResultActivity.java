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
                length.setText(String.valueOf(routes.get(0).getLength()));

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
                length.setText(String.valueOf(routes.get(index).getLength()));

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
                length.setText(String.valueOf(routes.get(index).getLength()));

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

        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
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

                    int sid = routes.get(index).getId();

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

    /*
        Classes for Slider.
     */

    public class SectionsPagerAdapter extends FragmentPagerAdapter {

        public SectionsPagerAdapter(FragmentManager fm) {

            super(fm);
        }

        @Override
        public Fragment getItem(int position) {
            return PlaceholderFragment.newInstance(position + 1);
        }

        @Override
        public int getCount() {
            return 3;
        }
    }

    public static class PlaceholderFragment extends Fragment {
        private static final String ARG_SECTION_NUMBER = "section_number";

        public PlaceholderFragment() {
        }

        public static PlaceholderFragment newInstance(int sectionNumber) {
            PlaceholderFragment fragment = new PlaceholderFragment();
            Bundle args = new Bundle();
            args.putInt(ARG_SECTION_NUMBER, sectionNumber);
            fragment.setArguments(args);
            return fragment;
        }

        @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
            View rootView = inflater.inflate(R.layout.map, container, false);
            return rootView;
        }
    }
    
    private void hideItem(){
        navigationView = (NavigationView) findViewById(R.id.nav_view);
        Menu nav_Menu = navigationView.getMenu();

        SharedPreferences prefs = getSharedPreferences("openrunning", MODE_PRIVATE);
        String type = prefs.getString("type", "");

        if (type.equals("2")) {
            nav_Menu.findItem(R.id.nav_release).setVisible(true);
        }else if (type.equals("3")) {
            nav_Menu.findItem(R.id.nav_release).setVisible(true);
            nav_Menu.findItem(R.id.nav_delete_route).setVisible(true);
            nav_Menu.findItem(R.id.nav_delete_user).setVisible(true);
        }
    }


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
