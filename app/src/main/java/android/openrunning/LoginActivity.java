package android.openrunning;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.AsyncTask;
import android.preference.PreferenceManager;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import org.osmdroid.api.IMapController;
import org.osmdroid.config.Configuration;
import org.osmdroid.tileprovider.tilesource.TileSourceFactory;
import org.osmdroid.util.GeoPoint;
import org.osmdroid.views.MapView;

import core.DBHandler;

public class LoginActivity extends AppCompatActivity {
    EditText UsernameEt, PasswordEt;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // set your user agent to prevent getting banned from the osm servers
        final Context ctx = getApplicationContext();
        Configuration.getInstance().load(ctx, PreferenceManager.getDefaultSharedPreferences(ctx));

        setContentView(R.layout.activity_login);

        //
        UsernameEt = (EditText) findViewById(R.id.editTextUsername);
        PasswordEt = (EditText) findViewById(R.id.editTextPassword);

        // adding osmdroid map
        MapView map = (MapView) findViewById(R.id.map);
        map.setTileSource(TileSourceFactory.MAPNIK);
        map.setTilesScaledToDpi(true);
        // set position
        final IMapController mapController = map.getController();
        mapController.setZoom(9);
        GeoPoint startPoint = new GeoPoint(51.341236, 2.374643);
        mapController.setCenter(startPoint);

        // functions for buttons
        // buttonRegister
        Button registerButton = (Button) findViewById(R.id.buttonRegister);
        registerButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent myIntent = new Intent(LoginActivity.this, RegisterActivity.class);
                LoginActivity.this.startActivity(myIntent);
            }
        });
        //buttonLogin
        Button loginButton = (Button) findViewById(R.id.buttonLogin);
        loginButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                final String username = ((EditText) findViewById(R.id.editTextUsername)).getText().toString();
                final String password = ((EditText) findViewById(R.id.editTextPassword)).getText().toString();

                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        String loginReturn = DBHandler.login(username, password);

                        if (!loginReturn.equals("login not success") || loginReturn != null  ){
                            int i = loginReturn.indexOf("_");
                            String bid = loginReturn.substring(0,i);
                            String type = loginReturn.substring(i+1,loginReturn.length());

                            SharedPreferences.Editor editor = getSharedPreferences("openrunning", MODE_PRIVATE).edit();
                            editor.putString("bid", bid);
                            editor.putString("type", type);
                            editor.commit();

                            Intent myIntent = new Intent(LoginActivity.this, StartActivity.class);
                            LoginActivity.this.startActivity(myIntent);

                        } else {
                            runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    Toast.makeText(ctx, "Login fehlgeschlagen!", Toast.LENGTH_LONG).show();
                                }
                            });

                        }
                    }
                }).start();

            }
        });
    }
}
