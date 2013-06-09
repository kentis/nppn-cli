package org.k1s.petriCode.generation

import javax.management.RuntimeErrorException;

import groovy.lang.Closure;
import groovy.text.GStringTemplateEngine;
import groovy.text.SimpleTemplateEngine
import groovy.text.Template

/**
 * Template manager that does common template operations
 * @author kent
 *
 */
class TemplateManager {

	def templateFinder 
	
	/**
	 * Finds a template givemn its name
	 * @param name
	 * @return
	 */
	def findTemplate(name){
		def template = templateFinder."$name"
		
		if(template instanceof String)
			return template
			
		if(template instanceof Closure)
			return template.call()
			
		throw new RuntimeException("could not find specified template $name".toString())
	}
	
	
	/**
	 * Runs a template
	 * @param template
	 * @param params
	 * @return
	 */
	def runTemplate(template, params){
		def retval = null
		try{
			def tmpl = new File(template).text
		
			SimpleTemplateEngine engine = new SimpleTemplateEngine()
			Template simpleTemplate = engine.createTemplate(tmpl)
			retval = simpleTemplate.make(params).toString()
		} catch(Exception e){
			throw new RuntimeException("Exception running template: $template with $params", e)
		}
		return retval
	}
	
//	def runTemplate(template, params){
//		if(template instanceof String){
//			
//		}
//		def engine = new GStringTemplateEngine()
//		def binding = [params: params]
//		
//		
//		def generated = engine.createTemplate(template).make(binding)
//		
//		return generated.toString()
//	}
//	
	/**
	 * Finds and then runs a template
	 * @param templateName
	 * @param params
	 * @return
	 */
	def findAndRunTemplate(templateName, params){
		return runTemplate(findTemplate(templateName), params)
	}
	
}
