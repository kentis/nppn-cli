package org.k1s.nppn.blocks

import org.k1s.nppn.generation.CodeGenerator;
import org.k1s.nppn.generation.TemplateManager
import org.k1s.nppn.generation.TemplateParameters

/**
 * Service
 * @author kent
 *
 */
class Service  {
	def name
	
	def start_node
	def end_node
	List<Block> children = []
	def node
	
	def text
	def declarationsText
	def declarations = ['__TOKEN__']
	
	/**
	 * code generator for services
	 * @param bindings
	 * @return
	 */
	def generateCode(bindings){
	//	println name
		//println bindings.prag2Binding
		def binding = bindings.prag2Binding["service"]
		//println binding
		//println node.pragmatics[0].name
		//println node.pragmatics[0].arguments
		
		def params = new TemplateParameters(bindings).getParamtersFor(node.pragmatics[0], node, binding.parameterStrategy)
		params.name = CodeGenerator.nameToFilename(name)
		this.text = new TemplateManager().runTemplate(binding.template,params).toString()
    //	println this.text
		return this.text
	}
	
	String toGraphString(i=1){
		"${CodeGenerator.nameToFilename(name)}"
	}
}
