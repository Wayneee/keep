//package com.sbt.Keep.Helper;
//
//import android.content.Context;
//
//import com.db4o.Db4oEmbedded;
//import com.db4o.EmbeddedObjectContainer;
//import com.db4o.ObjectContainer;
//import com.db4o.config.AndroidSupport;
//import com.db4o.config.EmbeddedConfiguration;
//
//public class DbHelper {
//	private EmbeddedObjectContainer container;
//	private Context context;
//
//	public DbHelper(Context context) {
//		this.context = context;
//	}
//
//	public void close() {
//		if (container != null) {
//			container.close();
//		}
//	}
//
//	public ObjectContainer db() {
//		if (container == null || container.ext().isClosed()) {
//			EmbeddedConfiguration config = Db4oEmbedded.newConfiguration();
//			config.common().add(new AndroidSupport());
//			String filename = context.getFilesDir() + "/keep.db4o";
//			
//			container = Db4oEmbedded.openFile(config, filename);
//		}
//
//		return container;
//	}
//}
