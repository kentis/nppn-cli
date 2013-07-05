package org.k1s.petriCode.blocks

import org.k1s.petriCode.generation.CodeGenerator;
import org.k1s.petriCode.generation.TemplateManager
import org.k1s.petriCode.generation.TemplateParameters

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
	def declarations = ['__TOKEN__':""]
	def parent
	def level = 2
	/**
	 * code generator for services
	 * @param bindings
	 * @return
	 */
	def generateCode(bindings){
		
		def binding = bindings.prag2Binding["service"]
		
		if(binding == null){
			throw new RuntimeException("No template found for service.")
		}
		
		def params = new TemplateParameters(bindings).getParamtersFor(node.pragmatics[0], node, this, binding.parameterStrategy)
		params.name = CodeGenerator.nameToFilename(name)
		params.'pre_conds' = getPreConds()
		params.'pre_sets' = getPreSets()
		if(end_node) params.'post_sets' = getPostSets()
		
		this.text = new TemplateManager().runTemplate(binding.template,params).toString()

		return this.text
	}
	
	def getPreConds(){
		def retval = getLCVs(start_node.getTargetArc(), "getSource")
		return retval
	}

	def getPreSets() {
		def retval = getLCVs(start_node.getSourceArc(), "getTarget")
		return retval
	}
	
	def getPostSets() {
		def retval = getLCVs(end_node.getSourceArc(), "getTarget")
		return retval
	}
		
	def getLCVs(arcs, method){
		def retval = []
		
		arcs.each{
			if(it."$method"().pragmatics.name.contains("LCV")){
				retval <<  CodeGenerator.nameToFilename( it.getSource().getName().text )
			}
		}
		return retval
	}
	
	String toGraphString(i=1){
		"<html><head><meta name='id' content='${this.hashCode()}'/></head><body><h2>${CodeGenerator.nameToFilename(name)}</h2></body></html>"
	}
}
