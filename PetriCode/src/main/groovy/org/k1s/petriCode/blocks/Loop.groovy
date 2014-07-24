package org.k1s.petriCode.blocks


import org.k1s.petriCode.PetriCode;
import org.k1s.petriCode.generation.CodeGenerator;

import org.k1s.petriCode.generation.TemplateManager
import org.k1s.petriCode.generation.Conditionals
import org.k1s.petriCode.PetriCode

/**
 * Loop
 * @author kent
 *
 */
class Loop extends Block{
	def sequence
	def endPragmmatic
	def declarationsText
	def declarations = [:]
	List<Block> children = []

	/**
	 * getter for children
	 * @return
	 */
	List<Block> getChildren(){
		return sequence.children
	}

	def parent

	/**
	 * code generator for Loops
	 * @param bindings
	 * @return
	 */
	def generateCode(bindings){
		if(PetriCode.log)PetriCode.log.finest("Generating code for loop: ${start.name}")
		def binding = bindings.prag2Binding["startLoop"]

		if(binding == null){
			if(PetriCode.strict) throw new RuntimeException("unable to find binding for startLoop");
			else {
				println "Warning: No binding given for pragmatic: startLoop"
				this.text = ""
				return this.text
			}
		}
		if(PetriCode.log)PetriCode.log.finest "end: $end"
		def translateExpr = Conditionals.translateExpr(end.pragmatics[0], bindings)

		this.text = new TemplateManager().runTemplate(binding.template,['end_cond':translateExpr, indentLevel:this.level]).toString()

		return this.text
	}
}
