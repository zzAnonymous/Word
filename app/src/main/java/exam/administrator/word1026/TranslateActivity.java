package exam.administrator.word1026;

import android.annotation.TargetApi;
import android.content.Intent;
import android.os.Build;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;

/**
 * Created by Administrator on 2017/11/2.
 */
public class TranslateActivity extends AppCompatActivity {

    private String transContent;
    private String apiUrl = "http://fanyi.youdao.com/openapi.do?keyfrom=pdblog&key=993123434&type=data&doctype=json&version=1.1&q=";
    private EditText editText;
    private Button button;
    private Button btn_addtoword;
    private TextView textView;
    private String tvMsg=null;

    private Handler handler = new Handler(){
        public void handleMessage(Message msg){
            if (msg.what==0){
                String responses =(String) msg.obj;
                textView.setText(responses);
            }
        }
    };
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_translate);
        initView();
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(view.getId()==R.id.id_transButton){
                    sendHttpURLConnection();

                }
            }
        });
        btn_addtoword.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String strWord=editText.getText().toString();
                String strMeaning =textView.getText().toString();

                WordsDB wordsDB=WordsDB.getWordsDB();
                wordsDB.Insert(strWord, strMeaning, null);


            }
        });

        Intent intent = getIntent();
        String value = intent.getStringExtra("get_English");
        //将值设置到TextView中显示
        editText.setText(value);



    }


    private void initView() {
        editText=(EditText)findViewById(R.id.id_EditText);
        button=(Button)findViewById(R.id.id_transButton);
        textView=(TextView)findViewById(R.id.id_TextView);
        btn_addtoword= (Button) findViewById(R.id.btn_addtoword);
    }
    private void sendHttpURLConnection() {
        new Thread(new Runnable() {
            @TargetApi(Build.VERSION_CODES.KITKAT)
            @Override
            public void run() {
                HttpURLConnection connection = null;
                transContent=editText.getText().toString();
                if(transContent.equals("")||transContent==null)
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            Toast.makeText(TranslateActivity.this,"输入为空",Toast.LENGTH_SHORT).show();
                            return;
                        }
                    });

                try {
                    URL url = new URL(apiUrl+ URLEncoder.encode(transContent,"utf8"));
                    connection = (HttpURLConnection)url.openConnection();
                    connection.setRequestMethod("GET");
                    connection.connect();
                    InputStream in = connection.getInputStream();
                    BufferedReader reader = new BufferedReader(new InputStreamReader(in,"utf-8"));
                    StringBuilder response = new StringBuilder();
                    String line=null;
                    while ((line=reader.readLine())!=null){
                        response.append(line);

                    }
                    JSONObject transJSON = new JSONObject(response.toString());

                    String errorCode = transJSON.getString("errorCode");
                    if(errorCode.equals("0")){
                        String query = transJSON.getString("query");
                        JSONArray translation = transJSON.getJSONArray("translation");
                        JSONObject basic = transJSON.getJSONObject("basic");
                        JSONArray web =transJSON.getJSONArray("web");

                        JSONArray explains = basic.getJSONArray("explains");
                        tvMsg="原文："+query;
                        tvMsg+="\n翻译结果：";
                        String explainStr="\n\n释意：";
                        for(int j = 1,s=0;s<explains.length();s++,j++){
                            explainStr+="\n"+j+". "+explains.getString(s);
                        }
                        tvMsg+=explainStr;
                    }

                    Message message = new Message();
                    message.what = 0;
                    message.obj=tvMsg.toString();
                    handler.sendMessage(message);


                }   catch (Exception e) {
                    Log.e("errss", e.getMessage());

                }
            }
        }).start();
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_translate, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {


        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        switch (id) {

            case R.id.action_word:
                Intent intent = new Intent(TranslateActivity.this,MainActivity.class);
                startActivity(intent);
                return true;
            case R.id.action_news:
                Intent intentNews = new Intent(TranslateActivity.this,EnglishWeb.class);
                startActivity(intentNews);
                return true;
        }


        return super.onOptionsItemSelected(item);
    }
}
