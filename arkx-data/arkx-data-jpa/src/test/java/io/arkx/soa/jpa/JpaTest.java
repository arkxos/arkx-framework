package io.arkx.soa.jpa;

import java.util.List;
import java.util.Map;

import org.aspectj.lang.annotation.Before;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.test.context.ContextConfiguration;

import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * .
 * <p/>
 *
 * @author <a href="mailto:stormning@163.com">stormning</a>
 * @version V1.0, 16/3/15.
 */
//@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(classes = AppConfig.class)
public class JpaTest {
	
	@Autowired
	private SampleRepository sampleRepository;

	@Before("")
    public void addSomeSample() {
		sampleRepository.deleteAll();
		for (int i = 0; i < 10; i++) {
			Sample sample = new Sample();
			sample.setContent("hello world" + i);
			sampleRepository.save(sample);
		}
	}

	@Test
	public void findByTemplateQuery() {
		Page<Sample> samples = sampleRepository.findByContent("%world%", PageRequest.of(0, 100));
		assertTrue(samples.getTotalElements() == 10);
	}

	@Test
	public void countByTemplateQuery() {
		long count = sampleRepository.countContent("%world%");
		assertTrue(count == 10);
	}

	@Test
	public void findByTemplateQueryAndReturnDTOs() {
		List<SampleDTO> dtos = sampleRepository.findDtos();
		assertTrue(dtos.size() == 10);
	}

	@Test
	public void findByTemplateQueryWithTemplateQueryObject() {
		SampleQuery sq = new SampleQuery();
		sq.setContent("world");
		List<Sample> samples = sampleRepository.findByTemplateQueryObject(sq, null);
		assertTrue(samples.size() == 10);
	}

	@Test
	public void findBySpringElQuery() {
		List<Sample> dtos = sampleRepository.findDtos2("%world%");
		assertTrue(dtos.size() == 10);
	}

	@Test
	public void findMap(){
		List<Map<String,Object>> listMaps = sampleRepository.findMap();
		assertTrue(listMaps.size() == 10);
	}

}
