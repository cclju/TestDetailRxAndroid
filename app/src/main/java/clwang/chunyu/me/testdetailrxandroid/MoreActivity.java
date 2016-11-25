package clwang.chunyu.me.testdetailrxandroid;

import android.app.Activity;
import android.os.Bundle;
import android.widget.TextView;
import android.widget.Toast;

import java.util.Arrays;
import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;
import rx.Observable;
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Action1;
import rx.functions.Func1;
import rx.functions.Func2;


/**
 *
 * just: 获取输入数据, 直接分发, 更加简洁, 省略其他回调.(just 可以非常简单的获取任何数据)
   from: 获取输入数组, 转变单个元素分发.
   map: 映射, 对输入数据进行转换, 如大写.
   flatMap: 增大, 本意就是增肥, 把输入数组映射多个值, 依次分发.
   reduce: 简化, 正好相反, 把多个数组的值, 组合成一个数据.
 *
 */
public class MoreActivity extends Activity {

    @Bind(R.id.simple_tv_text) TextView mTvText;

    final String[] mManyWords = {"Hello", "I", "am", "your", "friend", "Spike"};
    final List<String> mManyWordList = Arrays.asList(mManyWords);

    // Action类似订阅者, 设置TextView
    private Action1<String> mTextViewAction = new Action1<String>() {
        @Override public void call(String s) {
            mTvText.setText(s);
        }
    };

    // Action设置Toast
    private Action1<String> mToastAction = new Action1<String>() {
        @Override public void call(String s) {
            Toast.makeText(MoreActivity.this, s, Toast.LENGTH_SHORT).show();
        }
    };

    // 设置映射函数
    private Func1<List<String>, Observable<String>> mOneLetterFunc = new Func1<List<String>, Observable<String>>() {
        @Override public Observable<String> call(List<String> strings) {
            return Observable.from(strings); // 映射字符串
        }
    };

    // 设置大写字母
    private Func1<String, String> mUpperLetterFunc = new Func1<String, String>() {
        @Override public String call(String s) {
            return s.toUpperCase(); // 大小字母
        }
    };

    // 连接字符串
    private Func2<String, String, String> mMergeStringFunc = new Func2<String, String, String>() {
        @Override public String call(String s, String s2) {
            return String.format("%s %s", s, s2); // 空格连接字符串
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_simple);
        ButterKnife.bind(this);


        /**
         * 输入字符串, 变换大写, 输出至控件中显示
         *
         * just可以非常简单的获取任何数据, 分发时, 选择使用的线程.
         map是对输入数据加工, 转换类型, 输入Func1, 准换大写字母.
         Func1代表使用一个参数的函数, 前面是参数, 后面是返回值.
         Action1代表最终动作, 因而不需要返回值, 并且一个参数.
         */

        // 添加字符串, 省略Action的其他方法, 只使用一个onNext.
        Observable<String> obShow = Observable.just(sayMyName());

        // 先映射, 再设置TextView
        obShow.observeOn(AndroidSchedulers.mainThread())
                .map(mUpperLetterFunc).subscribe(mTextViewAction);



        /**
         * 输入数组, 单独分发数组中每一个元素, 转换大写, 输入Toast连续显示.
         *
         * from是读取数组中的值, 每次单独分发, 并分发多次, 其余类似.
         *
         */

        // 单独显示数组中的每个元素
        Observable<String> obMap = Observable.from(mManyWords);

        // 映射之后分发
        obMap.observeOn(AndroidSchedulers.mainThread())
                .map(mUpperLetterFunc).subscribe(mToastAction);



        /**
         * 输入数组, 映射为单独分发, 并组合到一起, 集中显示.
         *
         * 这次是使用just分发数组, 则分发数据就是数组, 并不是数组中的元素.
         flatMap把数组转换为单独分发, Func1内部使用from拆分数组.
         reduce把单独分发数据集中到一起, 再统一分发, 使用Func2.
         最终使用Action1显示获得数据. 本次代码也更加简洁.

         */

        // 优化过的代码, 直接获取数组, 再分发, 再合并, 再显示toast, Toast顺次执行.
        Observable.just(mManyWordList)
                .observeOn(AndroidSchedulers.mainThread())
                .flatMap(mOneLetterFunc)
                .reduce(mMergeStringFunc)
                .subscribe(mToastAction);
    }

    // 创建字符串
    private String sayMyName() {
        return "Hello, I am your friend, Spike!";
    }
}
