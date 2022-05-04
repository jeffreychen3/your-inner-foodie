import twitter4j.*;
import twitter4j.Query;
import twitter4j.QueryResult;

import java.util.*;

import twitter4j.conf.ConfigurationBuilder;

import java.util.List;

public class SearchTweets {

    public static void main(String[] args) {
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

    public ArrayList<String> GetSearchedTweets(Double[][] geolocation, String[] restaurantNames) {
        ArrayList<String> tweetedContent = new ArrayList<>();

        Twitter twitter = getTwitterInstance();
        String queryStr = "";

        // bounding_box:[west_long south_lat east_long north_lat]
        // TODO: check if the input is being given s.t. the first element is the "bottom corner" of the bounding box
        queryStr += "bounding_box:[" + geolocation[0][0] + " " + geolocation[0][1] + " " + geolocation[1][0] + " " +
                geolocation[1][1]+ "]";

        // Since query wants tweets from the bounding box AND contain the name of any of the restaurants, adding a
        //      bracket before we list the restaurant names
        queryStr += " (";

        int idx = 0;

        do {
            queryStr += " \"" + restaurantNames[idx] + "\"" + " OR";
            // Removing "OR" placed after the last restaurant in the query
            if (idx == restaurantNames.length - 1) {
                queryStr.substring(0, queryStr.length() - 3);
            }
            idx++;
        }
        // Twitter has a query length limit of 512 characters
        while (queryStr.length() + restaurantNames[idx].length() < 510 && idx < restaurantNames.length - 1);

        queryStr += ")";

        try {
            Query query = new Query(queryStr);
            QueryResult result;

            do {
                result = twitter.search(query);
                List<Status> tweets = result.getTweets();

                for (Status tweet : tweets) {
                    tweetedContent.add(tweet.getText());
                }
            } while ((query = result.nextQuery()) != null);

            return tweetedContent;
        } catch (TwitterException te) {
            te.printStackTrace();
            System.out.println("Failed to search tweets: "
                    + te.getMessage());
        }
        return null;
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