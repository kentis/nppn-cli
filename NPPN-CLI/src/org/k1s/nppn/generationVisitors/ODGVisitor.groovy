package org.k1s.nppn.generationVisitors

import org.k1s.cpn.nppn.att.AbstractTemplateTree;
import org.k1s.nppn.blocks.Block;


/**
 * The abstract vivitor
 * @author kent
 *
 */
abstract class ODGVisitor {

	abstract def visitElement(element, bindings);
	
	static final def visitorsPass1 = [new GenerationVisitor(),
									  new VarsVisitor()]
	
	static final def visitorsPass2 = [new VarsTextVisitor()]
	
	/**
	 * Visits all nodes on an ATT in no particular order vith all visitors
	 * @param att
	 * @param bindings
	 */
	final static void visitATT(att, bindings){

		//TODO: change to depth first strategy? Does it matter?		
		def atts = flattenAtt(att, [])
		
		atts.each{ node ->
			
			visitorsPass1.each{ visitor ->
				visitor.visitElement(node, bindings)
				
			}	
		}
		
		atts.each{ node ->
			
			visitorsPass2.each{ visitor ->
				visitor.visitElement(node, bindings)
				
			}
		}
	}
	
	final static def flattenAtt(att, list){
		
		list << att
		if(att.metaClass.hasProperty(att, "children")){
			att.children.each {
				if(it != null) flattenAtt(it, list)	
			}
		}
		return list
	}
}
