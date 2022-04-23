import java.io.IOException;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.PriorityQueue;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.nodes.Node;
import org.jsoup.select.Elements;

public class Parser {
	
	String baseURL;
    String defaultURL;
    Document currentDoc;
    HashMap<String, String> countriesToURL;
	
	static final String allCountries = "field/map-references/";
	
	public Parser(String baseURL, String defaultURL) {
		this.baseURL = baseURL;
		this.defaultURL = defaultURL;
		countriesToURL = mapCountriesToURL();
		
		try {
			currentDoc = Jsoup.connect(baseURL).get();
		} catch (IOException e) {
			
		}
		
	}
	
	public HashMap<String, String> mapCountriesToURL() {
		//Map all places to its specific page URL
		HashMap<String, String> map = new HashMap<>();
		
		
		try {
			currentDoc = Jsoup.connect(baseURL + allCountries).get();
		} catch (IOException e) {
		}
		
		Elements liElements = currentDoc.select("section").select("li");
		
		for (Element li : liElements) {
			Elements aTag = li.select("a");
			Element a = aTag.get(0);
			
			String countryURL = a.attr("href");
			String countryName = a.text();
			
			map.put(countryName, countryURL);
		}
		
		return map;
		
	}
	
	//Q1
	public ArrayList<String> allFlagsColored(String color1, String color2) {

		ArrayList<String> countries = new ArrayList<>();
		for (String country : countriesToURL.keySet()) {
			try {
				currentDoc = Jsoup.connect(defaultURL + countriesToURL.get(country)).get();
				Elements aTags = currentDoc.select("a");
				for (Element aTag : aTags) {
					
					if (aTag.text().contains("View Details")) {
						try {
							currentDoc = Jsoup.connect(defaultURL + aTag.attr("href").substring(1)).get();
							Element desc = currentDoc.getElementsByClass("image-detail-block-caption").first();
							if ((desc.text().contains(color1 + " ") || desc.text().contains(" " + color1)) && (desc.text().contains(" " + color2) || desc.text().contains(color2 + " "))) {		
								countries.add(country);
							}
							
						} catch (IOException e) {
							
						}
					}
				}
			} catch (IOException e) {
				
			}
		}
		return countries;
		
	}
	
	//Q2
	public String lowestPoint(String place) {
		if (!countriesToURL.containsKey(place)) {
			return null;
		}
		
		try {
			currentDoc = Jsoup.connect(defaultURL + countriesToURL.get(place)).get();
			Element lowestPointText = currentDoc.select("strong:contains(lowest point)").first();
			Node afterText = lowestPointText.nextSibling();
			return afterText.toString();
		} catch (IOException e) {
			return null;
		}
		
	}
	
	//Q3
	public String largestCountryEleProduct(String region) {
		HashMap<String, String> countriesEleProduct = new HashMap<>();
		
		try {
			currentDoc = Jsoup.connect(baseURL).get();
			Element regionATag = currentDoc.select("a:contains(" + region + ")").first();
			
			if (regionATag == null) {
				return null;
			}
			
			//Get region specific page
			try {
				currentDoc = Jsoup.connect(defaultURL + regionATag.attr("href").substring(1)).get();
				Elements countriesATags = currentDoc.getElementsByClass("link-button");
				
				for (Element countryATag : countriesATags) {
					//For every country of a region, go to gets page and get Electricity Production.
					try {
						String country = countryATag.text();
						currentDoc = Jsoup.connect(defaultURL + countriesToURL.get(country)).get();
						try {
							Element EleProductText = currentDoc.select("a:contains(Electricity - production)").first();
							Element pTextContainInfo = EleProductText.parent().parent().selectFirst("p");
							countriesEleProduct.put(country, pTextContainInfo.text());
						} catch (Exception e) {
							
						}
					} catch (IOException e) {
						
					}
				}
				
				//Now, we have all country of that region and its Electricity Production
				
				//Use priority queue to find largest 
				//custom comparator to compare Electricity Production
				PriorityQueue<String> queue = new PriorityQueue<>(new Comparator<String>() {
				    public int compare(String a, String b) {
				    	
				    	//we want the number and whether its milion or billion
				        String[] textA = countriesEleProduct.get(a).split(" ");
				        String[] textB = countriesEleProduct.get(b).split(" ");
				        
				       
				        double eleNumberA = convertPlace(textA[1], Double.parseDouble(textA[0].replace(",", "")));
				        double eleNumberB = convertPlace(textB[1], Double.parseDouble(textB[0].replace(",", "")));
				        	
				        if (eleNumberA < eleNumberB) {
				        	return 1;
				        } else if (eleNumberA > eleNumberB) {
				        	return -1;
				        } else {
				        	return 0;
				        }
				        
				        
				    }
				});
				
				//Add all countries to queue, sorted by its Electricity Production
				for (String country : countriesEleProduct.keySet()) {
					queue.add(country);
				}
				
				//Return country with largest Electricity Production
				if (queue.size() == 0) {
					return null;
				}
				
				return queue.peek();
				
			} catch (IOException e) {
				
			}
		} catch (IOException e) {
			
		}
		
		return null;
	}
	
	public double convertPlace(String place, double num) {
		switch (place) {
			case "trillion":
				return num * 10e12;
			case "billion":
				return num * 10e9;
			case "million":
				return num * 10e6;
			case "thousand":
				return num * 10e3;
			default:
				return num;
		}
	}
	
	//Q4
	public String largestCountryRatio(String region) {
		HashMap<String, Double> countriesRatio = new HashMap<>();
		
		try {
			currentDoc = Jsoup.connect(baseURL).get();
			Element regionATag = currentDoc.select("a:contains(" + region + ")").first();
			
			if (regionATag == null) {
				return null;
			}
			
			//Get region specific page
			try {
				currentDoc = Jsoup.connect(defaultURL + regionATag.attr("href").substring(1)).get();
				Elements countriesATags = currentDoc.getElementsByClass("link-button");
				
				for (Element countryATag : countriesATags) {
					//For every country of a region, go to gets page and get Electricity Production.
					try {
						String country = countryATag.text();
						currentDoc = Jsoup.connect(defaultURL + countriesToURL.get(country)).get();
						Element coastLineText = currentDoc.select("a:contains(Coastline)").first();
						Element pTextContainInfo = coastLineText.parent().parent().selectFirst("p");
						
						double coastLineNum;
						String regex = ".*[a-zA-Z].*";
						Pattern pattern = Pattern.compile(regex); 
						Matcher matchText = pattern.matcher(pTextContainInfo.text());
						
						if (matchText.matches()) {
							coastLineNum = 0.0;
						} else {
							coastLineNum = Double.parseDouble(pTextContainInfo.text().split(" ")[0].replace(",", ""));
						}
						
						Element landText = currentDoc.select("strong:contains(land)").first();
						
						//If land text doesn't exist, get total
						if (landText == null) {
							 landText = currentDoc.select("strong:contains(total)").first();
						}
						Node landNumText = landText.nextSibling();
						
						double landAreaNum;
						
						try {
							landAreaNum = Double.parseDouble(landNumText.toString().split(" ")[0].replace(",", ""));
							countriesRatio.put(country, coastLineNum / landAreaNum );
						} catch (Exception e) {
							countriesRatio.put(country, 0.0 );
						}
						
					} catch (Exception e) {
						
					}
				}
				
				//Now, we have all country of that region and its coastline to land area ratio
				
				//Use priority queue to find largest 
				//custom comparator to compare coastline to land area ratio
				PriorityQueue<String> queue = new PriorityQueue<>(new Comparator<String>() {
				    public int compare(String a, String b) {

				        double eleNumberA = countriesRatio.get(a);
				        double eleNumberB = countriesRatio.get(b);
				        	
				        if (eleNumberA < eleNumberB) {
				        	return 1;
				        } else if (eleNumberA > eleNumberB) {
				        	return -1;
				        } else {
				        	return 0;
				        }
				        
				    }
				});
				
				//Add all countries to queue, sorted by its Electricity Production
				for (String country : countriesRatio.keySet()) {
					queue.add(country);
				}
				
				//Return country with largest Electricity Production
				if (queue.size() == 0) {
					return null;
				}
				
				return queue.peek();
				
			} catch (Exception e) {
				
			}
		} catch (IOException e) {
			
		}
		
		return null;
	}
	
	//Q5
	public String popOfCountryHighestEle(String region) {
		HashMap<String, Integer> countriesMeanEle = new HashMap<>();
		
		try {
			currentDoc = Jsoup.connect(baseURL).get();
			Element regionATag = currentDoc.select("a:contains(" + region + ")").first();
			
			if (regionATag == null) {
				return null;
			}

			//Get region specific page
			try {
				currentDoc = Jsoup.connect(defaultURL + regionATag.attr("href").substring(1)).get();
				Elements countriesATags = currentDoc.getElementsByClass("link-button");
				
				for (Element countryATag : countriesATags) {
					//For every country of a region, go to gets page and get mean elevation
					try {
						String country = countryATag.text();
						currentDoc = Jsoup.connect(defaultURL + countriesToURL.get(country)).get();
						Element meanEleText = currentDoc.select("strong:contains(mean elevation)").first();
						
						if (meanEleText == null) {
							countriesMeanEle.put(country, 0);
						} else {
							Node afterText = meanEleText.nextSibling();
							int meanElevation = Integer.parseInt(afterText.toString().split(" ")[0].replaceAll(",", ""));
							countriesMeanEle.put(country, meanElevation);
						}
						
					} catch (IOException e) {
						
					}
				}
				
				//Now, we have all country of that region and its Mean Elevation 
				
				//Use priority queue to find largest 
				//custom comparator to compare Mean Elevation 
				PriorityQueue<String> queue = new PriorityQueue<>(new Comparator<String>() {
				    public int compare(String a, String b) {
				    	int meanEleA = countriesMeanEle.get(a);
				    	int meanEleB = countriesMeanEle.get(b);
				        	if (meanEleA < meanEleB) {
				        		return 1;
				        	} else if (meanEleA > meanEleB) {
				        		return -1;
				        	} else {
				        		return 0;
				        	}
				    }
				});
				
				//Add all countries to queue, sorted by its Mean Elevation 
				for (String country : countriesMeanEle.keySet()) {
					queue.add(country);
				}
				
				//Return country with largest Mean Elevation 
				if (queue.size() == 0) {
					return null;
				}
				
				try {
					
					currentDoc = Jsoup.connect(defaultURL + countriesToURL.get(queue.peek())).get();
					Element locationText = currentDoc.select("a:contains(Population)").get(1);
					Element locationTextSecond =  currentDoc.select("a:contains(Population)").get(2);
					
					Element pTextContainInfo = locationText.parent().parent().selectFirst("p");
					Element pTextContainInfoSecond = locationTextSecond.parent().parent().selectFirst("p");
					
					//Get population
					if (pTextContainInfoSecond != null && pTextContainInfo.text().length() > pTextContainInfoSecond.text().length()) {
						return pTextContainInfoSecond.text();
					} else {
						return pTextContainInfo.text();
					}
					

				} catch (IOException e) {
					
				}
				
				
				
			} catch (IOException e) {
				
			}
		} catch (IOException e) {
			
		}
		
		return null;
	}
	
	//Q6
		public String importPartnersForThirdIsland(String region) {
			
			HashMap<String, Double> countriesToArea = new HashMap<>();
			
			//Search through all countries and see if they are in that region and an island
			for (String country : countriesToURL.keySet()) {
				try {
					
					if (countriesToURL.get(country).contains("/world")) {
						break;
					}
					
					currentDoc = Jsoup.connect(defaultURL + countriesToURL.get(country)).get();
					
					Elements locationTexts = currentDoc.select("a:contains(Location)");
					
					if (locationTexts != null) {
						Element firstLocationText = locationTexts.first();
						Element pTextContainInfo = firstLocationText.parent().parent().selectFirst("p");
						
						if (pTextContainInfo.toString().contains(region) && pTextContainInfo.toString().contains("island")) {
							Element landText = currentDoc.select("strong:contains(total)").first();
							Node landNumText = landText.nextSibling();
							
							double landAreaNum = Double.parseDouble(landNumText.toString().split(" ")[0].replace(",", ""));
							countriesToArea.put(country, landAreaNum);
						}
					}
					
					
					
				} catch (IOException e) {
					
				}
			}
			
			//Get third largest Island in region
			
			//Queue to sort island by largest area
			PriorityQueue<String> queue = new PriorityQueue<>(new Comparator<String>() {
			    public int compare(String a, String b) {
			    	double areaA = countriesToArea.get(a);
			    	double areaB = countriesToArea.get(b);
			        	if (areaA < areaB) {
			        		return 1;
			        	} else if (areaA > areaB) {
			        		return -1;
			        	} else {
			        		return 0;
			        	}
			    }
			});
			
			//Add all countries to queue, sorted by its total land area
			for (String country : countriesToArea.keySet()) {
				queue.add(country);
			}
			
			//See if there is at least 3 islands
			if (queue.size() < 3) {
				return null;
			}
			
			queue.poll();
			queue.poll();
			
			try {
				
				currentDoc = Jsoup.connect(defaultURL + countriesToURL.get(queue.peek())).get();
				Element locationText = currentDoc.select("a:contains(Imports - partners)").first();
				Element pTextContainInfo = locationText.parent().parent().selectFirst("p");
				
				//Get all partners
				return pTextContainInfo.text();

			} catch (IOException e) {
				
			}
			
			
			return null;
			
		}
		
		//Q7
		public ArrayList<String> allCountriesWithStartingLetter(String letter) {
			HashMap<String, Double> countriesToArea = new HashMap<>();
			ArrayList<String> countries = new ArrayList<>();
			
			//Search through all countries and get all country starting with letter
			for (String country : countriesToURL.keySet()) {
				//First letter of country name is letter
				if (country.indexOf(letter) == 0 && countriesToURL.get(country).contains("countries")) {
					
					try {
						currentDoc = Jsoup.connect(defaultURL + countriesToURL.get(country)).get();
						Element landText = currentDoc.select("strong:contains(total)").first();
						Node landNumText = landText.nextSibling();
						
						double landAreaNum = Double.parseDouble(landNumText.toString().split(" ")[0].replace(",", ""));
						countriesToArea.put(country, landAreaNum);
					} catch (Exception e) {
						
					}
				}
				
			}
			
			
			//Queue to sort island by increasing total area
			PriorityQueue<String> queue = new PriorityQueue<>(new Comparator<String>() {
			    public int compare(String a, String b) {
			    	double areaA = countriesToArea.get(a);
			    	double areaB = countriesToArea.get(b);
			        	if (areaA < areaB) {
			        		return -1;
			        	} else if (areaA > areaB) {
			        		return 1;
			        	} else {
			        		return 0;
			        	}
			    }
			});
			
			//Add all countries to queue, sorted by its total area
			for (String country : countriesToArea.keySet()) {
				queue.add(country);
			}
			
			while (!queue.isEmpty()) {
				countries.add(queue.poll());
			}
			
			
			return countries;
		}
		
		//Q8
		public String countryWithHighestAge(String region) {
			HashMap<String, Double> countriesMedianAge = new HashMap<>();
			
			try {
				currentDoc = Jsoup.connect(baseURL).get();
				Element regionATag = currentDoc.select("a:contains(" + region + ")").first();
				
				if (regionATag == null) {
					return null;
				}
				
				//Get region specific page
				try {
					currentDoc = Jsoup.connect(defaultURL + regionATag.attr("href").substring(1)).get();
					Elements countriesATags = currentDoc.getElementsByClass("link-button");
					
					for (Element countryATag : countriesATags) {
						//For every country of a region, go to gets page and get Median Page
						try {
							String country = countryATag.text();
							currentDoc = Jsoup.connect(defaultURL + countriesToURL.get(country)).get();
							Element EleProductText = currentDoc.select("a:contains(Median age)").first();
							Element pTextContainInfo = EleProductText.parent().parent().selectFirst("p");
							Element totalText = pTextContainInfo.select("strong:contains(total)").first();
							Node medianAgeTotal = totalText.nextSibling();
							
							countriesMedianAge.put(country, Double.parseDouble(medianAgeTotal.toString().split(" ")[0]));
						} catch (IOException e) {
							
						}
					}
				
					
					//Use priority queue to find country with largest median age
					PriorityQueue<String> queue = new PriorityQueue<>(new Comparator<String>() {
					    public int compare(String a, String b) {
					    	
					        double medianAgeCountryA = countriesMedianAge.get(a);
					        double medianAgeCountryB = countriesMedianAge.get(b);
					        	
					        if (medianAgeCountryA < medianAgeCountryB) {
					        	return 1;
					        } else if (medianAgeCountryA > medianAgeCountryB) {
					        	return -1;
					        } else {
					        	return 0;
					        }
					       
					    }
					});
					
					//Add all countries to queue, sorted by its median age, in decreasing order
					for (String country : countriesMedianAge.keySet()) {
						queue.add(country);
					}
					
					//Return country with largest median age
					if (queue.size() == 0) {
						return null;
					}
					
					return queue.peek();
					
				} catch (Exception e) {
					
				}
			} catch (IOException e) {
				
			}
			
			return null;
		}

}
