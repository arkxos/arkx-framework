package io.arkx.framework.commons.crawl;

import io.arkx.framework.commons.crawl.scheduler.QueueScheduler;
import io.arkx.framework.commons.util.CountableThreadPool;
import io.arkx.framework.commons.util.LongTimeTask;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.ReentrantLock;

/**
 * 
 * @author Darkness
 * @date 2015-1-9 下午3:58:21
 * @version V1.0
 */
public class Spider extends LongTimeTask {

	public static Spider create(String taskName, String spiderName, PageProcessor pageProcessor) {
		return new Spider(taskName, spiderName, pageProcessor);
	}
	
	protected Logger logger = LoggerFactory.getLogger(getClass());

	private ReentrantLock newUrlLock = new ReentrantLock();
	private Condition newUrlCondition = newUrlLock.newCondition();
	private int emptySleepTime = 30000;
	
	private Site  site;
	private PageProcessor pageProcessor;
	private Pipeline pipeline;
	private DownloadFinishHandler downloadFinishHandler;
	private HttpClientDownloader downloader = new HttpClientDownloader();
	
	protected CountableThreadPool threadPool;

	private QueueScheduler scheduler;
	
	private String spiderName;
	
	Spider(String taskName, String spiderName, PageProcessor pageProcessor) {
		this.setType(spiderName);
		this.spiderName = spiderName;//"抓取数据 "
		this.pageProcessor = pageProcessor;
		this.site = pageProcessor.getSite();
		this.scheduler = new QueueScheduler();
		
		for (String url : this.site.startUrls()) {
			this.scheduler.push(new Request(url));
		}
	}
	
//	public Spider login(String loginUrl, String usernameField, String username, String passwordField, String password) {
//		this.downloader.login(loginUrl, usernameField, username, passwordField, password);
//		return this;
//	}
	
	public Spider headers(Map<String, String > headers) {
		this.downloader.headers(headers);
		return this;
	}
	
	public Spider setTypeJson() {
		this.downloader.setTypeJson();
		return this;
	}
	
	public Spider pipeline(Pipeline pipeline) {
		this.pipeline = pipeline;
		return this;
	}

	public Spider thread(int threadNum) {
		threadPool = new CountableThreadPool(threadNum);
		return this;
	}
	
	public Spider finish(DownloadFinishHandler downloadFinishHandler) {
		this.downloadFinishHandler = downloadFinishHandler;
		return this;
	}
	
	private void signalNewUrl() {
        try {
            newUrlLock.lock();
            newUrlCondition.signalAll();
        } finally {
            newUrlLock.unlock();
        }
    }
	
	protected void processRequest(Request request) {
		Page page = downloader.download(request, this.site);
		this.pageProcessor.process(page);
		
		if(this.pipeline != null) {
			this.pipeline.handle(page);
		}
		
		List<Request> links  = page.getTargetRequests();
		for (Request link : links) {
			this.scheduler.push(link);
		}
		
	}
	
	private void waitNewUrl() {
        newUrlLock.lock();
        try {
            //double check
            if (threadPool.getThreadAlive() == 0) {
                return;
            }
            newUrlCondition.await(emptySleepTime, TimeUnit.MILLISECONDS);
        } catch (InterruptedException e) {
            logger.warn("waitNewUrl - interrupted, error {}", e);
        } finally {
            newUrlLock.unlock();
        }
    }
	
	public void start() {
        runAsync();
    }
	
	public void runAsync() {
        Thread thread = new Thread(this);
        thread.setDaemon(false);
        thread.start();
    }

	public Spider addStartUrls(List<String> startUrls) {
		if(startUrls != null) {
			for (String startUrl : startUrls) {
				this.scheduler.push(new Request(startUrl));
			}
		}
		return this;
	}

	@Override
	protected void execute() {
		while(!Thread.currentThread().isInterrupted()) {
			Request request = scheduler.poll();
			if (request == null) {
				if (threadPool.getThreadAlive() == 0) {
                    break;
                }
				waitNewUrl();
			} else {
				int total = scheduler.getTotalRequestsCount();
				int left = scheduler.getLeftRequestsCount();
				int already = total-left;
				this.setPercent(Double.valueOf((already) * 100.0D /total).intValue());
				this.setCurrentInfo(this.spiderName+already+"/"+total);
//				System.out.println("总数："+scheduler.getTotalRequestsCount()+"，剩余："+scheduler.getLeftRequestsCount()+"，剩余百分比："+((scheduler.getLeftRequestsCount()-0.0)/scheduler.getTotalRequestsCount())+"%");
				final Request requestFinal = request;
                threadPool.execute(new Runnable() {
                    @Override
                    public void run() {
                        try {
                            processRequest(requestFinal);
                        } catch (Exception e) {
                            logger.error("process request " + requestFinal + " error", e);
                        } finally {
                            signalNewUrl();
                        }
                    }
                });
			}
		}
		
		if(this.downloadFinishHandler != null) {
			this.downloadFinishHandler.handle();
		}
		
		this.setPercent(100);
		
		close();
	}
	
	public void close() {
		threadPool.shutdown();
	}
	
	public static void main(String[] args) {
		Spider.create("AllSymbolsEastMoneyDownloader", "抓取东方财富股票代码", new PageProcessor() {
			
			@Override
			public void process(Page page) {
				System.out.println("ddd");
			}
			
			@Override
			public Site getSite() {
				Site site = new Site("http://quote.eastmoney.com");
				return site;
			}
		})
		.thread(1)
		.addStartUrls(Arrays.asList("http://quote.eastmoney.com/stocklist.html"))
		.finish(new DownloadFinishHandler() {
			@Override
			public void handle() {
				System.out.println("finished");
			}
		})
		.start();	
	}
}
