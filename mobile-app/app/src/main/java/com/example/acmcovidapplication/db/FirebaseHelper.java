package com.example.acmcovidapplication.db;


import android.util.Log;

import com.example.acmcovidapplication.db.model.DeviceModel;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.GeoPoint;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import androidx.annotation.NonNull;

import static com.example.acmcovidapplication.services.CustomService.TAG;


public class FirebaseHelper {
    private FirebaseAuth firebaseAuth;
    private FirebaseFirestore db = FirebaseFirestore.getInstance();
    FirebaseUser firebaseUser = FirebaseAuth.getInstance().getCurrentUser();

    //add this when detected
    public void update(final DeviceModel deviceModel, final DatabaseHelper databaseHelper) {

        //timestamp format --> "2020-05-18 07:42:24"
        String uid = firebaseUser.getUid();
        String owner_id =  databaseHelper.getUserId();
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

        try {
            Date date = dateFormat.parse(deviceModel.getTimeStamp());
            Calendar calendar = Calendar.getInstance();
            calendar.setTime(date);
            calendar.add(Calendar.HOUR_OF_DAY,5);
            calendar.add(Calendar.MINUTE,30);
            date = calendar.getTime();
//            System.out.println(date);
            GeoPoint location = new GeoPoint(deviceModel.getLatitude(),deviceModel.getLongitude());

            Map<String, Object> data = new HashMap<>();
            data.put("timestamp", date);
            data.put("location",location);

            db.collection("users")
                    .document(uid)
                    .collection(deviceModel.getUserID())
                    .document()
                    .set(data)
                    .addOnSuccessListener(new OnSuccessListener<Void>() {
                        @Override
                        public void onSuccess(Void aVoid) {
                            Log.d(TAG, "onSuccess: " + deviceModel.getUserID());
                            databaseHelper.deleteDevice(deviceModel.getID());
                        }
                    })
                    .addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            Log.d(TAG, "onFailure: failed to upload id " + deviceModel.getUserID() +"" +
                                    "\nException - " + e.getMessage());
                        }
                    });
        } catch (ParseException e) {
            e.printStackTrace();
        }

    }

    // add this when creating a new user
    public void onCreteUser(String id){
        Map<String, Object> data = new HashMap<>();
        data.put("bluetooth id",id);
        firebaseUser = FirebaseAuth.getInstance().getCurrentUser();

        db.collection("users")
                .document(firebaseUser.getUid())
                .set(data)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        System.out.println("Success");
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        System.out.println("fail");
                    }
                });
    }


}
