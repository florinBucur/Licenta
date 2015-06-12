package notifications;

import android.app.Notification;
import android.os.Bundle;
import android.service.notification.NotificationListenerService;
import android.service.notification.StatusBarNotification;
import android.support.v4.app.NotificationCompat;
import android.util.Log;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import de.greenrobot.event.EventBus;

/**
 * Created by bucur on 6/3/2015.
 */

public class NotificationReceiver extends NotificationListenerService {
    @Override
    public void onNotificationPosted(StatusBarNotification statusBarNotification) {
        //super.onNotificationPosted(statusBarNotification);

        if(!statusBarNotification.isOngoing()) {
            //As we want to ignore ongoing notifications
            EventBus.getDefault().post(extractWearNotification(statusBarNotification));
        }
    }

    @Override
    public void onNotificationRemoved(StatusBarNotification sbn) {
        super.onNotificationRemoved(sbn);
        //TODO remove notification from stack in Activity
    }

    /**
     * To extract WearNotification with RemoteInputs that we can use to respond later on
     * @param statusBarNotification
     * @return
     */
    private NotificationWear extractWearNotification(StatusBarNotification statusBarNotification) {
        //Should work for communicators such:"com.whatsapp", "com.facebook.orca", "com.google.android.talk", "jp.naver.line.android", "org.telegram.messenger"
        NotificationWear notificationWear = new NotificationWear();
        notificationWear.packageName = statusBarNotification.getPackageName();
        Log.d("Status", statusBarNotification.toString());

        NotificationCompat.WearableExtender wearableExtender = new NotificationCompat.WearableExtender(statusBarNotification.getNotification());
        List<NotificationCompat.Action> actions = wearableExtender.getActions();
        for(NotificationCompat.Action act : actions) {
            if(act != null && act.getRemoteInputs() != null) {
                notificationWear.remoteInputs.addAll(Arrays.asList(act.getRemoteInputs()));
            }
        }
        List<Notification> pages = wearableExtender.getPages();

        notificationWear.pages.addAll(pages);

        notificationWear.bundle = statusBarNotification.getNotification().extras;
        notificationWear.tag = statusBarNotification.getTag();
        notificationWear.pendingIntent = statusBarNotification.getNotification().contentIntent;

        return notificationWear;
    }

    /**
     * Sample of how it is possible to do manually without using NotificationCompat.WearableExtender constructor
     * @param statusBarNotification
     * @return
     */
    private NotificationWear extractOldWearNotification(StatusBarNotification statusBarNotification) {

        NotificationWear notificationWear = new NotificationWear();

        Bundle bundle = statusBarNotification.getNotification().extras;
        for (String key : bundle.keySet()) {
            Object value = bundle.get(key);

            if("android.wearable.EXTENSIONS".equals(key)){
                Bundle wearBundle = ((Bundle) value);
                for (String keyInner : wearBundle.keySet()) {
                    Object valueInner = wearBundle.get(keyInner);

                    if(keyInner != null && valueInner != null){
                        if("actions".equals(keyInner) && valueInner instanceof ArrayList){
                            ArrayList<Notification.Action> actions = new ArrayList<>();
                            actions.addAll((ArrayList) valueInner);
                            for(Notification.Action act : actions) {
                                if (act.getRemoteInputs() != null) {//API > 20 needed
                                    android.app.RemoteInput[] remoteInputs = act.getRemoteInputs();
                                }
                            }
                            //get remote inputs and save them to notificationWear... long spaghetti code
                        }
                    }
                }
            }
        }
        return notificationWear;
    }
}
