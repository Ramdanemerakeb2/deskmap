package fr.univavignon.ceri.deskmap;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.PriorityQueue;

public class Dijkstras {

}

class Graph implements Serializable{
	
	public Graph() {
		roadNetword = new HashMap<String, OverPassNode>();
	}

	public  HashMap<String,OverPassNode> roadNetword;
	public List<OverPassNode> getShortestPath(OverPassNode start, OverPassNode finish, String type,String calculeMode) {
		boolean trouve = false;
		System.out.println(roadNetword.size());

		PriorityQueue<VertexNode> noeuds = new PriorityQueue<VertexNode>();
		final Map<OverPassNode, Integer> dist = new HashMap<OverPassNode, Integer>();
		final Map<OverPassNode, VertexNode> precedent = new HashMap<OverPassNode, VertexNode>();
		for(Map.Entry<String, OverPassNode> v : roadNetword.entrySet()) {
			OverPassNode vertex = v.getValue();
			
//			System.out.println("1--------");
			if (vertex == start) {
				dist.put(vertex, 0);
//				System.out.println("2--------");
				noeuds.add(new VertexNode(vertex, 0,null));
//				System.out.println("3--------");
			} else {
				dist.put(vertex, Integer.MAX_VALUE);
//				System.out.println("4--------");
				noeuds.add(new VertexNode(vertex, Integer.MAX_VALUE,null));
//				System.out.println("5--------");
			}
//			System.out.println("6--------");
			precedent.put(vertex, null);
//			System.out.println("7--------");
		}
		int t = 0;
		System.out.println("Node size "+noeuds.size());
		while (!noeuds.isEmpty()) {
			t++;
			System.out.println(t);
			VertexNode smallest = noeuds.poll();
			if (smallest.getNode() == finish) {
				//System.out.println("trouve");
				final List<OverPassNode> path = new ArrayList<OverPassNode>();
				while (precedent.get(smallest.getNode()) != null) {
					path.add(smallest.getNode());
					smallest = precedent.get(smallest.getNode());
				}
				return (trouve)?path:null;
			}

			if (dist.get(smallest.getNode()) == Integer.MAX_VALUE) {
				break;
			}
			int tmp = noeuds.size();	
			for (VertexNode neighbor : smallest.getNode().connectedNode) {
				if(neighbor.getWay().apied == false &&  type.equals("A pied") ) continue;
				if(neighbor.getWay().velo == false &&  type.equals("Velo") ) continue;
				if(neighbor.getWay().voiture == false &&  type.equals("Voiture") ) continue;
				
				if(neighbor.getNode()==finish) {
					trouve = true;
					System.out.println("Trouve");
				}
				Integer alt = dist.get(smallest.getNode()) + neighbor.getDistance();
				if (alt < dist.get(neighbor.getNode())) {
					dist.put(neighbor.getNode(), alt);
					precedent.put(neighbor.getNode(), smallest);
					
					forloop:
					for(VertexNode n : noeuds) {
						if (n.getNode() == neighbor.getNode()) {
							noeuds.remove(n);
							n.setDistance(alt);
							noeuds.add(n);
							break forloop;
						}
					}
				}
			}
			System.out.println(tmp+" > "+noeuds.size());
		}
		if(trouve)
			return new ArrayList<OverPassNode>(dist.keySet());
		return null;
	}
	
}