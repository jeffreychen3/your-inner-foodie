import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.util.ArrayList;
import java.util.Scanner;

/**
 * Class will use HTTP to get the contents of a page.
 */
public class URLGetter {

    private URL url;
    private HttpURLConnection httpConnection;

    public URLGetter(String url) {
        try {
            this.url = new URL(url);

            URLConnection connection = this.url.openConnection();
            httpConnection = (HttpURLConnection) connection;
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * This method will print the status codes from the connection.
     */
    public void printStatusCode() {
        try {
            int code = httpConnection.getResponseCode();
            String message = httpConnection.getResponseMessage();

            System.out.println(code + " : " + message);
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    /**
     * This method will return the contents of the page
     * @return the arraylist of strings for each line of the page
     */
    public ArrayList<String> getContents() {
        ArrayList<String> contents = new ArrayList<>();

        try {
            Scanner in = new Scanner(httpConnection.getInputStream());

            while(in.hasNextLine()) {
                String line = in.nextLine();
                contents.add(line);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

        return contents;

    }
}
