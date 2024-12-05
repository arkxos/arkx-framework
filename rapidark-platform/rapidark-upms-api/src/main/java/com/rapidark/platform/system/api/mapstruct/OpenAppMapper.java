package com.rapidark.platform.system.api.mapstruct;

import com.rapidark.platform.system.api.dto.OpenAppDto;
import com.rapidark.framework.common.model.BaseMapper;
import com.rapidark.platform.system.api.entity.OpenApp;
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