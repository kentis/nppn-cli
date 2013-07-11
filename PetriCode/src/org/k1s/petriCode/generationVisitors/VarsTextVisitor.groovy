package org.k1s.petriCode.generationVisitors	

import org.k1s.petriCode.blocks.Principal;
import org.k1s.petriCode.blocks.Service
import org.k1s.petriCode.generation.CodeGenerator

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
			
			if(template == null){
				throw new RuntimeException("DECLARATIONS template not found.")
			}
			
			def decls = element.declarations.clone()
			//println "element: $element"
			//println "element.decl: $decls"
			if(element.metaClass.hasProperty(element, 'parent') && element.parent){
				def parent  = element.parent
				while(parent && !(parent instanceof Principal)){
					//remove previously defined variables
					parent.declarations.each{key, value ->
						decls.remove(key) 
					}
					
					if(parent instanceof Service){
						parent.node.pragmatics[0].getArguments().split(",").each{
							decls.remove(it.trim())
						}
						
					}
					
					parent = parent.parent
					
				}
				if(parent instanceof Principal){
					//def principalDecls = []
					parent.states.each{
						//println "removing $it from $decls"
						decls.remove(CodeGenerator.removePrags(it.name.text.toString()).trim())
						//println "resulting in $decls"
					}
					//decls = decls - principalDecls
				}
				
				
			}
			
			if(element instanceof Service){
				element.node.pragmatics[0].getArguments().split(",").each{
					//println "removing $it from $decls"
					decls.remove it.trim()
					//println "leaving $decls"
				}
				
			}
			def tmpl = engine.createTemplate(new File(template.template))
			def text = tmpl.make([vars: decls, indentLevel: element.level, typeMap: bindings.typeMap])
			//println new File(template.template).text 
			//println decls
			//println bindings.typeMap
			//println "text: $text"
			element.declarationsText = text.toString()
			//println "done"
		}
	}

}
