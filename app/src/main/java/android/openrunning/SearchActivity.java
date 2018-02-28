package android.openrunning;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
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

import org.osmdroid.config.Configuration;

import java.util.ArrayList;

import core.DBHandler;

public class SearchActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {

    NavigationView navigationView;

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

        navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);
        hideItem();

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

                            String resultRoutes = DBHandler.getRoutes(length, rating);

                            int index;

                            index = resultRoutes.indexOf("_");
                            b.putInt("1", Integer.parseInt(resultRoutes.substring(0, index)));
                            resultRoutes = resultRoutes.substring(index+1);

                            if (resultRoutes.contains("_")) {
                                index = resultRoutes.indexOf("_");
                                b.putInt("2", Integer.parseInt(resultRoutes.substring(0, index)));
                                resultRoutes = resultRoutes.substring(index + 1);

                                if (resultRoutes.contains("_")) {
                                    index = resultRoutes.indexOf("_");
                                    b.putInt("3", Integer.parseInt(resultRoutes.substring(0, index)));
                                }
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
}
