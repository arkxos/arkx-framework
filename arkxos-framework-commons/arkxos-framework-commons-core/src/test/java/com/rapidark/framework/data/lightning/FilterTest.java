package com.rapidark.framework.data.lightning;

import java.util.Collection;
import java.util.List;

import org.junit.jupiter.api.Test;

import com.google.common.base.Predicate;
import com.google.common.collect.Collections2;
import com.google.common.collect.Lists;
import com.rapidark.framework.data.lightning.filter.StringEqualFilter;

public class FilterTest {

	@Test
	public void whenFilterUsingMultiplePredicates_thenFiltered() {  
	    List<String> names = Lists.newArrayList("1", "2", "Adam", "Tom");  
//	    Collection<String> result = Collections2.filter(names,   
//	      Predicates.or(
//	    		  Predicates.containsPattern("J"),   
//	    		  Predicates.not(Predicates.containsPattern("a"))
//	     ));  
	    
	    
	    Predicate<String> predicate = new Predicate<String>() {  
	        @Override  
	        public boolean apply(String input) {  
	            return input.startsWith("A") || input.startsWith("J");  
	        }  
	    };  
	    Collection<String> result = Collections2.filter(names, new StringEqualFilter("John"));
//	    assertEquals(3, result.size());  
//	    assertThat(result, containsInAnyOrder("John", "Jane", "Tom"));  
	}  
	
}
