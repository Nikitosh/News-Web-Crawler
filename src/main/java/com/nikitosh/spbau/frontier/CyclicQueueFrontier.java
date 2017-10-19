package com.nikitosh.spbau.frontier;

import com.nikitosh.spbau.parser.ParserHelper;
import com.nikitosh.spbau.parser.PermissionsParser;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.net.URISyntaxException;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.Map;
import java.util.Queue;

public class CyclicQueueFrontier implements Frontier {
    private static final Logger LOGGER = LogManager.getLogger();

    private Map<String, DomainUrlsSet> domainUrlsSets = new HashMap<>();
    private Queue<String> domainsQueue = new LinkedList<>();
    private Map<String, Long> domainLastVisitedTime = new HashMap<>();
    private PermissionsParser permissionsParser;

    public CyclicQueueFrontier(PermissionsParser permissionsParser) {
        this.permissionsParser = permissionsParser;
    }

    @Override
    public DomainUrlsSet getNextDomainUrlsSet() {
        while (!domainsQueue.isEmpty()) {
            String domain = domainsQueue.remove();
            if (!domainUrlsSets.get(domain).isEmpty() && canVisit(domain)) {
                domainsQueue.add(domain);
                domainLastVisitedTime.put(domain, System.currentTimeMillis());
                return domainUrlsSets.get(domain);
            }
            domainsQueue.add(domain);
        }
        return null;
    }

    @Override
    public void addUrl(String url) {
        try {
            String domain = ParserHelper.getDomainName(url);
            if (!domainUrlsSets.containsKey(domain)) {
                domainUrlsSets.put(domain, new InDegreeDomainUrlsSet());
                domainsQueue.add(domain);
            }
            domainUrlsSets.get(domain).addUrl(url);
        } catch (URISyntaxException exception) {
            LOGGER.error("Failed to parse url: " + url + " due to exception: "
                    + exception.getMessage() + "\n");
        }
    }

    @Override
    public boolean isFinished() {
        for (String domain : domainsQueue) {
            if (!domainUrlsSets.get(domain).isEmpty()) {
                return false;
            }
        }
        return true;
    }

    private boolean canVisit(String domain) {
        return !domainLastVisitedTime.containsKey(domain)
                || System.currentTimeMillis() >
                domainLastVisitedTime.get(domain) + permissionsParser.getDelay(domain) * 1000;
    }
}
