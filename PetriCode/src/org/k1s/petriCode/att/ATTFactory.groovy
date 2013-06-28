package org.k1s.petriCode.att;

import org.cpntools.accesscpn.model.Arc;
import org.cpntools.accesscpn.model.Instance;
import org.cpntools.accesscpn.model.Page
import org.cpntools.accesscpn.model.PetriNet
import org.cpntools.accesscpn.model.Place;
import org.cpntools.accesscpn.model.Transition;

import org.k1s.petriCode.pragmatics.Pragmatics;
import org.k1s.petriCode.blocks.*
import org.k1s.petriCode.generation.CodeGenerator;

/**
 * Generator class for ATTs
 * @author kent
 *
 */
class ATTFactory {
	
	def controlFlowPragmatics = []
	def pragmatics
	def pragmaticsMap
	
	def visited = []
	
	
	/**
	 * Constructor
	 * @param pragmatics
	 */
	private ATTFactory(pragmatics){
		this.pragmatics = pragmatics.collect{ it.value }
		this.pragmaticsMap = pragmatics
		
		this.pragmatics.each { 
			if(it.controlFlow || it.block != null) controlFlowPragmatics << it.name
			
		}
	}
	
	/**
	 * Creates an ATT for the given CPN model
	 *  
	 * @param pn
	 * @param pragmatics
	 * @param bindings
	 * @return AbstractTemplateTree
	 */
	AbstractTemplateTree createATT(pn, pragmatics, bindings){
		def rootPage = pn.page[0]
		def att = new AbstractTemplateTree()
		
		//find Principals
		rootPage.object.each{ node ->
			//if(node.pragmatics != null) //println node.pragmatics.name
			if(node instanceof Instance && (node.pragmatics[0].name == 'Principal' || node.pragmatics[0].name == 'principal' )) {
				////println "found principal"
				att.children  << attForPrincipal(node, pn, bindings, att)
			}
		}
		return att
	}
	
	/**
	 * Generates a sub-ATT for a page at the principal level
	 * @param node
	 * @param pn
	 * @param bindings
	 * @param att
	 * @return
	 */
	def attForPrincipal(Instance node, PetriNet pn, bindings, att){
		def principal = new Principal(name: node.name.text, level: 1)
		
		
		def page = getPageForId(node.subPageID, pn) 
		page.object.each{
			////println it
			if(it instanceof Instance && it.pragmatics[0].name == 'service') {
				principal.children << attForService(it,pn, principal)
			}
		}
		
		
		//set LCVs and States
		page.object.findAll{ it instanceof Place && it.pragmatics.name.contains('LCV')}.each{ lcv ->
			principal.lcvs << lcv
			//println "INITIAL-MARKING: "+((Place)lcv).initialMarking.toString() 
		}
		page.object.findAll{ it instanceof Place && !it.pragmatics.name.contains('LCV')}.each{ state ->
			principal.states << state
		}
		
		
		return principal
	}
	
	/**
	 * Returns a subpage given its subPaheID
	 * @param subPageID
	 * @param pn
	 * @return
	 */
	static def getPageForId(subPageID, pn){
		def retVal
		pn.page.each{ Page page ->
			if(page.id == subPageID)
				retVal = page
		}
		return retVal
	}
	
	
	/**
	 * Generates a sub-ATT for a page at the service level
	 * @param node
	 * @param pn
	 * @return
	 */
	def attForService(Instance node, pn, principal){
		def service = new Service(name: node.name.getText(), parent: principal, level: 2)
		def page = getPageForId(node.subPageID, pn) 
		
		service.node = node
		
		service.start_node = findServiceNodeInPage(page)
		service.end_node = findReturnNodeInPage(page)
		
		//service.children << service.start_node
		
		def firstBlockStart = findFollowingControlflowPlaces(service.start_node)
		
		//TODO add pragmatics of single tranition services
		//If there are extra pragmatics on the service, add a "virtual" atomic for the service
		if(service.start_node.pragmatics.size() > 1){
			def atomic = new Atomic()
			atomic.transition = service.start_node
			atomic.pragmatics = service.start_node.pragmatics.findAll{it.name != "service"}
			atomic.parent = service
			atomic.level = atomic.parent.level +1
			service.children << atomic
		}
		
		if(firstBlockStart.size() == 0) return service
		
		if(firstBlockStart.size() > 1) throw new Exception("unexpected number of first nodes found for service ${node.name.text}: ${firstBlockStart.size()}")
		firstBlockStart = firstBlockStart[0]
		//service.children << findNextBlock(firstBlockStart)
		
		def block = findNextBlock(firstBlockStart, service)
		
		if(block != null) service.children << block
		while(block && block.end) {
			block = findNextBlock(block.end, service)
			if(block != null) service.children << block
		}
		

		return service
	}
	
	
	/**
	 * finds possible control-flow places following a transition
	 * @param trans
	 * @return
	 */
	def findFollowingControlflowPlaces(Transition trans){
		def retval = []
		trans.sourceArc.each {
			if(isControllFlowPlace(it.target)) retval << it.target
		}
		return retval
	}
	
	/**
	 * Determines if a node is a ControlFlow Palce
	 * @param p
	 * @return
	 */
	def isControllFlowPlace(p){
		def retval = false
		p.pragmatics.flatten().each {
			if(controlFlowPragmatics.contains(it.name)) retval = true
		}
		return retval
	}
	
	/**
	 * 
	 * Determines if the current place represents the start of a (non-atomoic) block
	 * 
	 * @param place
	 * @return
	 */
	def isBlockStart(place){
		def retval= false
		place.pragmatics.flatten().each{
			if(pragmaticsMap[it.name].block != null) retval = true
		}
		return retval
	}
	
	/**
	 * Fineds the next block
	 * 
	 * @param node
	 * @param service
	 * @return
	 */
	def findNextBlock(node, service){
		if(isControllFlowPlace(node)){
			//decide if this is a block or just an atomic
			if(isBlockStart(node)) {
				return createBlock(node, service)
			} else {
				//println "creating atomic for $node with prags: ${node.pragmatics.name}"
				return createAtomic(node, service)
			}
		} else {
			//println node
			//println node.pragmatics.flatten()
			//println node.pragmatics.flatten()[0].name
			//then this must be a block
		 
		}
		return null
	}
	
	/**
	 * Creates an appropriate block that starts at the given node
	 * 
	 * @param node
	 * @param parent
	 * @return
	 */
	def createBlock(Place node, parent){
		//println node.pragmatics.name.flatten()
		if(node.pragmatics.name.flatten().contains("startLoop")){
			//println "creating loop for: $node"
			def loop = new Loop()
			loop.start = node
			loop.parent = parent
			loop.level = parent.level + 1
			visited << node
			//loop.parent = parent
			def sequence = new Sequence(level: parent.level + 1)
			sequence.start = node
			
			def block = createAtomic(node, loop)
			
			if(block != null) sequence.children << block
			
			while(block != null && block.end != null && 
				  ! (block.end.pragmatics.flatten().name.contains("endLoop"))) {
				
				////println "${block.end.pragmatics.name} does not contain \"endLoop\""
				block = findNextBlock(block.end, loop)
				////println "next end $block.end"
				////println "next end prags: ${block.end.pragmatics.flatten().name}"
				////println "next end ends loop: ${block.end.pragmatics.flatten().name.contains("endLoop")}"
				if(block != null) sequence.children << block
				
			}
		    //println "escaped loop"
			if(block == null){
				throw new RuntimeException("End of loop not found for loop starting at: ${loop.start} in ${node.page.name}")
			}
			sequence.end = block.end
			loop.sequence = sequence
			loop.end = block.end
			
			
			visited << loop.end
			return loop
		} else if(node.pragmatics.name.flatten().contains("branch")){
			//throw new Exception("branching not yet supported")
			def branch = new Conditional()
			branch.start = node
			branch.parent = parent
			branch.level = parent.level + 1
			node.sourceArc.each { Arc outArc ->
				def nexts = findFollowingControlflowPlaces(outArc.target)
				//if(nexts.size() > 0){
					
					def sequence = new Sequence()
					sequence.start = node
					branch.level = parent.level + 1
					def block = createAtomic(node, branch, outArc.target)
					if(block != null) sequence.children << block
					def firstBlock = block
					while(block != null && block.end != null &&
						! (block.end.pragmatics.flatten().name.contains("merge"))) {
						
						block = findNextBlock(block.end, branch)
						if(block != null) sequence.children << block
						
					}
					sequence.end = block.end
					branch.sequences[CodeGenerator.nameToFilename(firstBlock.transition.name.asString()).trim()] = sequence
				//}
			}
			visited << branch.end
			branch.end = branch.sequences.values().toArray()[0].end
			visited << branch.end
			return branch
		}
		throw new Exception("block type for ${node.name} not yet supported")
	}
	
	/**
	 * creates an atomic block starting at the given place
	 * @param node Place
	 * @param parent
	 * @return
	 */
	def createAtomic(Place node, parent, transition = null){
		//if(node.sourceArc.size() != 1) throw new Exception("Not start of an Atomic: $node. Wrong number of outGoing arcs: found ${node.sourceArc.size()} expected 1.")
		def atomic = new Atomic()
		
		atomic.start = node
		atomic.parent = parent
		atomic.level = parent.level + 1
		if(transition == null){
			node.sourceArc.each{
				if( transition == null
					&& findFollowingControlflowPlaces(it.target) != [] 
					&& !(visited.contains(it.target))
					&& !(visited.contains(findFollowingControlflowPlaces(it.target)[0]))
					){
					transition = it.target
				}
			}
		}
		if(transition == null) return null
		atomic.transition = transition
		atomic.pragmatics = transition.pragmatics
		atomic.end = findFollowingControlflowPlaces( transition)[0]
		
		visited << node
		visited << atomic.transition
		
		return atomic
	}
	
	/**
	 * Finds the service node for a given page
	 * @param page
	 * @return
	 */
	def findServiceNodeInPage(page){
		def res = findNodesInPageByPragmatic(page, "service")
		if(res.size() > 1){
			throw new Exception("Too many services in $page : $res")
		}
		if(res.size() < 1){
			throw new Exception("No service found $page")
		}
		return res[0]
	}
	
	
	
	/**
	 * Finds the return node of a given serivice page
	 * @param page
	 * @return
	 */
	def findReturnNodeInPage(page){
		def res = findNodesInPageByPragmatic(page, "return")
		if(res.size() > 1){
			throw new Exception("Too many returns in $page")
		}
		/*if(res.size() < 1){
			throw new Exception("No return found $page")
		}*/
		return res[0]
	}
	
	
	/**
	 * Finds a node in a page with the given pragmatic
	 * @param page
	 * @param pragName
	 * @return
	 */
	private def findNodesInPageByPragmatic(page, pragName){
		def res = []
		page.object.findAll{ !(it instanceof org.cpntools.accesscpn.model.auxgraphics.impl.TextImpl)  }.each{
			if(it.pragmatics.name.contains(pragName)){
				res << it
			}
		}
		return res
	}

}

