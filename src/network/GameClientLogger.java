package network;

import java.util.logging.*;
import java.io.*;


public class GameClientLogger {
	public static Logger logger;
	private static int limit = 1000000;
	private static int numLogFiles = 400;
	
	private static class GameServerLoggerHolder {
		public static final GameClientLogger INSTANCE = new GameClientLogger();
	}
	
	public static GameClientLogger getLogger() {
		return GameServerLoggerHolder.INSTANCE;
	}
	
	private GameClientLogger() {
		try {
			boolean append = true;
			FileHandler fh = new FileHandler("%h/HawesClientLog%g.log", limit, numLogFiles);
			fh.setFormatter(new Formatter() {
				@Override
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
			logger = Logger.getLogger("ClientLog");
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

