package org.k1s.nppn.generationVisitors	

import groovy.text.SimpleTemplateEngine;


/**
 * Variables visitor pass 2
 * @author kent
 *
 */
class VarsTextVisitor extends ODGVisitor{
	def bindings
	
	def visitElement(element, bindings){
		
		//set declarationText on all Containers
		if(element.metaClass.hasProperty(element, "declarationsText")){
			SimpleTemplateEngine engine = new SimpleTemplateEngine()

			def template = bindings.bindings['DECLARATIONS']
			//println "DECLARING: for ${element.correspondingNetElement.name.text} ${element.declarations}"
			def text = engine.createTemplate(new File(template.template)).make([vars: element.declarations.unique()])
			
			element.declarationsText = text.toString()
		}
	}

}
