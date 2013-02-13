package org.k1s.nppn.blocks.derived


import org.cpntools.accesscpn.model.Arc;
import org.cpntools.accesscpn.model.Instance
import org.cpntools.accesscpn.model.PetriNet;
import org.cpntools.accesscpn.model.Place;
import org.cpntools.accesscpn.model.Transition;
import org.k1s.cpn.nppn.att.ATTFactory;
import org.k1s.cpn.nppn.pragmatics.Pragmatics;
import org.k1s.cpn.nppn.pragmatics.PragmaticsDescriptor;

class PragmaticsDerivator {
	
	static CONTROL_FLOW_PRAGS = ["Id", "startLoop", "endLoop", "branch", "merge"]
	
	static def getServicePages(PetriNet pn){
		def rootPage = pn.page[0]
		//def principals = []
		def services = []
		rootPage.object.each{ node ->
			//if(node.pragmatics != null) println node.pragmatics.name
			if(node instanceof Instance && node.pragmatics[0].name == 'Principal') {
				//println "found principal"
				def page = ATTFactory.getPageForId(node.subPageID, pn)
				//principals << page
				page.object.each { principalNode ->
					if(principalNode instanceof Instance && principalNode.pragmatics[0].name == 'service'){
						services << ATTFactory.getPageForId(principalNode.subPageID, pn)
					}
					
				}
			}
			
		}
		
		return services
	
	}

	
	static def addDerivedPragmatics(pn, pragmaticsDefinitions){
		println pragmaticsDefinitions
		
		getServicePages(pn).each{ page ->
			derivePragsForPage(page, pragmaticsDefinitions)
		}
	}	
	
	static def derivePragsForPage(page, pragmaticsDefinitions){
		def serviceNode = getServiceNode(page)
		if(serviceNode == null) throw new IllegalArgumentException("Page ${page.name.text} has no service")
		def visited = []
		derivePrags(serviceNode, page, pragmaticsDefinitions, visited)
		visited << serviceNode
		def nextNode = findNextNode(serviceNode)
		while(nextNode.size() > 0){
			nextNode.each{
				derivePrags(it, page, pragmaticsDefinitions, visited)
				visited << it
			}
			def thisNode = nextNode
			nextNode = []
			thisNode.each{
				nextNode += findNextNode(it)
			}
			nextNode = nextNode - visited
		}
	}
	
	static def derivePrags(node, page, pragmaticsDefinitions, visited){
		pragmaticsDefinitions.each{ key, PragmaticsDescriptor pragDef ->
			pragDef.derviationRules.each{
				PNPattern pd = new GroovyShell(this.getClass().getClassLoader()).evaluate("import org.k1s.nppn.blocks.derived.PNPattern; $it")
				
				if(pd.matchNode(node, visited)){
					addPrag(node, pragDef)
				}
			}
		}
	}
	
	static def addPrag(node, pragDef){
		def prag = new Pragmatics(pragDef)
		node.pragmatics << prag
	}
	
	 
	static def getServiceNode(page){
		println "findSerive for ${page.name.text}"
		println page.object.pragmatics.name
		def serviceNode = page.object.findAll{ it.pragmatics.name.contains("service") }
		if(serviceNode.size() != 1) throw new Exception("Illegal number for service nodes: ${serviceNode}") 
		return serviceNode[0]
	}
	
	
	static def findNextNode(node){
		def nodes = []
		node.getSourceArc().each { Arc outArc ->
			if(isControlFlow(outArc.target)){
				nodes << outArc.target
			}else if(outArc.target instanceof Transition){
				def furtherArcs = outArc.target.sourceArc
				furtherArcs.each {
					if(isControlFlow(it.target) && !(nodes.contains(outArc.target))){
						nodes << outArc.target
					}
				}
			}
			
		}
		return nodes
	}
	
	static def isControlFlow(node){
		if(node instanceof Place){
			if(node.pragmatics.size() > 0){
				return CONTROL_FLOW_PRAGS.contains(node.pragmatics[0].name)
			}
		}
		return false
	}
}