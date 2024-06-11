package com.example.musicapp.service;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

public class MyReceiver extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {
        String action = intent.getAction();
        if (action != null) {
            Intent serviceIntent = new Intent(context, MyService.class);
            switch (action) {
                case "ACTION_PREV":
                    // Handle previous action
                    context.startService(serviceIntent.putExtra("action_music_service", 2));
                    break;
                case "ACTION_PLAY_PAUSE":
                    // Handle play/pause action
                    context.startService(serviceIntent.putExtra("action_music_service", 1));
                    break;
                case "ACTION_NEXT":
                    // Handle next action
                    context.startService(serviceIntent.putExtra("action_music_service", 3));
                    break;
            }
        }
    }
}

