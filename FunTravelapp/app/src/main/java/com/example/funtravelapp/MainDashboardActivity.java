package com.example.funtravelapp;

import android.app.AlertDialog;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.navigation.NavigationView;

import java.util.ArrayList;
import java.util.List;

public class MainDashboardActivity extends AppCompatActivity {

    private DrawerLayout drawerLayout;
    private NavigationView navigationView;
    private Toolbar toolbar;

    private Button searchButton;
    private RecyclerView popularRecyclerView, recommendedRecyclerView;
    private List<Destination> popularDestinations;
    private List<Destination> recommendedDestinations;
    private DestinationAdapter popularAdapter;
    private DestinationAdapter recommendedAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_dashboard);

        // Set up Toolbar
        toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        // Set up DrawerLayout and NavigationView
        drawerLayout = findViewById(R.id.drawer_menu); // Make sure your XML has this ID
        navigationView = findViewById(R.id.nav_view);

        // Set up Drawer Toggle
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawerLayout, toolbar,
                R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawerLayout.addDrawerListener(toggle);
        toggle.syncState();

        //  Handle Drawer Menu Clicks including Logout
        navigationView.setNavigationItemSelectedListener(item -> {
            int id = item.getItemId();

            if (id == R.id.nav_map) {
                startActivity(new Intent(this, MapsActivity.class));
            } else if (id == R.id.nav_scanner) {
                startActivity(new Intent(this, ScannerActivity.class));
            } else if (id == R.id.nav_about_us) {
                startActivity(new Intent(this, AboutUs.class));
            } else if (id == R.id.nav_history) {
                startActivity(new Intent(this, BookingHistoryActivity.class));
            } else if (id == R.id.nav_logout) {
                // LOGOUT action
                Intent intent = new Intent(MainDashboardActivity.this, WelcomeActivity.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                startActivity(intent);
            }

            // Close drawer after item click
            drawerLayout.closeDrawer(GravityCompat.START);
            return true;
        });

        // 🔍 Set up search button with AlertDialog to select state
        searchButton = findViewById(R.id.search_button);
        searchButton.setOnClickListener(v -> {
            String[] states = getResources().getStringArray(R.array.malaysia_states);
            AlertDialog.Builder builder = new AlertDialog.Builder(MainDashboardActivity.this);
            builder.setTitle("Select a State")
                    .setItems(states, (dialog, which) -> {
                        String selectedState = states[which];
                        filterDestinationsByState(selectedState);
                    });
            builder.create().show();
        });

        EditText searchInput = findViewById(R.id.search_input);
        Button searchButton = findViewById(R.id.search_button);

        // When EditText is clicked, show the list of states
                searchInput.setOnClickListener(v -> {
                    String[] states = getResources().getStringArray(R.array.malaysia_states);
                    AlertDialog.Builder builder = new AlertDialog.Builder(MainDashboardActivity.this);
                    builder.setTitle("Select a State")
                            .setItems(states, (dialog, which) -> {
                                String selectedState = states[which];
                                searchInput.setText(selectedState); // Just set text
                            });
                    builder.create().show();
                });

        // When Search button is clicked, filter based on selected state
                searchButton.setOnClickListener(v -> {
                    String selectedState = searchInput.getText().toString().trim();
                    if (!selectedState.isEmpty()) {
                        filterDestinationsByState(selectedState); //  Filter only on Search click
                    }
                });

        //  RecyclerViews
        popularRecyclerView = findViewById(R.id.recycler_popular);
        recommendedRecyclerView = findViewById(R.id.recycler_recommended);
        popularRecyclerView.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false));
        recommendedRecyclerView.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false));

        //  Load Data
        setupDestinationData();
    }

    private void setupDestinationData() {
        //  Don't change this part as per your request
        popularDestinations = new ArrayList<>();
        popularDestinations.add(new Destination("Sunway Lagoon", "Selangor, Malaysia", "RM175", 4.8f, R.drawable.sunway));
        popularDestinations.add(new Destination("Cameron Highlands", "Pahang, Malaysia", "RM200", 4.5f, R.drawable.cameron));
        popularDestinations.add(new Destination("Sarawak Sunset River Cruise", "Sarawak, Malaysia", "RM50", 4.2f, R.drawable.sarawaksunset));
        popularDestinations.add(new Destination("Langkawi", "Kedah, Malaysia", "RM180", 4.1f, R.drawable.langkawi));
        popularDestinations.add(new Destination("Kundasang", "Sabah, Malaysia", "RM100", 4.0f, R.drawable.kundasang));
        popularDestinations.add(new Destination("Lost World of Tambun", "Perak, Malaysia", "RM150", 4.4f, R.drawable.tambun));
        popularDestinations.add(new Destination("Historical Melaka", "Melaka, Malaysia", "RM30", 4.3f, R.drawable.melaka));
        popularDestinations.add(new Destination("Pantai Melawi", "Kelantan, Malaysia", "Free", 4.7f, R.drawable.pantaimelawi));
        popularDestinations.add(new Destination("Genting Highlands", "Pahang, Malaysia", "RM175", 4.8f, R.drawable.gentinghighlands));
        popularDestinations.add(new Destination("Kelam Cave", "Perlis, Malaysia", "RM50", 4.3f, R.drawable.kelamcave));
        popularDestinations.add(new Destination("Lego Land", "Johor, Malaysia", "RM230", 4.5f, R.drawable.legoland));
        popularDestinations.add(new Destination("Pulau Perhentian", "Terengganu, Malaysia", "RM189", 4.6f, R.drawable.pulauperhentian));
        popularDestinations.add(new Destination("Masjid Sri Sendayan", "Negeri Sembilan, Malaysia", "Free", 4.5f, R.drawable.masjidsrisendayan));
        popularDestinations.add(new Destination("Batu Caves", "W.P Kuala Lumpur, Malaysia", "Free", 4.2f, R.drawable.batucaves));
        popularDestinations.add(new Destination("Labuan Chimney Museum", "W.P Labuan, Malaysia", "Free", 4.1f, R.drawable.labuanchimney));
        popularDestinations.add(new Destination("Putra Mosque", "W.P Putrajaya, Malaysia", "Free", 4.4f, R.drawable.putramosque));
        popularAdapter = new DestinationAdapter(popularDestinations, MainDashboardActivity.this);
        popularRecyclerView.setAdapter(popularAdapter);

        recommendedDestinations = new ArrayList<>();
        recommendedDestinations.add(new Destination("Icity", "Selangor, Malaysia", "RM150", 4.8f, R.drawable.icity));
        recommendedDestinations.add(new Destination("Bukit Tinggi", "Pahang, Malaysia", "RM80", 4.5f, R.drawable.bukittinggi));
        recommendedDestinations.add(new Destination("Bako National Park", "Sarawak, Malaysia", "RM90", 4.2f, R.drawable.bako));
        recommendedDestinations.add(new Destination("Gunung Jerai Resort", "Kedah, Malaysia", "RM120", 4.1f, R.drawable.gunungjerai));
        recommendedDestinations.add(new Destination("Pulau Mabul", "Sabah, Malaysia", "RM100", 4.0f, R.drawable.pulaumabul));
        recommendedDestinations.add(new Destination("Pulau Pangkor", "Perak, Malaysia", "RM95", 4.4f, R.drawable.pulaupangkor));
        recommendedDestinations.add(new Destination("Taman Air A'Famosa", "Melaka, Malaysia", "RM165", 4.3f, R.drawable.tamanfamosa));
        recommendedDestinations.add(new Destination("Gunung Stong", "Kelantan, Malaysia", "Free", 4.7f, R.drawable.gunungstong));
        recommendedDestinations.add(new Destination("Taman Tema Escape", "Pulau Pinang, Malaysia", "RM200", 4.8f, R.drawable.tamantemaescape));
        recommendedDestinations.add(new Destination("Kuala Perlis", "Perlis, Malaysia", "RM100", 4.3f, R.drawable.kualaperlis));
        recommendedDestinations.add(new Destination("Sea life Malaysia", "Johor, Malaysia", "RM230", 4.5f, R.drawable.sealife));
        recommendedDestinations.add(new Destination("Drawbridge", "Terengganu, Malaysia", "RM50", 4.6f, R.drawable.drawbridge));
        recommendedDestinations.add(new Destination("Lexis Hibiscus", "Negeri Sembilan, Malaysia", "RM850", 5.0f, R.drawable.lexis));
        recommendedDestinations.add(new Destination("Menara Kuala Lumpur", "W.P Kuala Lumpur, Malaysia", "RM25", 4.2f, R.drawable.menarakl));
        recommendedDestinations.add(new Destination("Menara Jam", "W.P Labuan, Malaysia", "RM30", 4.1f, R.drawable.menarajam));
        recommendedDestinations.add(new Destination("Putrajaya Secret Garden", "W.P Putrajaya, Malaysia", "RM35", 4.4f, R.drawable.putrajaya));
        recommendedAdapter = new DestinationAdapter(recommendedDestinations, MainDashboardActivity.this);
        recommendedRecyclerView.setAdapter(recommendedAdapter);
    }

    // Filter function for Search Button
    private void filterDestinationsByState(String selectedState) {
        List<Destination> filteredPopular = new ArrayList<>();
        for (Destination d : popularDestinations) {
            if (d.getLocation().contains(selectedState)) {
                filteredPopular.add(d);
            }
        }

        List<Destination> filteredRecommended = new ArrayList<>();
        for (Destination d : recommendedDestinations) {
            if (d.getLocation().contains(selectedState)) {
                filteredRecommended.add(d);
            }
        }

        // Update adapters with Activity context
        popularAdapter = new DestinationAdapter(filteredPopular, MainDashboardActivity.this);
        popularRecyclerView.setAdapter(popularAdapter);

        recommendedAdapter = new DestinationAdapter(filteredRecommended, MainDashboardActivity.this);
        recommendedRecyclerView.setAdapter(recommendedAdapter);
    }


    @Override
    public void onBackPressed() {
        if (drawerLayout.isDrawerOpen(GravityCompat.START)) {
            drawerLayout.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }
}