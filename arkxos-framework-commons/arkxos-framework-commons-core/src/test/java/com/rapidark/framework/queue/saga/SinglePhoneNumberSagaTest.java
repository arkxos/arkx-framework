package com.rapidark.framework.queue.saga;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

import java.util.UUID;

import org.aspectj.lang.annotation.Before;
import org.junit.jupiter.api.Test;

import com.arkxos.framework.queue2.MessageBus;
import com.arkxos.framework.queue2.Subscribe;

public class SinglePhoneNumberSagaTest {

    private PhoneNumberExecutive phoneNumberExecutive;

    @Test
    public void counterPhoneNumbers() throws Exception {
    	String[] phoneNumbers = new String[] {
    	        "303-555-1212   John",
    	        "212-555-1212   Joe",
    	        "718-555-1212   Zoe",
    	        "720-555-1212   Manny",
    	        "312-555-1212   Jerry",
    	        "303-555-9999   Sally"
    	    };
    	
        this.phoneNumberExecutive.start(phoneNumbers);

        assertNotNull(this.phoneNumberExecutive);
        assertEquals(2, this.phoneNumberExecutive.matchedPhoneNumbers());
        assertEquals(6, this.phoneNumberExecutive.totalPhoneNumbers());
    }

    @Before("")
    public void setUp() throws Exception {
        phoneNumberExecutive = new PhoneNumberExecutive();
        MessageBus.globalInstance().register(phoneNumberExecutive);
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

    	private String id;// 流程id
        private int matchedPhoneNumbers;// 匹配的号码数
        private int totalPhoneNumbers;// 总号码数
        
        public PhoneNumberExecutive() {
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

        public String start(String[] aPhoneNumbers) {
            String allPhoneNumbers = "";

            for (String phoneNumber : aPhoneNumbers) {
                if (!allPhoneNumbers.isEmpty()) {
                    allPhoneNumbers = allPhoneNumbers + "\n";
                }

                allPhoneNumbers = allPhoneNumbers + phoneNumber;
            }

            String processId = UUID.randomUUID().toString().toUpperCase();
            System.out.println("STARTED: " + processId);
            
            MessageBus.globalInstance().publish(new AllPhoneNumbersListed(
            		processId,
                    allPhoneNumbers));

            return processId;
        }

		@Subscribe(events = { AllPhoneNumbersCounted.class, MatchedPhoneNumbersCounted.class })
		public void filteredDispatch(PhoneNumberProcessEvent event) {
        	if(event instanceof AllPhoneNumbersCounted) {
        		AllPhoneNumbersCounted allPhoneNumbersCounted = (AllPhoneNumbersCounted)event;
        		
        		this.setTotalPhoneNumbers(allPhoneNumbersCounted.totalPhoneNumbers());
                System.out.println("AllPhoneNumbersCounted...");
    		}else if(event instanceof MatchedPhoneNumbersCounted) {
    			MatchedPhoneNumbersCounted matchedPhoneNumbersCounted = (MatchedPhoneNumbersCounted)event;
        		
    			this.setMatchedPhoneNumbers(matchedPhoneNumbersCounted.matchedPhoneNumbers());
                System.out.println("MatchedPhoneNumbersCounted...");
    		}
        	
            if (this.isCompleted()) {
                System.out.println(
                        "Process: "
                        + this.id()
                        + ": "
                        + this.matchedPhoneNumbers()
                        + " of "
                        + this.totalPhoneNumbers()
                        + " phone numbers found.");
            }
        }
		
		//查找指定号码处理器
        @Subscribe(events = { AllPhoneNumbersListed.class })
        public void finderPhoneNumber(AllPhoneNumbersListed event) {
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
        
        //匹配的号码计数器
        @Subscribe(events = { PhoneNumbersMatched.class })
        public void CounterMatchtedPhoneNumber(PhoneNumbersMatched event) {

            System.out.println("PhoneNumbersMatched (to count)...");

            String allMatchedPhoneNumbers = event.matchedPhoneNumbers();

            String[] allPhoneNumbersToCount = allMatchedPhoneNumbers.split("\n");

            MessageBus.globalInstance().publish(new MatchedPhoneNumbersCounted(
            		event.processId(),
                    allPhoneNumbersToCount.length));
        }
        
    	//所有号码计数器
    	@Subscribe(events = { AllPhoneNumbersListed.class })
        public void CounterTotalPhoneNumbers(AllPhoneNumbersListed event) {

            System.out.println("AllPhoneNumbersListed (to total)...");

            String allPhoneNumbers = event.allPhoneNumbers();

            String[] allPhoneNumbersToCount = allPhoneNumbers.split("\n");

            MessageBus.globalInstance().publish(new AllPhoneNumbersCounted(
            		event.processId(),
                    allPhoneNumbersToCount.length));
        }

    }

}
