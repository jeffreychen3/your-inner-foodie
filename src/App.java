import java.util.*;

public class App {

    public static void main(String[] args) {

		//Create Scanner object
		Scanner scanner = new Scanner(System.in);
		
		//Prompt user to enter city name
		System.out.println("Enter a city name you want restaurant recommendations for");
		String inputCity = scanner.nextLine();
		CityInfo cityInfo = new CityInfo(inputCity);

		//Check if valid city
		if (!cityInfo.isValidCity() ) {
			System.out.println("Invalid city name, try again!");
			return;
		}

		//Get restaurants in city
		System.out.println("Getting restaurant information in " + inputCity + " ...");
		ArrayList<String> restaurants = cityInfo.getRestaurantsInCity();
		String [] restaurantsArray = new String[restaurants.size()];
		restaurants.toArray(restaurantsArray);

		//Get tweets about each restaurant
		SearchTweets searchTweets = new SearchTweets();
		System.out.println("Fetching tweets about restaurants in " + inputCity + " ...");
		ArrayList<String> tweets= searchTweets.GetSearchedTweets(inputCity, restaurantsArray);

		//Get most mentioned restaurants
		Map<String, ArrayList<String>> restaurantMap = searchTweets.popularTweets(tweets, restaurantsArray);
		Map<String, List<String>> cleanedRestaurantMap = new HashMap<>();
		for (String restaurant: restaurantMap.keySet()) {
			cleanedRestaurantMap.put(restaurant, (List) restaurantMap.get(restaurant));
		}

		//Perform NLP
		System.out.println("Performing sentimnet analysis on tweets ...");
		NlpProcessing nlp = new NlpProcessing(cleanedRestaurantMap);
		ArrayList<String> topTenRestaurants = nlp.getTopTen();

		//Create MST and find shortest path to visit all top 10 restaurants squentially
		Double[][] restaurantGraph = cityInfo.graphRestaurants(topTenRestaurants);
		System.out.println("Finding shortest path to visit all top restaurants ...");
		PrimAlgorithmAdjacencyMatrix.Graph graph = new PrimAlgorithmAdjacencyMatrix.Graph(topTenRestaurants.size(), topTenRestaurants);
		graph.addMatrix(restaurantGraph);
		graph.primMST();

    }
}
