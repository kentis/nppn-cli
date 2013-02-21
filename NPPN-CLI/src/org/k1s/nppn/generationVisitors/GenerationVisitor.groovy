package org.k1s.nppn.generationVisitors



import groovy.text.SimpleTemplateEngine;
import groovy.text.Template;

class GenerationVisitor extends ODGVisitor{
	def bindings
	
	
	def visitElement(element, bindings){
		//println "visiting $element"
		element.generateCode(bindings)
	}

	
	
}
