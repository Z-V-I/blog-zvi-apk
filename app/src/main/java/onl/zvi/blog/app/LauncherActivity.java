package onl.zvi.blog.app;

import android.app.Activity;
import android.content.ActivityNotFoundException;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;

import androidx.browser.customtabs.CustomTabsIntent;

public class LauncherActivity extends Activity {

    public static final String START_URL = "https://blog.zvi.onl/";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        launchTwa();
    }

    private void launchTwa() {
        try {
            // TWA 方式：Chrome 会通过 assetlinks.json 验证本应用
            // 验证通过 -> 全屏无浏览器UI；验证失败 -> 以 Custom Tabs 形式打开
            CustomTabsIntent.Builder builder = new CustomTabsIntent.Builder();
            builder.setShowTitle(false);
            builder.setStartAnimations(this, android.R.anim.fade_in, android.R.anim.fade_out);
            builder.setExitAnimations(this, android.R.anim.fade_in, android.R.anim.fade_out);
            CustomTabsIntent intent = builder.build();
            intent.intent.setPackage("com.android.chrome");
            intent.launchUrl(this, Uri.parse(START_URL));
        } catch (ActivityNotFoundException e) {
            // Chrome 未安装时回退系统浏览器
            Intent fallback = new Intent(Intent.ACTION_VIEW, Uri.parse(START_URL));
            startActivity(fallback);
        }
        finish();
    }
}
