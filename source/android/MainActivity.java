package com.android.mainactivity;

import com.ideaworks3d.marmalade.LoaderActivity;

import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.util.Log;
import android.content.Intent;
import java.io.File;
import java.util.List;
import java.util.ArrayList;
import dalvik.system.DexFile;
import dalvik.system.DexClassLoader;
import java.util.Enumeration;


public class MainActivity extends LoaderActivity {

    private static final boolean mLogEnabled = false;
    private static final String mLogTag = "marmalade";
    private void _log(String fmt, Object... arguments) {
        if (mLogEnabled) {
            Log.d(mLogTag, String.format(fmt, arguments));
        }
    }


    private static final String MAIN_ACTIVITY_LISTENER_FACTORY_PACKAGE = "com.android.mainactivity.listenerfactory";
    public interface Listener {
        void onCreate(Bundle savedInstanceState);
        void onStart();
        void onRestart();
        void onResume();
        void onPause();
        void onStop();
        void onDestroy();

        void onActivityResult(int request, int response, Intent data);
    }

    public static abstract class ListenerFactory {
        abstract protected Listener makeListener();

        private static Listener _listener;
        public Listener getListener() {
            if (_listener == null) {
                _listener = makeListener();
            }
            return _listener;
        }
    }

    private List<Listener> mListeners = new ArrayList<Listener>();
    public void registerListener(Listener listener) {
        _log("registering MainActivity.Listener: " + listener);
        mListeners.add(listener);
    }
  
    public static MainActivity singleton;
  

    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        singleton = this;
        this.createListeners();

        for (Listener listener : mListeners) {
            try {
                listener.onCreate(savedInstanceState);
            } catch (Exception e) {
                Log.d(mLogTag, e.toString(), e);
            }
        }
    }

    private void createListeners() {
        try {
            final File tmpDir = this.getDir("dex", 0);
            final DexClassLoader loader = new DexClassLoader(this.getPackageCodePath(), tmpDir.getAbsolutePath(), null, this.getClass().getClassLoader());

            DexFile df = new DexFile(this.getPackageCodePath());
            for (Enumeration<String> iter = df.entries(); iter.hasMoreElements();) {
                String s = iter.nextElement();
                if (s.startsWith(MAIN_ACTIVITY_LISTENER_FACTORY_PACKAGE)) {
                    Class<?> listenerFactoryClass = loader.loadClass(s);
                    if (ListenerFactory.class.isAssignableFrom(listenerFactoryClass)) {
                        ListenerFactory factory = (ListenerFactory)(listenerFactoryClass.newInstance());
                        this.registerListener(factory.makeListener());
                    }
                }
            }
        } catch (Exception e) {
            Log.d("marmalade", e.toString(), e);
        }
    }

    protected void onDestroy() {
        _log("MainActivity.onDestroy()");
        
        for (Listener listener : mListeners) {
            try {
                listener.onDestroy();
            } catch (Exception e) {
                Log.d(mLogTag, e.toString(), e);
            }
        }
        mListeners = null;
        singleton = null;
        super.onDestroy();
    }

    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        for (Listener listener : mListeners) {
            Log.d("marmalade", "onActivityResult listener:" + listener + "; requestCode = "  + requestCode + "; resultCode = " + resultCode + ";data = " + data);
            try {
                listener.onActivityResult(requestCode, resultCode, data);
            } catch (Exception e) {
                Log.d(mLogTag, e.toString(), e);
            }
        }

        super.onActivityResult(requestCode, resultCode, data);        
    }

    public void onStart() {
        _log("MainActivity.onStart()");
        super.onStart();
        for (Listener listener : mListeners) {
            try {
                listener.onStart();
            } catch (Exception e) {
                Log.d(mLogTag, e.toString(), e);
            }        
        }
    }

    public void onRestart() {
        _log("MainActivity.onRestart()");
        super.onRestart();
        for (Listener listener : mListeners) {
            try {
                listener.onRestart();
            } catch (Exception e) {
                Log.d(mLogTag, e.toString(), e);
            }
        }
    }

    public void onResume() {
        _log("MainActivity.onResume()");
        super.onResume();
        for (Listener listener : mListeners) {
            try {
                listener.onResume();
            } catch (Exception e) {
                Log.d(mLogTag, e.toString(), e);
            }
        }
    }

    public void onPause() {
        _log("MainActivity.onPause()");
        super.onPause();
        for (Listener listener : mListeners) {
            try {
                listener.onPause();
            } catch (Exception e) {
                Log.d(mLogTag, e.toString(), e);
            }
        }
    }

    public void onStop() {
        _log("MainActivity.onStop()");
        super.onStop();
        for (Listener listener : mListeners) {
            try {
                listener.onStop();
            } catch (Exception e) {
                Log.d(mLogTag, e.toString(), e);
            }
        }
    }

    public static boolean getBooleanConfig(String key) {
        try {
            ApplicationInfo ai = LoaderActivity.m_Activity.getPackageManager().getApplicationInfo(LoaderActivity.m_Activity.getPackageName(), PackageManager.GET_META_DATA);
            Bundle bundle = ai.metaData;
            return bundle.getBoolean(key);
        } catch (Exception e) {
            return false;
        }
    }
}
