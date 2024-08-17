package dd.astolastudio.balochidictionary;

import android.app.Application;
import dd.astolastudio.balochidictionary.common.crash.CrashHandler;
import java.util.Map;
import java.util.HashMap;
import java.util.List;
import java.util.ArrayList;
import java.util.Arrays;

public class App extends Application {

	private static App sApp;

	@Override
	public void onCreate() {
		super.onCreate();
		sApp = this;
		CrashHandler.init(this);
	}

	public static App getApp() {
		return sApp;
	}
	
	public static List<String> getLogger(){
		List<String> logger = (List<String>) System.getProperties().get("mLogger");
		if(logger==null){
			logger=new ArrayList<>();
			System.getProperties().put("mLogger", logger);
		}
		return logger;
	}
	
	public static void log(String msg, Object...args){
		getLogger().add(String.format("%s: %s", thisTag(), args != null && args.length > 0 ? String.format(msg, args) : msg));
	}
	
	public static String thisTag(){
		boolean b=false;
		for(StackTraceElement e:Thread.currentThread().getStackTrace()){
			if(!e.getClassName().equalsIgnoreCase(App.class.getName())){
				if(b){
					return e.toString();
				}
			}else{
				b=true;
			}
		}
		return null;
	}
}
