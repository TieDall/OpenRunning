package android.openrunning;

import android.content.Context;
import android.content.Intent;
import android.preference.PreferenceManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import org.osmdroid.api.IMapController;
import org.osmdroid.config.Configuration;
import org.osmdroid.tileprovider.tilesource.TileSourceFactory;
import org.osmdroid.util.GeoPoint;
import org.osmdroid.views.MapView;

public class RegisterActivity extends AppCompatActivity {
    EditText ET_benutzername, ET_mailadresse, ET_password, ET_password2;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // set your user agent to prevent getting banned from the osm servers
        Context ctx = getApplicationContext();
        Configuration.getInstance().load(ctx, PreferenceManager.getDefaultSharedPreferences(ctx));

        setContentView(R.layout.activity_register);

        //
        ET_benutzername = (EditText) findViewById(R.id.editTextUsername);
        ET_mailadresse = (EditText) findViewById(R.id.editTextMailAdress);
        ET_password = (EditText) findViewById(R.id.editTextPassword);
        ET_password2 = (EditText) findViewById(R.id.editTextPasswordRepeatation);

      // adding osmdroid map
        MapView map = (MapView) findViewById(R.id.map);
        map.setTileSource(TileSourceFactory.MAPNIK);
        map.setTilesScaledToDpi(true);
        // set position
        IMapController mapController = map.getController();
        mapController.setZoom(9);
        GeoPoint startPoint = new GeoPoint(51.341236, 12.374643);
        mapController.setCenter(startPoint);

        // functions for buttons
        // buttonLogin
        Button loginButton = (Button) findViewById(R.id.buttonLogin);
        loginButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent myIntent = new Intent(RegisterActivity.this, LoginActivity.class);
                RegisterActivity.this.startActivity(myIntent);
            }
        });
    }

    public void OnRegister(View view) {
        /*
        String username = ET_benutzername.getText().toString();
        String mailadresse = ET_mailadresse.getText().toString();
        String password = ET_password.getText().toString();
        String password2 = ET_password2.getText().toString();
        String type = "Register";

        //Password 1 und 2 vergleichen
        if (password.equals(password2)){
            // Hash berechnen
            String salt = "$2a$12$FwcVI9O/dOqJKWJopl1fz.";
            String hash = BCrypt.hashpw(password, salt);

            BackgroundWorker backgroundWorker = new BackgroundWorker(this);
            backgroundWorker.execute(type, username, mailadresse, hash);
            // Intent myIntent = new Intent(RegisterActivity.this, StartActivity.class);
            // RegisterActivity.this.startActivity(myIntent);
        } else {
            startActivity(new Intent(this, RegisterActivity.class));
        }

*/

    }
}
