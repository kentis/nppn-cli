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
	
	def write(files){
		
		att.children.eachWithIndex { att, index ->
			//println "witing to '${outdir}/${nameToFilename(att.name)}'"
			
			def file = new File("${outdir}/${nameToFilename(att.name)}")
			file.createNewFile() 
			
			file.text = files[index]
		}
	}
	
	def attToFile(node){
		def yieald = new StringBuffer()
		if(node.metaClass.hasProperty(node, "children") || node.metaClass.respondsTo(node, "getChildren")){
			node.children.each{
				yieald.append attToFile(it).toString()
			}
		}
		
		def text = node.text.toString().replace("%%yield%%", yieald.toString())
		if(text.contains("%%yield_declarations%%")){
			text = text.replace("%%yield_declarations%%", node.declarationsText ? node.declarationsText : "/*no decls found*/")
		}
		return text
	}
	
	
	static def removePrags(str){
		if(str.indexOf("<") < 0) return str
		
		return str.substring(0,str.indexOf("<")).trim()
	}
	
	static def nameToFilename(str){
		str = removePrags(str)
		str = str.replace(" ", "_")
		return str
	}
}
