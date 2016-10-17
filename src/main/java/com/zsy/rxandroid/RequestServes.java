package com.zsy.rxandroid;
/*
 * 项目名:     JulyProject
 * 包名:       com.zsy.rxandroid
 * 文件名:     RequestServes
 * 创建者:     dell
 * 创建时间:   2016/10/17 20:03
 * 描述:       TODO
 */

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.Query;
import rx.Observable;

interface RequestServes {
    /**
     * 使用retrofit与Get方式获取网络数据
     *
     * @GET("getString")--->定义请求方式和请求地址==== Call<JSONObject>--->定义返回结果类型
     */
    @GET("getString")
    Call<String> getJson(@Query("username") String username, @Query("password") String password);
    //@Query("username") String username, @Query("password") String password
    //请求参数，括号内为键值名，参数为传递的值

    //使用retrofit与Post方式获取网络数据
    @POST("getString")
    Call<String> getStringRetrofit(@Query("username") String username, @Query("password") String password);

    //使用retrofit与RxJava与Post方式获取网络数据
    @POST("getString")
    Observable<String> getStringRxJava(@Query("username") String username, @Query("password") String password);
}
