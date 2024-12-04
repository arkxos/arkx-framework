package com.rapidark.cloud.base.client.service.mapstruct;

import com.rapidark.cloud.base.client.model.entity.OpenApp;
import com.rapidark.cloud.base.client.service.dto.OpenAppDto;
import com.rapidark.framework.common.model.BaseMapper;
import org.mapstruct.Mapper;
import org.mapstruct.ReportingPolicy;

/**
 * @website http://rapidark.com
 * @author Darkness
 * @date 2022-05-25
 **/
@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface OpenAppMapper extends BaseMapper<OpenAppDto, OpenApp> {

}