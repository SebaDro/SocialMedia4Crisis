/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package de.hsbo.fbg.sm4c.collect;

import de.hsbo.fbg.sm4c.common.model.Collection;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import org.joda.time.DateTime;

/**
 *
 * @author Sebastian Drost
 */
public abstract class AbstractReceiver {

    protected Collector collector;
    protected int interval; //in minutes
    protected ExecutorService executor;
    protected List<MessageHandler> handler;
    protected Collection collection;

    public AbstractReceiver(Collector collector, int interval, Collection collection) {
        this.collector = collector;
        this.interval = interval;
        this.executor = Executors.newCachedThreadPool();
        this.handler = new ArrayList();
        this.collection = collection;
    }

    public void addMessageHandler(MessageHandler handler) {
        this.handler.add(handler);
    }

    public void removeMessageHandler(MessageHandler handler) {
        this.handler.remove(handler);
    }

    public abstract boolean stopReceiving();

}
