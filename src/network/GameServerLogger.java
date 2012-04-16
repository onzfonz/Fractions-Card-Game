package network;

import java.util.logging.*;
import java.io.*;


public class GameServerLogger {
	public static Logger logger;
	private static int limit = 1000000;
	private static int numLogFiles = 400;
	
	private static class GameServerLoggerHolder {
		public static final GameServerLogger INSTANCE = new GameServerLogger();
	}
	
	public static GameServerLogger getLogger() {
		return GameServerLoggerHolder.INSTANCE;
	}
	
	private GameServerLogger() {
		try {
			boolean append = true;
			FileHandler fh = new FileHandler("HawesServerLog%g.log", limit, numLogFiles);
			fh.setFormatter(new Formatter() {
				public String format(LogRecord rec) {
					StringBuffer buf = new StringBuffer(1000);
					buf.append(new java.util.Date());
					buf.append(' ');
					buf.append(rec.getLevel());
					buf.append(' ');
					buf.append(formatMessage(rec));
					buf.append('\n');
					return buf.toString();
				}
			});
			logger = Logger.getLogger("TestLog");
			logger.addHandler(fh);
		}
		catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	public synchronized void logMessage(String msg) {
		logger.info(msg);
	}
	
	public synchronized void logWarningMessage(String msg) {
		logger.warning(msg);
	}
}

