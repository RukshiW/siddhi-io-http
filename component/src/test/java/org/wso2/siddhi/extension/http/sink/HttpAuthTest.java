package org.wso2.siddhi.extension.http.sink;

import org.apache.log4j.Logger;
import org.testng.Assert;
import org.testng.annotations.Test;
import org.wso2.siddhi.core.ExecutionPlanRuntime;
import org.wso2.siddhi.core.SiddhiManager;
import org.wso2.siddhi.core.stream.input.InputHandler;
import org.wso2.siddhi.extension.http.sink.exception.HttpSinkAdaptorRuntimeException;
import org.wso2.siddhi.extension.http.sink.util.HttpServerListenerHandler;
import org.wso2.siddhi.extension.output.mapper.xml.XMLSinkMapper;


/**
 * test cases for basic authentication.
 */
public class HttpAuthTest {
    private static final Logger log = Logger.getLogger(HttpAuthTest.class);

    /**
     * Creating test for publishing events wth basic authentication false.
     *
     * @throws Exception Interrupted exception
     */
    @Test
    public void testHTTPTextAuthFalse() throws Exception {
        log.info(" Creating test for publishing events wth basic authentication false.");
        SiddhiManager siddhiManager = new SiddhiManager();
        siddhiManager.setExtension("xml-output-mapper", XMLSinkMapper.class);
        String inStreamDefinition = "Define stream FooStream (message String,method String,headers String);" +
                "@sink(type='http'," + "publisher.url='http://localhost:8009'," + "method='{{method}}'," + "headers='" +
                "{{headers}}',"
                + "basic.auth.enabled='false'," + "@map(type='xml', @payload('{{message}}'))) "
                + "Define stream BarStream (message String,method String,headers String);";
        String query = ("@info(name = 'query') " +
                "from FooStream select message,method,headers insert into BarStream;");
        ExecutionPlanRuntime executionPlanRuntime = siddhiManager.createExecutionPlanRuntime(inStreamDefinition
                + query);
        InputHandler fooStream = executionPlanRuntime.getInputHandler("FooStream");
        executionPlanRuntime.start();
        HttpServerListenerHandler lst = new HttpServerListenerHandler(8009, "application/xml");
        fooStream.send(new Object[]{"<events><event><symbol>WSO2</symbol>" +
                "<price>55.645</price><volume>100</volume></event></events>", "GET", "Name:John#Age:23"});
        while (!lst.getServerListner().iaMessageArrive()) {
        }
        String eventData = lst.getServerListner().getData();
        String expected = "<events><event><symbol>WSO2</symbol><price>55.645</price><volume>100</volume>" +
                "</event></events>\n";
        Assert.assertEquals(eventData, expected);
        executionPlanRuntime.shutdown();
        lst.shutdown();
    }

    /**
     * Creating test for publishing events with basic authentication true.
     *
     * @throws Exception Interrupted exception
     */
    @Test
    public void testHTTPTextMappingBasicAuthTrue() throws Exception {
        log.info("Creating test for publishing events with basic authentication true.");
        SiddhiManager siddhiManager = new SiddhiManager();
        siddhiManager.setExtension("xml-output-mapper", XMLSinkMapper.class);
        String inStreamDefinition = "Define stream FooStream (message String,method String,headers String);" +
                "@sink(type='http'," + "publisher.url='http://localhost:8009'," + "method='{{method}}'," + "headers=" +
                "'{{headers}}'," + "basic.auth.username='admin',"
                + "basic.auth.password='admin'," + "basic.auth.enabled='true'," + "@map(type='xml', @payload" +
                "('{{message}}'))) "
                + "Define stream BarStream (message String,method String,headers String);";
        String query = ("@info(name = 'query') " +
                "from FooStream select message,method,headers insert into BarStream;");
        ExecutionPlanRuntime executionPlanRuntime = siddhiManager.createExecutionPlanRuntime(inStreamDefinition +
                query);
        InputHandler fooStream = executionPlanRuntime.getInputHandler("FooStream");
        executionPlanRuntime.start();
        HttpServerListenerHandler lst = new HttpServerListenerHandler(8009, "application/xml");
        fooStream.send(new Object[]{"<events><event><symbol>WSO2</symbol>" +
                "<price>55.645</price><volume>100</volume></event></events>", "GET", "Name:John#Age:23"});
        while (!lst.getServerListner().iaMessageArrive()) {
        }
        String eventData = lst.getServerListner().getData();
        Assert.assertEquals(eventData,
                "<events><event><symbol>WSO2</symbol>" +
                        "<price>55.645</price><volume>100</volume></event></events>\n");
        executionPlanRuntime.shutdown();
        lst.shutdown();
    }

    /**
     * Creating test for publishing events basic authentication true and user name password is not given.
     *
     * @throws Exception Interrupted exception
     */
    @Test(expectedExceptions = {HttpSinkAdaptorRuntimeException.class, ExceptionInInitializerError.class})
    public void testHTTPPublisherBasicAuthTrueCredNotGiven() throws Exception {
        log.info("Creating test for publishing events basic authentication true and user name password is not given.");
        SiddhiManager siddhiManager = new SiddhiManager();
        siddhiManager.setExtension("xml-output-mapper", XMLSinkMapper.class);
        String inStreamDefinition = "Define stream FooStream (message String,method String,headers String);" +
                "@sink(type='http'," + "publisher.url='http://localhost:8009'," + "method='{{method}}'," + "headers=" +
                "'{{headers}}',"
                + "basic.auth.enabled='true'," + "@map(type='xml', @payload" + "('{{message}}'))) "
                + "Define stream BarStream (message String,method String,headers String);";
        String query = ("@info(name = 'query') " +
                "from FooStream select message,method,headers insert into BarStream;");
        ExecutionPlanRuntime executionPlanRuntime = siddhiManager.createExecutionPlanRuntime(inStreamDefinition +
                query);
        InputHandler fooStream = executionPlanRuntime.getInputHandler("FooStream");
        executionPlanRuntime.start();
        HttpServerListenerHandler lst = new HttpServerListenerHandler(8009, "application/xml");
        fooStream.send(new Object[]{"<events><event><symbol>WSO2</symbol>" +
                "<price>55.645</price><volume>100</volume></event></events>", "GET", "Name:John#Age:23"});
        while (!lst.getServerListner().iaMessageArrive()) {
        }
        String eventData = lst.getServerListner().getData();
        Assert.assertEquals(eventData,
                "<events><event><symbol>WSO2</symbol>" +
                        "<price>55.645</price><volume>100</volume></event></events>\n");
        executionPlanRuntime.shutdown();
        lst.shutdown();
    }

    /**
     * Creating test for publishing events without URL.
     *
     * @throws Exception Interrupted exception
     */
    @Test(expectedExceptions = {HttpSinkAdaptorRuntimeException.class, ExceptionInInitializerError.class})
    public void testHTTPWithoutURL() throws Exception {
        log.info("Creating test for publishing events without URL.");
        SiddhiManager siddhiManager = new SiddhiManager();
        siddhiManager.setExtension("xml-output-mapper", XMLSinkMapper.class);

        String inStreamDefinition = "Define stream FooStream (message String,method String,headers String);" +
                "@sink(type='http'," + "method='{{method}}'," + "headers='{{headers}}',"
                + "@map(type='xml', @payload('{{message}}'))) "
                + "Define stream BarStream (message String,method String,headers String);";
        String query = ("@info(name = 'query') " +
                "from FooStream select message,method,headers insert into BarStream;");
        ExecutionPlanRuntime executionPlanRuntime = siddhiManager.createExecutionPlanRuntime(inStreamDefinition
                + query);
        InputHandler fooStream = executionPlanRuntime.getInputHandler("FooStream");
        executionPlanRuntime.start();
        HttpServerListenerHandler lst = new HttpServerListenerHandler(8009, "application/xml");
        fooStream.send(new Object[]{"<events><event><symbol>WSO2</symbol>" +
                "<price>55.645</price><volume>100</volume></event></events>", "GET", "Name:John#Age:23"});
        while (!lst.getServerListner().iaMessageArrive()) {
        }
        String eventData = lst.getServerListner().getData();
        Assert.assertEquals(eventData,
                "<events><event><symbol>WSO2</symbol>" +
                        "<price>55.645</price><volume>100</volume></event></events>\n");
        executionPlanRuntime.shutdown();
        lst.shutdown();
    }
}
