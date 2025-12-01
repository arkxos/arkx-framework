package io.arkx.framework.framework.common.download;

import io.arkx.framework.commons.download.RemoteHttpFile;
import io.arkx.framework.commons.util.SystemInfo;
import io.arkx.framework.commons.util.TimeWatch;

import java.io.File;
import java.io.IOException;

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
