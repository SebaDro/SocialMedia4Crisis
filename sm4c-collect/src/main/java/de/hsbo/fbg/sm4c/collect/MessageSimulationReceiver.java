/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package de.hsbo.fbg.sm4c.collect;

import de.hsbo.fbg.sm4c.common.model.MessageDocument;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ThreadPoolExecutor;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.joda.time.DateTime;

/**
 *
 * @author Sebastian Drost
 */
public class MessageSimulationReceiver extends AbstractReceiver {

    private static final Logger LOGGER = LogManager.getLogger(MessageSimulationReceiver.class);

    private static final int SIMULATION_INTERVAL = 10000;

    private DateTime startTime;
    private DateTime endTime;

    //simulation intervall in minutes
    public MessageSimulationReceiver(Collector collector, int minutes) {
        super(collector, minutes);
    }

    public MessageSimulationReceiver(Collector collector, int minutes, DateTime startTime, DateTime endTime) {
        super(collector, minutes);
        this.startTime = startTime;
        this.endTime = endTime;
    }

    public void run() {
        DateTime lastRetrieve = this.startTime;
        //add time difference between current time and last retrieve time
        //for the first collecting
        DateTime currentTime = lastRetrieve.plusMinutes(interval);;
        while (!isInterrupted() && currentTime.isBefore(endTime)) {
            try {
                List<MessageDocument> documents = collector.collectMessages(lastRetrieve.toDate(), currentTime.toDate());
                fireMessagesReceived(documents);
                sleep(SIMULATION_INTERVAL);
                lastRetrieve = currentTime;
                currentTime = lastRetrieve.plusMinutes(interval);
            } catch (InterruptedException ex) {
                LOGGER.error("Thread was interrupted", ex);
            }
        }
    }

    private void fireMessagesReceived(List<MessageDocument> documents) {
        handler.forEach(h -> {
            h.processMessages(documents);
        });
    }

    private void fireMessagesReceivedAsynchronous(List<MessageDocument> documents) {
        handler.forEach(h -> {
            executor.submit(new Runnable() {
                @Override
                public void run() {
                    h.processMessages(documents);
                }
            });
        });
    }

}
