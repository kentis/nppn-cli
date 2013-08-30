package org.k1s.petriCode.blocks

import org.k1s.petriCode.generation.CodeGenerator;
import org.k1s.petriCode.generation.TemplateManager;

/**
 * Principal
 * @author kent
 *
 */
class Principal {
	def name
	def channels = []
	def level = 1
	//services = children
	List<Service> services = []
	
	def lcvs = []
	def states = []
	
	def text
	
	def getChildren(){
		return services
	}
	
	def setChildren(children){
		services = children
	}
	
	
	
	/**
	 * Code generator for principals
	 * @param bindings
	 * @return
	 */
	def generateCode(bindings){
		//println name
		//println bindings.prag2Binding
		def binding = bindings.prag2Binding["principal"]
		//println binding
		this.text = new TemplateManager().runTemplate(binding.template,['name':CodeGenerator.nameToFilename(name), 
	                                                                    'lcvs': lcvs, 'fields': states, indentLevel: level -1,
																	   typeMap: bindings.typeMap]).toString()
		//println this.text
		return this.text
	}
	
	String toGraphString(i=0){
		return "<html><head><meta name='id' content='${this.hashCode()}'/></head><body><h2>${CodeGenerator.nameToFilename(name)}</h2></body></html>"
	}
}
