package io.arkx.framework.commons.uid;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

/**
 *
 * @author Nobody
 * @version 1.0
 * @date 2025-10-05 19:36
 * @since 1.0
 */
@SpringBootTest
public class UidGeneratorBootTest {

    @Autowired
    private UidGenerator uidGenerator;

    @Test
    public void testSerialGenerate() throws InterruptedException {
        for(int i = 0;i<10;i++) {
            Thread.sleep(500);
            // Generate UID
            long uid = uidGenerator.getUID();

            // Parse UID into [Timestamp, WorkerId, Sequence]
            // {"UID":"180363646902239241","parsed":{    "timestamp":"2017-01-19 12:15:46",    "workerId":"4",    "sequence":"9"        }}
            System.out.println(uidGenerator.parseUID(uid));
        }
    }

    @Test
    public void testParse() {
        // 1974829747187777538L
        // 1818192800399368
        // 1820220024954880
        // 1821701788680192
        // 1821701788680192
        // 1974829747187777538
        // 1820220024954880
        long uid = 1974829747187777538L;
        System.out.println(uidGenerator.parseUID(uid));
    }
}
