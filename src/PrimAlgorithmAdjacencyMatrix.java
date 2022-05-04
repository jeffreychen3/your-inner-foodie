import java.util.ArrayList;

/* 
*   Input: A[i][j] represent distance from restaurant i to restaurant j
*	and A[i][j] = A[j][i]
*	and A[i][i] = null since it doesn’t make sense to go from restaurant i to restaurant i
*	size: 10x10 2D Array
*
*	Prim Algorithim adapted from Sumit Jain from Tutorial Horizon
*/
public class PrimAlgorithmAdjacencyMatrix {

	static class Graph{

		int vertices;
		Double matrix[][];
		ArrayList<String> restaurants;

		public Graph(int vertex, ArrayList<String> restaurantNames) {
			this.restaurants = restaurantNames;
			this.vertices = vertex;
			matrix = new Double[vertex][vertex];
		}

		public void addEdge(int source, int destination, double weight) {
			//add edge
			matrix[source][destination] = weight;

			//add back edge for undirected graph
			matrix[destination][source] = weight;
		}

		public void addMatrix (Double[][] matrix) {
			this.matrix = matrix;
		}

		//get the vertex with minimum key which is not included in MST
		int getMinimumVertex(boolean [] mst, Double [] key){
			Double minKey = Double.MAX_VALUE;
			int vertex = -1;

			for (int i = 0; i <vertices ; i++) {
				if(mst[i]==false && minKey>key[i]){
					minKey = key[i];
					vertex = i;
				}
			}
			return vertex;
		}

		class ResultSet{
			// will store the vertex(parent) from which the current vertex will reached
			int parent;
			// will store the weight for printing the MST weight
			Double weight;
		}

		public void primMST(){
			boolean[] mst = new boolean[vertices];
			ResultSet[] resultSet = new ResultSet[vertices];
			Double[] key = new Double[vertices];

			//Initialize all the keys to infinity and
			//initialize resultSet for all the vertices
			for (int i = 0; i <vertices ; i++) {
				key[i] = Double.MAX_VALUE;
				resultSet[i] = new ResultSet();
			}

			//start from the vertex 0
			key[0] = 0.0;
			resultSet[0] = new ResultSet();
			resultSet[0].parent = -1;

			//create MST
			for (int i = 0; i <vertices ; i++) {

				//get the vertex with the minimum key
				int vertex = getMinimumVertex(mst, key);

				//include this vertex in MST
				mst[vertex] = true;

				//iterate through all the adjacent vertices of above vertex and update the keys
				for (int j = 0; j <vertices ; j++) {
					if (j==vertex) {
						continue;
					}

					//check of the edge
					if(matrix[vertex][j]>0){

					//check if this vertex 'j' already in mst and
					//if no then check if key needs an update or not
						if(mst[j]==false && matrix[vertex][j]<key[j]){
						//update the key
							key[j] = matrix[vertex][j];
							//update the result set
							resultSet[j].parent = vertex;
							resultSet[j].weight = key[j];
						}
					}
				}
			}
			
			//print mst
			printMST(resultSet);

		}

		public void printMST(ResultSet[] resultSet) {
			Double total_min_weight = 0.0;
			System.out.println("Fastest way to visit top restaurant: ");
			for (int i = 1; i <vertices ; i++) {
				System.out.print(restaurants.get(i) + "- (" + resultSet[i].weight + ") - ");
				total_min_weight += resultSet[i].weight;
			}
			
			System.out.println(restaurants.get(resultSet[vertices - 1].parent));
			System.out.printf("Total distance to visit all top restaurants is : %.3f\n", total_min_weight);
			
		}
	}

}