package org.k1s.nppn.blocks

/**
 * Sequence
 * @author kent
 *
 */
class Sequence extends Block{

	List<Block> children = []
	
	
	def text
	
	def generateCode(org.k1s.nppn.generation.Bindings bindings){
		this.text = "\n%%yield%%\n"
		return this.text
	}
}
