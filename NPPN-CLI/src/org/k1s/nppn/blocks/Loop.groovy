package org.k1s.nppn.blocks


import org.k1s.nppn.generation.CodeGenerator;

import org.k1s.nppn.generation.TemplateManager
import org.k1s.nppn.generation.Conditionals
class Loop extends Block{
	def sequence
	def endPragmmatic
	def declarationsText
	def declarations = []
	def getChildren(){
		return sequence.children
	}
	
	def parent
	
	def generateCode(bindings){
			def binding = bindings.prag2Binding["startLoop"]
			
			def translateExpr = Conditionals.translateExpr(end.pragmatics[0], bindings)
			
			this.text = new TemplateManager().runTemplate(binding.template,['end_cond':translateExpr]).toString()
	
			return this.text
	}
	
}
