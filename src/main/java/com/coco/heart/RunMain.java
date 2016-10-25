package com.coco.heart;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.FileSystemXmlApplicationContext;

/**
 * @author wanglin/netboy
 * @version 创建时间：2016年3月25日 下午3:45:37
 * @func
 */
public class RunMain {

    private static final Logger logger = LoggerFactory.getLogger(RunMain.class);

    /**
     * @param args
     * @throws Exception
     */
    public static void main(String[] args) throws Exception {
        String contextFile = "classpath:spring-all.xml";
        ApplicationContext context = null;
        try {
            context = new FileSystemXmlApplicationContext(contextFile);
        } catch (Exception e) {
            System.out.println("RunMain [spring-conf-file]");
            logger.warn("load spring error", e);
            return;
        }

        final HeartCenterServer server = (HeartCenterServer) context.getBean("heartCenterServer");
        server.start();

        Runtime.getRuntime().addShutdownHook(new Thread() {

            @Override
            public void run() {
                try {
                    server.close();
                } catch (Exception e) {
                    logger.warn("close error", e);
                }
            }

        });
    }

}
