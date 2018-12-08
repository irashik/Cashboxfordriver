package irashindmitrii.cashboxfordriver;


import android.content.pm.PackageManager;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.widget.TextView;
import com.crashlytics.android.Crashlytics;
import io.fabric.sdk.android.Fabric;

/**
 *
 * Created by dmitrii on 19.07.17.
 * Показывает диалоговое окно с информацие о программе
 * Стиль установлен в манифест файле.
 */

public class AboutActivity extends AppCompatActivity {

    TextView textVersion;
    private SQLiteDatabase db;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.about);
        Fabric.with(this, new Crashlytics());
        textVersion = (TextView) findViewById(R.id.about_version);

        // нужна обертка для метода getPackageInfo
        try {
            String versionName = getPackageManager().getPackageInfo(
                    this.getPackageName(), 0).versionName;
            textVersion.setText(getString(R.string.about_ver) + versionName);
        } catch (PackageManager.NameNotFoundException e) {
           // можно коченоч записать в лог
            Log.e("error PackageManager", e.toString());
        }
    }
}
