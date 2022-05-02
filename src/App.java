import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class App {

    public static void main(String[] args) {
        Map<String, List<String>> test = new HashMap<>();
        List<String> r1 = new ArrayList<>();
        List<String> r2 = new ArrayList<>();
        List<String> r3 = new ArrayList<>();
        r3.add("good positive great nice awesome good great wonderful positive nice");
        r3.add("good amazing best nice decent good great wow fantasitc love");
        r3.add("wonderful");
        r2.add("bad awful horrible bad stupid bad bad bad");
        r2.add("bad awful horrific bad stupid abhorrent bad bad");
        r1.add("Socrates suggests that they use the city as an image to seek how justice comes to be in the soul of an individual. After attributing the origin of society to the individual not being self-sufficient and having many needs which he cannot supply himself, they go on to describe the development of the city. Socrates first describes the \"healthy state\", but Glaucon asks him to describe \"a city of pigs\", as he finds little difference between the two.");
        r1.add("Socrates and his companions conclude their discussion concerning the lifestyle of the guardians, thus concluding their initial assessment of the city as a whole. Socrates assumes each person will be happy engaging in the occupation that suits them best. If the city as a whole is happy, then individuals are happy. In the physical education and diet of the guardians, the emphasis is on moderation, since both poverty and excessive wealth will corrupt them");
        test.put("rest1", r1);
        test.put("rest2", r2);
        test.put("rest3", r3);
        NlpProcessing nlp = new NlpProcessing(test);
        String[] s = nlp.getTopTen();
        System.out.println(s[0]);
    }
}