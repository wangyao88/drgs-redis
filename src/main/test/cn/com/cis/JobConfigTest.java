package cn.com.cis;

import cn.com.cis.module.task.service.JobConfigService;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.transaction.annotation.Transactional;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration("file:../../../../webapp/WEB-INF/config/spring-context.xml")
@Transactional
public class JobConfigTest{
    @Autowired
    private JobConfigService service;
    @Before
    public void setup(){

    }
    @Test
    public void testQueryAllJobs(){
        //int size = service.queryAllJobs().size();
        //System.out.println(size);
    }
}
