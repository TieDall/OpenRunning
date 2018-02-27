package android.openrunning;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import org.osmdroid.api.IMapController;
import org.osmdroid.config.Configuration;
import org.osmdroid.tileprovider.tilesource.TileSourceFactory;
import org.osmdroid.util.GeoPoint;
import org.osmdroid.views.MapView;

import core.DBHandler;
import core.Passwordhash;

public class RegisterActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // set your user agent to prevent getting banned from the osm servers
        final Context ctx = getApplicationContext();
        Configuration.getInstance().load(ctx, PreferenceManager.getDefaultSharedPreferences(ctx));

        setContentView(R.layout.activity_register);

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


        // buttonRegister
        Button registerButton = (Button) findViewById(R.id.buttonRegister);
        registerButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                final String username = ((EditText) findViewById(R.id.editTextUsername)).getText().toString();
                final String mailadresse = ((EditText) findViewById(R.id.editTextMailAdress)).getText().toString();
                final String password = ((EditText) findViewById(R.id.editTextPassword)).getText().toString();
                final String password2 = ((EditText) findViewById(R.id.editTextPasswordRepeatation)).getText().toString();

                new Thread(new Runnable() {
                    @Override
                    public void run() {

                        if (!username.isEmpty() && !mailadresse.isEmpty() && !password.isEmpty() && !password2.isEmpty() ) {

                            if (password.equals(password2)){

                                if (DBHandler.getUser_info(username, mailadresse).equals("")) {

                                    String hash = Passwordhash.hashpw(password,Passwordhash.gensalt(12));
                                    String StringRegister = DBHandler.register(username, mailadresse, hash);

                                    if (StringRegister.equals("Insert Succesfull")) {

                                        String StringReturn = DBHandler.login(username,hash);

                                        if (!StringReturn.equals("register not success") || StringReturn != null) {
                                            int i = StringReturn.indexOf("_");
                                            String bid = StringReturn.substring(0, i);
                                            String type = StringReturn.substring(i + 1, StringReturn.length());

                                            SharedPreferences.Editor editor = getSharedPreferences("openrunning", MODE_PRIVATE).edit();
                                            editor.putString("bid", bid);
                                            editor.putString("type", type);
                                            editor.commit();

                                            Intent myIntent = new Intent(RegisterActivity.this, StartActivity.class);
                                            RegisterActivity.this.startActivity(myIntent);

                                        } else {
                                            runOnUiThread(new Runnable() {
                                                @Override
                                                public void run() {
                                                    Toast.makeText(ctx, "Registrieren fehlgeschlagen!", Toast.LENGTH_LONG).show();
                                                }
                                            });
                                        }
                                    }else {
                                        runOnUiThread(new Runnable() {
                                            @Override
                                            public void run() {
                                                Toast.makeText(ctx, "Registrieren fehlgeschlagen!", Toast.LENGTH_LONG).show();
                                            }
                                        });
                                    }
                                }else {
                                    runOnUiThread(new Runnable() {
                                        @Override
                                        public void run() {
                                            Toast.makeText(ctx, "Benutzername oder Mailadresse bereits vergeben", Toast.LENGTH_LONG).show();
                                        }
                                    });
                                }


                            }else {
                                runOnUiThread(new Runnable() {
                                    @Override
                                    public void run() {
                                        Toast.makeText(ctx, "Passwörter ungleich!", Toast.LENGTH_LONG).show();
                                    }
                                });
                            }
                        }else {
                            runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    Toast.makeText(ctx, "nicht alle Felder befüllt!", Toast.LENGTH_LONG).show();
                                }
                            });
                        }
                    }
                }).start();
            }
        });
    }
}
