package com.utilfreedom.brainmath;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

/**
 * Created by kennywang on 8/2/17.
 */

public class Config {
    public static final DatabaseReference DB_BASE = FirebaseDatabase.getInstance().getReference();
}
