import org.tartarus.martin.Stemmer;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.*;


/*
* This class will take in a list of restaurants and all tweets related to them and
* output the best 10 restaurants based on popularity and sentiments of these tweets
*/
public class NlpProcessing {
    Map<String, List<String>> restaurants;
    Set<String> PositiveTerms;
    Set<String> NegativeTerms;
    // count of positive and negative terms in our dictionary, useful for
    // frequency calculations
    int positiveCount;
    int negativeCount;
    // Paths to negative positive and negative term documents
    private final String POSITIVE_TERMS = new File("").getAbsolutePath() + "/src/positive.txt";
    private final String NEGATIVE_TERMS = new File("").getAbsolutePath() + "/src/negative.txt";
    Map<String, Double> scores;

    // Constructor initializes all data structures
    public NlpProcessing (Map<String, List<String>> restautants) {
        this.restaurants = restautants;
        this.PositiveTerms = new HashSet<>();
        this.NegativeTerms = new HashSet<>();
        this.scores = new HashMap<>();
        loadFiles();
        evaluateTweets();
    }

    /*
    * Gets the highest ten restaurants based on positive sentiments and popularity
    * measured by how many tweets are there about the restaurant
    * Sentiment scores and tweet cound are rescaled from 0.0 to 1.0 and added together
    * to get a score from 0.0 to 2.0 which si used for comparision
    */
    public String[] getTopTen() {
        int maxTweets = 0;
        double maxScore = 0, minScore = Integer.MAX_VALUE;
        Map<String, Double> finalScores = new HashMap<>();
        for (String restaurant: scores.keySet()) {
            if (scores.get(restaurant) > maxScore) {
                maxScore = scores.get(restaurant);
            }
            if (scores.get(restaurant) < minScore) {
                minScore = scores.get(restaurant);
            }
            if (restaurants.get(restaurant).size() > maxTweets) {
                maxTweets = restaurants.get(restaurant).size();
            }
        }

        for (String restaurant: scores.keySet()) {
            double sentimentScore = (double) (scores.get(restaurant) - minScore) / (double) (maxScore - minScore);
            double popularityScore = (double) restaurants.get(restaurant).size() / (double) maxTweets;
            System.out.println(restaurant);
            System.out.println(sentimentScore+popularityScore);
            finalScores.put(restaurant, sentimentScore+popularityScore);
        }

        PriorityQueue<String> pq = new PriorityQueue((b,a) -> (int) (finalScores.get(a) - finalScores.get(b)));
        for (String restaurant: finalScores.keySet()) {
            pq.add(restaurant);
        }

        if (pq.size() >= 10) {
            String[] res = new String[10];
            for (int i=0; i<10; i++) {
                res[i] = pq.poll();
            }
            return res;
        } else {
            String[] res = new String[pq.size()];
            int count = 0;
            while (!pq.isEmpty()) {
                res[count] = pq.poll();
                count++;
            }
            return res;
        }
    }

    // Main method in the class, for each restaurant, it takes all its tweets,
    // and evaluates each tweet's sentiment and gets the average and places
    // it in the scores map
    public void evaluateTweets() {
        for (String restaurant: restaurants.keySet() ) {
            List<String> tweets = restaurants.get(restaurant);
            double sum = 0;
            for (String tweet: tweets) {
                double score = evaluate(tweet);
                sum+=score;
            }
            double avg = (double) sum/tweets.size();
            scores.put(restaurant, avg);
        }
    }

    // load the positive terms
    // scan each word and add it to the set of
    // positive terms
    public void loadFiles () {
        Scanner in = null;
        try {
            in = new Scanner(new File(POSITIVE_TERMS));
            while (in.hasNext()) {
                String nextWord = in.next();
                String term = processWord(nextWord);
                if (term != null && !PositiveTerms.contains(term)) {
                    PositiveTerms.add(term);
                }
            }
            positiveCount = PositiveTerms.size();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }

        // load the negative terms
        // scan each word and add it to the set of
        // negative terms
        try {
            in = new Scanner(new File(NEGATIVE_TERMS));
            while (in.hasNext()) {
                String nextWord = in.next();
                String term = processWord(nextWord);
                if (term != null && !NegativeTerms.contains(term)) {
                    NegativeTerms.add(term);
                }
            }
            negativeCount = NegativeTerms.size();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }

    }

    // evaluate a tweet by comparing the frequency of negative and positive terms
    // in the tweet
    public double evaluate (String tweet) {
        Set<String> terms = parsetext(tweet);
        int termsCount = terms.size();
        int termPosCount = 0;
        int termNegCount = 0;
        for (String term: terms) {
            if (PositiveTerms.contains(term)) {
                termPosCount++;
            }
            if (NegativeTerms.contains(term)) {
                termNegCount++;
            }
        }
        int sum = termPosCount-termNegCount;
        return (double) sum/terms.size();
    }

    // takes a text and parses and stemms it to get the terms
    public Set<String> parsetext(String text) {
        Set<String> terms = new HashSet<>();
        String[] words = text.split(" ");
        for (String word: words) {
            String term = processWord(word);
            if (term != null && !terms.contains(term)) {
                terms.add(term);
            }
        }
        return terms;
    }

    // parses a word and produces a term if the word is not a stop word, otherwise returns null
    public String processWord (String word) {
            Stemmer stemmer = new Stemmer();
            for (int i=0; i<word.length(); i++) {
                stemmer.add(word.charAt(i));
            }
            String filteredWord = word.replaceAll("[^A-Za-z0-9]", "").toLowerCase();
            stemmer.stem();
            String finalized = stemmer.toString();
            if (finalized.length() < 2) {
                return null;
            }
            return finalized;
    }
}
