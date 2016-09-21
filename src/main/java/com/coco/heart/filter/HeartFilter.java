package com.coco.heart.filter;

import com.coco.heart.domain.HeartRequest;
import com.coco.heart.domain.HeartResponse;

/**
* @author wanglin/netboy
* @version 创建时间：2016年9月21日 下午6:33:27
* @func 
*/
public interface HeartFilter {
    boolean filter(HeartRequest heartRequest, HeartResponse heartResponse);
}
