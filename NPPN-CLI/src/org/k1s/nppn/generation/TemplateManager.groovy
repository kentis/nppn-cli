package org.k1s.nppn.generation

import javax.management.RuntimeErrorException;

import groovy.lang.Closure;
import groovy.text.GStringTemplateEngine;
import groovy.text.SimpleTemplateEngine
import groovy.text.Template

class TemplateManager {

	def templateFinder 
	
	def findTemplate(name){
		def template = templateFinder."$name"
		
		if(template instanceof String)
			return template
			
		if(template instanceof Closure)
			return template.call()
			
		throw new RuntimeException("could not find specified template $name".toString())
	}
	
	
	def runTemplate(template, params){
		def tmpl = new File(template).text
		
		SimpleTemplateEngine engine = new SimpleTemplateEngine()
		Template simpleTemplate = engine.createTemplate(tmpl)
		def retval = simpleTemplate.make(params)
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
	def findAndRunTemplate(templateName, params){
		return runTemplate(findTemplate(templateName), params)
	}
	
}
