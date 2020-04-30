package NetflixSearch;

import org.json.simple.JSONObject;
import org.json.simple.JSONArray;
import org.json.simple.parser.JSONParser;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.Random;
import java.util.Scanner;
import java.util.Properties;

public class NetflixSearch {
	InputStream inputStream;
	
	
	public String getApiKey() throws IOException, FileNotFoundException {
		Properties prop = new Properties();
		String propFileName = "config.properties";
		inputStream = getClass().getClassLoader().getResourceAsStream(propFileName);
		prop.load(inputStream);

		return prop.getProperty("key");
		
	}

	public String getJson(String type, String apiKey) throws IOException, InterruptedException {
		
		String uri = "https://unogsng.p.rapidapi.com/search?type=" + type + "&limit=100&audio=english";
		HttpClient client = HttpClient.newHttpClient();
		HttpRequest request = HttpRequest.newBuilder()
				.uri(URI.create(uri))
				.header("x-rapi-host", "unogsng.p.rapidapi.com")
				.header("x-rapidapi-key", apiKey)
				.build();
		
		HttpResponse<String> response = client.send(request, 
				HttpResponse.BodyHandlers.ofString());
		
		return response.body();
	}
	
	public JSONObject parseJson(String parseMe) throws IOException, Exception {
	    JSONParser parser = new JSONParser();
	    Random rand = new Random(); 
        int random_int = rand.nextInt(100); 

		// HTTP GET and Parse data	
		JSONObject jsonObject = (JSONObject) parser.parse(parseMe);
	    JSONArray resultsArray = (JSONArray) jsonObject.get("results");
	    JSONObject resultsObject = (JSONObject) resultsArray.get(random_int);
	    return resultsObject;
	    		
	}
	
	public String getInput() {
	    // Get info from user
	    Scanner scan = new Scanner(System.in);
	    System.out.println("Movie or Show?");
	    String input = scan.nextLine().toLowerCase();
	    scan.close();
	    switch (input) {
	    case "movie":
	    	return input;
	    case "m":
	    	return "movie";
	    case "show":
	    	return "series";
	    case "s":
	    	return "series";
	    default:
	    	System.out.println("You did not enter a valid option.");
	    	System.exit(0);
	    	return "";
	    }
	}
	
	public void printPretty(String summary, String title, String type) {
		String bottomBox = "";
		String topBox = "";
		int typeLen = type.length();
		
		if (type == "series") {type = "T.V. Show";}
		
		while (bottomBox.length() < 100) {
	    	bottomBox = bottomBox + "=";
	    	
	    	if (bottomBox.length() + typeLen == 50) {
	    		topBox = topBox + " " + type.toUpperCase() + " ";
	    	} else if (topBox.length() == 100) {
	    		continue;
	    	} else { topBox = topBox + "="; }
	    }

	    System.out.println(topBox);
	    System.out.println("Lets Watch:\n  " + title);
	    System.out.println("\nDescription:\n  " + summary.replaceAll("&#39;", "'"));
        System.out.println(bottomBox);
        
		
	}
	
	public String summaryNewLine(String summary) {
    	int summaryLineNum = ((summary.length() / 100) + 1);
    	int summaryChar = 98;   	
    	String summaryNewLine = "";
    	
    	for(int i = 1; i <= summaryLineNum; i++) {
	       	if (i == summaryLineNum) {
	    		summary = summaryNewLine + " " + summary.substring(0);
	    		break;
	    	} else {
	    		while (summary.charAt(summaryChar) != ' ') { summaryChar--;}		
	    	 	String loop = summary.substring(0, summaryChar) + "\n";
	    		summaryNewLine = summaryNewLine + loop;
	    		summary = summary.substring(summaryNewLine.length()-1);
	    	}	    	
	    }

		return summary;
	}
	
	public static void main(String[] args) throws IOException, Exception {
		NetflixSearch blob = new NetflixSearch();

        String type = blob.getInput();
        String apiKey = blob.getApiKey();
        String parseMe = blob.getJson(type, apiKey);
        JSONObject json = blob.parseJson(parseMe);
	    String title = json.get("title").toString();
	    String summary = json.get("synopsis").toString();

	    if (summary.length() > 100) {
	    	summary = blob.summaryNewLine(summary);
	    }
	    
	    blob.printPretty(summary, title, type);

	}

}
