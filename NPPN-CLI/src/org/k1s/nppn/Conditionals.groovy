package org.k1s.nppn

class Conditionals {
	
	static def unless(boolean cond, Closure work){
		if(!cond) work()
	}
	
}
