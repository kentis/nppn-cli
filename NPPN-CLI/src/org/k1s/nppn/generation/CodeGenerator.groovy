package org.k1s.nppn.generation

import org.k1s.nppn.generationVisitors.ODGVisitor;

class CodeGenerator {

	def att
	def bindings
	def outdir
	
	public CodeGenerator(att, bindings, outdir = "./"){
		this.att = att
		this.bindings = bindings
		this.outdir = outdir
	}

	
	def generate(){

		att.children.each{
			ODGVisitor.visitATT(it, bindings)
		}
		
		def files = []
		att.children.each{ files << attToFile(it)  }
		return files
	}
	
	def attToFile(node){
		def yieald = new StringBuffer()
		if(node.metaClass.hasProperty(node, "children") || node.metaClass.respondsTo(node, "getChildren")){
			node.children.each{
				yieald.append attToFile(it).toString()
			}
		}
		
		return node.text.toString().replace("%%yield%%", yieald.toString())
	}
	
	
	static def removePrags(str){
		str.substring(0,str.indexOf("<")).trim()
	}
	
	static def nameToFilename(str){
		str = removePrags(str)
		str = str.replace(" ", "_")
		return str
	}
}
