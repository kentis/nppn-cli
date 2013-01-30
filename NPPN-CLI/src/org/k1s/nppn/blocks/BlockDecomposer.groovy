package org.k1s.nppn.blocks

class BlockDecomposer {
	
	def naiveDecomposer(servicePage){
		throw new RuntimeException("nyi")
	}
	
	/**
	 * This method is based on the following assumptions on the block structure:
	 * 1. The start of a loop is uniquely identified by having an incomming edge from a transition that has a single incomming and outgoing edge where the incomming egde comes from an Id place on the control-flow path reachable from the start node
	 * 2. End loops are identified uniquely by being connected to a start-loop 
	 * 3. Conditionals are identified uniquely by having two outgoing arcs both leading to id places
	 * 4. Merge places are uniquely identified by having two incomming edges that both are before on the control flow path
	 * 5. Places that satisfy both the properties of start loop and conditional should be interpreted as a conditional directly inside the loop.  
	 * 
	 * 
	 * @param servicePage
	 * @return void
	 */
	def naiveBlockAnnotator(servicePage){
		throw new RuntimeException("nyi")
		annotateLoops(servicePage)
		annotateConds(servicePage)
		
		sanityCheckBockAnnotations(servicePage)
		
	}
	
	/**
	 * This method is based on the following assumptions on the block structure:
	 * 1. The start of a loop is uniquely identified by having an incomming edge from a transition that has a single incomming and outgoing edge where the incomming egde comes from an Id place on the control-flow path reachable from the start node
	 * 2. End loops are identified uniquely by being connected to a start-loop 
	 */
	def annotateLoops(servicePage){
		throw new Exception("nyi")	
	}
	
	
	/**
	 * 1. Conditionals are identified uniquely by having two outgoing arcs both leading to id places
	 * 2. Merge places are uniquely identified by having two incomming edges that both are before on the control flow path 
	 */
	def annotateConds(servicePage){
		throw new Exception("nyi")
	}
	
}
