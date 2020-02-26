package com.techyourchance.multithreading.demonstrations.atomicity;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import com.techyourchance.multithreading.R;
import com.techyourchance.multithreading.common.BaseFragment;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;


@SuppressLint("SetTextI18n")
public class AtomicityDemonstrationFragment extends BaseFragment {

    private static final int COUNT_UP_TO = 1000;
    private static final int NUM_OF_COUNTER_THREADS = 100;

    public static Fragment newInstance() {
        return new AtomicityDemonstrationFragment();
    }

    private Button mBtnStartCount;
    private TextView mTxtFinalCount;

    private Handler mUiHandler = new Handler(Looper.getMainLooper());

    private volatile int mCount;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_atomicity_demonstration, container, false);

        mTxtFinalCount = view.findViewById(R.id.txt_final_count);

        mBtnStartCount = view.findViewById(R.id.btn_start_count);
        mBtnStartCount.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startCount();
            }
        });

        return view;
    }

    @Override
    protected String getScreenTitle() {
        return "";
    }

    @Override
    public void onStart() {
        super.onStart();
    }

    @Override
    public void onStop() {
        super.onStop();
    }

    private void startCount() {
        mCount = 0;
        mTxtFinalCount.setText("");
        mBtnStartCount.setEnabled(false);

        for (int i = 0; i < NUM_OF_COUNTER_THREADS; i++) {
            startCountThread();
        }

        mUiHandler.postDelayed(new Runnable() {
            @Override
            public void run() {
                mTxtFinalCount.setText(String.valueOf(mCount));
                mBtnStartCount.setEnabled(true);
            }
        }, NUM_OF_COUNTER_THREADS * 20);
    }

    private void startCountThread() {
        new Thread(new Runnable() {
            @Override
            public void run() {
                for (int i = 0; i < COUNT_UP_TO; i++) {

                    //Whenever a single thread performs read modify and write operation , no other thread should
                    //be allowed to use these variables.Or else atomicity problem will happen.
                    //Race condition is also same

                    //Android follows Preemptive multitasking , so below the problem is suppose we assign mCount
                    // to localCount then OS decides to pause right there and jump to a different thread for execution.Now
                    //what will happen is two threads has same value of localCount but that should be not the case.
                    //now the 2nd thread will assign the value of localhost + 1,and the same will happen in 1st thread
                    //So we are missing count by 1 in 2 threads.
                    //This will happen more often in slow system

                    //U will notice that the right count should be 10000 but at times u will get less than that,if u run
                    //it multiple times

                    //How we can fix this ?
                    //Check Solution

                    int localCount = mCount;
                    mCount = localCount + 1;
                    //mCount++;   This code is same as above
                }
            }
        }).start();
    }

}
