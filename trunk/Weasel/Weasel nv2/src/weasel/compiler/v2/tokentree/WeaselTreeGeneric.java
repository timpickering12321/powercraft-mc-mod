package weasel.compiler.v2.tokentree;

import java.util.ArrayList;
import java.util.List;
import java.util.ListIterator;

import weasel.compiler.WeaselCompilerException;
import weasel.compiler.WeaselOperator;
import weasel.compiler.WeaselToken;
import weasel.compiler.WeaselTokenType;

public class WeaselTreeGeneric {

	private List<TreeGenericElement> list = new ArrayList<TreeGenericElement>();
	public boolean close;
	
	public WeaselTreeGeneric(ListIterator<WeaselToken> iterator) throws WeaselCompilerException {
		WeaselToken token = iterator.next();
		if(!(token.tokenType==WeaselTokenType.OPERATOR && (token.param==WeaselOperator.GREATER || token.param==WeaselOperator.RSHIFT))){
			iterator.previous();
			do{
				TreeGenericElement tge = new TreeGenericElement(iterator);
				list.add(tge);
				if(tge.close)
					return;
				token = iterator.next();
			}while(token.tokenType==WeaselTokenType.COMMA);
			if(!(token.tokenType==WeaselTokenType.OPERATOR && (token.param==WeaselOperator.GREATER || token.param==WeaselOperator.RSHIFT))){
				throw new WeaselCompilerException(token.line, "Expect > but got %s", token);
			}
		}
		close = token.tokenType==WeaselTokenType.OPERATOR && token.param==WeaselOperator.RSHIFT;
	}
	
	@Override
	public String toString() {
		String s = "<";
		if(!list.isEmpty()){
			s += list.get(0).toString();
			for(int i=1; i<list.size(); i++){
				s += ", "+list.get(i).toString();
			}
		}
		return s+">";
	}



	private static class TreeGenericElement{

		private List<WeaselToken> list = new ArrayList<WeaselToken>();
		private WeaselTreeGeneric generic;
		public boolean close;
		
		public TreeGenericElement(ListIterator<WeaselToken> iterator) throws WeaselCompilerException {
			WeaselToken token = iterator.next();
			if(token.tokenType!=WeaselTokenType.IDENT){
				throw new WeaselCompilerException(token.line, "Expect Ident but got %s", token);
			}
			list.add(token);
			token = iterator.next();
			while(token.tokenType==WeaselTokenType.OPERATOR && token.param==WeaselOperator.ELEMENT){
				token = iterator.next();
				if(token.tokenType!=WeaselTokenType.IDENT){
					throw new WeaselCompilerException(token.line, "Expect Ident but got %s", token);
				}
				list.add(token);
				token = iterator.next();
			}
			if(token.tokenType==WeaselTokenType.OPERATOR && token.param==WeaselOperator.LESS){
				generic = new WeaselTreeGeneric(iterator);
				close = generic.close;
			}else{
				iterator.previous();
			}
		}

		@Override
		public String toString() {
			String s = list.get(0).toString();
			for(int i=1; i<list.size(); i++){
				s += "."+list.get(i).toString();
			}
			return s+(generic==null?"":generic.toString());
		}
		
	}
	
}
