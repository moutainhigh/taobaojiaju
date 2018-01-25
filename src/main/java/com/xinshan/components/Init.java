package com.xinshan.components;

import com.xinshan.utils.CommonUtils;
import org.springframework.context.ApplicationListener;
import org.springframework.context.event.ContextRefreshedEvent;
import org.springframework.stereotype.Component;

import java.io.File;
import java.net.URL;


/**
 * Created by mxt on 17-8-10.
 */
@Component
public class Init implements ApplicationListener<ContextRefreshedEvent> {
    @Override
    public void onApplicationEvent(ContextRefreshedEvent event) {
        if (event.getApplicationContext().getParent() == null) {
            ClassLoader classLoader = event.getApplicationContext().getClassLoader();
            URL resource = classLoader.getResource("/config.properties");
            File file = new File(resource.toString());
            String parent = file.getParent();
            System.out.println(parent);
            CommonUtils.init(parent);
            System.out.println("启动完成");
        }
    }
}
