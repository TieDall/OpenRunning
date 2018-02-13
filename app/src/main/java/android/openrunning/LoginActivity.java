package android.openrunning;

import android.content.Context;
import android.content.Intent;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.preference.PreferenceManager;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.EditText;

import org.osmdroid.api.IMapController;
import org.osmdroid.config.Configuration;
import org.osmdroid.tileprovider.tilesource.TileSourceFactory;
import org.osmdroid.util.GeoPoint;
import org.osmdroid.views.MapView;

public class LoginActivity extends AppCompatActivity {
    EditText UsernameEt, PasswordEt;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // set your user agent to prevent getting banned from the osm servers
        Context ctx = getApplicationContext();
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

    }
    public void OnLogin(View view) {
        String username = UsernameEt.getText().toString();

        //Passworthash


        String password = PasswordEt.getText().toString();
        String salt = "$2a$12$FwcVI9O/dOqJKWJopl1fz.";
        String hash = BCrypt.hashpw(password, salt);

        String type = "Login";

        BackgroundWorker backgroundWorker = new BackgroundWorker(this);
        backgroundWorker.execute(type, username, hash);
       // Intent myIntent = new Intent(LoginActivity.this, StartActivity.class);
       // LoginActivity.this.startActivity(myIntent);
    }
}
