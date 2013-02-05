package org.k1s.cpn.nppn.att

import org.cpntools.accesscpn.model.Instance;
import org.cpntools.accesscpn.model.Page
import org.cpntools.accesscpn.model.PetriNet

import org.k1s.nppn.blocks.*

class ATTFactory {
	
	
	static AbstractTemplateTree createATT(pn, pragmatics, bindings){
		def rootPage = pn.page[0]
		def att = new AbstractTemplateTree()
		
		//find Principals
		rootPage.object.each{ node ->
			//if(node.pragmatics != null) println node.pragmatics.name
			if(node instanceof Instance && node.pragmatics[0].name == 'Principal') {
				//println "found principal"
				att.children  << attForPrincipal(node, pn, bindings, att)
			}
		}
		return att
	}
	
	static def attForPrincipal(Instance node, PetriNet pn, bindings, att){
		def principal = new Principal(name: node.name.text)
		
		
		def page = getPageForId(node.subPageID, pn) 
		page.object.each{
			//println it
			if(it instanceof Instance && it.pragmatics[0].name == 'service') {
				principal.children << attForService(it,pn)
			}
		}
		
		return principal
	}
	
	static getPageForId(subPageID, pn){
		def retVal
		pn.page.each{ Page page ->
			if(page.id == subPageID)
				retVal = page
		}
		return retVal
	}
	
	static def attForService(Instance node, pn){
		def service = new Service(name: node.name.getText())
		def page = getPageForId(node.subPageID, pn) 
		
		service.node = node
		
		service.start_node = findServiceNodeInPage(page)
		service.end_node = findReturnNodeInPage(page)
		
		return service
	}
	
	
	static def findServiceNodeInPage(page){
		def res = findNodesInPageByPragmatic(page, "service")
		if(res.size() > 1){
			throw new Exception("Too many services in $page : $res")
		}
		if(res.size() < 1){
			throw new Exception("No service found $page")
		}
		return res[0]
	}
	
	static def findReturnNodeInPage(page){
		def res = findNodesInPageByPragmatic(page, "return")
		if(res.size() > 1){
			throw new Exception("Too many returns in $page")
		}
		/*if(res.size() < 1){
			throw new Exception("No return found $page")
		}*/
		return res[0]
	}
	
	static def findNodesInPageByPragmatic(page, pragName){
		def res = []
		page.object.each{
			if(it.pragmatics.name.contains(pragName)){
				res << it
			}
		}
		return res
	}
}

