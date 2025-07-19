package com.example.funtravelapp;

import android.app.AlertDialog;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

public class BookingActivity extends AppCompatActivity {

    private ImageView bookingImage;
    private TextView bookingName, bookingLocation, bookingRating, bookingDescription, bookingPrice;
    private Button confirmBookingButton;

    private float ratingValue;
    private int imageResId;
    private String name, location, price;
    private TextView bookingBestTime, bookingHighlights, bookingFacilities, bookingTips;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_booking);

        Button backBtn = findViewById(R.id.btn_back);
        backBtn.setOnClickListener(v -> finish());

        // Link Views
        bookingImage = findViewById(R.id.booking_image);
        bookingName = findViewById(R.id.booking_name);
        bookingLocation = findViewById(R.id.booking_location);
        bookingRating = findViewById(R.id.booking_rating);
        bookingDescription = findViewById(R.id.booking_description);
        bookingPrice = findViewById(R.id.booking_price);
        confirmBookingButton = findViewById(R.id.confirm_booking_button);
        bookingBestTime = findViewById(R.id.booking_best_time);
        bookingHighlights = findViewById(R.id.booking_highlights);
        bookingFacilities = findViewById(R.id.booking_facilities);
        bookingTips = findViewById(R.id.booking_tips);

        // Receive data from MainDashboardActivity
        name = getIntent().getStringExtra("destination_name");
        location = getIntent().getStringExtra("destination_location");
        price = getIntent().getStringExtra("destination_price");
        ratingValue = getIntent().getFloatExtra("destination_rating", 0f);
        imageResId = getIntent().getIntExtra("destination_image", R.drawable.bgapp);

        // Set Description (can customize more)
        String description;

        switch (name) {
            case "Sunway Lagoon":
                description = "A huge theme park in Selangor offering thrilling water rides, wildlife encounters, and fun-filled attractions for all ages.";
                break;
            case "Cameron Highlands":
                description = "Famous for cool climate, vast tea plantations, fresh strawberries, and scenic hiking trails in the highlands of Pahang.";
                break;
            case "Sarawak Sunset River Cruise":
                description = "Relax on a scenic evening cruise along the Sarawak River, enjoying sunset views and traditional cultural performances.";
                break;
            case "Langkawi":
                description = "Tropical island paradise with white sandy beaches, waterfalls, cable car rides, and vibrant night markets.";
                break;
            case "Kundasang":
                description = "Mountain town in Sabah known for breathtaking views of Mount Kinabalu and beautiful flower gardens.";
                break;
            case "Lost World of Tambun":
                description = "Adventure park in Perak featuring water slides, hot springs, and a petting zoo, perfect for families.";
                break;
            case "Historical Melaka":
                description = "UNESCO heritage city with charming colonial buildings, rich history, and famous local cuisine.";
                break;
            case "Pantai Melawi":
                description = "Quiet, unspoiled beach in Kelantan, ideal for picnics and enjoying nature’s tranquility.";
                break;
            case "Genting Highlands":
                description = "Cool hill resort with casinos, theme parks, shopping malls, and entertainment for all ages.";
                break;
            case "Kelam Cave":
                description = "Natural limestone cave in Perlis offering easy trails and unique rock formations to explore.";
                break;
            case "Lego Land":
                description = "Family-friendly theme park in Johor with Lego-themed rides, shows, and creative play areas.";
                break;
            case "Pulau Perhentian":
                description = "Island getaway with clear turquoise waters, great snorkeling, diving, and relaxed beach vibes.";
                break;
            case "Masjid Sri Sendayan":
                description = "Modern mosque in Negeri Sembilan showcasing elegant Islamic architecture and peaceful surroundings.";
                break;
            case "Batu Caves":
                description = "Iconic limestone caves with a giant golden statue and Hindu temples, located near Kuala Lumpur.";
                break;
            case "Labuan Chimney Museum":
                description = "Historic coal mining site in Labuan with preserved chimneys and exhibits on island heritage.";
                break;
            case "Putra Mosque":
                description = "Beautiful pink-domed mosque in Putrajaya, famous for its grand design and scenic lake views.";
                break;
            case "Icity":
                description = "Shah Alam’s vibrant digital city with dazzling light displays, snow-themed park, and fun water rides.";
                break;
            case "Bukit Tinggi":
                description = "French-themed village with charming architecture, beautiful gardens, and Japanese cultural spots.";
                break;
            case "Bako National Park":
                description = "Borneo’s oldest national park featuring jungle trekking, wildlife spotting, and secluded beaches.";
                break;
            case "Gunung Jerai Resort":
                description = "Hilltop retreat in Kedah offering cool weather, panoramic views, and nature trails.";
                break;
            case "Pulau Mabul":
                description = "Small island in Sabah popular for diving, marine life, and laid-back island atmosphere.";
                break;
            case "Pulau Pangkor":
                description = "Peaceful island in Perak known for beaches, seafood, and fishing villages.";
                break;
            case "Taman Air A'Famosa":
                description = "Water theme park and safari in Melaka with thrilling rides and animal encounters.";
                break;
            case "Gunung Stong":
                description = "Nature lover’s spot in Kelantan with waterfalls, jungle trails, and adventure activities.";
                break;
            case "Taman Tema Escape":
                description = "Adventure park in Penang offering zip lines, water slides, and outdoor challenges.";
                break;
            case "Kuala Perlis":
                description = "Coastal town in Perlis famous for fresh seafood and relaxing waterfront views.";
                break;
            case "Sea life Malaysia":
                description = "Aquarium in Johor showcasing diverse marine species and interactive exhibits.";
                break;
            case "Drawbridge":
                description = "Famous drawbridge in Terengganu offering scenic river views and photography spots.";
                break;
            case "Lexis Hibiscus":
                description = "Luxury resort in Negeri Sembilan with private pool villas and stunning hibiscus-shaped design.";
                break;
            case "Menara Kuala Lumpur":
                description = "Tall communications tower with observation decks offering panoramic views of Kuala Lumpur.";
                break;
            case "Menara Jam":
                description = "Unique clock tower landmark in Labuan featuring traditional design and historical value.";
                break;
            case "Putrajaya Secret Garden":
                description = "Serene garden in Putrajaya filled with colorful flowers, sculptures, and tranquil walking paths.";
                break;
            default:
                description = "Explore this amazing destination and enjoy unforgettable travel experiences.";
        }

        String bestTime, highlights, facilities, tips;

        switch (name) {
            case "Sunway Lagoon":
                bestTime = "Weekdays, Avoid public holidays";
                highlights = "Water Park, Wildlife Park, Extreme Park";
                facilities = "Lockers, Food Courts, Changing Rooms";
                tips = "Bring swimwear, sunscreen, arrive early";
                break;
            case "Cameron Highlands":
                bestTime = "March to August";
                highlights = "Tea Plantation, Strawberry Farm, Mossy Forest";
                facilities = "Parking, Cafeteria, Souvenir Shop";
                tips = "Bring jacket, avoid weekends, carry cash";
                break;
            case "Sarawak Sunset River Cruise":
                bestTime = "Evening hours";
                highlights = "River Views, Sunset, Cultural Performances";
                facilities = "Boat Service, Refreshments";
                tips = "Arrive early, bring camera, wear light clothing";
                break;
            case "Langkawi":
                bestTime = "November to March";
                highlights = "Beaches, Cable Car, Sky Bridge";
                facilities = "Hotels, Car Rentals, Restaurants";
                tips = "Rent a car, wear sunscreen, explore islands";
                break;
            case "Kundasang":
                bestTime = "March to July";
                highlights = "Mount Kinabalu View, Flower Gardens";
                facilities = "Homestays, Cafes, Parking";
                tips = "Bring sweater, book in advance, visit early morning";
                break;
            case "Lost World of Tambun":
                bestTime = "Weekdays";
                highlights = "Water Park, Hot Springs, Petting Zoo";
                facilities = "Changing Rooms, Lockers, Food Stalls";
                tips = "Bring towel, check schedule, wear water shoes";
                break;
            case "Historical Melaka":
                bestTime = "Evenings or non-peak seasons";
                highlights = "A Famosa, Jonker Street, Museums";
                facilities = "Tourist Info Center, Street Food, Hotels";
                tips = "Walk around, try local food, visit museums";
                break;
            case "Pantai Melawi":
                bestTime = "Dry season (March to September)";
                highlights = "Quiet Beach, Picnic, Sunset Views";
                facilities = "Basic Shops, Parking";
                tips = "Bring own food, swim safely, check tides";
                break;
            case "Genting Highlands":
                bestTime = "Anytime except holidays";
                highlights = "Theme Parks, Casino, Cable Car";
                facilities = "Hotels, Malls, Restaurants";
                tips = "Dress warm, avoid public holidays, book early";
                break;
            case "Kelam Cave":
                bestTime = "Morning or dry days";
                highlights = "Cave Trail, Rock Formations";
                facilities = "Rest Stops, Walking Paths";
                tips = "Bring torchlight, wear good shoes, stay on trail";
                break;
            case "Lego Land":
                bestTime = "Weekdays, avoid school holidays";
                highlights = "Theme Park, Water Park, Lego Kingdom";
                facilities = "Shops, Restaurants, Lockers";
                tips = "Buy tickets online, wear comfy shoes";
                break;
            case "Pulau Perhentian":
                bestTime = "April to October (Dry Season)";
                highlights = "Snorkeling, Scuba Diving, White Beaches";
                facilities = "Boat Transfer, Dive Shops, Cafes";
                tips = "Book early, bring mosquito repellent, no ATM on island";
                break;
            case "Masjid Sri Sendayan":
                bestTime = "Evening for night view";
                highlights = "Architecture, Islamic Designs";
                facilities = "Parking, Toilets, Prayer Areas";
                tips = "Dress modestly, visit during non-prayer time";
                break;
            case "Batu Caves":
                bestTime = "Morning";
                highlights = "Golden Statue, Hindu Temple, Caves";
                facilities = "Shops, Toilets, Parking";
                tips = "Wear proper attire, bring water, be respectful";
                break;
            case "Labuan Chimney Museum":
                bestTime = "Morning";
                highlights = "Chimneys, History Exhibit";
                facilities = "Museum Guide, Rest Area";
                tips = "Read exhibits, bring hat, hydrate";
                break;
            case "Putra Mosque":
                bestTime = "Morning or Sunset";
                highlights = "Architecture, Lake View, Prayer Hall";
                facilities = "Tourist Guide, Parking";
                tips = "Wear modest clothes, remove shoes inside";
                break;
            case "Icity":
                bestTime = "Night";
                highlights = "Light Park, Snow Walk, Water World";
                facilities = "Food Courts, Shops, Parking";
                tips = "Visit at night, bring jacket for snow zone";
                break;
            case "Bukit Tinggi":
                bestTime = "Morning to evening";
                highlights = "French Village, Japanese Garden";
                facilities = "Parking, Cafes, Shops";
                tips = "Wear comfy shoes, bring camera";
                break;
            case "Bako National Park":
                bestTime = "Dry Season (April to September)";
                highlights = "Trekking, Wildlife, Beaches";
                facilities = "Lodges, Trails, Guided Tours";
                tips = "Bring insect repellent, water, hiking shoes";
                break;
            case "Gunung Jerai Resort":
                bestTime = "Morning";
                highlights = "Hill Views, Trails, Flora";
                facilities = "Resort, Parking, Café";
                tips = "Bring jacket, stay overnight if possible";
                break;
            case "Pulau Mabul":
                bestTime = "May to September";
                highlights = "Diving, Marine Life";
                facilities = "Resorts, Dive Shops";
                tips = "Book diving slots early, avoid monsoon season";
                break;
            case "Pulau Pangkor":
                bestTime = "February to October";
                highlights = "Beaches, Seafood, Temples";
                facilities = "Boat, Hotels, Food Stalls";
                tips = "Bring snacks, explore by motorcycle";
                break;
            case "Taman Air A'Famosa":
                bestTime = "Weekdays";
                highlights = "Water Rides, Safari, Shows";
                facilities = "Parking, Lockers, Rest Areas";
                tips = "Arrive early, plan route, wear dry-fit clothes";
                break;
            case "Gunung Stong":
                bestTime = "Dry season";
                highlights = "Waterfalls, Jungle Trails, Camping";
                facilities = "Campsite, Guide Service";
                tips = "Bring gear, hike early, use insect spray";
                break;
            case "Taman Tema Escape":
                bestTime = "Morning on weekdays";
                highlights = "Zip Line, Obstacle Course, Water Slide";
                facilities = "Lockers, Cafes";
                tips = "Be active, wear sporty attire";
                break;
            case "Kuala Perlis":
                bestTime = "Evening";
                highlights = "Seafood, Jetty, Sunset";
                facilities = "Stalls, Jetty, Ferry";
                tips = "Arrive early, check ferry schedule";
                break;
            case "Sea life Malaysia":
                bestTime = "Weekdays";
                highlights = "Marine Life, Interactive Exhibits";
                facilities = "Restrooms, Cafes, Gift Shop";
                tips = "Great for kids, bring camera";
                break;
            case "Drawbridge":
                bestTime = "Sunset or night";
                highlights = "Bridge Views, Cityscape";
                facilities = "Parking, Walkway";
                tips = "Visit for photos, evening lights";
                break;
            case "Lexis Hibiscus":
                bestTime = "All year";
                highlights = "Private Pool Villas, Beach";
                facilities = "Spa, Room Service, Water Sports";
                tips = "Book sea-facing rooms, try water activities";
                break;
            case "Menara Kuala Lumpur":
                bestTime = "Evening";
                highlights = "City View, Tower Walk";
                facilities = "Restaurants, Decks, Shops";
                tips = "Go during sunset, bring camera";
                break;
            case "Menara Jam":
                bestTime = "Daylight hours";
                highlights = "Historic Clock Tower";
                facilities = "Parking, Info Signboards";
                tips = "Take pictures, visit nearby landmarks";
                break;
            case "Putrajaya Secret Garden":
                bestTime = "Morning";
                highlights = "Flower Gardens, Sculptures";
                facilities = "Benches, Shaded Areas";
                tips = "Wear comfy shoes, bring water";
                break;
            default:
                bestTime = "Year-round";
                highlights = "Various local attractions and activities";
                facilities = "Basic tourist facilities available";
                tips = "Prepare essentials, check weather, stay hydrated";
                break;
        }

        // Populate Views
        bookingImage.setImageResource(imageResId);
        bookingName.setText(name);
        bookingLocation.setText(location);
        bookingRating.setText(ratingValue + " ★");
        bookingBestTime.setText("Best Time to Visit: " + bestTime);
        bookingHighlights.setText("Activity Highlights: " + highlights);
        bookingFacilities.setText("Facilities Provided: " + facilities);
        bookingTips.setText("Travel Tips: " + tips);
        bookingPrice.setText(price + " / Person");
        bookingDescription.setText(description);

        TextView contactUsLink = findViewById(R.id.contact_us_link);
        contactUsLink.setOnClickListener(view -> showContactOptions());

        // Link to InformationBooking
        confirmBookingButton.setOnClickListener(v -> {
            Intent intent = new Intent(BookingActivity.this, InformationBooking.class);
            intent.putExtra("destination_name", name);
            intent.putExtra("destination_location", location);
            intent.putExtra("destination_price", price);
            intent.putExtra("destination_rating", ratingValue);
            intent.putExtra("destination_image", imageResId);
            startActivity(intent);
        });
    }

    // Method to show contact options in a dialog
    private void showContactOptions() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Contact Us");
        builder.setItems(new CharSequence[]{
                        "Phone Call",
                        "Send WhatsApp",
                        "Visit Website",
                        "Send Email"},
                (dialog, which) -> {
                    switch (which) {
                        case 0: // Phone Call
                            Intent callIntent = new Intent(Intent.ACTION_DIAL);
                            callIntent.setData(Uri.parse("tel:+60135350396"));
                            startActivity(callIntent);
                            break;

                        case 1: // WhatsApp
                            Intent whatsappIntent = new Intent(Intent.ACTION_VIEW);
                            whatsappIntent.setData(Uri.parse("https://wa.link/16oiet"));
                            startActivity(whatsappIntent);
                            break;

                        case 2: // Website
                            Intent webIntent = new Intent(Intent.ACTION_VIEW);
                            webIntent.setData(Uri.parse("https://www.funtravel.com"));
                            startActivity(webIntent);
                            break;

                        case 3: // Email
                            Intent emailIntent = new Intent(Intent.ACTION_SENDTO);
                            emailIntent.setData(Uri.parse("mailto:funtravelapp@example.com"));
                            emailIntent.putExtra(Intent.EXTRA_SUBJECT, "FunTravelApp Inquiry");
                            startActivity(emailIntent);
                            break;
                    }
                });
        builder.show();
    }
}
