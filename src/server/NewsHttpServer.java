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

import org.apache.lucene.document.Document;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.sun.net.httpserver.Headers;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import com.sun.net.httpserver.HttpServer;

import search.LuceneEntryNews;

public class NewsHttpServer {

	public static void main(String[] args) {
		try {
		    InetSocketAddress addr = new InetSocketAddress(8080);
		    HttpServer server = HttpServer.create(addr, 0);
		    server.createContext("/", new NewsSearchHandler());
		    server.setExecutor(Executors.newCachedThreadPool());
		    server.start();
		    System.out.println("Server is listening on port 8080" );
		}
		catch(Exception e) {
			e.printStackTrace();
		}
	}
	
}

class NewsSearchHandler implements HttpHandler {
	String mainHTML;
	LuceneEntryNews le;
	Gson GSON_BUILDER = (new GsonBuilder()).disableHtmlEscaping().create();
	
	public NewsSearchHandler() throws IOException {
		le = new LuceneEntryNews();
		StringBuffer sb = new StringBuffer();
		BufferedReader br = new BufferedReader(new InputStreamReader(new FileInputStream("data/news.html"), "UTF-8"));	
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
			System.out.println("in search " + kw);
			ArrayList<Map.Entry<Document,Double>> res = le.search(kw);
			ArrayList<NewsSearchResults> results = new ArrayList<NewsSearchResults>();
			int x = 0;
			for(Map.Entry<Document, Double> result:res) {
				x++;
				Document d = result.getKey();
				String show = d.get("content");
				if(show.length() > 200) {
					show = show.substring(0, 200) + "...";
				}
				results.add(new NewsSearchResults(d.get("content"), d.get("title"), d.get("url"), d.get("description"), show));
				if(x >= 10) break;
			}

			responseBody.write(GSON_BUILDER.toJson(results).getBytes());
		}
		catch(Exception e) {
			e.printStackTrace();
		}
	}

}

class NewsSearchResults {
	String content;
	String title;
	String url;
	String description;
	String show;
	public NewsSearchResults(String content, String title, String url, String description, String show) {
		this.content = content;
		this.title = title;
		this.url = url;
		this.description = description;
		this.show = show;
	}
}
