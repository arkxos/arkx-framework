package io.arkx.framework.commons.download;

import java.util.concurrent.atomic.AtomicInteger;

import jakarta.xml.bind.annotation.XmlAccessType;
import jakarta.xml.bind.annotation.XmlAccessorType;
import jakarta.xml.bind.annotation.XmlElement;
import jakarta.xml.bind.annotation.XmlRootElement;
import jakarta.xml.bind.annotation.adapters.XmlJavaTypeAdapter;

/**
 * 
 * @author Darkness
 * @date 2016年11月1日 下午6:46:59
 * @version V1.0
 */
@SuppressWarnings("restriction")
@XmlRootElement(name = "mission-monitor")
@XmlAccessorType(XmlAccessType.NONE)
public class MissionMonitor {

	public final DownloadMission hostMission;

	@XmlElement(name = "downloaded-size")
	@XmlJavaTypeAdapter(AtomicIntegerAdapter.class)
	private AtomicInteger downloadedSize = new AtomicInteger();

	@SuppressWarnings("unused")
	private MissionMonitor() {
		hostMission = null;
	}

	public MissionMonitor(DownloadMission monitorBelongsTo) {
		hostMission = monitorBelongsTo;
	}

	public void down(int size) {
		downloadedSize.addAndGet(size);
		if (downloadedSize.intValue() == hostMission.getFileSize()) {
			hostMission.setDownloadStatus(DownloadMission.FINISHED);
		}
	}

	public int getDownloadedSize() {
		return downloadedSize.get();
	}

}
