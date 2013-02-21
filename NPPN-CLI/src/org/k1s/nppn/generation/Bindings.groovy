package org.k1s.nppn.generation

import groovy.lang.Closure;

class Bindings {

	def bindings = [:]
	def prag2Binding = [:]
	
	def leftShift(binding){
		add(binding)
	}
	
	def add(Binding binding){
		bindings[binding.name] = binding
		prag2Binding[binding.pragmatic] = binding
	}
	
}
class Binding{
	def name
	def template
	def pragmatic
	def dependencies
	def isContainer = false
	def isMultiContainer = false
	
	def toFowl(){
		StringBuilder fowl = new StringBuilder()
		fowl.append """Declaration( NamedIndividual( :${name}))
	ClassAssertion( :TemplateBinding :${name})
	ObjectPropertyAssertion( :pragmatic :${name} nppn:${pragmatic} )
	DataPropertyAssertion(:template :${name} \"${template}\"^^xsd:string)
	DataPropertyAssertion(:container :${name} "${isContainer}"^^xsd:boolean)
	"""
		if(dependencies != null){
			fowl.append "DataPropertyAssertion(:dependencies :${name} \"${dependencies}\"^^xsd:string)"
		}
		fowl.append "\n\n"
		return fowl.toString()
	}
	
	def getTemplate(){
		if(template instanceof Closure) return template.call()
		return template
	}
	
	
}
