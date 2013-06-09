package org.k1s.cpn.nppn.att

import org.k1s.petriCode.blocks.Block;
import org.k1s.petriCode.blocks.Principal;

/**
 * The root and containg class for an ATT
 * @author kent
 *
 */ 
class AbstractTemplateTree {
	List<Principal> children = []
	       
	String toGraphString(i=0){
		return "<html><body><h2>ATT Root</h2></body></html>"
	}
}
