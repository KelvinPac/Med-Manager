package com.homeautogroup.med_manager.fragments;

import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.homeautogroup.med_manager.R;
import com.homeautogroup.med_manager.activities.AddEditAlarmActivity;
import com.homeautogroup.med_manager.adapters.AlarmsAdapter;
import com.homeautogroup.med_manager.models.Alarm;
import com.homeautogroup.med_manager.service.LoadAlarmsReceiver;
import com.homeautogroup.med_manager.service.LoadAlarmsService;
import com.homeautogroup.med_manager.utils.AlarmUtils;
import com.homeautogroup.med_manager.view.DividerItemDecoration;
import com.homeautogroup.med_manager.view.EmptyRecyclerView;

import java.util.ArrayList;

public final class AlarmsFragment extends Fragment
        implements LoadAlarmsReceiver.OnAlarmsLoadedListener {

    private LoadAlarmsReceiver mReceiver;
    private AlarmsAdapter mAdapter;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mReceiver = new LoadAlarmsReceiver(this);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        final View v = inflater.inflate(R.layout.fragment_add_alarms, container, false);

        final EmptyRecyclerView rv = (EmptyRecyclerView) v.findViewById(R.id.recycler);
        mAdapter = new AlarmsAdapter();
        rv.setEmptyView(v.findViewById(R.id.empty_view));
        rv.setAdapter(mAdapter);
        rv.addItemDecoration(new DividerItemDecoration(getContext()));
        rv.setLayoutManager(new LinearLayoutManager(getContext()));
        rv.setItemAnimator(new DefaultItemAnimator());

        final FloatingActionButton fab = (FloatingActionButton) v.findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                AlarmUtils.checkAlarmPermissions(getActivity());
                final Intent i =
                        AddEditAlarmActivity.buildAddEditAlarmActivityIntent(
                                getContext(), AddEditAlarmActivity.ADD_ALARM
                        );
                startActivity(i);
            }
        });

        return v;

    }

    @Override
    public void onStart() {
        super.onStart();
        final IntentFilter filter = new IntentFilter(LoadAlarmsService.ACTION_COMPLETE);
        LocalBroadcastManager.getInstance(getContext()).registerReceiver(mReceiver, filter);
        LoadAlarmsService.launchLoadAlarmsService(getContext());
    }

    @Override
    public void onStop() {
        super.onStop();
        LocalBroadcastManager.getInstance(getContext()).unregisterReceiver(mReceiver);
    }

    @Override
    public void onAlarmsLoaded(ArrayList<Alarm> alarms) {
        mAdapter.setAlarms(alarms);
    }

}
