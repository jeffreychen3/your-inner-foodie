import java.util.ArrayList;
import java.util.List;

public class Test {
    public static void main (String[] args) {
        System.out.println();
        CityInfo philly = new CityInfo("Philadelphia");

        System.out.println("Philadelphia a city? " + philly.isValidCity());
        System.out.println();
        System.out.println("Philadelphia bounds are:? ");


        for (Double[] corner : philly.getCityBounds()) {
            System.out.println();
            System.out.println("lat: " + corner[0] + ", lng: " + corner[1]);
        }

        System.out.println();
        System.out.println("Philadelphia location: ");
        System.out.println("lat: " + philly.getCityPos()[0] + ", lng: " + philly.getCityPos()[1]);

        System.out.println();
        System.out.println("Restaurants in Philadelphia? ");

        //Takes 6s
        ArrayList<String> restaurants = philly.getRestaurantsInCity();

        for (String restaurant : restaurants) {
            System.out.println(restaurant);
        }

        System.out.println(restaurants.size());
        ArrayList<String> topRestaurants = new ArrayList<>(List.of("IHOP", "Sumo Sushi", "Oyster House"));

        System.out.println("Mapping (Name to Location): ");
        Double[][] adjMatrix = philly.graphRestaurants(topRestaurants);
        for (int i = 0; i < adjMatrix.length; i++) {
            System.out.println();
            for (int j = 0; j < adjMatrix.length; j++) {
                System.out.print(adjMatrix[i][j] + ", ");
            }
        }


        PrimAlgorithmAdjacencyMatrix.Graph graph = new PrimAlgorithmAdjacencyMatrix.Graph(3, topRestaurants);
        graph.addMatrix(adjMatrix);
        graph.primMST();
    }
}
