package com.psj.welfare.hashkey;

import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.Signature;

import android.util.Base64;
import android.util.Log;


import androidx.appcompat.app.AppCompatActivity;



import java.security.MessageDigest;

public class HashKeyKakao extends AppCompatActivity {



	private void getAppKeyHash() {
		try {

			PackageInfo info = getPackageManager().getPackageInfo(getPackageName(), PackageManager.GET_SIGNATURES);
			for (Signature signature : info.signatures) {
				MessageDigest md;
				md = MessageDigest.getInstance("SHA");
				md.update(signature.toByteArray());
				String something = new String(Base64.encode(md.digest(), 0));
				Log.e("Hash key", something);
			}
		} catch (Exception e) {
			// TODO Auto-generated catch block
			Log.e("name not found", e.toString());
		}
	} // getAppKeyHash end
}
