package org.k1s.nppn.blocks

import org.k1s.nppn.generation.CodeGenerator;
import org.k1s.nppn.generation.ParameterStrategy;
import org.k1s.nppn.generation.TemplateManager
import org.k1s.nppn.generation.TemplateParameters;

class Atomic extends Block{
	def pragmatics = []
	def transition
	def parent
	
	def text
	def generateCode(bindings){
		
		def tm = new TemplateManager()
		def text = new StringBuffer()
		transition.pragmatics.each{
			if(it.name != 'service'){
				def binding = bindings.prag2Binding[it.name]
				println it.name
				if(binding){
					def params = new TemplateParameters(bindings).getParamtersFor(it, transition, binding.parameterStrategy)
					println params
					text.append tm.runTemplate(binding.template,params ? params : [:])
				} else {
					//throw new Exception("Unknown pragmatic: ${it.name}")
					println "WARNING: No binding given for pragmatic: ${it.name}"	
				}
			}
		}
		
		this.text = text.toString() 
		return this.text
	}
}
