package io.arkx.framework.commons.download;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.net.URLConnection;
import java.util.ArrayList;
import java.util.Timer;

import jakarta.xml.bind.JAXBContext;
import jakarta.xml.bind.JAXBException;
import jakarta.xml.bind.Marshaller;
import jakarta.xml.bind.annotation.*;

@XmlRootElement(namespace = "io.arkx.commons.download")
@XmlAccessorType(XmlAccessType.NONE)
public class DownloadMission {

    public static final int READY = 1;

    public static final int DOWNLOADING = 2;

    public static final int PAUSED = 3;

    public static final int FINISHED = 4;

    public static int DEFAULT_THREAD_COUNT = 4;
    static int MISSION_ID_COUNTER = 0;

    protected int missionId = MISSION_ID_COUNTER++;

    @XmlElement(name = "url")
    protected String url;

    @XmlElement(name = "folder")
    protected String folder;

    @XmlElement(name = "file-name")
    protected String fileName;

    @XmlElementWrapper(name = "file-parts")
    @XmlElement(name = "file-part")
    private ArrayList<RemoteHttpPartFile> remoteHttpPartFiles = new ArrayList<>();

    private ArrayList<RecoveryWorkerInfo> recoveryWorkerInfos = new ArrayList<>();

    @XmlElement(name = "mission-status")
    private int missionStatus = READY;

    private String progressFileName;

    @XmlElement(name = "file-size")
    private int fileSize;

    private int threadCount = DEFAULT_THREAD_COUNT;

    @XmlElement(name = "mission-monitor")
    protected MissionMonitor monitor = new MissionMonitor(this);

    @XmlElement(name = "speed-monitor")
    protected SpeedMonitor speedMonitor = new SpeedMonitor(this);

    protected StoreMonitor storeMonitor = new StoreMonitor(this);

    protected Timer speedTimer = new Timer();

    protected Timer storeTimer = new Timer();

    // protected DownloadScheduler scheduler;

    @SuppressWarnings("unused")
    private DownloadMission() {
        // just for annotation
    }

    public DownloadMission(String url, String saveDirectory, String saveName) throws IOException {
        this.url = url;
        this.folder = saveDirectory;
        this.fileName = saveName;
    }

    private boolean initTargetFile() throws IOException {

        File dirFile = new File(this.folder);
        if (!dirFile.exists()) {
            if (!dirFile.mkdirs()) {
                throw new RuntimeException("Error to create directory");
            }
        }

        File file = new File(dirFile.getPath() + File.separator + this.fileName);
        if (!file.exists()) {
            file.createNewFile();
        }

        return true;
    }

    public int getMissionId() {
        return missionId;
    }

    public String getUrl() {
        return this.url;
    }

    public void setUrl(String Url) {
        this.url = Url;
    }

    public String getSaveDirectory() {
        return this.folder;
    }

    public void setSaveDirectory(String saveDirectory) {
        this.folder = saveDirectory;
    }

    public String getSaveName() {
        return this.fileName;
    }

    public void setSaveName(String saveName) {
        this.fileName = saveName;
    }

    public void setMissionThreadCount(int threadCount) {
        this.threadCount = threadCount;
    }

    public int getMissionThreadCount() {
        return this.threadCount;
    }

    public void setDefaultThreadCount(int default_thread_count) {
        if (default_thread_count > 0) {
            DEFAULT_THREAD_COUNT = default_thread_count;
        }
    }

    public int getDefaultThreadCount() {
        return DEFAULT_THREAD_COUNT;
    }

    private int singleBlockSize = 4 * 1024;// 单个文件块 4k

    private ArrayList<RemoteHttpPartFile> splitDownload(int threadCount) {
        ArrayList<RemoteHttpPartFile> runnables = new ArrayList<>();
        try {
            int size = getContentLength(this.url);
            this.fileSize = size;
            int sublen = size / threadCount;
            for (int i = 0; i < threadCount; i++) {
                int startPos = sublen * i;
                int endPos = (i == threadCount - 1) ? (size - 1) : (sublen * (i + 1) - 1);
                RemoteHttpPartFile worker = new RemoteHttpPartFile(this.monitor, this.url, this.folder, this.fileName,
                        startPos, endPos);
                runnables.add(worker);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return runnables;
    }

    public void startMission() {
        try {
            initTargetFile();
            setProgessFile(this.folder + File.separator + this.fileName + ".download.xml");
        } catch (IOException e) {
            e.printStackTrace();
        }

        setDownloadStatus(DOWNLOADING);

        // this.scheduler = scheduler;
        if (!this.recoveryWorkerInfos.isEmpty()) {
            for (RecoveryWorkerInfo workerInfo : this.recoveryWorkerInfos) {
                if (!workerInfo.isFinished()) {
                    RemoteHttpPartFile worker = new RemoteHttpPartFile(this.monitor, this.url, this.folder,
                            this.fileName, workerInfo.getStartPosition(), workerInfo.getCurrentPosition(),
                            workerInfo.getEndPosition());
                    this.remoteHttpPartFiles.add(worker);
                    // scheduler.push(worker);
                }
            }
        } else {
            for (RemoteHttpPartFile runnable : splitDownload(this.threadCount)) {
                this.remoteHttpPartFiles.add(runnable);
                // scheduler.push(runnable);
            }
        }
        this.speedTimer.scheduleAtFixedRate(this.speedMonitor, 0, 1000);// 1s
        this.storeTimer.scheduleAtFixedRate(this.storeMonitor, 0, 5000);// 5s
    }

    public ArrayList<RemoteHttpPartFile> getDownloadWorkers() {
        return remoteHttpPartFiles;
    }

    public boolean isFinished() {
        return missionStatus == FINISHED;
    }

    public void addPartedMission(RemoteHttpPartFile runnable) {
        this.remoteHttpPartFiles.add(runnable);
    }

    private int getContentLength(String fileUrl) throws IOException {
        URL url = new URL(fileUrl);
        URLConnection connection = url.openConnection();
        return connection.getContentLength();
    }

    boolean setProgessFile(String progressFileName) throws IOException {
        File file = new File(progressFileName);
        if (!file.exists()) {
            file.createNewFile();
        }
        this.progressFileName = progressFileName;
        return true;
    }

    public File getProgressFile() {
        return new File(this.progressFileName);
    }

    public File getDownloadFile() {
        return new File(this.folder + File.separator + this.fileName);
    }

    public String getProgressFileName() {
        return this.progressFileName;
    }

    public int getDownloadedSize() {
        return this.monitor.getDownloadedSize();
    }

    public String getReadableSize() {
        return DownloadUtils.getReadableSize(getDownloadedSize());
    }

    public int getSpeed() {
        return this.speedMonitor.getSpeed();
    }

    public String getReadableSpeed() {
        return DownloadUtils.getReadableSpeed(getSpeed());
    }

    public int getMaxSpeed() {
        return this.speedMonitor.getMaxSpeed();
    }

    public String getReadableMaxSpeed() {
        return DownloadUtils.getReadableSpeed(getMaxSpeed());
    }

    public int getAverageSpeed() {
        return this.speedMonitor.getAverageSpeed();
    }

    public String getReadableAverageSpeed() {
        return DownloadUtils.getReadableSpeed(this.speedMonitor.getAverageSpeed());
    }

    public int getTimePassed() {
        return this.speedMonitor.getDownloadedTime();
    }

    public int getFileSize() {
        return this.fileSize;
    }

    public void pause() {
        if (!isFinished()) {
            setDownloadStatus(PAUSED);
        }
        storeProgress();
        inactiveDownload();
        speedTimer.cancel();
        storeTimer.cancel();
        // this.scheduler.pause(this.missionId);
    }

    void setDownloadStatus(int status) {
        if (status == FINISHED) {
            this.speedTimer.cancel();
        }
        this.missionStatus = status;
        // inactiveDownload();
    }

    private void inactiveDownload() {
        for (RemoteHttpPartFile remoteHttpPartFile : remoteHttpPartFiles) {
            remoteHttpPartFile.inactive();
        }
    }

    public void storeProgress() {
        try {
            JAXBContext context = JAXBContext.newInstance(DownloadMission.class);
            Marshaller m = context.createMarshaller();
            m.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, Boolean.TRUE);
            m.marshal(this, getProgressFile());
        } catch (JAXBException e) {
            e.printStackTrace();
        }
    }

    private void deleteProgressFile() {
        getProgressFile().delete();
    }

    public ArrayList<RecoveryWorkerInfo> getDownloadProgress() {
        return this.recoveryWorkerInfos;
    }

    public void cancel() {
        deleteProgressFile();
        this.speedTimer.cancel();
        this.remoteHttpPartFiles.clear();
        // this.scheduler.cancel(missionId);
        inactiveDownload();
    }

}
