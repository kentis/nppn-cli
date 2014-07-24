package org.k1s.petriCode.generationVisitors



import groovy.text.SimpleTemplateEngine;
import groovy.text.Template;

/**
 * The generation visitor
 * @author kent
 *
 */
class GenerationVisitor extends ODGVisitor{
	def bindings
	
	/**
	 * visits
	 */
	def visitElement(element, bindings){
		//println "visiting $element"
		try{
			element.generateCode(bindings)
		}catch(Exception ex){
			throw new RuntimeException("Error generating code for $element",ex)
		}
	}

	
	
}
