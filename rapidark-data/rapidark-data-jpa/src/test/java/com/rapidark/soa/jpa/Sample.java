package com.rapidark.soa.jpa;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;

import com.rapidark.framework.data.jpa.entity.BaseEntity;
import com.rapidark.framework.data.jpa.entity.IdLongEntity;

/**
 * .
 * <p/>
 *
 * @author <a href="mailto:stormning@163.com">stormning</a>
 * @version V1.0, 16/3/15.
 */
@Entity
@Table(name = "t_sample")
public class Sample extends IdLongEntity {
//	@Id
//	@GeneratedValue(strategy = GenerationType.AUTO)
//	private long id;

	@Column
	private String content;

	public String getContent() {
		return content;
	}

	public void setContent(String content) {
		this.content = content;
	}
}
