package io.arkx.framework.queue.saga;

import io.arkx.framework.queue2.MessageBus;
import io.arkx.framework.queue2.Subscribe;
import org.aspectj.lang.annotation.After;
import org.aspectj.lang.annotation.Before;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class PhoneNumberSagaTest {

	private MatchtedPhoneNumberCounter matchtedPhoneNumberCounter;
    private PhoneNumberExecutive phoneNumberExecutive;
    private PhoneNumberFinder phoneNumberFinder;
    private TotalPhoneNumbersCounter totalPhoneNumbersCounter;

    private static String[] phoneNumbers = new String[] {
        "303-555-1212   John",
        "212-555-1212   Joe",
        "718-555-1212   Zoe",
        "720-555-1212   Manny",
        "312-555-1212   Jerry",
        "303-555-9999   Sally"
    };

    public PhoneNumberSagaTest() {
        super();
    }

    public void testPhoneNumbersCounter() throws Exception {
        String processId = this.phoneNumberExecutive.start(phoneNumbers);

        Thread.sleep(1000L);

        PhoneNumberProcess process = this.phoneNumberExecutive.processOfId(processId);

        assertNotNull(process);
        assertEquals(2, process.matchedPhoneNumbers());
        assertEquals(6, process.totalPhoneNumbers());
    }

    private void assertNotNull(PhoneNumberProcess process) {
    }

    @Before("")
    protected void setUp() throws Exception {
    	
        phoneNumberExecutive = new PhoneNumberExecutive();
        phoneNumberFinder = new PhoneNumberFinder();
        matchtedPhoneNumberCounter = new MatchtedPhoneNumberCounter();
        totalPhoneNumbersCounter = new TotalPhoneNumbersCounter();

        MessageBus.globalInstance().register(phoneNumberExecutive);
        MessageBus.globalInstance().register(phoneNumberFinder);
        MessageBus.globalInstance().register(matchtedPhoneNumberCounter);
        MessageBus.globalInstance().register(totalPhoneNumbersCounter);

    }

    @After("")
    protected void tearDown() throws Exception {
    }

    /**
     * 电话号码流程
     *  
     * @author Darkness
     * @date 2014-12-17 下午9:43:26
     * @version V1.0
     * @since ark 1.0
     */
    private class PhoneNumberProcess {

        private String id;// 流程id
        private int matchedPhoneNumbers;// 匹配的号码数
        private int totalPhoneNumbers;// 总号码数

        public PhoneNumberProcess() {
            super();

            this.id = UUID.randomUUID().toString().toUpperCase();
            this.matchedPhoneNumbers = -1;
            this.totalPhoneNumbers = -1;
        }

        public boolean isCompleted() {
            return this.matchedPhoneNumbers() >= 0 && this.totalPhoneNumbers() >= 0;
        }

        public String id() {
            return this.id;
        }

        public int matchedPhoneNumbers() {
            return this.matchedPhoneNumbers;
        }

        public void setMatchedPhoneNumbers(int aMatchedPhoneNumbersCount) {
            this.matchedPhoneNumbers = aMatchedPhoneNumbersCount;
        }

        public int totalPhoneNumbers() {
            return this.totalPhoneNumbers;
        }

        public void setTotalPhoneNumbers(int aTotalPhoneNumberCount) {
            this.totalPhoneNumbers = aTotalPhoneNumberCount;
        }
    }

    /**
     * 电话号码执行器
     *  
     * @author Darkness
     * @date 2014-12-17 下午9:42:45
     * @version V1.0
     * @since ark 1.0
     */
    private class PhoneNumberExecutive {

        private Map<String, PhoneNumberProcess> processes;

        public PhoneNumberExecutive() {
            super();

            this.processes = new HashMap<String, PhoneNumberProcess>();
        }

        public PhoneNumberProcess processOfId(String aProcessId) {
            return this.processes.get(aProcessId);
        }

        public String start(String[] aPhoneNumbers) {
        		// 创建流程
            PhoneNumberProcess process = new PhoneNumberProcess();

            synchronized (this.processes) {
                this.processes.put(process.id(), process);
            }

            String allPhoneNumbers = "";

            for (String phoneNumber : aPhoneNumbers) {
                if (!allPhoneNumbers.isEmpty()) {
                    allPhoneNumbers = allPhoneNumbers + "\n";
                }

                allPhoneNumbers = allPhoneNumbers + phoneNumber;
            }

            System.out.println("STARTED: " + process.id());
            
            MessageBus.globalInstance().publish(new AllPhoneNumbersListed(
                    process.id(),
                    allPhoneNumbers));

            return process.id();
        }

		@Subscribe(events = { AllPhoneNumbersCounted.class, MatchedPhoneNumbersCounted.class })
		public void filteredDispatch(PhoneNumberProcessEvent event) {

    		String processId = event.processId();
    		PhoneNumberProcess process = this.processes.get(processId);
    		
        	if(event instanceof AllPhoneNumbersCounted) {
        		AllPhoneNumbersCounted allPhoneNumbersCounted = (AllPhoneNumbersCounted)event;
        		
        		process.setTotalPhoneNumbers(allPhoneNumbersCounted.totalPhoneNumbers());
                System.out.println("AllPhoneNumbersCounted...");
    		}else if(event instanceof MatchedPhoneNumbersCounted) {
    			MatchedPhoneNumbersCounted matchedPhoneNumbersCounted = (MatchedPhoneNumbersCounted)event;
        		
        		process.setMatchedPhoneNumbers(matchedPhoneNumbersCounted.matchedPhoneNumbers());
                System.out.println("MatchedPhoneNumbersCounted...");
    		}
        	
            if (process.isCompleted()) {
                System.out.println(
                        "Process: "
                        + process.id()
                        + ": "
                        + process.matchedPhoneNumbers()
                        + " of "
                        + process.totalPhoneNumbers()
                        + " phone numbers found.");
            }
        }

    }

    /**
     * 查找指定号码处理器
     *  
     * @author Darkness
     * @date 2014-12-17 下午9:59:06
     * @version V1.0
     * @since ark 1.0
     */
    private class PhoneNumberFinder {

        @Subscribe(events = { AllPhoneNumbersListed.class })
        public void filteredDispatch(AllPhoneNumbersListed event) {
            System.out.println("AllPhoneNumbersListed (to match)...");

            String allPhoneNumbers = event.allPhoneNumbers();

            String[] allPhoneNumbersToSearch = allPhoneNumbers.split("\n");

            String foundPhoneNumbers = "";

            for (String phoneNumber : allPhoneNumbersToSearch) {
                if (phoneNumber.contains("303-")) {
                    if (!foundPhoneNumbers.isEmpty()) {
                        foundPhoneNumbers = foundPhoneNumbers + "\n";
                    }
                    foundPhoneNumbers = foundPhoneNumbers + phoneNumber;
                }
            }

            MessageBus.globalInstance().publish(new PhoneNumbersMatched(
                    event.processId(),
                    foundPhoneNumbers));
        }

    }

    /**
     * 匹配的号码计数器
     *  
     * @author Darkness
     * @date 2014-12-17 下午10:02:00
     * @version V1.0
     * @since ark 1.0
     */
    private class MatchtedPhoneNumberCounter {

        @Subscribe(events = { PhoneNumbersMatched.class })
        public void filteredDispatch(PhoneNumbersMatched event) {

            System.out.println("PhoneNumbersMatched (to count)...");

            String allMatchedPhoneNumbers = event.matchedPhoneNumbers();

            String[] allPhoneNumbersToCount = allMatchedPhoneNumbers.split("\n");

            MessageBus.globalInstance().publish(new MatchedPhoneNumbersCounted(
            		event.processId(),
                    allPhoneNumbersToCount.length));
        }

    }

    /**
     * 所有号码计数器
     *  
     * @author Darkness
     * @date 2014-12-17 下午10:03:53
     * @version V1.0
     * @since ark 1.0
     */
    private class TotalPhoneNumbersCounter {

    	@Subscribe(events = { AllPhoneNumbersListed.class })
        public void filteredDispatch(AllPhoneNumbersListed event) {

            System.out.println("AllPhoneNumbersListed (to total)...");

            String allPhoneNumbers = event.allPhoneNumbers();

            String[] allPhoneNumbersToCount = allPhoneNumbers.split("\n");

            MessageBus.globalInstance().publish(new AllPhoneNumbersCounted(
            		event.processId(),
                    allPhoneNumbersToCount.length));
        }

    }
}
