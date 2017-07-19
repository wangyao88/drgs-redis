package cn.com.cis;

import junit.framework.TestCase;

import java.net.MalformedURLException;
import java.net.URL;

/**
 * Created by irock on 15-2-16.
 */
public class URLTest extends TestCase{
    public void testUrl() throws MalformedURLException {
        URL  url = new URL("file:/Users/irock/ExecutionStatisticsTest.xml");
        if(url != null ){
            System.out.println(url.toString());
        }
    }
}
