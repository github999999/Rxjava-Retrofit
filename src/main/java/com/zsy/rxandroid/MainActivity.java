package com.zsy.rxandroid;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.adapter.rxjava.RxJavaCallAdapterFactory;
import retrofit2.converter.scalars.ScalarsConverterFactory;
import rx.Observable;
import rx.Subscriber;
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Action1;
import rx.functions.Func1;
import rx.schedulers.Schedulers;

/*
 * 项目名:   RxJava
 * 包名:     com.zsy.rxandroid
 * 文件名:   MainActivity
 * 创建者:   ZSY
 * 创建时间: 2016/10/13 15:32
 * 描述:     响应式编程
 */
public class MainActivity extends AppCompatActivity implements View.OnClickListener {

    private TextView textView1;
    private TextView textView2;
    private TextView textView3;
    private TextView textView4;
    private Button button;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        textView1 = (TextView) findViewById(R.id.tv1);
        textView2 = (TextView) findViewById(R.id.tv2);
        textView3 = (TextView) findViewById(R.id.tv3);
        textView4 = (TextView) findViewById(R.id.tv4);
        button = (Button) findViewById(R.id.btn);
        findViewById(R.id.btn1).setOnClickListener(this);
        findViewById(R.id.btn2).setOnClickListener(this);
        findViewById(R.id.btn3).setOnClickListener(this);
        numberOne();
        numberTwo();
        numberThree();
        numberFour();
        numberFive();
    }


    //第一种
    private void numberOne() {
        //这里定义的Observable(被观察者，事件源)  对象仅仅发出一个Hello World字符串，然后就结束了。
        Observable<String> observable = Observable.create(
                new Observable.OnSubscribe<String>() {
                    @Override
                    public void call(Subscriber<? super String> sub) {
                        sub.onNext(sayRxAndroid());
                        sub.onCompleted();
                    }
                }
        );
        //接着我们创建一个Subscriber(观察者)来处理Observable对象发出的字符串。
        //只要订阅了observable，那么当他发生改变时这里就可以接收到消息
        Subscriber<String> subscriber = new Subscriber<String>() {
            @Override
            public void onNext(String s) {
                textView1.setText(s);
            }

            @Override
            public void onCompleted() {
            }

            @Override
            public void onError(Throwable e) {
            }
        };
        //通过subscribe函数就可以将我们定义的observable对象和subscriber对象关联起来，
        // 这样就完成了subscriber对observable的订阅。
        observable.subscribe(subscriber);//订阅
    }

    //第二种，简单形式。Observable.just()就是用来创建只发出一个事件就结束的Observable对象
    private void numberTwo() {
        Observable<String> observable = Observable.just(sayRxAndroid() + "简单形式");
        //我们其实并不关心OnComplete和OnError，
        //我们只需要在onNext的时候做一些处理，这时候就可以使用Action1类。
        Action1<String> action1 = new Action1<String>() {
            @Override
            public void call(String s) {
                textView2.setText(s);
            }
        };
        //订阅
        observable.subscribe(action1);
    }

    //方式二的代码最终可以简化成这样
    private void numberThree() {
        Observable.just(sayRxAndroid() + "简单形式二")
                .subscribe(new Action1<String>() {
                    @Override
                    public void call(String s) {
                        textView3.setText(s);
                    }
                });
    }

    //请求网络数据
    private void numberFour() {
        final Observable<String> observable = Observable.create(new Observable.OnSubscribe<String>() {
            @Override
            public void call(Subscriber<? super String> subscriber) {
                subscriber.onNext(getInternet());
                subscriber.onCompleted();
            }
        })
                //Schedulers(调度器)->线程控制
                //subscribeOn(Schedulers.io())--->指定 observable 发生在 IO 线程
                //observeOn(AndroidSchedulers.mainThread())--->指定 subscribe 的回调发生在主线程
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread());
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(final View v) {
                observable.subscribe(new Subscriber<String>() {
                    @Override
                    public void onCompleted() {

                    }

                    @Override
                    public void onError(Throwable e) {

                    }

                    @Override
                    public void onNext(String s) {
                        textView4.setText("RxJava:--Get" + s);
                    }
                });
            }
        });
    }

    //从网络上获取一张图片
    private void numberFive() {
        Observable.just("http://sc.jb51.net/uploads/allimg/141119/10-1411192130010-L.jpg") // 输入类型 String
                .map(new Func1<String, Bitmap>() {
                    @Override
                    public Bitmap call(String url) { // 参数类型 String
                        return getBitmap(url); // 返回类型 Bitmap
                    }
                })
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Action1<Bitmap>() {
                    @Override
                    public void call(Bitmap bitmap) {
                        showBitmap(bitmap);
                    }

                    //显示获取的图片
                    private void showBitmap(Bitmap bitmap) {
                        ImageView iv = (ImageView) findViewById(R.id.iv);
                        iv.setImageBitmap(bitmap);
                    }
                });
    }

    // 从网络获取资源
    private String getInternet() {
        StringBuilder sb = new StringBuilder();
        String line;
        BufferedReader reader;
        try {
            URL url = new URL("http://www.omghz.cn/FirstService/getString?username=a_zhon&password=9584");
            HttpURLConnection urlConnection = (HttpURLConnection) url.openConnection();
            urlConnection.setRequestMethod("GET");
            urlConnection.setConnectTimeout(3000);
            urlConnection.setReadTimeout(3000);
            if (urlConnection.getResponseCode() == 200) {
                reader = new BufferedReader(new InputStreamReader(urlConnection.getInputStream(), "UTF-8"));
                while ((line = reader.readLine()) != null) {
                    sb.append(line);
                }
                return sb.toString();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    //从网络获取图片
    private Bitmap getBitmap(String path) {
        try {
            URL url = new URL(path);
            HttpURLConnection urlConnection = (HttpURLConnection) url.openConnection();
            urlConnection.setRequestMethod("GET");
            urlConnection.setConnectTimeout(3000);
            urlConnection.setReadTimeout(3000);
            if (urlConnection.getResponseCode() == 200) {
                InputStream inputStream = urlConnection.getInputStream();
                return BitmapFactory.decodeStream(inputStream);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    // 创建字符串
    private String sayRxAndroid() {
        return "你好啊！RxAndroid";
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btn1:
                getStringRetrofit();
                break;
            case R.id.btn2:
                getStringRetrofitRxJava();
                break;
            case R.id.btn3:
                getJson();
                break;
        }
    }

    /**
     * 使用Retrofit与Get请求
     */
    private void getJson() {
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl("http://www.omghz.cn/FirstService/")
                //增加返回值为String的支持
                .addConverterFactory(ScalarsConverterFactory.create())
                .build();
        RequestServes serves = retrofit.create(RequestServes.class);//这里采用的是Java的动态代理模式
        Call<String> call = serves.getJson("名字", "密码");//传入我们请求的键值对的值
        call.enqueue(new Callback<String>() {
            @Override
            public void onResponse(Call<String> call, Response<String> response) {
                textView4.setText("Retrofit:--Get" + response.body());
            }

            @Override
            public void onFailure(Call<String> call, Throwable t) {

            }
        });
    }

    /**
     * 使用Retrofit与Post请求网络
     */
    private void getStringRetrofit() {
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl("http://www.omghz.cn/FirstService/")
                //增加返回值为String的支持
                .addConverterFactory(ScalarsConverterFactory.create())
                .build();
        RequestServes serves = retrofit.create(RequestServes.class);//这里采用的是Java的动态代理模式
        Call<String> call = serves.getStringRetrofit("阿钟", "1011");//传入我们请求的键值对的值
        call.enqueue(new Callback<String>() {
            @Override
            public void onResponse(Call<String> call, Response<String> response) {
                textView4.setText("Retrofit:--Post" + response.body());
            }

            @Override
            public void onFailure(Call<String> call, Throwable t) {

            }
        });

    }

    /**
     * 使用Retrofit+RxJava与Post请求网络
     */
    private void getStringRetrofitRxJava() {
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl("http://www.omghz.cn/FirstService/")
                .addConverterFactory(ScalarsConverterFactory.create())
                //解决 Unable to create call adapter for rx.Observable<java.lang.String>
                .addCallAdapterFactory(RxJavaCallAdapterFactory.create())
                .build();
        retrofit.create(RequestServes.class)//这里采用的是Java的动态代理模式
                .getStringRxJava("阿钟", "1011")//传入我们请求的键值对的值
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Subscriber<String>() {
                    @Override
                    public void onCompleted() {

                    }

                    @Override
                    public void onError(Throwable e) {

                    }

                    @Override
                    public void onNext(String s) {
                        textView4.setText("Retrofit+RxJava:--Post" + s);
                    }
                });
    }
}