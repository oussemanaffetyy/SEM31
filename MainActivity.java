package com.gps;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Build;
import android.os.Bundle;
import android.provider.Settings;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

public class MainActivity extends AppCompatActivity {
    private static final int REQUEST_ACCESS_COURSE_FINE_LOCATION = 1;
    private TextView mLocation;
    private Button btnActiver;
    private LocationManager mg;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        init();
    }

    @SuppressLint("WrongViewCast")
    private void init() {
        mLocation = findViewById(R.id.mLocation);
        btnActiver = findViewById(R.id.btnActiver);
        mg = (LocationManager) getSystemService(LOCATION_SERVICE);
        demanderPermissionGPS();
        if (!mg.isProviderEnabled(LocationManager.GPS_PROVIDER))
            demanderActivationGPS();
        ajouterEcouteur();
    }

    private void demanderPermissionGPS() {
        String[] permissions = new String[]{Manifest.permission.ACCESS_COARSE_LOCATION,
                Manifest.permission.ACCESS_FINE_LOCATION};
        // Show a dialog asking the user to allow the above permissions.
        ActivityCompat.requestPermissions(this, permissions,
                REQUEST_ACCESS_COURSE_FINE_LOCATION);

    }

    private void demanderActivationGPS() {
        Intent i = new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
        startActivity(i);
    }

    private void ajouterEcouteur() {
        btnActiver.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                demanderActivationGPS();
            }
        });
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (checkSelfPermission(Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && checkSelfPermission(Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                // TODO: Consider calling
                //    Activity#requestPermissions
                // The Permissions to ask user.
                String[] permissions = new String[]{Manifest.permission.ACCESS_COARSE_LOCATION,
                        Manifest.permission.ACCESS_FINE_LOCATION};
                // Show a dialog asking the user to allow the above permissions.
                ActivityCompat.requestPermissions(this, permissions,
                        REQUEST_ACCESS_COURSE_FINE_LOCATION);

                return;
            }
        }

        mg.requestLocationUpdates(LocationManager.GPS_PROVIDER, 1000, 0, new LocationListener() {
            @Override
            public void onLocationChanged(Location location) {
                afficherPosition(location);
            }

            @Override
            public void onStatusChanged(String provider, int status, Bundle extras) {

            }

            @Override
            public void onProviderEnabled(String provider) {

            }

            @Override
            public void onProviderDisabled(String provider) {

            }
        });


    }

    @Override
    protected void onPostResume() {
        if (mg.isProviderEnabled(LocationManager.GPS_PROVIDER)) {
            ajouterEcouteur();
            mLocation.setText(getResources().getText(R.string.localisation_vide));
        } else
            mLocation.setText("La localisation n'est pas activée!");
        super.onPostResume();
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        switch (requestCode) {
            case REQUEST_ACCESS_COURSE_FINE_LOCATION:
                // Note: If request is cancelled, the result arrays are empty.
                // Permissions granted (read/write).
                if (grantResults.length > 1
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED
                        && grantResults[1] == PackageManager.PERMISSION_GRANTED) {
                    ajouterEcouteur();
                }
                // Cancelled or denied.
                else {
                    Toast.makeText(this, "Permissions non accordées!", Toast.LENGTH_LONG).show();
                }
                break;
        }

        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
    }

    private void afficherPosition(Location location) {
        String res = "";

        res = "Position:\n";
        res += "\tLongitude: " + location.getLongitude() + "\n";
        res += "\tLatitude: " + location.getLatitude() + "\n";

        mLocation.setText(res);

    }


}
