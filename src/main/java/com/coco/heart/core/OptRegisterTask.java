package com.coco.heart.core;

import java.util.concurrent.Callable;

import com.coco.heart.common.Utils;
import com.coco.heart.entry.PingEntry;

/**
 * @author wanglin/netboy
 * @version 创建时间：2016年3月29日 上午10:40:52
 * @param <V>
 * @func
 */
public class OptRegisterTask implements Callable<Object> {
    private PingEntry pingEntry;
    private boolean mkAvalibale = true;

    public OptRegisterTask(PingEntry item, boolean mkAvalibal) {
        this.pingEntry = item;
        this.mkAvalibale = mkAvalibal;
    }

    @Override
    public Object call() throws Exception {
        boolean result = Utils.updateHostStatus(pingEntry.getServiceName(), pingEntry.getGroupName(),
                pingEntry.getHostKey(), mkAvalibale, pingEntry.getZkurl());
        return result;
    }

}
