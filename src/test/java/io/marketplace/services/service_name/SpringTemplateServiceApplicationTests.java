package io.marketplace.services.service_name;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.ApplicationContext;
import org.springframework.test.context.junit4.SpringRunner;

import io.marketplace.services.service_name.SpringTemplateServiceApplication;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.core.Is.is;
import static org.hamcrest.core.IsNull.notNullValue;

@SpringBootTest(classes = SpringTemplateServiceApplication.class)
@RunWith(SpringRunner.class)
public class SpringTemplateServiceApplicationTests {

    @Autowired
    private ApplicationContext applicationContext;

    @Test
    public void contextLoads() {
        assertThat(applicationContext, is(notNullValue()));
    }

}
