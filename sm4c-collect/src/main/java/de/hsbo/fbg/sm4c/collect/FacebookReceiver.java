/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package de.hsbo.fbg.sm4c.collect;

/**
 *
 * @author Sebastian Drost
 */
public class FacebookReceiver {
    
//    private List<FacebookSource> sources;
//    private DateTime startTime;
//    private DateTime endTime;
//    private long interval;
//    private boolean initial;
//
//    /**
//     * Creates a receiver that polls the Facebook Graph API for messages with no
//     * specified start and end time
//     *
//     * @param sources the Facebook source (groups and pages) used to poll for
//     * messages
//     * @param intervall the time intervall in seconds between the pollings Graph
//     * API
//     */
//    public FacebookReceiver(List<FacebookSource> sources, long intervall) {       
//        this.sources = sources;
//        this.startTime = null;
//        this.endTime = null;
//        this.initial = false;
//    }
//
//    /**
//     * Creates a receiver that polls the Facebook Graph API for historical
//     * messages with a specified start and end time
//     *
//     * @param sources the Facebook source (groups and pages) used to poll for
//     * messages
//     * @param startTime start time for querying historical messages or
//     * simulation
//     * @param endTime end time for querying historical messages or simulation
//     * @param intervall the time intervall in seconds between the pollings
//     * @param initial
//     */
//    public FacebookReceiver(List<FacebookSource> sources, DateTime startTime, DateTime endTime, long intervall, boolean initial) {
//        super(StorageLevel.MEMORY_AND_DISK_2());
//        this.sources = sources;
//        this.startTime = startTime;
//        this.endTime = endTime;
//        this.interval = intervall;
//        this.initial = initial;
//    }
//    
//    @Override
//    public void onStart() {
//        if (initial) {
//            new Thread(this::initialReceive).start();
//        } else {
//            new Thread(this::receive).start();
//        }
//    }
//    
//    @Override
//    public void onStop() {
//        
//    }
//    
//    private void receive() {
//        
//    }
//    
//    private void initialReceive() {
//        FacebookCollector collector = new FacebookCollector();
//        FacebookEncoder fbEncoder = new FacebookEncoder();
//        Iterator<FacebookSource> sourceIterator = sources.iterator();
//        while (!isStopped() && sourceIterator.hasNext()) {
//            FacebookSource source = sourceIterator.next();
//            List<Post> posts = collector.getMessagesFromSingleSource(source, startTime.toDate(), endTime.toDate());
//            List<MessageDocument> messages = posts.stream()
//                    .map(p -> fbEncoder.createMessage(p, source))
//                    .collect(Collectors.toList());
////            Iterator<MessageDocument> iterator = messages.iterator();
////            store(iterator);
//            messages.forEach(m -> store(m));
//        }
//    }
}
