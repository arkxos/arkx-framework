package com.rapidark.framework.framework.common.download;

import java.io.File;
import java.io.IOException;

import com.rapidark.framework.commons.download.RemoteHttpFile;
import com.rapidark.framework.commons.util.SystemInfo;
import com.rapidark.framework.commons.util.TimeWatch;

public class RemoteHttpFileTest {
	
public static void main(String[] args) throws IOException {
		
		TimeWatch timeWatch = TimeWatch.create().startWithTaskName("file1");
		String url = "http://quotes.money.163.com/service/chddata.html?code=1002312&start=19901219&end=20170909&fields=TCLOSE;HIGH;LOW;TOPEN;LCLOSE;CHG;PCHG;TURNOVER;VOTURNOVER;VATURNOVER;TCAP;MCAP&random=0.1477648064785";
		
		RemoteHttpFile remoteHttpFile = new RemoteHttpFile(url);
		
		System.out.println("->" +remoteHttpFile);
		System.out.println();
		
		String folder = SystemInfo.userDir() + File.separator + "temp";
		
		remoteHttpFile.download(folder);
			
		timeWatch.stopAndPrint();
	}

}
