package org.ark.framework.ssi;

import java.io.PrintWriter;
import java.text.ParseException;

/**
 * @class org.ark.framework.ssi.SSIConditional
 * @author Darkness
 * @date 2013-1-31 下午12:33:53
 * @version V1.0
 */
public class SSIConditional implements SSICommand {

    public long process(SSIMediator ssiMediator, String commandName, String[] paramNames, String[] paramValues,
            PrintWriter writer) throws SSIStopProcessingException {
        long lastModified = System.currentTimeMillis();

        SSIConditionalState state = ssiMediator.getConditionalState();
        if ("if".equalsIgnoreCase(commandName)) {
            if (state.processConditionalCommandsOnly) {
                state.nestingCount += 1;
                return lastModified;
            }
            state.nestingCount = 0;

            if (evaluateArguments(paramNames, paramValues, ssiMediator)) {
                state.branchTaken = true;
            } else {
                state.processConditionalCommandsOnly = true;
                state.branchTaken = false;
            }
        } else if ("elif".equalsIgnoreCase(commandName)) {
            if (state.nestingCount > 0)
                return lastModified;

            if (state.branchTaken) {
                state.processConditionalCommandsOnly = true;
                return lastModified;
            }

            if (evaluateArguments(paramNames, paramValues, ssiMediator)) {
                state.processConditionalCommandsOnly = false;
                state.branchTaken = true;
            } else {
                state.processConditionalCommandsOnly = true;
                state.branchTaken = false;
            }
        } else if ("else".equalsIgnoreCase(commandName)) {
            if (state.nestingCount > 0)
                return lastModified;

            state.processConditionalCommandsOnly = state.branchTaken;

            state.branchTaken = true;
        } else if ("endif".equalsIgnoreCase(commandName)) {
            if (state.nestingCount > 0) {
                state.nestingCount -= 1;
                return lastModified;
            }

            state.processConditionalCommandsOnly = false;

            state.branchTaken = true;
        } else {
            throw new SSIStopProcessingException();
        }

        return lastModified;
    }

    private boolean evaluateArguments(String[] names, String[] values, SSIMediator ssiMediator)
            throws SSIStopProcessingException {
        String expr = getExpression(names, values);
        if (expr == null) {
            throw new SSIStopProcessingException();
        }
        try {
            ExpressionParseTree tree = new ExpressionParseTree(expr, ssiMediator);
            return tree.evaluateTree();
        } catch (ParseException e) {
        }
        throw new SSIStopProcessingException();
    }

    private String getExpression(String[] paramNames, String[] paramValues) {
        if ("expr".equalsIgnoreCase(paramNames[0]))
            return paramValues[0];
        return null;
    }

}
