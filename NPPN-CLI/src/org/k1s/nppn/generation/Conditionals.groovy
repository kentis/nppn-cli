

package org.k1s.nppn.generation

import java.util.logging.Logger;

import org.k1s.GrSexp.Atom;
import org.k1s.GrSexp.GrSexp;

import groovy.lang.GroovyShell;
import groovy.text.SimpleTemplateEngine;
import groovy.text.Template;
import groovy.util.logging.*;

@Log
class Conditionals {
	
	def conds = []
	
	static def translatePrags(pragmatic, bindings){
		def retval = ""
		SimpleTemplateEngine engine = new SimpleTemplateEngine()
		
		def condStr = null

		if(pragmatic.name == "Id"){

			def args = pragmatic.arguments
			def map = new GroovyShell().evaluate("return [$args]")

			condStr = map.cond
		} else if(pragmatic.name == "Cond"){
			def args = pragmatic.arguments
			
			condStr = new GroovyShell().evaluate("return $args")
		} 
	
		def conds = parse(condStr)
		
		
		/*def iftmpl = '''
			${first ? '' : 'else '}if(${cond.e == 't'? t : cond.e}){ ${cond.p} }
		'''
		def trueTmpl = '''true'''
		
		def exprTmpl = '''<%
			def e = cond.e.split(' ')
			def verb = null
			switch(e[0]){
				case 'eq':
				 verb = "=="
				 break
				case 'neq': 
				 verb = "!="
				 break
				case 't':
				 verb = ''
				 e = [verb, 'true']
				 break
				default:
				 verb = ''
				 e = [verb, e[0]]
				 break
			} 
			for(def i = 1; i < e.size(); i++){%> ${e[i]} ${i < (e.size() -1) ? verb : ''}<%}%>'''  
		*/

		def exprTmpl = new File(bindings.bindings.EXPR.template).text
		
		def condTmpl = new File(bindings.bindings.COND.template).text
		def trueTmpl = new File(bindings.bindings.TRUE.template).text
		def first = true
		conds.conds.each { 
			
			Template exprTemplate = engine.createTemplate(exprTmpl)
			def exprText = exprTemplate.make([cond: it, first: first, t: trueTmpl]).toString()
			
			Template simpleTemplate = engine.createTemplate(condTmpl)
			def templateText = simpleTemplate.make([cond: it, first: first, t: trueTmpl, e: exprText]).toString()
			retval += templateText
			first = false
		}
		return retval
	}
	
	static def execTemplate(tmplName, conds){
		SimpleTemplateEngine engine = new SimpleTemplateEngine()
		def tmpl = new File(bindings.bindings."$tmplName".template).text
		tmpl = engine.createTemplate(tmpl)
		return tmpl.make(conds).text
	}
	
	static def translateExpr(pragmatic, bindings){
		def args = pragmatic.arguments
		args = args.replaceAll("\n", " ")
		
		def map = new GroovyShell().evaluate("return [$args]")

		def exprStr = map.cond
		def exprTmpl = new File(bindings.bindings.STMT.template).text
		
		SimpleTemplateEngine engine = new SimpleTemplateEngine()
		
		
		def trueTmpl = new File(bindings.bindings.TRUE.template).text
		Template exprTemplate = engine.createTemplate(exprTmpl)
		
		
		def stmt = parseExpr(exprStr)
		
		def exprText = exprTemplate.make([stmt: parseExpr(exprStr), t: trueTmpl]).toString()
		

		return exprText
		
	}
	
	static def parseExpr(exprStr){

		def exprs = []
		def currExpr
		def parentExprs = []
		def currToken = ""
		exprStr.trim().each{
			if(it == '('){
				if(currExpr != null) parentExprs.push(currExpr)
				currExpr = new Expr()
				
			}else if(it == ')'){
				currExpr.args << currToken
				currToken = ""
				def finishedExpr = currExpr
				if(parentExprs.size() > 0){
					currExpr = parentExprs.pop()
					currExpr.args << finishedExpr
				}else{
				 	exprs << currExpr
				}
			}else if(it == ' '){
				if(currExpr.oper == null){
					currExpr.oper = currToken
				}else {
					if(currToken.trim().size() > 0)
					currExpr.args << currToken
				}
				currToken = ""
			} else {
			    currToken += it
			}
		}
		
		
		return exprs[0]
	}
	
	
	static final int COND = 0
	static final int EXPR = 1
	
	static def parse(cond){
		def conds = new Conditionals()
		def currCond = null
		def currToken = ""
		
		def s_expr = new GrSexp().parse(cond)
		
		s_expr.each{
			def con = new Conditional()
			Expr e = new Expr()
			con.e = e
			
			e.oper = it[0]
			
			def size = it.size()
			def i = 1
			while(i < size -1){
				if(it[i] instanceof Atom){
					e.args << it[i]
				} else {
					e.args << getExpr(it[i])
				}
				i += 1
			}
			con.p = it.last()
			conds.conds << con
		}
		
//		def state = COND
//		cond.each{
//			if(it == '(' && currCond == null){
//				//new condition
//				currCond = new Conditional()
//			}else if(it == '(') {
//				state = EXPR
//			} else if(it == ')'){
//				if(state == EXPR){
//					currCond.e = currToken
//					currToken = ""
//					state = COND
//				} else 	if(state == COND){
//					if(currToken.size() > 0){
//						currCond.p = currToken
//					}
//					conds.conds << currCond
//					currToken = ""
//					currCond = null
//				}
//			}else if(it == ' ' && state == COND && currCond != null){
//			    if(currCond.e == null) currCond.e = currToken
//				else currCond.p = currToken
//				
//				currToken = ""
//			} else  if(it == ' ' && currCond == null){
//				//noop
//			} else {
//			    currToken += it
//			}
//			
//		}
		return conds
	}
	
	
	static Expr getExpr(lst){
		Expr e = new Expr()
					
		e.oper = lst[0]
			
		def size = lst.size()
		def i = 1
		while(i < size ){
			if(lst[i] instanceof Atom){
				e.args << lst[i]
			} else {
				e.args << getExpr(lst[i])
			}
			i += 1
		}
		return e
	}
}
class Conditional{
	def e 
	def p 
	
	String toString(){
		"(e: $e, p: $p)"
	}
}

class Expr {
	def oper
	def args = []
	
	String toString(){
		"|'${oper}' $args|"
	}
}
