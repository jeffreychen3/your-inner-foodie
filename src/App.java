import java.util.*;

public class App {

    public static void main(String[] args) {




		Scanner scanner = new Scanner(System.in);  // Create a Scanner object
		System.out.println("Enter a city name you want restaurant recommendations for");
		String inputCity = scanner.nextLine();
		CityInfo cityInfo = new CityInfo(inputCity);
		if (!cityInfo.isValidCity() ) {
			System.out.println("Invalid city name, try again!");
		}
		System.out.println("Getting restaurant information in " + inputCity + " ...");
		ArrayList<String> restaurants = cityInfo.getRestaurantsInCity();
		String [] restaurantsArray = new String[restaurants.size()];
		restaurants.toArray(restaurantsArray);
		SearchTweets searchTweets = new SearchTweets();
		System.out.println("Fetching tweets about restaurants in " + inputCity + " ...");
		ArrayList<String> tweets= searchTweets.GetSearchedTweets(inputCity, restaurantsArray);
		Map<String, ArrayList<String>> restaurantMap = searchTweets.popularTweets(tweets, restaurantsArray);
		Map<String, List<String>> cleanedRestaurantMap = new HashMap<>();
		for (String restaurant: restaurantMap.keySet()) {
			cleanedRestaurantMap.put(restaurant, (List) restaurantMap.get(restaurant));
		}
		System.out.println("Performing sentimnet analysis on tweets ...");
		NlpProcessing nlp = new NlpProcessing(cleanedRestaurantMap);
		ArrayList<String> topTenRestaurants = nlp.getTopTen();
		Double[][] restaurantGraph = cityInfo.graphRestaurants(topTenRestaurants);
		System.out.println("Finding shortest path to visit all top restaurants ...");
		PrimAlgorithmAdjacencyMatrix.Graph graph = new PrimAlgorithmAdjacencyMatrix.Graph(10, topTenRestaurants);
		graph.addMatrix(restaurantGraph);
		graph.primMST();





    }
}
