///*
// * To change this license header, choose License Headers in Project Properties.
// * To change this template file, choose Tools | Templates
// * and open the template in the editor.
// */
//package de.hsbo.fbg.sm4c.collect;
//
//import de.hsbo.fbg.sm4c.common.model.FacebookSource;
//import de.hsbo.fbg.sm4c.common.model.MessageDocument;
//import de.hsbo.fbg.sm4c.common.model.SourceCategory;
//import java.util.ArrayList;
//import java.util.List;
//import java.util.logging.Level;
//import java.util.logging.Logger;
//import org.apache.logging.log4j.LogManager;
//import org.apache.spark.SparkConf;
//import org.apache.spark.streaming.Duration;
//import org.apache.spark.streaming.api.java.JavaReceiverInputDStream;
//import org.apache.spark.streaming.api.java.JavaStreamingContext;
//import org.joda.time.DateTime;
//import org.junit.Before;
//import org.junit.Test;
//import org.spark_project.guava.collect.Lists;
//
///**
// *
// * @author Sebastian Drost
// */
//public class FacebookRecevierIT {
//
//    private static final org.apache.logging.log4j.Logger LOGGER = LogManager.getLogger(FacebookRecevierIT.class);
//
//    private static ArrayList<FacebookSource> sources;
//
//    public static void main(String[] args) {
//        setup();
//        receiveFacebookMessagesTest();
//    }
//
//    public static void setup() {
//        FacebookSource groupSource = new FacebookSource();
//        groupSource.setName("HILFE GRUPPE HOCHWASSER");
//        groupSource.setDescription("Wir wollen den Leute helfen mit Sachspenden wer gern mit helfen will kann das gerne tun pn an mich ");
//        groupSource.setFacebookId("525390524175222");
//        SourceCategory groupCategory = new SourceCategory();
//        groupCategory.setName("Group");
//        groupSource.setCategory(groupCategory);
//
//        FacebookSource pageSource = new FacebookSource();
//        pageSource.setName("Hochwasserhilfe für Sachsen, Sachsen-Anhalt, Thüringen, Bayern");
//        pageSource.setDescription("Weite Teile Deutschlands werden auch 2013 nicht vom Hochwasser verschont!");
//        pageSource.setFacebookId("337930749644059");
//        SourceCategory pageCategory = new SourceCategory();
//        pageCategory.setName("Page");
//        pageSource.setCategory(pageCategory);
//
//        sources = Lists.newArrayList(groupSource, pageSource);
//    }
//
//    public static void receiveFacebookMessagesTest() {
//
//        DateTime startDate = new DateTime("2013-06-01T00:00:00.000+02:00");
//        DateTime endDate = new DateTime("2013-09-01T18:25:46.000+02:00");
//
//        SparkConf sparkConf = new SparkConf().setMaster("local[2]").setAppName("JavaCustomReceiver");
//        JavaStreamingContext ssc = new JavaStreamingContext(sparkConf, new Duration(1000));
//        FacebookReceiver receiver = new FacebookReceiver(sources, startDate, endDate, 0, true);
//        JavaReceiverInputDStream<MessageDocument> documentStream = ssc.receiverStream(receiver);
//        documentStream.print();
////        documentStream.count().print();
//        ssc.start();
//        try {
//            ssc.awaitTermination();
//        } catch (InterruptedException ex) {
//            Logger.getLogger(FacebookRecevierIT.class.getName()).log(Level.SEVERE, null, ex);
//        }
//    }
//}
