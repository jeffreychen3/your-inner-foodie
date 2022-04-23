import java.util.ArrayList;
import java.util.Scanner;

public class AnalyzeData {
	
	public static void main(String[] args) {
		
		Parser parser = new Parser("https://www.cia.gov/the-world-factbook/", "https://www.cia.gov/");
		
		System.out.println("Enter command (format: [question #] [args], ex: 1 red green). Type E to exit.");
		Scanner scanner = new Scanner(System.in);
		
		String inputString = scanner.nextLine();
		String[] inputs = inputString.split(" ");
		
		while (inputs.length > 0 && !inputs[0].equals("E")) {
			if (inputs.length <= 1) {
				System.out.println("Invalid command (Args/Question # not provided)");
				return;
			}
			
			System.out.println();
			System.out.println("Finding answer...");
			System.out.println();
			
			switch(inputs[0]) {
			
				case "1":
					if (inputs.length < 3) {
						System.out.println("Invalid args");
						return;
					}
					ArrayList<String> countriesWithFlag = parser.allFlagsColored(inputs[1], inputs[2]);
					for (String country : countriesWithFlag) {
						System.out.println(country);
					}
					break;
				case "2":
					if (inputs.length < 2) {
						System.out.println("Invalid args");
						return;
					}
					
					//Support more than one word places.
					String place = inputString.substring(inputString.indexOf(" ") + 1);
					
					String lowestPoint = parser.lowestPoint(place);
					if (lowestPoint == null) {
						System.out.println("Lowest Point cannot be found.");
					} else {
						System.out.println(lowestPoint);
					}
					
					break;
				case "3":
					if (inputs.length < 2) {
						System.out.println("Invalid args");
						return;
					}
					
					//Support more than one word regions.
					String region = inputString.substring(inputString.indexOf(" ") + 1);
					
					String largestCountry = parser.largestCountryEleProduct(region);
					
					if (largestCountry == null) {
						System.out.println("Region does not exist/largest country cannot be found");
					} else {
						System.out.println(largestCountry);
					}
					
					break;
				case "4":
					if (inputs.length < 2) {
						System.out.println("Invalid args");
						return;
					}
					
					//Support more than one word regions.
					String region2 = inputString.substring(inputString.indexOf(" ") + 1);
					
					String largestCountry2 = parser.largestCountryRatio(region2);
					
					if (largestCountry2 == null) {
						System.out.println("Region does not exist/largest country cannot be found");
					} else {
						System.out.println(largestCountry2);
					}
					break;
				case "5":
					if (inputs.length < 2) {
						System.out.println("Invalid args");
						return;
					}
					
					//Support more than one word regions.
					String region3 = inputString.substring(inputString.indexOf(" ") + 1);
					
					String popOfCountryHighestEle = parser.popOfCountryHighestEle(region3);
					
					if (popOfCountryHighestEle == null) {
						System.out.println("Region does not exist/population cannot be found");
					} else {
						System.out.println(popOfCountryHighestEle);
					}
					break;
				
				case "6":
					if (inputs.length < 2) {
						System.out.println("Invalid args");
						return;
					}
					
					//Support more than one word regions.
					String region4 = inputString.substring(inputString.indexOf(" ") + 1);
					
					String partners = parser.importPartnersForThirdIsland(region4);
					
					if (partners == null) {
						System.out.println("Region does not exist/there are less than 3 country in that region");
					} else {
						System.out.println(partners);
					}
					
					
					break;
				case "7":
					if (inputs.length < 2) {
						System.out.println("Invalid args");
						return;
					}
					
					ArrayList<String> countries = parser.allCountriesWithStartingLetter(inputs[1]);
					
					if (countries == null || countries.size() == 0) {
						System.out.println("No countries starting with that letter");
					}
					
					for (String country : countries) {
						System.out.println(country);
					}
					break;
				case "8":
					//QUESTION :Find the country in [region] with the highest median age
					if (inputs.length < 2) {
						System.out.println("Invalid args");
						return;
					}
					
					//Support more than one word regions.
					String region5 = inputString.substring(inputString.indexOf(" ") + 1);
					
					String countryWithHighestAge = parser.countryWithHighestAge(region5);
					
					if (countryWithHighestAge == null) {
						System.out.println("Region does not exist/median age cannot be found");
					} else {
						System.out.println(countryWithHighestAge);
					}
					break;
				default: 
					System.out.println("Please select a question # from 1-8");
					break;	
			}
			
			System.out.println();
			System.out.println("Enter command (format: [question #] [args], ex: 1 red green). Type E to exit.");
			inputString = scanner.nextLine();
			inputs = inputString.split(" ");
			
		}
		
		System.out.println("Program exited");
		
	}

}