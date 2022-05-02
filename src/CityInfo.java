

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Set;

import ir.Document;

public class CityInfo {
	
	public CityInfo(ArrayList<Document> documents) {
		GeoApiContext context = new GeoApiContext.Builder()
			    .apiKey("AIza...")
			    .build();
			GeocodingResult[] results =  GeocodingApi.geocode(context,
			    "1600 Amphitheatre Parkway Mountain View, CA 94043").await();
			Gson gson = new GsonBuilder().setPrettyPrinting().create();
			System.out.println(gson.toJson(results[0].addressComponents));

			// Invoke .shutdown() after your application is done making requests
			context.shutdown();
	}
	
	

// Geocoder Result
//	results[]: {
//		 types[]: string,
//		 formatted_address: string,
//		 address_components[]: {
//		   short_name: string,
//		   long_name: string,
//		   postcode_localities[]: string,
//		   types[]: string
//		 },
//		 partial_match: boolean,
//		 place_id: string,
//		 postcode_localities[]: string,
//		 geometry: {
//		   location: LatLng,
//		   location_type: GeocoderLocationType
//		   viewport: LatLngBounds,
//		   bounds: LatLngBounds
//		 }


}
