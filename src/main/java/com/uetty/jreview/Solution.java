package com.uetty.jreview;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Scanner;
import java.util.Set;

public class Solution {

    static class Edge {
    	public final int selfPoint;
    	public final int nextPoint;
    	public final int distant;
    	public Edge(int self, int next, int weight) {
    		this.selfPoint = self;
    		this.nextPoint = next;
    		this.distant = weight;
    	}
    	
    	public boolean isBackOf(Edge e) {
    		return e != null
    				&& this.selfPoint == e.nextPoint
    				&& this.nextPoint == e.selfPoint
    				&& this.distant == -e.distant;
    	}
    }
	
	static class Cycle implements Cloneable {
        
		ArrayList<Node<Edge>> nodes = new ArrayList<>();
        
		public List<Node<Edge>> getNodes() {
        	return nodes;
        }
		public Edge getFirstEdge() {
        	for (int i = 0; i < nodes.size(); i++) {
        		if (nodes.get(i).value != null) {
        			return nodes.get(i).value;
        		}
        	}
        	return null;
		}
        public boolean isBackHead(Node<Edge> e) {
        	Edge firstEdge = getFirstEdge();
        	return firstEdge != null && e.value != null && firstEdge.selfPoint == e.value.nextPoint;
        }
        public boolean containNode(Node<Edge> e) {
        	for (int i = 0; i < nodes.size(); i++) {
        		if (nodes.get(i).value != null
        				&& nodes.get(i).value.nextPoint == e.value.nextPoint) {
        			return true;
        		}
        	}
        	return false;
        }
        public void push(Node<Edge> e) {
        	nodes.add(e);
        }
        public Node<Edge> pop() {
        	return nodes.remove(nodes.size() - 1);
        }
        public int nodeSize() {
        	return nodes.size();
        }
        public int edgeSize() {
        	int size = nodes.size();
        	if (nodes.size() > 0) {
        		Node<Edge> node = nodes.get(0);
        		if (node.value == null) size--;
        	}
        	if (nodes.size() > 1) {
        		Node<Edge> node = nodes.get(nodes.size() - 1);
        		if (node.value == null) size--;
        	}
        	return size;
        }
        public void clear() {
        	nodes.clear();
        }
        public boolean equals0(List<Node<Edge>> oNodes) {
        	List<Node<Edge>> nodes = new ArrayList<>(this.nodes);
        	if (nodes.size() > 0 && nodes.get(0).value == null) nodes.remove(0);
        	if (nodes.size() > 0 && nodes.get(nodes.size() - 1).value == null) nodes.remove(nodes.size() - 1);
        	oNodes = new ArrayList<>(oNodes);
        	if (oNodes.size() > 0 && oNodes.get(0).value == null) oNodes.remove(0);
        	if (oNodes.size() > 0 && oNodes.get(oNodes.size() - 1).value == null) oNodes.remove(oNodes.size() - 1);
        	
        	if (oNodes.size() != nodes.size()) return false;
        	int i = 0;
        	for (; i < nodes.size(); i++) {
        		if (nodes.get(i).value == oNodes.get(0).value) {
        			break;
        		}
        	}
        	if (i == nodes.size()) return false;
        	int j = 0;
        	for (; j < oNodes.size(); j++) {
        		int k = (i + j) % nodes.size();
        		if (!oNodes.get(j).value.equals(nodes.get(k).value)) {
        			return false;
        		}
        	}
        	return true;
        }
        @Override
        public boolean equals(Object oc) {
        	if (!(oc instanceof Cycle)) return false;
        	return equals0(((Cycle)oc).getNodes());
        }
        
        @SuppressWarnings("unchecked")
		@Override
        public Cycle clone() {
			Cycle clone = new Cycle();
			ArrayList<Node<Edge>> cloneNodes = (ArrayList<Node<Edge>>) this.nodes.clone();
			clone.nodes = cloneNodes;
			return clone;
        }
        
    }
	
	static class Node<T> {
		public T value;
		public Node<T> nextBrother;
	}

	static List<Integer> findIsolated(int max, List<Edge> edgeList, List<Edge>[] edgeMap) {
    	// 孤点（环外的点）计算
    	
    	// 度
        int[] deg = new int[max + 1];
        for (int i = 0; i < deg.length; i++) {
        	deg[i] = edgeMap[i].size();
        }
        List<Integer> isolated = new ArrayList<>();
        for (int i = 0; i < deg.length; i++) {
        	if (deg[i] == 1) {
        		isolated.add(i);
        	}
        }
        for (int i = 0; i < isolated.size(); i++) {
        	Integer from = isolated.get(i);
        	List<Edge> list = edgeMap[from];
        	for (Edge e : list) {
        		if (--deg[e.nextPoint] == 1) {
        			isolated.add(e.nextPoint);
        		}
        	}
        }
        
        return isolated;
    }
	
	static List<Cycle> findAllCycle(int max, List<Edge>[] edgeMap, List<Integer>  isolated) {
		List<Cycle> ncycles = new ArrayList<>();
		
		List<Integer> cyclePoints = new ArrayList<>();
    	for (int i = 0; i <= max; i++) {
    		if (isolated.contains(i)) continue;
    		cyclePoints.add(i);
    	}
		
    	Node<Edge> firstNode ,prevNode = new Node<>();
    	firstNode = prevNode;
    	for (int i = 0; i < edgeMap.length; i++) {
    		if (!cyclePoints.contains(i)) continue;
    		for (Edge e : edgeMap[i]) {
    			if (!cyclePoints.contains(e.nextPoint)) {
    				continue;
    			}
    			Node<Edge> node = new Node<>();
    			node.value = e;
    			prevNode.nextBrother = node;
    			prevNode = node;
    		}
    	}
    	
    	Cycle edgeStack = new Cycle();
    	edgeStack.push(firstNode);
    	while (edgeStack.nodeSize() > 0) {
    		Node<Edge> node = edgeStack.pop().nextBrother;
    		if (node == null) {
    			continue;
    		}
    		
    		do {
    			edgeStack.push(node);
    			List<Edge> childEdges = new ArrayList<>(edgeMap[node.value.nextPoint]);
        		
        		Node<Edge> fnode = new Node<>(), cnode = fnode;
        		for (int i = 0; i < childEdges.size(); i++) {
        			Edge el = childEdges.get(i);
        			Node<Edge> enode = new Node<>();
        			enode.value = el;
        			
        			if (edgeStack.isBackHead(enode)) {
        				Edge firstEdge = edgeStack.getFirstEdge();
        				if (edgeStack.edgeSize() > 1 || !firstEdge.isBackOf(el)) {
        					Cycle clone = edgeStack.clone();
        					clone.push(enode);
        					if (!ncycles.contains(clone)) {
        						ncycles.add(clone); // 记录环
        					}
        				}
        				continue;
        			}
        			if (edgeStack.containNode(enode) 		// 已经有该边了
        					|| !cyclePoints.contains(el.nextPoint)) // 下一个点不可能在环上 
        			{
        				continue;
        			}
        			
        			cnode.nextBrother = enode;
        			cnode = enode;
        		}
        		
        		node = fnode.nextBrother;
        		
    		} while(node != null);
    		
    	}
    	
    	return ncycles;
	}
	
	static List<Node<Integer>> findPathFromCycles(List<Cycle> cycles, int from, int to) {
		List<Node<Integer>> list = new ArrayList<>();
		if (from == to) {
			return list;
		}
		for (int i = 0; i < cycles.size(); i++) {
			List<Node<Edge>> nodes = cycles.get(i).getNodes();
			
			int match = -1;
			int j = 0;
			for (; j < nodes.size(); j++) {
				if (nodes.get(j).value != null
						&& nodes.get(j).value.selfPoint == from) {
					match = 0;
					break;
				}
			}
			if (match == -1) continue;
			
			Node<Integer> n = new Node<>();
			n.value = nodes.get(j).value.selfPoint;
			list.add(n);
			for (int k = 0; k < nodes.size(); k++) {
				Node<Edge> node = nodes.get((j + k) % nodes.size());
				if (node.value == null) continue;
				n = new Node<>();
				n.value = node.value.nextPoint;
				list.add(n);
				if (node.value.nextPoint == to) {
					match = 1;
					break;
				}
			}
			if (match != 1) {
				list.clear();
			} else {
				break;
			}
		}
		return list;
	}
	
	static int pathModDFS(List<Edge>[] edgeMap, List<Cycle> cycles, Set<Integer> cyclePoints,
			Map<Integer, Map<Integer, Integer>> weightMap, int from, int to, int mode) {
		
		List<Node<Integer>> path = findPathByDFS0(edgeMap, cyclePoints, from, to);
		Node<Integer> node = path.get(path.size() - 1);
		if (node.value != to) { // 通过中间值中转到达的方案
			int middleVal1 = node.value;
			
			Set<Integer> set = new HashSet<>();
			List<Cycle> middleCycles = new ArrayList<>();
			for (int i = 0; i < cycles.size(); i++) {
				Cycle cycle = cycles.get(i);
				List<Node<Edge>> nodes = cycle.getNodes();
				boolean contain = false;
				for (int j = 0; j < nodes.size(); j++) {
					if (nodes.get(j).value != null && nodes.get(j).value.selfPoint == middleVal1) {
						contain = true;
						break;
					}
				}
				if (!contain) {
					continue;
				}
				middleCycles.add(cycle);
				for (int j = 0; j < nodes.size(); j++) {
					if (nodes.get(j).value != null) {
						set.add(nodes.get(j).value.selfPoint);
					}
				}
			}
			
			
			if (set.contains(to)) { // 中介点1在环上，终点也在环上，直接从环上找路径
				List<Node<Integer>> mcycles = findPathFromCycles(middleCycles, middleVal1, to);
				if (mcycles.size() > 0) mcycles.remove(0);
				path.addAll(mcycles);
				
			} else {// 中间介点1在环上，终点不在环上，需要在加一个中节点2中转
				
				List<Node<Integer>> reversePath = findPathByDFS0(edgeMap, set, to, middleVal1);
				if (reversePath.size() > 0) {
					int middleVal2 = reversePath.get(reversePath.size() - 1).value;
					List<Node<Integer>> mcycles = findPathFromCycles(middleCycles, middleVal1, middleVal2);
					if (mcycles.size() > 0) mcycles.remove(0);
					path.addAll(mcycles);
					
					Collections.reverse(reversePath);
					if (reversePath.size() > 0) reversePath.remove(0);
					path.addAll(reversePath);
				}
			}
			
		}
		
		int mod = 0;
    	if (path.size() >= 2) {
    		int p1 = path.get(0).value;
    		for (int i = 1; i < path.size(); i++) {
    			Integer p2 = path.get(i).value;
    			Map<Integer, Integer> map = weightMap.get(p1);
    			int w = 0;
    			if (map != null) {
    				Integer integer = map.get(p2);
    				w = integer != null ? integer : w;
    			}
    			mod = (mod + w) % mode;
    			mod = mod < 0 ? mod + mode : mod;
    			p1 = p2;
    		}
    	}
    	
    	return mod;
	}
	
    static List<Node<Integer>> findPathByDFS0(List<Edge>[] edgeMap, Set<Integer> cyclePoints,
    		int from, int to) {
    	
		List<Node<Integer>> stack = new ArrayList<>();
		Node<Integer> firstNode = new Node<>();
		firstNode.nextBrother = new Node<>();
		firstNode.nextBrother.value = from;
		stack.add(firstNode);
		Node<Integer> lastNode = null;
		while (lastNode == null && stack.size() > 0) {
    		Node<Integer> node = stack.remove(stack.size() - 1).nextBrother;
    		if (node == null) {
    			continue;
    		}
    		
    		do {
    			stack.add(node);
    			List<Edge> childEdges = new ArrayList<>(edgeMap[node.value]);
        		
        		Node<Integer> fnode = new Node<>(), cnode = fnode;
        		for (int i = 0; i < childEdges.size(); i++) {
        			Edge el = childEdges.get(i);
        			Node<Integer> enode = new Node<>();
        			
        			if (el.nextPoint == to || cyclePoints.contains(el.nextPoint)) {
        				enode.value = el.nextPoint;
            			cnode = enode;
            			lastNode = enode;
        				break;
        			}
        			
        			boolean contain = false;
        			for (int k = 0; k < stack.size(); k++) {
        				Node<Integer> knode = stack.get(k);
        				if (knode.value.equals(Integer.valueOf(el.nextPoint))) {
        					contain = true;
        					break;
        				}
        			}
        			if (contain) {
        				continue;
        			}
        			
        			enode.value = el.nextPoint;
        			cnode.nextBrother = enode;
        			cnode = enode;
        		}
        		
        		node = fnode.nextBrother;
        		
    		} while(lastNode == null && node != null);
    		
    	}
		
		if (lastNode != null) {
			stack.add(lastNode);
		}
    	
    	
    	return stack;
    }
    
    /**
     * 公约数
     */
    static int divisor(int a, int b) {
    	if (a > b) {
    		a = a ^ b;
    		b = a ^ b;
    		a = a ^ b;
    	}
    	if (a == 0) return b;
    	if (a == 1) return 1;
    	int mod = b % a;
    	if (mod == 0) return a;
    	return divisor(a, mod);
    }
    
    /*
     * Complete the longestModPath function below.
     */
    @SuppressWarnings("unchecked")
	static int[] longestModPath(int max, List<Edge> edges, int[][] queries) {
    	
    	List<Edge>[] edgeMap = new List[max + 1];
    	for (int i = 0; i < edgeMap.length; i++) {
    		edgeMap[i] = new ArrayList<>();
    	}
    	Map<Integer, Map<Integer, Integer>> weightMap = new HashMap<>((int)(max / 2));
    	for (int i = 0; i < edges.size(); i++) {
    		Edge edge = edges.get(i);
    		edgeMap[edge.selfPoint].add(edge);
    		Map<Integer, Integer> map = weightMap.get(edge.selfPoint);
    		if (map == null) {
    			map = new HashMap<>();
    			weightMap.put(edge.selfPoint, map);
    		}
    		map.put(edge.nextPoint, edge.distant);
    	}
    	
    	List<Integer> isolated = findIsolated(max, edges, edgeMap);
    	
    	List<Cycle> allCycle = findAllCycle(max, edgeMap, isolated);
    	
    	isolated = null;
    	Set<Integer> cyclePoints = new HashSet<>();
    	for (int i = 0; i < allCycle.size(); i++) {
    		Cycle cycle = allCycle.get(i);
    		ArrayList<Node<Edge>> nodes = cycle.nodes;
    		for (int j = 0; j < nodes.size(); j++) {
    			if (nodes.get(j).value == null) {
    				continue;
    			}
    			cyclePoints.add(nodes.get(j).value.selfPoint);
    		}
    	}
    	
    	int[] result = new int[queries.length];
    	for (int j = 0; j < queries.length; j++) {
    		int mode = queries[j][2];
    		int modDFS = pathModDFS(edgeMap, allCycle, cyclePoints, weightMap, queries[j][0], queries[j][1], mode);
    		result[j] = modDFS;
    		if (allCycle.size() != 0) {
    			int divisor = 0;
    			for (Cycle c : allCycle) {
    				int cycleMod = 0;
    				List<Node<Edge>> es = c.getNodes();
    				for (int i = 0; i < es.size(); i++) {
    					Node<Edge> e = es.get(i);
    					if (e.value == null) continue;
    					cycleMod = (cycleMod + e.value.distant) % mode;
    					cycleMod = cycleMod < 0 ? cycleMod + mode : cycleMod;
    				}
    				divisor = divisor(divisor, cycleMod);
    				divisor = divisor % mode;
    				divisor = divisor < 0 ? divisor + mode : divisor;
    			}
    			if (divisor > 0) {
    				result[j] = result[j] + divisor * (int)((mode - result[j]) / divisor);
    				if (result[j] >= mode) result[j] -= divisor;
    			}
    		}
    	}
    	
    	return result;
    }

    public static void main(String[] args) throws IOException {
    	Scanner scanner = new Scanner(System.in);
        int n = scanner.nextInt();
        scanner.skip("(\r\n|[\n\r\u2028\u2029\u0085])*");

        int max = 0;
        List<Edge> edges = new ArrayList<>();
        
        for (int corridorRowItr = 0; corridorRowItr < n; corridorRowItr++) {
            String[] corridorRowItems = scanner.nextLine().split(" ");
            scanner.skip("(\r\n|[\n\r\u2028\u2029\u0085])*");

            int a1 = Integer.parseInt(corridorRowItems[0]) - 1;
            if (max < a1) max = a1;
            int a2 = Integer.parseInt(corridorRowItems[1]) - 1;
            if (max < a2) max = a2;
            int w =  Integer.parseInt(corridorRowItems[2]);
            
            Edge e1 = new Edge(a1, a2, w);
            edges.add(e1);
            
            Edge e2 = new Edge(a2, a1, -w);
            edges.add(e2);
        }
        
        int q = scanner.nextInt();
        scanner.skip("(\r\n|[\n\r\u2028\u2029\u0085])*");

        int[][] queries = new int[q][3];

        for (int queriesRowItr = 0; queriesRowItr < q; queriesRowItr++) {
            String[] queriesRowItems = scanner.nextLine().split(" ");
            scanner.skip("(\r\n|[\n\r\u2028\u2029\u0085])*");

            int a1 = Integer.parseInt(queriesRowItems[0]) - 1;
            int a2 = Integer.parseInt(queriesRowItems[1]) - 1;
            int mod = Integer.parseInt(queriesRowItems[2]);
            
            queries[queriesRowItr][0] = a1;
            queries[queriesRowItr][1] = a2;
            queries[queriesRowItr][2] = mod;
        }
        scanner.close();
        scanner = null;
        
        int[] result = longestModPath(max, edges, queries);

        for (int resultItr = 0; resultItr < result.length; resultItr++) {
            System.out.println(result[resultItr]);
        }

    }
}


