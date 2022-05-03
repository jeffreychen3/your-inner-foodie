import java.util.ArrayList;
import java.util.HashMap;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonStreamParser;

public class CityInfo {
	
	private String city;
	private Double[][] cityBounds;
	private Double[] cityPos;
	private HashMap<String, String> restToAddress;
	
	private final String GEOCODING_REQ_BASE = "https://maps.googleapis.com/maps/api/geocode/json?";
	private final String PLACES_REQ_BASE = "https://maps.googleapis.com/maps/api/place/nearbysearch/json?";
	private final String DISTANCE_REQ_BASE = "https://maps.googleapis.com/maps/api/distancematrix/json?";
	private final String API_KEY = "AIzaSyBganwo8Y1dKUcU51MRnQa-kCDlB6EulNM";
	private final int RADIUS = 1500;
	
	public CityInfo(String city) {
		this.city = city.replace(" ", "-");;
		this.cityBounds = null;
		this.cityPos = null;
		this.restToAddress = new HashMap<>();
	}
	
	
	public String getCityName() {
		return this.city;
	}
	
	/**
	 * 
	 * @return cityBounds - null: city is invalid or doesn't have bounds
	 * 						index 0: {lat, lng} for north east
	 * 						index 1: {lat, lng} for south west
	 */
	public Double[][] getCityBounds() {
		return cityBounds;
	}
	
	/**
	 * 
	 * @return cityPos - null: city is invalid or doesn't have bounds
	 * 					{lat, lng} 
	 */
	public Double[] getCityPos() {
		return cityPos;
	}
	
	
	/**
	 Geocoder Result Format from Google Maps
			results[]: {
				 types[]: string,
				 formatted_address: string,
				 address_components[]: {
				   short_name: string,
				   long_name: string,
				   postcode_localities[]: string,
				   types[]: string
				 },
				 partial_match: boolean,
				 place_id: string,
				 postcode_localities[]: string,
				 geometry: {
				   location: LatLng,
				   location_type: GeocoderLocationType
				   viewport: LatLngBounds,
				   bounds: LatLngBounds
				 }
	*/
	
	//City is valid if defined as "locality" under address_components
	public boolean isValidCity() {
		Gson gson = new GsonBuilder().setPrettyPrinting().create();
		
		URLGetter url = new URLGetter(GEOCODING_REQ_BASE + "address=" + city + "&key=" + API_KEY);
		ArrayList<String> res = url.getContents();
		JsonObject resObj = convertToJsonObject(res);
		
		//No result matching city, no info
		if (!resObj.get("status").getAsString().equals("OK")) {
			return false;
		} 
		
		JsonArray addressComponents = resObj.get("results").getAsJsonArray().get(0).getAsJsonObject().get("address_components").getAsJsonArray();
		
		//check if any of address component (contains state, region, street name, city, etc) is locality. 
		for (int i = 0; i < addressComponents.size(); i++) {
			//locality is contained in "types" for each address component
			
			JsonArray types = addressComponents.get(i).getAsJsonObject().get("types").getAsJsonArray();
			
			for (int j = 0; j < types.size(); j++) {
				if (types.get(j).getAsString().equals("locality")) {
					storeCityInfo(resObj);
					return true;
				}
			}
		}
		
		cityBounds = null;
		return false;
	}
	
	public void storeCityInfo(JsonObject results) {
		JsonObject geometryObj = results.get("results").getAsJsonArray().get(0).getAsJsonObject().get("geometry").getAsJsonObject();
		JsonObject boundsObj = geometryObj.get("bounds").getAsJsonObject();
		
		//if city is not inputted then "bounds" para may not exist
		if (boundsObj == null) {
			cityBounds = null;
			return;
		}
		
		JsonObject northEast = boundsObj.get("northeast").getAsJsonObject();
		JsonObject southWest = boundsObj.get("southwest").getAsJsonObject();
		
		cityBounds = new Double[2][2];
		cityBounds[0][0] = northEast.get("lat").getAsDouble();
		cityBounds[0][1] = northEast.get("lng").getAsDouble();
		cityBounds[1][0] = southWest.get("lat").getAsDouble();
		cityBounds[1][1] = southWest.get("lng").getAsDouble();
		
		
		cityPos = new Double[2];
		cityPos[0] = geometryObj.get("location").getAsJsonObject().get("lat").getAsDouble();
		cityPos[1] = geometryObj.get("location").getAsJsonObject().get("lng").getAsDouble();
		
	}
	
	public ArrayList<String> getRestaurantsInCity() {
		//Convert spaces to dashes for API URL.
		city = city.replace(" ", "-");
		boolean first = true;
		
		ArrayList<String> restaurants = new ArrayList<>();
		String nextPageToken = addRestaurants(null, restaurants);
		
		
		while (nextPageToken != null) {
			nextPageToken = addRestaurants(nextPageToken, restaurants);
		}
		
		return restaurants;
		
	}
	
	public String addRestaurants(String nextPageToken, ArrayList<String> restaurants) {
		String reqURL = PLACES_REQ_BASE + "location=" + cityPos[0] + "," + cityPos[1] + "&radius=" + RADIUS + "&type=restaurant" +  "&key=" + API_KEY;
		if (nextPageToken != null) {
			reqURL += "&pagetoken=" + nextPageToken + "&key=" + API_KEY;
		} 
		
		try {
			//
			Thread.sleep(2000);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
		
		URLGetter url = new URLGetter(reqURL);
		ArrayList<String> res = url.getContents();
		JsonObject resObj = convertToJsonObject(res);
	
		//Add restaurants
		JsonArray restaurantsObj = resObj.get("results").getAsJsonArray();
		
		for (int i = 0; i < restaurantsObj.size(); i++) {
			String restaurantName = restaurantsObj.get(i).getAsJsonObject().get("name").getAsString();
			restaurants.add(restaurantName);
			String location = restaurantsObj.get(i).getAsJsonObject().get("vicinity").getAsString();
			restToAddress.put(restaurantName, location);
		}
		
		if (resObj.has("next_page_token")) {
			return resObj.get("next_page_token").getAsString();
		} else {
			return null;
		}
		
	}
	
	//topRestaurants should only have 10 restaurants
	//Entries on diagonals are null since those represent dist of rest A to rest A
	public Double[][] graphRestaurants(ArrayList<String> topRestaurants) {
		
		Double[][] adjMatrix = new Double[topRestaurants.size()][topRestaurants.size()];
		
		for (int i = 0; i < topRestaurants.size(); i++) {
			for (int j = i + 1; j < topRestaurants.size(); j++) {
				
				String origRestaurantLoc = restToAddress.get(topRestaurants.get(i)).replace(" ", "%20");
				String destRestaurantLoc = restToAddress.get(topRestaurants.get(j)).replace(" ", "%20");
				
				URLGetter url = new URLGetter(DISTANCE_REQ_BASE + "destinations=" + destRestaurantLoc + "&origins=" + origRestaurantLoc +  "&key=" + API_KEY);
				ArrayList<String> res = url.getContents();
				JsonObject resObj = convertToJsonObject(res);
						
				JsonObject distObj = resObj.get("rows").getAsJsonArray().get(0).getAsJsonObject().get("elements").getAsJsonArray().get(0).getAsJsonObject().get("distance").getAsJsonObject();
				Double dist = Double.parseDouble(distObj.get("text").getAsString().split(" ")[0]);
				
				adjMatrix[i][j] = dist;
				adjMatrix[j][i] = dist;
				
			}
		}
		
		return adjMatrix;
		
	}
	
	//Convert result in String array format to Json Object
	public JsonObject convertToJsonObject(ArrayList<String> result) {
		
			String singleResult = "";
			
			for (String content : result) {
				singleResult += content;
			}
		
	        JsonStreamParser parser = new JsonStreamParser(singleResult);

	        JsonElement element = null;
	        while (parser.hasNext()) {
	            element = parser.next();
	        }

	        return element.getAsJsonObject();
	}
	

}
