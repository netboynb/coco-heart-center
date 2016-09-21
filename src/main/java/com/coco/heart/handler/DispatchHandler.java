package com.coco.heart.handler;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;

import com.coco.heart.domain.HeartRequest;
import com.coco.heart.domain.HeartResponse;
import com.coco.heart.filter.HeartFilter;

/**
* @author wanglin/netboy
* @version 创建时间：2016年9月21日 下午5:52:27
* @func 
*/
public class DispatchHandler {
    @Autowired
    private List<HeartFilter> filterList;

    public void dispatch(HeartRequest heartRequest, HeartResponse heartResponse) {
        if (null != filterList) {
            for (HeartFilter filter : filterList) {
                boolean result = filter.filter(heartRequest, heartResponse);
                if (!result) {
                    break;
                }
            }
        }
    }

    public List<HeartFilter> getFilterList() {
        return filterList;
    }

    public void setFilterList(List<HeartFilter> filterList) {
        this.filterList = filterList;
    }

}
