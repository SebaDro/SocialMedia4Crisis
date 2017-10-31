/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package de.hsbo.fbg.sm4c.collect;

import de.hsbo.fbg.sm4c.common.model.Collection;
import de.hsbo.fbg.sm4c.common.model.MessageDocument;
import java.util.List;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.joda.time.DateTime;

/**
 *
 * @author Sebastian Drost
 */
public class MessageSimulationReceiver extends AbstractReceiver implements Runnable {
    
    private static final Logger LOGGER = LogManager.getLogger(MessageSimulationReceiver.class);

    private static final int RECEIVER_PERIOD = 30;
    
    private AtomicBoolean finished = new AtomicBoolean(false);
    private AtomicBoolean running = new AtomicBoolean(false);
    private AtomicBoolean stopped = new AtomicBoolean(true);
    
    private DateTime startTime;
    private DateTime endTime;
    private DateTime currentTime;
    private DateTime lastRetrieve;
    private ScheduledFuture receiveHandler;

    //simulation intervall in minutes
    public MessageSimulationReceiver(Collector collector, int minutes, Collection collection) {
        super(collector, minutes, collection);
    }
    
    public MessageSimulationReceiver(Collector collector, int minutes, DateTime startTime, DateTime endTime, Collection collection) {
        super(collector, minutes, collection);
        this.startTime = startTime;
        this.endTime = endTime;
        this.lastRetrieve = this.startTime;
    }
    
    @Override
    public void run() {
        try {
            receiveMessages();
        } catch (Exception ex) {
            LOGGER.error("Could not receive messages.", ex);
        }
    }
    
    private void receiveMessages() {
        synchronized (this) {
            
            currentTime = lastRetrieve.plusMinutes(interval);;
            //add time difference between current time and last retrieve time
            //for the first collecting
            if (!currentTime.isBefore(endTime)) {
                if (this.receiveHandler.cancel(true)) {
                    this.finished.set(true);
                    this.running.set(false);
                    this.stopped.set(false);
                    LOGGER.info("Finished receiving for Collection: " + this.collection.getId());
                }
            }
            LOGGER.info("Collect messages between " + lastRetrieve.toString() + " and " + currentTime.toString());
            List<MessageDocument> documents = collector.collectMessages(lastRetrieve.toDate(), currentTime.toDate());
            lastRetrieve = currentTime;
            fireMessagesReceived(documents, this.collection);
        }
    }
    
    public boolean runSimulationReceiver(ScheduledExecutorService scheduler) {
        synchronized (this) {
            if (this.receiveHandler == null) {
                this.receiveHandler = scheduler.scheduleAtFixedRate(this, 0, RECEIVER_PERIOD, TimeUnit.SECONDS);
                this.running.set(true);
                this.finished.set(false);
                this.stopped.set(false);
                LOGGER.info("Started receiving for Collection: " + this.collection.getId());
                return true;
            } else if (this.stopped.get()) {
                this.receiveHandler = scheduler.scheduleAtFixedRate(this, 0, RECEIVER_PERIOD, TimeUnit.SECONDS);
                this.running.set(true);
                this.finished.set(false);
                this.stopped.set(false);
                LOGGER.info("Restarted receiving for Collection: " + this.collection.getId());
                return true;
            } else {
                return false;
            }
        }
    }
    
    @Override
    public boolean stopReceiving() {
        if (receiveHandler.cancel(true)) {
            this.stopped.set(true);
            this.running.set(false);
            this.finished.set(false);
            LOGGER.info("Stopped receiving for Collection: " + this.collection.getId());
        }
        return this.stopped.get();
    }
    
    private void fireMessagesReceived(List<MessageDocument> documents, Collection collection) {
        handler.forEach(h -> {
            h.processMessages(documents, collection);
        });
    }
    
    private void fireMessagesReceivedAsynchronous(List<MessageDocument> documents, Collection collection) {
        handler.forEach(h -> {
            executor.submit(new Runnable() {
                @Override
                public void run() {
                    h.processMessages(documents, collection);
                }
            });
        });
    }
    
}
