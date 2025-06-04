package com.arkxos.framework.commons.simplequeue;

import java.io.Closeable;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicLong;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.ReentrantLock;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.SerializationUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.arkxos.framework.commons.crawl.PageProcessor;
import com.arkxos.framework.commons.simplequeue.pipeline.CollectorPipeline;
import com.arkxos.framework.commons.simplequeue.pipeline.Pipeline;
import com.arkxos.framework.commons.simplequeue.pipeline.ResultItemsCollectorPipeline;
import com.arkxos.framework.commons.simplequeue.scheduler.MonitorableScheduler;
import com.arkxos.framework.commons.simplequeue.scheduler.QueueScheduler;
import com.arkxos.framework.commons.simplequeue.scheduler.Scheduler;
import com.arkxos.framework.commons.simplequeue.thread.CountableThreadPool;
import com.arkxos.framework.commons.simplequeue.utils.WMCollections;
import com.arkxos.framework.commons.util.NumberUtil;

/**
 * Entrance of a crawler.<br>
 * A spider contains four modules: Downloader, Scheduler, PageProcessor and
 * Pipeline.<br>
 * Every module is a field of Spider. <br>
 * The modules are defined in interface. <br>
 * You can customize a spider with various implementations of them. <br>
 * Examples: <br>
 * <br>
 * A simple crawler: <br>
 * Spider.create(new SimplePageProcessor("http://my.oschina.net/",
 * "http://my.oschina.net/*blog/*")).run();<br>
 * <br>
 * Store results to files by FilePipeline: <br>
 * Spider.create(new SimplePageProcessor("http://my.oschina.net/",
 * "http://my.oschina.net/*blog/*")) <br>
 * .pipeline(new FilePipeline("/data/temp/webmagic/")).run(); <br>
 * <br>
 * Use FileCacheQueueScheduler to store urls and cursor in files, so that a
 * Spider can resume the status when shutdown. <br>
 * Spider.create(new SimplePageProcessor("http://my.oschina.net/",
 * "http://my.oschina.net/*blog/*")) <br>
 * .scheduler(new FileCacheQueueScheduler("/data/temp/webmagic/cache/")).run(); <br>
 *
 * @author code4crafter@gmail.com <br>
 * @see ElementProcessor
 * @see Scheduler
 * @see PageProcessor
 * @see Pipeline
 * @since 0.1.0
 */
public class QueueExecutor implements Runnable, Task {

    protected ElementProcessor elementProcessor;

    protected List<Pipeline> pipelines = new ArrayList<>();

    protected List<ElementWarpper> startElementWrappers;

    protected Config config;

    protected String uuid;

    protected Scheduler scheduler = new QueueScheduler();

    protected Logger logger = LoggerFactory.getLogger(getClass());

    protected CountableThreadPool threadPool;

    protected ExecutorService executorService;

    protected int threadNum = 1;

    protected AtomicInteger stat = new AtomicInteger(STAT_INIT);

    protected boolean exitWhenComplete = true;

    protected final static int STAT_INIT = 0;

    protected final static int STAT_RUNNING = 1;

    protected final static int STAT_STOPPED = 2;

    protected boolean spawnElement = true;

    protected boolean destroyWhenExit = true;

    private ReentrantLock newElementLock = new ReentrantLock();

    private Condition newElementCondition = newElementLock.newCondition();

    private List<QueueListener> queueListeners = new ArrayList<>();

    private final AtomicLong elementCount = new AtomicLong(0);

    private Date startTime;

    private int emptySleepTime = 30000;

    private String name;
    
    /**
     * create a spider with pageProcessor.
     *
     * @param pageProcessor pageProcessor
     * @return new spider
     * @see PageProcessor
     */
    public static QueueExecutor create(QueueTask queueTask) {
        return new QueueExecutor(queueTask.taskName(), queueTask).addElement(queueTask.initElements());
    }
    
    public static QueueExecutor create(String name, ElementProcessor elementProcessor) {
        return new QueueExecutor(name, elementProcessor);
    }

    /**
     * create a spider with pageProcessor.
     *
     * @param pageProcessor pageProcessor
     */
    public QueueExecutor(String name, ElementProcessor elementProcessor) {
    	this.name = name;
        this.elementProcessor = elementProcessor;
        this.config = elementProcessor.getConfig();
    }

    /**
     * Set startUrls of Spider.<br>
     * Prior to startUrls of Site.
     *
     * @param startUrls startUrls
     * @return this
     */
    public QueueExecutor startElements(List<String> startElements) {
        checkIfRunning();
        this.startElementWrappers = convertToElementWarppers(startElements);
        return this;
    }
    
    public static List<ElementWarpper> convertToElementWarppers(Collection<String> elements) {
        List<ElementWarpper> result = new ArrayList<ElementWarpper>(elements.size());
        for (String element : elements) {
            result.add(new ElementWarpper(element));
        }
        return result;
    }

    /**
     * Set startUrls of Spider.<br>
     * Prior to startUrls of Site.
     *
     * @param startElementWarppers startRequests
     * @return this
     */
    public QueueExecutor startElementWarppers(List<ElementWarpper> startElementWarppers) {
        checkIfRunning();
        this.startElementWrappers = startElementWarppers;
        return this;
    }

    /**
     * Set an uuid for spider.<br>
     * Default uuid is domain of site.<br>
     *
     * @param uuid uuid
     * @return this
     */
    public QueueExecutor setUUID(String uuid) {
        this.uuid = uuid;
        return this;
    }

    /**
     * set scheduler for Spider
     *
     * @param scheduler scheduler
     * @return this
     * @see #setScheduler(com.arkxos.framework.commons.simplequeue.scheduler.Scheduler)
     */
    @Deprecated
    public QueueExecutor scheduler(Scheduler scheduler) {
        return setScheduler(scheduler);
    }

    /**
     * set scheduler for Spider
     *
     * @param scheduler scheduler
     * @return this
     * @see Scheduler
     * @since 0.2.1
     */
    public QueueExecutor setScheduler(Scheduler scheduler) {
        checkIfRunning();
        Scheduler oldScheduler = this.scheduler;
        this.scheduler = scheduler;
        if (oldScheduler != null) {
            ElementWarpper elementWarpper;
            while ((elementWarpper = oldScheduler.poll(this)) != null) {
                this.scheduler.push(elementWarpper, this);
            }
        }
        return this;
    }

    /**
     * add a pipeline for Spider
     *
     * @param pipeline pipeline
     * @return this
     * @see #addPipeline(com.arkxos.framework.commons.simplequeue.pipeline.Pipeline)
     * @deprecated
     */
    public QueueExecutor pipeline(Pipeline pipeline) {
        return addPipeline(pipeline);
    }

    /**
     * add a pipeline for Spider
     *
     * @param pipeline pipeline
     * @return this
     * @see Pipeline
     * @since 0.2.1
     */
    public QueueExecutor addPipeline(Pipeline pipeline) {
        checkIfRunning();
        this.pipelines.add(pipeline);
        return this;
    }

    /**
     * set pipelines for Spider
     *
     * @param pipelines pipelines
     * @return this
     * @see Pipeline
     * @since 0.4.1
     */
    public QueueExecutor setPipelines(List<Pipeline> pipelines) {
        checkIfRunning();
        this.pipelines = pipelines;
        return this;
    }

    /**
     * clear the pipelines set
     *
     * @return this
     */
    public QueueExecutor clearPipeline() {
        pipelines = new ArrayList<Pipeline>();
        return this;
    }

    protected void initComponent() {
        if (pipelines.isEmpty()) {
//            pipelines.add(new ConsolePipeline());
        }
        if (threadPool == null || threadPool.isShutdown()) {
            if (executorService != null && !executorService.isShutdown()) {
                threadPool = new CountableThreadPool(threadNum, executorService);
            } else {
                threadPool = new CountableThreadPool(threadNum);
            }
        }
        if (startElementWrappers != null) {
            for (ElementWarpper elementWarpper : startElementWrappers) {
                addElement(elementWarpper);
            }
            startElementWrappers.clear();
        }
        startTime = new Date();
    }

    @Override
    public void run() {
        checkRunningStat();
        initComponent();
        logger.info("Spider {} started!",getUUID());
        while (!Thread.currentThread().isInterrupted() && stat.get() == STAT_RUNNING) {
            final ElementWarpper elementWarpper = scheduler.poll(this);
            if (elementWarpper == null) {
                if (threadPool.getThreadAlive() == 0 && exitWhenComplete) {
                    break;
                }
                // wait until new element added
                waitNewElement();
            } else {
                threadPool.execute(new Runnable() {
                    @Override
                    public void run() {
                        try {
                            processElement(elementWarpper);
                            onSuccess(elementWarpper);
                        } catch (Exception e) {
                            onError(elementWarpper);
                            logger.error("process request " + elementWarpper + " error", e);
                        } finally {
                            elementCount.incrementAndGet();
                            signalNewElement();
                        }
                    }
                });
            }
        }
        stat.set(STAT_STOPPED);
        // release some resources
        if (destroyWhenExit) {
            close();
        }
        logger.info("Spider {} closed! {} pages downloaded.", getUUID(), elementCount.get());
    }

    protected void onError(ElementWarpper request) {
        if (CollectionUtils.isNotEmpty(queueListeners)) {
            for (QueueListener spiderListener : queueListeners) {
                spiderListener.onError(request);
            }
        }
    }

    protected void onSuccess(ElementWarpper request) {
        if (CollectionUtils.isNotEmpty(queueListeners)) {
            for (QueueListener spiderListener : queueListeners) {
                spiderListener.onSuccess(request);
            }
        }
    }

    private void checkRunningStat() {
        while (true) {
            int statNow = stat.get();
            if (statNow == STAT_RUNNING) {
                throw new IllegalStateException("Spider is already running!");
            }
            if (stat.compareAndSet(statNow, STAT_RUNNING)) {
                break;
            }
        }
    }

    public void close() {
        destroyEach(elementProcessor);
        destroyEach(scheduler);
        for (Pipeline pipeline : pipelines) {
            destroyEach(pipeline);
        }
        threadPool.shutdown();
    }

    private void destroyEach(Object object) {
        if (object instanceof Closeable) {
            try {
                ((Closeable) object).close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    /**
     * Process specific urls without url discovering.
     *
     * @param urls urls to process
     */
    public void test(String... urls) {
        initComponent();
        if (urls.length > 0) {
            for (String url : urls) {
                processElement(new ElementWarpper(url));
            }
        }
    }

    private void processElement(ElementWarpper request) {
        ExecuteResult page = elementProcessor.execute(request, this);
        if (page.isExecuteSuccess()){
            onProcessSuccess(request, page);
        } else {
            onProcessFail(request);
        }
    }

    private void onProcessSuccess(ElementWarpper request, ExecuteResult executeResult) {
		extractAndAddElementWarppers(executeResult, spawnElement);
		if (!executeResult.getResultItems().isSkip()) {
			for (Pipeline pipeline : pipelines) {
				pipeline.process(executeResult.getResultItems(), this);
			}
		}
        sleep(config.getSleepTime());
        return;
    }

    private void onProcessFail(ElementWarpper request) {
        if (config.getCycleRetryTimes() == 0) {
            sleep(config.getSleepTime());
        } else {
            // for cycle retry
            doCycleRetry(request);
        }
    }

    private void doCycleRetry(ElementWarpper elementWarpper) {
        Object cycleTriedTimesObject = elementWarpper.getExtra(ElementWarpper.CYCLE_TRIED_TIMES);
        if (cycleTriedTimesObject == null) {
            addElement(SerializationUtils.clone(elementWarpper).setPriority(0).putExtra(ElementWarpper.CYCLE_TRIED_TIMES, 1));
        } else {
            int cycleTriedTimes = (Integer) cycleTriedTimesObject;
            cycleTriedTimes++;
            if (cycleTriedTimes < config.getCycleRetryTimes()) {
                addElement(SerializationUtils.clone(elementWarpper).setPriority(0).putExtra(ElementWarpper.CYCLE_TRIED_TIMES, cycleTriedTimes));
            }
        }
        sleep(config.getRetrySleepTime());
    }

    protected void sleep(int time) {
        try {
            Thread.sleep(time);
        } catch (InterruptedException e) {
            logger.error("Thread interrupted when sleep",e);
        }
    }

    protected void extractAndAddElementWarppers(ExecuteResult executeResult, boolean spawnElement) {
        if (spawnElement && CollectionUtils.isNotEmpty(executeResult.getTargetRequests())) {
            for (ElementWarpper elementWarpper : executeResult.getTargetRequests()) {
                addElement(elementWarpper);
            }
        }
    }

    private void addElement(ElementWarpper elementWarpper) {
        scheduler.push(elementWarpper, this);
    }

    protected void checkIfRunning() {
        if (stat.get() == STAT_RUNNING) {
            throw new IllegalStateException("Spider is already running!");
        }
    }

    public void runAsync() {
        Thread thread = new Thread(this);
        thread.setDaemon(false);
        thread.start();
    }

    /**
     * Add urls to crawl. <br>
     *
     * @param urls urls
     * @return this
     */
    public QueueExecutor addElement(String... urls) {
        for (String url : urls) {
            addElement(new ElementWarpper(url));
        }
        signalNewElement();
        return this;
    }
    
    public QueueExecutor addElement(List<String> urls) {
        for (String url : urls) {
            addElement(new ElementWarpper(url));
        }
        signalNewElement();
        return this;
    }

    /**
     * Download urls synchronizing.
     *
     * @param urls urls
     * @param <T> type of process result
     * @return list downloaded
     */
    public <T> List<T> getAll(Collection<String> urls) {
        destroyWhenExit = false;
        spawnElement = false;
        if (startElementWrappers!=null){
            startElementWrappers.clear();
        }
        for (ElementWarpper request : convertToElementWarppers(urls)) {
            addElement(request);
        }
        CollectorPipeline collectorPipeline = getCollectorPipeline();
        pipelines.add(collectorPipeline);
        run();
        spawnElement = true;
        destroyWhenExit = true;
        return collectorPipeline.getCollected();
    }

    protected CollectorPipeline getCollectorPipeline() {
        return new ResultItemsCollectorPipeline();
    }

    public <T> T get(String url) {
        List<String> urls = WMCollections.newArrayList(url);
        List<T> resultItemses = getAll(urls);
        if (resultItemses != null && resultItemses.size() > 0) {
            return resultItemses.get(0);
        } else {
            return null;
        }
    }

    /**
     * Add urls with information to crawl.<br>
     *
     * @param requests requests
     * @return this
     */
    public QueueExecutor addRequest(ElementWarpper... requests) {
        for (ElementWarpper request : requests) {
            addElement(request);
        }
        signalNewElement();
        return this;
    }

    private void waitNewElement() {
        newElementLock.lock();
        try {
            //double check
            if (threadPool.getThreadAlive() == 0 && exitWhenComplete) {
                return;
            }
            newElementCondition.await(emptySleepTime, TimeUnit.MILLISECONDS);
        } catch (InterruptedException e) {
            logger.warn("waitNewElement - interrupted, error {}", e);
        } finally {
            newElementLock.unlock();
        }
    }

    private void signalNewElement() {
        try {
            newElementLock.lock();
            newElementCondition.signalAll();
        } finally {
            newElementLock.unlock();
        }
    }

    public void start() {
        runAsync();
    }

    public void stop() {
        if (stat.compareAndSet(STAT_RUNNING, STAT_STOPPED)) {
            logger.info("Queue " + getUUID() + " stop success!");
        } else {
            logger.info("Queue " + getUUID() + " stop fail!");
        }
    }

    /**
     * start with more than one threads
     *
     * @param threadNum threadNum
     * @return this
     */
    public QueueExecutor thread(int threadNum) {
        checkIfRunning();
        this.threadNum = threadNum;
        if (threadNum <= 0) {
            throw new IllegalArgumentException("threadNum should be more than one!");
        }
        return this;
    }

    /**
     * start with more than one threads
     *
     * @param executorService executorService to run the spider
     * @param threadNum threadNum
     * @return this
     */
    public QueueExecutor thread(ExecutorService executorService, int threadNum) {
        checkIfRunning();
        this.threadNum = threadNum;
        if (threadNum <= 0) {
            throw new IllegalArgumentException("threadNum should be more than one!");
        }
        this.executorService = executorService;
        return this;
    }

    public boolean isExitWhenComplete() {
        return exitWhenComplete;
    }

    /**
     * Exit when complete. <br>
     * True: exit when all url of the site is downloaded. <br>
     * False: not exit until call stop() manually.<br>
     *
     * @param exitWhenComplete exitWhenComplete
     * @return this
     */
    public QueueExecutor setExitWhenComplete(boolean exitWhenComplete) {
        this.exitWhenComplete = exitWhenComplete;
        return this;
    }

    public boolean isSpawnElement() {
        return spawnElement;
    }

    /**
     * Get page count downloaded by spider.
     *
     * @return total downloaded page count
     * @since 0.4.1
     */
    public long getElementCount() {
        return elementCount.get();
    }

    /**
     * Get running status by spider.
     *
     * @return running status
     * @see Status
     * @since 0.4.1
     */
    public Status getStatus() {
        return Status.fromValue(stat.get());
    }


    public enum Status {
        Init(0), Running(1), Stopped(2);

        private Status(int value) {
            this.value = value;
        }

        private int value;

        int getValue() {
            return value;
        }

        public static Status fromValue(int value) {
            for (Status status : Status.values()) {
                if (status.getValue() == value) {
                    return status;
                }
            }
            //default value
            return Init;
        }
    }

    /**
     * Get thread count which is running
     *
     * @return thread count which is running
     * @since 0.4.1
     */
    public int getThreadAlive() {
        if (threadPool == null) {
            return 0;
        }
        return threadPool.getThreadAlive();
    }

    /**
     * Whether add urls extracted to download.<br>
     * Add urls to download when it is true, and just download seed urls when it is false. <br>
     * DO NOT set it unless you know what it means!
     *
     * @param spawnUrl spawnUrl
     * @return this
     * @since 0.4.0
     */
    public QueueExecutor setSpawnUrl(boolean spawnUrl) {
        this.spawnElement = spawnUrl;
        return this;
    }

    @Override
    public String getUUID() {
        if (uuid != null) {
            return uuid;
        }
        uuid = UUID.randomUUID().toString();
        return uuid;
    }

    public QueueExecutor setExecutorService(ExecutorService executorService) {
        checkIfRunning();
        this.executorService = executorService;
        return this;
    }

    @Override
    public Config getSite() {
        return config;
    }

    public List<QueueListener> getSpiderListeners() {
        return queueListeners;
    }

    public QueueExecutor setSpiderListeners(List<QueueListener> spiderListeners) {
        this.queueListeners = spiderListeners;
        return this;
    }
    
    public QueueExecutor addSpiderListener(QueueListener spiderListener) {
        this.queueListeners.add(spiderListener);
        return this;
    }

    public Date getStartTime() {
        return startTime;
    }

    public Scheduler getScheduler() {
        return scheduler;
    }

    /**
     * Set wait time when no url is polled.<br><br>
     *
     * @param emptySleepTime In MILLISECONDS.
     */
    public void setEmptySleepTime(int emptySleepTime) {
        this.emptySleepTime = emptySleepTime;
    }

	public QueueExecutor printPercent() {
		QueueExecutor me = this;
		Thread thread = new Thread(new Runnable() {
			@Override
			public void run() {
				while(me.getStatus() != Status.Stopped) {
					printSchedulerPercent();
					
					try {
						Thread.sleep(1000);
					} catch (InterruptedException e) {
						e.printStackTrace();
					}
				}
				
				printSchedulerPercent();
			}
		});
		thread.setDaemon(false);
		thread.start();
		
		return this;
	}
	
	private void printSchedulerPercent() {
		MonitorableScheduler scheduler = (MonitorableScheduler)this.getScheduler();
		
		int total = scheduler.getTotalElementsCount(this);
		if(total == 0) {
			System.out.println("["+name+"]preparing...");
			return;
		}
		
		int left = scheduler.getLeftElementsCount(this);
		int finish = total - left;
		double percent = NumberUtil.roundHalfDown(finish * 100.0 / total, 2);
		
		System.out.println("["+name+"]total: " + total + ", left: " + left + ", percent: " + percent + " %");
	}
}
