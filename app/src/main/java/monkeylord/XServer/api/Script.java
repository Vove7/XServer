package monkeylord.XServer.api;

import android.app.AndroidAppHelper;

import java.io.File;
import java.util.HashMap;
import java.util.Map;

import de.robv.android.xposed.XposedBridge;
import monkeylord.XServer.XServer;

/**
 * Created by Vove on 2020/11/22
 */
public class Script extends BaseOperation {

    void initLib() {
        try {
            String libName = "libluajava.so";
            String lib = AndroidAppHelper.currentApplication().getApplicationInfo().nativeLibraryDir + "/" + libName;
            if (new File(lib).exists()) {
                XposedBridge.log("libluajava exists: " + lib);
                return;
            }
            //目前仅支持arm架构lib
            String sdlib = "/sdcard/" + libName;
            Runtime.getRuntime().exec("su\ncp " + sdlib + " " + lib + "\nchmod 777 " + lib);
        } catch (Throwable e) {
            e.printStackTrace();
            XposedBridge.log(e);
        }
    }


    @Override
    String handle(String url, Map<String, String> parms, Map<String, String> headers, Map<String, String> files) {
        initLib();
        try {
            Map<String, Object> map = new HashMap<String, Object>();
            map.put("filter", parms.get("filter"));
            return XServer.render(map, "pages/script.html");
        } catch (Exception e) {
            return e.getLocalizedMessage();
        }
    }
}
