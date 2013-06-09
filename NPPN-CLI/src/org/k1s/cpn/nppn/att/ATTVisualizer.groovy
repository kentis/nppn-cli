package org.k1s.cpn.nppn.att




import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics2D;
import java.awt.image.BufferedImage;

import javax.imageio.ImageIO;
import javax.swing.JFrame;

import org.k1s.petriCode.blocks.Atomic;
import org.k1s.petriCode.blocks.Block;
import org.k1s.petriCode.blocks.Principal;
import org.k1s.petriCode.blocks.Service;

import edu.uci.ics.jung.algorithms.layout.BalloonLayout;
import edu.uci.ics.jung.algorithms.layout.RadialTreeLayout
import edu.uci.ics.jung.algorithms.layout.TreeLayout;
import edu.uci.ics.jung.graph.DelegateForest;
import edu.uci.ics.jung.graph.DelegateTree;
import edu.uci.ics.jung.graph.Graph;
import edu.uci.ics.jung.graph.OrderedKAryTree;

import edu.uci.ics.jung.visualization.VisualizationViewer;
import edu.uci.ics.jung.visualization.decorators.ToStringLabeller;

class ATTVisualizer {
	
	def show(att){
		JFrame jf = new JFrame();
		Graph g = getGraph(att);
		VisualizationViewer vv = new VisualizationViewer(new TreeLayout<String, Integer>(g, 200, 100));
		vv.getRenderContext().setVertexLabelTransformer(new ToStringLabeller())
		jf.getContentPane().add(vv);
		jf.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		jf.pack();
		jf.setVisible(true);
	}
	
	void writeATTImage(att, String filename) {
		Graph g = getGraph(att);
		TreeLayout<String, Integer> layout = new TreeLayout<String, Integer>(g, 160, 100)
		//println layout.getSize()
		//layout.setSize(new Dimension(2000,2000))
		def vv = new edu.uci.ics.jung.visualization.VisualizationViewer(layout, new Dimension(2000,2000));
		
		vv.getRenderContext().setVertexLabelTransformer(new ToStringLabeller())
		//vv.getRenderContext().getRendererPane().setMaximumSize(new Dimension(2000,2000));
		
		//vv.setLayout(new BalloonLayout(g) )
		//int width = vv.getLayout().getCurrentSize().width;
		//int height = vv.getLayout().getCurrentSize().height;
		//Color bg = getBackground();
	
		BufferedImage bi = new BufferedImage(2000,2000,BufferedImage.TYPE_INT_BGR);
		Graphics2D graphics = bi.createGraphics();
		graphics.setColor(Color.GRAY);
		graphics.fillRect(0,0, 2000, 2000);
		vv.paintComponent(graphics);
	
		try{
		   ImageIO.write(bi,"png",new File("./$filename"));
		}catch(Exception e){e.printStackTrace();}
	}
	
	  
	
	def getGraph(att){
		def graph = new DelegateTree<String,Integer>();//new OrderedKAryTree<String, Integer>(500)//
		graph.addVertex(att.toGraphString(0))
		
		addChildren(att.children, graph, att.toGraphString(0))
		
		return graph
	}
	int a = 0
	
	def addChildren(children, graph, parent){
		
		children.eachWithIndex { att, i ->
			if(att != null){
			//println "adding $att"
			//graph.addChild(att.toGraphString(i))
//			println graph.class
//			println graph.metaClass.methods
//			println graph.whyDoYouNotChange()
			def node = att.toGraphString((i+1))
			graph.addEdge(a, parent, node );
			a++
			if(att != null && !(att instanceof Atomic) &&(att instanceof AbstractTemplateTree || att instanceof Block || att instanceof Principal || att instanceof Service)){
				 addChildren(att.children, graph, node)
			}
			
			}
		}
	}
}
