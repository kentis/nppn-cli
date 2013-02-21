package org.k1s.nppn.blocks

import org.k1s.nppn.generation.CodeGenerator;
import org.k1s.nppn.generation.TemplateManager;

class Principal {
	def name
	def channels = []
	
	//services = children
	def services = []
	
	def getChildren(){
		return services
	}
	
	def setChildren(children){
		services = children
	}
	
	def lcvs = []
	def states = []
	
	def text
	
	def generateCode(bindings){
		//println name
		//println bindings.prag2Binding
		def binding = bindings.prag2Binding["Principal"]
		//println binding
		this.text = new TemplateManager().runTemplate(binding.template,['name':CodeGenerator.nameToFilename(name)])
		//println this.text
		return this.text
	}
}
