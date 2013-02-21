package org.k1s.nppn.blocks

import org.k1s.nppn.generation.CodeGenerator;
import org.k1s.nppn.generation.TemplateManager

class Service extends Sequence {
	def name
	
	def start_node
	def end_node
	
	def node
	
	def text
	def generateCode(bindings){
	//	println name
		//println bindings.prag2Binding
		def binding = bindings.prag2Binding["service"]
		//println binding
		this.text = new TemplateManager().runTemplate(binding.template,['name':CodeGenerator.nameToFilename(name)])
    //	println this.text
		return this.text
	}
}
