package org.k1s.nppn

/**
 * Convenience class mimicing certain keywords not available in Groovy
 * @author kent
 *
 */
class Conditionals {
	
	/**
	 * Unless
	 * @param cond
	 * @param work
	 * @return
	 */
	static def unless(boolean cond, Closure work){
		if(!cond) work()
	}
	
}
