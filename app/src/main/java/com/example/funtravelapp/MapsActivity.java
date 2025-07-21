package com.example.funtravelapp;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.net.Uri;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.os.AsyncTask;
import org.json.JSONArray;
import org.json.JSONObject;
import java.io.InputStreamReader;
import java.io.InputStream;
import java.io.BufferedReader;
import java.net.HttpURLConnection;
import java.net.URL;
import com.google.android.gms.maps.model.PolylineOptions;
import android.graphics.Color;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.*;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class MapsActivity extends AppCompatActivity implements OnMapReadyCallback {

    private static final int LOCATION_PERMISSION_REQUEST_CODE = 1;
    private GoogleMap mMap;
    private FusedLocationProviderClient fusedLocationClient;
    private LatLng currentLatLng;
    private LatLng selectedDestinationLatLng;
    private EditText destinationInput;
    private Button btnSearch;

    private final HashMap<String, LatLng> destinationCoords = new HashMap<>();
    private Marker destinationMarker;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);

        destinationInput = findViewById(R.id.search_input);
        btnSearch = findViewById(R.id.btn_search);
        Button btnBack = findViewById(R.id.btn_back);

        SupportMapFragment mapFragment = (SupportMapFragment)
                getSupportFragmentManager().findFragmentById(R.id.map);
        if (mapFragment != null) mapFragment.getMapAsync(this);

        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this);
        setupDestinationCoordinates();

        btnBack.setOnClickListener(v -> finish());

        destinationInput.setOnClickListener(v -> {
            String[] destinations = destinationCoords.keySet().toArray(new String[0]);
            new AlertDialog.Builder(this)
                    .setTitle("Select a Destination")
                    .setItems(destinations, (dialog, which) -> destinationInput.setText(destinations[which]))
                    .show();
        });

        ImageButton btnOpenMaps = findViewById(R.id.btn_open_maps);
        btnOpenMaps.setOnClickListener(v -> {
            if (currentLatLng != null && selectedDestinationLatLng != null) {
                String uri = String.format("http://maps.google.com/maps?saddr=%f,%f&daddr=%f,%f",
                        currentLatLng.latitude, currentLatLng.longitude,
                        selectedDestinationLatLng.latitude, selectedDestinationLatLng.longitude);
                Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(uri));
                intent.setPackage("com.google.android.apps.maps");

                if (intent.resolveActivity(getPackageManager()) != null) {
                    startActivity(intent);
                } else {
                    Toast.makeText(this, "Google Maps app is not installed.", Toast.LENGTH_SHORT).show();
                }
            } else {
                Toast.makeText(this, "Please select a destination first.", Toast.LENGTH_SHORT).show();
            }
        });

        btnSearch.setOnClickListener(v -> {
            String destination = destinationInput.getText().toString().trim();
            if (destination.isEmpty()) {
                Toast.makeText(this, "Please select a destination.", Toast.LENGTH_SHORT).show();
                return;
            }

            if (currentLatLng == null) {
                Toast.makeText(this, "Current location not ready yet.", Toast.LENGTH_SHORT).show();
                return;
            }

            selectedDestinationLatLng = destinationCoords.get(destination);
            if (selectedDestinationLatLng == null) {
                Toast.makeText(this, "No coordinates found for " + destination, Toast.LENGTH_SHORT).show();
                return;
            }

            double distanceKm = distance(currentLatLng.latitude, currentLatLng.longitude,
                    selectedDestinationLatLng.latitude, selectedDestinationLatLng.longitude);

            Toast.makeText(this, String.format("Distance to %s: %.2f km", destination, distanceKm),
                    Toast.LENGTH_LONG).show();

            mMap.clear();
            mMap.addMarker(new MarkerOptions().position(currentLatLng).title("You are here"));
            destinationMarker = mMap.addMarker(new MarkerOptions().position(selectedDestinationLatLng).title(destination));
            mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(selectedDestinationLatLng, 7f));

            drawRoute(currentLatLng, selectedDestinationLatLng);

        });
    }

    private void drawRoute(LatLng origin, LatLng destination) {
        String apiKey = "YOUR_API_KEY";
        String urlString = "https://maps.googleapis.com/maps/api/directions/json?" +
                "origin=" + origin.latitude + "," + origin.longitude +
                "&destination=" + destination.latitude + "," + destination.longitude +
                "&key=" + apiKey;

        new AsyncTask<String, Void, String>() {
            @Override
            protected String doInBackground(String... params) {
                try {
                    URL url = new URL(params[0]);
                    HttpURLConnection conn = (HttpURLConnection) url.openConnection();
                    conn.connect();

                    InputStream inputStream = conn.getInputStream();
                    BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream));
                    StringBuilder sb = new StringBuilder();
                    String line;

                    while ((line = reader.readLine()) != null) {
                        sb.append(line);
                    }

                    return sb.toString();
                } catch (Exception e) {
                    e.printStackTrace();
                    return null;
                }
            }

            @Override
            protected void onPostExecute(String result) {
                try {
                    JSONObject json = new JSONObject(result);
                    JSONArray routes = json.getJSONArray("routes");

                    if (routes.length() > 0) {
                        JSONObject route = routes.getJSONObject(0);
                        JSONObject overviewPolyline = route.getJSONObject("overview_polyline");
                        String encodedString = overviewPolyline.getString("points");

                        List<LatLng> points = decodePoly(encodedString);
                        PolylineOptions polylineOptions = new PolylineOptions()
                                .addAll(points)
                                .width(10f)
                                .color(Color.BLUE)
                                .geodesic(true);
                        mMap.addPolyline(polylineOptions);
                    }

                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }.execute(urlString);
    }

    private List<LatLng> decodePoly(String encoded) {
        List<LatLng> poly = new ArrayList<>();
        int index = 0, len = encoded.length();
        int lat = 0, lng = 0;

        while (index < len) {
            int b, shift = 0, result = 0;
            do {
                b = encoded.charAt(index++) - 63;
                result |= (b & 0x1f) << shift;
                shift += 5;
            } while (b >= 0x20);
            int dlat = ((result & 1) != 0 ? ~(result >> 1) : (result >> 1));
            lat += dlat;

            shift = 0;
            result = 0;
            do {
                b = encoded.charAt(index++) - 63;
                result |= (b & 0x1f) << shift;
                shift += 5;
            } while (b >= 0x20);
            int dlng = ((result & 1) != 0 ? ~(result >> 1) : (result >> 1));
            lng += dlng;

            LatLng p = new LatLng((lat / 1E5), (lng / 1E5));
            poly.add(p);
        }

        return poly;
    }



    private void setupDestinationCoordinates() {
        destinationCoords.put("Sunway Lagoon", new LatLng(3.0726, 101.6067));
        destinationCoords.put("Cameron Highlands", new LatLng(4.4711, 101.3778));
        destinationCoords.put("Sarawak Sunset River Cruise", new LatLng(1.5613, 110.3440));
        destinationCoords.put("Langkawi", new LatLng(6.3523, 99.7985));
        destinationCoords.put("Kundasang", new LatLng(6.0169, 116.5851));
        destinationCoords.put("Lost World of Tambun", new LatLng(4.6251, 101.1552));
        destinationCoords.put("Historical Melaka", new LatLng(2.1896, 102.2501));
        destinationCoords.put("Pantai Melawi", new LatLng(6.0282, 102.3982));
        destinationCoords.put("Genting Highlands", new LatLng(3.4232, 101.7932));
        destinationCoords.put("Kelam Cave", new LatLng(6.5384, 100.1987));
        destinationCoords.put("Lego Land", new LatLng(1.4274, 103.6314));
        destinationCoords.put("Pulau Perhentian", new LatLng(5.9112, 102.7224));
        destinationCoords.put("Masjid Sri Sendayan", new LatLng(2.7304, 101.8392));
        destinationCoords.put("Batu Caves", new LatLng(3.2379, 101.6841));
        destinationCoords.put("Labuan Chimney Museum", new LatLng(5.3921, 115.2457));
        destinationCoords.put("Putra Mosque", new LatLng(2.9361, 101.6911));

        destinationCoords.put("Icity", new LatLng(3.0738, 101.4855));
        destinationCoords.put("Bukit Tinggi", new LatLng(3.4038, 101.7957));
        destinationCoords.put("Bako National Park", new LatLng(1.7167, 110.4675));
        destinationCoords.put("Gunung Jerai Resort", new LatLng(5.7954, 100.4253));
        destinationCoords.put("Pulau Mabul", new LatLng(4.2449, 118.6285));
        destinationCoords.put("Pulau Pangkor", new LatLng(4.2022, 100.5534));
        destinationCoords.put("Taman Air A'Famosa", new LatLng(2.4525, 102.2020));
        destinationCoords.put("Gunung Stong", new LatLng(5.3858, 101.9575));
        destinationCoords.put("Taman Tema Escape", new LatLng(5.4667, 100.2586));
        destinationCoords.put("Kuala Perlis", new LatLng(6.4001, 100.1343));
        destinationCoords.put("Sea life Malaysia", new LatLng(1.4246, 103.6312));
        destinationCoords.put("Drawbridge", new LatLng(5.3368, 103.1396));
        destinationCoords.put("Lexis Hibiscus", new LatLng(2.4307, 101.8602));
        destinationCoords.put("Menara Kuala Lumpur", new LatLng(3.1528, 101.7037));
        destinationCoords.put("Menara Jam", new LatLng(5.2750, 115.2446));
        destinationCoords.put("Putrajaya Secret Garden", new LatLng(2.9319, 101.6765));
    }

    @Override
    public void onMapReady(@NonNull GoogleMap googleMap) {
        mMap = googleMap;

        mMap.setOnMarkerClickListener(marker -> {
            if (selectedDestinationLatLng != null &&
                    marker.getPosition().equals(selectedDestinationLatLng)) {
                if (currentLatLng != null) {
                    String uri = String.format("http://maps.google.com/maps?saddr=%f,%f&daddr=%f,%f",
                            currentLatLng.latitude, currentLatLng.longitude,
                            selectedDestinationLatLng.latitude, selectedDestinationLatLng.longitude);
                    Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(uri));
                    intent.setPackage("com.google.android.apps.maps");

                    if (intent.resolveActivity(getPackageManager()) != null) {
                        startActivity(intent);
                    } else {
                        Toast.makeText(this, "Google Maps app is not installed.", Toast.LENGTH_SHORT).show();
                    }
                } else {
                    Toast.makeText(this, "Current location not found.", Toast.LENGTH_SHORT).show();
                }
                return true;
            }
            return false;
        });

        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)
                == PackageManager.PERMISSION_GRANTED) {
            mMap.setMyLocationEnabled(true);

            fusedLocationClient.getLastLocation()
                    .addOnSuccessListener(this, location -> {
                        if (location != null) {
                            currentLatLng = new LatLng(location.getLatitude(), location.getLongitude());
                            mMap.addMarker(new MarkerOptions().position(currentLatLng).title("You are here"));
                            mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(currentLatLng, 12f));
                        }
                    });

        } else {
            ActivityCompat.requestPermissions(this,
                    new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                    LOCATION_PERMISSION_REQUEST_CODE);
        }
    }

    private static double distance(double lat1, double lng1, double lat2, double lng2) {
        double earthRadius = 6371; // km
        double dLat = Math.toRadians(lat2 - lat1);
        double dLng = Math.toRadians(lat2 - lat1);
        double a = Math.sin(dLat / 2) * Math.sin(dLat / 2)
                + Math.cos(Math.toRadians(lat1)) * Math.cos(Math.toRadians(lat2))
                * Math.sin(dLng / 2) * Math.sin(dLng / 2);
        return earthRadius * 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1 - a));
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions,
                                           @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        if (requestCode == LOCATION_PERMISSION_REQUEST_CODE &&
                grantResults.length > 0 &&
                grantResults[0] == PackageManager.PERMISSION_GRANTED) {
            if (mMap != null) {
                onMapReady(mMap); // Retry map setup
            }
        } else {
            Toast.makeText(this, "Location permission denied", Toast.LENGTH_SHORT).show();
        }
    }
}
