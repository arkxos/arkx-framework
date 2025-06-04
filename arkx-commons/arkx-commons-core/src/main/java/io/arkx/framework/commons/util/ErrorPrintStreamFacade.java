package io.arkx.framework.commons.util;
import java.io.PrintStream;

public class ErrorPrintStreamFacade extends PrintStream {
	
	private static PrintStream stream = null;
	private static ErrorPrintStreamFacade instance;

	public ErrorPrintStreamFacade(PrintStream err) {
		super(err, true);
		instance = this;
	}

	public static ErrorPrintStreamFacade getInstance() {
		return instance;
	}

	public void setPrintStream(PrintStream ps) {
		stream = ps;
	}

	public void unloadClass() {
		stream = null;
	}

	public synchronized void println(Object obj) {
		try {
			if (stream != null)
				stream.println(obj);
		} catch (Throwable e) {
			System.out.println("ErrorPrintStreamFacade.println() failed:" + e.getMessage());
		}
	}
}