package com.coco.heart.common;

import java.util.concurrent.atomic.AtomicLong;

import org.apache.curator.framework.CuratorFramework;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.coco.heart.entry.PingEntry;
import com.google.common.base.Joiner;
import com.google.common.base.Preconditions;
import com.google.gson.Gson;
import com.ms.coco.registry.common.CocoUtils;
import com.ms.coco.registry.model.ServerNode;
import com.ms.coco.registry.model.ServerNode.ServerStatus;
import com.netflix.config.AbstractPollingScheduler;
import com.netflix.config.ConfigurationManager;
import com.netflix.config.DynamicBooleanProperty;
import com.netflix.config.DynamicConfiguration;
import com.netflix.config.DynamicIntProperty;
import com.netflix.config.DynamicLongProperty;
import com.netflix.config.DynamicPropertyFactory;
import com.netflix.config.DynamicStringProperty;
import com.netflix.config.FixedDelayPollingScheduler;
import com.netflix.config.PolledConfigurationSource;
import com.netflix.config.sources.URLConfigurationSource;

/**
 * @author wanglin/netboy
 * @version 创建时间：2016年2月7日 下午4:06:40
 * @func
 */
public class Utils {
    private static final Logger LOGGER = LoggerFactory.getLogger(Utils.class);

    public static DynamicBooleanProperty enabeSelfPing =
            DynamicPropertyFactory.getInstance().getBooleanProperty("soa.carkey.heartcenter.enabeSelfPing", false);

    public static DynamicLongProperty redisKeyLivetimeInMillisecond = DynamicPropertyFactory.getInstance()
            .getLongProperty("soa.carkey.heartcenter.redisKeyLivetimeInMillisecond", 2000);

    public static DynamicStringProperty pingUri =
            DynamicPropertyFactory.getInstance().getStringProperty("soa.carkey.heartcenter.pingUri", "/heart");
    public static DynamicStringProperty blackListUrlPath = DynamicPropertyFactory.getInstance()
            .getStringProperty("soa.carkey.heartcenter.blackListUrlPath", "/blackList");
    public static DynamicIntProperty httpClientTimeoutInMillisecond = DynamicPropertyFactory.getInstance()
            .getIntProperty("soa.carkey.heartcenter.httpClientTimeoutInMilliSecond", 500);

    public static DynamicLongProperty lockKeyLivetimeInMillisecond = DynamicPropertyFactory.getInstance()
            .getLongProperty("soa.carkey.heartcenter.lockKeyLivetimeInMillisecond", 1000);

    public static String BIZ_THREAD_POOL_NAME = "biz-thread-pool";

    public static PingEntry redisKey2PingEntry(String key) {
        String[] array = key.split("&");
        return new PingEntry(array[1], array[2], array[3], array[4]);
    }

    public static PingEntry redisKeyWithTraceId2PingEntry(String key) {
        String[] array = key.split("&");
        return new PingEntry(array[0], array[1], array[2], array[3], Long.valueOf(array[4]));
    }

    public static String pubSubKey = "=";
    public static String hostDead = "dead";
    public static String hostLive = "live";
    public static String traceIdKey = "soaHeartCenterTraceId";
    public static String lockStart = "lock_";
    public static String pingKeyPrefix = "soa-key";
    public static final Gson gson = new Gson();
    public static final AtomicLong idOffset = new AtomicLong();

    public static void sysInit(String dynamicConfName) {
        // System.setProperty("archaius.configurationSource.additionalUrls",
        // "./conf/heartCenterDynamic.properties");
        System.setProperty("archaius.configurationSource.defaultFileName", dynamicConfName);
        System.setProperty("archaius.fixedDelayPollingScheduler.delayMills", "2000");
        System.setProperty("archaius.fixedDelayPollingScheduler.initialDelayMills", "1000");
        //
        AbstractPollingScheduler scheduler = new FixedDelayPollingScheduler();
        PolledConfigurationSource source = new URLConfigurationSource();
        DynamicConfiguration configuration = new DynamicConfiguration(source, scheduler);
        ConfigurationManager.install(configuration);
        // ConfigJMXManager.registerConfigMbean(configuration);
    }
    public static String buildPath(String... pathName) {
        return Joiner.on("/").skipNulls().join(pathName);
    }

    /**
     *
     * TODO: update the server's status
     *
     * @param serviceName
     * @param groupName
     * @param hostKey
     * @param serverAvailable true means the server available . false to invalid
     * @param registerUrl
     * @return
     */
    public static boolean updateHostStatus(String serviceName, String groupName, String hostKey,
            boolean serverAvailable, String registerUrl) {
        Preconditions.checkNotNull(serviceName);
        Preconditions.checkNotNull(groupName);
        Preconditions.checkNotNull(hostKey);
        Preconditions.checkNotNull(serverAvailable);
        Preconditions.checkNotNull(registerUrl);
        String parentPath = buildPath("", serviceName, groupName);
        return updateHostStatus(parentPath, hostKey, serverAvailable, registerUrl);
    }


    public static boolean updateHostStatus(String serviceGroup, String hostKey, boolean serverAvailable,
            String registerUrl) {
        Preconditions.checkNotNull(serviceGroup);
        Preconditions.checkNotNull(hostKey);
        Preconditions.checkNotNull(serverAvailable);
        Preconditions.checkNotNull(registerUrl);
        boolean result = false;
        CuratorFramework client = RegisterHolder.getClient(registerUrl);
        String nodepath = serviceGroup + "/" + hostKey;
        ServerNode serverNode = null;
        try {
            Object obj = CocoUtils.getPathaData(client, nodepath);
            if (obj == null) {
                return result;
            }
            serverNode = ServerNode.jsonToNode(obj.toString());
            if (serverAvailable) {
                serverNode.setServiceStatus(ServerStatus.Available);
            } else {
                serverNode.setServiceStatus(ServerStatus.Invalid);
            }
            CocoUtils.createOrUpdateServerNode(client, serverNode, serviceGroup);
            result = true;
        } catch (Exception e) {
            LOGGER.error("soa-manager update host status ,path=[{}],data=[{}],detail info={}", nodepath, serverNode,
                    e.toString());
        }
        return result;
    }

}
