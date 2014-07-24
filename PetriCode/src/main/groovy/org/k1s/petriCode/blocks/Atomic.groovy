package org.k1s.petriCode.blocks

import org.k1s.petriCode.PetriCode;
import org.k1s.petriCode.generation.CodeGenerator;
import org.k1s.petriCode.generation.ParameterStrategy;
import org.k1s.petriCode.generation.TemplateManager
import org.k1s.petriCode.generation.TemplateParameters;
import groovy.util.logging.Log;
/**
 * An atomic block
 * @author kent
 *
 */
@Log
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
		transition.pragmatics.findAll{bindings.prag2Binding[it.name] == null}.each{
			if(PetriCode.strict){
				throw new Exception("Unknown pragmatic: ${it.name}")
			} else {
				 println "WARNING: No binding given for pragmatic: ${it.name}"
			}
		}
		
		
		transition.pragmatics.findAll{bindings.prag2Binding[it.name] != null}
		.sort{ bindings.prag2Binding[it.name].weight }.each{
			if(it.name != 'service'){
				def binding = bindings.prag2Binding[it.name]
				
				if(binding){
					def params = new TemplateParameters(bindings).getParamtersFor(it, transition, this, binding.parameterStrategy)
				
					text.append tm.runTemplate(binding.template,params ? params : [:])
				} else {
					//throw new Exception("Unknown pragmatic: ${it.name}")
					if(PetriCode.strict){
						throw new Exception("Unknown pragmatic: ${it.name}")
					} else {
						 println "WARNING: No binding given for pragmatic: ${it.name}"
					}	
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
