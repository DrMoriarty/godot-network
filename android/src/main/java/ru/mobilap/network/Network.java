package ru.mobilap.network;

import android.app.Activity;
import android.view.View;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Arrays;
import java.util.List;
import java.util.Set;
import org.godotengine.godot.Godot;
import org.godotengine.godot.GodotLib;
import org.godotengine.godot.plugin.GodotPlugin;
import org.godotengine.godot.plugin.SignalInfo;

public class Network extends GodotPlugin {

    //final private SignalInfo loggedInSignal = new SignalInfo("logged_in");

    public Network(Godot godot) 
    {
        super(godot);
    }

    @Override
    public String getPluginName() {
        return "Network";
    }

    @Override
    public List<String> getPluginMethods() {
        return Arrays.asList(
                             "get_request"
                             );
    }

    /*
    @Override
    public Set<SignalInfo> getPluginSignals() {
        return Collections.singleton(loggedInSignal);
    }
    */

    @Override
    public View onMainCreateView(Activity activity) {
        return null;
    }

    // Public methods

    public void get_request(final String url, final int callback_id, final String callback_method)
    {
        URL cacheUrl = null;
        try {
            cacheUrl = new URL(url);
        } catch (MalformedURLException e) {
            e.printStackTrace();
            GodotLib.calldeferred(callback_id, callback_method, new Object[]{ 0, e.toString() });
            return;
        }
        DownloadFilesTask d = new DownloadFilesTask();
        d.setResultListener(new DownloadFilesTask.ResultListener() {
                @Override
                public void onResultString(final int responseCode, final String body) {
                    if(body == null) {
                        GodotLib.calldeferred(callback_id, callback_method, new Object[]{ responseCode, "" });
                    } else {
                        GodotLib.calldeferred(callback_id, callback_method, new Object[]{ responseCode, body });
                    }
                }
            });
        d.execute(cacheUrl);
    }

}
