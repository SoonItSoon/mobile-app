package com.app.soonitsoon;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.PowerManager;
import android.provider.Settings;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.app.soonitsoon.background.BackgroundService;
import com.app.soonitsoon.background.BootReceiver;
import com.app.soonitsoon.briefing.BriefingActivity;
import com.app.soonitsoon.interest.InterestActivity;
import com.app.soonitsoon.message.MessageActivity;
import com.app.soonitsoon.safety.SafetyActivity;
import com.app.soonitsoon.timeline.TimelineActivity;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.GoogleAuthProvider;

public class MainActivity extends AppCompatActivity {
    private final static String TAG = "MainActivity";
    private static final int RC_SIGN_IN = 9001;

    public static Activity activity;
    private Intent mBackgroundServiceIntent;
    private BackgroundService mBackgroundService;
    private GoogleSignInClient mGoogleSignInClient;
    private FirebaseAuth mAuth;

    private Context context = this;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        activity = this;

        // 위치 권한 허용 받기
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.Q) {
            if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                Log.e(TAG, "GPS OFF!");
                ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, 0);
            }
        }
        else {
            if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_BACKGROUND_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                Log.e(TAG, "GPS OFF!");
                ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_BACKGROUND_LOCATION}, 0);
            }
        }

        // 배터리 예외 권한이 있는지 확인
        if (PackageManager.PERMISSION_GRANTED != getApplication().getPackageManager()
                .checkPermission(android.Manifest.permission.REQUEST_IGNORE_BATTERY_OPTIMIZATIONS,
                        getApplication().getPackageName())) { // 권한 체크
            Log.d(TAG, "checkBatteryOptimization: application hasn't REQUEST_IGNORE_BATTERY_OPTIMIZATIONS permission");
        }

        // 배터리 최적화 예외가 되어 있지 않으면 세팅 화면 열기
        PowerManager powerManager = (PowerManager) getApplication().getSystemService(Context.POWER_SERVICE);
        boolean ignoringBatteryOptimizations = powerManager.isIgnoringBatteryOptimizations(this.getPackageName());
        if (!ignoringBatteryOptimizations) {
            Log.d(TAG, "checkBatteryOptimization: Not ignored Battery Optimizations.");

            Intent intent = new Intent(Settings.ACTION_REQUEST_IGNORE_BATTERY_OPTIMIZATIONS);
            intent.setData(Uri.parse(String.format("package:%s", this.getPackageName())));
            startActivity(intent);
        }

        // firebase
        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN).requestIdToken(getString(R.string.default_web_client_id)).requestEmail().build();
        mGoogleSignInClient = GoogleSignIn.getClient(this, gso);
        mAuth = FirebaseAuth.getInstance();

        // BackgroundService
        mBackgroundService = new BackgroundService(getApplicationContext());
        mBackgroundServiceIntent = new Intent(getApplicationContext(), mBackgroundService.getClass());
        // 서비스가 실행 중인지 확인
        if (!BootReceiver.isServiceRunning(this, mBackgroundService.getClass())) {
            // 서비스가 실행하고 있지 않는 경우 서비스 실행
            startService(mBackgroundServiceIntent);
        }

        // 화면 전환 버튼
        View mainBtnBriefing = findViewById(R.id.btn_home_briefing);
        mainBtnBriefing.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mAuth.getCurrentUser() == null) {
                    Toast.makeText(getApplicationContext(), "먼저 로그인을 해주세요.", Toast.LENGTH_SHORT).show();
                } else {
                    Intent intent = new Intent(getApplicationContext(), BriefingActivity.class);
                    startActivity(intent);
                }
            }
        });
        View mainBtnInterest = findViewById(R.id.btn_home_interest);
        mainBtnInterest.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mAuth.getCurrentUser() == null) {
                    Toast.makeText(getApplicationContext(), "먼저 로그인을 해주세요.", Toast.LENGTH_SHORT).show();
                } else {
                    Intent intent = new Intent(getApplicationContext(), InterestActivity.class);
                    startActivity(intent);
                }
            }
        });
        View mainBtnTimeline = findViewById(R.id.btn_home_timeline);
        mainBtnTimeline.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mAuth.getCurrentUser() == null) {
                    Toast.makeText(getApplicationContext(), "먼저 로그인을 해주세요.", Toast.LENGTH_SHORT).show();
                } else {
                    Intent intent = new Intent(getApplicationContext(), TimelineActivity.class);
                    startActivity(intent);
                }
            }
        });
        View mainBtnSafety = findViewById(R.id.btn_home_safety);
        mainBtnSafety.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mAuth.getCurrentUser() == null) {
                    Toast.makeText(getApplicationContext(), "먼저 로그인을 해주세요.", Toast.LENGTH_SHORT).show();
                } else {
                    Intent intent = new Intent(getApplicationContext(), SafetyActivity.class);
                    startActivity(intent);
                }
            }
        });
        View mainBtnMsg = findViewById(R.id.btn_home_message);
        mainBtnMsg.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mAuth.getCurrentUser() == null) {
                    Toast.makeText(getApplicationContext(), "먼저 로그인을 해주세요.", Toast.LENGTH_SHORT).show();
                } else {
                    Intent intent = new Intent(getApplicationContext(), MessageActivity.class);
                    startActivity(intent);
                }
            }
        });
    }

    @Override
    protected void onStart() {
        super.onStart();
        FirebaseUser currentUser = mAuth.getCurrentUser();
        updateUI(currentUser);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == RC_SIGN_IN) {
            Task<GoogleSignInAccount> task = GoogleSignIn.getSignedInAccountFromIntent(data);
            try {
                GoogleSignInAccount account = task.getResult(ApiException.class);
                Log.d(TAG, "firebaseAuthWithGoogle " + account.getId());
                firebaseAuthWithGoogle(account.getIdToken());
            } catch (ApiException e) {
                Log.w(TAG, "Google sign in failed", e);
            }
        }
    }

    private void firebaseAuthWithGoogle(String idToken) {
        AuthCredential credential = GoogleAuthProvider.getCredential(idToken, null);
        mAuth.signInWithCredential(credential).addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                if (task.isSuccessful()) {
                    Log.d(TAG, "signInWithCredential:success");
                    FirebaseUser user = mAuth.getCurrentUser();
                    updateUI(user);
                } else {
                    Log.w(TAG, "signInWithCredential:failure", task.getException());
                    updateUI(null);
                }
            }
        });
    }

    private void signIn() {
        Intent signInIntent = mGoogleSignInClient.getSignInIntent();
        startActivityForResult(signInIntent, RC_SIGN_IN);
    }

    private void updateUI(FirebaseUser account) {
        TextView displayName = findViewById(R.id.text_home_displayName);
        if (account == null) {
            Toast.makeText(getApplicationContext(), "계정 연동이 되어있지 않습니다.", Toast.LENGTH_SHORT).show();
            displayName.setText("로그인");
            displayName.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    signIn();
                }
            });
        } else {
            Toast.makeText(getApplicationContext(), account.getDisplayName()+"님 반갑습니다!", Toast.LENGTH_SHORT).show();
            displayName.setText(account.getDisplayName());
            displayName.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    FirebaseAuth.getInstance().signOut();
                    updateUI(null);
                }
            });
        }

    }
}
