package com.example.floatplayer;

import android.app.job.JobParameters;
import android.app.job.JobService;
import android.util.Log;

/**
 * 测试用JobService
 */
public class PlayerJobService extends JobService {

    @Override
    public boolean onStartJob(JobParameters params) {
        Log.i("zhao", "JobService onStartJob");
        //耗时操作返回true，并且手动关闭
        return false;
    }

    @Override
    public boolean onStopJob(JobParameters params) {
        Log.i("zhao", "JobService onStopJob");
        return false;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        Log.i("zhao", "JobService onCreate");
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        Log.i("zhao", "JobService onDestroy");
    }
}
