package com.github.simplesteph.udemy;

import spark.Request;
import spark.Response;

import java.math.BigInteger;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.List;

import static spark.Spark.get;
import static spark.Spark.threadPool;

public class EC2SampleApp {

    private static List<String> myBigList = new ArrayList<>();
    private static Boolean isHealthy = true;

    public static void main(String[] args) {

        int maxThreads = 32;
        int minThreads = 2;
        int timeOutMillis = 30000;
        threadPool(maxThreads, minThreads, timeOutMillis);

        get("/", EC2SampleApp::hello);
        get("/cpu", EC2SampleApp::cpu);
        get("/ram", EC2SampleApp::ram);
        get("/health", EC2SampleApp::health);
        get("/flip-health", EC2SampleApp::flipHealth);
        get("/details", EC2SampleApp::details);
    }

    private static String hello(Request request, Response response) throws UnknownHostException {
        StringBuilder sb = new StringBuilder();

        String myHostname = InetAddress.getLocalHost().getHostName();
        sb.append("Hello World from: ").append(myHostname).append("<br/>");
        String sourceIP = request.ip();
        sb.append("Received from: ").append(sourceIP).append("<br/>");
        return sb.toString();
    }


    private static String cpu(Request request, Response response) {
        long currentTime = System.currentTimeMillis();
        BigInteger computation = fib(10000);
        return "fib function took " + (System.currentTimeMillis() - currentTime) + " milliseconds";
    }


    private static String ram(Request request, Response response) {
        String myBigString = new String(new char[10000000]);
        myBigList.add(myBigString);
        return getRamInfo();
    }


    private static String health(Request request, Response response) {
        if (isHealthy){
            return "Healthy!";
        } else {
            response.status(401);
            return "Not healthy";
        }
    }

    private static String flipHealth(Request request, Response response) {
        isHealthy = !isHealthy;
        return health(request, response);
    }


    private static String details(Request request, Response response) throws UnknownHostException {
        StringBuilder sb = new StringBuilder();
        String myHostname = InetAddress.getLocalHost().getHostName();
        sb.append("Hello World from: ").append(myHostname).append("<br/>");
        String sourceIP = request.ip();
        sb.append("Received from: ").append(sourceIP).append("<br/>").append("<br/>");
        sb.append("Request Headers are: ").append("<br/>");
        request.headers().forEach(header -> sb.append(header).append(": ").append(request.headers(header)).append("<br/>"));
        return sb.toString();
    }

    // Fibonacci sequence to perform some long computations
    private static BigInteger fib(long nth){
        nth = nth - 1;
        long count = 0;
        BigInteger first = BigInteger.ZERO;
        BigInteger second = BigInteger.ONE;

        BigInteger third = null;
        while(count < nth){
            third = new BigInteger(first.add(second).toString());
            first = new BigInteger(second.toString());
            second = new BigInteger(third.toString());
            count++;
        }

        return third;
    }


    // get some RAM statistics
    private static String getRamInfo() {

        Runtime runtime = Runtime.getRuntime();
        NumberFormat format = NumberFormat.getInstance();

        StringBuilder sb = new StringBuilder();
        long maxMemory = runtime.maxMemory();
        long allocatedMemory = runtime.totalMemory();
        long freeMemory = runtime.freeMemory();

        sb.append("free memory: ").append(format.format(freeMemory / 1024)).append("<br/>");
        sb.append("allocated memory: ").append(format.format(allocatedMemory / 1024)).append("<br/>");
        sb.append("max memory: ").append(format.format(maxMemory / 1024)).append("<br/>");
        sb.append("total free memory: ").append(format.format((freeMemory + (maxMemory - allocatedMemory)) / 1024)).append("<br/>");
        return sb.toString();
    }

}
