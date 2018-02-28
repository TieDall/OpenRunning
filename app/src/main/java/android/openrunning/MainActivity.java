package android.openrunning;

import android.app.Activity;
import android.content.Context;
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

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // set your user agent to prevent getting banned from the osm servers
        Context ctx = getApplicationContext();
        Configuration.getInstance().load(ctx, PreferenceManager.getDefaultSharedPreferences(ctx));

        setContentView(R.layout.activity_main);

        // hide ActionBar
        // getActionBar().hide();

        // adding osmdroid map
        MapView map = (MapView) findViewById(R.id.map);
        map.setTileSource(TileSourceFactory.MAPNIK);
        // set position
        IMapController mapController = map.getController();
        mapController.setZoom(9);
        GeoPoint startPoint = new GeoPoint(51.341236, 12.374643);
        mapController.setCenter(startPoint);

        // Sleeper
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    for (int i = 0; i<50; i++) {
                        Thread.sleep(100);
                        ProgressBar progressBar = (ProgressBar) findViewById(R.id.progressBar);
                        progressBar.setProgress(i);
                    }

                    SharedPreferences prefs = getSharedPreferences("openrunning", MODE_PRIVATE);
                    String bid = prefs.getString("bid", "");
                    String type = prefs.getString("type", "");

                    SharedPreferences.Editor editor = getSharedPreferences("openrunning", MODE_PRIVATE).edit();

                    //locking for changed user infos
                    String result = DBHandler.updateuser(bid);

                    if (result.equals("0") || result.equals("1") || result.equals("2") || result.equals("3")){
                        editor.putString("type", result);
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
                } catch (InterruptedException e) {}
            }
        }).start();
    }
}
