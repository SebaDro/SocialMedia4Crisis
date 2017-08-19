/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package de.hsbo.fbg.sm4c.collect;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import org.joda.time.DateTime;

/**
 *
 * @author Sebastian Drost
 */
public abstract class AbstractReceiver extends Thread {

    protected Collector collector;
    protected int interval; //in seconds
    protected ThreadPoolExecutor executor;
    protected List<MessageHandler> handler;

    public AbstractReceiver(Collector collector, int interval) {
        this.collector = collector;
        this.interval = interval;
        this.executor = new ThreadPoolExecutor(10, 50, 0L, TimeUnit.SECONDS, new LinkedBlockingQueue());
        this.handler = new ArrayList();
    }

    public void addMessageHandler(MessageHandler handler) {
        this.handler.add(handler);
    }

    public void removeMessageHandler(MessageHandler handler) {
        this.handler.remove(handler);
    }

    public abstract void run();

}
