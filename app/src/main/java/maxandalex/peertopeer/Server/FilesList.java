package maxandalex.peertopeer.Server;

import android.os.Environment;
import android.os.FileObserver;
import com.google.gson.Gson;

import org.greenrobot.eventbus.EventBus;

import java.io.File;

public class FilesList extends FileObserver{

    //EventBus.getDefault().register(this);
    private EventBus eventBus;
    // Constructeur
    public FilesList() {
        super(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS).getPath());
        eventBus = new EventBus();
    }

    @Override
    public void onEvent(int i, String context){
        if (i == CREATE){
            eventBus.post("add" + context);
        }
        else if(i == DELETE){
            eventBus.post("delete" + context);
        }
    }
    public void subscribe(Object subject){
        eventBus.register(subject);
    }

    public static File[] getFileList(){
        File[] files = Environment.getExternalStoragePublicDirectory((Environment.DIRECTORY_DOWNLOADS)).listFiles();
        return files;
    }

    public static File getFile(int i){
        String path = FilesList.getFileList()[i].getPath();
        return new File(path);
    }

    public static String listToJson(){
        File[] files = getFileList();
        return new Gson().toJson(files);
    }


}
