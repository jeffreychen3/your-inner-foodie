import java.util.*;

public class App {

    public static void main(String[] args) {



		/*
		Scanner scanner = new Scanner(System.in);  // Create a Scanner object
		System.out.println("Enter a city name you want restaurant recommendations for");
		String inputCity = scanner.nextLine();
		CityInfo cityInfo = new CityInfo(inputCity);
		if (!cityInfo.isValidCity() ) {
			System.out.println("Invalid city name, try again!");
		}
		ArrayList<String> restaurants = cityInfo.getRestaurantsInCity();
		String [] restaurantsArray = new String[restaurants.size()];
		restaurants.toArray(restaurantsArray);
		SearchTweets searchTweets = new SearchTweets();
		ArrayList<String> tweets= searchTweets.GetSearchedTweets(inputCity, restaurantsArray);
		Map<String, ArrayList<String>> restaurantMap = searchTweets.popularTweets(tweets, restaurantsArray);
		Map<String, List<String>> cleanedRestaurantMap = new HashMap<>();
		for (String restaurant: restaurantMap.keySet()) {
			cleanedRestaurantMap.put(restaurant, (List) restaurantMap.get(restaurant));
		}
		NlpProcessing nlp = new NlpProcessing(cleanedRestaurantMap);
		ArrayList<String> topTenRestaurants = nlp.getTopTen();
		Double[][] restaurantGraph = cityInfo.graphRestaurants(topTenRestaurants);
		PrimAlgorithmAdjacencyMatrix.Graph graph = new PrimAlgorithmAdjacencyMatrix.Graph(10);
		graph.addMatrix(restaurantGraph);
		graph.primMST();

		 */


		Double[][] restaurantGraph = new Double[10][10];
		for (int i=0; i<10; i++) {
			for (int j=0; j<10; j++) {
				restaurantGraph[i][j] = Double.valueOf(i*j);
			}
		}
		PrimAlgorithmAdjacencyMatrix.Graph graph = new PrimAlgorithmAdjacencyMatrix.Graph(10);
		graph.addMatrix(restaurantGraph);
		graph.primMST();










        
        


//        //City Info Test:
//        System.out.println();
//        CityInfo philly = new CityInfo("Philadelphia");
//
//        System.out.println("Philadelphia a city? " + philly.isValidCity());
//		System.out.println();
//		System.out.println("Philadelphia bounds are:? ");
//
//
//		for (Double[] corner : philly.getCityBounds()) {
//			System.out.println();
//			System.out.println("lat: " + corner[0] + ", lng: " + corner[1]);
//		}
//
//		System.out.println();
//		System.out.println("Philadelphia location: ");
//		System.out.println("lat: " + philly.getCityPos()[0] + ", lng: " + philly.getCityPos()[1]);
//
//		System.out.println();
//		System.out.println("Restaurants in Philadelphia? ");
//
//		//Takes 6s
//		ArrayList<String> restaurants = philly.getRestaurantsInCity();
//
//		for (String restaurant : restaurants) {
//			System.out.println(restaurant);
//		}
//
//		System.out.println(restaurants.size());
//		ArrayList<String> topRestaurants = new ArrayList<>(List.of("IHOP", "Sumo Sushi", "Oyster House"));
//
//		System.out.println("Mapping (Name to Location): ");
//		Double[][] adjMatrix = philly.graphRestaurants(topRestaurants);
//		for (int i = 0; i < adjMatrix.length; i++) {
//			System.out.println();
//			for (int j = 0; j < adjMatrix.length; j++) {
//				System.out.print(adjMatrix[i][j] + ", ");
//			}
//		}

    }
}
