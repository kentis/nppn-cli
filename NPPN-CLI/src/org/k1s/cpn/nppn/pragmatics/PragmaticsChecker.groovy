package org.k1s.cpn.nppn.pragmatics

import org.cpntools.accesscpn.model.Instance;
import org.cpntools.accesscpn.model.Place;
import org.cpntools.accesscpn.model.Page;
import org.cpntools.accesscpn.model.Transition;

/**
 * Checks simple constraints for pragmatics
 * @author kent
 *
 */
class PragmaticsChecker {
	
	/**
	 * Checks the pragmatics of CPN according to the restrictions given in the pragmatics definitions  
	 * @param cpn
	 * @param prags
	 * @return
	 */
	static boolean check(cpn, prags){
		def violations = []
		def retval = true
		
		def root = cpn.page[0] 
		if(!PragmaticsChecker.checkPage(root, prags, violations, PageLevels.PROTOCOL)){
			retval = false
		}
		
		def  serviceSubsts = []
		
		root.object.findAll { it instanceof Instance}.each{ Instance subst ->
			def pageId = subst.subPageID
			def page = cpn.page.find{Page page -> pageId == page.id }
			
			if(!PragmaticsChecker.checkPage(page, prags, violations, PageLevels.PRINCIPAL)){
				retval = false
			}
			
			page.object.findAll { it instanceof Instance}.each  {serviceSubsts << it}
		}
		
		
		serviceSubsts.each{Instance subst ->
			def pageId = subst.subPageID
			def page = cpn.page.find{Page page -> pageId == page.id }
			
			if(!PragmaticsChecker.checkPage(page, prags, violations, PageLevels.SERVICE)){
				retval = false
			}
		}
		
		/*cpn.page.each{
			if(!PragmaticsChecker.checkPage(it, prags, violations)){
				retval = false
			}
		}*/
		//println "checl returning: $retval"
		return retval
	}
	
	/**
	 * Checks the pragmatics of a page of CPN according to the restrictions given in the pragmatics definitions
	 * @param page
	 * @param prags
	 * @param violations
	 * @param pageType
	 * @return
	 */
	static boolean checkPage(page, prags, violations, pageType){
		def retval = true
		page.object.each{
			if(it.pragmatics){
				if(!PragmaticsChecker.checkObject(it, prags, page, violations, pageType)){
					retval = false
				}
			}
		}
		//println "checkPage returning $retval"
		return retval
	}
	
	/**
	 * Checks the pragmatics on a single object
	 * @param obj
	 * @param pragDescs
	 * @param page
	 * @param violations
	 * @param pageType
	 * @return
	 */
	static boolean checkObject(obj, pragDescs, page, violations, pageType){
		def retval = true
		
		obj.pragmatics.each { prag ->
			
			def desc = pragDescs[prag.name]
			
			if(desc && desc.constraints instanceof List){
				
			} else if(desc && desc.constraints instanceof PragmaticsConstraints ){
				//println "\t\t constraint ${desc.constraints}"
				if(desc.constraints.connectedTypes && desc.constraints.connectedTypes instanceof String){
					if(!PragmaticsChecker.checkConnectedType(desc.constraints.connectedTypes, obj)){
						
						retval = false
					} else{
						
					}
					
					if(!PragmaticsChecker.checklevel(desc.constraints.levels, pageType)){
						retval = false
					} else{
						
					}
				}
			}
		}
		//println "checkObj returning $retval"
		return retval
	}
	
	/**
	 * checkes level constraints on an object
	 * @param levels
	 * @param pageType
	 * @return
	 */
	static boolean checklevel(levels, pageType){
		
		if(levels){
			return PageLevels.checkSame(levels, pageType)
		}
		return true
	}
	
	
	/**
	 * Checks type constraints on a specific object
	 * @param typeStr
	 * @param obj
	 * @return
	 */
	static boolean checkConnectedType(typeStr, obj){
		def retval = false
		switch(typeStr){
			case 'Place':
				return obj instanceof Place
				break
			case 'Transition':
				return  obj instanceof Transition
				break
			case 'SubstitutionTransition':
				//println "substTrans: ${obj.class}"
				//println "${obj instanceof Instance}"
				return (obj instanceof Instance)
				break
			default:
				throw new Exception("unknown node type: $typeStr")
		}
		return retval
	}
}

/**
 * Enum for the available page levels
 * @author kent
 *
 */
enum PageLevels{
	PROTOCOL,
	PRINCIPAL,
	SERVICE
	
	static boolean checkSame(levelName, type){
		switch(levelName){
			case "protocol":
				return type == PageLevels.PROTOCOL
			case "principal":
				return type == PageLevels.PRINCIPAL
			case "service":
				return type == PageLevels.SERVICE
			default:
				throw new Exception("unknown level name: ${levelName}")
		}
	}
}