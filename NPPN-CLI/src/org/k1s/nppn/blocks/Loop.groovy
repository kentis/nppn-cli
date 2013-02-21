package org.k1s.nppn.blocks

import org.k1s.nppn.generation.CodeGenerator;

import org.k1s.nppn.generation.TemplateManager

class Loop extends Block{
	def sequence
	def endPragmmatic
	
	def getChildren(){
		return sequence.children
	}
	
	def generateCode(bindings){
			def binding = bindings.prag2Binding["startLoop"]
			
			this.text = new TemplateManager().runTemplate(binding.template,[cond: 'false'])
	
			return this.text
	}
	
}
