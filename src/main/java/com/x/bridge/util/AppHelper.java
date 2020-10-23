package com.x.bridge.util;

import com.x.doraemon.util.Strings;

/**
 * @Desc TODO
 * @Date 2020/10/23 23:10
 * @Author AD
 */
public class AppHelper {
    
    public static String getIP(String appAddress) {
        if (Strings.isNotNull(appAddress)) {
            return appAddress.split(":")[0];
        }
        return appAddress;
    }
    
    public static int getPort(String appAddress) {
        if (Strings.isNotNull(appAddress)) {
            return Integer.parseInt(appAddress.split(":")[1]);
        }
        return -1;
    }
    
}
