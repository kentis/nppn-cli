package org.k1s.petriCode.blocks.derived

import org.cpntools.accesscpn.model.Arc
import org.cpntools.accesscpn.model.Page
import org.cpntools.accesscpn.model.PetriNet
import org.cpntools.accesscpn.model.RefPlace

/**
 * A patern used to match 
 * @author kent
 *
 */
class  PNPattern {
	
	def name
	def pragmatics
	def type
	
	def minInEdges
	def maxInEdges
	
	def minOutEdges
	def maxOutEdges
	
	def outArcInscription
	
//	def patternClosure
	
	def adjacentPatterns
	
	
	def backLinks
	def forwardLinks
	
//	def backlink
//	def backlinkTo
	
	/**
	 * Matches a node to this pattern
	 * @param node
	 * @return
	 */
	def matchNode(node, visited = []){
		
		if(type != null)
			if(!type.isInstance(node)){
				
				return false
			} 
		
		
		if(node instanceof Arc){
			//println "WTF arc?"
		}
		
		if(name != null){
			
			if(name != node.name.text) return false
		}
		
		if(pragmatics != null){
			//println node.pragmatics
			def pragNode = node
			//if(node instanceof RefPlace) {
//			if(pragNode.ref == null){
//				//work-around for ACCESS/CPN bug
//			} else {
//				while(pragNode instanceof RefPlace) {
//					pragNode = pragNode.ref
//				}
//			}
			//}
			if(pragNode == null || pragNode.pragmatics == null || pragmatics.size() != pragNode.pragmatics.size()) return false
			def pragsOk = true
			pragNode.pragmatics.each { 
				//println "checking prag: ${it}"
				def pragName = it.name
				//println "pragName: $pragName"
				if(!pragmatics.contains(pragName)) pragsOk = false
			}
			if(!pragsOk) return false
		}
		
		if(adjacentPatterns != null){
			//println "chekig adjacentPatterns"
			if(node instanceof Page) return false 
			def adjOk = true
			adjacentPatterns.each { adjPattern -> 
				def hasMatchingAdj = false
				if(node != null && node.getSourceArc() != null){
					//println "found outgoings"
					node.getSourceArc().each { adjArc ->
						//println "checking ${adjArc.target}"
						if(adjPattern.matchNode(adjArc.target)) hasMatchingAdj = true
						//println hasMatchingAdj
					}
				}
				if(!hasMatchingAdj) adjOk = false
			}
			
			
			if(!adjOk) return false
		}
		
		if(backLinks != null){
			if(!checkLinks(node, backLinks, visited)){
				return false
			}
		}
		
		if(forwardLinks != null){
			if(!checkForwardLinks(node, forwardLinks,( node.page.object - visited ) )){
				return false
			}
		}
		
//		
//		if(backlink != null){
//			println "checking backlink"
//			def isBackLinked = hasOutLinkTo(node, backlinkTo)
//			println "isBackLinked: $isBackLinked"
//			if(isBackLinked != backlink) return false
//		}
		
		if(minInEdges != null){
			//println "checking num in edges"
			if(node.getTargetArc().size() < minInEdges) return false
		}
		
		if(minOutEdges != null){
			//println "checking num out edges"
			if(node.getSourceArc().size() < minOutEdges) return false
		}
		
		if(outArcInscription != null){
			if(node instanceof Page) return false
			def retval = false
			node.getSourceArc().each { Arc outArc ->
				if(outArc.getHlinscription() && 
					outArc.getHlinscription().text != null &&  
					outArc.getHlinscription().text.contains(outArcInscription)){
					retval = true
				}
			}
			if(!retval){
				return false
			}
		}
		
		return true
	}
	
	/**
	 * Checks outlink
	 * @param from
	 * @param to
	 * @return
	 */
	def hasOutLinkTo(from, to){
		def retval = false
		from.out.each{ arc ->
			if(arc.target ==  to) retval = true
		}
		return retval
	}
	
	/**
	 * checks links
	 * @param node
	 * @param links
	 * @param linksTo
	 * @return
	 */
	def checkLinks(node, links, linksTo){
		def numLinks = 0
		node.sourceArc.each {
			if(linksTo.contains(it.target)) numLinks++
			else if(it.target.sourceArc.size() == 1){
				if ( linksTo.contains(it.target.sourceArc[0].target)){
					numLinks++
				}
			}
		}
		
		return numLinks == links
	}
	
	def checkForwardLinks(node, numForwardLinks, linksFrom){
		def numLinks = 0
		node.targetArc.each {
			
			if(linksFrom.contains(it.source)){ numLinks++  }
//			else if(it.source.targetArc.size() == 1){
//				if ( linksFrom.contains(it.source.targetArc[0].source)){
//					numLinks++
//				}
//			}
		}
		//println "linksCheck for ${node.name.text}: $numLinks == $links"
		return numLinks == numForwardLinks
	}
	
	def match(p, retval = []){
		def nodes
		if(p instanceof PetriNet) nodes = p.page
		else if(p instanceof Page) nodes = p.object
		
		
		nodes.each { 
			if(!(it instanceof Arc)){
				
				if(it instanceof Page){
					match(it, retval)
				} else {
					if(matchNode(it)) retval << it
					
				}
			}
		}
		
		return retval
	}
}
