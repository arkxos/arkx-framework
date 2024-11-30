package com.bsd.comment.server.model.dto;

import com.bsd.comment.server.validation.constraints.TopicType;
import io.swagger.v3.oas.annotations.media.Schema;

import lombok.Data;

import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;

/**
 * 评论对象DTO
 *
 * @Author: linrongxin
 * @Date: 2019/9/9 14:37
 */
@Data
@Schema
public class CommentDTO {
    @NotNull(message = "评论主题ID不能为空")
    @Schema(required = true, name = "topicId", title = "评论主题ID(商品,课程,活动,文章对应ID)", example = "68")
    private Long topicId;

    @NotEmpty(message = "评论主题名称不能为空")
    @Schema(required = true, name = "topicName", title = "评论主题名称", example = "商品")
    private String topicName;

    @TopicType(message = "主题类型类型错误(商品:goods,课程:course,活动:activity,帖子:article)")
    @NotEmpty(message = "评论主题类型不能为空")
    @Schema(required = true, name = "topicType", title = "评论主题类型", example = "product")
    private String topicType;

    @NotEmpty(message = "评论主题子类型不能为空")
    @Schema(required = true, name = "topicSubType", title = "评论主题子类型", example = "product")
    private String topicSubType;

    @NotNull(message = "用户ID不能为空")
    @Schema(required = true, name = "userId", title = "用户ID", example = "1164480423881207844")
    private Long userId;

    @NotEmpty(message = "用户名称不能为空")
    @Schema(required = true, name = "userName", title = "用户名称", example = "用户1")
    private String userName;

    @NotEmpty(message = "评论内容不能为空")
    @Schema(required = true, name = "content", title = "评论内容", example = "商品XXX不错,质量很好")
    private String content;

    @NotNull(message = "来源不能为空")
    @Schema(required = true, name = "source", title = "来源(1.客户端APP 2.PC 3.WAP 4.unknow)", example = "1")
    private Integer source;
}
