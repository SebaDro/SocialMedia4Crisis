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
    
    private static final int SIMULATION_INTERVAL = 5000;

    //simulation intervall in minutes
    public MessageSimulationReceiver(Collector collector, MessageHandler handler, int minutes) {
        super(collector, handler, minutes);
    }

    public MessageSimulationReceiver(Collector collector, MessageHandler handler, int minutes, DateTime startTime) {
        super(collector, handler, minutes);
        this.startTime = startTime;
    }

    public void run() {
        DateTime lastRetrieve = this.startTime;
        DateTime currentTime = null;
        while (!isInterrupted()) {
            try {
                sleep(SIMULATION_INTERVAL);
                currentTime = lastRetrieve.plusMinutes(interval);  
                List<MessageDocument> documents = collector.collectMessages(lastRetrieve.toDate(), currentTime.toDate());
                lastRetrieve = currentTime;
                documents.forEach(d -> executor.submit(handler.getHandlingRoutine(d)));
            } catch (InterruptedException ex) {
                LOGGER.error("Thread was interrupted", ex);
            }
        }
    }

}
