package com.cypress.apps.crawler.app;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class ThreadCreator {

  public static void main(String args[]) {

    if (args.length < 1) {
      System.out.println("Please enter the search keywords separated with a space character!");
      System.exit(1);
    }

    ExecutorService execSvc = Executors.newFixedThreadPool(args.length);

    for (int i = 0; i < args.length; i++) {
      execSvc.execute(new WebCrawlerMultiple(args[i]));

    }

    execSvc.shutdown();

  }

}
