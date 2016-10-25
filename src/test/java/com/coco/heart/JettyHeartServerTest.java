package com.coco.heart;

import org.junit.Test;

/**
* @author wanglin/netboy
* @version 创建时间：2016年10月25日 下午1:37:56
* @func 
*/
public class JettyHeartServerTest {

    @Test
    public void testStart() {
        final JettyHeartServer jettyServer2 = new JettyHeartServer();

        Runtime.getRuntime().addShutdownHook(new Thread() {
            @Override
            public void run() {
                try {
                    jettyServer2.close();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }

        });
        try {
            jettyServer2.start();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

}
