package android.openrunning;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import org.osmdroid.config.Configuration;

import core.DBHandler;

public class DeleteUserActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener{

    NavigationView navigationView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_delete_user);

        // for osmdroid
        final Context ctx = getApplicationContext();
        Configuration.getInstance().load(ctx, PreferenceManager.getDefaultSharedPreferences(ctx));

        // toolbar
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        toolbar.setTitle("Nutzer löschen");
        setSupportActionBar(toolbar);

        // navigation drawer
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout_delete_user);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();

        // navigation
        navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);
        hideItem();

        // functions for buttons
        // buttonDelete
        Button deleteButton = (Button) findViewById(R.id.buttonDelete);
        deleteButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                // get String with username
                final String user = ((EditText) findViewById(R.id.editTextUsername)).getText().toString();

                new Thread(new Runnable() {
                    @Override
                    public void run() {

                    if (!user.isEmpty()) {

                        // checks if user is in database und delete them if they are
                        if (DBHandler.removeUser(user).equals(user)) {
                            runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    ((EditText) findViewById(R.id.editTextUsername)).setText("");
                                    Toast.makeText(ctx, "erfolgreich gelöscht", Toast.LENGTH_LONG).show();
                                }
                            });
                        } else if (DBHandler.removeUser(user).equals("not found")){
                            runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    ((EditText) findViewById(R.id.editTextUsername)).setText("");
                                    Toast.makeText(ctx, "Nutzer nicht gefunden", Toast.LENGTH_LONG).show();
                                }
                            });
                        }else {
                            runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    ((EditText) findViewById(R.id.editTextUsername)).setText("");
                                    Toast.makeText(ctx, "Fehler", Toast.LENGTH_LONG).show();
                                }
                            });
                        }
                    }else {
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                Toast.makeText(ctx, "kein Nutzer angegeben", Toast.LENGTH_LONG).show();
                            }
                        });
                    }
                    }
                }).start();
            }
        });
    }

    /**
     * Close navigation drawer when opened and do nothing when navigation drawer is closed.
     */
    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout_delete_user);
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

            Intent myIntent = new Intent(DeleteUserActivity.this, StartActivity.class);
            DeleteUserActivity.this.startActivity(myIntent);

        } else if (id == R.id.nav_search) {

            Intent myIntent = new Intent(DeleteUserActivity.this, SearchActivity.class);
            DeleteUserActivity.this.startActivity(myIntent);

        } else if (id == R.id.nav_favorites) {

        } else if (id == R.id.nav_add) {

            Intent myIntent = new Intent(DeleteUserActivity.this, CreateRouteActivity.class);
            DeleteUserActivity.this.startActivity(myIntent);

        } else if (id == R.id.nav_release) {

        } else if (id == R.id.nav_delete_user) {

            Intent myIntent = new Intent(DeleteUserActivity.this, DeleteUserActivity.class);
            DeleteUserActivity.this.startActivity(myIntent);

        } else if (id == R.id.nav_delete_route) {

            Intent myIntent = new Intent(DeleteUserActivity.this, DeleteRouteActivity.class);
            DeleteUserActivity.this.startActivity(myIntent);


        } else if (id == R.id.nav_logout_user) {

            // set the type und bid to empty
            SharedPreferences.Editor editor = getSharedPreferences("openrunning", MODE_PRIVATE).edit();
            editor.putString("bid", "");
            editor.putString("type", "");
            editor.commit();

            Intent myIntent = new Intent(DeleteUserActivity.this, LoginActivity.class);
            DeleteUserActivity.this.startActivity(myIntent);

        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);

        //Warum läuft das nicht?!
        //drawer.closeDrawer(GravityCompat.START);
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
        getMenuInflater().inflate(R.menu.actions_delete_user, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        if (id == R.id.action_search) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }
}
