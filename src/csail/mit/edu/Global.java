package csail.mit.edu;

import android.content.Context;

public class Global {
	public static Context context;
    public static String PayloadFieldDelimiter = ",";
    public static String FieldDelimiter = "|";
	public static void setContext(Context ctx){
		context = ctx;
	}
}
