package cn.fengmang.file.service;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.support.annotation.Nullable;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import cn.fengmang.baselib.ELog;

/**
 * Created by Administrator on 2018/8/17.
 */

public class TaskService extends Service {

    private static TaskService instance;

    public static TaskService getInstance() {
        return instance;
    }

    private TaskConsumer taskConsumer;
    private TaskProducer taskProducer;

    @Override
    public void onCreate() {
        super.onCreate();
        instance = this;
        ExecutorService service = Executors.newFixedThreadPool(3);
        ArrayBlockingQueue<Task> taskQueue = new ArrayBlockingQueue<Task>(6);
        taskProducer = new TaskProducer(taskQueue);
        taskConsumer = new TaskConsumer(taskQueue, service);
        new Thread(taskConsumer).start();
        new Thread(taskProducer).start();
        ELog.d("onCreate");
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        ELog.d("onStartCommand");
        return super.onStartCommand(intent, flags, startId);
    }


    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        taskProducer.stop();
        taskConsumer.stop();
    }

    public void addTask(Task task) {
        taskProducer.addTask(task);
    }


    //创建线程
    static class TaskProducer implements Runnable {

        private boolean isRunning;

        ArrayBlockingQueue<Task> taskQueue;

        List<Task> taskList;

        public TaskProducer(ArrayBlockingQueue<Task> taskQueue) {
            isRunning = true;
            this.taskQueue = taskQueue;
            taskList = new ArrayList<>();
        }

        public void addTask(Task task) {
            synchronized (taskList) {
                taskList.add(task);
                taskList.notify();
            }
        }

        @Override
        public void run() {
            try {
                synchronized (taskList) {
                    while (isRunning) {
                        if (taskList.size() == 0) {
                            ELog.e("thread wait");
                            taskList.wait();
                        }
                        Task task = taskList.remove(0);
                        taskQueue.put(task);
                        ELog.e("thread wakeup");
                    }
                }
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }

        public void stop() {
            isRunning = false;
        }
    }

    //处理线程
    static class TaskConsumer implements Runnable {

        private boolean isRunning;
        ArrayBlockingQueue<Task> taskQueue;
        ExecutorService service;

        public TaskConsumer(ArrayBlockingQueue<Task> taskQueue, ExecutorService service) {
            isRunning = true;
            this.taskQueue = taskQueue;
            this.service = service;
        }

        @Override
        public void run() {
            try {
                while (isRunning) {
                    Task task = taskQueue.take();
                    service.execute(task);
                }
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }

        public void stop() {
            isRunning = false;
        }
    }


    public static abstract class Task implements Runnable {

        protected int tid;

        public int getTid() {
            return tid;
        }

    }
}
