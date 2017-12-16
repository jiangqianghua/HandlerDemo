package com.jiang.cn.handlerutils;

public class Looper {

    static final ThreadLocal<Looper> sThreadLocal = new ThreadLocal<Looper>();

    public MessageQueue mQueue;

    public Looper() {
        mQueue = new MessageQueue();
    }

    public static void perpare(){
        if(sThreadLocal.get() != null){
            throw new RuntimeException("only one looper may be create per thread");
        }
        sThreadLocal.set(new Looper());
    }

    public static Looper myLooper(){
        return sThreadLocal.get();
    }

    public static void loop(){
        Looper me = myLooper();
        MessageQueue queue = me.mQueue;
        Message msg ;
        for(;;){
            msg = queue.next();
            if(msg == null || msg.target == null){
                continue;
            }
            msg.target.dispatchMessage(msg);
        }
    }


}
