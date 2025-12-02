package org.ark.framework.ssi;

import java.text.ParseException;
import java.util.LinkedList;
import java.util.List;

/**
 * @class org.ark.framework.ssi.ExpressionParseTree
 *
 * @author Darkness
 * @date 2013-1-31 下午12:32:29
 * @version V1.0
 */
public class ExpressionParseTree {
    private LinkedList<Node> nodeStack = new LinkedList();

    private LinkedList<Node> oppStack = new LinkedList();
    private Node root;
    private SSIMediator ssiMediator;
    private static final int PRECEDENCE_NOT = 5;
    private static final int PRECEDENCE_COMPARE = 4;
    private static final int PRECEDENCE_LOGICAL = 1;

    public ExpressionParseTree(String expr, SSIMediator ssiMediator) throws ParseException {
        this.ssiMediator = ssiMediator;
        parseExpression(expr);
    }

    public boolean evaluateTree() {
        return this.root.evaluate();
    }

    private void pushOpp(OppNode node) {
        if (node == null) {
            this.oppStack.add(0, node);
            return;
        }

        while (this.oppStack.size() != 0) {
            OppNode top = (OppNode) this.oppStack.get(0);

            if (top == null) {
                break;
            }
            if (top.getPrecedence() < node.getPrecedence()) {
                break;
            }
            this.oppStack.remove(0);

            top.popValues(this.nodeStack);

            this.nodeStack.add(0, top);
        }

        this.oppStack.add(0, node);
    }

    private void resolveGroup() {
        OppNode top = null;
        while ((top = (OppNode) this.oppStack.remove(0)) != null) {
            top.popValues(this.nodeStack);

            this.nodeStack.add(0, top);
        }
    }

    private void parseExpression(String expr) throws ParseException {
        StringNode currStringNode = null;

        pushOpp(null);
        ExpressionTokenizer et = new ExpressionTokenizer(expr);
        while (et.hasMoreTokens()) {
            int token = et.nextToken();
            if (token != 0)
                currStringNode = null;
            switch (token) {
                case 0 :
                    if (currStringNode == null) {
                        currStringNode = new StringNode(et.getTokenValue());
                        this.nodeStack.add(0, currStringNode);
                    } else {
                        currStringNode.value.append(" ");
                        currStringNode.value.append(et.getTokenValue());
                    }
                    break;
                case 1 :
                    pushOpp(new AndNode());
                    break;
                case 2 :
                    pushOpp(new OrNode());
                    break;
                case 3 :
                    pushOpp(new NotNode());
                    break;
                case 4 :
                    pushOpp(new EqualNode());
                    break;
                case 5 :
                    pushOpp(new NotNode());

                    this.oppStack.add(0, new EqualNode());
                    break;
                case 6 :
                    resolveGroup();
                    break;
                case 7 :
                    pushOpp(null);
                    break;
                case 8 :
                    pushOpp(new NotNode());

                    this.oppStack.add(0, new LessThanNode());
                    break;
                case 9 :
                    pushOpp(new NotNode());

                    this.oppStack.add(0, new GreaterThanNode());
                    break;
                case 10 :
                    pushOpp(new GreaterThanNode());
                    break;
                case 11 :
                    pushOpp(new LessThanNode());
                case 12 :
            }

        }

        resolveGroup();
        if (this.nodeStack.size() == 0) {
            throw new ParseException("No nodes created.", et.getIndex());
        }
        if (this.nodeStack.size() > 1) {
            throw new ParseException("Extra nodes created.", et.getIndex());
        }
        if (this.oppStack.size() != 0) {
            throw new ParseException("Unused opp nodes exist.", et.getIndex());
        }
        this.root = ((Node) this.nodeStack.get(0));
    }

    private final class AndNode extends ExpressionParseTree.OppNode {
        private AndNode() {
            super();
        }

        public boolean evaluate() {
            if (!this.left.evaluate())
                return false;
            return this.right.evaluate();
        }

        public int getPrecedence() {
            return 1;
        }

        public String toString() {
            return this.left + " " + this.right + " AND";
        }
    }

    private abstract class CompareNode extends ExpressionParseTree.OppNode {
        private CompareNode() {
            super();
        }

        protected int compareBranches() {
            String val1 = ((ExpressionParseTree.StringNode) this.left).getValue();
            String val2 = ((ExpressionParseTree.StringNode) this.right).getValue();
            return val1.compareTo(val2);
        }
    }

    private final class EqualNode extends ExpressionParseTree.CompareNode {
        private EqualNode() {
            super();
        }

        public boolean evaluate() {
            return compareBranches() == 0;
        }

        public int getPrecedence() {
            return 4;
        }

        public String toString() {
            return this.left + " " + this.right + " EQ";
        }
    }

    private final class GreaterThanNode extends ExpressionParseTree.CompareNode {
        private GreaterThanNode() {
            super();
        }

        public boolean evaluate() {
            return compareBranches() > 0;
        }

        public int getPrecedence() {
            return 4;
        }

        public String toString() {
            return this.left + " " + this.right + " GT";
        }
    }

    private final class LessThanNode extends ExpressionParseTree.CompareNode {
        private LessThanNode() {
            super();
        }

        public boolean evaluate() {
            return compareBranches() < 0;
        }

        public int getPrecedence() {
            return 4;
        }

        public String toString() {
            return this.left + " " + this.right + " LT";
        }
    }

    private abstract class Node {
        private Node() {
        }

        public abstract boolean evaluate();
    }

    private final class NotNode extends ExpressionParseTree.OppNode {
        private NotNode() {
            super();
        }

        public boolean evaluate() {
            return !this.left.evaluate();
        }

        public int getPrecedence() {
            return 5;
        }

        public void popValues(List<ExpressionParseTree.Node> values) {
            this.left = ((ExpressionParseTree.Node) values.remove(0));
        }

        public String toString() {
            return this.left + " NOT";
        }
    }

    private abstract class OppNode extends ExpressionParseTree.Node {
        ExpressionParseTree.Node left;
        ExpressionParseTree.Node right;

        private OppNode() {
            super();
        }

        public abstract int getPrecedence();

        public void popValues(List<ExpressionParseTree.Node> values) {
            this.right = ((ExpressionParseTree.Node) values.remove(0));
            this.left = ((ExpressionParseTree.Node) values.remove(0));
        }
    }

    private final class OrNode extends ExpressionParseTree.OppNode {
        private OrNode() {
            super();
        }

        public boolean evaluate() {
            if (this.left.evaluate())
                return true;
            return this.right.evaluate();
        }

        public int getPrecedence() {
            return 1;
        }

        public String toString() {
            return this.left + " " + this.right + " OR";
        }
    }

    private class StringNode extends ExpressionParseTree.Node {
        StringBuffer value;
        String resolved = null;

        public StringNode(String value) {
            super();
            this.value = new StringBuffer(value);
        }

        public String getValue() {
            if (this.resolved == null)
                this.resolved = ExpressionParseTree.this.ssiMediator.substituteVariables(this.value.toString());
            return this.resolved;
        }

        public boolean evaluate() {
            return getValue().length() != 0;
        }

        public String toString() {
            return this.value.toString();
        }
    }
}
