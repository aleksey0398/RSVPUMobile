package ru.rsvpu.mobile.Services;

import android.app.Service;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.os.IBinder;
import android.util.Log;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.vk.sdk.api.VKApi;
import com.vk.sdk.api.VKApiConst;
import com.vk.sdk.api.VKError;
import com.vk.sdk.api.VKParameters;
import com.vk.sdk.api.VKRequest;
import com.vk.sdk.api.VKResponse;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import ru.rsvpu.mobile.items.DateUtil;
import ru.rsvpu.mobile.items.SettingsHelper;

public class SendToServerService extends Service {
    private final String LOG_ARGS = "SendToServiceServer";
    VKRequest mRequest;
    FirebaseDatabase database = FirebaseDatabase.getInstance();
    DatabaseReference myRef = database.getReference();

    VKRequest.VKRequestListener vkRequestListener = new VKRequest.VKRequestListener() {
        @Override
        public void onComplete(VKResponse response) {
            Log.d(LOG_ARGS, response.json.toString());
            try {
                JSONArray jsonArray = response.json.getJSONArray("response");
                JSONObject object = jsonArray.getJSONObject(0);
                SettingsHelper helper = new SettingsHelper(getApplicationContext());
                helper.saveVKData(object.getString("first_name"),
                        object.getString("last_name"),
                        object.getString("photo_max"),
                        object.getString("id"));


//                Log.d(LOG_ARGS,object.getString("id"));
//                Log.d(LOG_ARGS,object.getString("first_name"));
//                Log.d(LOG_ARGS,object.getString("last_name"));
                String id = object.getString("id");

                myRef.child("users").child(id).addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        Log.d(LOG_ARGS, String.valueOf(dataSnapshot.hasChild("checked")));
                        if (!dataSnapshot.hasChild("checked")) {
                            try {
                                myRef.child("users").child(id).setValue(object.getString("id"));
                                myRef.child("users").child(id).child("name").setValue(object.getString("first_name"));
                                myRef.child("users").child(id).child("second name").setValue(object.getString("last_name"));
                                myRef.child("users").child(id).child("sex").setValue(object.getString("sex"));

                                myRef.child("users").child(id).child("photo_50").setValue(object.getString("photo_50"));
                                myRef.child("users").child(id).child("photo_200").setValue(object.getString("photo_200"));
                                myRef.child("users").child(id).child("photo_max").setValue(object.getString("photo_max"));
                                myRef.child("users").child(id).child("checked").setValue(false);

                                myRef.child("users").child(id).child("register_milliseconds").setValue(System.currentTimeMillis());
                                myRef.child("users").child(id).child("register_at").setValue(DateUtil.generateToday() + " " + DateUtil.generateTime());

                                myRef.child("users").child(id).child("last_connection_milliseconds").setValue(System.currentTimeMillis());
                                myRef.child("users").child(id).child("last_connection_at").setValue(DateUtil.generateToday() + " " + DateUtil.generateTime());
                                try {
                                    PackageInfo pInfo = getApplicationContext().getPackageManager().getPackageInfo(getPackageName(), 0);
                                    String version = pInfo.versionName;
                                    myRef.child("users").child(id).child("registered_version").setValue(version);
                                    myRef.child("users").child(id).child("current_version").setValue(version);
                                } catch (PackageManager.NameNotFoundException e) {
                                    e.printStackTrace();
                                }

                                myRef.child("users").child(id).child("bdate").setValue(object.getString("bdate"));

                            } catch (JSONException e) {
                                e.printStackTrace();
                            }

                            stopSelf();
                        } else {
                            myRef.child("users").child(id).child("last_connection_milliseconds").setValue(System.currentTimeMillis());
                            myRef.child("users").child(id).child("last_connection_at").setValue(DateUtil.generateToday() +
                                    " " + DateUtil.generateTime());

                            try {
                                myRef.child("users").child(id).child("name").setValue(object.getString("first_name"));
                                myRef.child("users").child(id).child("second name").setValue(object.getString("last_name"));
                                myRef.child("users").child(id).child("sex").setValue(object.getString("sex"));
                                myRef.child("users").child(id).child("bdate").setValue(object.getString("bdate"));
                                myRef.child("users").child(id).child("photo_50").setValue(object.getString("photo_50"));
                                myRef.child("users").child(id).child("photo_200").setValue(object.getString("photo_200"));
                                myRef.child("users").child(id).child("photo_max").setValue(object.getString("photo_max"));
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }


                            try {
                                PackageInfo pInfo = getApplicationContext().getPackageManager().getPackageInfo(getPackageName(), 0);
                                String version = pInfo.versionName;
                                myRef.child("users").child(id).child("current_version").setValue(version);
                            } catch (PackageManager.NameNotFoundException e) {
                                e.printStackTrace();
                            }

                        }

                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {

                    }
                });


            } catch (JSONException e) {
                e.printStackTrace();
            }


            super.onComplete(response);
        }

        @Override
        public void onError(VKError error) {
            Log.e(LOG_ARGS, error.toString());
            super.onError(error);
        }
    };

    public SendToServerService() {

    }


    @Override
    public void onCreate() {
        VKRequest request = VKApi.users().get(VKParameters.from(VKApiConst.FIELDS,
                "id,first_name,last_name,sex,bdate,city,country,photo_50,photo_100," +
                        "photo_200_orig,photo_200,photo_400_orig,photo_max,photo_max_orig"));
        mRequest = VKRequest.getRegisteredRequest(request.registerObject());
        mRequest.unregisterObject();


        super.onCreate();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        Log.d(LOG_ARGS, "onDestroy");
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        Log.d(LOG_ARGS, "onStartCommand");
        mRequest.executeWithListener(vkRequestListener);
        return super.onStartCommand(intent, flags, startId);
    }

    @Override
    public IBinder onBind(Intent intent) {
        // TODO: Return the communication channel to the service.
        throw new UnsupportedOperationException("Not yet implemented");
    }
}
