package io.arkx.framework.data.db.command;

/**
 * 修改字段长度指令（只支持长度扩大）。
 *
 */
public class ChangeColumnLengthCommand extends ChangeColumnMandatoryCommand {

    public static final String Prefix = "ChangeColumnLength:";

    @Override
    public String getPrefix() {
        return Prefix;
    }

}
