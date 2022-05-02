import com.twitter.clientlib.TwitterCredentialsBearer;
import com.twitter.clientlib.auth.TwitterOAuth20AppOnlyApi;
import twitter4j.*;
import twitter4j.conf.ConfigurationBuilder;

import java.time.OffsetDateTime;
import java.util.*;

import com.twitter.clientlib.TwitterCredentialsOAuth2;
import com.twitter.clientlib.ApiException;
import com.twitter.clientlib.api.TwitterApi;
import com.twitter.clientlib.model.*;
import com.twitter.clientlib.Configuration;
import com.twitter.clientlib.auth.*;
import com.twitter.clientlib.model.*;
import com.twitter.clientlib.TwitterCredentialsOAuth2;
import com.twitter.clientlib.TwitterCredentialsBearer;
import com.twitter.clientlib.TwitterCredentialsOAuth1;
import com.twitter.clientlib.api.TwitterApi;

public class SearchTweets {

    public static void main(String[] args) {

        SearchTweets t = new SearchTweets();
        String city = "NY";
        String[] names = {"Momofuku", "capital grille", "keens"};
        System.out.println(t.GetSearchedTweets(city, names));
    }

    /**
     *      Searches for tweets in the area we are considering in terms of longitude and latitude, and contains at least
     *      one of the restaurant names
     *
     *      Query length is limited to 512 CHARACTERS
     * */
    private Twitter getTwitterInstance() {
        ConfigurationBuilder cb = new ConfigurationBuilder();
        cb.setDebugEnabled(true)
                .setOAuthConsumerKey("aFJOAYb6xmGEfEv7kyspsTK8u")
                .setOAuthConsumerSecret("zw8QB4WyEqMZ0Wp5PWtc4xPRyPi1P0hHgV42LHan3eDTgYD2Qs")
                .setOAuthAccessToken("1322109139934306304-HdtnexWKKmQtJogYHgalVp2afbrVum")
                .setOAuthAccessTokenSecret("qMml5G2n3T9wbn7dCddpdDIUDZuloxquFNuzaNg2CEFd7");
        Twitter twitter = new TwitterFactory(cb.build()).getInstance();

        return twitter;
    }

    private TwitterApi getTwitterAPI() {
        TwitterCredentialsOAuth1 credentialsOAuth1 = new TwitterCredentialsOAuth1("aFJOAYb6xmGEfEv7kyspsTK8u",
                "zw8QB4WyEqMZ0Wp5PWtc4xPRyPi1P0hHgV42LHan3eDTgYD2Qs", "1322109139934306304-N5pglGAs4uePDqIlAT9nl4olwwDIXD",
                "rV6KbPPIjoFHOKPI9Vt9ffb6UxtBbP8CBymnXxo8RZXsd");
        TwitterCredentialsBearer credentials = new TwitterCredentialsBearer(System.getenv("AAAAAAAAAAAAAAAAAAAAAKPYbwEAAAAAgCJ2LKJg1u7JkZmjGYnBzKrIejo%3DaGdCNwSP2337H7V8LJheIpHNNNrWPpMWXJjQ2SutTVujBGMdBx"));
        TwitterApi apiInstance = new TwitterApi();
        apiInstance.setTwitterCredentials(credentials);
        apiInstance.setTwitterCredentials(credentialsOAuth1);
        return apiInstance;
    }

    public ArrayList<String> GetSearchedTweets(String cityName, String[] restaurantNames) {
        ArrayList<String> tweetedContent = new ArrayList<>();

        TwitterApi twitter = getTwitterAPI();

        Integer maxResults = 20; // Integer | The maximum number of search results to be returned by a request.
        Set<String> userFields = new HashSet<>(); // Set<String> | A comma separated list of User fields to display.
        Set<String> placeFields = new HashSet<>(); // Set<String> | A comma separated list of Place fields to display.
        Set<String> expansions = new HashSet<>();

        expansions.add("author_id");

        for (String restaurant : restaurantNames) {
            System.out.println("HIHIHIHI");
            String query = "(\"" + restaurant + "\" OR " + "#" + restaurant.replace(" ", "") + ")"; //+ " (\"" + cityName + "\" OR " + "#" + cityName.replace(" ", "") + ")" ;
            try {
                TweetSearchResponse result = twitter.tweets().tweetsRecentSearch(query, null, null, null, null, maxResults, null, null, null, expansions, null, null, null, placeFields, null);
                expansions.add("geo.place_id");

                placeFields.add("geo");
                placeFields.add("full_name");
                placeFields.add("name");

                userFields.add("location");

                if (result.getData() == null)
                    continue;

                for (Tweet tweet : result.getData()) {
                    SingleUserLookupResponse user = twitter.users().findUserById(tweet.getAuthorId(), null, null, userFields);
                    String location = user.getData().getLocation();

                    System.out.println("text: " + tweet.getText().replace("\n", ""));

                    if ((location != null && location.contains(cityName)) || tweet.getText().contains(cityName) ||
                    tweet.getText().contains(cityName.replace(" ", ""))) {
                        System.out.println("INSERTED");
                        tweetedContent.add(tweet.getText());
                    }
                }
            } catch (ApiException e) {
                System.err.println("Exception when calling TweetsApi#tweetsRecentSearch");
                System.err.println("Status code: " + e.getCode());
                System.err.println("Reason: " + e.getResponseBody());
                System.err.println("Response headers: " + e.getResponseHeaders());
                e.printStackTrace();
            }
        }

        System.out.println("num of registered tweets: " + tweetedContent.size());
        return tweetedContent;
    }


    public Map<String, ArrayList<String>> popularTweets(ArrayList<String> tweetedContent, String[] restaurantNames) {
        Map<String, ArrayList<String>> popularTweets = new HashMap<>();

        for (String tweet : tweetedContent) {
            for (int i = 0; i < restaurantNames.length; i++) {
                String restrt = restaurantNames[i];
                if (tweet.contains(restrt)) {
                    if (popularTweets.containsKey(restrt)) {
                        ArrayList<String> tweetsOfRestrt = popularTweets.get(restrt);
                        tweetsOfRestrt.add(tweet);
                        popularTweets.replace(restrt, tweetsOfRestrt);
                    } else {
                        ArrayList<String> tweetsOfRestrt = new ArrayList<>();
                        tweetsOfRestrt.add(tweet);
                        popularTweets.put(restrt, tweetsOfRestrt);
                    }
                    continue;
                }
            }
        }
        return popularTweets;
    }
}