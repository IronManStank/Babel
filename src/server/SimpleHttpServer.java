package server;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.InetSocketAddress;
import java.util.ArrayList;
import java.util.Map;
import java.util.concurrent.Executors;

import CLRLM.Entry;
import CLRLM.Relevance;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.sun.net.httpserver.Headers;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import com.sun.net.httpserver.HttpServer;

/**
 * simple http server for CLRLM
 * @author locoyou
 *
 */
public class SimpleHttpServer {

	public static void main(String[] args) throws IOException {
	    InetSocketAddress addr = new InetSocketAddress(8082);
	    HttpServer server = HttpServer.create(addr, 0);
	    server.createContext("/", new SearchHandler());
	    server.setExecutor(Executors.newCachedThreadPool());
	    server.start();
	    System.out.println("Server is listening on port 8082" );
	}
	
}

class SearchHandler implements HttpHandler {
	Entry entry;
	Relevance relevance;
	String mainHTML;
	Gson GSON_BUILDER = (new GsonBuilder()).disableHtmlEscaping().create();
	
	public SearchHandler() throws IOException {
		entry = new Entry();
		entry.stopWords();
		relevance = new Relevance(entry);
		StringBuffer sb = new StringBuffer();
		BufferedReader br = new BufferedReader(new InputStreamReader(new FileInputStream("data/main.html"), "UTF-8"));	
		String line;
		while((line = br.readLine()) != null) {
			sb.append(line);
			sb.append("\n");
		}
		mainHTML = sb.toString();
		br.close();
	} 

	@Override
	public void handle(HttpExchange exchange) throws IOException {
	    String requestMethod = exchange.getRequestMethod();
	    if (requestMethod.equalsIgnoreCase("GET")) {
		      Headers responseHeaders = exchange.getResponseHeaders();
		      exchange.sendResponseHeaders(200, 0);
		      OutputStream responseBody = exchange.getResponseBody();
		      String path = exchange.getRequestURI().getPath();
		      System.out.println(path);
		      String query = exchange.getRequestURI().getQuery();
	    	  System.out.println(query);
		      if (query!=null) {
			      if (path.equals("/search")) {
				      responseHeaders.set("Content-Type", "text/plain");
			    	  search(query, responseBody);
			      }
			      else {
				      responseHeaders.set("Content-Type", "text/html");
			    	  responseBody.write(mainHTML.getBytes());			    	  
			      }
		      }
		      else {
			      responseHeaders.set("Content-Type", "text/html");
		    	  responseBody.write(mainHTML.getBytes());
		      }
		      responseBody.close();
	    }
	}

	private void search(String kw, OutputStream responseBody) {
		try{
		    relevance.configure(kw);
			System.out.println("in search");
			ArrayList<SearchResults> results = new ArrayList<SearchResults>();
			int x = 0;
			for(Map.Entry<Double, String> targetTerm:relevance.pwRs.entrySet()) {
				x++;
				String w = targetTerm.getValue();
				results.add(new SearchResults(w));
				if(x >= 10) break;
			}
			for(int i = 0; i < 10; i++) {
				results.add(new SearchResults(relevance.result[i]));
			}

			responseBody.write(GSON_BUILDER.toJson(results).getBytes());
		}
		catch(Exception e) {
			e.printStackTrace();
		}
	}

}

class SearchResults {
	String result;
	public SearchResults(String str) {
		result = str;
	}
}


