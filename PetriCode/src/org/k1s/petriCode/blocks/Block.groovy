package org.k1s.petriCode.blocks

import org.k1s.petriCode.generation.CodeGenerator;

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
	public def level
	String toGraphString(i=0){
		return "<html><head><meta name='id' content='${this.hashCode()}'/></head><body><h2>$i: ${CodeGenerator.nameToFilename( start.name.text)}</h2></html></body>"
	}
}
