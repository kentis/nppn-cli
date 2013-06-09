package org.k1s.petriCode.cpnBuilder

import org.cpntools.accesscpn.model.Arc
import org.cpntools.accesscpn.model.Name
import org.cpntools.accesscpn.model.Page
import org.cpntools.accesscpn.model.impl.*
import org.k1s.petriCode.pragmatics.Pragmatics;

class CPNBuilder {
	def pn
	def currentPage
	def factory
	
	def labelsMap
	
	CPNBuilder(labelsMap = [:]){
		//println "setting factory: $factory"
		this.factory = ModelFactoryImpl.eINSTANCE
		this.labelsMap = labelsMap
		pn = factory.createPetriNet()
	}
	
	def make(closure){
		//def builder = new ePNKBuilder()
		closure.setDelegate(this)
		closure.call()
		
		return pn
	}
	
	
	def methodMissing(String methodName, args) {
		def obj
		switch(methodName.toLowerCase()){
			case "page":
					def p = createPage(args[0])
					if(currentPage == null){
						pn.page << p
						currentPage = p
						if(args.size() > 1){
							args[1].call()
						}
						currentPage = null
					} else {
						currentPage.object << p
						def prevPage = currentPage
						currentPage = p
						if(args.size() > 1){
							args[1].call()
						}
						currentPage = prevPage
					}
					obj = p
				break;
			case "place":
					def p = createPlace(args[0])
					currentPage.object << p
					obj = p
				break;
			case "transition":
					def t = createTransition(args[0])
					currentPage.object << t
					obj = t
				break
			case "refplace":
				//println args
					def rp = createRefPlace(args)
					currentPage.object << rp
					obj = rp
				break
			case "arc":
				//println "arc($args)"
				obj = createArc(args)
				break
			case "substtransition":
				obj = createSubstTransition(args[0])
				currentPage.object << obj
				break
			case "epnkbuilder":
				break
			default:
				throw new RuntimeException("Unknown node type: $methodName")
		}
		return obj
	}
	
//	def createRefPlace(args){
//		RefPlace ref = PnmlcoremodelFactory.eINSTANCE.createRefPlace()
//		ref.setRef args.ref
//		ref.setName(getName(args.name == null ? "page" : args.name))
//		return ref
//	}
	
	def createPage(args){
		//println args
		Page p = factory.createPage()
		p.setName(getName(args.name == null ? "page" : args.name))
		p.setId(args.id)
		if(args.labels != null){
			setLabels(p, args.labels)
		}
		return p
	}
	
	def createPlace(args){
		def place = factory.createPlace()
		place.setName getName(args.name == null ? "place" : args.name)
		setPragmatic(place, args.pragmatics)
		if(args.labels != null){
			setLabels(place, args.labels)
		}
		return place
	}
	
	def createTransition(args){
		def trans = factory.createTransition()
		trans.setName getName(args.name == null ? "trans" : args.name)
		
		setPragmatic(trans, args.pragmatics)
		
		if(args.labels != null){
			setLabels(trans, args.labels)
		}
		return trans
	}

	def createSubstTransition(args){
		def trans = factory.createInstance()
		trans.setName getName(args.name == null ? "trans" : args.name)
		trans.setSubPageID(args.subPageId)
		setPragmatic(trans, args.pragmatics)
		if(args.labels != null){
			setLabels(trans, args.labels)
		}
		return trans
	}

	
		
	def createArc(args){
		Arc arc = factory.createArc()
		if(args.size() >= 4){
			def insc = factory.eINSTANCE.createHLAnnotation()
			insc.setText args[3]
			arc.setHlinscription(insc)
		}
		
		arc.setSource args[0]
		arc.setTarget args[1]
		
		args[0].sourceArc.add arc
		args[1].targetArc.add arc
		return arc
	}
	
	def setPragmatic(node, pragmatic){
		if(pragmatic == null) return
		if(node.pragmatics == null) node.pragmatics = []
		node.pragmatics << Pragmatics.parse(pragmatic)
	}
	
	def setLabels(node, labels){
//		println "setting labels: ${labels.keySet()}"
//		labels.keySet().each { labelName ->
//			println "setting label: $labelName"
//			if(node.hasProperty(labelName) && labelsMap.containsKey(labelName)){
//				println "setting label: $labelName"
//				def label = labelsMap[labelName].newInstance()
//				label.setText(labels[labelName])
//				label.setStructure(label.parse(label.text))
//				println "to: ${label.getText()}"
//				node."$labelName" << label
//			}
//		}
		
	}
	
	def getName(text){
		Name n =  factory.createName()
		n.setText text
		return n
	}
}
