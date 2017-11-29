package com.nikitosh.spbau;

import com.nikitosh.spbau.database.DatabaseHandler;
import com.nikitosh.spbau.dataprocessor.DataHandler;
import com.nikitosh.spbau.dataprocessor.DataHandlerImpl;
import com.nikitosh.spbau.queryprocessor.QueryProcessor;
import com.nikitosh.spbau.queryprocessor.QueryProcessorImpl;
import com.nikitosh.spbau.webcrawler.WebCrawler;
import com.nikitosh.spbau.webcrawler.WebCrawlerImpl;

import java.io.IOException;
import java.util.Arrays;
import java.util.List;

import static com.nikitosh.spbau.storage.StorageImpl.HTML_STORAGE_DIRECTORY_PATH;

public final class Main {
    private static final List<String> SEED_URLS = Arrays.asList(
            "http://news.yandex.ru/",
            "http://news.google.ru/",
            "http://news.mail.ru/"
    );

    private Main() {}

    public static void main(String[] args) throws IOException {
        //crawl();
        process();
        answerQueries();
    }

    private static void crawl() {
        WebCrawler webCrawler = new WebCrawlerImpl();
        webCrawler.crawl(SEED_URLS);
    }

    private static void process() throws IOException {
        DataHandler dataHandler = new DataHandlerImpl();
        dataHandler.process(HTML_STORAGE_DIRECTORY_PATH);
    }

    private static void answerQueries() throws IOException {
        QueryProcessor queryProcessor = new QueryProcessorImpl();
        List<Integer> documents = queryProcessor.getDocumentsForQuery("");
        for (int documentId : documents) {
            String url = DatabaseHandler.getInstance().getUrlForId(documentId);
            System.out.println(url);
        }
    }
}
