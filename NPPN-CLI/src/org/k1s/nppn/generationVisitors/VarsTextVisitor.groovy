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
			
			def decls = element.declarations.unique()
			if(element.metaClass.hasProperty(element, 'parent') && element.parent){
				//remove previously defined variables
				
				decls = decls - element.parent.declarations
				
			}
			def text = engine.createTemplate(new File(template.template)).make([vars: decls])
			
			element.declarationsText = text.toString()
		}
	}

}
