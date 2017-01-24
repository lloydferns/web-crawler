package com.cypress.apps.crawler.app;

import java.io.IOException;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.concurrent.TimeUnit;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

public class WebCrawlerMultiple implements Runnable {

  private static String proxyhost = "rootproxy.cypress.com";
  private static String proxyport = "8080";

  static {
    System.setProperty("https.proxySet", "true");
    System.setProperty("https.proxyHost", proxyhost);
    System.setProperty("https.proxyPort", proxyport);
    System.setProperty("http.proxySet", "true");
    System.setProperty("http.proxyHost", proxyhost);
    System.setProperty("http.proxyPort", proxyport);
  }

  private static final int MAX_DEPTH = 2;
  private HashSet<String> links;

  private Map<String, Integer> rankMap;
  private Integer count = 0;

  private String searchString = "";
  private Pattern p;

  long startTime = 0;
  long elapsedTimeInMinutes = 0;

  public WebCrawlerMultiple(String searchString) {
    links = new HashSet<>();
    rankMap = new HashMap<String, Integer>();
    p = Pattern.compile(searchString);
    this.searchString = searchString;

    startTime = System.nanoTime();

  }

  public void getPageLinks(String URL, int depth) {
    if ((!links.contains(URL) && (depth < MAX_DEPTH))) {

      count++;

      if (count % 10 == 0) {
        System.out.println("[" + URL + "]");
      }

      try {
        links.add(URL);

        Document document = Jsoup.connect(URL).get();
        Elements linksOnPage = document.select("a[href]");

        String in = document.text();
        int i = 0;

        if (document.text().contains(searchString)) {
          Matcher m = p.matcher(in);
          while (m.find()) {
            i++;
          }
          rankMap.put(URL, i);
        }

        //elapsed time can be used if searching depth is high and the program takes a lot of time to complete
        elapsedTimeInMinutes = elapsedTime(startTime);

        /*if(elapsedTimeInMinutes > 1){
          System.out.println("");
          
          rankMap.forEach((k,v)->System.out.println("Key : " + k + " Value : " + v));
          
          System.exit(1);
          
        }*/

        depth++;
        for (Element page : linksOnPage) {
          //System.out.println("Page: " + page.toString()); 
          if (page.attr("abs:href").contains("http")) {
            getPageLinks(page.attr("abs:href"), depth);
          }

          //System.out.println("ABS:HREF " + depth + ": "+ page.attr("abs:href"));

        }

      } catch (IOException e) {
        System.err.println("For '" + URL + "': " + e.getMessage());
      }
    }

  }

  public static long elapsedTime(long startTime) {
    return TimeUnit.NANOSECONDS.toMinutes(System.nanoTime() - startTime);
  }

  public static void main(String[] args) {

    System.out.println("Search String: " + args[0]);

  }

  @Override
  public void run() {
    getPageLinks("http://www.mi.com/in", 0);
    
    System.out.println("Ranking for Search String: " + searchString);
    rankMap.forEach((k, v) -> System.out.println("Key : " + k + " Value : " + v));

    System.out.println();

  }

}
