package cn.vove7.rhino.api;

import android.util.Log;

import org.mozilla.javascript.Context;
import org.mozilla.javascript.Function;
import org.mozilla.javascript.NativeJavaArray;
import org.mozilla.javascript.Scriptable;
import org.mozilla.javascript.annotations.JSFunction;

import java.lang.reflect.Field;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

import cn.vove7.rhino.common.GcCollector;
import kotlin.Unit;
import kotlin.jvm.functions.Function1;
import kotlin.jvm.functions.Function2;

/**
 * Created by 17719247306 on 2018/8/28
 */
public class RhinoApi extends AbsApi {

    @Override
    protected String[] funs() {
        return new String[]{
                //"loadAsset",
                "quit",
                "print",
                "log",
        };
    }

    public static void quit(Context cx, Scriptable thisObj,//global
                            Object[] args, Function funObj) {
        System.err.println("over");
        GcCollector.gc(thisObj);
    }
    //
    ///**
    // * 从Asset加载
    // */
    //
    //public static void loadAsset(Context cx, Scriptable thisObj,
    //                             Object[] args, Function funObj) {
    //    for (Object arg : args) {
    //        String file = Context.toString(arg);
    //        try {
    //            Log.d("RhinoApi :", "loadAsset  ----> " + file);
    //            Reader reader = new InputStreamReader(ctx.getAssets().open(file));
    //            cx.evaluateReader(thisObj, reader, "load_" + file, 1, null);
    //        } catch (Exception ex) {
    //            onException(ex);
    //        }
    //    }
    //}

    public static void onException(Throwable e) {
        notifyOutput(Log.ERROR, e.getMessage());
    }

    private static final Set<Function2<Integer, String, Unit>> printList = new HashSet<>();

    public static void regPrint(Function2<Integer, String, Unit> print) {
        synchronized (printList) {
            printList.add(print);
        }
    }

    public static void unregPrint(Function2<Integer, String, Unit> print) {
        synchronized (printList) {
            printList.remove(print);
        }
    }

    private static void notifyOutput(int l, String o) {
        synchronized (printList) {
            for (Function2<Integer, String, Unit> p : printList) {
                p.invoke(l, o);
            }
        }
    }

    @JSFunction
    public static void log(Context cx, Scriptable thisObj,
                           Object[] args, Function funObj) {
        Log.d("Rhino", Arrays.toString(args));
    }


    @JSFunction
    public synchronized static void print(Context cx, Scriptable thisObj,
                                          Object[] args, Function funObj) {
        StringBuilder builder = new StringBuilder();
        for (int i = 0; i < args.length; i++) {

            if (i > 0)
                builder.append(' ');
            // Convert the arbitrary JavaScript value into a string form.
            Object obj = args[i];
            String s;
            if (obj instanceof NativeJavaArray) {
                try {
                    Field ar = obj.getClass().getDeclaredField("array");
                    ar.setAccessible(true);
                    s = Arrays.toString((Object[]) ar.get(obj));
                } catch (Exception e) {
                    s = Context.toString(obj);
                    e.printStackTrace();
                }
            } else {
                s = Context.toString(obj);
            }
            builder.append(s);
        }
        builder.append("\n");
        doLog(builder.toString());
        //Log.d("Vove :", " out ----> " + builder.toString());
    }

    public static void doLog(String m) {
        notifyOutput(Log.INFO, m);
    }
}
