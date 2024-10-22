package com.rapidark.soa.jpa;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;

import com.rapidark.framework.data.jpa.entity.BaseEntity;

/**
 * .
 * <p/>
 *
 * @author <a href="mailto:stormning@163.com">stormning</a>
 * @version V1.0, 16/3/15.
 */
@Entity
@Table(name = "t_sample")
public class Sample extends BaseEntity {
//	@Id
//	@GeneratedValue(strategy = GenerationType.AUTO)
//	private long id;

	@Column
	private String content;

//	public long getId() {
//		return id;
//	}
//
//	public void setId(long id) {
//		this.id = id;
//	}

	public String getContent() {
		return content;
	}

	public void setContent(String content) {
		this.content = content;
	}
}
