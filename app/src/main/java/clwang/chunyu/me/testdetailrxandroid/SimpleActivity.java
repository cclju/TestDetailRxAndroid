package clwang.chunyu.me.testdetailrxandroid;

import android.app.Activity;
import android.os.Bundle;
import android.widget.TextView;
import android.widget.Toast;

import butterknife.Bind;
import butterknife.ButterKnife;
import rx.Observable;
import rx.Subscriber;
import rx.android.schedulers.AndroidSchedulers;

/**
 * RxJava的基础讲解, 包含一个观察者(Observable), 两个订阅者(Subscriber).
 * <p>
 * Created by wangchenlong on 15/12/30.
 */
public class SimpleActivity extends Activity {

    @Bind(R.id.simple_tv_text) TextView mTvText;

    // 1. 创建一个观察者, 收到字符串的返回
    // 观察事件发生
    private Observable.OnSubscribe mObservableAction = new Observable.OnSubscribe<String>() {
        @Override public void call(Subscriber<? super String> subscriber) {
            subscriber.onNext(sayMyName()); // 发送事件
            subscriber.onCompleted(); // 完成事件
        }
    };

    // 订阅者, 接收字符串, 修改控件
    private Subscriber<String> mTextSubscriber = new Subscriber<String>() {
        @Override public void onCompleted() {

        }

        @Override public void onError(Throwable e) {

        }

        @Override public void onNext(String s) {
            mTvText.setText(s); // 设置文字
        }
    };

    // 订阅者, 接收字符串, 提示信息
    private Subscriber<String> mToastSubscriber = new Subscriber<String>() {
        @Override public void onCompleted() {

        }

        @Override public void onError(Throwable e) {

        }

        @Override public void onNext(String s) {
            Toast.makeText(SimpleActivity.this, s, Toast.LENGTH_SHORT).show();
        }
    };

    @Override protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_simple);
        ButterKnife.bind(this);

        // 在页面中, 观察者接收信息, 发送至主线程AndroidSchedulers.mainThread(), 再传递给订阅者, 由订阅者最终处理消息. 接收信息可以是同步, 也可以是异步.

        // 注册观察活动
        @SuppressWarnings("unchecked")
        Observable<String> observable = Observable.create(mObservableAction);

        // 分发订阅信息
        observable.observeOn(AndroidSchedulers.mainThread());

        // 2. 创建两个订阅者, 使用字符串输出信息
        observable.subscribe(mTextSubscriber);  // 发送事件后在这里 mTextSubscriber 结果处理
        observable.subscribe(mToastSubscriber); // 发送事件后在这里 mToastSubscriber 结果处理
    }

    // 创建字符串
    private String sayMyName() {
        return "Hello, I am your friend, Spike!";
    }
}
