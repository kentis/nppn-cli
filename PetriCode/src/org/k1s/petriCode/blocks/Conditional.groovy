package org.k1s.petriCode.blocks

import groovy.text.SimpleTemplateEngine;
import groovy.text.Template;

import org.k1s.petriCode.generation.TemplateManager
import org.k1s.petriCode.generation.Conditionals

/**
 * Conditional block
 * @author kent
 *
 */
class Conditional extends Block {
	def pragmatic
	def parent
	Map<Object, Sequence> sequences = [:]
	

	def declarationsText
	def declarations = [:]

	
	def getChildren(){
		def retval = []
		sequences.each{k,v ->
			retval << v
		}
		return retval
	}
		
	def generateCode(bindings){
/*		def binding = bindings.prag2Binding["Cond"]
		
		def translateExpr = Conditionals.translateExpr(start.pragmatics[0], bindings)
		
		this.text = new TemplateManager().runTemplate(binding.template,['cond':translateExpr, first: false, e:translateExpr  ]).toString()

		return this.text
	*/
		
//		def retval = ""
//		SimpleTemplateEngine engine = new SimpleTemplateEngine()
//		def pragmatic = start.pragmatics[0]
//		def condStr = null
//		
//		if(pragmatic.name == "Id"){
//		
//			def args = pragmatic.arguments
//			println args
//			def map = new GroovyShell().evaluate("return [$args]")
//			println map
//			condStr = map.cond
//			println condStr
//		} else if(pragmatic.name == "Cond"){
//			def args = pragmatic.arguments
//			condStr = new GroovyShell().evaluate("return $args")
//		}
//		//println condStr
//		def conds = Conditionals.parse(condStr)
//		println conds.conds
//		//println "BB: ${bindings.bindings}"
//		def exprTmpl = new File(bindings.bindings.EXPR.template).text
//		def condTmpl = new File(bindings.bindings.COND.template).text
//		def trueTmpl = new File(bindings.bindings.TRUE.template).text
//		def first = true
//		conds.conds.each {
//			
//			Template exprTemplate = engine.createTemplate(exprTmpl)
//			def exprText = exprTemplate.make([cond: it, first: first, t: trueTmpl]).toString()
//
//			Template simpleTemplate = engine.createTemplate(condTmpl)
//			def templateText = simpleTemplate.make([cond: it, first: first, t: trueTmpl, e: exprText]).toString()
//			retval += templateText
//			first = false
//		}
//		
//		this.text = retval
//		return retval
		
		def conds = Conditionals.translatePrags(start.pragmatics[0], bindings)
		
		
		this.text = conds
		return conds
	}
	
}
