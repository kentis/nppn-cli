package org.k1s.cpn.nppn.att

import org.cpntools.accesscpn.model.Instance;
import org.cpntools.accesscpn.model.Page
import org.cpntools.accesscpn.model.PetriNet
import org.cpntools.accesscpn.model.Place;
import org.cpntools.accesscpn.model.Transition;

import org.k1s.cpn.nppn.pragmatics.Pragmatics;
import org.k1s.nppn.blocks.*

class ATTFactory {
	
	def controlFlowPragmatics = []
	def pragmatics
	def pragmaticsMap
	
	def visited = []
	
	private ATTFactory(pragmatics){
		this.pragmatics = pragmatics.collect{ it.value }
		this.pragmaticsMap = pragmatics
		
		this.pragmatics.each { 
			if(it.controlFlow || it.block != null) controlFlowPragmatics << it.name
			
		}
	}
	
	def AbstractTemplateTree createATT(pn, pragmatics, bindings){
		def rootPage = pn.page[0]
		def att = new AbstractTemplateTree()
		
		//find Principals
		rootPage.object.each{ node ->
			//if(node.pragmatics != null) //println node.pragmatics.name
			if(node instanceof Instance && node.pragmatics[0].name == 'Principal') {
				////println "found principal"
				att.children  << attForPrincipal(node, pn, bindings, att)
			}
		}
		return att
	}
	
	def attForPrincipal(Instance node, PetriNet pn, bindings, att){
		def principal = new Principal(name: node.name.text)
		
		
		def page = getPageForId(node.subPageID, pn) 
		page.object.each{
			////println it
			if(it instanceof Instance && it.pragmatics[0].name == 'service') {
				principal.children << attForService(it,pn)
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
	
	static def getPageForId(subPageID, pn){
		def retVal
		pn.page.each{ Page page ->
			if(page.id == subPageID)
				retVal = page
		}
		return retVal
	}
	
	def attForService(Instance node, pn){
		def service = new Service(name: node.name.getText())
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
	
	def findFollowingControlflowPlaces(Transition trans){
		def retval = []
		trans.sourceArc.each {
			if(isControllFlowPlace(it.target)) retval << it.target
		}
		return retval
	}
	
	def isControllFlowPlace(p){
		def retval = false
		p.pragmatics.flatten().each {
			if(controlFlowPragmatics.contains(it.name)) retval = true
		}
		return retval
	}
	
	def isBlockStart(place){
		def retval= false
		place.pragmatics.flatten().each{
			if(pragmaticsMap[it.name].block != null) retval = true
		}
		return retval
	}
	
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
	
	
	def createBlock(Place node, parent){
		
		if(node.pragmatics.name.flatten().contains("startLoop")){
			//println "creating loop for: $node"
			def loop = new Loop()
			loop.start = node
			loop.parent = parent
			visited << node
			//loop.parent = parent
			def sequence = new Sequence()
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
			sequence.end = block.end
			loop.sequence = sequence
			loop.end = block.end
			
			
			visited << loop.end
			return loop
		} else if(node.pragmatics.name.flatten().contains("branch")){
			throw new Exception("branching not yet supported")
		}
		throw new Exception("block type for ${node.name} not yet supported")
	}
	
	def createAtomic(Place node, parent, transition = null){
		//if(node.sourceArc.size() != 1) throw new Exception("Not start of an Atomic: $node. Wrong number of outGoing arcs: found ${node.sourceArc.size()} expected 1.")
		def atomic = new Atomic()
		
		atomic.start = node
		atomic.parent = parent
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
	
	def findNodesInPageByPragmatic(page, pragName){
		def res = []
		page.object.each{
			if(it.pragmatics.name.contains(pragName)){
				res << it
			}
		}
		return res
	}
}

