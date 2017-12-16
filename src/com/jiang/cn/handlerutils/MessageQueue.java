package com.jiang.cn.handlerutils;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

/**
 * 消息队列
 */
public class MessageQueue {

    Message[] mItems;

    int mPutIndex ;
    //消息队列的个数
    private int mCount ;
    //取消息队列的索引号
    private int mTakeIndex;

    Lock mLock;
    Condition mNotEmpty;
    Condition mNotFull ;

    public MessageQueue() {
        mItems = new Message[50];
        mLock = new ReentrantLock();
        mNotEmpty = mLock.newCondition();
        mNotFull = mLock.newCondition();
        mTakeIndex = 0 ;
        mPutIndex = 0 ;
    }

    /**
     * 主线程里面执行
     * @return
     */
    Message next(){
        Message msg = null;
        try{
            mLock.lock();
            System.out.println("next1 count="+mCount);
            while(mCount <= 0){
                mNotEmpty.await();
                System.out.println("消息队列为空，堵塞");
            }
            msg = mItems[mTakeIndex];
            mItems[mTakeIndex] = null ;
            mTakeIndex = (++mTakeIndex >= mItems.length)?0:mTakeIndex;
            mCount--;
            System.out.println("next2 count="+mCount);
            // 通知生产者
            mNotFull.signalAll();
        }catch (InterruptedException e){
            e.printStackTrace();
        }finally {
            mLock.unlock();
        }
        return msg ;
    }

    /**
     * 任意线程在执行
     * @param message
     */
    public void enqueueMessage(Message message){

        try{
            mLock.lock();
            while (mCount >= mItems.length){
                mNotFull.await();
                System.out.println("队列满了，堵塞");
            }

            mItems[mPutIndex] = message;
            mPutIndex = (++mPutIndex >= mItems.length)?0:mPutIndex;
            mCount++;
            System.out.println("enqueue count="+mCount);
            //通知消费者
            mNotEmpty.signalAll();
        }catch (InterruptedException e){
            e.printStackTrace();
        }finally {
            mLock.unlock();
        }

    }

}
