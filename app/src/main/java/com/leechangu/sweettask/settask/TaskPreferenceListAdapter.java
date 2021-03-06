package com.leechangu.sweettask.settask;

import android.content.Context;
import android.database.Cursor;
import android.media.Ringtone;
import android.media.RingtoneManager;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.CheckedTextView;
import android.widget.TextView;

//import com.leechangu.sweettask.TaskItem;

import com.leechangu.sweettask.ParseTaskItem;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Administrator on 2015/10/14.
 */
public class TaskPreferenceListAdapter  extends BaseAdapter {
    ParseTaskItem parseTaskItem;
    private Context context;
    private String[] alarmTones;
    private String[] alarmTonePaths;
    private List<TaskPreference> preferences = new ArrayList<TaskPreference>();
    private final String[] timeBasisOptions = {ParseTaskItem.TimeBasisEnum.DAILY.toString(),ParseTaskItem.TimeBasisEnum.WEEKLY.toString(),ParseTaskItem.TimeBasisEnum.MONTHLY.toString()};

    public TaskPreferenceListAdapter(Context context, ParseTaskItem parseTaskItem) {
        setContext(context);
        loadAlarmTones();
        DisplayPreferences(parseTaskItem);
    }

    private void loadAlarmTones()
    {
        RingtoneManager ringtoneMgr = new RingtoneManager(getContext());
        ringtoneMgr.setType(RingtoneManager.TYPE_ALARM);
        Cursor alarmsCursor = ringtoneMgr.getCursor();
        alarmTones = new String[alarmsCursor.getCount()+1];
        alarmTones[0] = "Silent";
        alarmTonePaths = new String[alarmsCursor.getCount()+1];
        alarmTonePaths[0] = "";
        if (alarmsCursor.moveToFirst()) {
            do {
                alarmTones[alarmsCursor.getPosition()+1] = ringtoneMgr.getRingtone(alarmsCursor.getPosition()).getTitle(getContext());
                alarmTonePaths[alarmsCursor.getPosition()+1] = ringtoneMgr.getRingtoneUri(alarmsCursor.getPosition()).toString();
            }while(alarmsCursor.moveToNext());
        }
        alarmsCursor.close();
    }

    public void DisplayPreferences(ParseTaskItem parseTaskItem) {
        this.parseTaskItem = parseTaskItem;
        preferences.clear();
        preferences.add(new TaskPreference(TaskPreference.Key.TASK_ACTIVE, "Active", null, null, parseTaskItem.isActive(), TaskPreference.Type.BOOLEAN));
        preferences.add(new TaskPreference(TaskPreference.Key.TASK_CONTENT, "Content", parseTaskItem.getContent(), null, parseTaskItem.getContent(), TaskPreference.Type.STRING));
        preferences.add(new TaskPreference(TaskPreference.Key.TASK_REPEAT, "Repeat",parseTaskItem.getTimeBasisEnum().toString(), timeBasisOptions, parseTaskItem.getTimeBasisEnum(), TaskPreference.Type.LIST));
        preferences.add(new TaskPreference(TaskPreference.Key.TASK_VIBRATE, "Vibrate",null, null, parseTaskItem.isVibrate(), TaskPreference.Type.BOOLEAN));

        Uri alarmToneUri = Uri.parse(parseTaskItem.getAlarmTonePath());
        Ringtone alarmTone = RingtoneManager.getRingtone(getContext(), alarmToneUri);

        if(alarmTone instanceof Ringtone && !parseTaskItem.getAlarmTonePath().equalsIgnoreCase("")){
            preferences.add(new TaskPreference(TaskPreference.Key.TASK_TONE, "Ringtone", alarmTone.getTitle(getContext()),alarmTones, parseTaskItem.getAlarmTonePath(), TaskPreference.Type.LIST));
        }else{
            preferences.add(new TaskPreference(TaskPreference.Key.TASK_TONE, "Ringtone", getAlarmTones()[0],alarmTones, null, TaskPreference.Type.LIST));
        }
        preferences.add(new TaskPreference(TaskPreference.Key.TASK_MAP, "Map Task", parseTaskItem.getMapInfo(), null, parseTaskItem.getMapInfo(), TaskPreference.Type.MAP));
        preferences.add(new TaskPreference(TaskPreference.Key.TASK_PHOTO, "Photo Task", null, null, parseTaskItem.isPhotoTask(), TaskPreference.Type.BOOLEAN));

    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        TaskPreference taskPreference = (TaskPreference) getItem(position);
        LayoutInflater layoutInflater = LayoutInflater.from(getContext());
        switch (taskPreference.getType()) {
            case BOOLEAN:
                //if(null == convertView || convertView.getId() != android.R.layout.simple_list_item_checked)
                convertView = layoutInflater.inflate(android.R.layout.simple_list_item_checked, null);
                CheckedTextView checkedTextView = (CheckedTextView) convertView.findViewById(android.R.id.text1);
                checkedTextView.setText(taskPreference.getTitle());
                checkedTextView.setChecked((Boolean) taskPreference.getValue());
                break;
            case STRING:
            case LIST:
            case MAP:
            default:
                //if(null == convertView || convertView.getId() != android.R.layout.simple_list_item_2)
                convertView = layoutInflater.inflate(android.R.layout.simple_list_item_2, null);

                TextView text1 = (TextView) convertView.findViewById(android.R.id.text1);
                text1.setTextSize(18);
                text1.setText(taskPreference.getTitle());

                TextView text2 = (TextView) convertView.findViewById(android.R.id.text2);
                text2.setText(taskPreference.getSummary());
                break;
        }
        return convertView;
    }


    public void setContext(Context context) {
        this.context = context;
    }

    public Context getContext() {
        return context;
    }

    @Override
    public int getCount() { return preferences.size(); }

    @Override
    public Object getItem(int position) {
        return preferences.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }


    public String[] getAlarmTones() {
        return alarmTones;
    }

    public String[] getAlarmTonePaths() {
        return alarmTonePaths;
    }
}
