package exam.administrator.word1026;

import android.app.Application;
import android.content.Context;

/**
 * Created by hbs on 2015-10-24.
 */
public class WordsApplication extends Application{
    private static Context context;
    public static Context getContext(){
        return WordsApplication.context;
    }


    @Override
    public void onCreate() {
        super.onCreate();
        WordsApplication.context=getApplicationContext();
    }
}
