/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package de.hsbo.fbg.sm4c.collect;

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
    protected DateTime startTime;
    protected DateTime endTime;
    protected MessageHandler handler;
    protected ThreadPoolExecutor executor;

    public AbstractReceiver(Collector collector, MessageHandler handler, int interval) {
        this.collector = collector;
        this.handler = handler;
        this.interval = interval;
        this.executor = new ThreadPoolExecutor(10, 50, 0L, TimeUnit.SECONDS, new LinkedBlockingQueue());
    }

    public abstract void run();

}
