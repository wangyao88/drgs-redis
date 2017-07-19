package cn.com.cis;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.web.WebAppConfiguration;
import org.springframework.web.context.WebApplicationContext;

@WebAppConfiguration
@ContextConfiguration("file:../../../../webapp/WEB-INF/config/spring-context.xml")
public class AbstractContextControllerTests {

    @Autowired
    protected WebApplicationContext wac;

}
