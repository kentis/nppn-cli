package org.k1s.nppn.blocks

import org.k1s.nppn.generation.CodeGenerator;
import org.k1s.nppn.generation.TemplateManager

class Atomic extends Block{
	def pragmatics = []
	def transition
	
	def text
	def generateCode(bindings){
		
		def tm = new TemplateManager()
		def text = new StringBuffer()
		transition.pragmatics.each{
			def binding = bindings.prag2Binding[it.name]
			if(binding){
				println it.name
				text.append tm.runTemplate(binding.template,[])
			} else {
				//throw new Exception("Unknown pragmatic: ${it.name}")
				println "WARNING: No binding given for pragmatic: ${it.name}"	
			}
		}
		
		this.text = text.toString() 
		return this.text
	}
}
