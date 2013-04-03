package org.k1s.nppn.blocks

import org.k1s.nppn.generation.CodeGenerator;
import org.k1s.nppn.generation.TemplateManager;

/**
 * Principal
 * @author kent
 *
 */
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
	
	/**
	 * Code generator for principals
	 * @param bindings
	 * @return
	 */
	def generateCode(bindings){
		//println name
		//println bindings.prag2Binding
		def binding = bindings.prag2Binding["Principal"]
		//println binding
		this.text = new TemplateManager().runTemplate(binding.template,['name':CodeGenerator.nameToFilename(name), 'lcvs': lcvs, 'fields': states]).toString()
		//println this.text
		return this.text
	}
}
