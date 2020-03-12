package com.hapi.player.cache;

import android.content.Context;

import com.danikula.videocache.HttpProxyCacheServer;


/**
 * 缓存代理
 */
public class HttpProxyCacheManager {

    private HttpProxyCacheManager() {

    }

    private HttpProxyCacheServer proxy = null;

    public static HttpProxyCacheManager getHttpProxyCacheManager() {
        return Holder.instance;
    }

    public static class Holder {
        private static HttpProxyCacheManager instance = new HttpProxyCacheManager();
    }

    private HttpProxyCacheServer.Builder builder;

    public void setConfig(HttpProxyCacheServer.Builder builder) {
        this.builder = builder;
    }



    public HttpProxyCacheServer getProxy(Context context) {
        if (proxy == null) {
            proxy = newProxy(context);
        }
        return proxy;
    }

    private HttpProxyCacheServer newProxy(Context app) {

        if (builder == null) {
            builder = new HttpProxyCacheServer.Builder(app)
                    .maxCacheSize(1024 * 1024 * 1024);
        }
        return builder.build();
    }

}
