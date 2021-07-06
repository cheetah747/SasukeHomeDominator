package com.sibyl.sasukehomeDominator.util;

import android.app.Activity;

import java.lang.ref.WeakReference;
import java.util.Timer;
import java.util.TimerTask;

/**
 * Created by Sasuke on 2017/3/9.
 *
 * 针对Timer可中途取消的一个TimerTask而已。
 *
 *  算法原理：
 * 通过每秒都执行一次 t++，使 t 来代表当前秒数，
 * 再给goal = t + 3; 即为让goal比t超前3秒，由于 t 是不断在走的，
 * 所以当 t 到达goal时，就是等效于倒计时3秒，
 * 而每次给goal重新赋值，就相当于重新计时，这样就不需要重建对象或取消任务什么的了（目测有小于1秒的误差，不要在意这些细节~）
 * 取消倒计时就是用goal = t - 1;
 * 用来替代timer.cancel()不能取消已入队列的任务的缺陷。
 *
 *
 *
 * example:
 *
 * Timer autoTimer = new Timer();
 * counter = new CountDownDominator(this, this);
 * counter.build(autoTimer).start(5);
 *
 * counter.cancel;
 *
 */

public class CountDownDominator {
//    private long period = 30 * 1000;//倒计时时长
    private long goal = 0;//目标时间。
    private long t = 0;//当前秒数。(时间轴)
    private CountDownCallback callback;
    private WeakReference<Activity> mRef;
    private int tag = -1;
    private CountDownDominator instance;
    /**
     * 构造
     * @param activity 用来使callback在UI线程执行
     * @param callback 倒计时完成时的回调
     * @param tag 多个schedule任务添加到一个timer时，用来在Activity的回调中分辨开来。
     */
    public CountDownDominator(Activity activity, CountDownCallback callback, int tag){
        this.mRef = new WeakReference<Activity>(activity);
        this.callback = callback;
        this.tag = tag;
        instance = this;
    }

    public CountDownDominator(Activity activity, CountDownCallback callback){
        this.mRef = new WeakReference<Activity>(activity);
        this.callback = callback;
        instance = this;
    }

    public CountDownDominator build(Timer timer){
        timer.schedule(new TimerTask() {//匿名内部类，会隐匿持有外部类的一个引用。
            @Override
            public void run() {
                t += 100;
                if(t == goal && mRef != null){
                    mRef.get().runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            callback.doWorkWhenTimeUp(tag);
                        }
                    });
                }
            }
        },0,100);
        return instance;
    }

    /**重新倒计时*/
    public void start(long period){
        goal = t + period;
    }

    /**停止任务*/
    public void cancel(){
        goal = t - 1;
    }


    public interface CountDownCallback{
        void doWorkWhenTimeUp(int tag);
    }
}
