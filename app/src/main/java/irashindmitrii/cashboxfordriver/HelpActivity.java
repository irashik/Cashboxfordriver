package irashindmitrii.cashboxfordriver;

import android.app.ProgressDialog;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

//todo заменить устаревшую библиотеку httpClient на  httpURLConnection
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.util.EntityUtils;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.List;



/**
 * Created by dmitrii on 19.07.17.
 *
 * Этот класс отображает активити с мануалом и реализует отправку сообщения разработчику
 *
 *


 */

public class HelpActivity extends AppCompatActivity {

    private EditText infotext;
    public static String URL = "http://irashin-dim.myjino.ru/post_date_receiver.php";


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.help);
        infotext = (EditText) findViewById(R.id.helpedit);
    }

    public void sendauthor(View view) {
            new AsyncTaskPayment().execute();

    }



    // inner class
    class AsyncTaskPayment extends AsyncTask<String, Void, Void> {

        private final HttpClient client = new DefaultHttpClient();
        private String Content;
        private ProgressDialog dialog = new ProgressDialog(HelpActivity.this);
        private List<NameValuePair> nameValuePairs;


        @Override
        protected void onPreExecute() {
            dialog.setMessage("Wait...");
            dialog.show();

            // подготовка к загрузке
            String nm = infotext.getText().toString();
            String nubmerver = "Number SDK: " + Build.VERSION.SDK_INT;
            String version = "Version Android: " + Build.VERSION.RELEASE;
            String device = "Android model: " + Build.MODEL;
            String brand = "Android brand: " + Build.BRAND;
            String product = "Android product: " + Build.PRODUCT;

            StringBuilder mText = new StringBuilder();
            DisplayMetrics metrics = new DisplayMetrics();
            getWindowManager().getDefaultDisplay().getMetrics(metrics);

            mText.append("Плотность пикселей на экране (dpi): " + metrics.densityDpi);
            mText.append("\n Высота экрана: " + metrics.heightPixels);
            mText.append("\n Ширина экрана: " + metrics.widthPixels);
            String completedString = mText.toString();


            // add your data
            nameValuePairs = new ArrayList<NameValuePair>(2);

            nameValuePairs.add(new BasicNameValuePair("display",  completedString));
            nameValuePairs.add(new BasicNameValuePair("sdk", nubmerver));
            nameValuePairs.add(new BasicNameValuePair("version", version));
            nameValuePairs.add(new BasicNameValuePair("device", device));
            nameValuePairs.add(new BasicNameValuePair("brand", brand));
            nameValuePairs.add(new BasicNameValuePair("product", product));
            nameValuePairs.add(new BasicNameValuePair("numberver", nubmerver));
            nameValuePairs.add(new BasicNameValuePair("message", nm));
        }

        @Override
        protected Void doInBackground(String... urls) {
            try {
                HttpPost httpPost = new HttpPost(URL);
                httpPost.setEntity(new UrlEncodedFormEntity(nameValuePairs, "utf-8"));

                // методы получения данных
                HttpResponse resp = client.execute(httpPost);
                HttpEntity resEntity = resp.getEntity();
                Content = EntityUtils.toString(resEntity).trim();

            } catch (UnsupportedEncodingException e) {
                e.printStackTrace();
            } catch (ClientProtocolException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }
            return null;
        }

        @Override
        protected void onPostExecute(Void result) {
            dialog.dismiss();
            Toast.makeText(getApplicationContext(), "Ваше сообщение уже отправлено.",
                    Toast.LENGTH_LONG).show();
            infotext.getText().clear();
        }
    }
}
