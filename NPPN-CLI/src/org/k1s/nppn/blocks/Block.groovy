package org.k1s.nppn.blocks

import org.k1s.nppn.generation.CodeGenerator;

/**
 * abstract block
 * @author kent
 *
 */
abstract class Block {
	public def start
	public def end
	
	public def text
	public def pragmatics = []
	
	String toGraphString(i=0){
		return "$i: ${CodeGenerator.nameToFilename( start.name.text)}"
	}
}
