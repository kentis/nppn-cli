package org.k1s.nppn.blocks

import org.k1s.nppn.generation.CodeGenerator;
import org.k1s.nppn.generation.ParameterStrategy;
import org.k1s.nppn.generation.TemplateManager
import org.k1s.nppn.generation.TemplateParameters;

/**
 * An atomic block
 * @author kent
 *
 */
class Atomic extends Block{
	def pragmatics = []
	def transition
	def parent
	
	def text
	
	/**
	 * Code generator for an atomic
	 * @param bindings
	 * @return
	 */
	def generateCode(bindings){
		
		def tm = new TemplateManager()
		def text = new StringBuffer()
		transition.pragmatics.findAll{bindings.prag2Binding[it.name] != null}
		.sort{ bindings.prag2Binding[it.name].weight }.each{
			if(it.name != 'service'){
				def binding = bindings.prag2Binding[it.name]
				
				if(binding){
					def params = new TemplateParameters(bindings).getParamtersFor(it, transition, binding.parameterStrategy)
				
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
	
	String toGraphString(i=0){
		return "<html><head><meta name='id' content='${this.hashCode()}'/></head><body><h2>$i: ${CodeGenerator.nameToFilename(transition.name.text)}</h2></body></html>"
	}
}
