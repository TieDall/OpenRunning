package android.openrunning;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.ProgressBar;

import org.osmdroid.api.IMapController;
import org.osmdroid.config.Configuration;
import org.osmdroid.tileprovider.tilesource.TileSourceFactory;
import org.osmdroid.util.GeoPoint;
import org.osmdroid.views.MapView;

import core.DBHandler;

public class MainActivity extends Activity {

    boolean progressuserTypeReady = false;
    boolean userTypeReady = false;

    String userType = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // set your user agent to prevent getting banned from the osm servers
        Context ctx = getApplicationContext();
        Configuration.getInstance().load(ctx, PreferenceManager.getDefaultSharedPreferences(ctx));

        setContentView(R.layout.activity_main);

        // adding osmdroid map
        MapView map = (MapView) findViewById(R.id.map);
        map.setTileSource(TileSourceFactory.MAPNIK);
        // set position
        IMapController mapController = map.getController();
        mapController.setZoom(9);
        GeoPoint startPoint = new GeoPoint(51.341236, 12.374643);
        mapController.setCenter(startPoint);

        // proceed functions
        new Thread(new Runnable() {
            @Override
            public void run() {

                // checking userType
                new Thread(new Runnable() {
                    @Override
                    public void run() {

                        SharedPreferences prefs = getSharedPreferences("openrunning", MODE_PRIVATE);
                        String bid = prefs.getString("bid", "");
                        String type = prefs.getString("type", "");

                        //locking for changed user infos
                        userType = DBHandler.updateuser(bid);
                        userTypeReady = true;
                        System.out.println("=====> user type ready");
                    }
                }).start();

                // progressBar
                try {
                    for (int i = 0; i < 50; i++) {
                        Thread.sleep(100);
                        ProgressBar progressBar = (ProgressBar) findViewById(R.id.progressBar);
                        progressBar.setProgress(i);
                        System.out.println(i);
                    }
                    progressuserTypeReady = true;

                } catch (InterruptedException e) {
                }

                // open next activity
                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        // wait until loading is completed
                        while (!progressuserTypeReady) { /* do nothing */}

                        if (userTypeReady) {
                            System.out.println("start activity");
                            SharedPreferences.Editor editor = getSharedPreferences("openrunning", MODE_PRIVATE).edit();
                            if (userType.equals("0") || userType.equals("1") || userType.equals("2") || userType.equals("3")) {
                                editor.putString("type", userType);
                                editor.commit();
                                Intent myIntent = new Intent(MainActivity.this, StartActivity.class);
                                MainActivity.this.startActivity(myIntent);
                            } else {
                                editor.putString("type", "");
                                editor.putString("bid", "");
                                editor.commit();
                                Intent myIntent = new Intent(MainActivity.this, LoginActivity.class);
                                MainActivity.this.startActivity(myIntent);
                            }
                        } else {
                            // Alert Dialog when no connection to server
                            runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
                                    builder.setMessage("Bitte versuche es später erneut.")
                                            .setCancelable(false);
                                    AlertDialog alert = builder.create();
                                    alert.setTitle("Verbindung zum Server nicht möglich!");
                                    alert.show();
                                }
                            });
                        }
                    }
                }).start();

            }
        }).start();



    }
}
