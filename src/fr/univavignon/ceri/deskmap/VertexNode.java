package fr.univavignon.ceri.deskmap;

import java.io.Serializable;

class VertexNode implements Comparable<VertexNode>,Serializable {
	
	private String nodeRef;
	private long wayId;


	private Integer distance;
	
	public VertexNode(OverPassNode node, Integer distance, OverPassWay way) {
		super();
		if(node!=null)
			this.nodeRef = node.ref2;
		this.distance = distance;
		if(way!=null)
			this.wayId = way.id;
	}
	public OverPassWay getWay() {
		return Map.getMapInstance().ways.get(wayId);
	}

	public void setWay(OverPassWay way) {
		this.wayId = way.id;
	}
	public OverPassNode getNode() {
		return Map.getMapInstance().roadNetword.get(nodeRef);
	}

	public Integer getDistance() {
		return distance;
	}

	public void setNode(OverPassNode node) {
		this.nodeRef = node.ref2;
	}

	public void setDistance(Integer distance) {
		this.distance = distance;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result
				+ ((distance == null) ? 0 : distance.hashCode());
		result = prime * result + ((getNode() == null) ? 0 : getNode().hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		return false;
	}

	@Override
	public String toString() {
		return "Vertex [id=distance=" + distance + "]";
	}

	@Override
	public int compareTo(VertexNode o) {
//		System.out.println(o.distance+" "+distance+ " "+o.node.ref+" "+node.ref);
		if (this.distance < o.distance)
			return -1;
		else if (this.distance > o.distance)
			return 1;
		else
			return 0;
	}
	
}