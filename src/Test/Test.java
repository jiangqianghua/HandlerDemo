package Test;

import java.util.UUID;

import com.jiang.cn.handlerutils.*;

public class Test {

    public static void main(String args[]){
        Looper.perpare();
        final Handler handler = new Handler(){

            @Override
            public void handleMessage(Message message) {
                System.out.println("main thread[" + Thread.currentThread().getName() + "] msg = "+message.obj.toString());
            }
        };

        for(int i = 0 ; i < 10 ; i++){
            new Thread(new Runnable() {
                @Override
                public void run() {
                    Message msg = new Message();
                    synchronized (UUID.class){
                        msg.obj = UUID.randomUUID().toString();
                    }
                    System.out.println("sup thread " + Thread.currentThread().getName() + ": send message------" + msg.obj);
                    handler.sendMessage(msg);
                }
            }).start();
        }
        Looper.loop();
    }
}
