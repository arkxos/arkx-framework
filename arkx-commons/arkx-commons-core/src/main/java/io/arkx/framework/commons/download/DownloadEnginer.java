package io.arkx.framework.commons.download;

import io.arkx.framework.commons.queueexecutor.Element;
import io.arkx.framework.commons.queueexecutor.MultiThreadedQueueExecutor;
import io.arkx.framework.commons.util.Handler;
import jakarta.xml.bind.JAXBContext;
import jakarta.xml.bind.JAXBException;
import jakarta.xml.bind.Unmarshaller;

import java.io.File;
import java.io.FilenameFilter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Hashtable;

public class DownloadEnginer {

	private static DownloadEnginer instance = new DownloadEnginer();

//	private static DownloadScheduler threadPool;

//	public static final int DEFAULT_MISSION_THREAD_COUNT = 4;
//	public static final int DEFAULT_CORE_POOL_SIZE = 10;

//	public static final int DEFAULT_MAX_POOL_SIZE = Integer.MAX_VALUE;
//	public static final int DEFAULT_KEEP_ALIVE_TIME = 0;

	private static int ID = 0;
	
	public static DownloadEnginer getInstance() {
//		if (threadPool.isShutdown()) {
//			threadPool = new DownloadScheduler(DEFAULT_CORE_POOL_SIZE,
//					DEFAULT_MAX_POOL_SIZE, DEFAULT_KEEP_ALIVE_TIME,
//					TimeUnit.SECONDS, new LinkedBlockingDeque<Runnable>());
//		}
		return instance;
	}
	
	private MultiThreadedQueueExecutor<RemoteHttpPartFile> queueExecutor;
	
	private String downloadFolder = System.getProperty("user.dir") + File.separator + "download" + File.separator;
	
	private Hashtable<Integer, DownloadMission> missions = new Hashtable<>();

	private DownloadEnginer() {
//		threadPool = new DownloadScheduler(DEFAULT_CORE_POOL_SIZE,
//				DEFAULT_MAX_POOL_SIZE, DEFAULT_KEEP_ALIVE_TIME,
//				TimeUnit.SECONDS, new LinkedBlockingDeque<Runnable>());
		
		queueExecutor = new MultiThreadedQueueExecutor<>("DownloadEnginer", "下载引擎", new RemoteHttpPartFileProcessor())
		.thread(5)
//		.addStartTasks(Arrays.asList(new Task("")))
		.onFinish(new Handler() {
			@Override
			public void execute() {
				System.out.println("finished");
			}
		});
	}

//	public void setMaxThreadCount(int maxCount) {
//		if (maxCount > 0) {
//			threadPool.setCorePoolSize(maxCount);
//		}
//	}

	public void start() {
		File downloadFolderFile = new File(downloadFolder);
		if(!downloadFolderFile.exists()) {
			downloadFolderFile.mkdirs();
		}
		String[] missionRecordFiles = downloadFolderFile.list(new FilenameFilter() {
			
			@Override
			public boolean accept(File dir, String name) {
				return name.endsWith(".download.xml");
			}
		});
		
		for (String missionRecordFile : missionRecordFiles) {
			try {
				DownloadMission mission = recoverMissionFromProgressFile(missionRecordFile);
				if(!mission.isFinished()) {
					addMission(mission);
				}
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		
		for (DownloadMission mission : missions.values()) {
			mission.startMission();
			ArrayList<RemoteHttpPartFile> remoteHttpPartFiles = mission.getDownloadWorkers();
			for (RemoteHttpPartFile remoteHttpPartFile : remoteHttpPartFiles) {
				String id = remoteHttpPartFile.getId();
				queueExecutor.addElement(new Element<RemoteHttpPartFile>(id, remoteHttpPartFile));
			}
		}
		
		queueExecutor.startAsync();
	}
	
	public static DownloadMission recoverMissionFromProgressFile(String progressFilePath)
			throws IOException {
		try {
			File progressFile = new File(progressFilePath);
			if (!progressFile.exists()) {
				throw new IOException("Progress File does not exsist");
			}
			
			if(progressFile.length() == 0) {
				return null;
			}

			JAXBContext context = JAXBContext.newInstance(DownloadMission.class);
			Unmarshaller unmarshaller = context.createUnmarshaller();
			DownloadMission mission = (DownloadMission) unmarshaller.unmarshal(progressFile);
			File targetSaveFile = new File(FileUtils.getSafeDirPath(mission.folder + File.separator + mission.fileName));
			if (!targetSaveFile.exists()) {
				throw new IOException("Try to continue download file , but target file does not exist");
			}
			mission.setProgessFile(progressFilePath);
			mission.missionId = DownloadMission.MISSION_ID_COUNTER++;
			ArrayList<RecoveryWorkerInfo> recoveryRunnableInfos = mission.getDownloadProgress();
			recoveryRunnableInfos.clear();
			for (RemoteHttpPartFile runnable : mission.getDownloadWorkers()) {
				recoveryRunnableInfos.add(new RecoveryWorkerInfo(runnable.getStartPosition(), runnable.getCurrentPosition(), runnable.getEndPosition()));
			}
			mission.getDownloadWorkers().clear();
			mission.speedMonitor = new SpeedMonitor(mission);
			mission.storeMonitor = new StoreMonitor(mission);
			System.out.println("Resume finished");
			return mission;
		} catch (JAXBException e) {
			e.printStackTrace();
			return null;
		}
	}
	
	public DownloadMission addMission(String url, String saveDirectory, String saveName) throws IOException {
		DownloadMission downloadMission = new DownloadMission(url, saveDirectory, saveName);
		addMission(downloadMission);
		return downloadMission;
	}

	public void addMission(DownloadMission downloadTask) {
		missions.put(ID++, downloadTask);
	}

	public void pauseMission(int missionId) {
		if (missions.contains(missionId)) {
			DownloadMission mission = missions.get(missionId);
			mission.pause();
		}
	}
	
	public void pauseAllMissions() {
		for (Integer missionID : missions.keySet()) {
			pauseMission(missionID);
		}
	}

	public void cancelMission(int missionId) {
		if (missions.contains(missionId)) {
			DownloadMission mission = missions.remove(missionId);
			mission.cancel();
		}
	}

	public void cancelAllMissions() {
		for (Integer missionId : missions.keySet()) {
			cancelMission(missionId);
		}
	}
	
	public void shutdownSafely() {
		for (Integer missionId : missions.keySet()) {
			missions.get(missionId).pause();
		}
		queueExecutor.close();
//		threadPool.shutdown();
	}
	
	public void shutdDownloadRudely() {
//		threadPool.shutdownNow();
	}
	
	public DownloadMission getMission(int missionID) {
		return missions.get(missionID);
	}

	public boolean isMissionFinished(int missionId) {
		DownloadMission mission = missions.get(missionId);
		return mission.isFinished();
	}
	
	public boolean isAllMissionsFinished() {
		for (Integer missionId : missions.keySet()) {
			if (!isMissionFinished(missionId)) {
				return false;
			}
		}
		return true;
	}

	public int getTotalDownloadedSize() {
		int size = 0;
		for (DownloadMission mission : missions.values()) {
			size += mission.getDownloadedSize();
		}
		return size;
	}

	public String getReadableDownloadSize() {
		return DownloadUtils.getReadableSize(getTotalDownloadedSize());
	}

	public int getTotalSpeed() {
		int speed = 0;
		for (DownloadMission mission : missions.values()) {
			speed += mission.getSpeed();
		}
		return speed;
	}

	public String getReadableTotalSpeed() {
		return DownloadUtils.getReadableSpeed(getTotalSpeed());
	}

}
