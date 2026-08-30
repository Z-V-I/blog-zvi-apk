package onl.zvi.blog.app;

import android.app.Activity;
import android.content.ComponentName;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;

import androidx.browser.customtabs.CustomTabsClient;
import androidx.browser.customtabs.CustomTabsServiceConnection;
import androidx.browser.customtabs.CustomTabsSession;
import androidx.browser.customtabs.CustomTabsIntent;

public class LauncherActivity extends Activity {

    public static final String TAG = "LauncherActivity";

    public static final String HOST = "blog.zvi.onl";
    public static final String START_URL = "https://blog.zvi.onl/";
    public static final String ORIGIN = "https://blog.zvi.onl";

    private CustomTabsServiceConnection connection;
    private boolean launched = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        launchTwa();
    }

    private void launchTwa() {
        if (launched) return;
        launched = true;

        // 尝试以 TWA 方式启动：连接 Chrome 的 CustomTabs 服务
        connection = new CustomTabsServiceConnection() {
            @Override
            public void onCustomTabsServiceConnected(ComponentName name, CustomTabsClient client) {
                client.warmup(0L);
                CustomTabsSession session = client.newSession(null);
                CustomTabsIntent.Builder builder = new CustomTabsIntent.Builder(session);
                builder.setShowTitle(false);
                builder.setStartAnimations(LauncherActivity.this, android.R.anim.fade_in, android.R.anim.fade_out);
                builder.setExitAnimations(LauncherActivity.this, android.R.anim.fade_in, android.R.anim.fade_out);
                CustomTabsIntent intent = builder.build();
                intent.intent.setPackage(name.getPackageName());
                intent.launchUrl(LauncherActivity.this, Uri.parse(START_URL));
                finish();
            }

            @Override
            public void onCustomTabsServiceDisconnected(ComponentName name) {
            }
        };

        // 绑定 Chrome（TWA 需要 Chrome 支持数字资产链接）
        if (!CustomTabsClient.bindCustomTabsService(this, "com.android.chrome", connection)) {
            // Chrome 不可用时回退到系统浏览器
            fallbackToBrowser();
        }

        // 超时兜底：若 5 秒内未连接，回退系统浏览器
        new Handler(Looper.getMainLooper()).postDelayed(() -> {
            if (!isFinishing() && connection != null) {
                try {
                    unbindService(connection);
                } catch (Exception ignored) {
                }
                fallbackToBrowser();
            }
        }, 5000);
    }

    private void fallbackToBrowser() {
        if (isFinishing()) return;
        Intent i = new Intent(Intent.ACTION_VIEW, Uri.parse(START_URL));
        startActivity(i);
        finish();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (connection != null) {
            try {
                unbindService(connection);
            } catch (Exception ignored) {
            }
        }
    }
}
