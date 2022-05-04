import org.tartarus.martin.Stemmer;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.*;

public class NlpProcessing {

    Map<String, List<String>> restaurants;
    Set<String> PositiveTerms;
    Set<String> NegativeTerms;
    int positiveCount;
    int negativeCount;
    private final String POSITIVE_TERMS = new File("").getAbsolutePath() + "/src/positive.txt";
    private final String NEGATIVE_TERMS = new File("").getAbsolutePath() + "/src/negative.txt";
    Map<String, Integer> scores;


    public NlpProcessing (Map<String, List<String>> restautants) {
        this.restaurants = restautants;
        this.PositiveTerms = new HashSet<>();
        this.NegativeTerms = new HashSet<>();
        this.scores = new HashMap<>();
        loadFiles();
        evaluateTweets();
    }

    public Map<String, Integer> getTopRestaurants() {
        return this.scores;
    }

    public String[] getTopTen() {
        int maxTweets = 0, minScore = Integer.MAX_VALUE, maxScore = 0;
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

    public void evaluateTweets() {
        for (String restaurant: restaurants.keySet() ) {
            List<String> tweets = restaurants.get(restaurant);
            int sum = 0;
            for (String tweet: tweets) {
                int score = evaluate(tweet);
                sum+=score;
            }
            int avg = sum/tweets.size();
            scores.put(restaurant, avg);
        }
    }

    public void loadFiles () {
        // load the positive terms
        // scan each word and add it to the set of
        // positive terms
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

    public int evaluate (String tweet) {
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
        return sum;
    }

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
