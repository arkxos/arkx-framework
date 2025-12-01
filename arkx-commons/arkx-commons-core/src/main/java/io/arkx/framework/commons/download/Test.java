package io.arkx.framework.commons.download;

import io.arkx.framework.commons.util.TimeWatch;

import java.io.File;
import java.io.IOException;

public class Test {

	public static void main(String[] args) {
		DownloadEnginer downloadEnginer = DownloadEnginer.getInstance();

		String file1 = "http://mirror.bit.edu.cn/apache/tomcat/tomcat-7/v7.0.81/bin/apache-tomcat-7.0.81-windows-x86.zip";
		
		String saveDirectory = System.getProperty("user.dir") + File.separator + "temp" + File.separator;
		System.out.println(saveDirectory);
		
		TimeWatch timeWatch = TimeWatch.create().startWithTaskName("download");
		try {
//			urls.add("http://quotes.money.163.com/service/chddata.html?code=0601128&start=19901219&end=20160919&fields=TCLOSE;HIGH;LOW;TOPEN;LCLOSE;CHG;PCHG;TURNOVER;VOTURNOVER;VATURNOVER;TCAP;MCAP&random=0.1477648064781");
//			urls.add("http://quotes.money.163.com/service/chddata.html?code=1000534&start=19901219&end=20160919&fields=TCLOSE;HIGH;LOW;TOPEN;LCLOSE;CHG;PCHG;TURNOVER;VOTURNOVER;VATURNOVER;TCAP;MCAP&random=0.1477648064785");
//			
			DownloadMission mission = new DownloadMission(file1, saveDirectory, "test1.zip");
			downloadEnginer.addMission(mission);
//			DownloadMission mission2 = new DownloadMission(file2, saveDirectory, "test2");
//			downloadManager.addMission(mission2);
//			DownloadMission mission3 = new DownloadMission(file3, saveDirectory, "test3");
//			downloadManager.addMission(mission3);
			
			
			downloadEnginer.start();
			
			//int counter = 0;
			while (true) {
				// System.out.println("The mission has finished :"
				// + mission.getReadableSize() + "Active Count:"
				// + mission.getActiveTheadCount() + " speed:"
				// + mission.getReadableSpeed() + " status:"
				// + mission.isFinished() + " AverageSpeed:"
				// + mission.getReadableAverageSpeed() + " MaxSpeed:"
				// + mission.getReadableMaxSpeed() + " Time:"
				// + mission.getTimePassed() + "s");
				System.out.println("Downloader information Speed:" + downloadEnginer.getReadableTotalSpeed() + " Down Size:" + downloadEnginer.getReadableDownloadSize());
				Thread.sleep(1000);
				//counter++;
				// if (counter == 6) {
				// mission.pause();
				// }
				// if (counter == 11) {
				// downloadManager.start();
				// }
				if(downloadEnginer.isAllMissionsFinished()) {
					downloadEnginer.shutdownSafely();
					break;
				}
			}
		} catch (IOException e1) {
			e1.printStackTrace();
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
		
		timeWatch.stopAndPrint();
	}
}
